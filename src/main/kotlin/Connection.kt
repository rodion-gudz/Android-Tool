import AndroidTool.Companion.atForm
import java.awt.Component

private fun noConnection() {
	val components: Array<Component> =
		atForm.fastbootPanel.components + atForm.adbPanel.components + atForm.logsPanel.components + atForm.consolePanel.components + atForm.recoveryPanel.components
	for (component in components)
		if (component != atForm.openSystemTerminalButton)
			component.isEnabled = false
	atForm.textArea1.isFocusable = false
	atForm.textArea2.isFocusable = false
	atForm.list2.isFocusable = false
	atForm.list1.isFocusable = false
	listModel.removeAllElements()
	listModelLogs.removeAllElements()
	if (model.rowCount != 0)
		for (i in model.rowCount - 1 downTo 0)
			model.removeRow(i)
	atForm.textField1.text = ""
	atForm.textField1.isEnabled = false
	atForm.connectButton.isEnabled = true
	atForm.textField2.isEnabled = true
	atForm.notConnectedLabel1.text = "Not connected"
	atForm.notConnectedLabel1.icon = null
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
			atForm.shutdownButton.isEnabled = true
			atForm.rebootButton.isEnabled = true
			atForm.rebootToRecoveryButton.isEnabled = true
			atForm.rebootToFastbootButton.isEnabled = true
			if (enabledAll) {
				val disableComponents: Array<Component> = atForm.fastbootPanel.components + atForm.recoveryPanel.components
				for (component in disableComponents)
					if (component != atForm.openSystemTerminalButton)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					atForm.adbPanel.components + atForm.consolePanel.components + atForm.logsPanel.components
				for (component in enableComponents)
					if (component != atForm.stopButton && component != atForm.saveButton && component != atForm.installButton && component != atForm.installButton1)
						component.isEnabled = true
			}
			atForm.textArea1.isFocusable = true
			atForm.textArea2.isFocusable = true
			atForm.list2.isFocusable = true
			atForm.list1.isFocusable = true

			if (ConnectedAdbUsb) {
				atForm.notConnectedLabel1.text = "Connected via Adb"
				atForm.notConnectedLabel1.icon = iconYes
				atForm.connectButton.isEnabled = false
				atForm.textField2.isEnabled = false
				atForm.buttonResetPort.isVisible = true
			} else {
				if (ConnectedAdbWifi) {
					atForm.notConnectedLabel1.text = "Connected to ${
						AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
					}"
					atForm.notConnectedLabel1.icon = iconYes
				}
			}
			if (newPhone) {
				atForm.tabbedPane1.selectedIndex = 0
				getProp()
				getListOfPackages()
			}
			newPhone = false
			enabledAll = false
		}
		ConnectedViaFastboot -> {
			atForm.shutdownButton.isEnabled = false
			atForm.rebootButton.isEnabled = true
			atForm.rebootToRecoveryButton.isEnabled = true
			atForm.rebootToFastbootButton.isEnabled = true
			atForm.connectButton.isEnabled = false
			atForm.buttonResetPort.isVisible = false
			if (enabledAll) {
				val disableComponents: Array<Component> =
					atForm.adbPanel.components + atForm.logsPanel.components + atForm.recoveryPanel.components
				for (component in disableComponents)
					if (component != atForm.openSystemTerminalButton)
						component.isEnabled = false
				val enableComponents: Array<Component> = atForm.fastbootPanel.components + atForm.consolePanel.components
				for (component in enableComponents)
					component.isEnabled = true
			}
			atForm.textArea1.isFocusable = false
			atForm.textArea2.isFocusable = false
			atForm.list2.isFocusable = false
			atForm.list1.isFocusable = false

			if (newPhone) {
				atForm.tabbedPane1.selectedIndex = 2
				atForm.notConnectedLabel1.text = "Connected via Fastboot"
				atForm.notConnectedLabel1.icon = iconYes
				getPropFastboot()
			}
			newPhone = false
			enabledAll = false
		}
		ConnectedViaRecovery -> {
			atForm.shutdownButton.isEnabled = true
			atForm.rebootButton.isEnabled = true
			atForm.rebootToRecoveryButton.isEnabled = true
			atForm.rebootToFastbootButton.isEnabled = true
			atForm.connectButton.isEnabled = false
			atForm.buttonResetPort.isVisible = false
			if (enabledAll) {
				val disableComponents: Array<Component> = atForm.adbPanel.components + atForm.fastbootPanel.components
				for (component in disableComponents)
					if (component != atForm.openSystemTerminalButton)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					atForm.recoveryPanel.components + atForm.consolePanel.components + atForm.logsPanel.components
				for (component in enableComponents)
					component.isEnabled = true
			}
			atForm.textArea2.isFocusable = false
			atForm.textArea1.isFocusable = false
			atForm.list1.isFocusable = false
			atForm.list2.isFocusable = false

			if (newPhone) {
				atForm.tabbedPane1.selectedIndex = 3
				atForm.notConnectedLabel1.text = "Connected via Adb"
				atForm.notConnectedLabel1.icon = iconYes
				getPropRecovery()
			}

			newPhone = false
			enabledAll = false
		}
		else -> {
			atForm.buttonResetPort.isVisible = false
			atForm.shutdownButton.isEnabled = false
			atForm.rebootButton.isEnabled = false
			atForm.rebootToRecoveryButton.isEnabled = false
			atForm.rebootToFastbootButton.isEnabled = false
			atForm.connectButton.isEnabled = false
			enabledAll = true
			newPhone = true
			noConnection()
		}
	}
}
