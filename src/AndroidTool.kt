import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.ImageIcon
import javax.swing.JFileChooser
import javax.swing.SwingWorker
import javax.swing.filechooser.FileNameExtensionFilter


var arrayList = emptyArray<String>()
var selectedDirectoryPath = ""
var selectedFileAbsolutePath = ""
var selectedFileSaveAbsolutePath = ""
var selectedFilePath = ""
var selectedZipPath = ""
var listModel = DefaultListModel<Any?>()
var listModelLogs = DefaultListModel<Any?>()
var stars: ArrayList<Any> = ArrayList<Any>()
var Manufacturer = ""
var Brand = ""
var Model = ""
var Codename = ""
var CPU = ""
var CPUA = ""
var SN = ""
var GsmOperator = ""
var Fingerprint = ""
var VersionRelease = ""
var SDK = ""
var SecurityPatch = ""
var Language = ""
var Selinux = ""
var Treble = ""
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
var moreThan2 = false
var GetStateOutput = ""
var GetStateErrorOutput = ""
var AdbDevicesOutput = ""
var FastbootDevicesOutput = ""
var ConnectedViaAdb = false
var ConnectedViaFastboot = false
var UnauthorizedDevice = false
var MultipleDevicesConnected = false
var CommandRunning = false
var ConnectedAdbUsb = false
var ConnectedAdbWifi = false
var FirstFastbootConnection = true
var FirstAdbConnection = true
var iconYes = ImageIcon(AndroidTool()::class.java.getResource("/icon/check.png"))
var iconNo = ImageIcon(AndroidTool()::class.java.getResource("/icon/not.png"))

open class AndroidTool : Command() {

    init {
        AndroidToolUI()
        Command()
    }
    companion object : AndroidTool() {
        @JvmStatic
        fun main(args: Array<String>) {
            val Windows = "Windows" in System.getProperty("os.name")
            print(Windows)
            buttonIpConnect.addActionListener {
                labelConnect.text = ""
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonIpConnect.isEnabled = false
                        Runtime.getRuntime().exec("adb kill-server").waitFor()
                        val builderList = Runtime.getRuntime().exec("adb connect ${textFieldIP.text}")
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        var output = ""
                        while (reader.readLine().also { line = it } != null) {
                            output += "\n"
                            output += line
                        }
                        if (output.indexOf("connected to") != -1) {
                            labelTCPConnection.text = "Connected to ${textFieldIP.text}"
                            labelTCPConnection.icon = iconYes
                        } else {
                            labelConnect.text = "Failed"
                        }
                        builderList.waitFor()

                    }

                    override fun done() {
                        buttonIpConnect.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }



            fun searchFilter(searchTerm: String) {
                val filteredItems: DefaultListModel<Any?> = DefaultListModel<Any?>()
                val stars = stars
                stars.stream().forEach { star: Any ->
                    val starName = star.toString().toLowerCase()
                    if (starName.contains(searchTerm.toLowerCase())) {
                        if (!filteredItems.contains(star)) {
                        }
                        filteredItems.addElement(star)
                    }
                }
                listModel = filteredItems
                list.setModel(listModel)
            }

            textFieldIPa.addKeyListener(object : KeyAdapter() {
                override fun keyReleased(evt: KeyEvent) {
                    searchFilter(textFieldIPa.text)
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
                        choseFile.fileFilter = choseFile.getChoosableFileFilters()[1]
                        val chooseDialog = choseFile.showSaveDialog(frame)
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            val file = File(choseFile.getSelectedFile().getCanonicalPath().toString() + "." + (choseFile.getFileFilter() as FileNameExtensionFilter).extensions[0])
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

                val worker = MyWorker()
                worker.execute()
            }
            var functionButtonStart = true
            var ifStopSelected = false
            var LogsWorking: Boolean
            buttonStart.addActionListener {
                buttonStop.isEnabled =true
                buttonSave.isEnabled = false
                if (ifStopSelected) {
                    functionButtonStart = true
                }
                if (functionButtonStart) {
                    buttonSave.isEnabled = false
                    LogsWorking = true
                    buttonStart.text = "Pause"
                    if (ifStopSelected) {
                        listModelLogs.removeAllElements()
                    }
                    class MyWorker : SwingWorker<Unit, Int>() {
                        override fun doInBackground() {
                            arrayList = emptyArray()
                            when {
                                radioButtonVerbose.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:V")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonDebug.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:D")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonInfo.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:I")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonWarning.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:W")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonError.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:E")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonFatal.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:F")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
                                    }
                                }
                                radioButtonSilent.isSelected -> {
                                    Runtime.getRuntime().exec("adb logcat -c").waitFor()
                                    val builderList = Runtime.getRuntime().exec("adb logcat *:S")
                                    val input = builderList.inputStream
                                    val reader = BufferedReader(InputStreamReader(input))
                                    var line: String?
                                    while (reader.readLine().also { line = it } != null) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
                                            if (LogsWorking) {
                                                listModelLogs.addElement(line)
                                                listLogs.ensureIndexIsVisible(listLogs.model.size - 1)
                                            }
                                        }
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
                        LogsWorking = false
                        functionButtonStart = true
                        buttonStart.text = "Continue"
                    }
                }
            }

            buttonStop.addActionListener {
                buttonStop.isEnabled = false
                buttonStart.text = "Start"
                LogsWorking = false
                ifStopSelected = true
                buttonSave.isEnabled = true
                functionButtonStart = true
            }



            buttonReboot.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonReboot.isEnabled = false
                        when {
                            tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1 -> Runtime.getRuntime().exec("adb reboot")
                            tabbedpane.selectedIndex == 2 -> Runtime.getRuntime().exec("fastboot reboot")
                            tabbedpane.selectedIndex == 3 -> Runtime.getRuntime().exec("adb shell twrp reboot")
                        }
                    }

                    override fun done() {
                        buttonReboot.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }


            buttonRecoveryReboot.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRecoveryReboot.isEnabled = false
                        when {
                            tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1 -> Runtime.getRuntime().exec("adb reboot recovery")
                            tabbedpane.selectedIndex == 2 -> Runtime.getRuntime().exec("fastboot oem reboot-recovery")
                            tabbedpane.selectedIndex == 3 -> Runtime.getRuntime().exec("adb shell twrp reboot recovery")
                        }
                    }

                    override fun done() {
                        buttonRecoveryReboot.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }


            buttonFastbootReboot.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonFastbootReboot.isEnabled = false
                        when {
                            tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1 -> Runtime.getRuntime().exec("adb reboot bootloader")
                            tabbedpane.selectedIndex == 2 -> Runtime.getRuntime().exec("fastboot reboot-bootloader")
                            tabbedpane.selectedIndex == 3 -> Runtime.getRuntime().exec("adb shell twrp reboot bootloader")
                        }
                    }

                    override fun done() {
                        buttonFastbootReboot.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }


            buttonPowerOff.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonPowerOff.isEnabled = false
                        when {
                            tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1 -> Runtime.getRuntime().exec("adb reboot -p")
                            tabbedpane.selectedIndex == 3 -> Runtime.getRuntime().exec("adb shell twrp reboot poweroff")
                        }
                    }

                    override fun done() {
                        buttonPowerOff.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }

            dialogUnauthorizedDevice.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    frame.isEnabled = true
                }
            })


            val run = Thread {
                while (true) {
                    try {
                        connectionCheck()
                        Thread.sleep(1000) //1000 - 1 сек
                    } catch (ex: InterruptedException) {
                    }
                }
            }
            run.start()


            tabbedpane.addChangeListener {
                try {
                    textFieldIP.text = AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
                } catch (e: Exception) { }
                if (tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1) {
                    boardInfoPanel.isVisible = true
                    softInfoPanel.isVisible = true
                    BootloaderFastbootInfoPanel.isVisible = false
                    softFastbootInfoPanel.isVisible = false
                    StorageFastbootInfoPanel.isVisible = false
                    deviceInfoPanel.setBounds(5, 5, 310, 380)
                    deviceControlPanel.setBounds(5, 385, 310, 85)
                    deviceConnection.setBounds(5, 475, 310, 100)
                    labelTCP.isVisible = true
                    labelTCPConnection.isVisible = true
                    buttonIpConnect.isVisible = true
                    textFieldIP.isVisible = true
                    labelConnect.isVisible = true
                    labelIP.isVisible = true
                } else if (tabbedpane.selectedIndex == 2) {
                    boardInfoPanel.isVisible = false
                    softInfoPanel.isVisible = false
                    BootloaderFastbootInfoPanel.isVisible = true
                    softFastbootInfoPanel.isVisible = true
                    StorageFastbootInfoPanel.isVisible = true
                    deviceInfoPanel.setBounds(5, 5, 310, 430)
                    deviceControlPanel.setBounds(5, 435, 310, 85)
                    deviceConnection.setBounds(5, 525, 310, 50)
                    labelTCP.isVisible = false
                    labelTCPConnection.isVisible = false
                    buttonIpConnect.isVisible = false
                    textFieldIP.isVisible = false
                    labelConnect.isVisible = false
                    labelIP.isVisible = false
                } else if (tabbedpane.selectedIndex == 3) {
                    boardInfoPanel.isVisible = true
                    softInfoPanel.isVisible = true
                    BootloaderFastbootInfoPanel.isVisible = false
                    softFastbootInfoPanel.isVisible = false
                    StorageFastbootInfoPanel.isVisible = false
                    deviceInfoPanel.setBounds(5, 5, 310, 380)
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
                class MyWorker : SwingWorker<Unit, Int>() {
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
                            if (System.getProperty("os.name").indexOf("Windows") != -1) {
                                Runtime.getRuntime().exec("adb install \"$path\"").waitFor()
                            } else {
                                Runtime.getRuntime().exec("adb install $path").waitFor()
                            }
                        }
                    }

                    override fun done() {
                        buttonInstallAll.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }


            buttonInstallOne.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallOne.isEnabled = false
                        if (System.getProperty("os.name").indexOf("Windows") != -1) {
                            Runtime.getRuntime().exec("adb install \"$selectedFileAbsolutePath\"").waitFor()
                        } else {
                            Runtime.getRuntime().exec("adb install $selectedFileAbsolutePath").waitFor()
                        }
                    }

                    override fun done() {
                        buttonInstallOne.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }


            buttonDisable.addActionListener {
                val textInput: String = if (textAreaInput.text != "You can enter app package here" && textAreaInput.text != "") {
                    textAreaInput.text
                } else {
                    list.selectedValue.toString()
                }


                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonDisable.isEnabled = false
                        Runtime.getRuntime().exec("adb shell pm disable-user --user 0 $textInput").waitFor()
                    }

                    override fun done() {
                        buttonDisable.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }


            buttonUninstall.addActionListener {
                val textInput: String = if (textAreaInput.text != "You can enter app package here" && textAreaInput.text != "") {
                    textAreaInput.text
                } else {
                    list.selectedValue.toString()
                }


                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonUninstall.isEnabled = false
                        Runtime.getRuntime().exec("adb shell pm uninstall --user 0 $textInput").waitFor()
                    }

                    override fun done() {
                        buttonUninstall.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }


            buttonEnable.addActionListener {
                val textInput: String = if (textAreaInput.text != "You can enter app package here" && textAreaInput.text != "") {
                    textAreaInput.text
                } else {
                    list.selectedValue.toString()
                }


                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonEnable.isEnabled = false
                        Runtime.getRuntime().exec("adb shell pm enable $textInput").waitFor()
                    }

                    override fun done() {
                        buttonEnable.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }


            buttonCheck.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonCheck.isEnabled = false
                        textFieldIPa.isFocusable = true
                        when {
                            radioButtonDisabled.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val builderList = Runtime.getRuntime().exec("adb shell pm list packages -d")
                                val input = builderList.inputStream
                                val reader = BufferedReader(InputStreamReader(input))
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    if (line?.indexOf("no devices/emulators found") == -1 && line?.indexOf("device unauthorized.") == -1 && line?.indexOf("kill-server") == -1 && line?.indexOf("server's") == -1 && line?.indexOf("a confirmation dialog") == -1) {
                                        if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully") {
                                        }
                                        arrayList += line?.substring(8).toString()
                                    }

                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                                builderList.waitFor()
                            }
                            radioButtonSystem.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val builderList = Runtime.getRuntime().exec("adb shell pm list packages -s")
                                val input = builderList.inputStream
                                val reader = BufferedReader(InputStreamReader(input))
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    if (line?.indexOf("no devices/emulators found") == -1 && line?.indexOf("device unauthorized.") == -1 && line?.indexOf("kill-server") == -1 && line?.indexOf("server's") == -1 && line?.indexOf("a confirmation dialog") == -1) {
                                        if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully") {
                                        }
                                        arrayList += line?.substring(8).toString()
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                                builderList.waitFor()
                            }
                            radioButtonEnabled.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val builderList = Runtime.getRuntime().exec("adb shell pm list packages -e")
                                val input = builderList.inputStream
                                val reader = BufferedReader(InputStreamReader(input))
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    if (line?.indexOf("no devices/emulators found") == -1 && line?.indexOf("device unauthorized.") == -1 && line?.indexOf("kill-server") == -1 && line?.indexOf("server's") == -1 && line?.indexOf("a confirmation dialog") == -1) {
                                        if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully") {
                                        }
                                        arrayList += line?.substring(8).toString()
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                                builderList.waitFor()
                            }
                            radioButtonThird.isSelected -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val builderList = Runtime.getRuntime().exec("adb shell pm list packages -3")
                                val input = builderList.inputStream
                                val reader = BufferedReader(InputStreamReader(input))
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    if (line?.indexOf("no devices/emulators found") == -1 && line?.indexOf("device unauthorized.") == -1 && line?.indexOf("kill-server") == -1 && line?.indexOf("server's") == -1 && line?.indexOf("a confirmation dialog") == -1) {
                                        if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully") {
                                        }
                                        arrayList += line?.substring(8).toString()
                                    }
                                }
                                arrayList.sort()
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                                builderList.waitFor()
                            }
                            else -> {
                                arrayList = emptyArray()
                                listModel.removeAllElements()
                                val builderList = Runtime.getRuntime().exec("adb shell pm list packages")
                                val input = builderList.inputStream
                                val reader = BufferedReader(InputStreamReader(input))
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    if (line?.indexOf("no devices/emulators found") == -1 && line?.indexOf("device unauthorized.") == -1 && line?.indexOf("kill-server") == -1 && line?.indexOf("server's") == -1 && line?.indexOf("a confirmation dialog") == -1) {
                                        if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully") {
                                        }
                                        arrayList += line?.substring(8).toString()
                                    }
                                }
                                arrayList.sort()
                                buttonCheck.isEnabled = true
                                for (element in arrayList) {
                                    listModel.addElement(element)
                                    stars.add(element)
                                }
                                builderList.waitFor()
                            }
                        }
                    }

                    override fun done() {
                        buttonCheck.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }



            buttonChooseOne.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
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

                val worker = MyWorker()
                worker.execute()
            }


            buttonChoseAll.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
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

                val worker = MyWorker()
                worker.execute()

            }


            buttonRunCommand.addActionListener {

                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRunCommand.isEnabled = false
                        val builderList = Runtime.getRuntime().exec(textAreaCommandInput.text)
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        var output = ""
                        while (reader.readLine().also { line = it } != null) {
                            output += line
                            output += "\n"
                        }
                        textAreaCommandOutput.text = output
                        builderList.waitFor()
                    }

                    override fun done() {
                        buttonRunCommand.isEnabled = true
                    }

                }

                val worker = MyWorker()
                worker.execute()

            }



            buttonRunCommandFastboot.addActionListener {

                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonRunCommandFastboot.isEnabled = false
                        val builderList = Runtime.getRuntime().exec(textAreaCommandFastbootInput.text)
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        var output = ""
                        while (reader.readLine().also { line = it } != null) {
                            output += line
                            output += "\n"
                        }
                        textAreaCommandFastbootOutput.text = output
                        builderList.waitFor()
                    }

                    override fun done() {
                        buttonRunCommandFastboot.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }


            buttonErase.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonErase.isEnabled = false
                        if (checkBoxPartitionBoot.isSelected) {
                            Runtime.getRuntime().exec("fastboot erase boot").waitFor()
                        }
                        if (checkBoxPartitionSystem.isSelected) {
                            Runtime.getRuntime().exec("fastboot erase system").waitFor()
                        }
                        if (checkBoxPartitionData.isSelected) {
                            Runtime.getRuntime().exec("fastboot erase userdata").waitFor()
                        }
                        if (checkBoxPartitionCache.isSelected) {
                            Runtime.getRuntime().exec("fastboot erase cache").waitFor()
                        }
                        if (checkBoxPartitionRecovery.isSelected) {
                            Runtime.getRuntime().exec("fastboot erase recovery").waitFor()
                        }
                        if (checkBoxPartitionRadio.isSelected) {
                            Runtime.getRuntime().exec("fastboot erase radio").waitFor()
                        }
                    }

                    override fun done() {
                        buttonErase.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }



            buttonChoseRecovery.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
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

                    override fun done() {
                        buttonChoseRecovery.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }


            buttonInstallRecovery.addActionListener {

                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallRecovery.isEnabled = false
                        if (System.getProperty("os.name").indexOf("Windows") != -1) {
                            Runtime.getRuntime().exec("fastboot flash recovery \"$selectedFileAbsolutePath\"").waitFor()
                        } else {
                            Runtime.getRuntime().exec("fastboot flash recovery $selectedFileAbsolutePath").waitFor()
                        }
                    }

                    override fun done() {
                        buttonInstallRecovery.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }


            buttonBootToRecovery.addActionListener {

                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonBootToRecovery.isEnabled = false
                        if (System.getProperty("os.name").indexOf("Windows") != -1) {
                            Runtime.getRuntime().exec("fastboot boot \"$selectedFileAbsolutePath\"").waitFor()
                        } else {
                            Runtime.getRuntime().exec("fastboot boot $selectedFileAbsolutePath").waitFor()
                        }
                    }

                    override fun done() {
                        buttonBootToRecovery.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }

            buttonChooseZip.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonChooseZip.isEnabled = false
                        val choseFile = JFileChooser()
                        val filter = FileNameExtensionFilter("Zip files", "zip")
                        choseFile.fileFilter = filter
                        val chooseDialog = choseFile.showDialog(null, "Select Zip")
                        if (chooseDialog == JFileChooser.APPROVE_OPTION) {
                            selectedZipPath = choseFile.selectedFile.absolutePath
                        }
                    }

                    override fun done() {
                        buttonChooseZip.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()
            }

            buttonInstallZip.addActionListener {
                class MyWorker : SwingWorker<Unit, Int>() {
                    override fun doInBackground() {
                        buttonInstallZip.isEnabled = false
                        Runtime.getRuntime().exec("adb shell twrp sideload")
                        Thread.sleep(3_000)
                        if (System.getProperty("os.name").indexOf("Windows") != -1) {
                            Runtime.getRuntime().exec("adb sideload \"${selectedZipPath}\"").waitFor()
                        } else {
                            Runtime.getRuntime().exec("adb sideload ${selectedZipPath}").waitFor()
                        }
                    }

                    override fun done() {
                        buttonInstallZip.isEnabled = true
                    }
                }

                val worker = MyWorker()
                worker.execute()

            }
            boardInfoPanel.isVisible = true
            softInfoPanel.isVisible = true
            BootloaderFastbootInfoPanel.isVisible = false
            softFastbootInfoPanel.isVisible = false
            StorageFastbootInfoPanel.isVisible = false
            deviceInfoPanel.setBounds(5, 5, 310, 380)
            deviceControlPanel.setBounds(5, 385, 310, 85)
            deviceConnection.setBounds(5, 475, 310, 100)
            labelTCP.isVisible = true
            labelTCPConnection.isVisible = true
            buttonIpConnect.isVisible = true
            textFieldIP.isVisible = true
            labelConnect.isVisible = true
            labelIP.isVisible = true
            frame.isVisible = true
        }
    }
}
