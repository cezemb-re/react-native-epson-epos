package com.reactnativeepsonepos

import android.util.Log

import android.content.Context

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.Arguments

import com.epson.epos2.Epos2Exception
import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.FilterOption
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.DeviceInfo
import com.epson.epos2.printer.Printer
import com.epson.epos2.printer.PrinterStatusInfo
import com.epson.epos2.printer.PrinterSettingListener
import com.epson.epos2.printer.StatusChangeListener
import com.epson.epos2.printer.ReceiveListener

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;

class EpsonEposModule : ReactContextBaseJavaModule {

  var _context: ReactApplicationContext? = null

  var _printerSeries: Int = Printer.TM_M10
  var _lang: Int = Printer.MODEL_ANK
  var _target: String? = null

  var _printer: Printer? = null

  var _assetManager: AssetManager? = null

  var _printerSettingListener: PrinterSettingListener? = null

  var _paperWidth: Int = 0

  constructor(reactContext: ReactApplicationContext) : super(reactContext) {
    _context = reactContext
    _assetManager = reactContext.getAssets()

    _printerSettingListener = object : PrinterSettingListener {
      override fun onGetPrinterSetting(code: Int, type: Int, value: Int) {

        val setting: WritableMap = Arguments.createMap()

        setting.putInt("code", code)
        setting.putInt("type", type)
        setting.putInt("value", value)

        if (code == 0 && type == Printer.SETTING_PAPERWIDTH) {
          when (value) {
            Printer.SETTING_PAPERWIDTH_58_0 -> _paperWidth = 58
            Printer.SETTING_PAPERWIDTH_60_0 -> _paperWidth = 60
            Printer.SETTING_PAPERWIDTH_80_0 -> _paperWidth = 80
            else -> _paperWidth = 0
          }
        }

        _context?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)?.emit("setting", setting)
      }

      override fun onSetPrinterSetting(code: Int) {

        Log.d("EpsonEpos", "Set printer setting code: $code")

        //val setting: WritableMap = Arguments.createMap()

        //setting.putInt("code", code)
        //setting.putInt("type", type)
        //setting.putInt("value", value)

        //_context?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)?.emit("setting", setting)
      }
    }
  }

  override fun getName(): String {
      return "EpsonEpos"
  }

  override fun getConstants(): Map<String, Any> {
    val constants: HashMap<String, Any> = HashMap()

    // Discovery
    constants.put("DISCOVERY_TRUE", Discovery.TRUE)
    constants.put("DISCOVERY_FALSE", Discovery.FALSE)

    // Discovery > Port types
    constants.put("DISCOVERY_PORTTYPE_ALL", Discovery.PORTTYPE_ALL)
    constants.put("DISCOVERY_PORTTYPE_TCP", Discovery.PORTTYPE_TCP)
    constants.put("DISCOVERY_PORTTYPE_BLUETOOTH", Discovery.PORTTYPE_BLUETOOTH)
    constants.put("DISCOVERY_PORTTYPE_USB", Discovery.PORTTYPE_USB)

    // Discovery > Models
    constants.put("DISCOVERY_MODEL_ALL", Discovery.MODEL_ALL)

    // Discovery > Filters
    constants.put("DISCOVERY_FILTER_NAME", Discovery.FILTER_NAME)
    constants.put("DISCOVERY_FILTER_NONE", Discovery.FILTER_NONE)

    // Discovery > Types
    constants.put("DISCOVERY_TYPE_ALL", Discovery.TYPE_ALL)
    constants.put("DISCOVERY_TYPE_PRINTER", Discovery.TYPE_PRINTER)
    constants.put("DISCOVERY_TYPE_HYBRID_PRINTER", Discovery.TYPE_HYBRID_PRINTER)
    constants.put("DISCOVERY_TYPE_DISPLAY", Discovery.TYPE_DISPLAY)
    constants.put("DISCOVERY_TYPE_KEYBOARD", Discovery.TYPE_KEYBOARD)
    constants.put("DISCOVERY_TYPE_SCANNER", Discovery.TYPE_SCANNER)
    constants.put("DISCOVERY_TYPE_SERIAL", Discovery.TYPE_SERIAL)
    constants.put("DISCOVERY_TYPE_POS_KEYBOARD", Discovery.TYPE_POS_KEYBOARD)
    constants.put("DISCOVERY_TYPE_MSR", Discovery.TYPE_MSR)
    constants.put("DISCOVERY_TYPE_GFE", Discovery.TYPE_GFE)
    constants.put("DISCOVERY_TYPE_OTHER_PERIPHERAL", Discovery.TYPE_OTHER_PERIPHERAL)

    // Printer
    constants.put("PRINTER_FALSE", Printer.FALSE)
    constants.put("PRINTER_TRUE", Printer.TRUE)
    constants.put("PRINTER_UNKNOWN", Printer.UNKNOWN)
    constants.put("PRINTER_PARAM_DEFAULT", Printer.PARAM_DEFAULT)

    // Printer > Series
    constants.put("PRINTER_TM_M10", Printer.TM_M10)
    constants.put("PRINTER_TM_M30", Printer.TM_M30)
    constants.put("PRINTER_TM_M30II", Printer.TM_M30II)
    constants.put("PRINTER_TM_P20", Printer.TM_P20)
    constants.put("PRINTER_TM_P60", Printer.TM_P60)
    constants.put("PRINTER_TM_P60II", Printer.TM_P60II)
    constants.put("PRINTER_TM_P80", Printer.TM_P80)
    constants.put("PRINTER_TM_T20", Printer.TM_T20)
    constants.put("PRINTER_TM_T60", Printer.TM_T60)
    constants.put("PRINTER_TM_T70", Printer.TM_T70)
    constants.put("PRINTER_TM_T81", Printer.TM_T81)
    constants.put("PRINTER_TM_T82", Printer.TM_T82)
    constants.put("PRINTER_TM_T83", Printer.TM_T83)
    constants.put("PRINTER_TM_T83III", Printer.TM_T83III)
    constants.put("PRINTER_TM_T88", Printer.TM_T88)
    constants.put("PRINTER_TM_T90", Printer.TM_T90)
    constants.put("PRINTER_TM_T100", Printer.TM_T100)
    constants.put("PRINTER_TM_U220", Printer.TM_U220)
    constants.put("PRINTER_TM_U330", Printer.TM_U330)
    constants.put("PRINTER_TM_L90", Printer.TM_L90)
    constants.put("PRINTER_TM_H6000", Printer.TM_H6000)

    // Printer > Models
    constants.put("PRINTER_MODEL_ANK", Printer.MODEL_ANK)
    constants.put("PRINTER_MODEL_CHINESE", Printer.MODEL_CHINESE)
    constants.put("PRINTER_MODEL_TAIWAN", Printer.MODEL_TAIWAN)
    constants.put("PRINTER_MODEL_KOREAN", Printer.MODEL_KOREAN)
    constants.put("PRINTER_MODEL_THAI", Printer.MODEL_THAI)
    constants.put("PRINTER_MODEL_SOUTHASIA", Printer.MODEL_SOUTHASIA)

    // Printer > Langs
    constants.put("PRINTER_LANG_EN", Printer.LANG_EN)
    constants.put("PRINTER_LANG_JA", Printer.LANG_JA)
    constants.put("PRINTER_LANG_ZH_CN", Printer.LANG_ZH_CN)
    constants.put("PRINTER_LANG_ZH_TW", Printer.LANG_ZH_TW)
    constants.put("PRINTER_LANG_KO", Printer.LANG_KO)
    constants.put("PRINTER_LANG_TH", Printer.LANG_TH)
    constants.put("PRINTER_LANG_VI", Printer.LANG_VI)

    // Printer > Status
    constants.put("PRINTER_PAPER_OK", Printer.PAPER_OK)
    constants.put("PRINTER_PAPER_NEAR_END", Printer.PAPER_NEAR_END)
    constants.put("PRINTER_PAPER_EMPTY", Printer.PAPER_EMPTY)
    constants.put("PRINTER_SWITCH_ON", Printer.SWITCH_ON)
    constants.put("PRINTER_SWITCH_OFF", Printer.SWITCH_OFF)
    constants.put("PRINTER_DRAWER_HIGH", Printer.DRAWER_HIGH)
    constants.put("PRINTER_DRAWER_LOW", Printer.DRAWER_LOW)
    constants.put("PRINTER_BATTERY_LEVEL_0", Printer.BATTERY_LEVEL_0)
    constants.put("PRINTER_BATTERY_LEVEL_1", Printer.BATTERY_LEVEL_1)
    constants.put("PRINTER_BATTERY_LEVEL_2", Printer.BATTERY_LEVEL_2)
    constants.put("PRINTER_BATTERY_LEVEL_3", Printer.BATTERY_LEVEL_3)
    constants.put("PRINTER_BATTERY_LEVEL_4", Printer.BATTERY_LEVEL_4)
    constants.put("PRINTER_BATTERY_LEVEL_5", Printer.BATTERY_LEVEL_5)
    constants.put("PRINTER_BATTERY_LEVEL_6", Printer.BATTERY_LEVEL_6)

    // Printer > Aligns
    constants.put("PRINTER_ALIGN_LEFT", Printer.ALIGN_LEFT)
    constants.put("PRINTER_ALIGN_CENTER", Printer.ALIGN_CENTER)
    constants.put("PRINTER_ALIGN_RIGHT", Printer.ALIGN_RIGHT)
    constants.put("PRINTER_PARAM_DEFAULT", Printer.PARAM_DEFAULT)

    // Printer > Events
    constants.put("PRINTER_EVENT_ONLINE", Printer.EVENT_ONLINE)
    constants.put("PRINTER_EVENT_OFFLINE", Printer.EVENT_OFFLINE)
    constants.put("PRINTER_EVENT_POWER_OFF", Printer.EVENT_POWER_OFF)
    constants.put("PRINTER_EVENT_COVER_CLOSE", Printer.EVENT_COVER_CLOSE)
    constants.put("PRINTER_EVENT_COVER_OPEN", Printer.EVENT_COVER_OPEN)
    constants.put("PRINTER_EVENT_PAPER_NEAR_END", Printer.EVENT_PAPER_NEAR_END)
    constants.put("PRINTER_EVENT_PAPER_EMPTY", Printer.EVENT_PAPER_EMPTY)
    constants.put("PRINTER_EVENT_DRAWER_HIGH", Printer.EVENT_DRAWER_HIGH)
    constants.put("PRINTER_EVENT_DRAWER_LOW", Printer.EVENT_DRAWER_LOW)
    constants.put("PRINTER_EVENT_BATTERY_ENOUGH", Printer.EVENT_BATTERY_ENOUGH)
    constants.put("PRINTER_EVENT_BATTERY_EMPTY", Printer.EVENT_BATTERY_EMPTY)

    // Printer > Settings
    constants.put("PRINTER_SETTING_PAPER_WIDTH", Printer.SETTING_PAPERWIDTH)
    constants.put("PRINTER_SETTING_PRINT_DENSITY", Printer.SETTING_PRINTDENSITY)
    constants.put("PRINTER_SETTING_PRINT_SPEED", Printer.SETTING_PRINTSPEED)

    constants.put("PRINTER_SETTING_PAPER_WIDTH_58", Printer.SETTING_PAPERWIDTH_58_0)
    constants.put("PRINTER_SETTING_PAPER_WIDTH_60", Printer.SETTING_PAPERWIDTH_60_0)
    constants.put("PRINTER_SETTING_PAPER_WIDTH_80", Printer.SETTING_PAPERWIDTH_80_0)

    constants.put("PRINTER_SETTING_PRINT_DENSITY_DIP", Printer.SETTING_PRINTDENSITY_DIP)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_70", Printer.SETTING_PRINTDENSITY_70)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_75", Printer.SETTING_PRINTDENSITY_75)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_80", Printer.SETTING_PRINTDENSITY_80)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_85", Printer.SETTING_PRINTDENSITY_85)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_90", Printer.SETTING_PRINTDENSITY_90)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_95", Printer.SETTING_PRINTDENSITY_95)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_100", Printer.SETTING_PRINTDENSITY_100)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_105", Printer.SETTING_PRINTDENSITY_105)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_110", Printer.SETTING_PRINTDENSITY_110)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_115", Printer.SETTING_PRINTDENSITY_115)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_120", Printer.SETTING_PRINTDENSITY_120)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_125", Printer.SETTING_PRINTDENSITY_125)
    constants.put("PRINTER_SETTING_PRINT_DENSITY_130", Printer.SETTING_PRINTDENSITY_130)

    constants.put("PRINTER_SETTING_PRINT_SPEED_1", Printer.SETTING_PRINTSPEED_1)
    constants.put("PRINTER_SETTING_PRINT_SPEED_2", Printer.SETTING_PRINTSPEED_2)
    constants.put("PRINTER_SETTING_PRINT_SPEED_3", Printer.SETTING_PRINTSPEED_3)
    constants.put("PRINTER_SETTING_PRINT_SPEED_4", Printer.SETTING_PRINTSPEED_4)
    constants.put("PRINTER_SETTING_PRINT_SPEED_5", Printer.SETTING_PRINTSPEED_5)
    constants.put("PRINTER_SETTING_PRINT_SPEED_6", Printer.SETTING_PRINTSPEED_6)
    constants.put("PRINTER_SETTING_PRINT_SPEED_7", Printer.SETTING_PRINTSPEED_7)
    constants.put("PRINTER_SETTING_PRINT_SPEED_8", Printer.SETTING_PRINTSPEED_8)
    constants.put("PRINTER_SETTING_PRINT_SPEED_9", Printer.SETTING_PRINTSPEED_9)
    constants.put("PRINTER_SETTING_PRINT_SPEED_10", Printer.SETTING_PRINTSPEED_10)
    constants.put("PRINTER_SETTING_PRINT_SPEED_11", Printer.SETTING_PRINTSPEED_11)
    constants.put("PRINTER_SETTING_PRINT_SPEED_12", Printer.SETTING_PRINTSPEED_12)
    constants.put("PRINTER_SETTING_PRINT_SPEED_13", Printer.SETTING_PRINTSPEED_13)
    constants.put("PRINTER_SETTING_PRINT_SPEED_14", Printer.SETTING_PRINTSPEED_14)

    // Printer > Fonts
    constants.put("PRINTER_FONT_A", Printer.FONT_A)
    constants.put("PRINTER_FONT_B", Printer.FONT_B)
    constants.put("PRINTER_FONT_C", Printer.FONT_C)
    constants.put("PRINTER_FONT_D", Printer.FONT_D)
    constants.put("PRINTER_FONT_E", Printer.FONT_E)

    // Printer > Colors
    constants.put("PRINTER_COLOR_NONE", Printer.COLOR_NONE)
    constants.put("PRINTER_COLOR_1", Printer.COLOR_1)
    constants.put("PRINTER_COLOR_2", Printer.COLOR_2)
    constants.put("PRINTER_COLOR_3", Printer.COLOR_3)
    constants.put("PRINTER_COLOR_4", Printer.COLOR_4)

    // Printer > Modes
    constants.put("PRINTER_MODE_MONO", Printer.MODE_MONO)
    constants.put("PRINTER_MODE_GRAY16", Printer.MODE_GRAY16)
    constants.put("PRINTER_MODE_MONO_HIGH_DENSITY", Printer.MODE_MONO_HIGH_DENSITY)

    // Printer > Halftones
    constants.put("PRINTER_HALFTONE_DITHER", Printer.HALFTONE_DITHER)
    constants.put("PRINTER_HALFTONE_ERROR_DIFFUSION", Printer.HALFTONE_ERROR_DIFFUSION)
    constants.put("PRINTER_HALFTONE_THRESHOLD", Printer.HALFTONE_THRESHOLD)

    // Printer > Halftones
    constants.put("PRINTER_COMPRESS_DEFLATE", Printer.COMPRESS_DEFLATE)
    constants.put("PRINTER_COMPRESS_NONE", Printer.COMPRESS_NONE)
    constants.put("PRINTER_COMPRESS_AUTO", Printer.COMPRESS_AUTO)

    // Printer > Cuts
    constants.put("PRINTER_CUT_FEED", Printer.CUT_FEED)
    constants.put("PRINTER_CUT_NO_FEED", Printer.CUT_NO_FEED)
    constants.put("PRINTER_CUT_RESERVE", Printer.CUT_RESERVE)

    return constants
  }

  @ReactMethod
  fun startDiscovery(
    portType: Int,
    broadcast: String,
    deviceModel: Int,
    epsonFilter: Int,
    deviceType: Int,
    bondedDevices: Int,
    promise: Promise
  ) {

    try {

      val discoveryListener = object : DiscoveryListener {
        override fun onDiscovery(deviceInfo: DeviceInfo) {

          Log.d("EpsonEpos", "Device Found: ${deviceInfo.getDeviceName()}")

          val device: WritableMap = Arguments.createMap()

          device.putString("target", deviceInfo.getTarget())
          device.putString("deviceName", deviceInfo.getDeviceName())
          device.putString("ipAddress", deviceInfo.getIpAddress())
          device.putString("macAddress", deviceInfo.getMacAddress())
          device.putString("bdAddress", deviceInfo.getBdAddress())

          _context?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)?.emit("discovery", device)
        }
      }

      var filterOption: FilterOption = FilterOption()

      filterOption.portType = portType
      filterOption.broadcast = broadcast
      filterOption.deviceModel = deviceModel
      filterOption.epsonFilter = epsonFilter
      filterOption.deviceType = deviceType
      filterOption.bondedDevices = bondedDevices

      Discovery.start(_context, filterOption, discoveryListener);

      promise.resolve(null)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("Tried to start search when search had been already done. Bluetooth is OFF. There is no permission for the position information."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        Epos2Exception.ERR_PROCESSING -> promise.reject("ERR_PROCESSING", Exception("Could not run the process."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun stopDiscovery(
    promise: Promise
  ) {

    try {

      Discovery.stop()

      promise.resolve(null)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("Tried to stop a search while it had not been started."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun initPrinter(
    printerSeries: Int,
    lang: Int,
    promise: Promise
  ) {

    try {

      _printer = Printer(printerSeries, lang, _context)

      val statusListener = object : StatusChangeListener {
        override fun onPtrStatusChange(printerObj: Printer, eventType: Int) {

          val status: WritableMap = Arguments.createMap()

          status.putInt("type", eventType)

          _context?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)?.emit("status", status)
        }
      }

      _printer?.setStatusChangeEventListener(statusListener)

      promise.resolve(null)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_UNSUPPORTED -> promise.reject("ERR_UNSUPPORTED", Exception("A model name or language not supported was specified."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun connectPrinter(
    printerSeries: Int,
    lang: Int,
    target: String,
    timeout: Int,
    promise: Promise
  ) {

    try {

      if (_printer === null || _printerSeries != printerSeries || _lang != lang) {

        _printer = Printer(printerSeries, lang, _context)

        val statusListener = object : StatusChangeListener {
          override fun onPtrStatusChange(printerObj: Printer, eventType: Int) {

            val status: WritableMap = Arguments.createMap()

            status.putInt("type", eventType)

            _context?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)?.emit("status", status)
          }
        }

        _printer?.setStatusChangeEventListener(statusListener)

      }

      Log.d("EpsonEpos", "Connecting to ${target} ...")

      _target = target

      _printer?.connect(target, timeout)

      Log.d("EpsonEpos", "Connected to ${target} !")

      _printer?.startMonitor()

      getPrinterStatus(promise)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_UNSUPPORTED -> promise.reject("ERR_UNSUPPORTED", Exception("A model name or language not supported was specified."))
        Epos2Exception.ERR_CONNECT -> promise.reject("ERR_CONNECT", Exception("Failed to open the device."))
        Epos2Exception.ERR_TIMEOUT -> promise.reject("ERR_TIMEOUT", Exception("Failed to communicate with the devices within the specified time."))
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("Tried to start communication with a printer with which communication had been already established."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        Epos2Exception.ERR_PROCESSING -> promise.reject("ERR_PROCESSING", Exception("Could not run the process."))
        Epos2Exception.ERR_NOT_FOUND -> promise.reject("ERR_NOT_FOUND", Exception("The device could not be found."))
        Epos2Exception.ERR_IN_USE -> promise.reject("ERR_IN_USE", Exception("The device was in use."))
        Epos2Exception.ERR_TYPE_INVALID -> promise.reject("ERR_TYPE_INVALID", Exception("The device type is different."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun getPrinterSetting(
    timeout: Int,
    type: Int,
    promise: Promise
  ) {

    try {

      _printer?.getPrinterSetting(timeout, type, _printerSettingListener)

      promise.resolve(null)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun getPrinterSettings(
    timeout: Int,
    promise: Promise
  ) {

    try {

      _printer?.getPrinterSetting(timeout, Printer.SETTING_PAPERWIDTH, _printerSettingListener)
      _printer?.getPrinterSetting(timeout, Printer.SETTING_PRINTDENSITY, _printerSettingListener)
      _printer?.getPrinterSetting(timeout, Printer.SETTING_PRINTSPEED, _printerSettingListener)

      promise.resolve(null)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun disconnectPrinter(
    promise: Promise
  ) {

    try {

      Log.d("EpsonEpos", "Disconnecting ...")

      _printer?.stopMonitor()

      _printer?.disconnect()

      Log.d("EpsonEpos", "Disconnected !")

      promise.resolve(null)

    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("Tried to end communication where it had not been established."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Necessary memory could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        Epos2Exception.ERR_PROCESSING -> promise.reject("ERR_PROCESSING", Exception("Could not run the process."))
        Epos2Exception.ERR_DISCONNECT -> promise.reject("ERR_DISCONNECT", Exception("Failed to disconnect the device."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }

  }

  @ReactMethod
  fun getPrinterStatus(
    promise: Promise
  ) {

    if (_printer === null) {
      promise.resolve(null)
      return
    }

    var status: PrinterStatusInfo? = _printer?.getStatus()

    if (status?.errorStatus == Printer.NO_ERR) {

      val response: WritableMap = Arguments.createMap()

      response.putBoolean("connection", status.connection == Printer.TRUE)
      response.putBoolean("online", status.online == Printer.TRUE)
      response.putBoolean("coverOpen", status.coverOpen == Printer.TRUE)
      response.putInt("paper", status.paper)
      response.putBoolean("paperFeed", status.paperFeed == Printer.TRUE)
      response.putBoolean("panelSwitch", status.panelSwitch == Printer.SWITCH_ON)
      response.putInt("drawer", status.drawer)
      response.putBoolean("buzzer", status.buzzer == Printer.TRUE)
      response.putBoolean("adapter", status.adapter == Printer.TRUE)
      response.putInt("batteryLevel", status.batteryLevel)

      promise.resolve(response)

    } else if (status?.errorStatus == Printer.AUTORECOVER_ERR) {

      when (status.autoRecoverError) {
        Printer.HEAD_OVERHEAT -> promise.reject("HEAD_OVERHEAT", Exception("Head overheat error"))
        Printer.MOTOR_OVERHEAT -> promise.reject("MOTOR_OVERHEAT", Exception("Motor driver IC overheat error"))
        Printer.BATTERY_OVERHEAT -> promise.reject("BATTERY_OVERHEAT", Exception("Battery overheat error"))
        Printer.WRONG_PAPER -> promise.reject("WRONG_PAPER", Exception("Paper error"))
        Printer.COVER_OPEN -> promise.reject("COVER_OPEN", Exception("Cover is open"))
        Printer.UNKNOWN -> promise.reject("UNKNOWN", Exception("Status is unknown."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }

    } else {

      when (status?.errorStatus) {
        Printer.MECHANICAL_ERR -> promise.reject("MECHANICAL_ERR", Exception("Mechanical error occurred."))
        Printer.AUTOCUTTER_ERR -> promise.reject("AUTOCUTTER_ERR", Exception("Auto cutter error occurred."))
        Printer.UNRECOVER_ERR -> promise.reject("UNRECOVER_ERR", Exception("Unrecoverable error occurred."))
        Printer.UNKNOWN -> promise.reject("UNKNOWN", Exception("Status is unknown."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }

    }

  }

  @ReactMethod
  fun addTextAlign(
    align: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextAlign(align)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addLineSpace(
    lineSpace: Int,
    promise: Promise
  ) {
    try {
      _printer?.addLineSpace(lineSpace)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addTextRotate(
    rotate: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextRotate(rotate)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addText(
    data: String,
    promise: Promise
  ) {
    try {
      _printer?.addText(data)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addTextLang(
    lang: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextLang(lang)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addTextFont(
    font: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextFont(font)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addTextSmooth(
    smooth: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextSmooth(smooth)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addTextSize(
    width: Int,
    height: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextSize(width, height)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addTextStyle(
    reverse: Int,
    ul: Int,
    em: Int,
    color: Int,
    promise: Promise
  ) {
    try {
      _printer?.addTextStyle(reverse, ul, em, color)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addHPosition(
    x: Int,
    promise: Promise
  ) {
    try {
      _printer?.addHPosition(x)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addFeedUnit(
    unit: Int,
    promise: Promise
  ) {
    try {
      _printer?.addFeedUnit(unit)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addFeedLine(
    line: Int,
    promise: Promise
  ) {
    try {
      _printer?.addFeedLine(line)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun addImage(
    path: String,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    color: Int,
    mode: Int,
    halftone: Int,
    brightness: Double,
    compress: Int,
    promise: Promise
  ) {
    try {

      var inputStream: InputStream? = _assetManager?.open(path)

      var image: Bitmap = BitmapFactory.decodeStream(inputStream);

      _printer?.addImage(
        image,
        x, y,
        width, height,
        color,
        mode,
        halftone,
        brightness,
        compress
      )

      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  // ...

  @ReactMethod
  fun addCut(
    type: Int,
    promise: Promise
  ) {
    try {
      _printer?.addCut(type)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()
      when (errorStatus) {
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("The control commands have not been buffered. This API was called while no communication had been started."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  // ...

  @ReactMethod
  fun beginTransaction(
    promise: Promise
  ) {
    try {
      _printer?.beginTransaction()
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("The control commands have not been buffered. This API was called while no communication had been started."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun endTransaction(
    promise: Promise
  ) {
    try {
      _printer?.endTransaction()
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("The control commands have not been buffered. This API was called while no communication had been started."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun clearCommandBuffer(
    promise: Promise
  ) {
      _printer?.clearCommandBuffer()
      promise.resolve(null)
  }

  @ReactMethod
  fun sendData(
    timeout: Int,
    promise: Promise
  ) {
    try {
      _printer?.sendData(timeout)
      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        Epos2Exception.ERR_PROCESSING -> promise.reject("ERR_PROCESSING", Exception("Could not run the process."))
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("The control commands have not been buffered. This API was called while no communication had been started."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun print(
    promise: Promise
  ) {
    try {

      _printer?.addCut(Printer.CUT_FEED);

      _printer?.beginTransaction()

      _printer?.sendData(Printer.PARAM_DEFAULT)

      _printer?.endTransaction()

      _printer?.clearCommandBuffer()

      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        Epos2Exception.ERR_PROCESSING -> promise.reject("ERR_PROCESSING", Exception("Could not run the process."))
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("The control commands have not been buffered. This API was called while no communication had been started."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

  @ReactMethod
  fun printTestSheet(
    promise: Promise
  ) {
    try {

      _printer?.addTextAlign(Printer.ALIGN_CENTER);

      _printer?.addFeedLine(1)

      var inputStream: InputStream? = _assetManager?.open("img/icon.png")

      var icon: Bitmap = BitmapFactory.decodeStream(inputStream);

      _printer?.addImage(
        icon,
        0, 0,
        200, 200,
        Printer.PARAM_DEFAULT,
        Printer.PARAM_DEFAULT,
        Printer.PARAM_DEFAULT,
        1.0,
        Printer.PARAM_DEFAULT
      )

      _printer?.addTextSize(2, 2)

      _printer?.addTextFont(Printer.FONT_B)

      _printer?.addText("\n\nCézembre\n\n")

      _printer?.addTextAlign(Printer.ALIGN_LEFT);

      _printer?.addTextSize(1, 1)

      _printer?.addTextFont(Printer.FONT_A)

      val infos: StringBuilder = StringBuilder()
      infos.append("\n\n")
      infos.append("Package : @cezembre/react-native-epson-epos\n")
      infos.append("Author : Lucien Perouze\n")
      infos.append("Mail : lucien.perouze@cezembre.co\n")
      infos.append("Organisation : www.cezemb.re\n")
      infos.append("\n\n")

      _printer?.addText(infos.toString())

      _printer?.addTextSize(8, 8)
      _printer?.addText("8")

      _printer?.addTextSize(7, 7)
      _printer?.addText("7")

      _printer?.addTextSize(6, 6)
      _printer?.addText("6")

      _printer?.addTextSize(5, 5)
      _printer?.addText("5")

      _printer?.addTextSize(4, 4)
      _printer?.addText("4")

      _printer?.addTextSize(3, 3)
      _printer?.addText("3")

      _printer?.addTextSize(2, 2)
      _printer?.addText("2")

      _printer?.addTextSize(1, 1)
      _printer?.addText("1")

      _printer?.addText("\n")

      _printer?.addTextSize(3, 3)

      _printer?.addText("Font A\n")

      _printer?.addTextFont(Printer.FONT_B)
      _printer?.addText("Font B\n")

      _printer?.addTextFont(Printer.FONT_C)
      _printer?.addText("Font C\n")

      _printer?.addTextFont(Printer.FONT_A)
      _printer?.addTextSmooth(Printer.TRUE)
      _printer?.addTextSize(5, 5)
      _printer?.addText("Smooth")

      _printer?.addText("\n\n\n")

      _printer?.addCut(Printer.CUT_FEED);

      _printer?.beginTransaction()

      _printer?.sendData(Printer.PARAM_DEFAULT)

      _printer?.endTransaction()

      _printer?.clearCommandBuffer()

      promise.resolve(null)
    } catch (error: Epos2Exception) {
      val errorStatus: Int = error.getErrorStatus()

      when (errorStatus) {
        Epos2Exception.ERR_PARAM -> promise.reject("ERR_PARAM", Exception("An invalid parameter was passed."))
        Epos2Exception.ERR_MEMORY -> promise.reject("ERR_MEMORY", Exception("Memory necessary for processing could not be allocated."))
        Epos2Exception.ERR_FAILURE -> promise.reject("ERR_FAILURE", Exception("An unknown error occurred."))
        Epos2Exception.ERR_PROCESSING -> promise.reject("ERR_PROCESSING", Exception("Could not run the process."))
        Epos2Exception.ERR_ILLEGAL -> promise.reject("ERR_ILLEGAL", Exception("The control commands have not been buffered. This API was called while no communication had been started."))
        else -> promise.reject("UNKNOWN_ERROR", Exception("Unkown error"))
      }
    }
  }

}
