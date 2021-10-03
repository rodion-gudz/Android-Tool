import AndroidTool.Companion.at_form
import java.awt.Component

private fun noConnection() {
	val components: Array<Component> =
		at_form.fastboot_panel.components + at_form.adb_panel.components + at_form.logs_panel.components + at_form.console_panel.components + at_form.recovery_panel.components
	for (component in components)
		if (component != at_form.open_system_terminal_button)
			component.isEnabled = false
	at_form.command_input.isFocusable = false
	at_form.command_output.isFocusable = false
	at_form.logs_list.isFocusable = false
	at_form.apps_list.isFocusable = false
	apps_list_model.removeAllElements()
	logs_list_model.removeAllElements()
	if (device_properties_model.rowCount != 0)
		for (i in device_properties_model.rowCount - 1 downTo 0)
			device_properties_model.removeRow(i)
	at_form.apps_filter_textfield.text = ""
	at_form.apps_filter_textfield.isEnabled = false
	at_form.connect_to_device_button.isEnabled = true
	at_form.system_ip_address_field.isEnabled = true
	at_form.connection_label.text = "Not connected"
	at_form.connection_label.icon = null
}

fun connectionCheck() {
	if (!CommandRunning) {
		device_state = exec("adb", "get-state", output = true)
		device_state_error = exec("adb", "get-state", streamType = "Error", output = true)
		adb_devices = exec("adb", "devices", output = true)
		if (!cpu_arm_arch)
			fastboot_devices = exec("fastboot", "devices", output = true)
	}

	connected_via_fastboot = "fastboot" in fastboot_devices
	connected_via_recovery = "recovery" in device_state
	connected_via_adb = "device" in device_state
	connected_via_USB = "192.168" !in adb_devices
	connected_via_WiFi = "offline" !in device_state
	unauthorized_device = "unauthorized" in adb_devices
	multiple_devices_connected = "error: more than one device/emulator" in device_state_error


	if (multiple_devices_connected)
		multiple_devices_dialog.main()
	else if (unauthorized_device)
		unauthorized_device_dialog.main()

	when {
		connected_via_adb -> {
			at_form.shutdown_device_button.isEnabled = true
			at_form.reboot_to_system_button.isEnabled = true
			at_form.reboot_to_recovery_device_button.isEnabled = true
			at_form.reboot_to_fastboot_device_button.isEnabled = true
			if (enabledAll) {
				val disableComponents: Array<Component> =
					at_form.fastboot_panel.components + at_form.recovery_panel.components
				for (component in disableComponents)
					if (component != at_form.open_system_terminal_button)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					at_form.adb_panel.components + at_form.console_panel.components + at_form.logs_panel.components
				for (component in enableComponents)
					if (component != at_form.stop_logs_button && component != at_form.save_logs_button && component != at_form.install_multiple_apps_button && component != at_form.install_one_app_button)
						component.isEnabled = true
			}
			at_form.command_input.isFocusable = true
			at_form.command_output.isFocusable = true
			at_form.logs_list.isFocusable = true
			at_form.apps_list.isFocusable = true

			if (connected_via_USB) {
				at_form.connection_label.text = "Connected via Adb"
				at_form.connection_label.icon = success_icon
				at_form.connect_to_device_button.isEnabled = false
				at_form.system_ip_address_field.isEnabled = false
				at_form.change_wireless_port_button.isVisible = true
			} else {
				if (connected_via_WiFi) {
					at_form.connection_label.text = "Connected to ${
						adb_devices.substring(adb_devices.indexOf("192.168")).substringBefore(':')
					}"
					at_form.connection_label.icon = success_icon
				}
			}
			if (newPhone) {
				at_form.tabbed_pane.selectedIndex = 0
				getProp()
				getListOfPackages()
			}
			newPhone = false
			enabledAll = false
		}
		connected_via_fastboot -> {
			at_form.shutdown_device_button.isEnabled = false
			at_form.reboot_to_system_button.isEnabled = true
			at_form.reboot_to_recovery_device_button.isEnabled = true
			at_form.reboot_to_fastboot_device_button.isEnabled = true
			at_form.connect_to_device_button.isEnabled = false
			at_form.change_wireless_port_button.isVisible = false
			if (enabledAll) {
				val disableComponents: Array<Component> =
					at_form.adb_panel.components + at_form.logs_panel.components + at_form.recovery_panel.components
				for (component in disableComponents)
					if (component != at_form.open_system_terminal_button)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					at_form.fastboot_panel.components + at_form.console_panel.components
				for (component in enableComponents)
					component.isEnabled = true
			}
			at_form.command_input.isFocusable = true
			at_form.command_output.isFocusable = true
			at_form.logs_list.isFocusable = false
			at_form.apps_list.isFocusable = false

			if (newPhone) {
				at_form.tabbed_pane.selectedIndex = 2
				at_form.connection_label.text = "Connected via Fastboot"
				at_form.connection_label.icon = success_icon
				getPropFastboot()
			}
			newPhone = false
			enabledAll = false
		}
		connected_via_recovery -> {
			at_form.shutdown_device_button.isEnabled = true
			at_form.reboot_to_system_button.isEnabled = true
			at_form.reboot_to_recovery_device_button.isEnabled = true
			at_form.reboot_to_fastboot_device_button.isEnabled = true
			at_form.connect_to_device_button.isEnabled = false
			at_form.change_wireless_port_button.isVisible = false
			if (enabledAll) {
				val disableComponents: Array<Component> =
					at_form.adb_panel.components + at_form.fastboot_panel.components
				for (component in disableComponents)
					if (component != at_form.open_system_terminal_button)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					at_form.recovery_panel.components + at_form.console_panel.components + at_form.logs_panel.components
				for (component in enableComponents)
					component.isEnabled = true
			}
			at_form.command_output.isFocusable = true
			at_form.command_input.isFocusable = true
			at_form.apps_list.isFocusable = false
			at_form.logs_list.isFocusable = false

			if (newPhone) {
				at_form.tabbed_pane.selectedIndex = 3
				at_form.connection_label.text = "Connected via Adb"
				at_form.connection_label.icon = success_icon
				getPropRecovery()
			}

			newPhone = false
			enabledAll = false
		}
		else -> {
			at_form.change_wireless_port_button.isVisible = false
			at_form.shutdown_device_button.isEnabled = false
			at_form.reboot_to_system_button.isEnabled = false
			at_form.reboot_to_recovery_device_button.isEnabled = false
			at_form.reboot_to_fastboot_device_button.isEnabled = false
			at_form.connect_to_device_button.isEnabled = false
			enabledAll = true
			newPhone = true
			noConnection()
		}
	}
}
