import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

fun getSettings(): String? {
	val prop = Properties()
	return try {
		prop.load(FileInputStream(program_folder + "config.properties"))
		val theme = prop.getProperty("theme")
		theme
	} catch (e: Exception) {
		if (program_folder != null) {
			val settingsFile: File = File(program_folder + "config.properties")
			settingsFile.createNewFile()
			settingsFile.writeText("theme=dark")
		}
		"dark"
	}
}

fun getSettings(key: String): String {
	val prop = Properties()
	prop.load(FileInputStream(program_folder + "config.properties"))
	return try {
		val IP = prop.getProperty(key)
		IP
	} catch (e: NullPointerException) {
		""
	}
}

fun setSettings(key: String, value: String) {
	val prop = Properties()
	prop.load(FileInputStream(program_folder + "config.properties"))
	prop.setProperty(key, value)
	FileOutputStream(program_folder + "config.properties").use { output -> prop.store(output, null) }
}