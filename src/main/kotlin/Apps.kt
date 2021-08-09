import AndroidTool.Companion.atForm
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import javax.swing.DefaultListModel

@OptIn(DelicateCoroutinesApi::class)
fun getListOfPackages(button: Boolean = false) {
	if (button)
		atForm.refreshButton.isEnabled = false
	GlobalScope.launch(Dispatchers.Swing) {
		val items: DefaultListModel<Any?> = DefaultListModel()
		atForm.textField1.isFocusable = true
		arrayList.clear()
		apps.clear()
		val reader = when {
			atForm.disabledRadioButton.isSelected -> execLines("adb shell pm list packages -d")
			atForm.systemRadioButton.isSelected -> execLines("adb shell pm list packages -s")
			atForm.enabledRadioButton.isSelected -> execLines("adb shell pm list packages -e")
			atForm.thirdRadioButton.isSelected -> execLines("adb shell pm list packages -3")
			else -> execLines("adb shell pm list packages")
		}
		for (element in reader) {
			if ("no devices/emulators found" !in element && "device unauthorized." !in element && "kill-server" !in element && "server's" !in element && "a confirmation dialog" !in element && "not access" !in element) {
				if (element != "* daemon not running starting now at tcp:5037" && element != "* daemon started successfully") {
					arrayList.add(
						if (appProp.getProperty(element.substring(8)) != null)
							"${element.substring(8)} (${appProp.getProperty(element.substring(8), "")})"
						else
							element.substring(8)
					)
				}
			}
		}
		arrayList.sort()
		if (button)
			atForm.refreshButton.isEnabled = true
		listModel.removeAllElements()
		for (element in arrayList) {
			if (atForm.textField1.text == "")
				items.addElement(element)
			apps.add(element)
		}
		listModel = items
		atForm.list1.model = listModel
		atForm.refreshButton.isEnabled = true
		searchFilter(atForm.textField1.text)
	}
}