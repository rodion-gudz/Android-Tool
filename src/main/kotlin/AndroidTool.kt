import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import org.apache.maven.artifact.versioning.ComparableVersion
import java.awt.Component
import java.awt.Desktop
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.*
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.util.*
import java.util.zip.ZipFile
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.DefaultTableModel
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
var iconYes = ImageIcon(AndroidTool()::class.java.getResource("connectionSuccess.png"))
val appIcon = ImageIcon(AndroidTool::class.java.getResource("appIcon.png")).image
val Windows = "Windows" in System.getProperty("os.name")
val Linux = "Linux" in System.getProperty("os.name")
val MacOS = "Mac" in System.getProperty("os.name")
val JarDir = System.getProperty("user.dir").toString()
val userFolder = System.getProperty("user.home").toString()
val armArch = "aarch64" == System.getProperty("os.arch").toString()
val ProgramDir = userFolder + if (Windows) "\\.android_tool\\" else "/.android_tool/"
var SdkDir = ProgramDir + if (Windows) "SDK-Tools\\" else "SDK-Tools/"
val model = DefaultTableModel()
val frame = JFrame()


const val programVersion = "2.0-alpha7"
var programVersionLatest = programVersion
val appProp = Properties()

val menuBar = JMenuBar()
val fileMenu = JMenu("Program")
val aboutItem = JMenuItem("About")
val exitItem = JMenuItem("Exit")
val settingsMenu = JMenuItem("Settings")


open class AndroidTool{

	init {
		createFolder()
		UIManager.put( "Button.arc", 6 )
		UIManager.put( "Component.arc", 6 )
		UIManager.put( "CheckBox.arc", 6 )
		UIManager.put( "ProgressBar.arc", 6 )
		UIManager.put( "TextComponent.arc", 6 )
		JFrame.setDefaultLookAndFeelDecorated(true)
		JDialog.setDefaultLookAndFeelDecorated(true)
		System.setProperty("apple.awt.application.appearance", "system")
		System.setProperty("apple.laf.useScreenMenuBar", "true")
		val theme = SettingsValues().getSettings()
		if (theme== "dark")
			UIManager.setLookAndFeel(FlatDarculaLaf())
		else if (theme == "white")
			UIManager.setLookAndFeel(FlatIntelliJLaf())
	}

	var a = ATForm()


	fun createFolder() {
		if (ProgramDir != null) {
			when {
				Windows -> File("$userFolder\\.android_tool", "SDK-Tools").mkdirs()
				else -> File("$userFolder/.android_tool", "SDK-Tools").mkdirs()
			}
			if (Windows) Runtime.getRuntime().exec("attrib +h $userFolder\\.android_tool")
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
				UpdateDialog.main(programVersion,  programVersionLatest)
			}
		} catch (e: Exception) {
			print(e)
		}
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
		a.list1.model = listModel
	}

	fun sdkCheck() {
		when {
			Windows -> {
				if (File("$userFolder\\.android_tool\\SDK-Tools\\adb.exe").exists() && File("$userFolder\\.android_tool\\SDK-Tools\\fastboot.exe").exists()) {
					SdkDir = "$userFolder\\.android_tool\\SDK-Tools\\"
					return
				}
			}
			else -> {
				if (File("$userFolder/.android_tool/SDK-Tools/adb").exists() && File("$userFolder/.android_tool/SDK-Tools/fastboot").exists()) {
					SdkDir = "$userFolder/.android_tool/SDK-Tools/"
					return
				}
			}
		}
		when {
			Windows -> {
				if (File("adb.exe").exists() && File("fastboot.exe").exists() && File("AdbWinApi.dll").exists() && File(
						"AdbWinUsbApi.dll"
					).exists()
				) {
					SdkDir = "$JarDir\\"
					return
				} else if (File("$JarDir\\SDK-Tools\\adb.exe").exists() && File("$JarDir\\SDK-Tools\\fastboot.exe").exists() && File(
						"$JarDir\\SDK-Tools\\AdbWinApi.dll"
					).exists() && File("$JarDir\\SDK-Tools\\AdbWinUsbApi.dll").exists()
				) {
					SdkDir = "$JarDir\\SDK-Tools\\"
					return
				}
			}
			else -> {
				if (File("adb").exists() && File("fastboot").exists()) {
					SdkDir = "$JarDir/"
					return
				} else if (File("$JarDir/SDK-Tools/adb").exists() && File("$JarDir/SDK-Tools/fastboot").exists()) {
					SdkDir = "$JarDir/SDK-Tools/"
					return
				}
			}
		}
		SDKDialog.main()
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
			if (!armArch)
				FastbootDevicesOutput = exec("fastboot", "devices", output = true)
		}

		ConnectedViaFastboot = "fastboot" in FastbootDevicesOutput
		ConnectedViaRecovery = "recovery" in GetStateOutput
		ConnectedViaAdb = "device" in GetStateOutput
		ConnectedAdbUsb = "192.168" !in AdbDevicesOutput
		ConnectedAdbWifi = "offline" !in GetStateOutput
		UnauthorizedDevice = "unauthorized" in AdbDevicesOutput
		MultipleDevicesConnected = "error: more than one device/emulator" in GetStateErrorOutput


		if (MultipleDevicesConnected)
			MultipleDevices.main()
		else if (UnauthorizedDevice)
			UnathorizedDevice.main()

		when {
			ConnectedViaAdb -> {
				a.shutdownButton.isEnabled = true
				a.rebootButton.isEnabled = true
				a.rebootToRecoveryButton.isEnabled = true
				a.rebootToFastbootButton.isEnabled = true
				if (enabledAll) {
					val disableComponents: Array<Component> = a.fastbootPanel.components + a.recoveryPanel.components
					for (component in disableComponents)
						if (component != a.openSystemTerminalButton)
							component.isEnabled = false
					val enableComponents: Array<Component> =
						a.adbPanel.components + a.consolePanel.components + a.logsPanel.components
					for (component in enableComponents)
						if (component != a.stopButton && component != a.saveButton1 && component != a.installButton && component != a.installButton1)
							component.isEnabled = true
				}
				a.textArea1.isFocusable = true
				a.textArea2.isFocusable = true
				a.list2.isFocusable = true
				a.list1.isFocusable = true

				if (ConnectedAdbUsb) {
					a.notConnectedLabel1.text = "Connected via Adb"
					a.notConnectedLabel1.icon = iconYes
					a.connectButton.isEnabled = false
					a.textField2.isEnabled = false
					a.buttonResetPort.isVisible = true
				} else {
					if (ConnectedAdbWifi) {
						a.notConnectedLabel1.text = "Connected to ${
							AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
						}"
						a.notConnectedLabel1.icon = iconYes
					}
				}
				if (newPhone) {
					a.tabbedPane1.selectedIndex = 0
					getProp()
					getListOfPackages()
				}
				newPhone = false
				enabledAll = false
			}
			ConnectedViaFastboot -> {
				a.shutdownButton.isEnabled = false
				a.rebootButton.isEnabled = true
				a.rebootToRecoveryButton.isEnabled = true
				a.rebootToFastbootButton.isEnabled = true
				a.connectButton.isEnabled = false
				a.buttonResetPort.isVisible = false
				if (enabledAll) {
					val disableComponents: Array<Component> =
						a.adbPanel.components + a.logsPanel.components + a.recoveryPanel.components
					for (component in disableComponents)
						if (component != a.openSystemTerminalButton)
							component.isEnabled = false
					val enableComponents: Array<Component> = a.fastbootPanel.components + a.consolePanel.components
					for (component in enableComponents)
						if (component != a.stopButton && component != a.saveButton1)
							component.isEnabled = true
				}
				a.textArea1.isFocusable = false
				a.textArea2.isFocusable = false
				a.list2.isFocusable = false
				a.list1.isFocusable = false

				if (newPhone) {
					a.tabbedPane1.selectedIndex = 3
					a.notConnectedLabel1.text = "Connected via Fastboot"
					a.notConnectedLabel1.icon = iconYes
					getPropFastboot()
				}
				newPhone = false
				enabledAll = false
			}
			ConnectedViaRecovery -> {
				a.shutdownButton.isEnabled = true
				a.rebootButton.isEnabled = true
				a.rebootToRecoveryButton.isEnabled = true
				a.rebootToFastbootButton.isEnabled = true
				a.connectButton.isEnabled = false
				a.buttonResetPort.isVisible = false
				if (enabledAll) {
					val disableComponents: Array<Component> = a.adbPanel.components + a.fastbootPanel.components
					for (component in disableComponents)
						if (component != a.openSystemTerminalButton)
							component.isEnabled = false
					val enableComponents: Array<Component> =
						a.recoveryPanel.components + a.consolePanel.components + a.logsPanel.components
					for (component in enableComponents)
						if (component != a.stopButton && component != a.saveButton1)
							component.isEnabled = true
				}
				a.textArea2.isFocusable = false
				a.textArea1.isFocusable = false
				a.list1.isFocusable = false
				a.list2.isFocusable = false

				if (newPhone) {
					a.tabbedPane1.selectedIndex = 4
					a.notConnectedLabel1.text = "Connected via Adb"
					a.notConnectedLabel1.icon = iconYes
					getPropRecovery()
				}

				newPhone = false
				enabledAll = false
			}
			else -> {
				a.buttonResetPort.isVisible = false
				a.shutdownButton.isEnabled = false
				a.rebootButton.isEnabled = false
				a.rebootToRecoveryButton.isEnabled = false
				a.rebootToFastbootButton.isEnabled = false
				a.connectButton.isEnabled = false
				enabledAll = true
				newPhone = true
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
		Selinux =
			if (lineValue14 == deviceProps || "DEVICE" in lineValue14) "Unknown" else lineValue14.substringBefore(']')
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
		SecureBoot = if (lineValue16 == deviceProps) "Unknown" else {
			if (lineValue16.substringBefore(']') == "1") "true" else "false"
		}
		val lineValue17 = deviceProps.substringAfter("ro.build.host]: [")
		DeviceHost = if (lineValue17 == deviceProps) "Unknown" else lineValue17.substringBefore(']')
		val lineValue18 = deviceProps.substringAfter("ro.allow.mock.location]: [")
		MockLocation = if (lineValue18 == deviceProps) "Unknown" else {
			if (lineValue18.substringBefore(']') == "1") "true" else "false"
		}
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
		FastbootCodename =
			fastbootProps.substringAfter("(bootloader) product:").substringBefore("(bootloader) ").trimMargin()
		FastbootSN =
			fastbootProps.substringAfter("(bootloader) serialno:").substringBefore("(bootloader) ").trimMargin()
		SystemFS = fastbootProps.substringAfter("(bootloader) partition-type:system:").substringBefore("(bootloader) ")
			.trimMargin()
		val systemDec =
			fastbootProps.substringAfter("(bootloader) partition-size:system: 0x").substringBefore("(bootloader) ")
				.trimMargin()
		SystemCapacity = (java.lang.Long.parseLong(systemDec, 16) / 1048576).toString().trimMargin()
		DataFS = fastbootProps.substringAfter("(bootloader) partition-type:userdata:").substringBefore("(bootloader) ")
			.trimMargin()
		val dataDec =
			fastbootProps.substringAfter("(bootloader) partition-size:userdata: 0x").substringBefore("(bootloader) ")
				.trimMargin()
		DataCapacity = (java.lang.Long.parseLong(dataDec, 16) / 1048576).toString().trimMargin()
		BootFS = fastbootProps.substringAfter("(bootloader) partition-type:boot:").substringBefore("(bootloader) ")
			.trimMargin()
		val bootDec =
			fastbootProps.substringAfter("(bootloader) partition-size:boot: 0x").substringBefore("(bootloader) ")
				.trimMargin()
		BootCapacity = (java.lang.Long.parseLong(bootDec, 16) / 1048576).toString().trimMargin()
		RecoveryFS =
			fastbootProps.substringAfter("(bootloader) partition-type:recovery:").substringBefore("(bootloader) ")
				.trimMargin()
		val recoveryDec =
			fastbootProps.substringAfter("(bootloader) partition-size:recovery: 0x").substringBefore("(bootloader) ")
				.trimMargin()
		RecoveryCapacity = (java.lang.Long.parseLong(recoveryDec, 16) / 1048576).toString().trimMargin()
		CacheFS = fastbootProps.substringAfter("(bootloader) partition-type:cache:").substringBefore("(bootloader) ")
			.trimMargin()
		val cacheDec =
			fastbootProps.substringAfter("(bootloader) partition-size:cache: 0x").substringBefore("(bootloader) ")
				.trimMargin()
		CacheCapacity = (java.lang.Long.parseLong(cacheDec, 16) / 1048576).toString().trimMargin()
		VendorFS = fastbootProps.substringAfter("(bootloader) partition-type:vendor:").substringBefore("(bootloader) ")
			.trimMargin()
		val vendorDec =
			fastbootProps.substringAfter("(bootloader) partition-size:vendor: 0x").substringBefore("(bootloader) ")
				.trimMargin()
		VendorCapacity = (java.lang.Long.parseLong(vendorDec, 16) / 1048576).toString()
		AllCapacity =
			(SystemCapacity.toInt() + DataCapacity.toInt() + BootCapacity.toInt() + RecoveryCapacity.toInt() + CacheCapacity.toInt() + VendorCapacity.toInt()).toString()
		model.addRow(arrayOf("Unlocked", if (Unlock != "< waiting for any device >") Unlock else "-"))
		model.addRow(
			arrayOf(
				"Codename",
				if (FastbootCodename != "< waiting for any device >") FastbootCodename else "-"
			)
		)
		model.addRow(arrayOf("Serial Number", if (FastbootSN != "< waiting for any device >") FastbootSN else "-"))
		model.addRow(arrayOf("/system File system:", if (SystemFS != "< waiting for any device >") SystemFS else "-"))
		model.addRow(
			arrayOf(
				"/system Capacity (MB):",
				if (SystemCapacity != "< waiting for any device >") SystemCapacity else "-"
			)
		)
		model.addRow(arrayOf("/data File system:", if (DataFS != "< waiting for any device >") DataFS else "-"))
		model.addRow(
			arrayOf(
				"/data Capacity (MB):",
				if (DataCapacity != "< waiting for any device >") DataCapacity else "-"
			)
		)
		model.addRow(arrayOf("/boot File system:", if (BootFS != "< waiting for any device >") BootFS else "-"))
		model.addRow(
			arrayOf(
				"/boot Capacity (MB):",
				if (BootCapacity != "< waiting for any device >") BootCapacity else "-"
			)
		)
		model.addRow(
			arrayOf(
				"/recovery File system:",
				if (RecoveryFS != "< waiting for any device >") RecoveryFS else "-"
			)
		)
		model.addRow(
			arrayOf(
				"/recovery Capacity (MB):",
				if (RecoveryCapacity != "< waiting for any device >") RecoveryCapacity else "-"
			)
		)
		model.addRow(arrayOf("/cache File system:", if (CacheFS != "< waiting for any device >") CacheFS else "-"))
		model.addRow(
			arrayOf(
				"/cache Capacity (MB):",
				if (CacheCapacity != "< waiting for any device >") CacheCapacity else "-"
			)
		)
		model.addRow(arrayOf("/vendor File system:", if (VendorFS != "< waiting for any device >") VendorFS else "-"))
		model.addRow(
			arrayOf(
				"/vendor Capacity (MB):",
				if (VendorCapacity != "< waiting for any device >") VendorCapacity else "-"
			)
		)
		model.addRow(
			arrayOf(
				"All Capacity (MB):",
				if (AllCapacity != "< waiting for any device >") AllCapacity else "-"
			)
		)
	}
	fun runUrl(url: String) {
		val urlString = URI(url)
		Desktop.getDesktop().browse(urlString)
	}
	fun runUpdate() {
		GlobalScope.launch {
			runUrl("https://github.com/fast-geek/Android-Tool/releases/latest")
		}
		Runtime.getRuntime().exec("${SdkDir}adb kill-server")
		exitProcess(0)
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
	fun runSDK(progress : JProgressBar, label : JLabel){
		progress.isIndeterminate = true
		label.text = "Installing..."
		GlobalScope.launch {
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
					"https://github.com/fast-geek/SDK-Platform-Tools/blob/68198653d62b008fcb9c4bb01c31a9f5bfcea857/MacOS.zip?raw=true",
					"$SdkDir/MacOS.zip"
				)
			}
			when {
				Windows -> unZipFile("$SdkDir\\Windows.zip")
				Linux -> unZipFile("$SdkDir/Linux.zip")
				MacOS -> unZipFile("$SdkDir/MacOS.zip")
			}
			when {
				Windows -> SdkDir = "$userFolder\\.android_tool\\SDK-Tools\\"
				else -> SdkDir = "$userFolder/.android_tool/SDK-Tools/"
			}
			progress.isIndeterminate = false
			label.text = "Completed!"
		}
	}
	fun execLines(command: String): List<String> {
		val process = Runtime.getRuntime().exec("$SdkDir$command")
		return process.inputStream.bufferedReader().readLines()
	}

	private fun noConnection() {
		val components: Array<Component> =
			a.fastbootPanel.components + a.adbPanel.components + a.logsPanel.components + a.consolePanel.components + a.recoveryPanel.components
		for (component in components)
			if (component != a.openSystemTerminalButton)
				component.isEnabled = false
		a.textArea1.isFocusable = false
		a.textArea2.isFocusable = false
		a.list2.isFocusable = false
		a.list1.isFocusable = false
		listModel.removeAllElements()
		listModelLogs.removeAllElements()
//		if (a.table1.model.rowCount != 0)
//			for (i in a.table1.model.rowCount - 1 downTo 0)
//				a.table1.model.removeRow(i)
		a.textField1.text = ""
		a.textField1.isEnabled = false
		a.connectButton.isEnabled = true
		a.textField2.isEnabled = true
		a.notConnectedLabel1.text = "Not connected"
		a.notConnectedLabel1.icon = null
	}

	fun getListOfPackages(button: Boolean = false) {
		if (button)
			a.refreshButton.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val items: DefaultListModel<Any?> = DefaultListModel()
			a.textField1.isFocusable = true
			arrayList.clear()
			apps.clear()
			val reader = when {
				a.disabledRadioButton.isSelected -> execLines("adb shell pm list packages -d")
				a.systemRadioButton.isSelected -> execLines("adb shell pm list packages -s")
				a.enabledRadioButton.isSelected -> execLines("adb shell pm list packages -e")
				a.thirdRadioButton.isSelected -> execLines("adb shell pm list packages -3")
				else -> execLines("adb shell pm list packages")
			}
			for (element in reader) {
				if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element && "not access" !in element) {
					if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
						arrayList.add(
							if (appProp.getProperty(element.substring(8)) != null)
								"${element.substring(8)} (${appProp.getProperty(element.substring(8), "")})"
							else
								element.substring(8)
						)
					}
				}
			}
			arrayList.sort()
			if (button)
				a.refreshButton.isEnabled = true
			listModel.removeAllElements()
			for (element in arrayList) {
				if (a.textField1.text == "")
					items.addElement(element)
				apps.add(element)
			}
			listModel = items
			a.list1.model = listModel
			a.refreshButton.isEnabled = true
			searchFilter(a.textField1.text)
		}
	}

	fun updateUI(){
		SwingUtilities.updateComponentTreeUI(frame)
	}

	companion object : AndroidTool() {
		@OptIn(DelicateCoroutinesApi::class)
		@JvmStatic
		fun main(args: Array<String>) {
			a.list2.model = listModelLogs
			a.textField1.addKeyListener(object : KeyAdapter() {
				override fun keyReleased(evt: KeyEvent) {
					searchFilter(a.textField1.text)
				}
			})
			frame.addWindowListener(object : WindowAdapter() {
				override fun windowClosing(e: WindowEvent) {
					exec("adb", "kill-server")
				}
			})
			a.table1.model = model
			a.table1.showHorizontalLines = true
			a.table1.showVerticalLines = true
			a.table1.setDefaultEditor(Any::class.java, null)
			model.addColumn("Property")
			model.addColumn("Value")
			a.saveButton1.addActionListener {
				a.saveButton1.isEnabled = false
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
					a.saveButton1.isEnabled = true
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
//			tabbedpane.addChangeListener {
//				try {
//					textFieldIP.text =
//						AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
//				} catch (e: Exception) {
//				}
//				if (tabbedpane.selectedIndex == 0 || tabbedpane.selectedIndex == 1) {
//					contents.setBounds(5, 5, 310, 375)
//					deviceControlPanel.setBounds(5, 385, 310, 85)
//					deviceConnection.setBounds(5, 475, 310, 100)
//					labelTCP.isVisible = true
//					labelTCPConnection.isVisible = true
//					buttonIpConnect.isVisible = true
//					textFieldIP.isVisible = true
//					labelConnect.isVisible = true
//					labelIP.isVisible = true
//				} else if (tabbedpane.selectedIndex == 2) {
//					contents.setBounds(5, 5, 310, 425)
//					deviceControlPanel.setBounds(5, 435, 310, 85)
//					deviceConnection.setBounds(5, 525, 310, 50)
//					labelTCP.isVisible = false
//					labelTCPConnection.isVisible = false
//					buttonIpConnect.isVisible = false
//					textFieldIP.isVisible = false
//					labelConnect.isVisible = false
//					labelIP.isVisible = false
//				} else if (tabbedpane.selectedIndex == 3) {
//					contents.setBounds(5, 5, 310, 425)
//					deviceControlPanel.setBounds(5, 435, 310, 85)
//					deviceConnection.setBounds(5, 525, 310, 50)
//					labelTCP.isVisible = false
//					labelTCPConnection.isVisible = false
//					buttonIpConnect.isVisible = false
//					textFieldIP.isVisible = false
//					labelConnect.isVisible = false
//					labelIP.isVisible = false
//				} else if (tabbedpane.selectedIndex == 4) {
//					contents.setBounds(5, 5, 310, 375)
//					deviceControlPanel.setBounds(5, 385, 310, 85)
//					deviceConnection.setBounds(5, 475, 310, 100)
//					labelTCP.isVisible = true
//					labelTCPConnection.isVisible = true
//					buttonIpConnect.isVisible = true
//					textFieldIP.isVisible = true
//					labelConnect.isVisible = true
//					labelIP.isVisible = true
//				}
//			}
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
				AppsControl("Disable", a.disableButton, a.list1)
				getListOfPackages()
			}
			a.uninstallButton.addActionListener {
				AppsControl("Uninstall", a.uninstallButton, a.list1)
				getListOfPackages()
			}
			a.enableButton.addActionListener {
				AppsControl("Enable", a.enableButton, a.list1)
				getListOfPackages()
			}
			a.clearDataButton.addActionListener {
				AppsControl("Clear", a.clearDataButton, a.list1)
			}
			a.openButton.addActionListener {
				AppsControl("Open", a.openButton, a.list1)
			}
			a.forceStopButton.addActionListener {
				AppsControl("Stop", a.forceStopButton, a.list1)
			}
			a.refreshButton.addActionListener {
				getListOfPackages(true)
			}
			a.saveButton.addActionListener {
				a.saveButton.isEnabled = false
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
					a.saveButton.isEnabled = true
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
            fileMenu.add(settingsMenu)
			settingsMenu.addActionListener{
				Settings.main()
			}
			fileMenu.add(aboutItem)
			aboutItem.addActionListener{
				AboutDialog.main()
			}
			fileMenu.addSeparator()
			fileMenu.add(exitItem)
			exitItem.addActionListener {
				exec("adb", "kill-server")
				exitProcess(0)
			}
            menuBar.add(fileMenu)

			frame.jMenuBar = menuBar
			appProp.load(AndroidTool::class.java.getResourceAsStream("applist.properties"))
			val components: Array<Component> =
				a.fastbootPanel.components + a.adbPanel.components + a.logsPanel.components + a.consolePanel.components + a.recoveryPanel.components + a.devicePanel.components
			for (component in components)
				if (component != a.openSystemTerminalButton)
					component.isEnabled = false
			frame.iconImage = appIcon
			frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			frame.contentPane = a.ATPanel
			frame.setSize(1100, 650)
			frame.setLocationRelativeTo(null)
			frame.isVisible = true

			when {
				Windows -> systemIP = InetAddress.getLocalHost().hostAddress
				MacOS -> systemIP = Runtime.getRuntime().exec("ipconfig getifaddr en0").inputStream.bufferedReader().readLine()
				Linux -> systemIP = Runtime.getRuntime().exec("hostname -I").inputStream.bufferedReader().readLine()
			}
			systemIP = systemIP.substringBeforeLast('.') + "."
			a.textField2.text = systemIP
			sdkCheck()
			GlobalScope.launch {
				while (true) {
					connectionCheck()
					delay(1000)
				}
			}
			versionCheck()
		}
	}
}
