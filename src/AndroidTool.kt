import java.awt.Rectangle
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JFileChooser
import javax.swing.SwingWorker
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.collections.ArrayList
import kotlin.system.exitProcess




var arrayList = java.util.ArrayList<String>()
var selectedDirectoryPath = ""
var selectedFileAbsolutePath = ""
var selectedFilePath = ""
var selectedZipPath = ""
var selectedZipName = ""
var listModel = DefaultListModel<Any?>()
var listModelLogs = DefaultListModel<Any?>()
var apps: ArrayList<Any> = ArrayList()
var systemIP = ""
var Manufacturer = ""
var Brand = ""
var Model = ""
var Codename = ""
var CPU = ""
var CPUArch = ""
var SN = ""
var GsmOperator = ""
var Fingerprint = ""
var VersionRelease = ""
var SDK = ""
var SecurityPatch = ""
var Language = ""
var Selinux = ""
var Treble = ""
var DeviceHost = ""
var SecureBoot = ""
var MockLocation = ""
var Unlock = ""
var FastbootCodename = ""
var FastbootSN = ""
var SystemFS = ""
var SystemCapacity = ""
var DataFS = ""
var DataCapacity = ""
var BootFS = ""
var BootCapacity = ""
var RecoveryFS = ""
var RecoveryCapacity = ""
var CacheFS = ""
var CacheCapacity = ""
var VendorFS = ""
var VendorCapacity = ""
var AllCapacity = ""
var newPhone = true
var enabledAll = true
var GetStateOutput = ""
var GetStateErrorOutput = ""
var AdbDevicesOutput = ""
var FastbootDevicesOutput = ""
var ConnectedViaAdb = false
var ConnectedViaFastboot = false
var ConnectedViaRecovery = false
var UnauthorizedDevice = false
var MultipleDevicesConnected = false
var CommandRunning = false
var ConnectedAdbUsb = false
var ConnectedAdbWifi = false
var functionButtonStart = true
var ifStopSelected = false
var logsWorking: Boolean = false
var iconYes = ImageIcon(AndroidTool()::class.java.getResource("/icon/connectionSuccess.png"))
var iconNo = ImageIcon(AndroidTool()::class.java.getResource("/icon/connectionFailed.png"))
val Windows = "Windows" in System.getProperty("os.name")
val Linux = "Linux" in System.getProperty("os.name")
val MacOS = "Mac" in System.getProperty("os.name")
val JarDir = System.getProperty("user.dir").toString()
val userFolder = System.getProperty("user.home").toString()
var SdkDir = userFolder + if (Windows) { "\\.android_tool\\SDK-Tools\\"} else if (Linux) { "/.android_tool/SDK-Tools/" } else { "/.android_tool/SDK-Tools/"}
val ProgramDir = userFolder + if (Windows) { "\\.android_tool\\"} else if (Linux) { "/.android_tool/" } else { "/.android_tool/"}
val programBuildDate = getProgramBuildTime()
const val programVersion = "1.2.1"
var programVersionLatest = programVersion
val appProp = Properties()

open class AndroidTool : Command(){
    init {
        AndroidToolUI()
        Command()
    }
    companion object : AndroidTool() {
        @JvmStatic
        fun main(args: Array<String>) {
            searchTextField.addKeyListener(object : KeyAdapter() {
                override fun keyReleased(evt: KeyEvent) {
                    searchFilter(searchTextField.text)
                }
            })
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    exec("adb", "kill-server")
                }
            })
            buttonSave.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonSave.isEnabled = false
                        val choseFile = JFileChooser()
                        choseFile.dialogTitle = "Save logs file"
                        choseFile.addChoosableFileFilter(FileNameExtensionFilter("Logs File (.log)", "log"))
                        choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
                        choseFile.fileFilter = choseFile.choosableFileFilters[1]
                        val chooseDialog = choseFile.showSaveDialog(frame)
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            val file = File(choseFile.selectedFile.canonicalPath.toString() + "." + (choseFile.fileFilter as FileNameExtensionFilter).extensions[0])
                            if (!file.exists()) {
                                file.createNewFile()
                            }
                            val fw = FileWriter(file.absoluteFile)
                            val bw = BufferedWriter(fw)
                            for (element in 0 until listModelLogs.size()) {
                                bw.write(listModelLogs[element].toString())
                                bw.write("\n")
                            }
                            bw.close()
                        }
                    }
                    override fun done() {
                        buttonSave.isEnabled = true
                    }
                }
                MyWorker().execute()
            }
            buttonSdkDownload.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonSdkDownload.isEnabled = false
                        createFolder()
                        when{
                            Windows -> downloadFile("https://github.com/fast-geek/SDK-Platform-Tools/raw/main/Windows.zip", "$SdkDir\\Windows.zip")
                            Linux -> downloadFile("https://github.com/fast-geek/SDK-Platform-Tools/raw/main/Linux.zip", "$SdkDir/Linux.zip")
                            MacOS -> downloadFile("https://github.com/fast-geek/SDK-Platform-Tools/raw/main/MacOS.zip", "$SdkDir/MacOS.zip")
                        }
                        when{
                            Windows -> unZipFile("$SdkDir\\Windows.zip")
                            Linux -> unZipFile("$SdkDir/Linux.zip")
                            MacOS -> unZipFile("$SdkDir/MacOS.zip")
                        }
                        if (Windows) Runtime.getRuntime().exec("attrib +h $userFolder\\.android_tool")
                        when{
                            Windows -> SdkDir = "$userFolder\\.android_tool\\SDK-Tools\\"
                            Linux -> SdkDir = "$userFolder/.android_tool/SDK-Tools/"
                            MacOS -> SdkDir = "$userFolder/.android_tool/SDK-Tools/"
                        }
                    }
                    override fun done() {
                        dialogSdkDownload.dispose()
                    }
                }
                Worker().execute()
            }
            radioButtonAll.addActionListener {
                getListOfPackages()
            }
            radioButtonDisabled.addActionListener {
                getListOfPackages()
            }
            radioButtonEnabled.addActionListener {
                getListOfPackages()
            }
            radioButtonSystem.addActionListener {
                getListOfPackages()
            }
            radioButtonThird.addActionListener {
                getListOfPackages()
            }
            buttonUpdate.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonUpdate.isEnabled = false
                        runUrl("https://github.com/fast-geek/Android-Tool/releases/latest")
                    }
                    override fun done() {
                        Runtime.getRuntime().exec("${SdkDir}adb kill-server")
                        exitProcess(0)
                    }
                }
                Worker().execute()
            }
            buttonIpConnect.addActionListener {
                labelConnect.text = ""
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonIpConnect.isEnabled = false
                        exec("adb", "kill-server")
                        val output = exec("adb", "connect ${textFieldIP.text}", output = true)
                        if ("connected to" in output || "failed to authenticate to" in output) {
                            labelTCPConnection.text = "Connected to ${textFieldIP.text}"
                            labelTCPConnection.icon = iconYes
                        } else {
                            labelConnect.text = "Failed"
                        }
                    }
                    override fun done() {
                        buttonIpConnect.isEnabled = true
                    }
                }
                Worker().execute()
            }
            buttonStart.addActionListener {
                buttonStop.isEnabled =true
                buttonSave.isEnabled = false
                if (ifStopSelected) {
                    functionButtonStart = true
                }
                if (functionButtonStart) {
                    buttonSave.isEnabled = false
                    logsWorking = true
                    buttonStart.text = "Pause"
                    if (ifStopSelected) {
                        listModelLogs.removeAllElements()
                    }
                    class MyWorker : SwingWorker<Unit, Int>() {
                        override fun doInBackground() {
                            arrayList.clear()
                            Runtime.getRuntime().exec("${SdkDir}adb logcat -c").waitFor()
                            val builderList = when {
                                radioButtonVerbose.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:V")
                                radioButtonDebug.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:D")
                                radioButtonInfo.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:I")
                                radioButtonWarning.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:W")
                                radioButtonError.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:E")
                                radioButtonFatal.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:F")
                                radioButtonSilent.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:S")
                                else -> Runtime.getRuntime().exec("${SdkDir}adb logcat -c")
                            }
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                    if (logsWorking) {
                                        listModelLogs.addElement(line)
                                        listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                    }
                                }
                            }
                        }
                    }
                    val worker = MyWorker()
                    worker.execute()
                    functionButtonStart = false
                    ifStopSelected = false
                } else {
                    if (ifStopSelected) {
                        listModelLogs.removeAllElements()
                    } else {
                        logsWorking = false
                        functionButtonStart = true
                        buttonStart.text = "Continue"
                    }
                }
            }
            buttonStop.addActionListener {
                buttonStop.isEnabled = false
                buttonStart.text = "Start"
                logsWorking = false
                ifStopSelected = true
                buttonSave.isEnabled = true
                functionButtonStart = true
            }
            buttonReboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonReboot.isEnabled = false
                        when {
                            ConnectedViaAdb -> exec("adb", "reboot")
                            ConnectedViaFastboot -> exec("fastboot", "reboot")
                            ConnectedViaRecovery -> exec("adb", "shell twrp reboot")
                        }
                    }
                    override fun done() {
                        buttonReboot.isEnabled = true
                    }
                }
                Worker().execute()
            }
            buttonRecoveryReboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRecoveryReboot.isEnabled = false
                        when {
                            ConnectedViaAdb -> exec("adb", "reboot recovery")
                            ConnectedViaFastboot -> exec("fastboot", "oem reboot-recovery")
                            ConnectedViaRecovery -> exec("adb", "shell twrp reboot recovery")
                        }
                    }

                    override fun done() {
                        buttonRecoveryReboot.isEnabled = true
                    }
                }
                Worker().execute()
            }
            buttonGetLogs.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonGetLogs.isEnabled = false
                        exec("adb", "shell cp -f /tmp/recovery.log /sdcard")
                    }
                    override fun done() { buttonGetLogs.isEnabled = true }
                }
                Worker().execute()
            }
            openConsole.addActionListener {
                if (Windows)
                    Runtime.getRuntime().exec("cmd /c start cd $SdkDir")
                else
                    Runtime.getRuntime().exec("open -a Terminal $SdkDir")
            }
            buttonFastbootReboot.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonFastbootReboot.isEnabled = false
                        when {
                            ConnectedViaAdb -> exec("adb", "reboot bootloader")
                            ConnectedViaFastboot -> exec("fastboot", "reboot-bootloader")
                            ConnectedViaRecovery -> exec("adb", "shell twrp reboot bootloader")
                        }
                    }
                    override fun done() { buttonFastbootReboot.isEnabled = true }
                }
                Worker().execute()
            }
            buttonPowerOff.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonPowerOff.isEnabled = false
                        when{
                            ConnectedViaAdb -> exec("adb", "reboot -p")
                            ConnectedViaRecovery -> exec("adb", "shell twrp reboot poweroff")
                        }
                    }
                    override fun done() { buttonPowerOff.isEnabled = true }
                }
                Worker().execute()
            }
            tabbedpane.addChangeListener {
                try {
                    textFieldIP.text = AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
                } catch (e: Exception) { }
                if (tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1) {
                    contents.setBounds(5, 5, 310, 375)
                    deviceControlPanel.setBounds(5, 385, 310, 85)
                    deviceConnection.setBounds(5, 475, 310, 100)
                    labelTCP.isVisible = true
                    labelTCPConnection.isVisible = true
                    buttonIpConnect.isVisible = true
                    textFieldIP.isVisible = true
                    labelConnect.isVisible = true
                    labelIP.isVisible = true
                } else if (tabbedpane.selectedIndex == 2) {
                    contents.setBounds(5, 5, 310, 425)
                    deviceControlPanel.setBounds(5, 435, 310, 85)
                    deviceConnection.setBounds(5, 525, 310, 50)
                    labelTCP.isVisible = false
                    labelTCPConnection.isVisible = false
                    buttonIpConnect.isVisible = false
                    textFieldIP.isVisible = false
                    labelConnect.isVisible = false
                    labelIP.isVisible = false
                } else if (tabbedpane.selectedIndex == 3) {
                    contents.setBounds(5, 5, 310, 425)
                    deviceControlPanel.setBounds(5, 435, 310, 85)
                    deviceConnection.setBounds(5, 525, 310, 50)
                    labelTCP.isVisible = false
                    labelTCPConnection.isVisible = false
                    buttonIpConnect.isVisible = false
                    textFieldIP.isVisible = false
                    labelConnect.isVisible = false
                    labelIP.isVisible = false
                }
                else if (tabbedpane.selectedIndex == 3) {

                }
                else if (tabbedpane.selectedIndex == 5) {
                    contents.setBounds(5, 5, 310, 375)
                    deviceControlPanel.setBounds(5, 385, 310, 85)
                    deviceConnection.setBounds(5, 475, 310, 100)
                    labelTCP.isVisible = true
                    labelTCPConnection.isVisible = true
                    buttonIpConnect.isVisible = true
                    textFieldIP.isVisible = true
                    labelConnect.isVisible = true
                    labelIP.isVisible = true
                }
            }
            buttonInstallAll.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallAll.isEnabled = false
                        val paths: Array<File>?
                        val file = File(selectedDirectoryPath)
                        val fileNameFilter = FilenameFilter { _, name ->
                            if (name.lastIndexOf('.') > 0) {
                                val lastIndex = name.lastIndexOf('.')
                                val str = name.substring(lastIndex)
                                if (str == ".apk") {
                                    return@FilenameFilter true
                                }
                            }
                            false
                        }
                        paths = file.listFiles(fileNameFilter)
                        for (path in paths) {
                            if (Windows)
                                exec("adb", "install \"$path\"")
                            else
                                exec("adb", "install $path")
                        }
                    }
                    override fun done() { buttonInstallAll.isEnabled = true }
                }
                Worker().execute()
            }
            buttonInstallOne.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallOne.isEnabled = false
                        if (Windows)
                            exec("adb", "install \"$selectedFileAbsolutePath\"")
                        else
                            exec("adb", "install $selectedFileAbsolutePath")
                    }
                    override fun done() { buttonInstallOne.isEnabled = true }
                }
                Worker().execute()
            }
            disableButton.addActionListener {
                val textInput = list.selectedValue.toString().substringBefore("(")
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        disableButton.isEnabled = false
                        exec("adb", "shell pm disable-user --user 0 $textInput")
                    }
                    override fun done() {
                        disableButton.isEnabled = true
                        getListOfPackages()
                    }
                }
                Worker().execute()
            }
            uninstallButton.addActionListener {
                val textInput = list.selectedValue.toString().substringBefore("(")

                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        uninstallButton.isEnabled = false
                        exec("adb", "shell pm uninstall --user 0 $textInput")
                    }

                    override fun done() {
                        uninstallButton.isEnabled = true
                        getListOfPackages()
                    }
                }
                Worker().execute()
            }
            enableButton.addActionListener {
                val textInput = list.selectedValue.toString().substringBefore("(")
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        enableButton.isEnabled = false
                        exec("adb", "shell pm enable $textInput")
                    }
                    override fun done() {
                        enableButton.isEnabled = true
                    }
                }
                Worker().execute()
            }
            clearButton.addActionListener {
                val textInput = list.selectedValue.toString().substringBefore("(")
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        clearButton.isEnabled = false
                        exec("adb", "shell pm clear $textInput")
                    }
                    override fun done() {
                        clearButton.isEnabled = true
                    }
                }
                Worker().execute()
            }
            openButton.addActionListener {
                val textInput = list.selectedValue.toString().substringBefore("(")
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        openButton.isEnabled = false
                        exec("adb", "shell monkey -p $textInput 1")
                    }
                    override fun done() {
                        openButton.isEnabled = true
                    }
                }
                Worker().execute()
            }
            forceStopButton.addActionListener {
                val textInput = list.selectedValue.toString().substringBefore("(")
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        forceStopButton.isEnabled = false
                        exec("adb", "shell am force-stop $textInput")
                    }
                    override fun done() {
                        forceStopButton.isEnabled = true
                    }
                }
                Worker().execute()
            }
            refreshButton.addActionListener {
                getListOfPackages(true)
            }
            buttonChooseOne.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChooseOne.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("APK Files", "apk")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Choose APK")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
                            selectedFilePath = choseFile.selectedFile.path
                            labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
                        }
                    }
                    override fun done() {
                        buttonChooseOne.isEnabled = true
                    }
                }
                Worker().execute()
            }
            buttonChoseAll.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChoseAll.isEnabled = false
                        val choseDirectory = JFileChooser()
                        choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                        val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedDirectoryPath = choseDirectory.selectedFile.path
                            labelSelectedAll.text = "Selected: ${choseDirectory.selectedFile.path}"
                        }
                    }
                    override fun done() {
                        buttonChoseAll.isEnabled = true
                    }
                }
                Worker().execute()
            }
            buttonRunCommand.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRunCommand.isEnabled = false
                        val command = textAreaCommandInput.text
                        when {
                            "adb" in command -> textAreaCommandOutput.text = exec("adb", textAreaCommandInput.text.substring(4), output = true)
                            "fastboot" in command -> textAreaCommandOutput.text = exec("fastboot", textAreaCommandInput.text.substring(9), output = true)
                            else -> textAreaCommandOutput.text = exec("adb", textAreaCommandInput.text, output = true)
                        }
                    }
                    override fun done() {
                        buttonRunCommand.isEnabled = true
                    }
                }
                Worker().execute()
            }
            buttonErase.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonErase.isEnabled = false
                        if (checkBoxPartitionBoot.isSelected)
                            exec("fastboot", "erase boot")
                        if (checkBoxPartitionSystem.isSelected)
                            exec("fastboot", "erase system")
                        if (checkBoxPartitionData.isSelected)
                            exec("fastboot", "erase userdata")
                        if (checkBoxPartitionCache.isSelected)
                            exec("fastboot", "erase cache")
                        if (checkBoxPartitionRecovery.isSelected)
                            exec("fastboot", "erase recovery")
                        if (checkBoxPartitionRadio.isSelected)
                            exec("fastboot", "erase radio")
                    }
                    override fun done() { buttonErase.isEnabled = true }
                }
                Worker().execute()
            }
            buttonChoseRecovery.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChoseRecovery.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("Recovery Files", "img")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
                            selectedFilePath = choseFile.selectedFile.path
                            labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
                        }
                    }
                    override fun done() { buttonChoseRecovery.isEnabled = true }
                }
                Worker().execute()
            }
            buttonInstallRecovery.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallRecovery.isEnabled = false
                        if (Windows)
                            exec("fastboot", "flash recovery \"$selectedFileAbsolutePath\"")
                        else
                            exec("fastboot", "flash recovery $selectedFileAbsolutePath")
                    }
                    override fun done() { buttonInstallRecovery.isEnabled = true }
                }
                Worker().execute()
            }
            buttonBootToRecovery.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonBootToRecovery.isEnabled = false
                        if (Windows)
                            exec("fastboot", "boot \"$selectedFileAbsolutePath\"")
                        else
                            exec("fastboot", "boot $selectedFileAbsolutePath")
                    }
                    override fun done() { buttonBootToRecovery.isEnabled = true }
                }
                Worker().execute()
            }
            buttonChooseZip.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChooseZip.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("Zip files", "zip")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Select Zip")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedZipPath = choseFile.selectedFile.absolutePath
                            selectedZipName = choseFile.selectedFile.name
                        }
                    }
                    override fun done() { buttonChooseZip.isEnabled = true }
                }
                Worker().execute()
            }
            buttonInstallZip.addActionListener {
                class Worker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallZip.isEnabled = false
                        if (Windows) {
                            exec("adb", "push \"${selectedZipPath}\" /sdcard/")
                        } else {
                            exec("adb", "push $selectedZipPath /sdcard/")
                        }
                        exec("adb", "shell twrp install $selectedZipName")
                        exec("adb", "shell rm $selectedZipName")
                    }
                    override fun done() { buttonInstallZip.isEnabled = true }
                }
                Worker().execute()
            }
            deviceControlPanel.setBounds(5, 385, 310, 85)
            deviceConnection.setBounds(5, 475, 310, 100)
            labelTCP.isVisible = true
            labelTCPConnection.isVisible = true
            buttonIpConnect.isVisible = true
            textFieldIP.isVisible = true
            labelConnect.isVisible = true
            labelIP.isVisible = true

            frame.isVisible = true

            appProp.load(AndroidTool::class.java.getResource("applist.properties").openStream())

            DatagramSocket().use { socket ->
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
                systemIP = "${socket.localAddress.hostAddress.substringBeforeLast('.')}."
            }
            try {
                textFieldIP.text = systemIP
            } catch (e: Exception) {
            }
            sdkCheck()
            Thread {
                while (true) {
                    try {
                        connectionCheck()
                        Thread.sleep(1000)
                    } catch (ex: InterruptedException) { }
                }
            }.start()
            versionCheck()
        }
    }
}
