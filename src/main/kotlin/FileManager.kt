import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipFile

fun unZipFile(urlStr: String) {
	ZipFile(File(urlStr)).use { zip ->
		zip.stream().forEach { entry ->
			if (entry.isDirectory)
				File(SdkDir, entry.name).mkdirs()
			else zip.getInputStream(entry).use { input ->
				File(SdkDir, entry.name).apply {
					outputStream().use { output ->
						input.copyTo(output)
					}
					setExecutable(true, false)
				}
			}
		}
	}
	File(ProgramDir + if (Windows) "\\SDK-Tools\\Windows.zip" else if (Linux) "/SDK-Tools/Linux.zip" else "/SDK-Tools/MacOS.zip").delete()
}

fun downloadFile(urlStr: String, file: String) {
	val url = URL(urlStr)
	val bis = BufferedInputStream(url.openStream())
	val fis = FileOutputStream(file)
	val buffer = ByteArray(1024)
	var count = 0
	while (bis.read(buffer, 0, 1024).also { count = it } != -1) {
		fis.write(buffer, 0, count)
	}
	fis.close()
	bis.close()
}

fun createFolder() {
	if (ProgramDir != null) {
		when {
			Windows -> File("$userFolder\\.android_tool", "SDK-Tools").mkdirs()
			else -> File("$userFolder/.android_tool", "SDK-Tools").mkdirs()
		}
		if (Windows) Runtime.getRuntime().exec("attrib +h $userFolder\\.android_tool")
	}
}
