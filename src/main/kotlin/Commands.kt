import java.io.IOException

fun exec(app: String, command: String, output: Boolean = false, streamType: String = "Input"): String {
	try {
		val process = Runtime.getRuntime().exec("$SdkDir$app $command")
		if (output) {
			return if (streamType == "Input")
				process.inputStream.bufferedReader().readText()
			else
				process.errorStream.bufferedReader().readText()
		}
		process.waitFor()
	} catch (e: IOException) {
		e.printStackTrace()
	}
	return ""
}

fun execLines(command: String): List<String> {
	val process = Runtime.getRuntime().exec("$SdkDir$command")
	return process.inputStream.bufferedReader().readLines()
}
