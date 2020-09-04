import { NativeModules } from 'react-native';

type EpsonEposType = {
  multiply(a: number, b: number): Promise<number>;
};

const { EpsonEpos } = NativeModules;

export default EpsonEpos as EpsonEposType;
