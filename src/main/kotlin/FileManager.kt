import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import net.lingala.zip4j.ZipFile

fun unZipFile(urlStr: String) {
	ZipFile(urlStr).extractAll(ProgramDir)
	File(ProgramDir + if (Windows) "\\Windows.zip" else if (Linux) "/Linux.zip" else "/MacOS.zip").delete()
}

fun downloadFile(urlStr: String, file: String) {
	val url = URL(urlStr)
	val bis = BufferedInputStream(url.openStream())
	val fis = FileOutputStream(file)
	val buffer = ByteArray(1024)
	var count: Int
	while (bis.read(buffer, 0, 1024).also { count = it } != -1) {
		fis.write(buffer, 0, count)
	}
	fis.close()
	bis.close()
}

fun createFolder() {
	if (ProgramDir != null) {
		File(userFolder, ".android_tool").mkdirs()
		if (Windows) Runtime.getRuntime().exec("attrib +h $userFolder\\.android_tool")
	}
}
