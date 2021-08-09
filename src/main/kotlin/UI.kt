import AndroidTool.Companion.atForm
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
	atForm.list2.model = listModelLogs
	atForm.table1.model = model
	atForm.table1.showHorizontalLines = true
	atForm.table1.showVerticalLines = true
	atForm.table1.setDefaultEditor(Any::class.java, null)
	model.addColumn("Property")
	model.addColumn("Value")
	AndroidTool.frame.jMenuBar = menuBar
	AndroidTool.frame.addWindowListener(object : WindowAdapter() {
		override fun windowClosing(e: WindowEvent) {
			exec("adb", "kill-server")
		}
	})
	fileMenu.add(settingsMenu)
	fileMenu.add(aboutItem)
	fileMenu.addSeparator()
	fileMenu.add(exitItem)
	menuBar.add(fileMenu)
	atForm.textField1.addKeyListener(object : KeyAdapter() {
		override fun keyReleased(evt: KeyEvent) {
			searchFilter(atForm.textField1.text)
		}
	})
	atForm.saveButton.addActionListener {
		atForm.saveButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			choseFile.dialogTitle = "Save logs file"
			choseFile.selectedFile = File("ATLog")
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
			atForm.saveButton.isEnabled = true
		}
	}
	atForm.allRadioButton.addActionListener {
		getListOfPackages()
	}
	atForm.disabledRadioButton.addActionListener {
		getListOfPackages()
	}
	atForm.enabledRadioButton.addActionListener {
		getListOfPackages()
	}
	atForm.systemRadioButton.addActionListener {
		getListOfPackages()
	}
	atForm.thirdRadioButton.addActionListener {
		getListOfPackages()
	}
	atForm.connectButton.addActionListener {
		atForm.connectButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "kill-server")
			val output = exec("adb", "connect ${atForm.textField2.text}", output = true)
			if ("connected to" in output || "failed to authenticate to" in output) {
				atForm.notConnectedLabel1.text = "Connected to ${atForm.textField2.text}"
				setSettings("lastIP", atForm.textField2.text)
			}
			atForm.connectButton.isEnabled = true
		}
	}
	atForm.startButton.addActionListener {
		atForm.stopButton.isEnabled = true
		atForm.saveButton1.isEnabled = false
		if (ifStopSelected) {
			functionButtonStart = true
		}
		if (functionButtonStart) {
			atForm.saveButton1.isEnabled = false
			logsWorking = true
			atForm.startButton.text = "Pause"
			if (ifStopSelected) {
				listModelLogs.removeAllElements()
			}
			GlobalScope.launch(Dispatchers.Swing) {
				arrayList.clear()
				withContext(Dispatchers.Default) {
					Runtime.getRuntime().exec("${SdkDir}adb logcat -c").waitFor()
					val builderList = when {
						atForm.verboseRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:V")
						atForm.debugRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:D")
						atForm.infoRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:I")
						atForm.warningRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:W")
						atForm.errorRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:E")
						atForm.fatalRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:F")
						atForm.slentRadioButton.isSelected -> Runtime.getRuntime().exec("${SdkDir}adb logcat *:S")
						else -> Runtime.getRuntime().exec("${SdkDir}adb logcat -c")
					}

					val input = builderList.inputStream
					val reader = BufferedReader(InputStreamReader(input))
					var line: String?
					while (reader.readLine().also { line = it } != null) {
						if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
							if (logsWorking) {
								listModelLogs.addElement(line)
								atForm.list2.ensureIndexIsVisible(atForm.list2.model.size - 1)
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
				atForm.startButton.text = "Continue"
			}
		}
	}
	atForm.stopButton.addActionListener {
		atForm.stopButton.isEnabled = false
		atForm.startButton.text = "Start"
		logsWorking = false
		ifStopSelected = true
		atForm.saveButton1.isEnabled = true
		functionButtonStart = true
	}
	atForm.rebootButton.addActionListener {
		atForm.rebootButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot")
				ConnectedViaFastboot -> exec("fastboot", "reboot")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot")
			}
			atForm.rebootButton.isEnabled = true
		}
	}
	atForm.buttonResetPort.addActionListener {
		atForm.buttonResetPort.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "tcpip 5555")
			atForm.buttonResetPort.isEnabled = true
		}
	}
	atForm.rebootToRecoveryButton.addActionListener {
		atForm.rebootToRecoveryButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot recovery")
				ConnectedViaFastboot -> exec("fastboot", "oem reboot-recovery")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot recovery")
			}
			atForm.rebootToRecoveryButton.isEnabled = true
		}
	}
	atForm.saveRecoveryLogsButton.addActionListener {
		atForm.saveRecoveryLogsButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "shell cp -f /tmp/recovery.log /sdcard")
			atForm.saveRecoveryLogsButton.isEnabled = true
		}
	}
	atForm.openSystemTerminalButton.addActionListener {
		when {
			Windows -> Runtime.getRuntime().exec("cmd /c start cd $SdkDir")
			MacOS -> Runtime.getRuntime().exec("open -a Terminal $SdkDir")
			else -> Runtime.getRuntime().exec("gnome-terminal --working-directory=$SdkDir")
		}
	}
	atForm.rebootToFastbootButton.addActionListener {
		atForm.rebootToFastbootButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot bootloader")
				ConnectedViaFastboot -> exec("fastboot", "reboot-bootloader")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot bootloader")
			}
			atForm.rebootToFastbootButton.isEnabled = true
		}
	}
	atForm.shutdownButton.addActionListener {
		atForm.shutdownButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				ConnectedViaAdb -> exec("adb", "reboot -p")
				ConnectedViaRecovery -> exec("adb", "shell twrp reboot poweroff")
			}
			atForm.shutdownButton.isEnabled = true
		}
	}
	atForm.installButton.addActionListener {
		atForm.installButton.isEnabled = false
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
			atForm.installButton.isEnabled = true
			atForm.selectedLabel.text = "Selected: -"
		}
		getListOfPackages()
	}
	atForm.installButton1.addActionListener {
		atForm.installButton1.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "install \"$selectedFileAbsolutePath\"")
			atForm.installButton1.isEnabled = true
			atForm.selectedLabel1.text = "Selected: -"
		}
		getListOfPackages()
	}
	atForm.disableButton.addActionListener {
		app("Disable", atForm.disableButton, atForm.list1)
		getListOfPackages()
	}
	atForm.uninstallButton.addActionListener {
		app("Uninstall", atForm.uninstallButton, atForm.list1)
		getListOfPackages()
	}
	atForm.enableButton.addActionListener {
		app("Enable", atForm.enableButton, atForm.list1)
		getListOfPackages()
	}
	atForm.clearDataButton.addActionListener {
		app("Clear", atForm.clearDataButton, atForm.list1)
	}
	atForm.openButton.addActionListener {
		app("Open", atForm.openButton, atForm.list1)
	}
	atForm.forceStopButton.addActionListener {
		app("Stop", atForm.forceStopButton, atForm.list1)
	}
	atForm.refreshButton.addActionListener {
		getListOfPackages(true)
	}
	atForm.saveButton1.addActionListener {
		atForm.saveButton1.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			choseFile.dialogTitle = "Save app list"
			choseFile.selectedFile = File("ATAppList")
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
			atForm.saveButton1.isEnabled = true
		}
	}
	atForm.selectFileButton.addActionListener {
		atForm.selectButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("APK Files", "apk")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Choose APK")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
				selectedFilePath = choseFile.selectedFile.path
				atForm.selectedLabel.text = "Selected: ${choseFile.selectedFile.name}"
				atForm.installButton1.isEnabled = true
			}
			atForm.selectButton.isEnabled = true
		}
	}
	atForm.selectFolderButton.addActionListener {
		atForm.selectFolderButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseDirectory = JFileChooser()
			choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
			val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedDirectoryPath = choseDirectory.selectedFile.path
				atForm.selectedLabel1.text = "Selected: ${choseDirectory.selectedFile.path}"
				atForm.installButton.isEnabled = true
			}
			atForm.selectFolderButton.isEnabled = true
		}
	}
	atForm.runButton.addActionListener {
		atForm.runButton.isEnabled = false

		GlobalScope.launch(Dispatchers.Swing) {
			val command = atForm.textArea1.text
			when {
				"adb" in command -> atForm.textArea2.text =
					exec("adb", atForm.textArea1.text.substring(4), output = true)
				"fastboot" in command -> atForm.textArea2.text =
					exec("fastboot", atForm.textArea1.text.substring(9), output = true)
				else -> atForm.textArea2.text = exec("adb", atForm.textArea1.text, output = true)
			}
			atForm.runButton.isEnabled = true
		}
	}
	atForm.eraseButton.addActionListener {
		atForm.eraseButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			if (atForm.bootCheckBox.isSelected)
				exec("fastboot", "erase boot")
			if (atForm.systemCheckBox.isSelected)
				exec("fastboot", "erase system")
			if (atForm.dataCheckBox.isSelected)
				exec("fastboot", "erase userdata")
			if (atForm.cacheCheckBox.isSelected)
				exec("fastboot", "erase cache")
			if (atForm.recoveryCheckBox.isSelected)
				exec("fastboot", "erase recovery")
			if (atForm.radioCheckBox.isSelected)
				exec("fastboot", "erase radio")
			atForm.eraseButton.isEnabled = true
		}
	}
	atForm.selectRecoveryButton.addActionListener {
		atForm.selectRecoveryButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("Recovery Files", "img")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
				selectedFilePath = choseFile.selectedFile.path
			}
			atForm.selectRecoveryButton.isEnabled = true
		}
	}
	atForm.installButton2.addActionListener {
		atForm.installButton2.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("fastboot", "flash recovery \"$selectedFileAbsolutePath\"")
			atForm.installButton2.isEnabled = true
		}
	}
	atForm.bootButton.addActionListener {
		atForm.bootButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("fastboot", "boot \"$selectedFileAbsolutePath\"")
			atForm.bootButton.isEnabled = true
		}
	}
	atForm.selectButton.addActionListener {
		atForm.selectButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("Zip files", "zip")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select Zip")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selectedZipPath = choseFile.selectedFile.absolutePath
				selectedZipName = choseFile.selectedFile.name
			}
			atForm.selectButton.isEnabled = true
		}
	}
	atForm.installButton3.addActionListener {
		atForm.installButton3.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "push \"$selectedZipPath\" /sdcard/")
			exec("adb", "shell twrp install \"$selectedZipName\"")
			exec("adb", "shell rm \"$selectedZipName\"")
			atForm.installButton3.isEnabled = true
		}
	}
	settingsMenu.addActionListener {
		Settings.main()
	}
	aboutItem.addActionListener {
		AboutDialog.main()
	}
	exitItem.addActionListener {
		exec("adb", "kill-server")
		exitProcess(0)
	}
	desableCompoments()
	if (getSettings("lastIP") == "") {
		systemIP = when {
			Windows -> InetAddress.getLocalHost().hostAddress
			MacOS -> Runtime.getRuntime().exec("ipconfig getifaddr en0").inputStream.bufferedReader().readLine()
			Linux -> Runtime.getRuntime().exec("ip n").inputStream.bufferedReader().readLine().substringBefore(" ")
			else -> ""
		}
		systemIP = systemIP.substringBeforeLast('.') + "."
	} else
		systemIP = getSettings("lastIP")
	atForm.textField2.text = systemIP
}