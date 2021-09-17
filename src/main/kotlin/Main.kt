import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import kotlinx.coroutines.DelicateCoroutinesApi
import java.awt.Image
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.table.DefaultTableModel


var apps_list = ArrayList<String>()
var selected_directory_path = ""
var selected_file_path = ""
var selected_IMG_path = ""
var selected_ZIP_path = ""
var selected_ZIP_name = ""
var apps_list_model = DefaultListModel<Any?>()
var logs_list_model = DefaultListModel<Any?>()
var filtered_apps_list: ArrayList<Any> = ArrayList()
var system_IP_address = ""
var newPhone = true
var enabledAll = true
var device_state = ""
var device_state_error = ""
var adb_devices = ""
var fastboot_devices = ""
var connected_via_adb = false
var connected_via_fastboot = false
var connected_via_recovery = false
var unauthorized_device = false
var multiple_devices_connected = false
var CommandRunning = false
var connected_via_USB = false
var connected_via_WiFi = false
var functionButtonStart = true
var ifStopSelected = false
var logsWorking: Boolean = false
var success_icon = ImageIcon(AndroidTool()::class.java.getResource("connectionSuccess.png"))
val app_icon: Image = ImageIcon(AndroidTool::class.java.getResource("appIcon.png")).image
val windows = "Windows" in System.getProperty("os.name")
val linux = "Linux" in System.getProperty("os.name")
val macos = "Mac" in System.getProperty("os.name")
val jar_folder = System.getProperty("user.dir").toString()
val user_folder = System.getProperty("user.home").toString()
val cpu_arm_arch = "aarch64" == System.getProperty("os.arch").toString()
val program_folder: String = user_folder + if (windows) "\\.android_tool\\" else "/.android_tool/"
var SDK_folder = program_folder + if (windows) "SDK-Tools\\" else "SDK-Tools/"
val device_properties_model = DefaultTableModel()
var program_version = ""
val values_properties = Properties()
var latest_program_version = ""
val app_names_list = Properties()
val menu_bar = JMenuBar()
val menu_bar_main = JMenu("Program")
val menu_bar_about = JMenuItem("About")
val menu_bar_exit = JMenuItem("Exit")
val menu_bar_settings = JMenuItem("Settings")


open class AndroidTool {

	init {
		System.setProperty("apple.awt.application.name", "Android-Tool")
		System.setProperty("apple.awt.application.appearance", "system")
		System.setProperty("apple.laf.useScreenMenuBar", "true")
		createFolder()
		UIManager.put("Button.arc", 6)
		UIManager.put("Component.arc", 6)
		UIManager.put("CheckBox.arc", 6)
		UIManager.put("ProgressBar.arc", 6)
		UIManager.put("TextComponent.arc", 6)
		JFrame.setDefaultLookAndFeelDecorated(true)
		JDialog.setDefaultLookAndFeelDecorated(true)
		val theme = getSettings()
		if (theme == "dark")
			UIManager.setLookAndFeel(FlatDarculaLaf())
		else if (theme == "white")
			UIManager.setLookAndFeel(FlatIntelliJLaf())
	}

	var at_form = ATForm()
	val frame = JFrame()

	companion object : AndroidTool() {
		@OptIn(DelicateCoroutinesApi::class)
		@JvmStatic
		fun main() {
			if (macos) {
				val application = com.apple.eawt.Application.getApplication()
				val image = ImageIO.read(AndroidTool()::class.java.getResource("appIcon.png"))
				application.dockIconImage = image
				application.setAboutHandler { AboutDialog.main() }
			}
			frame.iconImage = app_icon
			frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			frame.contentPane = at_form.ATPanel
			frame.setSize(1100, 650)
			frame.setLocationRelativeTo(null)
			frame.isVisible = true
		}
	}
}
