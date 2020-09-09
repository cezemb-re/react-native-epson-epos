import BleManager from 'react-native-ble-manager';
import { useEffect, useState } from 'react';
import { NativeModules, NativeEventEmitter } from 'react-native';

const bleManagerEmitter = new NativeEventEmitter(NativeModules.BleManager);

export function useBluetooth(): {
  bluetoothReady: boolean;
  bluetoothError: Error | null;
} {
  const [bluetoothReady, setBluetoothReady] = useState(false);
  const [bluetoothError, setBluetoothError] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        await BleManager.start({ showAlert: true, forceLegacy: true });
        setBluetoothReady(true);
      } catch (error) {
        setBluetoothError(error);
      }
    })();
  }, []);

  return { bluetoothReady, bluetoothError };
}

export function useBluetoothState(): string | null {
  const [bluetoothState, setBluetoothState] = useState(null);
  bleManagerEmitter.addListener('BleManagerDidUpdateState', ({ state }) => {
    setBluetoothState(state);
  });
  useEffect(() => {
    BleManager.checkState();
  }, []);
  return bluetoothState;
}

export async function enableBluetooth() {
  return await BleManager.enableBluetooth();
}
