package com.reactnativeepsonepos

import android.util.Log

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.modules.core.DeviceEventManagerModule

import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.DeviceInfo
import com.epson.epos2.Epos2Exception

class EpsonEposModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  var discovery: EpsonEposDiscovery = EpsonEposDiscovery()

  override fun getName(): String {
      return "EpsonEpos"
  }

  override fun getConstants(): Map<String, Any> {
    val constants: HashMap<String, Any> = HashMap()

    // Filter options

    // Port types
    constants.put("FILTER_OPTION_PORT_TYPE_ALL", Discovery.PORTTYPE_ALL)
    constants.put("FILTER_OPTION_PORT_TYPE_TCP", Discovery.PORTTYPE_TCP)
    constants.put("FILTER_OPTION_PORT_TYPE_BLUETOOTH", Discovery.PORTTYPE_BLUETOOTH)
    constants.put("FILTER_OPTION_PORT_TYPE_USB", Discovery.PORTTYPE_USB)

    // Device models
    constants.put("FILTER_OPTION_DEVICE_MODEL_ALL", Discovery.MODEL_ALL)

    // Epson filters
    constants.put("FILTER_OPTION_FILTER_NAME", Discovery.FILTER_NAME)
    constants.put("FILTER_OPTION_FILTER_NONE", Discovery.FILTER_NONE)

    // Device types
    constants.put("FILTER_OPTION_DEVICE_TYPE_ALL", Discovery.TYPE_ALL)
    constants.put("FILTER_OPTION_DEVICE_TYPE_PRINTER", Discovery.TYPE_PRINTER)
    constants.put("FILTER_OPTION_DEVICE_TYPE_HYBRID_PRINTER", Discovery.TYPE_HYBRID_PRINTER)
    constants.put("FILTER_OPTION_DEVICE_TYPE_DISPLAY", Discovery.TYPE_DISPLAY)
    constants.put("FILTER_OPTION_DEVICE_TYPE_KEYBOARD", Discovery.TYPE_KEYBOARD)
    constants.put("FILTER_OPTION_DEVICE_TYPE_SCANNER", Discovery.TYPE_SCANNER)
    constants.put("FILTER_OPTION_DEVICE_TYPE_SERIAL", Discovery.TYPE_SERIAL)
    constants.put("FILTER_OPTION_DEVICE_TYPE_POS_KEYBOARD", Discovery.TYPE_POS_KEYBOARD)
    constants.put("FILTER_OPTION_DEVICE_TYPE_MSR", Discovery.TYPE_MSR)
    constants.put("FILTER_OPTION_DEVICE_TYPE_GFE", Discovery.TYPE_GFE)
    constants.put("FILTER_OPTION_DEVICE_TYPE_OTHER_PERIPHERAL", Discovery.TYPE_OTHER_PERIPHERAL)

    // Bonded devices
    constants.put("FILTER_OPTION_BONDED_DEVICES_TRUE", Discovery.TRUE)
    constants.put("FILTER_OPTION_BONDED_DEVICES_FALSE", Discovery.FALSE)

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

      val reactContext = getReactApplicationContext();

      val discoveryListener = object : DiscoveryListener {
        override fun onDiscovery(deviceInfo: DeviceInfo) {

          val deviceName = deviceInfo.getDeviceName();
          var deviceTarget = deviceInfo.getTarget()

          Log.d("EpsonEpos", "Device found : $deviceTarget")

          reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("discovery", deviceTarget)
        }
      }

      discovery.start(
        reactContext,
        portType,
        broadcast,
        deviceModel,
        epsonFilter,
        deviceType,
        bondedDevices,
        discoveryListener
      );

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

      discovery.stop();

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


}
