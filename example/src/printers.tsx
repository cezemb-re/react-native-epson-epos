import React from 'react';
import { StyleSheet } from 'react-native';
// @ts-ignore
import {
  DeviceType,
  PortType,
  useDiscovery,
} from '@cezembre/react-native-epson-epos';
import { Layout, Text, Spinner } from '@ui-kitten/components';

const Printers = () => {
  const discovery = useDiscovery({
    portType: PortType.BLUETOOTH,
    deviceType: DeviceType.PRINTER,
    bondedDevices: true,
  });

  if (discovery.error) {
    return (
      <Layout style={style.centered}>
        <Text>{discovery.error.message}</Text>
      </Layout>
    );
  }

  if (discovery.discovering) {
    return (
      <Layout style={style.centered}>
        <Spinner />
      </Layout>
    );
  }

  return (
    <Layout style={style.container}>
      <Text>Devices</Text>
    </Layout>
  );
};

const style = StyleSheet.create({
  centered: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  container: {
    flex: 1,
  },
});

export default Printers;
