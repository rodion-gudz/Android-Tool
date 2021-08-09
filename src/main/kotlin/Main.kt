import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import kotlinx.coroutines.DelicateCoroutinesApi
import java.awt.Image
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel


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
val appIcon: Image = ImageIcon(AndroidTool::class.java.getResource("appIcon.png")).image
val Windows = "Windows" in System.getProperty("os.name")
val Linux = "Linux" in System.getProperty("os.name")
val MacOS = "Mac" in System.getProperty("os.name")
val JarDir = System.getProperty("user.dir").toString()
val userFolder = System.getProperty("user.home").toString()
val armArch = "aarch64" == System.getProperty("os.arch").toString()
val ProgramDir: String = userFolder + if (Windows) "\\.android_tool\\" else "/.android_tool/"
var SdkDir = ProgramDir + if (Windows) "SDK-Tools\\" else "SDK-Tools/"
val model = DefaultTableModel()
const val programVersion = "2.0-alpha8"
var programVersionLatest = programVersion
val appProp = Properties()
val menuBar = JMenuBar()
val fileMenu = JMenu("Program")
val aboutItem = JMenuItem("About")
val exitItem = JMenuItem("Exit")
val settingsMenu = JMenuItem("Settings")


open class AndroidTool {

	init {
		createFolder()
		UIManager.put("Button.arc", 6)
		UIManager.put("Component.arc", 6)
		UIManager.put("CheckBox.arc", 6)
		UIManager.put("ProgressBar.arc", 6)
		UIManager.put("TextComponent.arc", 6)
		JFrame.setDefaultLookAndFeelDecorated(true)
		JDialog.setDefaultLookAndFeelDecorated(true)
		System.setProperty("apple.awt.application.appearance", "system")
		System.setProperty("apple.laf.useScreenMenuBar", "true")
		val theme = getSettings()
		if (theme == "dark")
			UIManager.setLookAndFeel(FlatDarculaLaf())
		else if (theme == "white")
			UIManager.setLookAndFeel(FlatIntelliJLaf())
	}

	var atForm = ATForm()
	val frame = JFrame()

	companion object : AndroidTool() {
		@OptIn(DelicateCoroutinesApi::class)
		@JvmStatic
		fun main() {
			frame.iconImage = appIcon
			frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			frame.contentPane = atForm.ATPanel
			frame.setSize(1100, 650)
			frame.setLocationRelativeTo(null)
			frame.isVisible = true
		}
	}
}
