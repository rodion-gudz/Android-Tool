import net.lingala.zip4j.ZipFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

fun unZipFile(urlStr: String) {
	ZipFile(urlStr).extractAll(program_folder)
	File(program_folder + if (windows) "\\Windows.zip" else if (linux) "/Linux.zip" else "/MacOS.zip").delete()
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
	if (program_folder != null) {
		File(user_folder, ".android_tool").mkdirs()
		if (windows) Runtime.getRuntime().exec("attrib +h $user_folder\\.android_tool")
	}
}
