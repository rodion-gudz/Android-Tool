import AndroidTool.Companion.at_form
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.maven.artifact.versioning.ComparableVersion
import java.awt.Component
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URL
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JProgressBar
import javax.swing.SwingUtilities
import kotlin.system.exitProcess


fun sdkCheck() {
	when {
		windows -> {
			if (File("$user_folder\\.android_tool\\SDK-Tools\\adb.exe").exists() && File("$user_folder\\.android_tool\\SDK-Tools\\fastboot.exe").exists()) {
				SDK_folder = "$user_folder\\.android_tool\\SDK-Tools\\"
				return
			}
		}
		else -> {
			if (File("$user_folder/.android_tool/SDK-Tools/adb").exists() && File("$user_folder/.android_tool/SDK-Tools/fastboot").exists()) {
				SDK_folder = "$user_folder/.android_tool/SDK-Tools/"
				return
			}
		}
	}
	when {
		windows -> {
			if (File("adb.exe").exists() && File("fastboot.exe").exists() && File("AdbWinApi.dll").exists() && File(
					"AdbWinUsbApi.dll"
				).exists()
			) {
				SDK_folder = "$jar_folder\\"
				return
			} else if (File("$jar_folder\\SDK-Tools\\adb.exe").exists() && File("$jar_folder\\SDK-Tools\\fastboot.exe").exists() && File(
					"$jar_folder\\SDK-Tools\\AdbWinApi.dll"
				).exists() && File("$jar_folder\\SDK-Tools\\AdbWinUsbApi.dll").exists()
			) {
				SDK_folder = "$jar_folder\\SDK-Tools\\"
				return
			}
		}
		else -> {
			if (File("adb").exists() && File("fastboot").exists()) {
				SDK_folder = "$jar_folder/"
				return
			} else if (File("$jar_folder/SDK-Tools/adb").exists() && File("$jar_folder/SDK-Tools/fastboot").exists()) {
				SDK_folder = "$jar_folder/SDK-Tools/"
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

@OptIn(DelicateCoroutinesApi::class)
fun runUpdate() {
	GlobalScope.launch {
		runUrl("https://github.com/fast-geek/Android-Tool/releases/latest/download/Android-Tool.jar")
	}
	Runtime.getRuntime().exec("${SDK_folder}adb kill-server")
	exitProcess(0)
}

fun searchFilter(searchTerm: String) {
	val filteredItems: DefaultListModel<Any?> = DefaultListModel()
	val apps = filtered_apps_list
	apps.stream().forEach { app: Any ->
		val starName = app.toString().lowercase(Locale.getDefault())
		if (starName.contains(searchTerm.lowercase(Locale.getDefault()))) {
			if (!filteredItems.contains(app)) {
				filteredItems.addElement(app)
			}
		}
	}
	apps_list_model = filteredItems
	at_form.list1.model = apps_list_model
}

fun versionCheck() {
	try {
		val properties = Properties()
		val inputStream =
			URL("https://github.com/fast-geek/Android-Tool/releases/latest/download/values.properties").openStream()
		properties.load(inputStream)
		if (ComparableVersion(properties.getProperty("latestVersion")) > ComparableVersion(program_version)) {
			latest_program_version = properties.getProperty("latestVersion")
			UpdateDialog.main(program_version, latest_program_version)
		}
	} catch (e: Exception) {
		print(e)
	}
}

@OptIn(DelicateCoroutinesApi::class)
fun runSDK(progress: JProgressBar, label: JLabel) {
	progress.isIndeterminate = true
	label.text = "Installing..."
	GlobalScope.launch {
		when {
			windows -> downloadFile(
				"https://dl.google.com/android/repository/platform-tools-latest-windows.zip",
				"$program_folder\\Windows.zip"
			)
			linux -> downloadFile(
				"https://dl.google.com/android/repository/platform-tools-latest-linux.zip",
				"$program_folder/Linux.zip"
			)
			macos -> downloadFile(
				"https://dl.google.com/android/repository/platform-tools-latest-darwin.zip",
				"$program_folder/MacOS.zip"
			)
		}
		when {
			windows -> unZipFile("$program_folder\\Windows.zip")
			linux -> unZipFile("$program_folder/Linux.zip")
			macos -> unZipFile("$program_folder/MacOS.zip")
		}
		File("${program_folder}platform-tools").renameTo(File("${program_folder}SDK-Tools"))
		SDK_folder = program_folder + if (windows) "SDK-Tools\\" else "SDK-Tools/"
		progress.isIndeterminate = false
		label.text = "Completed!"
	}
}

fun updateUI() {
	SwingUtilities.updateComponentTreeUI(AndroidTool.frame)
}

fun desableCompoments() {
	val components: Array<Component> =
		at_form.fastbootPanel.components + at_form.adbPanel.components + at_form.logsPanel.components + at_form.consolePanel.components + at_form.recoveryPanel.components + at_form.devicePanel.components
	for (component in components)
		if (component != at_form.open_system_terminal_button)
			component.isEnabled = false
}