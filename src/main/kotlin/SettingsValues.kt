import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

fun getSettings(): String? {
	val prop = Properties()
	return try {
		prop.load(FileInputStream(ProgramDir + "config.properties"))
		val theme = prop.getProperty("theme")
		theme
	} catch (e: Exception) {
		if (ProgramDir != null) {
			val settingsFile: File = File(ProgramDir + "config.properties")
			settingsFile.createNewFile()
			settingsFile.writeText("theme=dark")
		}
		"dark"
	}
}

fun getSettings(key: String): String {
	val prop = Properties()
	prop.load(FileInputStream(ProgramDir + "config.properties"))
	return try {
		val IP = prop.getProperty(key)
		IP
	} catch (e: NullPointerException) {
		""
	}
}

fun setSettings(key: String, value: String) {
	val prop = Properties()
	prop.load(FileInputStream(ProgramDir + "config.properties"))
	prop.setProperty(key, value)
	FileOutputStream(ProgramDir + "config.properties").use { output -> prop.store(output, null) }
}