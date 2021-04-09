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
import javax.swing.SwingWorker


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
                if(model.rowCount != 0)
                    for (i in model.rowCount - 1 downTo 0)
                        model.removeRow(i)
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
                buttonPowerOff.isEnabled = true
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
                dialogUnauthorizedDevice.dispose()
                if (enabledAll) {
                    val disableComponents: Array<Component> = fastbootPanel.components + recoveryPanel.components
                    for (component in disableComponents)
                        if (component != openConsole)
                            component.isEnabled = false
                    val enableComponents: Array<Component> = adbPanel.components + consolePanel.components + logsPanel.components
                    for (component in enableComponents)
                        if (component != buttonStop && component != buttonSave)
                            component.isEnabled = true
                }
                textAreaCommandOutput.isFocusable = true
                textAreaCommandInput.isFocusable = true
                listLogs.isFocusable = true
                list.isFocusable = true

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
                if (newPhone) {
                    tabbedpane.selectedIndex = 0
                    getProp()
                    getListOfPackages()
                }
                newPhone = false
                enabledAll = false
            }
            ConnectedViaFastboot -> {
                buttonPowerOff.isEnabled = false
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
                buttonIpConnect.isEnabled = false
                if (enabledAll) {
                    val disableComponents: Array<Component> = adbPanel.components + logsPanel.components + recoveryPanel.components
                    for (component in disableComponents)
                        if (component != openConsole)
                            component.isEnabled = false
                    val enableComponents: Array<Component> = fastbootPanel.components + consolePanel.components
                    for (component in enableComponents)
                        if (component != buttonStop && component != buttonSave)
                            component.isEnabled = true
                }
                textAreaCommandOutput.isFocusable = false
                textAreaCommandInput.isFocusable = false
                listLogs.isFocusable = false
                list.isFocusable = false

                if (newPhone) {
                    tabbedpane.selectedIndex = 2
                    labelUSBConnection.text = "Connected via Fastboot"
                    labelUSBConnection.icon = iconYes
                    getPropFastboot()
                }
                newPhone = false
                enabledAll = false
            }
            ConnectedViaRecovery -> {
                buttonReboot.isEnabled = true
                buttonRecoveryReboot.isEnabled = true
                buttonFastbootReboot.isEnabled = true
                buttonPowerOff.isEnabled = true
                buttonIpConnect.isEnabled = false
                frame.isEnabled = true
                dialogUnauthorizedDevice.dispose()
                if (enabledAll) {
                    val disableComponents: Array<Component> = adbPanel.components + fastbootPanel.components
                    for (component in disableComponents)
                        if (component != openConsole)
                            component.isEnabled = false
                    val enableComponents: Array<Component> = recoveryPanel.components + consolePanel.components + logsPanel.components
                    for (component in enableComponents)
                        if (component != buttonStop && component != buttonSave)
                            component.isEnabled = true
                }
                textAreaCommandOutput.isFocusable = false
                textAreaCommandInput.isFocusable = false
                listLogs.isFocusable = false
                list.isFocusable = false

                if (newPhone) {
                    tabbedpane.selectedIndex = 3

                    labelUSBConnection.text = "Connected via Adb"
                    labelUSBConnection.icon = iconYes
                    getPropRecovery()
                }

                newPhone = false
                enabledAll = false
            }
            else -> {
                buttonReboot.isEnabled = false
                buttonRecoveryReboot.isEnabled = false
                buttonFastbootReboot.isEnabled = false
                buttonPowerOff.isEnabled = false
                buttonIpConnect.isEnabled = false
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
        val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [")
        Manufacturer = if (lineValue1 == deviceProps) "Unknown" else lineValue1.substringBefore(']')
        val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [")
        Brand = if (lineValue2 == deviceProps) "Unknown" else lineValue2.substringBefore(']')
        val lineValue3 = deviceProps.substringAfter("ro.product.model]: [")
        Model = if (lineValue3 == deviceProps) "Unknown" else lineValue3.substringBefore(']')
        val lineValue4 = deviceProps.substringAfter("ro.product.name]: [")
        Codename = if (lineValue4 == deviceProps) "Unknown" else lineValue4.substringBefore(']')
        val lineValue5 = deviceProps.substringAfter("ro.product.board]: [")
        CPU = if (lineValue5 == deviceProps) "Unknown" else lineValue5.substringBefore(']')
        val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [")
        CPUArch = if (lineValue6 == deviceProps) "Unknown" else lineValue6.substringBefore(']')
        val lineValue7 = deviceProps.substringAfter("ro.serialno]: [")
        SN = if (lineValue7 == deviceProps) "Unknown" else lineValue7.substringBefore(']')
        val lineValue8 = deviceProps.substringAfter("gsm.operator.alpha]: [")
        GsmOperator = if (lineValue8 == deviceProps || lineValue8 == ",") "Unknown" else lineValue8.substringBefore(']')
        val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [")
        Fingerprint = if (lineValue9 == deviceProps) "Unknown" else lineValue9.substringBefore(']')
        val lineValue10 = deviceProps.substringAfter("ro.build.version.release]: [")
        VersionRelease = if (lineValue10 == deviceProps) "Unknown" else lineValue10.substringBefore(']')
        val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [")
        SDK = if (lineValue11 == deviceProps) "Unknown" else lineValue11.substringBefore(']')
        val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [")
        SecurityPatch = if (lineValue12 == deviceProps) "Unknown" else lineValue12.substringBefore(']')
        val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [")
        Language = if (lineValue13 == deviceProps) "Unknown" else lineValue13.substringBefore(']')
        val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [")
        Selinux = if (lineValue14 == deviceProps || "DEVICE" in lineValue14) "Unknown" else lineValue14.substringBefore(']')
        val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [")
        Treble = if (lineValue15 == deviceProps) "Unknown" else lineValue15.substringBefore(']')
        model.addRow(arrayOf("Manufacturer", Manufacturer))
        model.addRow(arrayOf("Brand", Brand))
        model.addRow(arrayOf("Model", Model))
        model.addRow(arrayOf("Codename", Codename))
        model.addRow(arrayOf("CPU", CPU))
        model.addRow(arrayOf("CPU Architecture", CPUArch))
        model.addRow(arrayOf("Serial Number", SN))
        model.addRow(arrayOf("Cellular Provider", GsmOperator))
        model.addRow(arrayOf("Fingerprint", Fingerprint))
        model.addRow(arrayOf("Android Version", VersionRelease))
        model.addRow(arrayOf("SDK Version", SDK))
        model.addRow(arrayOf("Security Patch", SecurityPatch))
        model.addRow(arrayOf("Language", Language))
        model.addRow(arrayOf("Selinux", Selinux))
        model.addRow(arrayOf("Project Treble", Treble))
    }

    private fun getPropRecovery() {
        val deviceProps = exec("adb", "shell getprop", output = true)
        val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [")
        Manufacturer = if (lineValue1 == deviceProps) "Unknown" else lineValue1.substringBefore(']')
        val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [")
        Brand = if (lineValue2 == deviceProps) "Unknown" else lineValue2.substringBefore(']')
        val lineValue3 = deviceProps.substringAfter("ro.product.model]: [")
        Model = if (lineValue3 == deviceProps) "Unknown" else lineValue3.substringBefore(']')
        val lineValue4 = deviceProps.substringAfter("ro.product.name]: [")
        Codename = if (lineValue4 == deviceProps) "Unknown" else lineValue4.substringBefore(']')
        val lineValue5 = deviceProps.substringAfter("ro.boot.hardware]: [")
        CPU = if (lineValue5 == deviceProps) "Unknown" else lineValue5.substringBefore(']')
        val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [")
        CPUArch = if (lineValue6 == deviceProps) "Unknown" else lineValue6.substringBefore(']')
        val lineValue7 = deviceProps.substringAfter("ro.serialno]: [")
        SN = if (lineValue7 == deviceProps) "Unknown" else lineValue7.substringBefore(']')
        val lineValue8 = deviceProps.substringAfter("sys.usb.state]: [")
        GsmOperator = if (lineValue8 == deviceProps) "Unknown" else lineValue8.substringBefore(']')
        val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [")
        Fingerprint = if (lineValue9 == deviceProps) "Unknown" else lineValue9.substringBefore(']')
        var lineValue10 = deviceProps.substringAfter("ro.orangefox.version]: [").substringBefore(']')
        if (lineValue10.isNotBlank()) VersionRelease = lineValue10 else {
            lineValue10 = deviceProps.substringAfter("ro.twrp.version]: [")
            VersionRelease = if (lineValue10 == deviceProps) "Unknown" else lineValue10.substringBefore(']')
        }
        val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [")
        SDK = if (lineValue11 == deviceProps) "Unknown" else lineValue11.substringBefore(']')
        val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [")
        SecurityPatch = if (lineValue12 == deviceProps) "Unknown" else lineValue12.substringBefore(']')
        val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [")
        Language = if (lineValue13 == deviceProps) "Unknown" else lineValue13.substringBefore(']')
        val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [")
        Selinux = if (lineValue14 == deviceProps) "Unknown" else lineValue14.substringBefore(']')
        val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [")
        Treble = if (lineValue15 == deviceProps) "Unknown" else lineValue15.substringBefore(']')
        val lineValue16 = deviceProps.substringAfter("ro.boot.secureboot]: [")
        SecureBoot = if (lineValue16 == deviceProps) "Unknown" else {if (lineValue16.substringBefore(']') == "1") "true" else "false"}
        val lineValue17 = deviceProps.substringAfter("ro.build.host]: [")
        DeviceHost = if (lineValue17 == deviceProps) "Unknown" else lineValue17.substringBefore(']')
        val lineValue18 = deviceProps.substringAfter("ro.allow.mock.location]: [")
        MockLocation = if (lineValue18 == deviceProps) "Unknown" else {if (lineValue18.substringBefore(']') == "1") "true" else "false" }
        val lineValue19 = deviceProps.substringAfter("ro.build.id]: [")
        Language = if (lineValue19 == deviceProps) "Unknown" else lineValue19.substringBefore(']')
        model.addRow(arrayOf("Manufacturer", Manufacturer))
        model.addRow(arrayOf("Brand", Brand))
        model.addRow(arrayOf("Model", Model))
        model.addRow(arrayOf("Codename", Codename))
        model.addRow(arrayOf("CPU", CPU))
        model.addRow(arrayOf("CPU Architecture", CPUArch))
        model.addRow(arrayOf("Serial Number", SN))
        model.addRow(arrayOf("USB Mode", GsmOperator))
        model.addRow(arrayOf("Fingerprint", Fingerprint))
        model.addRow(arrayOf("Recovery Version", VersionRelease))
        model.addRow(arrayOf("SDK Version", SDK))
        model.addRow(arrayOf("SecurityPatch", SecurityPatch))
        model.addRow(arrayOf("Build ID", Language))
        model.addRow(arrayOf("Selinux", Selinux))
        model.addRow(arrayOf("Project Treble", Treble))
        model.addRow(arrayOf("Secure Boot", SecureBoot))
        model.addRow(arrayOf("Build Hostname", DeviceHost))
        model.addRow(arrayOf("Mock Locations", MockLocation))
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
        model.addRow(arrayOf("Unlocked", if (Unlock != "< waiting for any device >") Unlock else "-"))
        model.addRow(arrayOf("Codename", if (FastbootCodename != "< waiting for any device >") FastbootCodename else "-"))
        model.addRow(arrayOf("Serial Number", if (FastbootSN != "< waiting for any device >") FastbootSN else "-"))
        model.addRow(arrayOf("/system File system:", if (SystemFS != "< waiting for any device >") SystemFS else "-"))
        model.addRow(arrayOf("/system Capacity (MB):", if (SystemCapacity != "< waiting for any device >") SystemCapacity else "-"))
        model.addRow(arrayOf("/data File system:", if (DataFS != "< waiting for any device >") DataFS else "-"))
        model.addRow(arrayOf("/data Capacity (MB):", if (DataCapacity != "< waiting for any device >") DataCapacity else "-"))
        model.addRow(arrayOf("/boot File system:", if (BootFS != "< waiting for any device >") BootFS else "-"))
        model.addRow(arrayOf("/boot Capacity (MB):", if (BootCapacity != "< waiting for any device >") BootCapacity else "-"))
        model.addRow(arrayOf("/recovery File system:", if (RecoveryFS != "< waiting for any device >") RecoveryFS else "-"))
        model.addRow(arrayOf("/recovery Capacity (MB):", if (RecoveryCapacity != "< waiting for any device >") RecoveryCapacity else "-"))
        model.addRow(arrayOf("/cache File system:", if (CacheFS != "< waiting for any device >") CacheFS else "-"))
        model.addRow(arrayOf("/cache Capacity (MB):", if (CacheCapacity != "< waiting for any device >") CacheCapacity else "-"))
        model.addRow(arrayOf("/vendor File system:", if (VendorFS != "< waiting for any device >") VendorFS else "-"))
        model.addRow(arrayOf("/vendor Capacity (MB):", if (VendorCapacity != "< waiting for any device >") VendorCapacity else "-"))
        model.addRow(arrayOf("All Capacity (MB):", if (AllCapacity != "< waiting for any device >") AllCapacity else "-"))
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
        val components: Array<Component> = fastbootPanel.components + adbPanel.components + logsPanel.components + consolePanel.components + recoveryPanel.components
        for (component in components)
            if (component != buttonStop && component != buttonSave && component != openConsole)
                component.isEnabled = false

        textAreaCommandOutput.isFocusable = false
        textAreaCommandInput.isFocusable = false
        listLogs.isFocusable = false
        list.isFocusable = false
        listModel.removeAllElements()
        listModelLogs.removeAllElements()
        if(model.rowCount != 0)
            for (i in model.rowCount - 1 downTo 0)
                model.removeRow(i)
        searchTextField.text = ""
        searchTextField.isEnabled = false
        buttonIpConnect.isEnabled = true
        labelIP.isEnabled = true
        textFieldIP.isEnabled = true
        labelUSBConnection.text = "Not connected"
        labelUSBConnection.icon = iconNo
    }
    fun getListOfPackages(button: Boolean = false){
        class Worker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                val items: DefaultListModel<Any?> = DefaultListModel()
                if (button)
                    refreshButton.isEnabled = false
                searchTextField.isFocusable = true
                arrayList.clear()
                apps.clear()
                val reader = when {
                    radioButtonDisabled.isSelected -> execLines("adb shell pm list packages -d")
                    radioButtonSystem.isSelected -> execLines("adb shell pm list packages -s")
                    radioButtonEnabled.isSelected -> execLines("adb shell pm list packages -e")
                    radioButtonThird.isSelected -> execLines("adb shell pm list packages -3")
                    else -> execLines("adb shell pm list packages")
                }
                for(element in reader){
                    if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element && "not access" !in element) {
                        if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
                            arrayList.add(if (appProp.getProperty(element.substring(8)) != null)
                                "${element.substring(8)} (${appProp.getProperty(element.substring(8), "")})"
                            else
                                element.substring(8))
                        }
                    }
                }
                arrayList.sort()
                if (button)
                    refreshButton.isEnabled = true
                listModel.removeAllElements()
                for (element in arrayList) {
                    if (searchTextField.text == "")
                        items.addElement(element)
                    apps.add(element)
                }
                listModel = items
                list.model = listModel
            }

            override fun done() {
                refreshButton.isEnabled = true
                searchFilter(searchTextField.text)
            }
        }
        Worker().execute()
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
