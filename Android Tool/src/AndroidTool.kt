import com.formdev.flatlaf.FlatDarculaLaf
import java.awt.Desktop
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.BufferedReader
import java.io.File
import java.io.FilenameFilter
import java.io.InputStreamReader
import java.net.URI
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.text.MaskFormatter


////DefaultCommands////
fun runUrl(url:String){
    val urlString = URI(url)
    Desktop.getDesktop().browse(urlString)
}
fun start(){
    val builderStart = ProcessBuilder("cmd.exe", "/c", "cd \"$directoryWorking\"")
    val valStart = builderStart.start()
    valStart.waitFor()
}
fun simpleCommand(command:String){
    val builderCommand = ProcessBuilder("cmd.exe", "/c", command)
    val valCommand = builderCommand.start()
    valCommand.waitFor()
}
fun end(){
    val builderEnd = ProcessBuilder("cmd.exe" , "/c", "adb kill-server")
    val valEnd = builderEnd.start()
    valEnd.waitFor()
}
////DefaultCommands////

var arrayList = emptyArray<String>()
val directoryWorking: String = System.getProperty("user.dir")
var selectedDirectoryPath = ""
var selectedFileAbsolutePath = ""
var selectedFilePath = ""
val listModel = DefaultListModel<Any?>()

fun main() {
    start()
    val builderStartAdb = ProcessBuilder("cmd.exe", "/c", "adb devices")
    builderStartAdb.start()

    FlatDarculaLaf.install()
    JFrame.setDefaultLookAndFeelDecorated(true)
    val frame = JFrame("Android Tool")
    frame.setSize(880, 610)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.layout = null
    frame.isResizable = false
    frame.setLocationRelativeTo(null)
    frame.iconImage = Toolkit.getDefaultToolkit().getImage({class AndroidTool() {} }::class.java.getResource("/icon/frameIcon.png"))
    frame.addWindowListener(object : WindowAdapter() { override fun windowClosing(e: WindowEvent) { end() } })


    val adbPanel = JPanel()
    adbPanel.layout = null

    ////////AdbPanel////////

    ////ScrollPane////
    val list = JList(listModel)
    val scrollPane = JScrollPane()
    scrollPane.setViewportView(list)
    scrollPane.setBounds(325, 25, 550, 225)
    adbPanel.add(scrollPane)
    ////ScrollPane////

    ////JFormattedTextField////
    var maskIP = MaskFormatter("###.###.****###")
    var textFieldIP = JFormattedTextField(maskIP)
    textFieldIP.value="192.168"
    textFieldIP.addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
            textFieldIP.caretPosition=8
        }
    })
    textFieldIP.bounds= Rectangle(25,5,100,25)
    adbPanel.add(textFieldIP)
    ////JFormattedTextField////

    ////TextArea////
    val textAreaInput=JTextArea("You can enter app package here")
    textAreaInput.bounds= Rectangle(605,263,267,45)
    adbPanel.add(textAreaInput)
    textAreaInput.addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
            textAreaInput.setText("")
        }
    })

    val textAreaCommandInput=JTextArea("")
    textAreaCommandInput.bounds= Rectangle(6,445,305,65)
    adbPanel.add(textAreaCommandInput)

    val textAreaCommandOutput=JTextArea("")
    textAreaCommandOutput.bounds= Rectangle(315,443,560,98)
    val scroll = JScrollPane(textAreaCommandOutput)
    scroll.setBounds(315,443,560,98)
    adbPanel.add(scroll)
    ////TextArea////

    ////Label////
    val labelIP=JLabel("IP:")
    labelIP.bounds= Rectangle(7,5,15,25)
    labelIP.font = labelIP.font.deriveFont(15.0f)
    adbPanel.add(labelIP)

    val labelConnect=JLabel("")
    labelConnect.bounds= Rectangle(230,5,100,23)
    labelConnect.font = labelIP.font.deriveFont(13.0f)
    adbPanel.add(labelConnect)

    val labelInstallAll=JLabel("Install all APK in the folder")
    labelInstallAll.bounds= Rectangle(7,85,250,50)
    adbPanel.add(labelInstallAll)

    val labelInstallOne=JLabel("Install one APK")
    labelInstallOne.bounds= Rectangle(7,260,250,50)
    adbPanel.add(labelInstallOne)

    val labelSelectedOne=JLabel("Selected: -")
    labelSelectedOne.bounds= Rectangle(7,405,250,20)
    adbPanel.add(labelSelectedOne)

    val labelSelectedAll=JLabel("Selected: -")
    labelSelectedAll.bounds= Rectangle(7,215,250,50)
    adbPanel.add(labelSelectedAll)

    val labelEnterAdbCommand=JLabel("Enter other command")
    labelEnterAdbCommand.bounds= Rectangle(7,425,250,20)
    adbPanel.add(labelEnterAdbCommand)

    val labelOutputAdbCommand=JLabel("Output:")
    labelOutputAdbCommand.bounds= Rectangle(317,425,250,20)
    adbPanel.add(labelOutputAdbCommand)
    ////Label////

    ////CheckBox////
    val checkboxDisabled = JCheckBox("Disabled apps")
    checkboxDisabled.bounds= Rectangle(325,5,100,20)
    adbPanel.add(checkboxDisabled)

    val checkboxSystem = JCheckBox("System apps")
    checkboxSystem.bounds= Rectangle(465,5,100,20)
    adbPanel.add(checkboxSystem)

    val checkboxEnabled = JCheckBox("Enabled apps")
    checkboxEnabled.bounds= Rectangle(615,5,100,20)
    adbPanel.add(checkboxEnabled)

    val checkboxThird = JCheckBox("Third apps")
    checkboxThird.bounds= Rectangle(755,5,100,20)
    adbPanel.add(checkboxThird)

    checkboxDisabled.addItemListener {
        checkboxSystem.isSelected  = false
        checkboxEnabled.isSelected = false
        checkboxThird.isSelected   = false
    }
    checkboxSystem.addItemListener {
        checkboxDisabled.isSelected = false
        checkboxEnabled.isSelected  = false
        checkboxThird.isSelected    = false
    }
    checkboxEnabled.addItemListener {
        checkboxSystem.isSelected   = false
        checkboxDisabled.isSelected = false
        checkboxThird.isSelected    = false
    }
    checkboxThird.addItemListener {
        checkboxSystem.isSelected   = false
        checkboxEnabled.isSelected  = false
        checkboxDisabled.isSelected = false
    }
    ////CheckBox////

    ////Button////
    val buttonIpConnect = JButton("Connect")
    buttonIpConnect.bounds = Rectangle(127, 5, 100, 25)
    buttonIpConnect.isFocusable = false
    adbPanel.add(buttonIpConnect)
    buttonIpConnect.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonIpConnect.isEnabled=false
                simpleCommand("adb kill-server")
                simpleCommand("adb tcpip 5555")
                val builderList = ProcessBuilder("cmd.exe", "/c", "adb connect ${textFieldIP.text}")
                builderList.redirectErrorStream(true)
                val valList = builderList.start()
                val input = valList.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                var line: String?
                var output = ""
                while (reader.readLine().also { line = it } != null) {
                    output += "\n"
                    output += line
                }
                valList.waitFor()
                if(output.indexOf("connected to")!=-1){
                    if(line!="* daemon not running starting now at tcp:5037" && line!="* daemon started successfully")
                        labelConnect.text = "Success"
                    else{
                        labelConnect.text = "Error"
                    }
                }


            }
            override fun done() {
                buttonIpConnect.isEnabled = true
            }
        }
        val worker = MyWorker()
        worker.execute()
    }

    val buttonInstallAll = JButton("Install")
    buttonInstallAll.bounds = Rectangle(5, 120, 285, 50)
    buttonInstallAll.isFocusable = false
    adbPanel.add(buttonInstallAll)
    buttonInstallAll.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonInstallAll.isEnabled = false
                val paths: Array<File>
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
                    simpleCommand("adb install \"$path\"")
                }
            }

            override fun done() {
                buttonInstallAll.isEnabled = true
            }
        }

        val worker = MyWorker()
        worker.execute()

    }

    val buttonInstallOne=JButton("Install")
    buttonInstallOne.bounds= Rectangle(5,295,285,50)
    buttonInstallOne.isFocusable=false
    adbPanel.add(buttonInstallOne)
    buttonInstallOne.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonInstallOne.isEnabled=false
                simpleCommand("adb install \"$selectedFileAbsolutePath\"")
            }
            override fun done() {
                buttonInstallOne.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonDisable=JButton("Disable")
    buttonDisable.bounds= Rectangle(325,320,176,50)
    buttonDisable.isFocusable=false
    adbPanel.add(buttonDisable)
    buttonDisable.addActionListener {
        val textInput:String = if(textAreaInput.text !="")
            textAreaInput.text
        else
            list.selectedValue.toString()

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonDisable.isEnabled=false
                simpleCommand("adb shell pm disable-user --user 0 $textInput")
            }
            override fun done() {
                buttonDisable.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonUninstall=JButton("Uninstall")
    buttonUninstall.bounds= Rectangle(510,320,176,50)
    buttonUninstall.isFocusable=false
    adbPanel.add(buttonUninstall)
    buttonUninstall.addActionListener {
        val textInput:String = if(textAreaInput.text !="")
            textAreaInput.text
        else
            list.selectedValue.toString()

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonUninstall.isEnabled=false
                simpleCommand("adb shell pm uninstall --user 0 $textInput")
            }
            override fun done() {
                buttonUninstall.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonEnable=JButton("Enable")
    buttonEnable.bounds= Rectangle(695,320,180,50)
    buttonEnable.isFocusable=false
    adbPanel.add(buttonEnable)
    buttonEnable.addActionListener {
        val textInput:String = if(textAreaInput.text !="")
            textAreaInput.text
        else
            list.selectedValue.toString()

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonEnable.isEnabled=false
                simpleCommand("adb shell pm enable $textInput")
            }
            override fun done() {
                buttonEnable.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonCheck=JButton("Get list of packages")
    buttonCheck.bounds= Rectangle(325,260,270,50)
    buttonCheck.isFocusable=false
    adbPanel.add(buttonCheck)
    buttonCheck.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonCheck.isEnabled=false
                when {
                    checkboxDisabled.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = ProcessBuilder("cmd.exe", "/c", "adb shell pm list packages -d")
                        builderList.redirectErrorStream(true)
                        val valList = builderList.start()
                        val input = valList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1){
                                if(line!="* daemon not running starting now at tcp:5037" && line!="* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }

                        }
                        arrayList.sort()
                        valList.waitFor()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                    }
                    checkboxSystem.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = ProcessBuilder("cmd.exe", "/c", "adb shell pm list packages -s")
                        builderList.redirectErrorStream(true)
                        val valList = builderList.start()
                        val input = valList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        valList.waitFor()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                    }
                    checkboxEnabled.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = ProcessBuilder("cmd.exe", "/c", "adb shell pm list packages -e")
                        builderList.redirectErrorStream(true)
                        val valList = builderList.start()
                        val input = valList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        valList.waitFor()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                    }
                    checkboxThird.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = ProcessBuilder("cmd.exe", "/c", "adb shell pm list packages -3")
                        builderList.redirectErrorStream(true)
                        val valList = builderList.start()
                        val input = valList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        valList.waitFor()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                    }
                    else -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = ProcessBuilder("cmd.exe", "/c", "adb shell pm list packages")
                        builderList.redirectErrorStream(true)
                        val valList = builderList.start()
                        val input = valList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        valList.waitFor()
                        buttonCheck.isEnabled = true
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                    }
                }
            }
            override fun done() {
                buttonCheck.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()

    }

    val buttonReboot=JButton("Reboot")
    buttonReboot.bounds= Rectangle(5,40,137,25)
    buttonReboot.isFocusable=false
    adbPanel.add(buttonReboot)
    buttonReboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonReboot.isEnabled=false
                simpleCommand("adb reboot")
            }
            override fun done() {
                buttonReboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonRecoveryReboot=JButton("Reboot to Recovery")
    buttonRecoveryReboot.bounds= Rectangle(5,65,137,25)
    buttonRecoveryReboot.isFocusable=false
    adbPanel.add(buttonRecoveryReboot)
    buttonRecoveryReboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRecoveryReboot.isEnabled=false
                simpleCommand("adb reboot recovery")
            }
            override fun done() {
                buttonRecoveryReboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonFastbootReboot=JButton("Reboot to Fastboot")
    buttonFastbootReboot.bounds= Rectangle(145,65,137,25)
    buttonFastbootReboot.isFocusable=false
    adbPanel.add(buttonFastbootReboot)
    buttonFastbootReboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonFastbootReboot.isEnabled=false
                simpleCommand("adb reboot bootloader")
            }
            override fun done() {
                buttonFastbootReboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonPowerOff=JButton("Shutdown")
    buttonPowerOff.bounds= Rectangle(145,40,137,25)
    buttonPowerOff.isFocusable=false
    adbPanel.add(buttonPowerOff)
    buttonPowerOff.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonPowerOff.isEnabled=false
                simpleCommand("adb reboot -p")
            }
            override fun done() {
                buttonPowerOff.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonChooseOne=JButton("Select APK")
    buttonChooseOne.bounds= Rectangle(5,355,285,50)
    buttonChooseOne.isFocusable=false
    adbPanel.add(buttonChooseOne)
    buttonChooseOne.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonChooseOne.isEnabled=false
                val choseFile = JFileChooser()
                val filter = FileNameExtensionFilter("APK Files", "apk")
                choseFile.fileFilter = filter
                val chooseDialog = choseFile.showDialog(null, "Choose APK")
                if(chooseDialog == JFileChooser.APPROVE_OPTION) {
                    selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
                    selectedFilePath = choseFile.selectedFile.path
                    labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
                }
            }
            override fun done() {
                buttonChooseOne.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonChoseAll=JButton("Select Folder")
    buttonChoseAll.bounds= Rectangle(5,180,285,50)
    buttonChoseAll.isFocusable=false
    adbPanel.add(buttonChoseAll)
    buttonChoseAll.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonChoseAll.isEnabled=false

                val choseDirectory = JFileChooser()
                choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
                if(chooseDialog == JFileChooser.APPROVE_OPTION) {
                    selectedDirectoryPath = choseDirectory.selectedFile.path
                    labelSelectedAll.text = "Selected: ${choseDirectory.selectedFile.path}"
                }
            }
            override fun done() {
                buttonChoseAll.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()

    }

    val buttonRunCommand=JButton("Run")
    buttonRunCommand.bounds= Rectangle(173,513,140,25)
    buttonRunCommand.isFocusable=false
    adbPanel.add(buttonRunCommand)
    buttonRunCommand.addActionListener {

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRunCommand.isEnabled=false
                val builderList = ProcessBuilder("cmd.exe", "/c", textAreaCommandInput.text)
                builderList.redirectErrorStream(true)
                val valList = builderList.start()
                val input = valList.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                var line: String?
                var output = ""
                while (reader.readLine().also { line = it } != null) {
                    output += "\n"
                    output += line
                }
                valList.waitFor()
                textAreaCommandOutput.text = output
            }
            override fun done() {
                buttonRunCommand.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()

    }
    ////Button////

    ////////AdbPanel////////

    val fastbootPanel = JPanel()
    fastbootPanel.layout = null

    ////////FastBoot////////

    ////Label////
    val labelOutputFastbootCommand=JLabel("Output:")
    labelOutputFastbootCommand.bounds= Rectangle(317,425,250,20)
    fastbootPanel.add(labelOutputFastbootCommand)

    val labelEnterFastbootCommand=JLabel("Enter other command")
    labelEnterFastbootCommand.bounds= Rectangle(7,425,250,20)
    fastbootPanel.add(labelEnterFastbootCommand)

    val labelInstallRecovery=JLabel("Install or boot recovery")
    labelInstallRecovery.bounds= Rectangle(7,60,250,50)
    fastbootPanel.add(labelInstallRecovery)

    val labelErase=JLabel("Erase partition")
    labelErase.bounds= Rectangle(300,5,250,20)
    fastbootPanel.add(labelErase)
    ////Label////

    ////TextArea////
    val textAreaCommandFastbootInput=JTextArea("")
    textAreaCommandFastbootInput.bounds= Rectangle(6,445,305,65)
    fastbootPanel.add(textAreaCommandFastbootInput)

    val textAreaCommandFastbootOutput=JTextArea("")
    textAreaCommandFastbootOutput.bounds= Rectangle(315,443,560,98)
    val scroll2 = JScrollPane(textAreaCommandFastbootOutput)
    scroll2.setBounds(315,443,560,98)
    fastbootPanel.add(scroll2)
    ////TextArea////

    ////CheckBox////
    val checkboxPartitionSystem = JCheckBox("System")
    checkboxPartitionSystem.bounds= Rectangle(300,30,100,20)
    fastbootPanel.add(checkboxPartitionSystem)

    val checkboxPartitionData = JCheckBox("Data")
    checkboxPartitionData.bounds= Rectangle(300,55,100,20)
    fastbootPanel.add(checkboxPartitionData)

    val checkboxPartitionCache = JCheckBox("Cache")
    checkboxPartitionCache.bounds= Rectangle(300,80,100,20)
    fastbootPanel.add(checkboxPartitionCache)

    val checkboxPartitionRecovery = JCheckBox("Recovery")
    checkboxPartitionRecovery.bounds= Rectangle(300,105,100,20)
    fastbootPanel.add(checkboxPartitionRecovery)

    val checkboxPartitionBoot = JCheckBox("Boot")
    checkboxPartitionBoot.bounds= Rectangle(300,130,100,20)
    fastbootPanel.add(checkboxPartitionBoot)

    val checkboxPartitionRadio = JCheckBox("Radio")
    checkboxPartitionRadio.bounds= Rectangle(300,155,100,20)
    fastbootPanel.add(checkboxPartitionRadio)

    ////CheckBox////

    ////Button////
    val buttonRunCommandFastboot=JButton("Run")
    buttonRunCommandFastboot.bounds= Rectangle(173,513,140,25)
    buttonRunCommandFastboot.isFocusable=false
    fastbootPanel.add(buttonRunCommandFastboot)
    buttonRunCommandFastboot.addActionListener {

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRunCommandFastboot.isEnabled=false
                val builderList = ProcessBuilder("cmd.exe", "/c", textAreaCommandFastbootInput.text)
                builderList.redirectErrorStream(true)
                val valList = builderList.start()
                val input = valList.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                var line: String?
                var output = ""
                while (reader.readLine().also { line = it } != null) {
                    output += "\n"
                    output += line
                }
                valList.waitFor()
                textAreaCommandFastbootOutput.text = output
            }
            override fun done() {
                buttonRunCommandFastboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()

    }

    val buttonErase=JButton("Erase")
    buttonErase.bounds= Rectangle(300,178,137,25)
    buttonErase.isFocusable=false
    fastbootPanel.add(buttonErase)
    buttonErase.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonErase.isEnabled=false
                if(checkboxPartitionBoot.isSelected){
                    simpleCommand("fastboot erase boot")
                }
                if(checkboxPartitionSystem.isSelected){
                    simpleCommand("fastboot erase system")
                }
                if(checkboxPartitionData.isSelected){
                    simpleCommand("fastboot erase userdata")
                }
                if(checkboxPartitionCache.isSelected){
                    simpleCommand("fastboot erase cache")
                }
                if(checkboxPartitionRecovery.isSelected){
                    simpleCommand("fastboot erase recovery")
                }
                if(checkboxPartitionRadio.isSelected){
                    simpleCommand("fastboot erase radio")
                }
            }
            override fun done() {
                buttonErase.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()
    }

    val buttonRebootFastboot=JButton("Reboot")
    buttonRebootFastboot.bounds= Rectangle(5,5,137,25)
    buttonRebootFastboot.isFocusable=false
    fastbootPanel.add(buttonRebootFastboot)
    buttonRebootFastboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRebootFastboot.isEnabled=false
                simpleCommand("fastboot reboot")
            }
            override fun done() {
                buttonRebootFastboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonRecoveryRebootFastboot=JButton("Reboot to Recovery")
    buttonRecoveryRebootFastboot.bounds= Rectangle(80,31,137,25)
    buttonRecoveryRebootFastboot.isFocusable=false
    fastbootPanel.add(buttonRecoveryRebootFastboot)
    buttonRecoveryRebootFastboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRecoveryRebootFastboot.isEnabled=false
                simpleCommand("fastboot oem reboot-recovery")
            }
            override fun done() {
                buttonRecoveryRebootFastboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonFastbootRebootFastboot=JButton("Reboot to Fastboot")
    buttonFastbootRebootFastboot.bounds= Rectangle(145,5,137,25)
    buttonFastbootRebootFastboot.isFocusable=false
    fastbootPanel.add(buttonFastbootRebootFastboot)
    buttonFastbootRebootFastboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonFastbootRebootFastboot.isEnabled=false
                simpleCommand("fastboot reboot-bootloader")
            }
            override fun done() {
                buttonFastbootRebootFastboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonChoseRecovery=JButton("Select Recovery")
    buttonChoseRecovery.bounds= Rectangle(5,95,285,50)
    buttonChoseRecovery.isFocusable=false
    fastbootPanel.add(buttonChoseRecovery)
    buttonChoseRecovery.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonChoseRecovery.isEnabled=false
                val choseFile = JFileChooser()
                val filter = FileNameExtensionFilter("Recovery Files", "img")
                choseFile.fileFilter = filter
                val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
                if(chooseDialog == JFileChooser.APPROVE_OPTION) {
                    selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
                    selectedFilePath = choseFile.selectedFile.path
                    labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
                }
            }
            override fun done() {
                buttonChoseRecovery.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonInstallRecovery = JButton("Install")
    buttonInstallRecovery.bounds= Rectangle(5,153,138,50)
    buttonInstallRecovery.isFocusable=false
    fastbootPanel.add(buttonInstallRecovery)
    buttonInstallRecovery.addActionListener {

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonInstallRecovery.isEnabled=false
                simpleCommand("fastboot flash recovery \"$selectedFileAbsolutePath\"")
            }
            override fun done() {
                buttonInstallRecovery.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()
    }

    val buttonBootToRecovery = JButton("Boot")
    buttonBootToRecovery.bounds= Rectangle(152,153,138,50)
    buttonBootToRecovery.isFocusable=false
    fastbootPanel.add(buttonBootToRecovery)
    buttonBootToRecovery.addActionListener {

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonBootToRecovery.isEnabled=false
                simpleCommand("fastboot boot \"$selectedFileAbsolutePath\"")
            }
            override fun done() {
                buttonBootToRecovery.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()
    }
    ////Button////

    ////////FastBoot////////

    val linksPanel = JPanel()
    linksPanel.layout = null

    ////////LinksPanel////////

    ////ROMs////
    val labelRoms=JLabel("ROMs")
    labelRoms.bounds= Rectangle(220,5,140,30)
    labelRoms.font = labelRoms.font.deriveFont(22.0f)
    linksPanel.add(labelRoms)

    val labelPE=JLabel("Pixel Experience")
    labelPE.bounds= Rectangle(10,45,100,20)
    linksPanel.add(labelPE)

    val buttonPE=JButton("Official Site")
    buttonPE.bounds= Rectangle(10,65,110,25)
    buttonPE.isFocusable=false
    linksPanel.add(buttonPE)
    buttonPE.addActionListener { runUrl("https://download.pixelexperience.org/") }

    val buttonPEDownload=JButton("Download")
    buttonPEDownload.bounds= Rectangle(10,90,110,25)
    buttonPEDownload.isEnabled=false
    linksPanel.add(buttonPEDownload)

    val labelHavocOS=JLabel("Havoc OS")
    labelHavocOS.bounds= Rectangle(10,125,100,20)
    linksPanel.add(labelHavocOS)

    val buttonHavocOS=JButton("Official Site")
    buttonHavocOS.bounds= Rectangle(10,145,110,25)
    buttonHavocOS.isEnabled=false
    linksPanel.add(buttonHavocOS)

    val buttonHavocOSDownload=JButton("Download")
    buttonHavocOSDownload.bounds= Rectangle(10,170,110,25)
    buttonHavocOSDownload.isFocusable=false
    linksPanel.add(buttonHavocOSDownload)
    buttonHavocOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/havoc-os/files/") }

    val labelcrDroid=JLabel("crDroid")
    labelcrDroid.bounds= Rectangle(10,205,100,20)
    linksPanel.add(labelcrDroid)

    val buttoncrDroid=JButton("Official Site")
    buttoncrDroid.bounds= Rectangle(10,225,110,25)
    buttoncrDroid.isFocusable=false
    linksPanel.add(buttoncrDroid)
    buttoncrDroid.addActionListener { runUrl("https://crdroid.net/") }

    val buttoncrDroidDownload=JButton("Download")
    buttoncrDroidDownload.bounds= Rectangle(10,250,110,25)
    buttoncrDroidDownload.isFocusable=false
    linksPanel.add(buttoncrDroidDownload)
    buttoncrDroidDownload.addActionListener { runUrl("https://sourceforge.net/projects/crdroid/files/") }

    val labelLineageOS=JLabel("Lineage OS")
    labelLineageOS.bounds= Rectangle(10,285,100,20)
    linksPanel.add(labelLineageOS)

    val buttonLineageOS=JButton("Official Site")
    buttonLineageOS.bounds= Rectangle(10,305,110,25)
    buttonLineageOS.isFocusable=false
    linksPanel.add(buttonLineageOS)
    buttonLineageOS.addActionListener { runUrl("https://lineageos.org/") }

    val buttonLineageOSDownload=JButton("Download")
    buttonLineageOSDownload.bounds= Rectangle(10,330,110,25)
    buttonLineageOSDownload.isFocusable=false
    linksPanel.add(buttonLineageOSDownload)
    buttonLineageOSDownload.addActionListener { runUrl("https://download.lineageos.org/") }

    val labelParanoid=JLabel("Paranoid Android")
    labelParanoid.bounds= Rectangle(10,365,100,20)
    linksPanel.add(labelParanoid)

    val buttonParanoid=JButton("Official Site")
    buttonParanoid.bounds= Rectangle(10,385,110,25)
    buttonParanoid.isFocusable=false
    linksPanel.add(buttonParanoid)
    buttonParanoid.addActionListener { runUrl("https://paranoidandroid.co/") }

    val buttonParanoidDownload=JButton("Download")
    buttonParanoidDownload.bounds= Rectangle(10,410,110,25)
    buttonParanoidDownload.isEnabled=false
    linksPanel.add(buttonParanoidDownload)

    val labelDerpFest=JLabel("AOSiP DerpFest")
    labelDerpFest.bounds= Rectangle(10,445,100,20)
    linksPanel.add(labelDerpFest)

    val buttonDerpFest=JButton("Official Site")
    buttonDerpFest.bounds= Rectangle(10,465,110,25)
    buttonDerpFest.isFocusable=false
    linksPanel.add(buttonDerpFest)
    buttonDerpFest.addActionListener { runUrl("https://derpfest.org/") }

    val buttonDerpFestDownload=JButton("Download")
    buttonDerpFestDownload.bounds= Rectangle(10,490,110,25)
    buttonDerpFestDownload.isEnabled=false
    linksPanel.add(buttonDerpFestDownload)

    val labelZenX=JLabel("ZenX-OS")
    labelZenX.bounds= Rectangle(130,45,100,20)
    linksPanel.add(labelZenX)

    val buttonZenX=JButton("Official Site")
    buttonZenX.bounds= Rectangle(130,65,110,25)
    buttonZenX.isEnabled=false
    linksPanel.add(buttonZenX)

    val buttonZenXDownload=JButton("Download")
    buttonZenXDownload.bounds= Rectangle(130,90,110,25)
    buttonZenXDownload.isFocusable=false
    linksPanel.add(buttonZenXDownload)
    buttonZenXDownload.addActionListener { runUrl("https://sourceforge.net/projects/zenx-os/files/") }
    val labelEvolutionX=JLabel("Evolution X")
    labelEvolutionX.bounds= Rectangle(130,125,100,20)
    linksPanel.add(labelEvolutionX)

    val buttonEvolutionX=JButton("Official Site")
    buttonEvolutionX.bounds= Rectangle(130,145,110,25)
    buttonEvolutionX.isFocusable=false
    linksPanel.add(buttonEvolutionX)
    buttonEvolutionX.addActionListener { runUrl("https://evolution-x.org/#/") }

    val buttonEvolutionXDownload=JButton("Download")
    buttonEvolutionXDownload.bounds= Rectangle(130,170,110,25)
    buttonEvolutionXDownload.isFocusable=false
    linksPanel.add(buttonEvolutionXDownload)
    buttonEvolutionXDownload.addActionListener { runUrl("https://sourceforge.net/projects/evolution-x/files/") }

    val labelCorvusOS=JLabel("Corvus OS")
    labelCorvusOS.bounds= Rectangle(130,205,100,20)
    linksPanel.add(labelCorvusOS)

    val buttonCorvusOS=JButton("Official Site")
    buttonCorvusOS.bounds= Rectangle(130,225,110,25)
    buttonCorvusOS.isEnabled=false
    linksPanel.add(buttonCorvusOS)

    val buttonCorvusOSDownload=JButton("Download")
    buttonCorvusOSDownload.bounds= Rectangle(130,250,110,25)
    buttonCorvusOSDownload.isFocusable=false
    linksPanel.add(buttonCorvusOSDownload)
    buttonCorvusOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/corvus-os/files/") }

    val labelIon=JLabel("Ion OS")
    labelIon.bounds= Rectangle(130,285,100,20)
    linksPanel.add(labelIon)

    val buttonIon=JButton("Official Site")
    buttonIon.bounds= Rectangle(130,305,110,25)
    buttonIon.isEnabled=false
    linksPanel.add(buttonIon)

    val buttonIonDownload=JButton("Download")
    buttonIonDownload.bounds= Rectangle(130,330,110,25)
    buttonIonDownload.isFocusable=false
    linksPanel.add(buttonIonDownload)
    buttonIonDownload.addActionListener { runUrl("https://sourceforge.net/projects/i-o-n/files/") }

    val labelMoKee=JLabel("MoKee")
    labelMoKee.bounds= Rectangle(130,365,100,20)
    linksPanel.add(labelMoKee)

    val buttonMoKee=JButton("Official Site")
    buttonMoKee.bounds= Rectangle(130,385,110,25)
    buttonMoKee.isFocusable=false
    linksPanel.add(buttonMoKee)
    buttonMoKee.addActionListener { runUrl("https://www.mokeedev.com/en/") }

    val buttonMoKeeDownload=JButton("Download")
    buttonMoKeeDownload.bounds= Rectangle(130,410,110,25)
    buttonMoKeeDownload.isFocusable=false
    linksPanel.add(buttonMoKeeDownload)
    buttonMoKeeDownload.addActionListener { runUrl("https://download.mokeedev.com/") }

    val labelBlissOS=JLabel("Bliss OS")
    labelBlissOS.bounds= Rectangle(130,445,100,20)
    linksPanel.add(labelBlissOS)

    val buttonBlissOS=JButton("Official Site")
    buttonBlissOS.bounds= Rectangle(130,465,110,25)
    buttonBlissOS.isFocusable=false
    linksPanel.add(buttonBlissOS)
    buttonBlissOS.addActionListener { runUrl("https://blissroms.com/") }

    val buttonBlissOSDownload=JButton("Download")
    buttonBlissOSDownload.bounds= Rectangle(130,490,110,25)
    buttonBlissOSDownload.isFocusable=false
    linksPanel.add(buttonBlissOSDownload)
    buttonBlissOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/blissroms/files/") }

    val labelMSMXtended=JLabel("MSM-Xtended")
    labelMSMXtended.bounds= Rectangle(250,45,100,20)
    linksPanel.add(labelMSMXtended)

    val buttonMSMXtended=JButton("Official Site")
    buttonMSMXtended.bounds= Rectangle(250,65,110,25)
    buttonMSMXtended.isFocusable=false
    linksPanel.add(buttonMSMXtended)
    buttonMSMXtended.addActionListener { runUrl("https://msmxtended.me/") }

    val buttonMSMXtendedDownload=JButton("Download")
    buttonMSMXtendedDownload.bounds= Rectangle(250,90,110,25)
    buttonMSMXtendedDownload.isFocusable=false
    linksPanel.add(buttonMSMXtendedDownload)
    buttonMSMXtendedDownload.addActionListener { runUrl("https://sourceforge.net/projects/xtended/files/") }

    val labelAOSPExtended=JLabel("AOSP Extended")
    labelAOSPExtended.bounds= Rectangle(250,125,100,20)
    linksPanel.add(labelAOSPExtended)

    val buttonAOSPExtended=JButton("Official Site")
    buttonAOSPExtended.bounds= Rectangle(250,145,110,25)
    buttonAOSPExtended.isFocusable=false
    linksPanel.add(buttonAOSPExtended)
    buttonAOSPExtended.addActionListener { runUrl("https://www.aospextended.com/") }

    val buttonAOSPExtendedDownload=JButton("Download")
    buttonAOSPExtendedDownload.bounds= Rectangle(250,170,110,25)
    buttonAOSPExtendedDownload.isFocusable=false
    linksPanel.add(buttonAOSPExtendedDownload)
    buttonAOSPExtendedDownload.addActionListener { runUrl("https://downloads.aospextended.com/") }

    val labelAICPRom=JLabel("AICP")
    labelAICPRom.bounds= Rectangle(250,205,100,20)
    linksPanel.add(labelAICPRom)

    val buttonAICPRom=JButton("Official Site")
    buttonAICPRom.bounds= Rectangle(250,225,110,25)
    buttonAICPRom.isFocusable=false
    linksPanel.add(buttonAICPRom)
    buttonAICPRom.addActionListener { runUrl("https://dwnld.aicp-rom.com/") }

    val buttonAICPRomDownload=JButton("Download")
    buttonAICPRomDownload.bounds= Rectangle(250,250,110,25)
    buttonAICPRomDownload.isEnabled=false
    linksPanel.add(buttonAICPRomDownload)

    val labelArrowOS=JLabel("Arrow OS")
    labelArrowOS.bounds= Rectangle(250,285,100,20)
    linksPanel.add(labelArrowOS)

    val buttonArrowOS=JButton("Official Site")
    buttonArrowOS.bounds= Rectangle(250,305,110,25)
    buttonArrowOS.isFocusable=false
    linksPanel.add(buttonArrowOS)
    buttonArrowOS.addActionListener { runUrl("https://arrowos.net/") }

    val buttonArrowOSDownload=JButton("Download")
    buttonArrowOSDownload.bounds= Rectangle(250,330,110,25)
    buttonArrowOSDownload.isFocusable=false
    linksPanel.add(buttonArrowOSDownload)
    buttonArrowOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/arrow-os/files/") }

    val labelCarbonROM=JLabel("CarbonROM")
    labelCarbonROM.bounds= Rectangle(250,365,100,20)
    linksPanel.add(labelCarbonROM)

    val buttonCarbonROM=JButton("Official Site")
    buttonCarbonROM.bounds= Rectangle(250,385,110,25)
    buttonCarbonROM.isFocusable=false
    linksPanel.add(buttonCarbonROM)
    buttonCarbonROM.addActionListener { runUrl("https://carbonrom.org/") }

    val buttonCarbonROMDownload=JButton("Download")
    buttonCarbonROMDownload.bounds= Rectangle(250,410,110,25)
    buttonCarbonROMDownload.isFocusable=false
    linksPanel.add(buttonCarbonROMDownload)
    buttonCarbonROMDownload.addActionListener { runUrl("https://get.carbonrom.org/") }

    val labelRevengeOS=JLabel("Revenge OS")
    labelRevengeOS.bounds= Rectangle(250,445,100,20)
    linksPanel.add(labelRevengeOS)

    val buttonRevengeOS=JButton("Official Site")
    buttonRevengeOS.bounds= Rectangle(250,465,110,25)
    buttonRevengeOS.isFocusable=false
    linksPanel.add(buttonRevengeOS)
    buttonRevengeOS.addActionListener { runUrl("http://revengeos.com") }

    val buttonRevengeOSDownload=JButton("Download")
    buttonRevengeOSDownload.bounds= Rectangle(250,490,110,25)
    buttonRevengeOSDownload.isFocusable=false
    linksPanel.add(buttonRevengeOSDownload)
    buttonRevengeOSDownload.addActionListener { runUrl("https://osdn.net/projects/revengeos/storage/") }

    val labelXiaomiEU=JLabel("Xiaomi EU")
    labelXiaomiEU.bounds= Rectangle(370,45,100,20)
    linksPanel.add(labelXiaomiEU)

    val buttonXiaomiEU=JButton("Official Site")
    buttonXiaomiEU.bounds= Rectangle(370,65,110,25)
    buttonXiaomiEU.isFocusable=false
    linksPanel.add(buttonXiaomiEU)
    buttonXiaomiEU.addActionListener { runUrl("https://xiaomi.eu/community/") }

    val buttonXiaomiEUDownload=JButton("Download")
    buttonXiaomiEUDownload.bounds= Rectangle(370,90,110,25)
    buttonXiaomiEUDownload.isFocusable=false
    linksPanel.add(buttonXiaomiEUDownload)
    buttonXiaomiEUDownload.addActionListener { runUrl("https://sourceforge.net/projects/xiaomi-eu-multilang-miui-roms/files/xiaomi.eu/") }

    val labelMasik=JLabel("Masik")
    labelMasik.bounds= Rectangle(370,125,100,20)
    linksPanel.add(labelMasik)

    val buttonMasik=JButton("Official Site")
    buttonMasik.bounds= Rectangle(370,145,110,25)
    buttonMasik.isFocusable=false
    linksPanel.add(buttonMasik)
    buttonMasik.addActionListener { runUrl("https://sites.google.com/view/masikupdates") }

    val buttonMasikDownload=JButton("Download")
    buttonMasikDownload.bounds= Rectangle(370,170,110,25)
    buttonMasikDownload.isFocusable=false
    linksPanel.add(buttonMasikDownload)
    buttonMasikDownload.addActionListener { runUrl("https://sites.google.com/view/masikupdates/Update") }

    val labelMiRoom=JLabel("MiRoom")
    labelMiRoom.bounds= Rectangle(370,205,100,20)
    linksPanel.add(labelMiRoom)

    val buttonMiRoom=JButton("Official Site")
    buttonMiRoom.bounds= Rectangle(370,225,110,25)
    buttonMiRoom.isFocusable=false
    linksPanel.add(buttonMiRoom)
    buttonMiRoom.addActionListener { runUrl("https://portal.mi-room.ru/roms/") }

    val buttonMiRoomDownload=JButton("Download")
    buttonMiRoomDownload.bounds= Rectangle(370,250,110,25)
    buttonMiRoomDownload.isFocusable=false
    linksPanel.add(buttonMiRoomDownload)
    buttonMiRoomDownload.addActionListener { runUrl("https://sourceforge.net/projects/miroom/files/") }

    val labelMiuiMix=JLabel("MiuiMix")
    labelMiuiMix.bounds= Rectangle(370,285,100,20)
    linksPanel.add(labelMiuiMix)

    val buttonMiuiMix=JButton("Official Site")
    buttonMiuiMix.bounds= Rectangle(370,305,110,25)
    buttonMiuiMix.isEnabled=false
    linksPanel.add(buttonMiuiMix)

    val buttonMiuiMixDownload=JButton("Download")
    buttonMiuiMixDownload.bounds= Rectangle(370,330,110,25)
    buttonMiuiMixDownload.isFocusable=false
    linksPanel.add(buttonMiuiMixDownload)
    buttonMiuiMixDownload.addActionListener { runUrl("https://t.me/s/MiuiMix") }

    val labelRevOS=JLabel("RevOS")
    labelRevOS.bounds= Rectangle(370,365,100,20)
    linksPanel.add(labelRevOS)

    val buttonRevOS=JButton("Official Site")
    buttonRevOS.bounds= Rectangle(370,385,110,25)
    buttonRevOS.isFocusable=false
    linksPanel.add(buttonRevOS)
    buttonRevOS.addActionListener { runUrl("https://revtechs.me/") }

    val buttonRevOSDownload=JButton("Download")
    buttonRevOSDownload.bounds= Rectangle(370,410,110,25)
    buttonRevOSDownload.isFocusable=false
    linksPanel.add(buttonRevOSDownload)
    buttonRevOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/revos/files/") }

    val labelMiGlobe=JLabel("Mi-Globe")
    labelMiGlobe.bounds= Rectangle(370,445,100,20)
    linksPanel.add(labelMiGlobe)

    val buttonMiGlobe=JButton("Official Site")
    buttonMiGlobe.bounds= Rectangle(370,465,110,25)
    buttonMiGlobe.isFocusable=false
    linksPanel.add(buttonMiGlobe)
    buttonMiGlobe.addActionListener { runUrl("https://mi-globe.com/miui-firmware-rom-builder/") }

    val buttonMiGlobeDownload=JButton("Download")
    buttonMiGlobeDownload.bounds= Rectangle(370,490,110,25)
    buttonMiGlobeDownload.isEnabled=false
    linksPanel.add(buttonMiGlobeDownload)
    ////ROMs////

    ////GoogleApps////
    val labelGapps=JLabel("Google Apps")
    labelGapps.bounds= Rectangle(620,5,140,30)
    labelGapps.font = labelGapps.font.deriveFont(22.0f)
    linksPanel.add(labelGapps)

    val labelOpenGapps=JLabel("Open GApps")
    labelOpenGapps.bounds= Rectangle(630,45,100,20)
    linksPanel.add(labelOpenGapps)

    val buttonOpenGapps=JButton("Official Site")
    buttonOpenGapps.bounds= Rectangle(630,65,110,25)
    buttonOpenGapps.isFocusable=false
    linksPanel.add(buttonOpenGapps)
    buttonOpenGapps.addActionListener { runUrl("https://opengapps.org/") }

    val buttonOpenGappsDownload=JButton("Download")
    buttonOpenGappsDownload.bounds= Rectangle(630,90,110,25)
    buttonOpenGappsDownload.isFocusable=false
    linksPanel.add(buttonOpenGappsDownload)

    val labelBiTGApps=JLabel("BiTGApps")
    labelBiTGApps.bounds= Rectangle(630,125,100,20)
    linksPanel.add(labelBiTGApps)

    val buttonBiTGApps=JButton("Official Site")
    buttonBiTGApps.bounds= Rectangle(630,145,110,25)
    buttonBiTGApps.isFocusable=false
    linksPanel.add(buttonBiTGApps)

    val buttonBiTGAppsDownload=JButton("Download")
    buttonBiTGAppsDownload.bounds= Rectangle(630,170,110,25)
    buttonBiTGAppsDownload.isFocusable=false
    linksPanel.add(buttonBiTGAppsDownload)
    buttonBiTGAppsDownload.addActionListener { runUrl("https://bitgapps.cf/") }

    val labelFlameGApps=JLabel("FlameGApps")
    labelFlameGApps.bounds= Rectangle(630,205,100,20)
    linksPanel.add(labelFlameGApps)

    val buttonFlameGApps=JButton("Official Site")
    buttonFlameGApps.bounds= Rectangle(630,225,110,25)
    buttonFlameGApps.isFocusable=false
    linksPanel.add(buttonFlameGApps)
    buttonFlameGApps.addActionListener { runUrl("https://flamegapps.github.io/") }

    val buttonFlameGAppsDownload=JButton("Download")
    buttonFlameGAppsDownload.bounds= Rectangle(630,250,110,25)
    buttonFlameGAppsDownload.isFocusable=false
    linksPanel.add(buttonFlameGAppsDownload)
    buttonFlameGAppsDownload.addActionListener { runUrl("https://sourceforge.net/projects/flamegapps/files/") }

    val labelNikGApps=JLabel("NikGApps")
    labelNikGApps.bounds= Rectangle(630,285,100,20)
    linksPanel.add(labelNikGApps)

    val buttonNikGApps=JButton("Official Site")
    buttonNikGApps.bounds= Rectangle(630,305,110,25)
    buttonNikGApps.isFocusable=false
    linksPanel.add(buttonNikGApps)
    buttonNikGApps.addActionListener { runUrl("https://nikgapps.com/") }

    val buttonNikGAppsDownload=JButton("Download")
    buttonNikGAppsDownload.bounds= Rectangle(630,330,110,25)
    buttonNikGAppsDownload.isFocusable=false
    linksPanel.add(buttonNikGAppsDownload)
    buttonNikGAppsDownload.addActionListener { runUrl("https://sourceforge.net/projects/nikgapps/files/Releases/") }
    ////GoogleApps////

    ////Recovery////
    val labelRecovery=JLabel("Recovery")
    labelRecovery.bounds= Rectangle(508,5,140,30)
    labelRecovery.font = labelGapps.font.deriveFont(22.0f)
    linksPanel.add(labelRecovery)

    val labelTwrp=JLabel("TWRP")
    labelTwrp.bounds= Rectangle(500,45,100,20)
    linksPanel.add(labelTwrp)

    val buttonTwrp=JButton("Official Site")
    buttonTwrp.bounds= Rectangle(500,65,110,25)
    buttonTwrp.isFocusable=false
    linksPanel.add(buttonTwrp)
    buttonTwrp.addActionListener { runUrl("https://twrp.me/") }

    val buttonTwrpDownload=JButton("Download")
    buttonTwrpDownload.bounds= Rectangle(500,90,110,25)
    buttonTwrpDownload.isEnabled=false
    linksPanel.add(buttonTwrpDownload)

    val labelOrangeFox=JLabel("OrangeFox")
    labelOrangeFox.bounds= Rectangle(500,125,100,20)
    linksPanel.add(labelOrangeFox)

    val buttonOrangeFox=JButton("Official Site")
    buttonOrangeFox.bounds= Rectangle(500,145,110,25)
    buttonOrangeFox.isFocusable=false
    linksPanel.add(buttonOrangeFox)
    buttonOrangeFox.addActionListener { runUrl("https://wiki.orangefox.tech/en/home") }

    val buttonOrangeFoxDownload=JButton("Download")
    buttonOrangeFoxDownload.bounds= Rectangle(500,170,110,25)
    buttonOrangeFoxDownload.isFocusable=false
    linksPanel.add(buttonOrangeFoxDownload)
    buttonOrangeFoxDownload.addActionListener { runUrl("https://sourceforge.net/projects/orangefox/files/") }

    val labelSkyHawk=JLabel("SkyHawk")
    labelSkyHawk.bounds= Rectangle(500,205,100,20)
    linksPanel.add(labelSkyHawk)

    val buttonSkyHawk=JButton("Official Site")
    buttonSkyHawk.bounds= Rectangle(500,225,110,25)
    buttonSkyHawk.isFocusable=false
    linksPanel.add(buttonSkyHawk)
    buttonSkyHawk.addActionListener { runUrl("https://skyhawk-recovery-project.github.io/#/") }

    val buttonSkyHawkDownload=JButton("Download")
    buttonSkyHawkDownload.bounds= Rectangle(500,250,110,25)
    buttonSkyHawkDownload.isFocusable=false
    linksPanel.add(buttonSkyHawkDownload)
    buttonSkyHawkDownload.addActionListener { runUrl("https://sourceforge.net/projects/shrp/files/") }

    val labelPitchBlack=JLabel("PitchBlack")
    labelPitchBlack.bounds= Rectangle(500,285,100,20)
    linksPanel.add(labelPitchBlack)

    val buttonPitchBlack=JButton("Official Site")
    buttonPitchBlack.bounds= Rectangle(500,305,110,25)
    buttonPitchBlack.isFocusable=false
    linksPanel.add(buttonPitchBlack)
    buttonPitchBlack.addActionListener { runUrl("https://pbrp.ml/") }

    val buttonPitchBlackDownload=JButton("Download")
    buttonPitchBlackDownload.bounds= Rectangle(500,330,110,25)
    buttonPitchBlackDownload.isFocusable=false
    linksPanel.add(buttonPitchBlackDownload)
    buttonPitchBlackDownload.addActionListener { runUrl("https://sourceforge.net/projects/pbrp/files/") }
    ////Recovery////

    ////Other////
    val labelOther=JLabel("Other")
    labelOther.bounds= Rectangle(780,5,140,30)
    labelOther.font = labelGapps.font.deriveFont(22.0f)
    linksPanel.add(labelOther)

    val labelMagisk=JLabel("Root")
    labelMagisk.bounds= Rectangle(760,45,100,20)
    linksPanel.add(labelMagisk)

    val buttonMagisk=JButton("Magisk")
    buttonMagisk.bounds= Rectangle(760,65,110,25)
    buttonMagisk.isFocusable=false
    linksPanel.add(buttonMagisk)
    buttonMagisk.addActionListener { runUrl("https://github.com/topjohnwu/Magisk/releases/") }

    val buttonSuperSU=JButton("Super SU")
    buttonSuperSU.bounds= Rectangle(760,90,110,25)
    buttonSuperSU.isFocusable=false
    linksPanel.add(buttonSuperSU)
    buttonSuperSU.addActionListener { runUrl("https://download.chainfire.eu/1220/SuperSU/SR5-SuperSU-v2.82-SR5-20171001224502.zip") }

    val labelGcam=JLabel("Google Camera")
    labelGcam.bounds= Rectangle(760,125,100,20)
    linksPanel.add(labelGcam)

    val buttonGcam=JButton("Download")
    buttonGcam.bounds= Rectangle(760,145,110,25)
    buttonGcam.isFocusable=false
    linksPanel.add(buttonGcam)
    buttonGcam.addActionListener { runUrl("https://www.celsoazevedo.com/files/android/google-camera/") }

    val buttonChecker=JButton("2API Checker")
    buttonChecker.bounds= Rectangle(760,170,110,25)
    buttonChecker.isFocusable=false
    linksPanel.add(buttonChecker)
    buttonChecker.addActionListener { runUrl("https://www.apkmirror.com/apk/march-media-labs/camera2-api-probe/") }

    val labelANX=JLabel("ANX Camera")
    labelANX.bounds= Rectangle(760,205,100,20)
    linksPanel.add(labelANX)

    val buttonANX=JButton("Official Site")
    buttonANX.bounds= Rectangle(760,225,110,25)
    buttonANX.isFocusable=false
    linksPanel.add(buttonANX)
    buttonANX.addActionListener { runUrl("https://camera.aeonax.com/") }

    val buttonANXDownload=JButton("Download")
    buttonANXDownload.bounds= Rectangle(760,250,110,25)
    buttonANXDownload.isFocusable=false
    linksPanel.add(buttonANXDownload)
    buttonANXDownload.addActionListener { runUrl("https://sourceforge.net/projects/anxcamera/files/") }
    ////Other////

    ////////LinksPanel////////

    val tabbedpane = JTabbedPane()
    tabbedpane.setBounds(0,0,880,580)
    tabbedpane.add("ADB", adbPanel)
    tabbedpane.add("Fastboot", fastbootPanel)
    tabbedpane.add("Links", linksPanel)

    frame.add(tabbedpane)
    frame.isVisible=true
}


