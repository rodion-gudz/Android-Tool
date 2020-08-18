import com.formdev.flatlaf.FlatDarculaLaf
import java.awt.Desktop
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.net.URI
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.text.MaskFormatter


////DefaultCommands////
fun runUrl(url:String){
    val urlString = URI(url)
    Desktop.getDesktop().browse(urlString)
}
////DefaultCommands////

var arrayList = emptyArray<String>()
var selectedDirectoryPath = ""
var selectedFileAbsolutePath = ""
var selectedFileSaveAbsolutePath = ""
var selectedFilePath = ""
var selectedZipPath = ""
val listModel = DefaultListModel<Any?>()
val listModelLogs = DefaultListModel<Any?>()

fun main() {
    Runtime.getRuntime().exec( "adb devices")
    FlatDarculaLaf.install()
    JFrame.setDefaultLookAndFeelDecorated(true)
    val frame = JFrame("Android Tool")
    frame.setSize(880, 610)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.layout = null
    frame.isResizable = false
    frame.setLocationRelativeTo(null)
    frame.iconImage = Toolkit.getDefaultToolkit().getImage({class AndroidTool() {} }::class.java.getResource("/icon/frameIcon.png"))
    frame.addWindowListener(object : WindowAdapter() { override fun windowClosing(e: WindowEvent) { Runtime.getRuntime().exec("adb kill-server") } })

    val adbPanel = JPanel()
    adbPanel.layout = null

    ////////AdbPanel////////

    ////ScrollPane////
    val list = JList(listModel)
    val scrollPane = JScrollPane()
    scrollPane.setViewportView(list)
    scrollPane.setBounds(328, 25, 550, 225)
    adbPanel.add(scrollPane)
    ////ScrollPane////

    ////JFormattedTextField////
    val maskIP = MaskFormatter("###.###.****###")
    val textFieldIP = JFormattedTextField(maskIP)
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
    textAreaInput.bounds= Rectangle(608,263,267,45)
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
    labelIP.bounds= Rectangle(7,5,20,25)
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

    val groupApps = ButtonGroup()

    val radioButtonAll = JRadioButton("All apps",true)
    radioButtonAll.bounds= Rectangle(328,5,80,20)
    adbPanel.add(radioButtonAll)
    groupApps.add(radioButtonAll)

    val radioButtonDisabled = JRadioButton("Disabled apps",false)
    radioButtonDisabled.bounds= Rectangle(418,5,120,20)
    adbPanel.add(radioButtonDisabled)
    groupApps.add(radioButtonDisabled)

    val radioButtonSystem = JRadioButton("System apps",false)
    radioButtonSystem.bounds= Rectangle(538,5,120,20)
    adbPanel.add(radioButtonSystem)
    groupApps.add(radioButtonSystem)

    val radioButtonEnabled = JRadioButton("Enabled apps",false)
    radioButtonEnabled.bounds= Rectangle(658,5,100,20)
    adbPanel.add(radioButtonEnabled)
    groupApps.add(radioButtonEnabled)

    val radioButtonThird = JRadioButton("Third apps",false)
    radioButtonThird.bounds= Rectangle(778,5,120,20)
    adbPanel.add(radioButtonThird)
    groupApps.add(radioButtonThird)

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
                Runtime.getRuntime().exec("adb kill-server").waitFor()
                val builderList = Runtime.getRuntime().exec( "adb connect ${textFieldIP.text}")
                val input = builderList.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                var line: String?
                var output = ""
                while (reader.readLine().also { line = it } != null) {
                    output += "\n"
                    output += line
                }
                if(output.indexOf("connected to")!=-1){
                    if(line!="* daemon not running starting now at tcp:5037" && line!="* daemon started successfully")
                        labelConnect.text = "Success"
                    else{
                        labelConnect.text = "Error"
                    }
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
                    if(System.getProperty("os.name").indexOf("Windows")!=-1){
                        Runtime.getRuntime().exec("adb install \"$path\"").waitFor()
                    }else{
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

    val buttonInstallOne=JButton("Install")
    buttonInstallOne.bounds= Rectangle(5,295,285,50)
    buttonInstallOne.isFocusable=false
    adbPanel.add(buttonInstallOne)
    buttonInstallOne.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonInstallOne.isEnabled=false
                if(System.getProperty("os.name").indexOf("Windows")!=-1){
                    Runtime.getRuntime().exec("adb install \"$selectedFileAbsolutePath\"").waitFor()
                }else{
                    Runtime.getRuntime().exec("adb install $selectedFileAbsolutePath").waitFor()
                }
            }
            override fun done() {
                buttonInstallOne.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonDisable=JButton("Disable")
    buttonDisable.bounds= Rectangle(328,320,176,50)
    buttonDisable.isFocusable=false
    adbPanel.add(buttonDisable)
    buttonDisable.addActionListener {
        val textInput:String = if(textAreaInput.text !="You can enter app package here" && textAreaInput.text !="")
            textAreaInput.text
        else
            list.selectedValue.toString()

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonDisable.isEnabled=false
                Runtime.getRuntime().exec("adb shell pm disable-user --user 0 $textInput").waitFor()
            }
            override fun done() {
                buttonDisable.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonUninstall=JButton("Uninstall")
    buttonUninstall.bounds= Rectangle(513,320,176,50)
    buttonUninstall.isFocusable=false
    adbPanel.add(buttonUninstall)
    buttonUninstall.addActionListener {
        val textInput:String = if(textAreaInput.text !="You can enter app package here" && textAreaInput.text !="")
            textAreaInput.text
        else
            list.selectedValue.toString()

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonUninstall.isEnabled=false
                Runtime.getRuntime().exec("adb shell pm uninstall --user 0 $textInput").waitFor()
            }
            override fun done() {
                buttonUninstall.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonEnable=JButton("Enable")
    buttonEnable.bounds= Rectangle(698,320,180,50)
    buttonEnable.isFocusable=false
    adbPanel.add(buttonEnable)
    buttonEnable.addActionListener {
        val textInput:String = if(textAreaInput.text !="You can enter app package here" && textAreaInput.text !="")
            textAreaInput.text
        else
            list.selectedValue.toString()

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonEnable.isEnabled=false
                Runtime.getRuntime().exec("adb shell pm enable $textInput").waitFor()
            }
            override fun done() {
                buttonEnable.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }

    val buttonCheck=JButton("Get list of packages")
    buttonCheck.bounds= Rectangle(328,260,270,50)
    buttonCheck.isFocusable=false
    adbPanel.add(buttonCheck)
    buttonCheck.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonCheck.isEnabled=false
                when {
                    radioButtonDisabled.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = Runtime.getRuntime().exec("adb shell pm list packages -d")
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1){
                                if(line!="* daemon not running starting now at tcp:5037" && line!="* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }

                        }
                        arrayList.sort()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                        builderList.waitFor()
                    }
                    radioButtonSystem.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = Runtime.getRuntime().exec( "adb shell pm list packages -s")
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                        builderList.waitFor()
                    }
                    radioButtonEnabled.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = Runtime.getRuntime().exec( "adb shell pm list packages -e")
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                        builderList.waitFor()
                    }
                    radioButtonThird.isSelected -> {
                        arrayList = emptyArray()
                        listModel.removeAllElements()
                        val builderList = Runtime.getRuntime().exec( "adb shell pm list packages -3")
                        val input = builderList.inputStream
                        val reader = BufferedReader(InputStreamReader(input))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        for(element in arrayList){
                            listModel.addElement(element)
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
                            if(line?.indexOf("no devices/emulators found")==-1 && line?.indexOf("device unauthorized.")==-1 && line?.indexOf("kill-server")==-1 && line?.indexOf("server's")==-1 && line?.indexOf("a confirmation dialog")==-1) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully")
                                    arrayList += line?.substring(8).toString()
                            }
                        }
                        arrayList.sort()
                        buttonCheck.isEnabled = true
                        for(element in arrayList){
                            listModel.addElement(element)
                        }
                        builderList.waitFor()
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
    buttonReboot.bounds= Rectangle(5,40,120,25)
    buttonReboot.isFocusable=false
    adbPanel.add(buttonReboot)
    buttonReboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonReboot.isEnabled=false
                Runtime.getRuntime().exec("adb reboot").waitFor()
            }
            override fun done() {
                buttonReboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonRecoveryReboot=JButton("Reboot to Recovery")
    buttonRecoveryReboot.bounds= Rectangle(126,40,163,25)
    buttonRecoveryReboot.isFocusable=false
    adbPanel.add(buttonRecoveryReboot)
    buttonRecoveryReboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRecoveryReboot.isEnabled=false
                Runtime.getRuntime().exec("adb reboot recovery").waitFor()
            }
            override fun done() {
                buttonRecoveryReboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonFastbootReboot=JButton("Reboot to Fastboot")
    buttonFastbootReboot.bounds= Rectangle(126,65,163,25)
    buttonFastbootReboot.isFocusable=false
    adbPanel.add(buttonFastbootReboot)
    buttonFastbootReboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonFastbootReboot.isEnabled=false
                Runtime.getRuntime().exec("adb reboot bootloader").waitFor()
            }
            override fun done() {
                buttonFastbootReboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonPowerOff=JButton("Shutdown")
    buttonPowerOff.bounds= Rectangle(5,65,120,25)
    buttonPowerOff.isFocusable=false
    adbPanel.add(buttonPowerOff)
    buttonPowerOff.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonPowerOff.isEnabled=false
                Runtime.getRuntime().exec("adb reboot -p").waitFor()
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
                val builderList = Runtime.getRuntime().exec(textAreaCommandInput.text)
                val input = builderList.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                var line: String?
                var output = ""
                while (reader.readLine().also { line = it } != null) {
                    output += "\n"
                    output += line
                }
                textAreaCommandOutput.text = output
                builderList.waitFor()
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


    val logsPanel = JPanel()
    logsPanel.layout = null


    ////////LogsPane////////

    ////Label////

    ////Label////

    ////ScrollPane////
    val listLogs = JList(listModelLogs)
    val scrollPaneLogs = JScrollPane()
    scrollPaneLogs.setBounds(5, 5, 870, 425)
    scrollPaneLogs.setViewportView(listLogs)
    logsPanel.add(scrollPaneLogs)
    ////ScrollPane////

    val group = ButtonGroup()

    val radioButtonVerbose = JRadioButton("Verbose",false)
    radioButtonVerbose.bounds= Rectangle(165,436,80,20)
    logsPanel.add(radioButtonVerbose)
    group.add(radioButtonVerbose)

    val radioButtonDebug = JRadioButton("Debug",false)
    radioButtonDebug.bounds= Rectangle(265,436,80,20)
    logsPanel.add(radioButtonDebug)
    group.add(radioButtonDebug)

    val radioButtonInfo = JRadioButton("Info",true)
    radioButtonInfo.bounds= Rectangle(365,436,80,20)
    radioButtonInfo.isSelected=true
    logsPanel.add(radioButtonInfo)
    group.add(radioButtonInfo)

    val radioButtonWarning = JRadioButton("Warning",false)
    radioButtonWarning.bounds= Rectangle(465,436,80,20)
    logsPanel.add(radioButtonWarning)
    group.add(radioButtonWarning)

    val radioButtonError = JRadioButton("Error",false)
    radioButtonError.bounds= Rectangle(565,436,80,20)
    logsPanel.add(radioButtonError)
    group.add(radioButtonError)

    val radioButtonFatal = JRadioButton("Fatal",false)
    radioButtonFatal.bounds= Rectangle(665,436,80,20)
    logsPanel.add(radioButtonFatal)
    group.add(radioButtonFatal)

    val radioButtonSilent = JRadioButton("Silent",false)
    radioButtonSilent.bounds= Rectangle(765,436,80,20)
    logsPanel.add(radioButtonSilent)
    group.add(radioButtonSilent)

    ////Button////
    val buttonSave=JButton("Save")
    buttonSave.bounds= Rectangle(5,505,150,35)
    buttonSave.isFocusable=false
    buttonSave.isEnabled=false
    logsPanel.add(buttonSave)
    buttonSave.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonSave.isEnabled=false
                val choseFile = JFileChooser()
                choseFile.dialogTitle = "Save logs file"
                choseFile.addChoosableFileFilter(FileNameExtensionFilter("Logs File (.log)", "log"))
                choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
                choseFile.fileFilter = choseFile.getChoosableFileFilters()[1]
                val chooseDialog = choseFile.showSaveDialog(frame)
                if(chooseDialog == JFileChooser.APPROVE_OPTION) {
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
                buttonSave.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }
    val buttonStart=JButton("Start")
    buttonStart.bounds= Rectangle(5,431,150,35)
    buttonStart.isFocusable=false
    logsPanel.add(buttonStart)
    var functionButtonStart = true
    var ifStopSelected = false
    buttonStart.addActionListener {
        buttonSave.isEnabled=false
        if(ifStopSelected) {
            functionButtonStart=true
        }
        if(functionButtonStart) {
            buttonStart.text="Pause"
            if(ifStopSelected) {
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
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                        radioButtonDebug.isSelected -> {
                            Runtime.getRuntime().exec("adb logcat -c").waitFor()
                            val builderList = Runtime.getRuntime().exec("adb logcat *:D")
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                        radioButtonInfo.isSelected -> {
                            Runtime.getRuntime().exec("adb logcat -c").waitFor()
                            val builderList = Runtime.getRuntime().exec("adb logcat *:I")
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                        radioButtonWarning.isSelected -> {
                            Runtime.getRuntime().exec("adb logcat -c").waitFor()
                            val builderList = Runtime.getRuntime().exec("adb logcat *:W")
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                        radioButtonError.isSelected -> {
                            Runtime.getRuntime().exec("adb logcat -c").waitFor()
                            val builderList = Runtime.getRuntime().exec("adb logcat *:E")
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                        radioButtonFatal.isSelected -> {
                            Runtime.getRuntime().exec("adb logcat -c").waitFor()
                            val builderList = Runtime.getRuntime().exec("adb logcat *:F")
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                        radioButtonSilent.isSelected -> {
                            Runtime.getRuntime().exec("adb logcat -c").waitFor()
                            val builderList = Runtime.getRuntime().exec("adb logcat *:S")
                            val input = builderList.inputStream
                            val reader = BufferedReader(InputStreamReader(input))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system")
                                    listModelLogs.addElement(line)
                                listLogs.ensureIndexIsVisible(listModelLogs.size() - 1)
                            }
                        }
                    }
                }
            }
            val worker = MyWorker()
            worker.execute()
            functionButtonStart=false
            ifStopSelected=false
        }else{
            if(ifStopSelected) {
                listModelLogs.removeAllElements()
            }else {
                Runtime.getRuntime().exec("adb kill-server")
                functionButtonStart = true
                buttonStart.text = "Continue"
            }
        }
    }

    val buttonStop=JButton("Stop")
    buttonStop.bounds= Rectangle(5,468,150,35)
    buttonStop.isFocusable=false
    logsPanel.add(buttonStop)
    buttonStop.addActionListener {
        buttonStart.text="Start"
        Runtime.getRuntime().exec("adb kill-server")
        ifStopSelected=true
        buttonSave.isEnabled=true
    }


    
    ////Button////



    ////////LogsPane////////

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

    ////radioButton////

    val checkBoxPartitionSystem = JCheckBox("System")
    checkBoxPartitionSystem.bounds= Rectangle(300,30,100,20)
    fastbootPanel.add(checkBoxPartitionSystem)

    val checkBoxPartitionData = JCheckBox("Data")
    checkBoxPartitionData.bounds= Rectangle(300,55,100,20)
    fastbootPanel.add(checkBoxPartitionData)

    val checkBoxPartitionCache = JCheckBox("Cache")
    checkBoxPartitionCache.bounds= Rectangle(300,80,100,20)
    fastbootPanel.add(checkBoxPartitionCache)

    val checkBoxPartitionRecovery = JCheckBox("Recovery")
    checkBoxPartitionRecovery.bounds= Rectangle(300,105,100,20)
    fastbootPanel.add(checkBoxPartitionRecovery)

    val checkBoxPartitionBoot = JCheckBox("Boot")
    checkBoxPartitionBoot.bounds= Rectangle(300,130,100,20)
    fastbootPanel.add(checkBoxPartitionBoot)

    val checkBoxPartitionRadio = JCheckBox("Radio")
    checkBoxPartitionRadio.bounds= Rectangle(300,155,100,20)
    fastbootPanel.add(checkBoxPartitionRadio)

    ////radioButton////

    ////Button////
    val buttonRunCommandFastboot=JButton("Run")
    buttonRunCommandFastboot.bounds= Rectangle(173,513,140,25)
    buttonRunCommandFastboot.isFocusable=false
    fastbootPanel.add(buttonRunCommandFastboot)
    buttonRunCommandFastboot.addActionListener {

        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRunCommandFastboot.isEnabled=false
                val builderList = Runtime.getRuntime().exec(textAreaCommandFastbootInput.text)
                val input = builderList.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                var line: String?
                var output = ""
                while (reader.readLine().also { line = it } != null) {
                    output += "\n"
                    output += line
                }
                textAreaCommandFastbootOutput.text = output
                builderList.waitFor()
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
                if(checkBoxPartitionBoot.isSelected){
                    Runtime.getRuntime().exec("fastboot erase boot").waitFor()
                }
                if(checkBoxPartitionSystem.isSelected){
                    Runtime.getRuntime().exec("fastboot erase system").waitFor()
                }
                if(checkBoxPartitionData.isSelected){
                    Runtime.getRuntime().exec("fastboot erase userdata").waitFor()
                }
                if(checkBoxPartitionCache.isSelected){
                    Runtime.getRuntime().exec("fastboot erase cache").waitFor()
                }
                if(checkBoxPartitionRecovery.isSelected){
                    Runtime.getRuntime().exec("fastboot erase recovery").waitFor()
                }
                if(checkBoxPartitionRadio.isSelected){
                    Runtime.getRuntime().exec("fastboot erase radio").waitFor()
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
    buttonRebootFastboot.bounds= Rectangle(5,5,120,25)
    buttonRebootFastboot.isFocusable=false
    fastbootPanel.add(buttonRebootFastboot)
    buttonRebootFastboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRebootFastboot.isEnabled=false
                Runtime.getRuntime().exec("fastboot reboot").waitFor()
            }
            override fun done() {
                buttonRebootFastboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonRecoveryRebootFastboot=JButton("Reboot to Recovery")
    buttonRecoveryRebootFastboot.bounds= Rectangle(126,5,170,25)
    buttonRecoveryRebootFastboot.isFocusable=false
    fastbootPanel.add(buttonRecoveryRebootFastboot)
    buttonRecoveryRebootFastboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRecoveryRebootFastboot.isEnabled=false
                Runtime.getRuntime().exec("fastboot oem reboot-recovery").waitFor()
            }
            override fun done() {
                buttonRecoveryRebootFastboot.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonFastbootRebootFastboot=JButton("Reboot to Fastboot")
    buttonFastbootRebootFastboot.bounds= Rectangle(126,30,170,25)
    buttonFastbootRebootFastboot.isFocusable=false
    fastbootPanel.add(buttonFastbootRebootFastboot)
    buttonFastbootRebootFastboot.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonFastbootRebootFastboot.isEnabled=false
                Runtime.getRuntime().exec("fastboot reboot-bootloader").waitFor()
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
                if(System.getProperty("os.name").indexOf("Windows")!=-1){
                    Runtime.getRuntime().exec("fastboot flash recovery \"$selectedFileAbsolutePath\"").waitFor()
                }else{
                    Runtime.getRuntime().exec("fastboot flash recovery $selectedFileAbsolutePath").waitFor()
                }
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
                if(System.getProperty("os.name").indexOf("Windows")!=-1){
                    Runtime.getRuntime().exec("fastboot boot \"$selectedFileAbsolutePath\"").waitFor()
                }else{
                    Runtime.getRuntime().exec("fastboot boot $selectedFileAbsolutePath").waitFor()
                }
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

    val recoveryPanel = JPanel()
    recoveryPanel.layout = null

    ////////RecoveryPane////////

    ////Label////
    val labelInstallZip=JLabel("Install zip")
    labelInstallZip.bounds= Rectangle(7,60,250,20)
    recoveryPanel.add(labelInstallZip)
    ////Label////

    ////Button////
    val buttonRebootRecovery=JButton("Reboot")
    buttonRebootRecovery.bounds= Rectangle(5,5,120,25)
    buttonRebootRecovery.isFocusable=false
    recoveryPanel.add(buttonRebootRecovery)
    buttonRebootRecovery.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRebootRecovery.isEnabled=false
                Runtime.getRuntime().exec("adb shell twrp reboot").waitFor()
            }
            override fun done() {
                buttonRebootRecovery.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonRecoveryRebootRecovery=JButton("Reboot to Recovery")
    buttonRecoveryRebootRecovery.bounds= Rectangle(126,5,170,25)
    buttonRecoveryRebootRecovery.isFocusable=false
    recoveryPanel.add(buttonRecoveryRebootRecovery)
    buttonRecoveryRebootRecovery.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonRecoveryRebootRecovery.isEnabled=false
                Runtime.getRuntime().exec("adb shell twrp reboot recovery").waitFor()
            }
            override fun done() {
                buttonRecoveryRebootRecovery.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonFastbootRebootRecovery=JButton("Reboot to Fastboot")
    buttonFastbootRebootRecovery.bounds= Rectangle(126,30,170,25)
    buttonFastbootRebootRecovery.isFocusable=false
    recoveryPanel.add(buttonFastbootRebootRecovery)
    buttonFastbootRebootRecovery.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonFastbootRebootRecovery.isEnabled=false
                Runtime.getRuntime().exec("adb shell twrp reboot bootloader").waitFor()
            }
            override fun done() {
                buttonFastbootRebootRecovery.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonPowerOffRecovery=JButton("Shutdown")
    buttonPowerOffRecovery.bounds= Rectangle(5,30,120,25)
    buttonPowerOffRecovery.isFocusable=false
    recoveryPanel.add(buttonPowerOffRecovery)
    buttonPowerOffRecovery.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonPowerOffRecovery.isEnabled=false
                Runtime.getRuntime().exec("adb shell twrp reboot poweroff").waitFor()
            }
            override fun done() {
                buttonPowerOffRecovery.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }

    val buttonChooseZip=JButton("Select Zip")
    buttonChooseZip.bounds= Rectangle(5,135,285,50)
    buttonChooseZip.isFocusable=false
    recoveryPanel.add(buttonChooseZip)
    buttonChooseZip.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonChooseZip.isEnabled=false
                val choseFile = JFileChooser()
                val filter = FileNameExtensionFilter("Zip files", "zip")
                choseFile.fileFilter = filter
                val chooseDialog = choseFile.showDialog(null, "Select Zip")
                if(chooseDialog == JFileChooser.APPROVE_OPTION) {
                    selectedZipPath = choseFile.selectedFile.absolutePath
                }
            }
            override fun done() {
                buttonChooseZip.isEnabled=true
            }
        }

        val worker = MyWorker()
        worker.execute()
    }
    val buttonInstallZip=JButton("Install")
    buttonInstallZip.bounds= Rectangle(5,80,285,50)
    buttonInstallZip.isFocusable=false
    recoveryPanel.add(buttonInstallZip)
    buttonInstallZip.addActionListener {
        class MyWorker : SwingWorker<Unit, Int>() {
            override fun doInBackground() {
                buttonInstallZip.isEnabled=false
                Runtime.getRuntime().exec("adb shell twrp sideload")
                Thread.sleep(3_000)
                if(System.getProperty("os.name").indexOf("Windows")!=-1){
                        Runtime.getRuntime().exec("adb sideload \"${selectedZipPath}\"").waitFor()
                }else{
                        Runtime.getRuntime().exec("adb sideload ${selectedZipPath}").waitFor()
                }
            }
            override fun done() {
                buttonInstallZip.isEnabled=true
            }
        }
        val worker = MyWorker()
        worker.execute()

    }
    ////Button////

    ////////RecoveryPane////////

    val linksPanel = JPanel()
    linksPanel.layout = null

    ////////LinksPanel////////

    ////ROMs////
    val labelRoms=JLabel("ROMs")
    labelRoms.bounds= Rectangle(220,5,140,30)
    labelRoms.font = labelRoms.font.deriveFont(22.0f)
    linksPanel.add(labelRoms)

    val labelPE=JLabel("Pixel Experience")
    labelPE.bounds= Rectangle(10,45,110,20)
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
    labelParanoid.bounds= Rectangle(10,365,120,20)
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
    labelDerpFest.bounds= Rectangle(10,445,120,20)
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
    buttonMSMXtended.addActionListener { runUrl("https://msmxtended.org/") }

    val buttonMSMXtendedDownload=JButton("Download")
    buttonMSMXtendedDownload.bounds= Rectangle(250,90,110,25)
    buttonMSMXtendedDownload.isFocusable=false
    linksPanel.add(buttonMSMXtendedDownload)
    buttonMSMXtendedDownload.addActionListener { runUrl("https://sourceforge.net/projects/xtended/files/") }

    val labelAOSPExtended=JLabel("AOSP Extended")
    labelAOSPExtended.bounds= Rectangle(250,125,120,20)
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
    buttonRevengeOS.isEnabled=false
    linksPanel.add(buttonRevengeOS)

    val buttonRevengeOSDownload=JButton("Download")
    buttonRevengeOSDownload.bounds= Rectangle(250,490,110,25)
    buttonRevengeOSDownload.isFocusable=false
    linksPanel.add(buttonRevengeOSDownload)
    buttonRevengeOSDownload.addActionListener { runUrl("https://get.revengeos.com/") }

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
    buttonMasikDownload.isEnabled=false
    linksPanel.add(buttonMasikDownload)

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
    labelGcam.bounds= Rectangle(760,125,120,20)
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
    tabbedpane.add("Logcat", logsPanel)
    tabbedpane.add("Fastboot", fastbootPanel)
    tabbedpane.add("Recovery", recoveryPanel)
    tabbedpane.add("Links", linksPanel)


    frame.add(tabbedpane)
    frame.isVisible=true
}


