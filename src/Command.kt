import java.awt.Component
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.util.zip.ZipFile
import org.apache.maven.artifact.versioning.ComparableVersion
import java.util.jar.JarFile
import java.net.URISyntaxException
import java.util.*
import javax.swing.DefaultListModel


open class Command : AndroidToolUI() {
    fun createFolder() {
        when {
            Windows -> File("$userFolder\\.android_tool", "SDK-Tools").mkdirs()
            Linux -> File("$userFolder/.android_tool", "SDK-Tools").mkdirs()
            else -> File("$userFolder/.android_tool", "SDK-Tools").mkdirs()
        }
    }

    fun versionCheck() {
        try {
            val properties = Properties()
            val inputStream =
                URL("https://raw.githubusercontent.com/fast-geek/Android-Tool/master/values.properties").openStream()
            properties.load(inputStream)
            if (ComparableVersion(properties.getProperty("latestVersion")) > ComparableVersion(programVersion)) {
                programVersionLatest = properties.getProperty("latestVersion")
                labelUpdateVersion.text =
                    "<html><font size='4'><b>Current version:</b> $programVersion <br> <b>Latest:</b> $programVersionLatest</font></html>"
                dialogUpdate.title = "Version $programVersionLatest available!"
                dialogUpdate.isVisible = true
            }
        } catch (e: Exception){}
    }

    fun searchFilter(searchTerm: String) {
        val filteredItems: DefaultListModel<Any?> = DefaultListModel()
        val apps = apps
        apps.stream().forEach { app: Any ->
            val starName = app.toString().toLowerCase()
            if (starName.contains(searchTerm.toLowerCase())) {
                if (!filteredItems.contains(app)) {
                    filteredItems.addElement(app)
                }
            }
        }
        listModel = filteredItems
        list.model = listModel
    }

    fun sdkCheck() {
        when {
            Windows -> {
                if (File("$userFolder\\.android_tool\\SDK-Tools\\adb.exe").exists() && File("$userFolder\\.android_tool\\SDK-Tools\\fastboot.exe").exists()) {
                    SdkDir = "$userFolder\\.android_tool\\SDK-Tools\\"
                    return
                }
            }
            Linux -> {
                if (File("$userFolder/.android_tool/SDK-Tools/adb").exists() && File("$userFolder/.android_tool/SDK-Tools/fastboot").exists()) {
                    SdkDir = "$userFolder/.android_tool/SDK-Tools/"
                    return
                }
            }
            MacOS -> {
                if (File("$userFolder/.android_tool/SDK-Tools/adb").exists() && File("$userFolder/.android_tool/SDK-Tools/fastboot").exists()) {
                    SdkDir = "$userFolder/.android_tool/SDK-Tools/"
                    return
                }
            }
        }
        when {
            Windows -> {
                if (File("adb.exe").exists() && File("fastboot.exe").exists() && File("AdbWinApi.dll").exists() && File("AdbWinUsbApi.dll").exists()) {
                    SdkDir = "$JarDir\\"
                    return
                } else if (File("$JarDir\\SDK-Tools\\adb.exe").exists() && File("$JarDir\\SDK-Tools\\fastboot.exe").exists() && File("$JarDir\\SDK-Tools\\AdbWinApi.dll").exists() && File("$JarDir\\SDK-Tools\\AdbWinUsbApi.dll").exists()) {
                    SdkDir = "$JarDir\\SDK-Tools\\"
                    return
                }
            }
            Linux -> {
                if (File("adb").exists() && File("fastboot").exists()) {
                    SdkDir = "$JarDir/"
                    return
                } else if (File("$JarDir/SDK-Tools/adb").exists() && File("$JarDir/SDK-Tools/fastboot").exists()) {
                    SdkDir = "$JarDir/SDK-Tools/"
                    return
                }
            }
            MacOS -> {
                if (File("adb").exists() && File("fastboot").exists()) {
                    SdkDir = "$JarDir/"
                    return
                } else if (File("$JarDir/SDK-Tools/adb").exists() && File("$JarDir/SDK-Tools/fastboot").exists()) {
                    SdkDir = "$JarDir/SDK-Tools/"
                    return
                }
            }
        }
        dialogSdkDownload.isVisible = true
        return
    }

    fun downloadFile(urlStr: String, file: String) {
        val url = URL(urlStr)
        val bis = BufferedInputStream(url.openStream())
        val fis = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var count = 0
        while (bis.read(buffer, 0, 1024).also { count = it } != -1) {
            fis.write(buffer, 0, count)
        }
        fis.close()
        bis.close()
    }

    fun unZipFile(urlStr: String) {
        ZipFile(File(urlStr)).use { zip ->
            zip.stream().forEach { entry ->
                if (entry.isDirectory)
                    File(SdkDir, entry.name).mkdirs()
                else zip.getInputStream(entry).use { input ->
                    File(SdkDir, entry.name).apply {
                        outputStream().use { output ->
                            input.copyTo(output)
                        }
                        setExecutable(true, false)
                    }
                }
            }
        }
        File(ProgramDir + if (Windows) "\\SDK-Tools\\Windows.zip" else if (Linux) "/SDK-Tools/Linux.zip" else "/SDK-Tools/MacOS.zip").delete()
    }
    fun connectionCheck() {
        if (!CommandRunning) {
            GetStateOutput = exec("adb", "get-state", output = true)
            GetStateErrorOutput = exec("adb", "get-state", streamType = "Error", output = true)
            AdbDevicesOutput = exec("adb", "devices", output = true)
            FastbootDevicesOutput = exec("fastboot", "devices", output = true)
        }

        ConnectedViaFastboot = "fastboot" in FastbootDevicesOutput
        ConnectedViaRecovery = "recovery" in GetStateOutput
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
            if (!dialogUnauthorizedDevice.isVisible)
                dialogUnauthorizedDevice.isVisible = true
        }

        when {
            ConnectedViaAdb -> {
                if (tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1) {
                    buttonPowerOff.isEnabled = true
                    buttonReboot.isEnabled = true
                    buttonRecoveryReboot.isEnabled = true
                    buttonFastbootReboot.isEnabled = true
                }

                if (FirstAdbConnection) {
                    tabbedpane.selectedIndex = 0
                    FirstAdbConnection = false
                }
                dialogUnauthorizedDevice.dispose()
                if (enabledAll) {
                    val components: Array<Component> = fastbootPanel.components
                    for (component in components) {
                        component.isEnabled = false
                    }
                    val components4: Array<Component> = recoveryPanel.components
                    for (component in components4) {
                        component.isEnabled = false
                    }
                    val components2: Array<Component> = adbPanel.components
                    for (component in components2) {
                        component.isEnabled = true
                    }
                    val components3: Array<Component> = logsPanel.components
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

                if (newPhone) {
                    getProp()
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
                if (tabbedpane.selectedIndex == 2) {
                    buttonPowerOff.isEnabled = false
                    buttonReboot.isEnabled = true
                    buttonRecoveryReboot.isEnabled = true
                    buttonFastbootReboot.isEnabled = true
                }
                if (FirstFastbootConnection) {
                    tabbedpane.selectedIndex = 2
                    FirstFastbootConnection = false
                }
                buttonIpConnect.isEnabled = false
                if (enabledAll) {
                    val components: Array<Component> = fastbootPanel.components
                    for (component in components) {
                        component.isEnabled = true
                    }
                    val components2: Array<Component> = adbPanel.components
                    for (component in components2) {
                        component.isEnabled = false
                    }
                    val components4: Array<Component> = recoveryPanel.components
                    for (component in components4) {
                        component.isEnabled = false
                    }
                    val components3: Array<Component> = logsPanel.components
                    for (component in components3) {
                        component.isEnabled = false
                    }
                }
                textAreaCommandFastbootOutput.isFocusable = true
                textAreaCommandOutput.isFocusable = false
                textAreaCommandInput.isFocusable = false
                textAreaCommandFastbootInput.isFocusable = true
                listLogs.isFocusable = false
                list.isFocusable = false

                if (newPhone) {
                    getPropFastboot()
                    labelUSBConnection.text = "Connected via Fastboot"
                    labelUSBConnection.icon = iconYes
                }
                newPhone = false
                enabledAll = false
            }
            ConnectedViaRecovery -> {
                if (tabbedpane.selectedIndex == 3) {
                    buttonReboot.isEnabled = true
                    buttonRecoveryReboot.isEnabled = true
                    buttonFastbootReboot.isEnabled = true
                    buttonPowerOff.isEnabled = true
                }
                if (FirstRecoveryConnection) {
                    tabbedpane.selectedIndex = 3
                    FirstRecoveryConnection = false
                }
                buttonIpConnect.isEnabled = false
                frame.isEnabled = true
                dialogUnauthorizedDevice.dispose()
                if (enabledAll) {
                    val components: Array<Component> = fastbootPanel.components
                    for (component in components) {
                        component.isEnabled = false
                    }
                    val components2: Array<Component> = adbPanel.components
                    for (component in components2) {
                        component.isEnabled = false
                    }
                    val components4: Array<Component> = recoveryPanel.components
                    for (component in components4) {
                        component.isEnabled = true
                    }
                    val components3: Array<Component> = logsPanel.components
                    for (component in components3) {
                        if (component != buttonStop && component != buttonSave) {
                            component.isEnabled = false
                        }
                    }
                }
                textAreaCommandFastbootOutput.isFocusable = false
                textAreaCommandOutput.isFocusable = false
                textAreaCommandInput.isFocusable = false
                textAreaCommandFastbootInput.isFocusable = false
                listLogs.isFocusable = false
                list.isFocusable = false

                if (newPhone) {
                    getPropRecovery()
                    labelUSBConnection.text = "Connected via Adb"
                    labelUSBConnection.icon = iconYes
                }
                newPhone = false
                enabledAll = false
            }
            else -> {
                buttonIpConnect.isEnabled = true
                FirstFastbootConnection = true
                FirstAdbConnection = true
                FirstRecoveryConnection = true
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

    private fun getProp() {
        val deviceProps = exec("adb", "shell getprop", output = true)
        val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [").substringBefore(']')
        Manufacturer = if (lineValue1.isNotBlank()) {
            lineValue1
        } else {
            "-"
        }
        val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [").substringBefore(']')
        Brand = if (lineValue2.isNotBlank()) {
            lineValue2
        } else {
            "-"
        }
        val lineValue3 = deviceProps.substringAfter("ro.product.model]: [").substringBefore(']')
        Model = if (lineValue3.isNotBlank()) {
            lineValue3
        } else {
            "-"
        }
        val lineValue4 = deviceProps.substringAfter("ro.product.name]: [").substringBefore(']')
        Codename = if (lineValue4.isNotBlank()) {
            lineValue4
        } else {
            "-"
        }
        val lineValue5 = deviceProps.substringAfter("ro.product.board]: [").substringBefore(']')
        CPU = if (lineValue5.isNotBlank()) {
            lineValue5
        } else {
            "-"
        }
        val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [").substringBefore(']')
        CPUArch = if (lineValue6.isNotBlank()) {
            lineValue6
        } else {
            "-"
        }
        val lineValue7 = deviceProps.substringAfter("ro.serialno]: [").substringBefore(']')
        SN = if (lineValue7.isNotBlank()) {
            lineValue7
        } else {
            "-"
        }
        val lineValue8 = deviceProps.substringAfter("gsm.operator.alpha]: [").substringBefore(']')
        GsmOperator = if (lineValue8.isNotBlank() && lineValue8 != ",") {
            lineValue8
        } else {
            "-"
        }
        val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [").substringBefore(']')
        Fingerprint = if (lineValue9.isNotBlank()) {
            lineValue9
        } else {
            "-"
        }
        val lineValue10 = deviceProps.substringAfter("ro.build.version.release]: [").substringBefore(']')
        VersionRelease = if (lineValue10.isNotBlank()) {
            lineValue10
        } else {
            "-"
        }
        val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [").substringBefore(']')
        SDK = if (lineValue11.isNotBlank()) {
            lineValue11
        } else {
            "-"
        }
        val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [").substringBefore(']')
        SecurityPatch = if (lineValue12.isNotBlank()) {
            lineValue12
        } else {
            "-"
        }
        val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [").substringBefore(']')
        Language = if (lineValue13.isNotBlank()) {
            lineValue13
        } else {
            "-"
        }
        val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [").substringBefore(']')
        Selinux = if (lineValue14.isNotBlank() && "DEVICE" !in lineValue14) {
            lineValue14
        } else {
            "-"
        }
        val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [").substringBefore(']')
        Treble = if (lineValue15.isNotBlank()) {
            lineValue15
        } else {
            "-"
        }
        labelManufacturerValue.text = Manufacturer
        labelBrandValue.text = Brand
        labelModelValue.text = Model
        labelCodenameValue.text = Codename
        labelCPUValue.text = CPU
        labelCPUAValue.text = CPUArch
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

    private fun getPropRecovery() {
        val deviceProps = exec("adb", "shell getprop", output = true)
        val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [").substringBefore(']')
        Manufacturer = if (lineValue1.isNotBlank()) {
            lineValue1
        } else {
            "Unknown"
        }
        val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [").substringBefore(']')
        Brand = if (lineValue2.isNotBlank()) {
            lineValue2
        } else {
            "-"
        }
        val lineValue3 = deviceProps.substringAfter("ro.product.model]: [").substringBefore(']')
        Model = if (lineValue3.isNotBlank()) {
            lineValue3
        } else {
            "-"
        }
        val lineValue4 = deviceProps.substringAfter("ro.product.name]: [").substringBefore(']')
        Codename = if (lineValue4.isNotBlank()) {
            lineValue4
        } else {
            "-"
        }
        val lineValue5 = deviceProps.substringAfter("ro.boot.hardware]: [").substringBefore(']')
        CPU = if (lineValue5.isNotBlank()) {
            lineValue5
        } else {
            "-"
        }
        val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [").substringBefore(']')
        CPUArch = if (lineValue6.isNotBlank()) {
            lineValue6
        } else {
            "-"
        }
        val lineValue7 = deviceProps.substringAfter("ro.serialno]: [").substringBefore(']')
        SN = if (lineValue7.isNotBlank()) {
            lineValue7
        } else {
            "-"
        }
        val lineValue8 = deviceProps.substringAfter("sys.usb.state]: [").substringBefore(']')
        GsmOperator = if (lineValue8.isNotBlank()) {
            lineValue8
        } else {
            "-"
        }
        val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [").substringBefore(']')
        Fingerprint = if (lineValue9.isNotBlank()) {
            lineValue9
        } else {
            "-"
        }
        var lineValue10 = deviceProps.substringAfter("ro.orangefox.version]: [").substringBefore(']')
        if (lineValue10.isNotBlank()) {
            VersionRelease = lineValue10
        } else {
            lineValue10 = deviceProps.substringAfter("ro.twrp.version]: [").substringBefore(']')
            VersionRelease = if (lineValue10.isNotBlank()) {
                lineValue10
            } else {
                "-"
            }
        }
        val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [").substringBefore(']')
        SDK = if (lineValue11.isNotBlank()) {
            lineValue11
        } else {
            "-"
        }
        val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [").substringBefore(']')
        SecurityPatch = if (lineValue12.isNotBlank()) {
            lineValue12
        } else {
            "-"
        }
        val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [").substringBefore(']')
        Language = if (lineValue13.isNotBlank()) {
            lineValue13
        } else {
            "-"
        }
        val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [").substringBefore(']')
        Selinux = if (lineValue14.isNotBlank() && "DEVICE" !in lineValue14) {
            lineValue14
        } else {
            "-"
        }
        val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [").substringBefore(']')
        Treble = if (lineValue15.isNotBlank()) {
            lineValue15
        } else {
            "-"
        }
        val lineValue17 = deviceProps.substringAfter("ro.boot.secureboot]: [").substringBefore(']')
        SecureBoot = if (lineValue17.isNotBlank()) {
            if (lineValue17 == "1") {
                "true"
            } else {
                "false"
            }
        } else {
            "-"
        }
        val lineValue18 = deviceProps.substringAfter("ro.build.host]: [").substringBefore(']')
        DeviceHost = if (lineValue18.isNotBlank() && "DEVICE" !in lineValue14) {
            lineValue18
        } else {
            "-"
        }
        val lineValue16 = deviceProps.substringAfter("ro.allow.mock.location]: [").substringBefore(']')
        MockLocation = if (lineValue16.isNotBlank()) {
            if (lineValue16 == "1") {
                "true"
            } else {
                "false"
            }
        } else {
            "-"
        }
        val lineValue19 = deviceProps.substringAfter("ro.build.id]: [").substringBefore(']')
        Language = if (lineValue19.isNotBlank()) {
            lineValue19
        } else {
            "-"
        }
        labelManufacturerValue.text = Manufacturer
        labelBrandValue.text = Brand
        labelModelValue.text = Model
        labelCodenameValue.text = Codename
        labelCPUValue.text = CPU
        labelCPUAValue.text = CPUArch
        labelSNValue.text = SN
        labelGsmOperatorValue.text = GsmOperator
        labelFingerprintValue.text = Fingerprint
        labelVersionReleaseValue.text = VersionRelease
        labelSDKValue.text = SDK
        labelSecurityPatchValue.text = SecurityPatch
        labelLanguageValue.text = Language
        labelSelinuxValue.text = Selinux
        labelTrebleValue.text = Treble
        labelDeviceHostnameValue.text = DeviceHost
        labelSecureBootValue.text = SecureBoot
        labelLocationsValue.text = MockLocation
    }

    private fun getPropFastboot() {
        val fastbootProps = exec("fastboot", "getvar all", output = true, streamType = "Error")
        Unlock = fastbootProps.substringAfter("(bootloader) unlocked:").substringBefore("(bootloader) ").trimMargin()
        FastbootCodename = fastbootProps.substringAfter("(bootloader) product:").substringBefore("(bootloader) ").trimMargin()
        FastbootSN = fastbootProps.substringAfter("(bootloader) serialno:").substringBefore("(bootloader) ").trimMargin()
        SystemFS = fastbootProps.substringAfter("(bootloader) partition-type:system:").substringBefore("(bootloader) ").trimMargin()
        val systemDec = fastbootProps.substringAfter("(bootloader) partition-size:system: 0x").substringBefore("(bootloader) ").trimMargin()
        SystemCapacity = (java.lang.Long.parseLong(systemDec, 16) / 1048576).toString().trimMargin()
        DataFS = fastbootProps.substringAfter("(bootloader) partition-type:userdata:").substringBefore("(bootloader) ").trimMargin()
        val dataDec = fastbootProps.substringAfter("(bootloader) partition-size:userdata: 0x").substringBefore("(bootloader) ").trimMargin()
        DataCapacity = (java.lang.Long.parseLong(dataDec, 16) / 1048576).toString().trimMargin()
        BootFS = fastbootProps.substringAfter("(bootloader) partition-type:boot:").substringBefore("(bootloader) ").trimMargin()
        val bootDec = fastbootProps.substringAfter("(bootloader) partition-size:boot: 0x").substringBefore("(bootloader) ").trimMargin()
        BootCapacity = (java.lang.Long.parseLong(bootDec, 16) / 1048576).toString().trimMargin()
        RecoveryFS = fastbootProps.substringAfter("(bootloader) partition-type:recovery:").substringBefore("(bootloader) ").trimMargin()
        val recoveryDec = fastbootProps.substringAfter("(bootloader) partition-size:recovery: 0x").substringBefore("(bootloader) ").trimMargin()
        RecoveryCapacity = (java.lang.Long.parseLong(recoveryDec, 16) / 1048576).toString().trimMargin()
        CacheFS = fastbootProps.substringAfter("(bootloader) partition-type:cache:").substringBefore("(bootloader) ").trimMargin()
        val cacheDec = fastbootProps.substringAfter("(bootloader) partition-size:cache: 0x").substringBefore("(bootloader) ").trimMargin()
        CacheCapacity = (java.lang.Long.parseLong(cacheDec, 16) / 1048576).toString().trimMargin()
        VendorFS = fastbootProps.substringAfter("(bootloader) partition-type:vendor:").substringBefore("(bootloader) ").trimMargin()
        val vendorDec = fastbootProps.substringAfter("(bootloader) partition-size:vendor: 0x").substringBefore("(bootloader) ").trimMargin()
        VendorCapacity = (java.lang.Long.parseLong(vendorDec, 16) / 1048576).toString()
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

    fun exec(app: String, command: String, output: Boolean = false, streamType: String = "Input"): String {
        try {
            val process = Runtime.getRuntime().exec("$SdkDir$app $command")
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

    fun execLines(command: String): List<String> {
        val process = Runtime.getRuntime().exec("$SdkDir$command")
        return process.inputStream.bufferedReader().readLines()
    }

    private fun noConnection() {
        val components: Array<Component> = fastbootPanel.components
        for (component in components) {
            component.isEnabled = false
        }
        val components2: Array<Component> = adbPanel.components
        for (component in components2) {
            component.isEnabled = false
        }
        val components4: Array<Component> = recoveryPanel.components
        for (component in components4) {
            component.isEnabled = false
        }
        val components3: Array<Component> = logsPanel.components
        for (component in components3) {
            if (component != buttonStop && component != buttonSave) {
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

fun getProgramBuildTime(): String {
    var d: Date? = null
    val currentClass = object : Any() {}.javaClass.enclosingClass
    val resource = currentClass.getResource(currentClass.simpleName + ".class")
    if (resource != null) {
        when (resource.protocol) {
            "file" -> {
                try {
                    d = Date(File(resource.toURI()).lastModified())
                } catch (ignored: URISyntaxException) {
                }
            }
            "jar" -> {
                val path = resource.path
                d = Date(File(path.substring(5, path.indexOf("!"))).lastModified())
            }
            "zip" -> {
                val path = resource.path
                val jarFileOnDisk = File(path.substring(0, path.indexOf("!")))
                try {
                    JarFile(jarFileOnDisk).use { jf ->
                        val ze = jf.getEntry(path.substring(path.indexOf("!") + 2))
                        val zeTimeLong = ze.time
                        val zeTimeDate = Date(zeTimeLong)
                        d = zeTimeDate
                    }
                } catch (ignored: IOException) {
                } catch (ignored: RuntimeException) {
                }
            }
        }
    }
    return d.toString()
}