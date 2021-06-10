import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
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
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess


var arrayList = ArrayList<String>()
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
val armArch = "aarch64" == System.getProperty("os.arch").toString()
val ProgramDir = userFolder + when {
	Windows -> "\\.android_tool\\"
	Linux -> "/.android_tool/"
	else -> "/.android_tool/"
}
var SdkDir = ProgramDir + when {
	Windows -> "SDK-Tools\\"
	Linux -> "SDK-Tools/"
	else -> "SDK-Tools/"
}

const val programVersion = "1.3.0-alpha5"
var programVersionLatest = programVersion
val appProp = Properties()

open class AndroidTool : Command() {
	init {
		AndroidToolUI()
		Command()
	}

	companion object : AndroidTool() {
		@OptIn(DelicateCoroutinesApi::class)
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
				buttonSave.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					val choseFile = JFileChooser()
					choseFile.dialogTitle = "Save logs file"
					choseFile.selectedFile = File("ATLog");
					choseFile.addChoosableFileFilter(FileNameExtensionFilter("Logs File (.log)", "log"))
					choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
					choseFile.fileFilter = choseFile.choosableFileFilters[1]
					val chooseDialog = choseFile.showSaveDialog(frame)
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
					buttonSave.isEnabled = true
				}
			}
			buttonSdkDownload.addActionListener {
				buttonSdkDownload.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					createFolder()
					when {
						Windows -> downloadFile(
							"https://github.com/fast-geek/SDK-Platform-Tools/raw/main/Windows.zip",
							"$SdkDir\\Windows.zip"
						)
						Linux -> downloadFile(
							"https://github.com/fast-geek/SDK-Platform-Tools/raw/main/Linux.zip",
							"$SdkDir/Linux.zip"
						)
						MacOS -> downloadFile(
							"https://github.com/fast-geek/SDK-Platform-Tools/raw/main/MacOS.zip",
							"$SdkDir/MacOS.zip"
						)
					}
					when {
						Windows -> unZipFile("$SdkDir\\Windows.zip")
						Linux -> unZipFile("$SdkDir/Linux.zip")
						MacOS -> unZipFile("$SdkDir/MacOS.zip")
					}
					if (Windows) Runtime.getRuntime().exec("attrib +h $userFolder\\.android_tool")
					when {
						Windows -> SdkDir = "$userFolder\\.android_tool\\SDK-Tools\\"
						Linux -> SdkDir = "$userFolder/.android_tool/SDK-Tools/"
						MacOS -> SdkDir = "$userFolder/.android_tool/SDK-Tools/"
					}
					dialogSdkDownload.dispose()
				}
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
				buttonUpdate.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					runUrl("https://github.com/fast-geek/Android-Tool/releases/latest")
				}
				Runtime.getRuntime().exec("${SdkDir}adb kill-server")
				exitProcess(0)
			}
			buttonIpConnect.addActionListener {
				labelConnect.text = ""
				buttonIpConnect.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "kill-server")
					val output = exec("adb", "connect ${textFieldIP.text}", output = true)
					if ("connected to" in output || "failed to authenticate to" in output) {
						labelTCPConnection.text = "Connected to ${textFieldIP.text}"
						labelTCPConnection.icon = iconYes
					} else {
						labelConnect.text = "Failed"
					}
					buttonIpConnect.isEnabled = true
				}
			}
			buttonStart.addActionListener {
				buttonStop.isEnabled = true
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
					GlobalScope.launch(Dispatchers.Swing) {
						arrayList.clear()
						withContext(Dispatchers.Default) {
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
				buttonReboot.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					when {
						ConnectedViaAdb -> exec("adb", "reboot")
						ConnectedViaFastboot -> exec("fastboot", "reboot")
						ConnectedViaRecovery -> exec("adb", "shell twrp reboot")
					}
					buttonReboot.isEnabled = true
				}
			}
			buttonResetPort.addActionListener {
				buttonResetPort.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "tcpip 5555")
					buttonResetPort.isEnabled = true
				}
			}
			buttonRecoveryReboot.addActionListener {
				buttonRecoveryReboot.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					when {
						ConnectedViaAdb -> exec("adb", "reboot recovery")
						ConnectedViaFastboot -> exec("fastboot", "oem reboot-recovery")
						ConnectedViaRecovery -> exec("adb", "shell twrp reboot recovery")
					}
					buttonRecoveryReboot.isEnabled = true
				}
			}
			buttonGetLogs.addActionListener {
				buttonGetLogs.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell cp -f /tmp/recovery.log /sdcard")
					buttonGetLogs.isEnabled = true
				}
			}
			openConsole.addActionListener {
				if (Windows)
					Runtime.getRuntime().exec("cmd /c start cd $SdkDir")
				else
					Runtime.getRuntime().exec("open -a Terminal $SdkDir")
			}
			buttonFastbootReboot.addActionListener {
				buttonFastbootReboot.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					when {
						ConnectedViaAdb -> exec("adb", "reboot bootloader")
						ConnectedViaFastboot -> exec("fastboot", "reboot-bootloader")
						ConnectedViaRecovery -> exec("adb", "shell twrp reboot bootloader")
					}
					buttonFastbootReboot.isEnabled = true
				}
			}
			buttonPowerOff.addActionListener {
				buttonPowerOff.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					when {
						ConnectedViaAdb -> exec("adb", "reboot -p")
						ConnectedViaRecovery -> exec("adb", "shell twrp reboot poweroff")
					}
					buttonPowerOff.isEnabled = true
				}
			}
			tabbedpane.addChangeListener {
				try {
					textFieldIP.text =
						AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
				} catch (e: Exception) {
				}
				if (tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1 || tabbedpane.selectedIndex == 2) {
					contents.setBounds(5, 5, 310, 375)
					deviceControlPanel.setBounds(5, 385, 310, 85)
					deviceConnection.setBounds(5, 475, 310, 100)
					labelTCP.isVisible = true
					labelTCPConnection.isVisible = true
					buttonIpConnect.isVisible = true
					textFieldIP.isVisible = true
					labelConnect.isVisible = true
					labelIP.isVisible = true
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
				} else if (tabbedpane.selectedIndex == 4) {
					contents.setBounds(5, 5, 310, 425)
					deviceControlPanel.setBounds(5, 435, 310, 85)
					deviceConnection.setBounds(5, 525, 310, 50)
					labelTCP.isVisible = false
					labelTCPConnection.isVisible = false
					buttonIpConnect.isVisible = false
					textFieldIP.isVisible = false
					labelConnect.isVisible = false
					labelIP.isVisible = false
				} else if (tabbedpane.selectedIndex == 6) {
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
				buttonInstallAll.isEnabled = false
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
						if (Windows or MacOS)
							exec("adb", "install \"$path\"")
						else
							exec("adb", "install \'$path\'")
					}
					buttonInstallAll.isEnabled = true
				}
				getListOfPackages()
			}
			buttonInstallOne.addActionListener {
				buttonInstallOne.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					if (Windows or MacOS)
						exec("adb", "install \"$selectedFileAbsolutePath\"")
					else
						exec("adb", "install \'$selectedFileAbsolutePath\'")
					buttonInstallOne.isEnabled = true
				}
				getListOfPackages()
			}
			disableButton.addActionListener {
				val textInput = list.selectedValue.toString().substringBefore("(")
				disableButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell pm disable-user --user 0 $textInput")
					disableButton.isEnabled = true
				}
				getListOfPackages()
			}
			uninstallButton.addActionListener {
				val textInput = list.selectedValue.toString().substringBefore("(")
				uninstallButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell pm uninstall --user 0 $textInput")
					uninstallButton.isEnabled = true
				}
				getListOfPackages()
			}
			enableButton.addActionListener {
				val textInput = list.selectedValue.toString().substringBefore("(")
				enableButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell pm enable $textInput")
					enableButton.isEnabled = true
				}
				getListOfPackages()
			}
			clearButton.addActionListener {
				val textInput = list.selectedValue.toString().substringBefore("(")
				clearButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell pm clear $textInput")
					clearButton.isEnabled = true
				}
			}
			openButton.addActionListener {
				val textInput = list.selectedValue.toString().substringBefore("(")
				openButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell monkey -p $textInput 1")
					openButton.isEnabled = true
				}
			}
			forceStopButton.addActionListener {
				val textInput = list.selectedValue.toString().substringBefore("(")
				forceStopButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					exec("adb", "shell am force-stop $textInput")
					forceStopButton.isEnabled = true
				}
			}
			refreshButton.addActionListener {
				getListOfPackages(true)
			}
			saveListButton.addActionListener {
				saveListButton.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					val choseFile = JFileChooser()
					choseFile.dialogTitle = "Save app list"
					choseFile.selectedFile = File("ATAppList");
					choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
					choseFile.fileFilter = choseFile.choosableFileFilters[1]
					val chooseDialog = choseFile.showSaveDialog(frame)
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
					saveListButton.isEnabled = true
				}
			}

			buttonChooseOne.addActionListener {
				buttonChooseOne.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					val choseFile = JFileChooser()
					val filter = FileNameExtensionFilter("APK Files", "apk")
					choseFile.fileFilter = filter
					val chooseDialog = choseFile.showDialog(null, "Choose APK")
					if (chooseDialog == JFileChooser.APPROVE_OPTION) {
						selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
						selectedFilePath = choseFile.selectedFile.path
						labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
					}
					buttonChooseOne.isEnabled = true
				}
			}
			buttonChoseAll.addActionListener {
				buttonChoseAll.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					val choseDirectory = JFileChooser()
					choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
					val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
					if (chooseDialog == JFileChooser.APPROVE_OPTION) {
						selectedDirectoryPath = choseDirectory.selectedFile.path
						labelSelectedAll.text = "Selected: ${choseDirectory.selectedFile.path}"
					}
					buttonChoseAll.isEnabled = true
				}
			}
			buttonRunCommand.addActionListener {
				buttonRunCommand.isEnabled = false

				GlobalScope.launch(Dispatchers.Swing) {
					val command = textAreaCommandInput.text
					when {
						"adb" in command -> textAreaCommandOutput.text =
							exec("adb", textAreaCommandInput.text.substring(4), output = true)
						"fastboot" in command -> textAreaCommandOutput.text =
							exec("fastboot", textAreaCommandInput.text.substring(9), output = true)
						else -> textAreaCommandOutput.text = exec("adb", textAreaCommandInput.text, output = true)
					}
					buttonRunCommand.isEnabled = true
				}
			}
			buttonErase.addActionListener {
				buttonErase.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
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
					buttonErase.isEnabled = true
				}
			}
			buttonChoseRecovery.addActionListener {
				buttonChoseRecovery.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					val choseFile = JFileChooser()
					val filter = FileNameExtensionFilter("Recovery Files", "img")
					choseFile.fileFilter = filter
					val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
					if (chooseDialog == JFileChooser.APPROVE_OPTION) {
						selectedFileAbsolutePath = choseFile.selectedFile.absolutePath
						selectedFilePath = choseFile.selectedFile.path
						labelSelectedOne.text = "Selected: ${choseFile.selectedFile.name}"
					}
					buttonChoseRecovery.isEnabled = true
				}
			}
			buttonInstallRecovery.addActionListener {
				buttonInstallRecovery.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					if (Windows)
						exec("fastboot", "flash recovery \"$selectedFileAbsolutePath\"")
					else
						exec("fastboot", "flash recovery $selectedFileAbsolutePath")
					buttonInstallRecovery.isEnabled = true
				}
			}
			buttonBootToRecovery.addActionListener {
				buttonBootToRecovery.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					if (Windows)
						exec("fastboot", "boot \"$selectedFileAbsolutePath\"")
					else
						exec("fastboot", "boot $selectedFileAbsolutePath")
					buttonBootToRecovery.isEnabled = true
				}
			}
			buttonChooseZip.addActionListener {
				buttonChooseZip.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					val choseFile = JFileChooser()
					val filter = FileNameExtensionFilter("Zip files", "zip")
					choseFile.fileFilter = filter
					val chooseDialog = choseFile.showDialog(null, "Select Zip")
					if (chooseDialog == JFileChooser.APPROVE_OPTION) {
						selectedZipPath = choseFile.selectedFile.absolutePath
						selectedZipName = choseFile.selectedFile.name
					}
					buttonChooseZip.isEnabled = true
				}
			}
			buttonInstallZip.addActionListener {
				buttonInstallZip.isEnabled = false
				GlobalScope.launch(Dispatchers.Swing) {
					if (Windows) {
						exec("adb", "push \"${selectedZipPath}\" /sdcard/")
					} else {
						exec("adb", "push $selectedZipPath /sdcard/")
					}
					exec("adb", "shell twrp install $selectedZipName")
					exec("adb", "shell rm $selectedZipName")
					buttonInstallZip.isEnabled = true
				}
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
			GlobalScope.launch(Dispatchers.Swing) {
				while (true) {
					connectionCheck()
					delay(1000)
				}
			}

			versionCheck()
		}
	}
}
