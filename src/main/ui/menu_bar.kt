import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

fun createMenuBar() {
	AndroidTool.frame.jMenuBar = menu_bar
	AndroidTool.frame.addWindowListener(object : WindowAdapter() {
		override fun windowClosing(e: WindowEvent) {
			exec("adb", "kill-server")
		}
	})
	menu_bar_main.add(menu_bar_settings)
	menu_bar_main.add(menu_bar_about)
	menu_bar_main.addSeparator()
	menu_bar_main.add(menu_bar_exit)
	menu_bar.add(menu_bar_main)
	menu_bar_settings.addActionListener {
		settings_dialog.main()
	}
	menu_bar_about.addActionListener {
		about_dialog.main()
	}
	menu_bar_exit.addActionListener {
		exec("adb", "kill-server")
		exitProcess(0)
	}
}