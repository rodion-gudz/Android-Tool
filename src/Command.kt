import java.awt.Component
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

open class Command : AndroidToolUI() {
fun connectionCheck(){
    if (!CommandRunning) {
        val builderListGetState = Runtime.getRuntime().exec("adb get-state")
        GetStateOutput = BufferedReader(InputStreamReader(builderListGetState.inputStream)).readText()
        GetStateErrorOutput = BufferedReader(InputStreamReader(builderListGetState.errorStream)).readText()
        AdbDevicesOutput = BufferedReader(InputStreamReader(Runtime.getRuntime().exec("adb devices").inputStream)).readText()
        FastbootDevicesOutput = BufferedReader(InputStreamReader(Runtime.getRuntime().exec("fastboot devices").inputStream)).readText()
    }

    ConnectedViaFastboot = "fastboot" in FastbootDevicesOutput
    ConnectedViaAdb = "device" in GetStateOutput
    ConnectedAdbUsb = "192.168" !in AdbDevicesOutput
    ConnectedAdbWifi = "offline" !in GetStateOutput
    UnauthorizedDevice = "unauthorized" in AdbDevicesOutput
    MultipleDevicesConnected = "error: more than one device/emulator" in GetStateErrorOutput


    if (MultipleDevicesConnected) {
        dialogUnauthorizedDevice.dispose()
        if (!dialogMultipleDevice.isVisible) {
            labelManufacturerValue.text = "-"
            labelBrandValue.text = "-"
            labelModelValue.text = "-"
            labelCodenameValue.text = "-"
            labelCPUValue.text = "-"
            labelCPUAValue.text = "-"
            labelSNValue.text = "-"
            labelGsmOperatorValue.text = "-"
            labelFingerprintValue.text = "-"
            labelVersionReleaseValue.text = "-"
            labelSDKValue.text = "-"
            labelSecurityPatchValue.text = "-"
            labelLanguageValue.text = "-"
            labelSelinuxValue.text = "-"
            labelTrebleValue.text = "-"
            labelUnlockValue.text = "-"
            labelFastbootCodenameValue.text = "-"
            labelFastbootSNValue.text = "-"
            labelSystemFSValue.text = "-"
            labelSystemCapacityValue.text = "-"
            labelDataFSValue.text = "-"
            labelDataCapacityValue.text = "-"
            labelBootFSValue.text = "-"
            labelBootCapacityValue.text = "-"
            labelRecoveryFSValue.text = "-"
            labelRecoveryCapacityValue.text = "-"
            labelCacheFSValue.text = "-"
            labelCacheCapacityValue.text = "-"
            labelVendorFSValue.text = "-"
            labelVendorCapacityValue.text = "-"
            labelAllCapacityValue.text = "-"
            labelUSBConnection.text = "Not connected"
            labelUSBConnection.icon = iconNo
            labelTCPConnection.text = "Not connected"
            labelTCPConnection.icon = iconNo
            listModelLogs.removeAllElements()
            frame.isEnabled = false
            dialogMultipleDevice.isVisible = true
        }
    } else if (UnauthorizedDevice) {
        if (!dialogUnauthorizedDevice.isVisible) {
            frame.isEnabled = false
            dialogUnauthorizedDevice.isVisible = true
        }
    }

    when {
        ConnectedViaAdb -> {
            if (FirstAdbConnection) {
                tabbedpane.selectedIndex = 0
                FirstAdbConnection = false
            }
            frame.isEnabled = true
            dialogUnauthorizedDevice.dispose()
            if (enabledAll) {
                val components: Array<Component> = fastbootPanel.getComponents()
                for (component in components) {
                    component.setEnabled(false)
                }
                val components2: Array<Component> = adbPanel.getComponents()
                for (component in components2) {
                    component.setEnabled(true)
                }
                val components3: Array<Component> = logsPanel.getComponents()
                for (component in components3) {
                    if (component != buttonStop && component != buttonSave) {
                        component.setEnabled(true)
                    }
                }
            }
            textAreaCommandFastbootOutput.isFocusable = false
            textAreaCommandOutput.isFocusable = true
            textAreaCommandInput.isFocusable = true
            textAreaCommandFastbootInput.isFocusable = false
            listLogs.isFocusable = true
            list.isFocusable = true
            if (tabbedpane.selectedIndex == 2) {
                buttonPowerOff.isEnabled = false
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
            } else if (tabbedpane.selectedIndex == 3) {
                buttonReboot.isEnabled = false
                buttonRecoveryReboot.isEnabled = false
                buttonFastbootReboot.isEnabled = false
                buttonPowerOff.isEnabled = false
            } else {
                buttonPowerOff.isEnabled = true
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
            }
            if (newPhone) {
                getprop()
            }
            if (ConnectedAdbUsb) {
                labelUSBConnection.text = "Connected via Adb"
                labelUSBConnection.icon = iconYes
                buttonIpConnect.isEnabled = false
                labelIP.isEnabled = false
                textFieldIP.isEnabled = false
            } else {
                if (ConnectedAdbWifi) {
                    labelTCPConnection.text = "Connected to ${AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')}"
                    labelTCPConnection.icon = iconYes
                    labelConnect.text = ""
                }
            }
            newPhone = false
            enabledAll = false
        }
        ConnectedViaFastboot -> {
            if (FirstFastbootConnection) {
                tabbedpane.selectedIndex = 2
                FirstFastbootConnection = false
            }
            if (enabledAll) {
                val components: Array<Component> = fastbootPanel.getComponents()
                for (component in components) {
                    component.setEnabled(true)
                }
                val components2: Array<Component> = adbPanel.getComponents()
                for (component in components2) {
                    component.setEnabled(false)
                }
                val components3: Array<Component> = logsPanel.getComponents()
                for (component in components3) {
                    component.setEnabled(false)
                }
            }
            textAreaCommandFastbootOutput.isFocusable = true
            textAreaCommandOutput.isFocusable = false
            textAreaCommandInput.isFocusable = false
            textAreaCommandFastbootInput.isFocusable = true
            listLogs.isFocusable = false
            list.isFocusable = false
            if (tabbedpane.selectedIndex == 2) {
                buttonPowerOff.isEnabled = false
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
            } else if (tabbedpane.selectedIndex == 3) {
                buttonReboot.isEnabled = false
                buttonRecoveryReboot.isEnabled = false
                buttonFastbootReboot.isEnabled = false
                buttonPowerOff.isEnabled = false
            } else {
                buttonPowerOff.isEnabled = true
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
            }
            if (newPhone) {
                getPropFastboot()
                labelUSBConnection.text = "Connected via Fastboot"
                labelUSBConnection.icon = iconYes
            }
            newPhone = false
            enabledAll = false
        }
        else -> {
            FirstFastbootConnection = true
            FirstAdbConnection = true
            enabledAll = true
            newPhone = true
            if (!UnauthorizedDevice) {
                frame.isEnabled = true
                dialogUnauthorizedDevice.dispose()
            }
            noConnection()
        }
    }
}
    private fun getprop() {
        val builderList = Runtime.getRuntime().exec("adb shell getprop ro.product.manufacturer")
        val input = builderList.inputStream
        val reader = BufferedReader(InputStreamReader(input))
        var lineValue1 = reader.readLine()
        Manufacturer = if (lineValue1 != "") {
            lineValue1
        } else {
            "Unknown"
        }
        val builderList2 = Runtime.getRuntime().exec("adb shell getprop ro.product.brand")
        val input2 = builderList2.inputStream
        val reader2 = BufferedReader(InputStreamReader(input2))
        var lineValue2 = reader2.readLine()
        Brand = if (lineValue2 != "") {
            lineValue2
        } else {
            "Unknown"
        }
        val builderList3 = Runtime.getRuntime().exec("adb shell getprop ro.product.model")
        val input3 = builderList3.inputStream
        val reader3 = BufferedReader(InputStreamReader(input3))
        var lineValue3 = reader3.readLine()
        Model = if (lineValue3 != "") {
            lineValue3
        } else {
            "Unknown"
        }
        val builderList4 = Runtime.getRuntime().exec("adb shell getprop ro.product.name")
        val input4 = builderList4.inputStream
        val reader4 = BufferedReader(InputStreamReader(input4))
        var lineValue4 = reader4.readLine()
        Codename = if (lineValue4 != "") {
            lineValue4
        } else {
            "Unknown"
        }
        val builderList5 = Runtime.getRuntime().exec("adb shell getprop ro.product.board")
        val input5 = builderList5.inputStream
        val reader5 = BufferedReader(InputStreamReader(input5))
        var lineValue5 = reader5.readLine()
        CPU = if (lineValue5 != "") {
            lineValue5
        } else {
            "Unknown"
        }
        val builderList6 = Runtime.getRuntime().exec("adb shell getprop ro.product.cpu.abi")
        val input6 = builderList6.inputStream
        val reader6 = BufferedReader(InputStreamReader(input6))
        var lineValue6 = reader6.readLine()
        CPUA = if (lineValue6 != "") {
            lineValue6
        } else {
            "Unknown"
        }
        val builderList7 = Runtime.getRuntime().exec("adb shell getprop ro.serialno")
        val input7 = builderList7.inputStream
        val reader7 = BufferedReader(InputStreamReader(input7))
        var lineValue7 = reader7.readLine()
        SN = if (lineValue7 != "") {
            lineValue7
        } else {
            "Unknown"
        }
        val builderList8 = Runtime.getRuntime().exec("adb shell getprop gsm.operator.alpha")
        val input8 = builderList8.inputStream
        val reader8 = BufferedReader(InputStreamReader(input8))
        var lineValue8 = reader8.readLine()
        GsmOperator = if (lineValue8 != "" && lineValue8 != ",") {
            lineValue8
        } else {
            "Unknown"
        }
        val builderList9 = Runtime.getRuntime().exec("adb shell getprop ro.build.fingerprint")
        val input9 = builderList9.inputStream
        val reader9 = BufferedReader(InputStreamReader(input9))
        var lineValue9 = reader9.readLine()
        Fingerprint = if (lineValue9 != "") {
            lineValue9
        } else {
            "Unknown"
        }
        val builderList10 = Runtime.getRuntime().exec("adb shell getprop ro.build.version.release")
        val input10 = builderList10.inputStream
        val reader10 = BufferedReader(InputStreamReader(input10))
        var lineValue10 = reader10.readLine()
        VersionRelease = if (lineValue10 != "") {
            lineValue10
        } else {
            "Unknown"
        }
        val builderList11 = Runtime.getRuntime().exec("adb shell getprop ro.build.version.sdk")
        val input11 = builderList11.inputStream
        val reader11 = BufferedReader(InputStreamReader(input11))
        var lineValue11 = reader11.readLine()
        SDK = if (lineValue11 != "") {
            lineValue11
        } else {
            "Unknown"
        }
        val builderList12 = Runtime.getRuntime().exec("adb shell getprop ro.build.version.security_patch")
        val input12 = builderList12.inputStream
        val reader12 = BufferedReader(InputStreamReader(input12))
        var lineValue12 = reader12.readLine()
        SecurityPatch = if (lineValue12 != "") {
            lineValue12
        } else {
            "Unknown"
        }
        val builderList13 = Runtime.getRuntime().exec("adb shell getprop ro.product.locale")
        val input13 = builderList13.inputStream
        val reader13 = BufferedReader(InputStreamReader(input13))
        var lineValue13 = reader13.readLine()
        Language = if (lineValue13 != "") {
            lineValue13
        } else {
            "Unknown"
        }
        val builderList14 = Runtime.getRuntime().exec("adb shell getprop ro.boot.selinux")
        val input14 = builderList14.inputStream
        val reader14 = BufferedReader(InputStreamReader(input14))
        var lineValue14 = reader14.readLine()
        Selinux = if (lineValue14 != "") {
            lineValue14
        } else {
            "Unknown"
        }
        val builderList15 = Runtime.getRuntime().exec("adb shell getprop ro.treble.enabled")
        val input15 = builderList15.inputStream
        val reader15 = BufferedReader(InputStreamReader(input15))
        var lineValue15 = reader15.readLine()
        Treble = if (lineValue15 != "") {
            lineValue15
        } else {
            "Unknown"
        }
        labelManufacturerValue.text = Manufacturer
        labelBrandValue.text = Brand
        labelModelValue.text = Model
        labelCodenameValue.text = Codename
        labelCPUValue.text = CPU
        labelCPUAValue.text = CPUA
        labelSNValue.text = SN
        labelGsmOperatorValue.text = GsmOperator
        labelFingerprintValue.text = Fingerprint
        labelVersionReleaseValue.text = VersionRelease
        labelSDKValue.text = SDK
        labelSecurityPatchValue.text = SecurityPatch
        labelLanguageValue.text = Language
        labelSelinuxValue.text = Selinux
        labelTrebleValue.text = Treble
    }

    private fun getPropFastboot() {
        Unlock = exece("fastboot getvar unlocked", stdIn = "yes").toString().substringAfter(":")
        FastbootCodename = exece("fastboot getvar product", stdIn = "yes").toString().substringAfter(":")
        FastbootSN = exece("fastboot getvar serialno", stdIn = "yes").toString().substringAfter(":")
        SystemFS = exece("fastboot getvar partition-type:system", stdIn = "yes").toString().substringAfter(":").substringAfter(":")
        val SystemDec = exece("fastboot getvar partition-size:system", stdIn = "yes").toString().substringAfter(":").substringAfter(":").substringAfter("x")
        SystemCapacity = (java.lang.Long.parseLong(SystemDec, 16) / 1048576).toString()
        DataFS = exece("fastboot getvar partition-type:userdata", stdIn = "yes").toString().substringAfter(":").substringAfter(":")
        val DataDec = exece("fastboot getvar partition-size:userdata", stdIn = "yes").toString().substringAfter(":").substringAfter(":").substringAfter("x")
        DataCapacity = (java.lang.Long.parseLong(DataDec, 16) / 1048576).toString()
        BootFS = exece("fastboot getvar partition-type:boot", stdIn = "yes").toString().substringAfter(":").substringAfter(":")
        val BootDec = exece("fastboot getvar partition-size:boot", stdIn = "yes").toString().substringAfter(":").substringAfter(":").substringAfter("x")
        BootCapacity = (java.lang.Long.parseLong(BootDec, 16) / 1048576).toString()
        RecoveryFS = exece("fastboot getvar partition-type:recovery", stdIn = "yes").toString().substringAfter(":").substringAfter(":")
        val RecoveryDec = exece("fastboot getvar partition-size:recovery", stdIn = "yes").toString().substringAfter(":").substringAfter(":").substringAfter("x")
        RecoveryCapacity = (java.lang.Long.parseLong(RecoveryDec, 16) / 1048576).toString()
        CacheFS = exece("fastboot getvar partition-type:cache", stdIn = "yes").toString().substringAfter(":").substringAfter(":")
        val CacheDec = exece("fastboot getvar partition-size:cache", stdIn = "yes").toString().substringAfter(":").substringAfter(":").substringAfter("x")
        CacheCapacity = (java.lang.Long.parseLong(CacheDec, 16) / 1048576).toString()
        VendorFS = exece("fastboot getvar partition-type:vendor", stdIn = "yes").toString().substringAfter(":").substringAfter(":")
        val VendorDec = exece("fastboot getvar partition-size:vendor", stdIn = "yes").toString().substringAfter(":").substringAfter(":").substringAfter("x")
        VendorCapacity = (java.lang.Long.parseLong(VendorDec, 16) / 1048576).toString()
        AllCapacity = (SystemCapacity.toInt() + DataCapacity.toInt() + BootCapacity.toInt() + RecoveryCapacity.toInt() + CacheCapacity.toInt() + VendorCapacity.toInt()).toString()
        labelUnlockValue.text = if (Unlock != "< waiting for any device >") {
            Unlock
        } else {
            "-"
        }
        labelFastbootCodenameValue.text = if (FastbootCodename != "< waiting for any device >") {
            FastbootCodename
        } else {
            "-"
        }
        labelFastbootSNValue.text = if (FastbootSN != "< waiting for any device >") {
            FastbootSN
        } else {
            "-"
        }
        labelSystemFSValue.text = if (SystemFS != "< waiting for any device >") {
            SystemFS
        } else {
            "-"
        }
        labelSystemCapacityValue.text = if (SystemCapacity != "< waiting for any device >") {
            SystemCapacity
        } else {
            "-"
        }
        labelDataFSValue.text = if (DataFS != "< waiting for any device >") {
            DataFS
        } else {
            "-"
        }
        labelDataCapacityValue.text = if (DataCapacity != "< waiting for any device >") {
            DataCapacity
        } else {
            "-"
        }
        labelBootFSValue.text = if (BootFS != "< waiting for any device >") {
            BootFS
        } else {
            "-"
        }
        labelBootCapacityValue.text = if (BootCapacity != "< waiting for any device >") {
            BootCapacity
        } else {
            "-"
        }
        labelRecoveryFSValue.text = if (RecoveryFS != "< waiting for any device >") {
            RecoveryFS
        } else {
            "-"
        }
        labelRecoveryCapacityValue.text = if (RecoveryCapacity != "< waiting for any device >") {
            RecoveryCapacity
        } else {
            "-"
        }
        labelCacheFSValue.text = if (CacheFS != "< waiting for any device >") {
            CacheFS
        } else {
            "-"
        }
        labelCacheCapacityValue.text = if (CacheCapacity != "< waiting for any device >") {
            CacheCapacity
        } else {
            "-"
        }
        labelVendorFSValue.text = if (VendorFS != "< waiting for any device >") {
            VendorFS
        } else {
            "-"
        }
        labelVendorCapacityValue.text = if (VendorCapacity != "< waiting for any device >") {
            VendorCapacity
        } else {
            "-"
        }
        labelAllCapacityValue.text = if (AllCapacity != "< waiting for any device >") {
            AllCapacity
        } else {
            "-"
        }
    }



    fun exece(cmd: String, stdIn: String = "", captureOutput: Boolean = true, workingDir: File = File(".")): String? {
        try {
            val process = ProcessBuilder(*cmd.split("\\s".toRegex()).toTypedArray())
                    .directory(workingDir)
                    .start().apply {
                        if (stdIn != "") {
                            outputStream.bufferedWriter().apply {
                                write(stdIn)
                                flush()
                                close()
                            }
                        }
                        waitFor()
                    }
            if (captureOutput) {
                return process.errorStream.bufferedReader().readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun noConnection() {
        val components: Array<Component> = fastbootPanel.getComponents()
        for (component in components) {
            component.setEnabled(false)
        }
        val components2: Array<Component> = adbPanel.getComponents()
        for (component in components2) {
            component.setEnabled(false)
        }
        val components3: Array<Component> = logsPanel.getComponents()
        for (component in components3) {
            if(component != buttonStop && component != buttonSave) {
                component.setEnabled(false)
            }
        }
        buttonReboot.isEnabled = false
        buttonRecoveryReboot.isEnabled = false
        buttonFastbootReboot.isEnabled = false
        buttonPowerOff.isEnabled = false
        textAreaCommandFastbootOutput.isFocusable = false
        textAreaCommandOutput.isFocusable = false
        textAreaCommandInput.isFocusable = false
        textAreaCommandFastbootInput.isFocusable = false
        listLogs.isFocusable = false
        list.isFocusable = false
        listModel.removeAllElements()
        listModelLogs.removeAllElements()
        labelManufacturerValue.text = "-"
        labelBrandValue.text = "-"
        labelModelValue.text = "-"
        labelCodenameValue.text = "-"
        labelCPUValue.text = "-"
        labelCPUAValue.text = "-"
        labelSNValue.text = "-"
        labelGsmOperatorValue.text = "-"
        labelFingerprintValue.text = "-"
        labelVersionReleaseValue.text = "-"
        labelSDKValue.text = "-"
        labelSecurityPatchValue.text = "-"
        labelLanguageValue.text = "-"
        labelSelinuxValue.text = "-"
        labelTrebleValue.text = "-"
        labelUnlockValue.text = "-"
        labelFastbootCodenameValue.text = "-"
        labelFastbootSNValue.text = "-"
        labelSystemFSValue.text = "-"
        labelSystemCapacityValue.text = "-"
        labelDataFSValue.text = "-"
        labelDataCapacityValue.text = "-"
        labelBootFSValue.text = "-"
        labelBootCapacityValue.text = "-"
        labelRecoveryFSValue.text = "-"
        labelRecoveryCapacityValue.text = "-"
        labelCacheFSValue.text = "-"
        labelCacheCapacityValue.text = "-"
        labelVendorFSValue.text = "-"
        labelVendorCapacityValue.text = "-"
        labelAllCapacityValue.text = "-"
        textFieldIPa.text = ""
        textFieldIPa.isEnabled = false
        buttonIpConnect.isEnabled = true
        labelIP.isEnabled = true
        textFieldIP.isEnabled = true
        labelUSBConnection.text = "Not connected"
        labelUSBConnection.icon = iconNo
    }
}