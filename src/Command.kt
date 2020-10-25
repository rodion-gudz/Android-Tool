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
                    component.isEnabled = false
                }
                val components2: Array<Component> = adbPanel.getComponents()
                for (component in components2) {
                    component.isEnabled = true
                }
                val components3: Array<Component> = logsPanel.getComponents()
                for (component in components3) {
                    if (component != buttonStop && component != buttonSave) {
                        component.isEnabled = true
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
        var deviceProps = exec("adb shell getprop", output = true)
        var lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [").substringBefore(']')
        Manufacturer = if (!lineValue1.isBlank()) {
            lineValue1
        } else {
            "Unknown"
        }
        var lineValue2 = deviceProps.substringAfter("ro.product.brand]: [").substringBefore(']')
        Brand = if (!lineValue2.isBlank()) {
            lineValue2
        } else {
            "Unknown"
        }
        var lineValue3 = deviceProps.substringAfter("ro.product.model]: [").substringBefore(']')
        Model = if (!lineValue3.isBlank()) {
            lineValue3
        } else {
            "Unknown"
        }
        var lineValue4 = deviceProps.substringAfter("ro.product.name]: [").substringBefore(']')
        Codename = if (!lineValue4.isBlank()) {
            lineValue4
        } else {
            "Unknown"
        }
        var lineValue5 = deviceProps.substringAfter("ro.product.board]: [").substringBefore(']')
        CPU = if (!lineValue5.isBlank()) {
            lineValue5
        } else {
            "Unknown"
        }
        var lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [").substringBefore(']')
        CPUA = if (!lineValue6.isBlank()) {
            lineValue6
        } else {
            "Unknown"
        }
        var lineValue7 = deviceProps.substringAfter("ro.serialno]: [").substringBefore(']')
        SN = if (!lineValue7.isBlank()) {
            lineValue7
        } else {
            "Unknown"
        }
        var lineValue8 = deviceProps.substringAfter("gsm.operator.alpha]: [").substringBefore(']')
        GsmOperator = if (!lineValue8.isBlank() && lineValue8 != ",") {
            lineValue8
        } else {
            "Unknown"
        }
        var lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [").substringBefore(']')
        Fingerprint = if (!lineValue9.isBlank()) {
            lineValue9
        } else {
            "Unknown"
        }
        var lineValue10 = deviceProps.substringAfter("ro.build.version.release]: [").substringBefore(']')
        VersionRelease = if (!lineValue10.isBlank()) {
            lineValue10
        } else {
            "Unknown"
        }
        var lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [").substringBefore(']')
        SDK = if (!lineValue11.isBlank()) {
            lineValue11
        } else {
            "Unknown"
        }
        var lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [").substringBefore(']')
        SecurityPatch = if (!lineValue12.isBlank()) {
            lineValue12
        } else {
            "Unknown"
        }
        var lineValue13 = deviceProps.substringAfter("ro.product.locale]: [").substringBefore(']')
        Language = if (!lineValue13.isBlank()) {
            lineValue13
        } else {
            "Unknown"
        }
        var lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [").substringBefore(']')
        Selinux = if (!lineValue14.isBlank() && "DEVICE" !in lineValue14) {
            lineValue14
        } else {
            "Unknown"
        }
        var lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [").substringBefore(']')
        Treble = if (!lineValue15.isBlank()) {
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
    fun exec(command: String, output: Boolean = false, streamType: String = "Input"): String {
        try {
            val process = Runtime.getRuntime().exec("$WorkingDir$command")
            if (output) {
                return if (streamType == "Input")
                    process.inputStream.bufferedReader().readText()
                else
                    process.errorStream.bufferedReader().readText()
            }
            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
    fun execa(command: String): List<String> {
        val process = Runtime.getRuntime().exec("$WorkingDir$command")
        return process.inputStream.bufferedReader().readLines()
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
            component.isEnabled = false
        }
        val components2: Array<Component> = adbPanel.getComponents()
        for (component in components2) {
            component.isEnabled = false
        }
        val components3: Array<Component> = logsPanel.getComponents()
        for (component in components3) {
            if(component != buttonStop && component != buttonSave) {
                component.isEnabled = false
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