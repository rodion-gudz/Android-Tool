import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun main(args: Array<String>){
	createUI()
	AndroidTool.main()
	appProp.load(AndroidTool::class.java.getResourceAsStream("applist.properties"))
	sdkCheck()
	GlobalScope.launch {
		while (true) {
			connectionCheck()
			delay(1000)
		}
	}
	versionCheck()
}