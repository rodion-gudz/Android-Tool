import AndroidTool.Companion.at_form
import java.awt.Component

private fun noConnection() {
	val components: Array<Component> =
		at_form.fastbootPanel.components + at_form.adbPanel.components + at_form.logsPanel.components + at_form.consolePanel.components + at_form.recoveryPanel.components
	for (component in components)
		if (component != at_form.open_system_terminal_button)
			component.isEnabled = false
	at_form.inputArea.isFocusable = false
	at_form.outputArea.isFocusable = false
	at_form.logsList.isFocusable = false
	at_form.list1.isFocusable = false
	apps_list_model.removeAllElements()
	logs_list_model.removeAllElements()
	if (device_properties_model.rowCount != 0)
		for (i in device_properties_model.rowCount - 1 downTo 0)
			device_properties_model.removeRow(i)
	at_form.apps_filter_textfield.text = ""
	at_form.apps_filter_textfield.isEnabled = false
	at_form.connect_to_device_button.isEnabled = true
	at_form.system_ip_address_field.isEnabled = true
	at_form.notConnectedLabel1.text = "Not connected"
	at_form.notConnectedLabel1.icon = null
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
					at_form.fastbootPanel.components + at_form.recoveryPanel.components
				for (component in disableComponents)
					if (component != at_form.open_system_terminal_button)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					at_form.adbPanel.components + at_form.consolePanel.components + at_form.logsPanel.components
				for (component in enableComponents)
					if (component != at_form.stop_logs_button && component != at_form.save_logs_button && component != at_form.install_multiple_apps_button && component != at_form.install_one_app_button)
						component.isEnabled = true
			}
			at_form.inputArea.isFocusable = true
			at_form.outputArea.isFocusable = true
			at_form.logsList.isFocusable = true
			at_form.list1.isFocusable = true

			if (connected_via_USB) {
				at_form.notConnectedLabel1.text = "Connected via Adb"
				at_form.notConnectedLabel1.icon = success_icon
				at_form.connect_to_device_button.isEnabled = false
				at_form.system_ip_address_field.isEnabled = false
				at_form.change_wireless_port_button.isVisible = true
			} else {
				if (connected_via_WiFi) {
					at_form.notConnectedLabel1.text = "Connected to ${
						adb_devices.substring(adb_devices.indexOf("192.168")).substringBefore(':')
					}"
					at_form.notConnectedLabel1.icon = success_icon
				}
			}
			if (newPhone) {
				at_form.tabbedPane1.selectedIndex = 0
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
					at_form.adbPanel.components + at_form.logsPanel.components + at_form.recoveryPanel.components
				for (component in disableComponents)
					if (component != at_form.open_system_terminal_button)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					at_form.fastbootPanel.components + at_form.consolePanel.components
				for (component in enableComponents)
					component.isEnabled = true
			}
			at_form.inputArea.isFocusable = true
			at_form.outputArea.isFocusable = true
			at_form.logsList.isFocusable = false
			at_form.list1.isFocusable = false

			if (newPhone) {
				at_form.tabbedPane1.selectedIndex = 2
				at_form.notConnectedLabel1.text = "Connected via Fastboot"
				at_form.notConnectedLabel1.icon = success_icon
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
				val disableComponents: Array<Component> = at_form.adbPanel.components + at_form.fastbootPanel.components
				for (component in disableComponents)
					if (component != at_form.open_system_terminal_button)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					at_form.recoveryPanel.components + at_form.consolePanel.components + at_form.logsPanel.components
				for (component in enableComponents)
					component.isEnabled = true
			}
			at_form.outputArea.isFocusable = true
			at_form.inputArea.isFocusable = true
			at_form.list1.isFocusable = false
			at_form.logsList.isFocusable = false

			if (newPhone) {
				at_form.tabbedPane1.selectedIndex = 3
				at_form.notConnectedLabel1.text = "Connected via Adb"
				at_form.notConnectedLabel1.icon = success_icon
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
