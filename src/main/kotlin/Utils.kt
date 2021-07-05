import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.maven.artifact.versioning.ComparableVersion
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URL
import java.util.*
import javax.swing.DefaultListModel
import kotlin.system.exitProcess
import AndroidTool.Companion.a
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JProgressBar
import javax.swing.SwingUtilities


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

fun versionCheck() {
	try {
		val properties = Properties()
		val inputStream =
			URL("https://raw.githubusercontent.com/fast-geek/Android-Tool/master/values.properties").openStream()
		properties.load(inputStream)
		if (ComparableVersion(properties.getProperty("latestVersion")) > ComparableVersion(programVersion)) {
			programVersionLatest = properties.getProperty("latestVersion")
			UpdateDialog.main(programVersion, programVersionLatest)
		}
	} catch (e: Exception) {
		print(e)
	}
}

fun runSDK(progress: JProgressBar, label: JLabel) {
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

fun updateUI(){
	SwingUtilities.updateComponentTreeUI(AndroidTool.frame)
}

fun desableCompoments(){
	val components: Array<Component> =
		a.fastbootPanel.components + a.adbPanel.components + a.logsPanel.components + a.consolePanel.components + a.recoveryPanel.components + a.devicePanel.components
	for (component in components)
		if (component != a.openSystemTerminalButton)
			component.isEnabled = false
}