import java.io.File


fun createFolder() {
	if (program_folder != null) {
		File(user_folder, ".android_tool").mkdirs()
		if (windows) Runtime.getRuntime().exec("attrib +h $user_folder\\.android_tool")
	}
}
