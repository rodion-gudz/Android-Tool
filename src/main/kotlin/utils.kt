import AndroidTool.Companion.at_form
import java.awt.Component
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.SwingUtilities

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
	at_form.apps_list.model = apps_list_model
}


fun updateUI() {
	SwingUtilities.updateComponentTreeUI(AndroidTool.frame)
}

fun disableComponents() {
	val components: Array<Component> =
		at_form.fastboot_panel.components + at_form.adb_panel.components + at_form.logs_panel.components + at_form.console_panel.components + at_form.recovery_panel.components + at_form.device_panel.components
	for (component in components)
		if (component != at_form.open_system_terminal_button)
			component.isEnabled = false
}