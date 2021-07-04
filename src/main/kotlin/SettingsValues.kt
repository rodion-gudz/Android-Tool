import java.io.File
import java.io.FileInputStream
import java.util.*

class SettingsValues{
	fun getSettings(): String? {
		val prop = Properties()
		return try {
			prop.load(FileInputStream("${ProgramDir}settings.properties"))
			val theme = prop.getProperty("theme")
			theme
		}catch (e: Exception){
			if (ProgramDir != null) {
				val settingsFile: File = File("${ProgramDir}settings.properties")
				settingsFile.createNewFile()
				settingsFile.writeText("theme=dark")
			}
			"dark"
		}
	}
}
