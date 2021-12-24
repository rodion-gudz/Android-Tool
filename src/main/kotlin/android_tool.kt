import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun main(args: Array<String>) {
	createUI()
	AndroidTool.main()
	app_names_list.load(AndroidTool::class.java.getResourceAsStream("applist.properties"))
	values_properties.load(AndroidTool::class.java.getResourceAsStream("values.properties"))
	program_version = values_properties.getProperty("version")
	GlobalScope.launch {
		while (true) {
			connectionCheck()
			delay(1000)
		}
	}
}