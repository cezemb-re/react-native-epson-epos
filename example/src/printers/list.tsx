import React, { useEffect, useState } from 'react';
import { StyleSheet } from 'react-native';
import {
  DeviceType,
  PortType,
  useDiscovery,
} from '@cezembre/react-native-epson-epos';
import {
  Button,
  Card,
  Divider,
  Layout,
  List,
  Modal,
  Text,
  TopNavigation,
} from '@ui-kitten/components';
import Thumbnail from './thumbnail';
import { useNavigation } from '@react-navigation/native';

export default () => {
  const discovery = useDiscovery({
    portType: PortType.BLUETOOTH,
    deviceType: DeviceType.PRINTER,
    bondedDevices: true,
  });

  const navigation = useNavigation();
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    if (discovery.error && discovery.error.message) {
      setShowModal(true);
    }
  }, [discovery.error]);

  return (
    <Layout style={style.container}>
      <TopNavigation title="Printers" />

      <Divider />

      <List
        style={style.list}
        data={discovery.devices}
        renderItem={(props) => (
          <Thumbnail
            {...props}
            onPress={() => navigation.navigate('Printer', props.item)}
          />
        )}
        keyExtractor={({ target }) => target}
      />

      <Modal
        visible={showModal}
        backdropStyle={style.backdrop}
        onBackdropPress={() => setShowModal(false)}
      >
        <Card disabled={true}>
          <Text>
            {discovery.error ? discovery.error.message : 'Unknown error'}
          </Text>
          <Button onPress={() => setShowModal(false)}>Dismiss</Button>
        </Card>
      </Modal>
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
  list: {
    flex: 1,
  },
  activity: {
    paddingRight: 10,
  },
  backdrop: {
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
});
