import React, { useCallback, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { enableBluetooth, useBluetoothState } from './helpers/bluetooth';
import { Layout, Toggle, Text } from '@ui-kitten/components';
import Printers from './printers/list';

export default () => {
  const bluetoothState = useBluetoothState();
  const [bluetoothError, setBluetoothError] = useState(null);

  const toggleBluetooth = useCallback(() => {
    if (bluetoothState === 'off') {
      (async () => {
        try {
          await enableBluetooth();
        } catch (error) {
          setBluetoothError(error);
        }
      })();
    }
  }, [bluetoothState]);

  if (bluetoothState !== 'on' || bluetoothError) {
    return (
      <Layout style={style.noBluetoothContainer}>
        <Text category="h5">Activate Bluetooth</Text>
        <View style={style.toggleContainer}>
          <Toggle
            checked={bluetoothState === 'on'}
            onChange={toggleBluetooth}
          />
        </View>
      </Layout>
    );
  }

  return (
    <Layout style={style.container}>
      <Printers />
    </Layout>
  );
};

const style = StyleSheet.create({
  noBluetoothContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  toggleContainer: {
    marginTop: 20,
  },
  container: {
    flex: 1,
  },
});
