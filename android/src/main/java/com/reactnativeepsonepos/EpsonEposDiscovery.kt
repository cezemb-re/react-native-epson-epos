package com.reactnativeepsonepos

import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.FilterOption
import com.epson.epos2.discovery.DeviceInfo

import android.content.Context
import android.util.Log

class EpsonEposDiscovery {

  var filterOption: FilterOption = FilterOption()

  fun start(
    context: Context,
    portType: Int = Discovery.PORTTYPE_ALL,
    broadcast: String? = "255.255.255.255",
    deviceModel: Int = Discovery.MODEL_ALL,
    epsonFilter: Int = Discovery.FILTER_NAME,
    deviceType: Int = Discovery.TYPE_ALL,
    bondedDevices: Int = Discovery.FALSE,
    discoveryListener: DiscoveryListener
  ) {
    filterOption.portType = portType
    filterOption.broadcast = broadcast
    filterOption.deviceModel = deviceModel
    filterOption.epsonFilter = epsonFilter
    filterOption.deviceType = deviceType
    filterOption.bondedDevices = bondedDevices

    Discovery.start(context, filterOption, discoveryListener);

  }

  fun stop() {
    Discovery.stop();
  }

}
