import AndroidTool.Companion.a
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.net.InetAddress
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess

fun createUI() {
	a.list2.model = listModelLogs
	a.table1.model = model
	a.table1.showHorizontalLines = true
	a.table1.showVerticalLines = true
	a.table1.setDefaultEditor(Any::class.java, null)
	model.addColumn("Property")
	model.addColumn("Value")
	fileMenu.add(settingsMenu)
	fileMenu.add(aboutItem)
	fileMenu.addSeparator()
	fileMenu.add(exitItem)
	menuBar.add(fileMenu)
	AndroidTool.frame.jMenuBar = menuBar
	AndroidTool.frame.addWindowListener(object : WindowAdapter() {
		override fun windowClosing(e: WindowEvent) {
			exec("adb", "kill-server")
		}
	})
	a.textField1.addKeyListener(object : KeyAdapter() {
		override fun keyReleased(evt: KeyEvent) {
			searchFilter(a.textField1.text)
		}
	})
	a.saveButton.addActionListener {
		a.saveButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			choseFile.dialogTitle = "Save logs file"
			choseFile.selectedFile = File("ATLog");
			choseFile.addChoosableFileFilter(FileNameExtensionFilter("Logs File (.log)", "log"))
			choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
			choseFile.fileFilter = choseFile.choosableFileFilters[1]
			val chooseDialog = choseFile.showSaveDialog(AndroidTool.frame)
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				val file =
					File(choseFile.selectedFile.canonicalPath.toString() + "." + (choseFile.fileFilter as FileNameExtensionFilter).extensions[0])
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
			a.saveButton.isEnabled = true
		}
	}
	a.allRadioButton.addActionListener {
		getListOfPackages()
	}
	a.disabledRadioButton.addActionListener {
		getListOfPackages()
	}
	a.enabledRadioButton.addActionListener {
		getListOfPackages()
	}
	a.systemRadioButton.addActionListener {
		getListOfPackages()
	}
	a.thirdRadioButton.addActionListener {
		getListOfPackages()
	}
	a.connectButton.addActionListener {
		a.connectButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "kill-server")
			val output = exec("adb", "connect ${a.textField2.text}", output = true)
			if ("connected to" in output || "failed to authenticate to" in output) {
				a.notConnectedLabel1.text = "Connected to ${a.textField2.text}"
			}
			a.connectButton.isEnabled = true
		}
	}
	a.startButton.addActionListener {
		a.stopButton.isEnabled = true
		a.saveButton1.isEnabled = false
		if (ifStopSelected) {
			functionButtonStart = true
		}
		if (functionButtonStart) {
			a.saveButton1.isEnabled = false
			logsWorking = true
			a.startButton.text = "Pause"
			if (ifStopSelected) {
				listModelLogs.removeAllElements()
			}
			GlobalScope.launch(Dispatchers.Swing) {
				arrayList.clear()
				withContext(Dispatchers.Default) {
					Runtime.getRuntime().exec("${SdkDir}adb logcat -c").waitFor()
					val builderList = when {
						a.verboseRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:V")
						a.debugRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:D")
						a.infoRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:I")
						a.warningRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:W")
						a.errorRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:E")
						a.fatalRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:F")
						a.slentRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:S")
						else -> Runtime.getRuntime().exec("${SdkDir}adb logcat -c")
					}

					val input = builderList.inputStream
					val reader = BufferedReader(InputStreamReader(input))
					var line: String?
					while (reader.readLine().also { line = it } != null) {
						if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
							if (logsWorking) {
								listModelLogs.addElement(line)
								a.list2.ensureIndexIsVisible(a.list2.model.size - 1)
							}
						}
					}
				}
			}
			functionButtonStart = false
			ifStopSelected = false
		} else {
			if (ifStopSelected) {
				listModelLogs.removeAllElements()
			} else {
				logsWorking = false
				functionButtonStart = true
				a.startButton.text = "Continue"
			}
		}
	}
	a.stopButton.addActionListener {
		a.stopButton.isEnabled = false
		a.startButton.text = "Start"
		logsWorking = false
		ifStopSelected = true
		a.saveButton1.isEnabled = true
		functionButtonStart = true
	}
	a.rebootButton.addActionListener {
		a.rebootButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot")
				ConnectedViaFastboot -> exec("fastboot", "reboot")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot")
			}
			a.rebootButton.isEnabled = true
		}
	}
	a.buttonResetPort.addActionListener {
		a.buttonResetPort.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "tcpip 5555")
			a.buttonResetPort.isEnabled = true
		}
	}
	a.rebootToRecoveryButton.addActionListener {
		a.rebootToRecoveryButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot recovery")
				ConnectedViaFastboot -> exec("fastboot", "oem reboot-recovery")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot recovery")
			}
			a.rebootToRecoveryButton.isEnabled = true
		}
	}
	a.saveRecoveryLogsButton.addActionListener {
		a.saveRecoveryLogsButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "shell cp -f /tmp/recovery.log /sdcard")
			a.saveRecoveryLogsButton.isEnabled = true
		}
	}
	a.openSystemTerminalButton.addActionListener {
		if (Windows)
			Runtime.getRuntime().exec("cmd /c start cd $SdkDir")
		else
			Runtime.getRuntime().exec("open -a Terminal $SdkDir")
	}
	a.rebootToFastbootButton.addActionListener {
		a.rebootToFastbootButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot bootloader")
				ConnectedViaFastboot -> exec("fastboot", "reboot-bootloader")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot bootloader")
			}
			a.rebootToFastbootButton.isEnabled = true
		}
	}
	a.shutdownButton.addActionListener {
		a.shutdownButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot -p")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot poweroff")
			}
			a.shutdownButton.isEnabled = true
		}
	}
	a.installButton.addActionListener {
		a.installButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
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
				exec("adb", "install \"$path\"")
			}
			a.installButton.isEnabled = true
			a.selectedLabel.text = "Selected: -"
		}
		getListOfPackages()
	}
	a.installButton1.addActionListener {
		a.installButton1.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "install \"$selectedFileAbsolutePath\"")
			a.installButton1.isEnabled = true
			a.selectedLabel1.text = "Selected: -"
		}
		getListOfPackages()
	}
	a.disableButton.addActionListener {
		app("Disable", a.disableButton, a.list1)
		getListOfPackages()
	}
	a.uninstallButton.addActionListener {
		app("Uninstall", a.uninstallButton, a.list1)
		getListOfPackages()
	}
	a.enableButton.addActionListener {
		app("Enable", a.enableButton, a.list1)
		getListOfPackages()
	}
	a.clearDataButton.addActionListener {
		app("Clear", a.clearDataButton, a.list1)
	}
	a.openButton.addActionListener {
		app("Open", a.openButton, a.list1)
	}
	a.forceStopButton.addActionListener {
		app("Stop", a.forceStopButton, a.list1)
	}
	a.refreshButton.addActionListener {
		getListOfPackages(true)
	}
	a.saveButton1.addActionListener {
		a.saveButton1.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			choseFile.dialogTitle = "Save app list"
			choseFile.selectedFile = File("ATAppList");
			choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
			choseFile.fileFilter = choseFile.choosableFileFilters[1]
			val chooseDialog = choseFile.showSaveDialog(AndroidTool.frame)
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				val file =
					File(choseFile.selectedFile.canonicalPath.toString() + "." + (choseFile.fileFilter as FileNameExtensionFilter).extensions[0])
				if (!file.exists()) {
					file.createNewFile()
				}
				val fw = FileWriter(file.absoluteFile)
				val bw = BufferedWriter(fw)
				for (element in 0 until listModel.size()) {
					bw.write(listModel[element].toString())
					bw.write("\n")
				}
				bw.close()
			}
			a.saveButton1.isEnabled = true
		}
	}
	a.selectFileButton.addActionListener {
		a.selectButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("APK Files", "apk")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Choose APK")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
				selectedFilePath = choseFile.selectedFile.path
				a.selectedLabel.text = "Selected: ${choseFile.selectedFile.name}"
				a.installButton1.isEnabled = true
			}
			a.selectButton.isEnabled = true
		}
	}
	a.selectFolderButton.addActionListener {
		a.selectFolderButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseDirectory = JFileChooser()
			choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
			val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedDirectoryPath = choseDirectory.selectedFile.path
				a.selectedLabel1.text = "Selected: ${choseDirectory.selectedFile.path}"
				a.installButton.isEnabled = true
			}
			a.selectFolderButton.isEnabled = true
		}
	}
	a.runButton.addActionListener {
		a.runButton.isEnabled = false

		GlobalScope.launch(Dispatchers.Swing) {
			val command = a.textArea1.text
			when {
				"adb" in command -> a.textArea2.text =
					exec("adb", a.textArea1.text.substring(4), output = true)
				"fastboot" in command -> a.textArea2.text =
					exec("fastboot", a.textArea1.text.substring(9), output = true)
				else -> a.textArea2.text = exec("adb", a.textArea1.text, output = true)
			}
			a.runButton.isEnabled = true
		}
	}
	a.eraseButton.addActionListener {
		a.eraseButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			if (a.bootCheckBox.isSelected)
				exec("fastboot", "erase boot")
			if (a.systemCheckBox.isSelected)
				exec("fastboot", "erase system")
			if (a.dataCheckBox.isSelected)
				exec("fastboot", "erase userdata")
			if (a.cacheCheckBox.isSelected)
				exec("fastboot", "erase cache")
			if (a.recoveryCheckBox.isSelected)
				exec("fastboot", "erase recovery")
			if (a.radioCheckBox.isSelected)
				exec("fastboot", "erase radio")
			a.eraseButton.isEnabled = true
		}
	}
	a.selectRecoveryButton.addActionListener {
		a.selectRecoveryButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("Recovery Files", "img")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
				selectedFilePath = choseFile.selectedFile.path
			}
			a.selectRecoveryButton.isEnabled = true
		}
	}
	a.installButton2.addActionListener {
		a.installButton2.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("fastboot", "flash recovery \"$selectedFileAbsolutePath\"")
			a.installButton2.isEnabled = true
		}
	}
	a.bootButton.addActionListener {
		a.bootButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("fastboot", "boot \"$selectedFileAbsolutePath\"")
			a.bootButton.isEnabled = true
		}
	}
	a.selectButton.addActionListener {
		a.selectButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("Zip files", "zip")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select Zip")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedZipPath = choseFile.selectedFile.absolutePath
				selectedZipName = choseFile.selectedFile.name
			}
			a.selectButton.isEnabled = true
		}
	}
	a.installButton3.addActionListener {
		a.installButton3.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "push \"$selectedZipPath\" /sdcard/")
			exec("adb", "shell twrp install \"$selectedZipName\"")
			exec("adb", "shell rm \"$selectedZipName\"")
			a.installButton3.isEnabled = true
		}
	}
	settingsMenu.addActionListener{
		Settings.main()
	}
	aboutItem.addActionListener{
		AboutDialog.main()
	}
	exitItem.addActionListener {
		exec("adb", "kill-server")
		exitProcess(0)
	}
	desableCompoments()
	if (getLastConnectIP() == "") {
		systemIP = when {
			Windows -> InetAddress.getLocalHost().hostAddress
			MacOS -> Runtime.getRuntime().exec("ipconfig getifaddr en0").inputStream.bufferedReader().readLine()
			Linux -> Runtime.getRuntime().exec("hostname -I").inputStream.bufferedReader().readLine()
			else -> ""
		}
		systemIP = systemIP.substringBeforeLast('.') + "."
	} else
		systemIP = getLastConnectIP()
	a.textField2.text = systemIP
}