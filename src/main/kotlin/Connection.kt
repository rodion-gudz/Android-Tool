import java.awt.Component
import AndroidTool.Companion.a

private fun noConnection() {
	val components: Array<Component> =
		a.fastbootPanel.components + a.adbPanel.components + a.logsPanel.components + a.consolePanel.components + a.recoveryPanel.components
	for (component in components)
		if (component != a.openSystemTerminalButton)
			component.isEnabled = false
	a.textArea1.isFocusable = false
	a.textArea2.isFocusable = false
	a.list2.isFocusable = false
	a.list1.isFocusable = false
	listModel.removeAllElements()
	listModelLogs.removeAllElements()
//		if (a.table1.model.rowCount != 0)
//			for (i in a.table1.model.rowCount - 1 downTo 0)
//				a.table1.model.removeRow(i)
	a.textField1.text = ""
	a.textField1.isEnabled = false
	a.connectButton.isEnabled = true
	a.textField2.isEnabled = true
	a.notConnectedLabel1.text = "Not connected"
	a.notConnectedLabel1.icon = null
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
			a.shutdownButton.isEnabled = true
			a.rebootButton.isEnabled = true
			a.rebootToRecoveryButton.isEnabled = true
			a.rebootToFastbootButton.isEnabled = true
			if (enabledAll) {
				val disableComponents: Array<Component> = a.fastbootPanel.components + a.recoveryPanel.components
				for (component in disableComponents)
					if (component != a.openSystemTerminalButton)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					a.adbPanel.components + a.consolePanel.components + a.logsPanel.components
				for (component in enableComponents)
					if (component != a.stopButton && component != a.saveButton1 && component != a.installButton && component != a.installButton1)
						component.isEnabled = true
			}
			a.textArea1.isFocusable = true
			a.textArea2.isFocusable = true
			a.list2.isFocusable = true
			a.list1.isFocusable = true

			if (ConnectedAdbUsb) {
				a.notConnectedLabel1.text = "Connected via Adb"
				a.notConnectedLabel1.icon = iconYes
				a.connectButton.isEnabled = false
				a.textField2.isEnabled = false
				a.buttonResetPort.isVisible = true
			} else {
				if (ConnectedAdbWifi) {
					a.notConnectedLabel1.text = "Connected to ${
						AdbDevicesOutput.substring(AdbDevicesOutput.indexOf("192.168")).substringBefore(':')
					}"
					a.notConnectedLabel1.icon = iconYes
				}
			}
			if (newPhone) {
				a.tabbedPane1.selectedIndex = 0
				getProp()
				getListOfPackages()
			}
			newPhone = false
			enabledAll = false
		}
		ConnectedViaFastboot -> {
			a.shutdownButton.isEnabled = false
			a.rebootButton.isEnabled = true
			a.rebootToRecoveryButton.isEnabled = true
			a.rebootToFastbootButton.isEnabled = true
			a.connectButton.isEnabled = false
			a.buttonResetPort.isVisible = false
			if (enabledAll) {
				val disableComponents: Array<Component> =
					a.adbPanel.components + a.logsPanel.components + a.recoveryPanel.components
				for (component in disableComponents)
					if (component != a.openSystemTerminalButton)
						component.isEnabled = false
				val enableComponents: Array<Component> = a.fastbootPanel.components + a.consolePanel.components
				for (component in enableComponents)
					if (component != a.stopButton && component != a.saveButton1)
						component.isEnabled = true
			}
			a.textArea1.isFocusable = false
			a.textArea2.isFocusable = false
			a.list2.isFocusable = false
			a.list1.isFocusable = false

			if (newPhone) {
				a.tabbedPane1.selectedIndex = 3
				a.notConnectedLabel1.text = "Connected via Fastboot"
				a.notConnectedLabel1.icon = iconYes
				getPropFastboot()
			}
			newPhone = false
			enabledAll = false
		}
		ConnectedViaRecovery -> {
			a.shutdownButton.isEnabled = true
			a.rebootButton.isEnabled = true
			a.rebootToRecoveryButton.isEnabled = true
			a.rebootToFastbootButton.isEnabled = true
			a.connectButton.isEnabled = false
			a.buttonResetPort.isVisible = false
			if (enabledAll) {
				val disableComponents: Array<Component> = a.adbPanel.components + a.fastbootPanel.components
				for (component in disableComponents)
					if (component != a.openSystemTerminalButton)
						component.isEnabled = false
				val enableComponents: Array<Component> =
					a.recoveryPanel.components + a.consolePanel.components + a.logsPanel.components
				for (component in enableComponents)
					if (component != a.stopButton && component != a.saveButton1)
						component.isEnabled = true
			}
			a.textArea2.isFocusable = false
			a.textArea1.isFocusable = false
			a.list1.isFocusable = false
			a.list2.isFocusable = false

			if (newPhone) {
				a.tabbedPane1.selectedIndex = 4
				a.notConnectedLabel1.text = "Connected via Adb"
				a.notConnectedLabel1.icon = iconYes
				getPropRecovery()
			}

			newPhone = false
			enabledAll = false
		}
		else -> {
			a.buttonResetPort.isVisible = false
			a.shutdownButton.isEnabled = false
			a.rebootButton.isEnabled = false
			a.rebootToRecoveryButton.isEnabled = false
			a.rebootToFastbootButton.isEnabled = false
			a.connectButton.isEnabled = false
			enabledAll = true
			newPhone = true
			noConnection()
		}
	}
}
