import { NativeEventEmitter, NativeModules } from 'react-native';
import { useEffect, useState, useRef, useCallback } from 'react';
import isEqual from 'lodash.isequal';

const { EpsonEpos } = NativeModules;

const epsonEposEmitter = new NativeEventEmitter(EpsonEpos);

export enum PortType {
  ALL = EpsonEpos.DISCOVERY_PORTTYPE_ALL,
  TCP = EpsonEpos.DISCOVERY_PORTTYPE_TCP,
  BLUETOOTH = EpsonEpos.DISCOVERY_PORTTYPE_BLUETOOTH,
  USB = EpsonEpos.DISCOVERY_PORTTYPE_USB,
}

export enum DeviceModel {
  ALL = EpsonEpos.DISCOVERY_MODEL_ALL,
}

export enum EpsonFilter {
  FILTER_NAME = EpsonEpos.DISCOVERY_FILTER_NAME,
  FILTER_NONE = EpsonEpos.DISCOVERY_FILTER_NONE,
}

export enum DeviceType {
  ALL = EpsonEpos.DISCOVERY_TYPE_ALL,
  PRINTER = EpsonEpos.DISCOVERY_TYPE_PRINTER,
  HYBRID_PRINTER = EpsonEpos.DISCOVERY_TYPE_HYBRID_PRINTER,
  DISPLAY = EpsonEpos.DISCOVERY_TYPE_DISPLAY,
  KEYBOARD = EpsonEpos.DISCOVERY_TYPE_KEYBOARD,
  SCANNER = EpsonEpos.DISCOVERY_TYPE_SCANNER,
  SERIAL = EpsonEpos.DISCOVERY_TYPE_SERIAL,
  POS_KEYBOARD = EpsonEpos.DISCOVERY_TYPE_POS_KEYBOARD,
  MSR = EpsonEpos.DISCOVERY_TYPE_MSR,
  GFE = EpsonEpos.DISCOVERY_TYPE_GFE,
  OTHER_PERIPHERAL = EpsonEpos.DISCOVERY_TYPE_OTHER_PERIPHERAL,
}

export interface DiscoveryParams {
  portType?: PortType;
  broadcast?: string;
  deviceModel?: DeviceModel;
  epsonFilter?: EpsonFilter;
  deviceType?: DeviceType;
  bondedDevices?: boolean;
}

export enum DiscoveryErrors {
  ERR_PARAM = 'ERR_PARAM',
  ERR_ILLEGAL = 'ERR_ILLEGAL',
  ERR_MEMORY = 'ERR_MEMORY',
  ERR_FAILURE = 'ERR_FAILURE',
  ERR_PROCESSING = 'ERR_PROCESSING',
}

export const defaultBroadcast: string = '255.255.255.255';

export async function startDiscovery(
  params: DiscoveryParams | null
): Promise<void> {
  params = params ? params : {};

  if (!('portType' in params)) {
    params.portType = PortType.ALL;
  }

  if (!('broadcast' in params)) {
    params.broadcast = defaultBroadcast;
  }

  if (!('deviceModel' in params)) {
    params.deviceModel = DeviceModel.ALL;
  }

  if (!('epsonFilter' in params)) {
    params.epsonFilter = EpsonFilter.FILTER_NAME;
  }

  if (!('deviceType' in params)) {
    params.deviceType = DeviceType.ALL;
  }

  if (!('bondedDevices' in params)) {
    params.bondedDevices = false;
  }

  await EpsonEpos.startDiscovery(
    params.portType,
    params.broadcast,
    params.deviceModel,
    params.epsonFilter,
    params.bondedDevices ? EpsonEpos.DISCOVERY_TRUE : EpsonEpos.DISCOVERY_FALSE,
    params.deviceType
  );
}

export async function stopDiscovery(): Promise<void> {
  await EpsonEpos.stopDiscovery();
}

function useMemoizedDiscoveryParams(
  params: DiscoveryParams
): { current: DiscoveryParams | null } {
  const memoizedParams: { current: DiscoveryParams | null } = useRef(null);

  if (!isEqual(params, memoizedParams.current)) {
    memoizedParams.current = params;
  }

  return memoizedParams;
}

export interface Device {
  target: string;
  deviceName: string;
  ipAddress: string;
  macAddress: string;
  bdAddress: string;
}

export function useDiscovery(
  params: DiscoveryParams = {}
): {
  discovering: boolean;
  error: Error | null;
  devices: Array<Device>;
} {
  const [discovering, setDiscovering] = useState(false);
  const [error, setError] = useState(null);
  const [devices, setDevices] = useState([] as Array<Device>);

  const memoizedParams = useMemoizedDiscoveryParams(params);

  const onDiscovery = useCallback((device: Device) => {
    setDevices((list: Array<Device>) => {
      const index = list.findIndex((d: Device) => d.target === device.target);
      if (index === -1) {
        return [device, ...list];
      }
      return list;
    });
  }, []);

  useEffect(() => {
    epsonEposEmitter.addListener('discovery', onDiscovery);

    (async () => {
      try {
        await startDiscovery(memoizedParams.current);
        setDiscovering(true);
      } catch (e) {
        if (e.code === DiscoveryErrors.ERR_ILLEGAL) {
          try {
            await stopDiscovery();
            await startDiscovery(memoizedParams.current);
            setDiscovering(true);
          } catch (_e) {
            setError(_e);
          }
        } else {
          setError(e);
        }
      }
    })();

    return () => {
      epsonEposEmitter.removeListener('discovery', onDiscovery);

      setDiscovering(false);

      (async () => {
        try {
          await stopDiscovery();
        } catch (e) {
          setError(e);
        }
      })();
    };
  }, [memoizedParams, onDiscovery]);

  return { discovering, error, devices };
}
