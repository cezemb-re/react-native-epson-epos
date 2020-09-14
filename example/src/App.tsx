import 'react-native-gesture-handler';
import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { useBluetooth } from './helpers/bluetooth';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import Home from './home';
import Printer from './printers/page';
import {
  ApplicationProvider,
  Spinner,
  IconRegistry,
} from '@ui-kitten/components';
import { EvaIconsPack } from '@ui-kitten/eva-icons';
import * as eva from '@eva-design/eva';

export default function App() {
  const { bluetoothReady, bluetoothError } = useBluetooth();

  if (bluetoothError) {
    return (
      <View style={styles.container}>
        <Text>{bluetoothError.message}</Text>
      </View>
    );
  }

  if (!bluetoothReady) {
    return (
      <ApplicationProvider {...eva} theme={eva.light}>
        <View style={styles.container}>
          <Spinner size="large" />
        </View>
      </ApplicationProvider>
    );
  }

  const Stack = createStackNavigator();

  return (
    <>
      <IconRegistry icons={EvaIconsPack} />
      <ApplicationProvider {...eva} theme={eva.light}>
        <NavigationContainer>
          <Stack.Navigator screenOptions={{ headerShown: false }}>
            <Stack.Screen name="Home" component={Home} />
            <Stack.Screen name="Printer" component={Printer} />
          </Stack.Navigator>
        </NavigationContainer>
      </ApplicationProvider>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
