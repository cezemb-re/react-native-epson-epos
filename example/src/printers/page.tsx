import React, { useCallback, useState } from 'react';
import {
  Button,
  Card,
  Divider,
  Icon,
  Layout,
  Text,
  TopNavigation,
  TopNavigationAction,
} from '@ui-kitten/components';
import {
  connectPrinter,
  maxConnectPrinterTimeout,
  ModelLang,
  PrinterSeries,
  printerSettingsToStrings,
  printerStatusToStrings,
  printTestSheet,
  usePrinterSettings,
  usePrinterStatus,
} from '@cezembre/react-native-epson-epos';
import { useNavigation } from '@react-navigation/native';
import { ScrollView, StyleSheet, View } from 'react-native';

export default ({
  route: { params },
}: {
  route: { params: { deviceName: string; target: string } };
}) => {
  const { status, error, setStatus } = usePrinterStatus();
  const { settings } = usePrinterSettings();
  const [connectError, setConnectError] = useState(null as Error | null);
  const [printError, setPrintError] = useState(null as Error | null);

  const connect = useCallback(() => {
    setPrintError(null);

    (async () => {
      try {
        setStatus(
          await connectPrinter({
            target: params.target,
            printerSeries: PrinterSeries.TM_M30, // TODO Parse from device.deviceName
            lang: ModelLang.ANK,
            timeout: maxConnectPrinterTimeout,
          })
        );
      } catch (e) {
        setConnectError(e);
      }
    })();
  }, [params.target, setStatus, setConnectError]);

  const testPrint = useCallback(() => {
    setPrintError(null);

    (async () => {
      try {
        await printTestSheet();
      } catch (e) {
        setPrintError(e);
      }
    })();
  }, []);

  const navigation = useNavigation();

  return (
    <Layout style={style.container}>
      <TopNavigation
        title={params.deviceName}
        accessoryLeft={() => (
          <TopNavigationAction
            onPress={() => navigation.goBack()}
            icon={(props) => <Icon {...props} name="arrow-back" />}
          />
        )}
      />

      <Divider />

      <ScrollView>
        <View style={style.content}>
          {!status || !status.connection ? (
            <View style={style.actionsContainer}>
              <Button onPress={connect}>Connect</Button>

              {connectError ? (
                <Text style={style.error}>{connectError.message}</Text>
              ) : null}
            </View>
          ) : (
            <View style={style.actionsContainer}>
              <Button onPress={testPrint}>Test print</Button>

              {printError ? (
                <Text style={style.error}>{printError.message}</Text>
              ) : null}
            </View>
          )}

          {status ? (
            <Card
              style={style.statusContainer}
              header={() => <Text style={style.title}>Status</Text>}
            >
              {printerStatusToStrings(status).map(
                (statusLine: string, index: number) => (
                  <Text key={index}>{statusLine}</Text>
                )
              )}
            </Card>
          ) : null}

          {settings ? (
            <Card
              style={style.statusContainer}
              header={() => <Text style={style.title}>Settings</Text>}
            >
              {printerSettingsToStrings(settings).map(
                (settingLine: string, index: number) => (
                  <Text key={index}>{settingLine}</Text>
                )
              )}
            </Card>
          ) : null}

          {error ? <Text style={style.error}>{error.message}</Text> : null}
        </View>
      </ScrollView>
    </Layout>
  );
};

const style = StyleSheet.create({
  container: {
    flex: 1,
  },

  content: {
    paddingBottom: 100,
  },

  actionsContainer: {
    marginTop: 30,
    paddingLeft: 20,
    paddingRight: 20,
  },

  statusContainer: {
    marginTop: 30,
    marginLeft: 20,
    marginRight: 20,
  },

  title: {
    padding: 10,
    color: '#aaaaaa',
  },

  error: {
    color: 'red',
    textAlign: 'center',
    padding: 10,
  },
});
