/**
 *  Qubino Flush Shutter v2
 *
 *  Copyright 2016 Tom Philip
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

//  Sources of inspiration
//  https://github.com/A5HRAJ/SmartThings/blob/master/somfy_shades.groovy
//  https://community.smartthings.com/t/fibaro-fgrm-222-roller-shutter/5804/49
//  https://github.com/kjamsek/SmartThings/blob/master/DeviceHandlers/Qubino/FlushShutter/QubinoFlushShutterDeviceHandler.groovy

metadata {
  definition(name: "Qubino Flush Shutter", namespace: "tommysqueak", author: "Tom Philip") {
    capability "Actuator"
    capability "Sensor"
    capability "Window Shade"
    capability "Switch Level"
    capability "Switch"
    capability "Refresh"

    capability "Configuration" // and updated() which get called each time the device is updated (incl. device handler)

    //	TODO, do we care about these?
    //capability "Power Meter"
    //capability "Energy Meter"

    //	Secure inclusion. How? Does the Software version of qubino support it?

    command "pause"
    command "unpause"
    command "calibrate"
    command "uncalibrate"

    command "setCustomLevel1"
    command "setCustomLevel2"
    command "setCustomLevel3"

    attribute "destinationLevel", "number"
    attribute "customLevel1Display", "number"
    attribute "customLevel2Display", "number"
    attribute "customLevel3Display", "number"


    fingerprint inClusters: "0x5E, 0x86, 0x72, 0x5A, 0x73, 0x20, 0x27, 0x25, 0x26, 0x32, 0x85, 0x8E, 0x59, 0x70", outClusters: "0x20, 0x26", model: "0052", prod: "0003"
    // zw:L type:1107 mfr:0159 prod:0003 model:0052 ver:1.01 zwv:4.05 lib:03 cc:5E,86,72,5A,73,20,27,25,26,32,85,8E,59,70 ccOut:20,26 role:05 ff:9A00 ui:9A00

    //	Qubino commands
    //	COMMAND_CLASS_POWERLEVEL_V1
    //	COMMAND_CLASS_METER_V4
    //	COMMAND_CLASS_SWITCH_ALL_V1
    //	COMMAND_CLASS_SWITCH_BINARY_V1
    //	COMMAND_CLASS_SWITCH_MULTILEVEL_V3
    //	COMMAND_CLASS_SENSOR_MULTILEVEL_V7 <-- for temperature
  }

  simulator {
    // TODO: define status and reply messages here
  }

  tiles(scale: 2) {
    multiAttributeTile(name: "toggle", type: "generic", width: 6, height: 4, canChangeIcon: false) {
      tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
        attributeState("unknown", label: '-', action: "refresh", icon: "st.doors.garage.garage-open", backgroundColor: "#ffa81e")
        attributeState("closed", label: '${name}', action: "open", icon: "st.doors.garage.garage-closed", backgroundColor: "#bbbbdd", nextState: "opening")
        attributeState("open", label: '${name}', action: "close", icon: "st.doors.garage.garage-open", backgroundColor: "#ffcc33", nextState: "closing")
        attributeState("partially open", label: 'partial', action: "unpause", icon: "st.doors.garage.garage-open", backgroundColor: "#ffcc33")
        attributeState("closing", label: '${name}', action: "pause", icon: "st.doors.garage.garage-closing", backgroundColor: "#bbbbdd", nextState: "partially open")
        attributeState("opening", label: '${name}', action: "pause", icon: "st.doors.garage.garage-opening", backgroundColor: "#ffcc33", nextState: "partially open")
      }

      tileAttribute("device.level", key: "VALUE_CONTROL") {
        attributeState("VALUE_UP", action: "open")
        attributeState("VALUE_DOWN", action: "close")
      }
    }

    standardTile("slats", "device.windowShade", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: 'preset - slats', action: "presetPosition", icon: "st.Kids.kids15"
    }
    standardTile("custom1", "device.customLevel1Display", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: 'preset - ${currentValue}%', action: "setCustomLevel1", icon: "st.Kids.kids15"
    }
    standardTile("custom2", "device.customLevel2Display", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: 'preset - ${currentValue}%', action: "setCustomLevel1", icon: "st.Kids.kids15"
    }
    standardTile("custom3", "device.customLevel3Display", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: 'preset - ${currentValue}%', action: "setCustomLevel1", icon: "st.Kids.kids15"
    }

    standardTile("refresh", "device.windowShade", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: '', action: "refresh", icon: "st.secondary.refresh"
    }
    standardTile("calibrate", "device.windowShade", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: 'calibrate', action: "calibrate", icon: "st.motion.acceleration.active"
    }
    standardTile("uncalibrate", "device.windowShade", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label: '', action: "uncalibrate", icon: "st.people.people10"
    }

    main "toggle"
    details(["toggle", "slats", "custom1", "custom2", "custom3", "refresh", "calibrate"])
  }

  preferences {
    input "slatsLevel", "number", title: "Slats Level", description: "Level for almost closed", range: "0..100", displayDuringSetup: false, defaultValue: 15
    input "customLevel1", "number", title: "Preset Level 1", description: "Set the level quickly", range: "0..100", displayDuringSetup: false, defaultValue: 25
    input "customLevel2", "number", title: "Preset Level 2", description: "Set the level quickly", range: "0..100", displayDuringSetup: false, defaultValue: 50
    input "customLevel3", "number", title: "Preset Level 3", description: "Set the level quickly", range: "0..100", displayDuringSetup: false, defaultValue: 75
  }
}

// parse events into attributes
def parse(String description) {
  def result = null
  def cmd = zwave.parse(description)

  if (cmd) {
    result = zwaveEvent(cmd)
    log.debug "Parsed ${cmd} to ${result.inspect()}"
  } else {
    log.debug "Non-parsed event: ${description}"
  }
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
  storeState(cmd.value)
}

//	Very occasionally we don't get SwitchMultilevelReport, but we get a BasicReport. So this is our back-up.
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
  storeState(cmd.value)
}

def storeState(level) {
  def result = []
  log.trace "SwitchMultilevelReport cmd.value:  ${level}"

  if (level == 0) {
    result << createEvent(name: "switch", value: "off")
    result << createEvent(name: "windowShade", value: "closed")
    log.debug "Reported state is closed; device is ${device.latestValue('switch')}  ${device.latestValue('level')} "
  } else if (level > 98) {
    //	normally only opens to 99%, so make/fudge it 100%
    //  TODO: put up a recalibrate notice
    level = 100
    result << createEvent(name: "switch", value: "on")
    result << createEvent(name: "windowShade", value: "open")
    log.debug "Reported state is open; device is ${device.latestValue('switch')}  ${device.latestValue('level')} "
  } else {
    result << createEvent(name: "switch", value: "default")
    //	TODO: small problem with this. If we're 'opening' and then call close() this will record that we're 'partially open' instead of 'closing'
    //	as we receive this event about 2 seconds after calling close()
    result << createEvent(name: "windowShade", value: "partially open")
    log.debug "Reported state is neither open or closed; device is ${device.latestValue('switch')}  ${device.latestValue('level')} "
  }
  result << createEvent(name: "level", value: level, unit: "%")

  return result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
  // This will capture any commands not handled by other instances of zwaveEvent
  // and is recommended for development so you can see every command the device sends
  return createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

// ************
// * COMMANDS *
// ************

def refresh() {
  log.trace "refresh()"

  delayBetween([
    zwave.switchMultilevelV1.switchMultilevelGet().format(),
  ], 3000)
}

def calibrate() {
  log.trace "calibrate()"
  zwave.configurationV1.configurationSet(parameterNumber: 78, configurationValue: [1], size: 1).format()
}

def uncalibrate() {
  log.trace "uncalibrate()"
  zwave.configurationV1.configurationSet(parameterNumber: 78, configurationValue: [0], size: 1).format()
}

def pause() {
  zwave.switchMultilevelV3.switchMultilevelStopLevelChange().format()
}

def unpause() {
  //	Before it was paused, what level was it trying to get to.
  def originalDestinationLevel = currentDouble("destinationLevel")
  def currentLevel = currentDouble("level")

  if (originalDestinationLevel != currentLevel) {
    setLevel(originalDestinationLevel)
  }
}

def setCustomLevel1() {
  setLevel(customLevel1 ?: 25)
}

def setCustomLevel2() {
  setLevel(customLevel2 ?: 50)
}

def setCustomLevel3() {
  setLevel(customLevel3 ?: 75)
}

//
//	Commands for capability: Switch
//
def on() {
  open()
}

def off() {
  close()
}

//
//	Commands for capability: Window Shade
//
def open() {
  log.debug "Executing 'open'"
  setLevel(100)
}

def close() {
  log.debug "Executing 'close'"
  setLevel(0)
}

def presetPosition() {
  log.debug "Executing 'presetPosition'"
  def configuredLevel = (slatsLevel ?: 15)
  setLevel(configuredLevel)
}

//
//	Commands for capability: Switch Level
//
def setLevel() {
  log.trace "setLevel()"
  setLevel(50)
}

def setLevel(level) {
  log.trace "setLevel(level) {$level}"

  //  z-wave max level can only be 99
  if(level > 99) level = 99

  def currentLevel = currentDouble("level")
  sendEvent(name: "destinationLevel", value: level, displayed: false)

  if (level != null) {
    if (level > currentLevel) {
      sendEvent(name: "windowShade", value: "opening")
    } else if (level < currentLevel) {
      sendEvent(name: "windowShade", value: "closing")
    }

    if (level >= 98) {
      zwave.switchMultilevelV3.switchMultilevelSet(value: 0xFF, dimmingDuration: 0x00).format()
    } else if (level <= 2) {
      zwave.switchMultilevelV3.switchMultilevelSet(value: 0x00, dimmingDuration: 0x00).format()
    } else {
      zwave.switchMultilevelV3.switchMultilevelSet(value: level, dimmingDuration: 0x00).format()
    }
  }
}

def installed() {
  configure()
}

def updated() {
  configure()
}

def configure() {
  log.debug("configure")

  //  Grab the values from the preferences, so that we can display them in the ui.
  //  preference themselves can't be shown in the ui/tiles :(
  sendEvent(name: "customLevel1Display", value: (customLevel1 ?: 25), displayed: false)
  sendEvent(name: "customLevel2Display", value: (customLevel2 ?: 50), displayed: false)
  sendEvent(name: "customLevel3Display", value: (customLevel3 ?: 75), displayed: false)

  delayBetween([
    //	Turn off energy reporting - by wattage (40) and by time (42), as it's not useful info.
    zwave.configurationV1.configurationSet(parameterNumber: 40, size: 1, scaledConfigurationValue: 0).format(),
    zwave.configurationV1.configurationSet(parameterNumber: 42, size: 2, scaledConfigurationValue: 0).format(),
  ], 3000)
}

private currentDouble(attributeName) {
  if (device.currentValue(attributeName)) {
    return device.currentValue(attributeName).doubleValue()
  } else {
    return 0d
  }
}
