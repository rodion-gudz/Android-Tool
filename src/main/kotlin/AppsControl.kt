import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import javax.swing.JButton
import javax.swing.JList

fun app(func: Any, button: JButton, list: JList<Any?>) {
	val app = list.selectedValue.toString().substringBefore("(")
	val command = when (func) {
		"Open" -> "shell monkey -p $app 1"
		"Stop" -> "shell am force-stop $app"
		"Clear" -> "shell pm clear $app"
		"Enable" -> "shell pm enable $app"
		"Uninstall" -> "shell pm uninstall --user 0 $app"
		"Disable" -> "shell pm disable-user --user 0 $app"
		else -> ""
	}
	button.isEnabled = false
	GlobalScope.launch(Dispatchers.Swing) {
		exec("adb", command)
		button.isEnabled = true
	}
}