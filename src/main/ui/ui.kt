import AndroidTool.Companion.at_form
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.*
import java.net.InetAddress
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun createUI() {
	at_form.logs_list.model = logs_list_model
	at_form.device_properties_table.model = device_properties_model
	at_form.device_properties_table.showHorizontalLines = true
	at_form.device_properties_table.showVerticalLines = true
	at_form.device_properties_table.setDefaultEditor(Any::class.java, null)
	device_properties_model.addColumn("Property")
	device_properties_model.addColumn("Value")
	at_form.apps_filter_textfield.addKeyListener(object : KeyAdapter() {
		override fun keyReleased(evt: KeyEvent) {
			searchFilter(at_form.apps_filter_textfield.text)
		}
	})
	at_form.save_logs_button.addActionListener {
		at_form.save_logs_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			choseFile.dialogTitle = "Save logs file"
			choseFile.selectedFile = File("ATLog")
			choseFile.addChoosableFileFilter(FileNameExtensionFilter("Logs File (.log)", "log"))
			choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
			choseFile.fileFilter = choseFile.choosableFileFilters[1]
			val chooseDialog = choseFile.showSaveDialog(AndroidTool.frame)
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				val file =
					File(choseFile.selectedFile.canonicalPath.toString() + "." + (choseFile.fileFilter as FileNameExtensionFilter).extensions[0])
				if (!file.exists()) {
					file.createNewFile()
				}
				val fw = FileWriter(file.absoluteFile)
				val bw = BufferedWriter(fw)
				for (element in 0 until logs_list_model.size()) {
					bw.write(logs_list_model[element].toString())
					bw.write("\n")
				}
				bw.close()
			}
			at_form.save_logs_button.isEnabled = true
		}
	}
	at_form.all_apps_radiobutton.addActionListener {
		getListOfPackages()
	}
	at_form.disable_apps_radiobutton.addActionListener {
		getListOfPackages()
	}
	at_form.enabled_apps_radiobutton.addActionListener {
		getListOfPackages()
	}
	at_form.system_apps_radiobutton.addActionListener {
		getListOfPackages()
	}
	at_form.user_apps_radiobutton.addActionListener {
		getListOfPackages()
	}
	at_form.connect_to_device_button.addActionListener {
		at_form.connect_to_device_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "kill-server")
			val output = exec("adb", "connect ${at_form.system_ip_address_field.text}", output = true)
			if ("connected to" in output || "failed to authenticate to" in output) {
				at_form.connection_label.text = "Connected to ${at_form.system_ip_address_field.text}"
				setSettings("lastIP", at_form.system_ip_address_field.text)
			}
			at_form.connect_to_device_button.isEnabled = true
		}
	}
	at_form.start_logs_button.addActionListener {
		at_form.stop_logs_button.isEnabled = true
		at_form.save_logs_button.isEnabled = false
		if (ifStopSelected) {
			functionButtonStart = true
		}
		if (functionButtonStart) {
			at_form.save_logs_button.isEnabled = false
			logsWorking = true
			at_form.start_logs_button.text = "Pause"
			if (ifStopSelected) {
				logs_list_model.removeAllElements()
			}
			GlobalScope.launch(Dispatchers.Swing) {
				apps_list.clear()
				withContext(Dispatchers.Default) {
					Runtime.getRuntime().exec("${SDK_folder}adb logcat -c").waitFor()
					val builderList = when {
						at_form.verbose_radio_button.isSelected -> Runtime.getRuntime()
							.exec("${SDK_folder}adb logcat *:V")
						at_form.debug_radio_button.isSelected -> Runtime.getRuntime()
							.exec("${SDK_folder}adb logcat *:D")
						at_form.info_radio_button.isSelected -> Runtime.getRuntime().exec("${SDK_folder}adb logcat *:I")
						at_form.warning_radio_button.isSelected -> Runtime.getRuntime()
							.exec("${SDK_folder}adb logcat *:W")
						at_form.error_radio_button.isSelected -> Runtime.getRuntime()
							.exec("${SDK_folder}adb logcat *:E")
						at_form.fatal_radio_button.isSelected -> Runtime.getRuntime()
							.exec("${SDK_folder}adb logcat *:F")
						at_form.silent_radio_button.isSelected -> Runtime.getRuntime()
							.exec("${SDK_folder}adb logcat *:S")
						else -> Runtime.getRuntime().exec("${SDK_folder}adb logcat -c")
					}

					val input = builderList.inputStream
					val reader = BufferedReader(InputStreamReader(input))
					var line: String?
					while (reader.readLine().also { line = it } != null) {
						if (line != "* daemon not running; starting now at tcp:5037" && line != "* daemon started successfully" && line != "--------- beginning of main" && line != "--------- beginning of system") {
							if (logsWorking) {
								logs_list_model.addElement(line)
								at_form.logs_list.ensureIndexIsVisible(at_form.logs_list.model.size - 1)
							}
						}
					}
				}
			}
			functionButtonStart = false
			ifStopSelected = false
		} else {
			if (ifStopSelected) {
				logs_list_model.removeAllElements()
			} else {
				logsWorking = false
				functionButtonStart = true
				at_form.start_logs_button.text = "Continue"
			}
		}
	}
	at_form.stop_logs_button.addActionListener {
		at_form.stop_logs_button.isEnabled = false
		at_form.start_logs_button.text = "Start"
		logsWorking = false
		ifStopSelected = true
		at_form.save_logs_button.isEnabled = true
		functionButtonStart = true
	}
	at_form.reboot_to_system_button.addActionListener {
		at_form.reboot_to_system_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				connected_via_adb -> exec("adb", "reboot")
				connected_via_fastboot -> exec("fastboot", "reboot")
				connected_via_recovery -> exec("adb", "shell twrp reboot")
			}
			at_form.reboot_to_system_button.isEnabled = true
		}
	}
	at_form.change_wireless_port_button.addActionListener {
		at_form.change_wireless_port_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "tcpip 5555")
			at_form.change_wireless_port_button.isEnabled = true
		}
	}
	at_form.reboot_to_recovery_device_button.addActionListener {
		at_form.reboot_to_recovery_device_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				connected_via_adb -> exec("adb", "reboot recovery")
				connected_via_fastboot -> exec("fastboot", "oem reboot-recovery")
				connected_via_recovery -> exec("adb", "shell twrp reboot recovery")
			}
			at_form.reboot_to_recovery_device_button.isEnabled = true
		}
	}
	at_form.save_recovery_logs_button.addActionListener {
		at_form.save_recovery_logs_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "shell cp -f /tmp/recovery.log /sdcard")
			at_form.save_recovery_logs_button.isEnabled = true
		}
	}
	at_form.open_system_terminal_button.addActionListener {
		when {
			windows -> Runtime.getRuntime().exec("cmd /c start cd $SDK_folder")
			macos -> Runtime.getRuntime().exec("open -a Terminal $SDK_folder")
			else -> Runtime.getRuntime().exec("gnome-terminal --working-directory=$SDK_folder")
		}
	}
	at_form.reboot_to_fastboot_device_button.addActionListener {
		at_form.reboot_to_fastboot_device_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				connected_via_adb -> exec("adb", "reboot bootloader")
				connected_via_fastboot -> exec("fastboot", "reboot-bootloader")
				connected_via_recovery -> exec("adb", "shell twrp reboot bootloader")
			}
			at_form.reboot_to_fastboot_device_button.isEnabled = true
		}
	}
	at_form.shutdown_device_button.addActionListener {
		at_form.shutdown_device_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			when {
				connected_via_adb -> exec("adb", "reboot -p")
				connected_via_recovery -> exec("adb", "shell twrp reboot poweroff")
			}
			at_form.shutdown_device_button.isEnabled = true
		}
	}
	at_form.install_multiple_apps_button.addActionListener {
		at_form.install_multiple_apps_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val paths: Array<File>?
			val file = File(selected_directory_path)
			val fileNameFilter = FilenameFilter { _, name ->
				if (name.lastIndexOf('.') > 0) {
					val lastIndex = name.lastIndexOf('.')
					val str = name.substring(lastIndex)
					if (str == ".apk") {
						return@FilenameFilter true
					}
				}
				false
			}
			paths = file.listFiles(fileNameFilter)
			for (path in paths) {
				exec("adb", "install \"$path\"")
			}
			at_form.install_multiple_apps_button.isEnabled = true
			at_form.selected_folder_label.text = "Selected: -"
		}
		getListOfPackages()
	}
	at_form.install_one_app_button.addActionListener {
		at_form.install_one_app_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			Runtime.getRuntime().exec("adb install \"$selected_file_path\"")
			at_form.install_one_app_button.isEnabled = true
			at_form.selected_file_label.text = "Selected: -"
		}
		getListOfPackages()
	}
	at_form.disable_app_button.addActionListener {
		app("Disable", at_form.disable_app_button, at_form.apps_list)
		getListOfPackages()
	}
	at_form.uninstall_app_button.addActionListener {
		app("Uninstall", at_form.uninstall_app_button, at_form.apps_list)
		getListOfPackages()
	}
	at_form.enable_app_button.addActionListener {
		app("Enable", at_form.enable_app_button, at_form.apps_list)
		getListOfPackages()
	}
	at_form.clear_app_button.addActionListener {
		app("Clear", at_form.clear_app_button, at_form.apps_list)
	}
	at_form.open_app_button.addActionListener {
		app("Open", at_form.open_app_button, at_form.apps_list)
	}
	at_form.force_stop_app_button.addActionListener {
		app("Stop", at_form.force_stop_app_button, at_form.apps_list)
	}
	at_form.refresh_app_list_button.addActionListener {
		getListOfPackages(true)
	}
	at_form.save_app_list_button.addActionListener {
		at_form.save_app_list_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			choseFile.dialogTitle = "Save app list"
			choseFile.selectedFile = File("ATAppList")
			choseFile.addChoosableFileFilter(FileNameExtensionFilter("Text File (.txt)", "txt"))
			choseFile.fileFilter = choseFile.choosableFileFilters[1]
			val chooseDialog = choseFile.showSaveDialog(AndroidTool.frame)
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				val file =
					File(choseFile.selectedFile.canonicalPath.toString() + "." + (choseFile.fileFilter as FileNameExtensionFilter).extensions[0])
				if (!file.exists()) {
					file.createNewFile()
				}
				val fw = FileWriter(file.absoluteFile)
				val bw = BufferedWriter(fw)
				for (element in 0 until apps_list_model.size()) {
					bw.write(apps_list_model[element].toString())
					bw.write("\n")
				}
				bw.close()
			}
			at_form.save_app_list_button.isEnabled = true
		}
	}
	at_form.select_APK_file_Button.addActionListener {
		at_form.select_recovery_ZIP_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("APK Files", "apk")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Choose APK")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selected_file_path = choseFile.selectedFile.absolutePath
				at_form.selected_file_label.text = "Selected: ${choseFile.selectedFile.name}"
				at_form.install_one_app_button.isEnabled = true
			}
			at_form.select_recovery_ZIP_button.isEnabled = true
		}
	}
	at_form.select_apps_folder_button.addActionListener {
		at_form.select_apps_folder_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseDirectory = JFileChooser()
			choseDirectory.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
			val chooseDialog = choseDirectory.showDialog(null, "Choose folder")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selected_directory_path = choseDirectory.selectedFile.path
				at_form.selected_folder_label.text = "Selected: ${choseDirectory.selectedFile.path}"
				at_form.install_multiple_apps_button.isEnabled = true
			}
			at_form.select_apps_folder_button.isEnabled = true
		}
	}
	at_form.run_console_command_button.addActionListener {
		at_form.run_console_command_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val command = at_form.command_input.text
			when {
				"adb" in command -> at_form.command_output.text =
					exec("adb", at_form.command_input.text.substring(4), output = true)
				"fastboot" in command -> at_form.command_output.text =
					exec("fastboot", at_form.command_input.text.substring(9), output = true)
				else -> at_form.command_output.text = exec("adb", at_form.command_input.text, output = true)
			}
			at_form.run_console_command_button.isEnabled = true
		}
	}
	at_form.wipe_partition_button.addActionListener {
		at_form.wipe_partition_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			if (at_form.boot_checkbox.isSelected)
				exec("fastboot", "erase boot")
			if (at_form.system_checkbox.isSelected)
				exec("fastboot", "erase system")
			if (at_form.data_checkbox.isSelected)
				exec("fastboot", "erase userdata")
			if (at_form.cache_checkbox.isSelected)
				exec("fastboot", "erase cache")
			if (at_form.recovery_checkbox.isSelected)
				exec("fastboot", "erase recovery")
			if (at_form.radio_checkbox.isSelected)
				exec("fastboot", "erase radio")
			at_form.wipe_partition_button.isEnabled = true
		}
	}
	at_form.install_partition_IMG_button.addActionListener {
		at_form.install_partition_IMG_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			if (at_form.boot_radio_button.isSelected)
				exec("fastboot", "flash boot \"$selected_IMG_path\"")
			if (at_form.system_radio_button.isSelected)
				exec("fastboot", "flash system \"$selected_IMG_path\"")
			if (at_form.data_radio_button.isSelected)
				exec("fastboot", "flash userdata \"$selected_IMG_path\"")
			if (at_form.cache_radio_button.isSelected)
				exec("fastboot", "flash cache \"$selected_IMG_path\"")
			if (at_form.recovery_radio_button.isSelected)
				exec("fastboot", "flash recovery \"$selected_IMG_path\"")
			if (at_form.radio_radio_button.isSelected)
				exec("fastboot", "flash radio \"$selected_IMG_path\"")
			at_form.install_partition_IMG_button.isEnabled = true
		}
	}
	at_form.select_recovery_IMG_button.addActionListener {
		at_form.select_recovery_IMG_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("Recovery Files", "img")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select Recovery img")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selected_file_path = choseFile.selectedFile.absolutePath
			}
			at_form.select_recovery_IMG_button.isEnabled = true
		}
	}
	at_form.select_partition_IMG_button.addActionListener {
		at_form.select_partition_IMG_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("IMG Files", "img")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select partition img")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selected_IMG_path = choseFile.selectedFile.absolutePath
			}
			at_form.select_partition_IMG_button.isEnabled = true
		}
	}
	at_form.install_recovery_IMG_button.addActionListener {
		at_form.install_recovery_IMG_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("fastboot", "flash recovery \"$selected_file_path\"")
			at_form.install_recovery_IMG_button.isEnabled = true
		}
	}
	at_form.boot_recovery_IMG_button.addActionListener {
		at_form.boot_recovery_IMG_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("fastboot", "boot \"$selected_file_path\"")
			at_form.boot_recovery_IMG_button.isEnabled = true
		}
	}
	at_form.select_recovery_ZIP_button.addActionListener {
		at_form.select_recovery_ZIP_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			val choseFile = JFileChooser()
			val filter = FileNameExtensionFilter("Zip files", "zip")
			choseFile.fileFilter = filter
			val chooseDialog = choseFile.showDialog(null, "Select Zip")
			if (chooseDialog == JFileChooser.APPROVE_OPTION) {
				selected_ZIP_path = choseFile.selectedFile.absolutePath
				selected_ZIP_name = choseFile.selectedFile.name
			}
			at_form.select_recovery_ZIP_button.isEnabled = true
		}
	}
	at_form.install_recovery_zip_button.addActionListener {
		at_form.install_recovery_zip_button.isEnabled = false
		GlobalScope.launch(Dispatchers.Swing) {
			exec("adb", "push \"$selected_ZIP_path\" /sdcard/")
			exec("adb", "shell twrp install \"$selected_ZIP_name\"")
			exec("adb", "shell rm \"$selected_ZIP_name\"")
			at_form.install_recovery_zip_button.isEnabled = true
		}
	}
	createMenuBar()
	desableCompoments()
	if (getSettings("lastIP") == "") {
		system_IP_address = when {
			windows -> InetAddress.getLocalHost().hostAddress
			macos -> Runtime.getRuntime().exec("ipconfig getifaddr en0").inputStream.bufferedReader()
				.readText() + Runtime.getRuntime().exec("ipconfig getifaddr en1").inputStream.bufferedReader()
				.readText()
			linux -> Runtime.getRuntime().exec("ip n").inputStream.bufferedReader().readLine().substringBefore(" ")
			else -> ""
		}
		system_IP_address = system_IP_address.substringBeforeLast('.') + "."
	} else
		system_IP_address = getSettings("lastIP")
	at_form.system_ip_address_field.text = system_IP_address
}