import { NativeEventEmitter, NativeModules } from 'react-native';
import { useEffect, useState, useRef } from 'react';
import isEqual from 'lodash.isequal';

const { EpsonEpos } = NativeModules;

const epsonEposEmitter = new NativeEventEmitter(EpsonEpos);

export enum PortType {
  ALL = EpsonEpos.FILTER_OPTION_PORT_TYPE_ALL,
  TCP = EpsonEpos.FILTER_OPTION_PORT_TYPE_TCP,
  BLUETOOTH = EpsonEpos.FILTER_OPTION_PORT_TYPE_BLUETOOTH,
  USB = EpsonEpos.FILTER_OPTION_PORT_TYPE_USB,
}

export enum DeviceModel {
  ALL = EpsonEpos.FILTER_OPTION_DEVICE_MODEL_ALL,
}

export enum EpsonFilter {
  FILTER_NAME = EpsonEpos.FILTER_OPTION_FILTER_NAME,
  FILTER_NONE = EpsonEpos.FILTER_OPTION_FILTER_NONE,
}

export enum DeviceType {
  ALL = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_ALL,
  PRINTER = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_PRINTER,
  HYBRID_PRINTER = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_HYBRID_PRINTER,
  DISPLAY = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_DISPLAY,
  KEYBOARD = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_KEYBOARD,
  SCANNER = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_SCANNER,
  SERIAL = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_SERIAL,
  POS_KEYBOARD = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_POS_KEYBOARD,
  MSR = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_MSR,
  GFE = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_GFE,
  OTHER_PERIPHERAL = EpsonEpos.FILTER_OPTION_DEVICE_TYPE_OTHER_PERIPHERAL,
}

export interface DiscoveryParams {
  portType?: PortType;
  broadcast?: string;
  deviceModel?: DeviceModel;
  epsonFilter?: EpsonFilter;
  deviceType?: DeviceType;
  bondedDevices?: boolean;
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
    params.bondedDevices
      ? EpsonEpos.FILTER_OPTION_BONDED_DEVICES_TRUE
      : EpsonEpos.FILTER_OPTION_BONDED_DEVICES_FALSE,
    params.deviceType
  );
}

export async function stopDiscovery(): Promise<void> {
  await EpsonEpos.stopDiscovery();
}

function compareDiscoveryParams(
  a: DiscoveryParams | null,
  b: DiscoveryParams | null
): boolean {
  return isEqual(a, b);
}

function useMemoizedDiscoveryParams(
  params: DiscoveryParams
): { current: DiscoveryParams | null } {
  const memoizedParams: { current: DiscoveryParams | null } = useRef(null);

  if (!compareDiscoveryParams(params, memoizedParams.current)) {
    memoizedParams.current = params;
  }

  return memoizedParams;
}

export function useDiscovery(
  params: DiscoveryParams = {}
): { discovering: boolean; error: Error | null; devices: Array<object> } {
  const [discovering, setDiscovering] = useState(false);
  const [error, setError] = useState(null);

  const memoizedParams = useMemoizedDiscoveryParams(params);

  const onDiscovery = (target: string) => {
    console.log(target); // TODO : Connect
  };

  useEffect(() => {
    epsonEposEmitter.addListener('discovery', onDiscovery);

    (async () => {
      try {
        console.log('startDiscovery()', memoizedParams.current);
        await startDiscovery(memoizedParams.current);
        setDiscovering(true);
      } catch (e) {
        setDiscovering(false);
        setError(e);
      }
    })();

    return () => {
      epsonEposEmitter.removeListener('discovery', onDiscovery);

      setDiscovering(false);

      (async () => {
        try {
          console.log('stopDiscovery()');
          await stopDiscovery();
        } catch (e) {
          setError(e);
        }
      })();
    };
  }, [memoizedParams]);

  return { discovering, error, devices: [] };
}
