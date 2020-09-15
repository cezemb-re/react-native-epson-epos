import { useState, useEffect, useCallback } from 'react';
import { NativeEventEmitter, NativeModules } from 'react-native';

const { EpsonEpos } = NativeModules;

const epsonEposEmitter = new NativeEventEmitter(EpsonEpos);

export enum PrinterSeries {
  TM_M10 = EpsonEpos.PRINTER_TM_M10,
  TM_M30 = EpsonEpos.PRINTER_TM_M30,
  TM_M30II = EpsonEpos.PRINTER_TM_M30II,
  TM_P20 = EpsonEpos.PRINTER_TM_P20,
  TM_P60 = EpsonEpos.PRINTER_TM_P60,
  TM_P60II = EpsonEpos.PRINTER_TM_P60II,
  TM_P80 = EpsonEpos.PRINTER_TM_P80,
  TM_T20 = EpsonEpos.PRINTER_TM_T20,
  TM_T60 = EpsonEpos.PRINTER_TM_T60,
  TM_T70 = EpsonEpos.PRINTER_TM_T70,
  TM_T81 = EpsonEpos.PRINTER_TM_T81,
  TM_T82 = EpsonEpos.PRINTER_TM_T82,
  TM_T83 = EpsonEpos.PRINTER_TM_T83,
  TM_T83III = EpsonEpos.PRINTER_TM_T83III,
  TM_T88 = EpsonEpos.PRINTER_TM_T88,
  TM_T90 = EpsonEpos.PRINTER_TM_T90,
  TM_T100 = EpsonEpos.PRINTER_TM_T100,
  TM_U220 = EpsonEpos.PRINTER_TM_U220,
  TM_U330 = EpsonEpos.PRINTER_TM_U330,
  TM_L90 = EpsonEpos.PRINTER_TM_L90,
  TM_H6000 = EpsonEpos.PRINTER_TM_H6000,
}

export enum ModelLang {
  ANK = EpsonEpos.PRINTER_MODEL_ANK,
  CHINESE = EpsonEpos.PRINTER_MODEL_CHINESE,
  TAIWAN = EpsonEpos.PRINTER_MODEL_TAIWAN,
  KOREAN = EpsonEpos.PRINTER_MODEL_KOREAN,
  THAI = EpsonEpos.PRINTER_MODEL_THAI,
  SOUTH_ASIA = EpsonEpos.PRINTER_MODEL_SOUTHASIA,
}

export enum ConnectPrinterError {
  ERR_PARAM = 'ERR_PARAM',
  ERR_CONNECT = 'ERR_CONNECT',
  ERR_TIMEOUT = 'ERR_TIMEOUT',
  ERR_ILLEGAL = 'ERR_ILLEGAL',
  ERR_MEMORY = 'ERR_MEMORY',
  ERR_FAILURE = 'ERR_FAILURE',
  ERR_PROCESSING = 'ERR_PROCESSING',
  ERR_NOT_FOUND = 'ERR_NOT_FOUND',
  ERR_IN_USE = 'ERR_IN_USE',
  ERR_TYPE_INVALID = 'ERR_TYPE_INVALID',
}

export const defaultConnectPrinterTimeout = 15000;
export const minConnectPrinterTimeout = 1000;
export const maxConnectPrinterTimeout = 300000;

export async function initPrinter(
  printerSeries: PrinterSeries,
  lang: ModelLang = ModelLang.ANK
): Promise<void> {
  await EpsonEpos.initPrinter(printerSeries, lang);
}

export interface ConnectPrinterParams {
  target: string;
  printerSeries: PrinterSeries;
  timeout?: number;
  lang?: ModelLang;
}

export async function connectPrinter(
  params: ConnectPrinterParams
): Promise<PrinterStatus> {
  if (!('timeout' in params) || params.timeout === undefined) {
    params.timeout = defaultConnectPrinterTimeout;
  } else if (params.timeout < minConnectPrinterTimeout) {
    params.timeout = minConnectPrinterTimeout;
  } else if (params.timeout > maxConnectPrinterTimeout) {
    params.timeout = maxConnectPrinterTimeout;
  }

  if (!('lang' in params) || params.lang === undefined) {
    params.lang = ModelLang.ANK;
  }

  return <PrinterStatus>(
    await EpsonEpos.connectPrinter(
      params.printerSeries,
      params.lang,
      params.target,
      params.timeout
    )
  );
}

export async function disconnectPrinter(): Promise<void> {
  await EpsonEpos.disconnectPrinter();
}

export const defaultGetPrinterSettingTimeout = 10000;
export const minGetPrinterSettingTimeout = 5000;
export const maxGetPrinterSettingTimeout = 600000;

export enum PrinterSetting {
  PaperWidth = EpsonEpos.PRINTER_SETTING_PAPER_WIDTH,
  PrintDensity = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY,
  PrintSpeed = EpsonEpos.PRINTER_SETTING_PRINT_SPEED,
}

export enum PaperWidth {
  _58 = EpsonEpos.PRINTER_SETTING_PAPER_WIDTH_58,
  _60 = EpsonEpos.PRINTER_SETTING_PAPER_WIDTH_60,
  _80 = EpsonEpos.PRINTER_SETTING_PAPER_WIDTH_80,
}

export enum PrintDensity {
  DIP = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_DIP,
  _70 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_70,
  _75 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_75,
  _80 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_80,
  _85 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_85,
  _90 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_90,
  _95 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_95,
  _100 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_100,
  _105 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_105,
  _110 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_110,
  _115 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_115,
  _120 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_120,
  _125 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_125,
  _130 = EpsonEpos.PRINTER_SETTING_PRINT_DENSITY_130,
}

export enum PrintSpeed {
  _1 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_1,
  _2 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_2,
  _3 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_3,
  _4 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_4,
  _5 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_5,
  _6 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_6,
  _7 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_7,
  _8 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_8,
  _9 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_9,
  _10 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_10,
  _11 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_11,
  _12 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_12,
  _13 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_13,
  _14 = EpsonEpos.PRINTER_SETTING_PRINT_SPEED_14,
}

export async function getPrinterSetting(
  type: PrinterSetting = PrinterSetting.PaperWidth,
  timeout: number = defaultGetPrinterSettingTimeout
): Promise<void> {
  if (timeout < minGetPrinterSettingTimeout) {
    timeout = minGetPrinterSettingTimeout;
  } else if (timeout > maxGetPrinterSettingTimeout) {
    timeout = maxGetPrinterSettingTimeout;
  }
  await EpsonEpos.getPrinterSetting(timeout, type);
}

export interface PrinterSettings {
  paperWidth?: number;
  printDensity?: number;
  printSpeed?: number;
}

function resolvePaperWidth(paperWidth: PaperWidth): number {
  switch (paperWidth) {
    case PaperWidth._58:
      return 58;
    case PaperWidth._60:
      return 60;
    case PaperWidth._80:
      return 80;
  }
  return 0;
}

function resolvePrintDensity(printDensity: PrintDensity): number {
  switch (printDensity) {
    case PrintDensity.DIP:
      return 0;
    case PrintDensity._70:
      return 70;
    case PrintDensity._75:
      return 75;
    case PrintDensity._80:
      return 80;
    case PrintDensity._85:
      return 85;
    case PrintDensity._90:
      return 90;
    case PrintDensity._95:
      return 95;
    case PrintDensity._100:
      return 100;
    case PrintDensity._105:
      return 105;
    case PrintDensity._110:
      return 110;
    case PrintDensity._115:
      return 115;
    case PrintDensity._120:
      return 120;
    case PrintDensity._125:
      return 125;
    case PrintDensity._130:
      return 130;
  }
  return 0;
}

function resolvePrintSpeed(printSpeed: PrintSpeed): number {
  switch (printSpeed) {
    case PrintSpeed._1:
      return 1;
    case PrintSpeed._2:
      return 2;
    case PrintSpeed._3:
      return 3;
    case PrintSpeed._4:
      return 4;
    case PrintSpeed._5:
      return 5;
    case PrintSpeed._6:
      return 6;
    case PrintSpeed._7:
      return 7;
    case PrintSpeed._8:
      return 8;
    case PrintSpeed._9:
      return 9;
    case PrintSpeed._10:
      return 10;
    case PrintSpeed._11:
      return 11;
    case PrintSpeed._12:
      return 12;
    case PrintSpeed._13:
      return 13;
    case PrintSpeed._14:
      return 14;
  }
  return 0;
}

export function usePrinterSettings(
  timeout: number = defaultGetPrinterSettingTimeout
): { settings: PrinterSettings | null; error: Error | null } {
  const [settings, setSettings] = useState(null as PrinterSettings | null);
  const [error, setError] = useState(null as Error | null);

  const onSettingEvent = useCallback(
    (setting: { code: number; type: PrinterSetting; value: number }) => {
      if (setting.code !== 0) {
        return; // TODO : Parse error code
      }

      switch (setting.type) {
        case PrinterSetting.PaperWidth:
          setSettings((prevState: PrinterSettings | null) => ({
            ...prevState,
            paperWidth: resolvePaperWidth(setting.value),
          }));
          break;

        case PrinterSetting.PrintDensity:
          setSettings((prevState: PrinterSettings | null) => ({
            ...prevState,
            printDensity: resolvePrintDensity(setting.value),
          }));
          break;

        case PrinterSetting.PrintSpeed:
          setSettings((prevState: PrinterSettings | null) => ({
            ...prevState,
            printSpeed: resolvePrintSpeed(setting.value),
          }));
          break;
      }
    },
    []
  );

  useEffect(() => {
    epsonEposEmitter.addListener('setting', onSettingEvent);

    (async () => {
      try {
        await EpsonEpos.getPrinterSettings(timeout);
      } catch (e) {
        setError(e);
      }
    })();

    return () => epsonEposEmitter.removeListener('setting', onSettingEvent);
  }, [onSettingEvent, timeout]);

  return { settings, error };
}

export enum TextAlign {
  LEFT = EpsonEpos.PRINTER_ALIGN_LEFT,
  CENTER = EpsonEpos.PRINTER_ALIGN_CENTER,
  RIGHT = EpsonEpos.PRINTER_ALIGN_RIGHT,
  DEFAULT = EpsonEpos.PARAM_DEFAULT,
}

export async function addTextAlign(
  align: TextAlign = TextAlign.DEFAULT
): Promise<void> {
  await EpsonEpos.addTextAlign(align);
}

export async function addLineSpace(lineSpace: number): Promise<void> {
  await EpsonEpos.addLineSpace(lineSpace);
}

export async function addTextRotate(rotate: boolean = true): Promise<void> {
  await EpsonEpos.addLineSpace(
    rotate ? EpsonEpos.PRINTER_TRUE : EpsonEpos.PRINTER_FALSE
  );
}

export async function addText(data: string): Promise<void> {
  await EpsonEpos.addText(data);
}

export enum Lang {
  ENGLISH = EpsonEpos.PRINTER_LANG_EN,
  JAPANESE = EpsonEpos.PRINTER_LANG_JA,
  SIMPLIFIED_CHINESE = EpsonEpos.PRINTER_LANG_ZH_CN,
  TRADITIONAL_CHINESE = EpsonEpos.PRINTER_LANG_ZH_TW,
  KOREAN = EpsonEpos.PRINTER_LANG_KO,
  THAI = EpsonEpos.PRINTER_LANG_TH,
  VIETNAMESE = EpsonEpos.PRINTER_LANG_VI,
}

export async function addTextLang(lang: Lang): Promise<void> {
  await EpsonEpos.addTextLang(lang);
}

export enum Font {
  A = EpsonEpos.PRINTER_FONT_A,
  B = EpsonEpos.PRINTER_FONT_B,
  C = EpsonEpos.PRINTER_FONT_C,
  D = EpsonEpos.PRINTER_FONT_D,
  E = EpsonEpos.PRINTER_FONT_E,
}

export async function addTextFont(font: Font): Promise<void> {
  await EpsonEpos.addTextFont(font);
}

export async function addTextSmooth(smooth: boolean): Promise<void> {
  await EpsonEpos.addTextSmooth(
    smooth ? EpsonEpos.PRINTER_TRUE : EpsonEpos.PRINTER_FALSE
  );
}

export async function addTextSize(
  width: number,
  height: number
): Promise<void> {
  await EpsonEpos.addTextSize(width, height);
}

export async function addTextStyle(
  reverse: boolean,
  ul: boolean,
  em: boolean,
  color: number
): Promise<void> {
  await EpsonEpos.addTextStyle(
    reverse ? EpsonEpos.PRINTER_TRUE : EpsonEpos.PRINTER_FALSE,
    ul ? EpsonEpos.PRINTER_TRUE : EpsonEpos.PRINTER_FALSE,
    em ? EpsonEpos.PRINTER_TRUE : EpsonEpos.PRINTER_FALSE,
    color
  );
}

export async function addHPosition(x: number): Promise<void> {
  await EpsonEpos.addHPosition(x);
}

export async function addFeedUnit(unit: number): Promise<void> {
  await EpsonEpos.addFeedUnit(unit);
}

export async function addFeedLine(line: number): Promise<void> {
  await EpsonEpos.addFeedLine(line);
}

export enum Color {
  NONE = EpsonEpos.PRINTER_COLOR_NONE,
  _1 = EpsonEpos.PRINTER_COLOR_1,
  _2 = EpsonEpos.PRINTER_COLOR_2,
  _3 = EpsonEpos.PRINTER_COLOR_3,
  _4 = EpsonEpos.PRINTER_COLOR_4,
  DEFAULT = EpsonEpos.PRINTER_PARAM_DEFAULT,
}

export enum ColorMode {
  MONO = EpsonEpos.PRINTER_MODE_MONO,
  GRAY16 = EpsonEpos.PRINTER_MODE_GRAY16,
  MONO_HIGH_DENSITY = EpsonEpos.PRINTER_MODE_MONO_HIGH_DENSITY,
  DEFAULT = EpsonEpos.PRINTER_PARAM_DEFAULT,
}

export enum Halftone {
  DITHER = EpsonEpos.PRINTER_HALFTONE_DITHER,
  ERROR_DIFFUSION = EpsonEpos.PRINTER_HALFTONE_ERROR_DIFFUSION,
  THRESHOLD = EpsonEpos.PRINTER_HALFTONE_THRESHOLD,
  DEFAULT = EpsonEpos.PRINTER_PARAM_DEFAULT,
}

export enum Compress {
  DEFLATE = EpsonEpos.PRINTER_COMPRESS_DEFLATE,
  NONE = EpsonEpos.PRINTER_COMPRESS_NONE,
  AUTO = EpsonEpos.PRINTER_COMPRESS_AUTO,
  DEFAULT = EpsonEpos.PRINTER_PARAM_DEFAULT,
}

export interface Image {
  path: string;
  x?: number;
  y?: number;
  width: number;
  height: number;
  color?: Color;
  mode?: ColorMode;
  halftone?: Halftone;
  brightness?: number;
  compress?: Compress;
}

export async function addImage(image: Image): Promise<void> {
  if (!('x' in image)) {
    image.x = 0;
  }
  if (!('y' in image)) {
    image.y = 0;
  }
  if (!('color' in image)) {
    image.color = Color.DEFAULT;
  }
  if (!('mode' in image)) {
    image.mode = ColorMode.DEFAULT;
  }
  if (!('halftone' in image)) {
    image.halftone = Halftone.DEFAULT;
  }
  if (!('brightness' in image)) {
    image.brightness = 1.0;
  }
  if (!('compress' in image)) {
    image.compress = Compress.DEFAULT;
  }
  await EpsonEpos.addImage(
    image.path,
    image.x,
    image.y,
    image.width,
    image.height,
    image.color,
    image.mode,
    image.halftone,
    image.brightness,
    image.compress
  );
}

export enum Cut {
  FEED = EpsonEpos.PRINTER_CUT_FEED,
  NO_FEED = EpsonEpos.PRINTER_CUT_NO_FEED,
  RESERVE = EpsonEpos.PRINTER_CUT_RESERVE,
  DEFAULT = EpsonEpos.PRINTER_PARAM_DEFAULT,
}

export async function addCut(type: Cut = Cut.DEFAULT): Promise<void> {
  await EpsonEpos.addCut(type);
}

export async function beginTransaction(): Promise<void> {
  await EpsonEpos.beginTransaction();
}

export async function endTransaction(): Promise<void> {
  await EpsonEpos.endTransaction();
}

export async function clearCommandBuffer(): Promise<void> {
  await EpsonEpos.clearCommandBuffer();
}

export const defaultSendDataTimeout = 10000;
export const minSendDataTimeout = 5000;
export const maxSendDataTimeout = 600000;

export async function sendData(
  timeout: number = defaultSendDataTimeout
): Promise<void> {
  if (timeout < minSendDataTimeout) {
    timeout = minSendDataTimeout;
  } else if (timeout > maxSendDataTimeout) {
    timeout = maxSendDataTimeout;
  }
  await EpsonEpos.sendData(timeout);
}

export async function print(): Promise<void> {
  await EpsonEpos.print();
}

export enum PaperStatus {
  OK = EpsonEpos.PRINTER_PAPER_OK,
  NEAR_END = EpsonEpos.PRINTER_PAPER_NEAR_END,
  EMPTY = EpsonEpos.PRINTER_PAPER_EMPTY,
  UNKNOWN = EpsonEpos.PRINTER_UNKNOWN,
}

export enum DrawerStatus {
  HIGH = EpsonEpos.PRINTER_DRAWER_HIGH,
  LOW = EpsonEpos.PRINTER_DRAWER_LOW,
  UNKNOWN = EpsonEpos.PRINTER_UNKNOWN,
}

export enum BatteryLevel {
  LEVEL_0 = EpsonEpos.PRINTER_BATTERY_LEVEL_0,
  LEVEL_1 = EpsonEpos.PRINTER_BATTERY_LEVEL_1,
  LEVEL_2 = EpsonEpos.PRINTER_BATTERY_LEVEL_2,
  LEVEL_3 = EpsonEpos.PRINTER_BATTERY_LEVEL_3,
  LEVEL_4 = EpsonEpos.PRINTER_BATTERY_LEVEL_4,
  LEVEL_5 = EpsonEpos.PRINTER_BATTERY_LEVEL_5,
  LEVEL_6 = EpsonEpos.PRINTER_BATTERY_LEVEL_6,
  UNKNOWN = EpsonEpos.PRINTER_UNKNOWN,
}

export interface PrinterStatus {
  connection?: boolean;
  online?: boolean;
  coverOpen?: boolean;
  paper?: PaperStatus;
  paperFeed?: boolean;
  panelSwitch?: boolean;
  drawer?: DrawerStatus;
  buzzer?: boolean;
  adapter?: boolean;
  batteryLevel?: BatteryLevel;
}

export function printerStatusToStrings(
  printerStatus: PrinterStatus | null
): Array<string> {
  const result: string[] = [];

  if (!printerStatus) {
    return result;
  }

  if ('connection' in printerStatus) {
    result.push(printerStatus.connection ? 'Connected' : 'Disconnected');
  }

  if ('online' in printerStatus) {
    result.push(printerStatus.online ? 'Online' : 'Offline');
  }

  if ('coverOpen' in printerStatus) {
    result.push(printerStatus.coverOpen ? 'Cover is open' : 'Cover is closed');
  }

  if ('paper' in printerStatus) {
    switch (printerStatus.paper) {
      case PaperStatus.OK:
        result.push('Paper remains');
        break;

      case PaperStatus.NEAR_END:
        result.push('Paper is running out');
        break;

      case PaperStatus.EMPTY:
        result.push('Paper has run out');
        break;

      case PaperStatus.UNKNOWN:
        result.push('Paper status is unknown');
        break;
    }
  }

  if ('paperFeed' in printerStatus) {
    result.push(
      printerStatus.paperFeed ? 'Paper feed in progress' : 'Paper feed stopped'
    );
  }

  if ('panelSwitch' in printerStatus) {
    result.push(
      printerStatus.panelSwitch
        ? 'Panel switch pressed'
        : 'Paper switch not pressed'
    );
  }

  if ('drawer' in printerStatus) {
    switch (printerStatus.drawer) {
      case DrawerStatus.HIGH:
        result.push('Drawer high');
        break;

      case DrawerStatus.LOW:
        result.push('Drawer low');
        break;
    }
  }

  if ('buzzer' in printerStatus) {
    result.push(printerStatus.buzzer ? 'Buzzer sounding' : 'Buzzer stopped');
  }

  if ('adapter' in printerStatus) {
    result.push(
      printerStatus.adapter ? 'Adapter connected' : 'Adapter disconnected'
    );
  }

  if ('batteryLevel' in printerStatus) {
    switch (printerStatus.batteryLevel) {
      case BatteryLevel.LEVEL_0:
        result.push('Battery level : 0%');
        break;

      case BatteryLevel.LEVEL_1:
        result.push('Battery level : 16%');
        break;

      case BatteryLevel.LEVEL_2:
        result.push('Battery level : 33%');
        break;

      case BatteryLevel.LEVEL_3:
        result.push('Battery level : 50%');
        break;

      case BatteryLevel.LEVEL_4:
        result.push('Battery level : 66%');
        break;

      case BatteryLevel.LEVEL_5:
        result.push('Battery level : 80%');
        break;

      case BatteryLevel.LEVEL_6:
        result.push('Battery level : 100%');
        break;

      case BatteryLevel.UNKNOWN:
        result.push('Unknown battery level');
        break;
    }
  }

  return result;
}

export async function getPrinterStatus(): Promise<PrinterStatus> {
  return <PrinterStatus>await EpsonEpos.getPrinterStatus();
}

export enum StatusEvent {
  ONLINE = EpsonEpos.PRINTER_EVENT_ONLINE,
  OFFLINE = EpsonEpos.PRINTER_EVENT_OFFLINE,
  POWER_OFF = EpsonEpos.PRINTER_EVENT_POWER_OFF,
  COVER_CLOSE = EpsonEpos.PRINTER_EVENT_COVER_CLOSE,
  COVER_OPEN = EpsonEpos.PRINTER_EVENT_COVER_OPEN,
  PAPER_NEAR_END = EpsonEpos.PRINTER_EVENT_PAPER_NEAR_END,
  PAPER_EMPTY = EpsonEpos.PRINTER_EVENT_PAPER_EMPTY,
  DRAWER_HIGH = EpsonEpos.PRINTER_EVENT_DRAWER_HIGH,
  DRAWER_LOW = EpsonEpos.PRINTER_EVENT_DRAWER_LOW,
  BATTERY_ENOUGH = EpsonEpos.PRINTER_EVENT_BATTERY_ENOUGH,
  BATTERY_EMPTY = EpsonEpos.PRINTER_EVENT_BATTERY_EMPTY,
}

export function printerSettingsToStrings(
  printerSettings: PrinterSettings | null
): Array<string> {
  const result: string[] = [];

  if (!printerSettings) {
    return result;
  }

  if ('paperWidth' in printerSettings) {
    result.push(`Paper width: ${printerSettings.paperWidth}mm`);
  }

  if ('printDensity' in printerSettings) {
    result.push(`Print density: ${printerSettings.printDensity}%`);
  }

  if ('printSpeed' in printerSettings) {
    result.push(`Print speed: ${printerSettings.printSpeed}`);
  }

  return result;
}

export function usePrinterStatus(): {
  pending: boolean;
  status: PrinterStatus | null;
  error: Error | null;
  setStatus: any;
} {
  const [pending, setPending] = useState(false);
  const [status, setStatus] = useState(null as PrinterStatus | null);
  const [error, setError] = useState(null as Error | null);

  const onStatusEvent = useCallback((statusEvent: { type: StatusEvent }) => {
    switch (statusEvent.type) {
      case StatusEvent.ONLINE:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          online: true,
        }));
        break;

      case StatusEvent.OFFLINE:
      case StatusEvent.POWER_OFF:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          connection: false,
          online: false,
        }));
        break;

      case StatusEvent.COVER_CLOSE:
      case StatusEvent.COVER_OPEN:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          coverOpen: statusEvent.type === StatusEvent.COVER_OPEN,
        }));
        break;

      case StatusEvent.PAPER_NEAR_END:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          paper: PaperStatus.NEAR_END,
        }));
        break;

      case StatusEvent.PAPER_EMPTY:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          paper: PaperStatus.EMPTY,
        }));
        break;

      case StatusEvent.DRAWER_HIGH:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          drawer: DrawerStatus.HIGH,
        }));
        break;

      case StatusEvent.DRAWER_LOW:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          drawer: DrawerStatus.LOW,
        }));
        break;

      case StatusEvent.BATTERY_EMPTY:
        setStatus((prevState: PrinterStatus | null) => ({
          ...prevState,
          batteryLevel: BatteryLevel.LEVEL_0,
        }));
        break;
    }
  }, []);

  useEffect(() => {
    epsonEposEmitter.addListener('status', onStatusEvent);

    (async () => {
      try {
        setPending(true);
        setStatus(await getPrinterStatus());
        setPending(false);
      } catch (e) {
        setPending(false);
        setError(e);
      }
    })();

    return () => epsonEposEmitter.removeListener('status', onStatusEvent);
  }, [onStatusEvent]);

  return {
    pending,
    status,
    error,
    setStatus: (s: PrinterStatus | null) => {
      setStatus(s);
      setPending(false);
      setError(null);
    },
  };
}

export async function printTestSheet() {
  await EpsonEpos.printTestSheet();
}
