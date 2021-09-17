import AndroidTool.Companion.at_form
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import javax.swing.DefaultListModel

@OptIn(DelicateCoroutinesApi::class)
fun getListOfPackages(button: Boolean = false) {
	if (button)
		at_form.refresh_app_list_button.isEnabled = false
	GlobalScope.launch(Dispatchers.Swing) {
		val items: DefaultListModel<Any?> = DefaultListModel()
		at_form.apps_filter_textfield.isFocusable = true
		apps_list.clear()
		filtered_apps_list.clear()
		val reader = when {
			at_form.disable_apps_radiobutton.isSelected -> execLines("adb shell pm list packages -d")
			at_form.system_apps_radiobutton.isSelected -> execLines("adb shell pm list packages -s")
			at_form.enabled_apps_radiobutton.isSelected -> execLines("adb shell pm list packages -e")
			at_form.user_apps_radiobutton.isSelected -> execLines("adb shell pm list packages -3")
			else -> execLines("adb shell pm list packages")
		}
		for (element in reader) {
			if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element && "not access" !in element) {
				if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
					apps_list.add(
						if (app_names_list.getProperty(element.substring(8)) != null)
							"${element.substring(8)} (${app_names_list.getProperty(element.substring(8), "")})"
						else
							element.substring(8)
					)
				}
			}
		}
		apps_list.sort()
		if (button)
			at_form.refresh_app_list_button.isEnabled = true
		apps_list_model.removeAllElements()
		for (element in apps_list) {
			if (at_form.apps_filter_textfield.text == "")
				items.addElement(element)
			filtered_apps_list.add(element)
		}
		apps_list_model = items
		at_form.list1.model = apps_list_model
		at_form.refresh_app_list_button.isEnabled = true
		searchFilter(at_form.apps_filter_textfield.text)
	}
}