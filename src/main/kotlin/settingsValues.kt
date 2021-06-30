import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class settingsValues{
	fun cum(): String? {
		val prop = Properties()
		return try {
			prop.load(FileInputStream("${ProgramDir}settings.properties"))
			val theme = prop.getProperty("theme")
			theme
		}catch (e: FileNotFoundException){
			if (ProgramDir != null) {
				val settingsFile = File("${ProgramDir}settings.properties")
				settingsFile.createNewFile()
				settingsFile.writeText("theme=dark")
			}
			"dark"
		}
	}
}
