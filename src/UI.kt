import com.formdev.flatlaf.FlatDarculaLaf
import java.awt.Color
import java.awt.Component
import java.awt.Desktop
import java.awt.Rectangle
import java.awt.event.*
import java.io.IOException
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URI
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.UIManager

import java.awt.Insets





open class AndroidToolUI {
    fun runUrl(url: String) {
        val urlString = URI(url)
        Desktop.getDesktop().browse(urlString)
    }

    init {
        FlatDarculaLaf.install()
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
        UIManager.put("ScrollBar.thumbArc", 999)
        UIManager.put("ScrollBar.thumbInsets", Insets(2, 2, 2, 2))
    }

    val frame = JFrame("Android Tool")
    private var sdf = ""
    var labelManufacturerValue = JLabel("-")
    var labelBrandValue = JLabel("-")
    var labelModelValue = JLabel("-")
    var labelCodenameValue = JLabel("-")
    var labelCPUValue = JLabel("-")
    var labelCPUAValue = JLabel("-")
    var labelSNValue = JLabel("-")
    var labelGsmOperatorValue = JLabel("-")
    var labelManufacturer = JLabel("Manufacturer:")
    var labelBrand = JLabel("Brand:")
    var labelModel = JLabel("Model:")
    var labelCodename = JLabel("Codename:")
    var labelCPU = JLabel("CPU:")
    var labelCPUA = JLabel("CPU architecture:")
    var labelSN = JLabel("Serial number:")
    var labelGsmOperator = JLabel("Cellular provider:")
    val title5 = BorderFactory.createTitledBorder("Hardware")
    val boardInfoPanel = JPanel()
    val title1 = BorderFactory.createTitledBorder("Device Info")
    val deviceInfoPanel = JPanel()
    val softInfoPanel = JPanel()
    val title6 = BorderFactory.createTitledBorder("Software")
    var labelFingerprint = JLabel("Fingerprint:")
    var labelFingerprintValue = JLabel("-")
    var labelVersionRelease = JLabel("Android version:")
    var labelVersionReleaseValue = JLabel("-")
    var labelSDK = JLabel("SDK version:")
    var labelSDKValue = JLabel("-")
    var labelSecurityPatch = JLabel("Security Patch:")
    var labelSecurityPatchValue = JLabel("-")
    var labelLanguage = JLabel("Language:")
    var labelLanguageValue = JLabel("-")
    var labelSelinux = JLabel("Selinux:")
    var labelSelinuxValue = JLabel("-")
    var labelTreble = JLabel("Project treble support:")
    var labelTrebleValue = JLabel("-")
    var labelSecureBoot = JLabel("Secure boot:")
    var labelSecureBootValue = JLabel("-")
    var labelDeviceHostname = JLabel("Build hostname:")
    var labelDeviceHostnameValue = JLabel("-")
    var labelLocations = JLabel("Mock location allowed:")
    var labelLocationsValue = JLabel("-")
    var labelDataFSValue = JLabel("-")
    var labelDataCapacity = JLabel("/data Capacity (MB):")
    var labelDataCapacityValue = JLabel("-")
    var labelBootFS = JLabel("/boot File system:")
    var labelBootFSValue = JLabel("-")
    var labelBootCapacity = JLabel("/boot Capacity (MB):")
    var labelBootCapacityValue = JLabel("-")
    var labelRecoveryFS = JLabel("/recovery File system:")
    var labelRecoveryFSValue = JLabel("-")
    var labelRecoveryCapacity = JLabel("/recovery Capacity (MB):")
    var labelRecoveryCapacityValue = JLabel("-")
    var labelCacheFS = JLabel("/cache File system:")
    var labelCacheFSValue = JLabel("-")
    var labelCacheCapacity = JLabel("/cache Capacity (MB):")
    var labelCacheCapacityValue = JLabel("-")
    var labelVendorFS = JLabel("/vendor File system:")
    var labelVendorFSValue = JLabel("-")
    var labelVendorCapacity = JLabel("/vendor Capacity (MB):")
    var labelVendorCapacityValue = JLabel("-")
    var labelAllCapacity = JLabel("All Capacity (MB):")
    var labelAllCapacityValue = JLabel("-")
    val deviceControlPanel = JPanel()
    val title2 = BorderFactory.createTitledBorder("Device Control Panel")
    val deviceConnection = JPanel()
    var labelDataFS = JLabel("/data File system:")
    var labelSystemCapacityValue = JLabel("-")
    var labelSystemCapacity = JLabel("/system Capacity (MB):")
    var labelSystemFSValue = JLabel("-")
    var labelSystemFS = JLabel("/system File system:")
    val title93 = BorderFactory.createTitledBorder("Storage")
    val title3 = BorderFactory.createTitledBorder("Connection")
    val StorageFastbootInfoPanel = JPanel()
    var labelFastbootSNValue = JLabel("-")
    var labelFastbootSN = JLabel("Serial number:")
    var labelFastbootCodenameValue = JLabel("-")
    var labelFastbootCodename = JLabel("Codename:")
    val title9 = BorderFactory.createTitledBorder("Main")
    val softFastbootInfoPanel = JPanel()
    var labelUnlockValue = JLabel("-")
    var labelUnlock = JLabel("Unlocked:")
    val title12 = BorderFactory.createTitledBorder("Bootloader")
    val BootloaderFastbootInfoPanel = JPanel()
    val fastbootPanel = JPanel()
    val adbPanel = JPanel()
    val labelUSB = JLabel("USB:")
    val labelTCP = JLabel("TCP/IP:")
    val logsPanel = JPanel()
    val tabbedpane = JTabbedPane()
    var textFieldIP = JTextField("")
    var buttonIpConnect = JButton("Connect")
    var labelConnect = JLabel("")
    var labelTCPConnection = JLabel("Not connected")
    var labelUSBConnection = JLabel("Not connected")
    var textFieldIPa = JTextField("")
    val labelICon = JLabel(ImageIcon(this::class.java.getResource("/icon/sa.png")))
    val list = JList(listModel)
    var scrollPaneLogs = JScrollPane()
    var listLogs = JList(listModelLogs)
    val scrollPane = JScrollPane()
    var labelIP = JLabel("IP:")
    val buttonReboot = JButton("Reboot")
    val buttonRecoveryReboot = JButton("Reboot to Recovery")
    val buttonFastbootReboot = JButton("Reboot to Fastboot")
    val buttonPowerOff = JButton("Shutdown")
    val textAreaCommandFastbootInput = JTextArea("")
    val scroll24 = JScrollPane(textAreaCommandFastbootInput)
    val textAreaCommandFastbootOutput = JTextArea("")
    val scroll2 = JScrollPane(textAreaCommandFastbootOutput)
    val textAreaCommandInput = JTextArea("")
    val scroll23 = JScrollPane(textAreaCommandInput)
    val textAreaCommandOutput = JTextArea("")
    val scroll = JScrollPane(textAreaCommandOutput)
    val group = ButtonGroup()
    val radioButtonDebug = JRadioButton("Debug", false)
    val radioButtonInfo = JRadioButton("Info", true)
    val radioButtonWarning = JRadioButton("Warning", false)
    val radioButtonError = JRadioButton("Error", false)
    val radioButtonFatal = JRadioButton("Fatal", false)
    val radioButtonSilent = JRadioButton("Silent", false)
    val radioButtonVerbose = JRadioButton("Verbose", false)
    val buttonSave = JButton("Save")
    val buttonStop = JButton("Stop")
    val dialogUnauthorizedDevice = JDialog(frame, "Unauthorized device found", true)
    val labelUnauthorized = JLabel("<html><font size='4'>Please check the box <strong>\"Always allow from this computer\"</strong><br>and click <strong>\"Allow\"</strong> on your device</font></html>")
    val labelSample = JLabel("Sample:")
    val labelScreenshotUnauthorized = JLabel(ImageIcon(this::class.java.getResource("/icon/una.png")))
    val dialogMultipleDevice = JDialog(frame, "Multiple devices connected", true)
    val dialogSdkDownload = JDialog(frame, "No SDK found!", true)
    val dialogUpdate = JDialog(frame, "Version $programVersionLatest available!", true)
    val labelMultipleDevice = JLabel("<html><font size='4'>Please <strong>disconnect one of the devices</strong></font></html>")
    val labelSdkDownload = JLabel("<html><font size='4'>Android, please click button to install</font></html>")
    val labelUpdate = JLabel("<html><font size='4'>Android-Tool update available, please click button to update</font></html>")
    val labelUpdateVersion = JLabel("<html><font size='4'><b>Current version:</b> $programVersion <br> <b>Latest:</b> $programVersionLatest</font></html>")
    val textAreaInput = JTextField("You can enter app package here")
    val labelInstallAll = JLabel("Install all APK in the folder")
    val labelInstallOne = JLabel("Install one APK")
    val labelSelectedOne = JLabel("Selected: -")
    val labelSelectedAll = JLabel("Selected: -")
    val labelOutputAdbCommand = JLabel("Output:")
    val labelEnterAdbCommand = JLabel("Enter other command")
    val groupApps = ButtonGroup()
    val radioButtonAll = JRadioButton("All apps", true)
    val radioButtonDisabled = JRadioButton("Disabled apps", false)
    val radioButtonEnabled = JRadioButton("Enabled apps", false)
    val radioButtonThird = JRadioButton("Third apps", false)
    val buttonInstallAll = JButton("Install")
    val radioButtonSystem = JRadioButton("System apps", false)
    val labelcrDroid = JLabel("crDroid")
    val buttonHavocOSDownload = JButton("Download")
    val buttonHavocOS = JButton("Official Site")
    val labelHavocOS = JLabel("Havoc OS")
    val buttonPEDownload = JButton("Download")
    val buttonPE = JButton("Official Site")
    val labelPE = JLabel("Pixel Experience")
    val labelRoms = JLabel("ROMs")
    val linksPanel = JPanel()
    val buttoncrDroid = JButton("Official Site")
    val buttoncrDroidDownload = JButton("Download")
    val labelLineageOS = JLabel("Lineage OS")
    val buttonLineageOS = JButton("Official Site")
    val buttonLineageOSDownload = JButton("Download")
    val labelParanoid = JLabel("Paranoid Android")
    val buttonParanoid = JButton("Official Site")
    val buttonParanoidDownload = JButton("Download")
    val labelDerpFest = JLabel("AOSiP DerpFest")
    val buttonDerpFest = JButton("Official Site")
    val buttonDerpFestDownload = JButton("Download")
    val labelZenX = JLabel("ZenX-OS")
    val buttonZenX = JButton("Official Site")
    val buttonZenXDownload = JButton("Download")
    val labelEvolutionX = JLabel("Evolution X")
    val buttonEvolutionX = JButton("Official Site")
    val buttonEvolutionXDownload = JButton("Download")
    val labelCorvusOS = JLabel("Corvus OS")
    val buttonCorvusOS = JButton("Official Site")
    val buttonCorvusOSDownload = JButton("Download")
    val labelIon = JLabel("Ion OS")
    val buttonIon = JButton("Official Site")
    val buttonIonDownload = JButton("Download")
    val labelMoKee = JLabel("MoKee")
    val buttonMoKee = JButton("Official Site")
    val buttonMoKeeDownload = JButton("Download")
    val labelBlissOS = JLabel("Bliss OS")
    val buttonBlissOS = JButton("Official Site")
    val buttonBlissOSDownload = JButton("Download")
    val labelMSMXtended = JLabel("MSM-Xtended")
    val buttonMSMXtended = JButton("Official Site")
    val buttonMSMXtendedDownload = JButton("Download")
    val labelAOSPExtended = JLabel("AOSP Extended")
    val buttonAOSPExtended = JButton("Official Site")
    val buttonAOSPExtendedDownload = JButton("Download")
    val labelAICPRom = JLabel("AICP")
    val buttonAICPRom = JButton("Official Site")
    val buttonAICPRomDownload = JButton("Download")
    val labelArrowOS = JLabel("Arrow OS")
    val buttonArrowOS = JButton("Official Site")
    val buttonArrowOSDownload = JButton("Download")
    val labelCarbonROM = JLabel("CarbonROM")
    val buttonCarbonROM = JButton("Official Site")
    val buttonCarbonROMDownload = JButton("Download")
    val labelRevengeOS = JLabel("Revenge OS")
    val buttonRevengeOS = JButton("Official Site")
    val buttonRevengeOSDownload = JButton("Download")
    val labelXiaomiEU = JLabel("Xiaomi EU")
    val buttonXiaomiEU = JButton("Official Site")
    val buttonXiaomiEUDownload = JButton("Download")
    val labelMasik = JLabel("Masik")
    val buttonMasik = JButton("Official Site")
    val buttonMasikDownload = JButton("Download")
    val labelMiRoom = JLabel("MiRoom")
    val buttonMiRoom = JButton("Official Site")
    val buttonMiRoomDownload = JButton("Download")
    val labelMiuiMix = JLabel("MiuiMix")
    val buttonMiuiMix = JButton("Official Site")
    val buttonMiuiMixDownload = JButton("Download")
    val labelRevOS = JLabel("RevOS")
    val buttonRevOS = JButton("Official Site")
    val buttonRevOSDownload = JButton("Download")
    val labelMiGlobe = JLabel("Mi-Globe")
    val buttonMiGlobe = JButton("Official Site")
    val buttonMiGlobeDownload = JButton("Download")
    val labelGapps = JLabel("Google Apps")
    val labelOpenGapps = JLabel("Open GApps")
    val buttonOpenGapps = JButton("Official Site")
    val buttonOpenGappsDownload = JButton("Download")
    val labelBiTGApps = JLabel("BiTGApps")
    val buttonBiTGApps = JButton("Official Site")
    val buttonBiTGAppsDownload = JButton("Download")
    val labelFlameGApps = JLabel("FlameGApps")
    val buttonFlameGApps = JButton("Official Site")
    val buttonFlameGAppsDownload = JButton("Download")
    val labelNikGApps = JLabel("NikGApps")
    val buttonNikGApps = JButton("Official Site")
    val buttonNikGAppsDownload = JButton("Download")
    val labelRecovery = JLabel("Recovery")
    val labelTwrp = JLabel("TWRP")
    val buttonTwrp = JButton("Official Site")
    val buttonTwrpDownload = JButton("Download")
    val labelOrangeFox = JLabel("OrangeFox")
    val buttonOrangeFox = JButton("Official Site")
    val buttonOrangeFoxDownload = JButton("Download")
    val labelSkyHawk = JLabel("SkyHawk")
    val buttonSkyHawk = JButton("Official Site")
    val buttonSkyHawkDownload = JButton("Download")
    val labelPitchBlack = JLabel("PitchBlack")
    val buttonPitchBlack = JButton("Official Site")
    val buttonPitchBlackDownload = JButton("Download")
    val labelOther = JLabel("Other")
    val labelMagisk = JLabel("Root")
    val buttonMagisk = JButton("Magisk")
    val buttonSuperSU = JButton("Super SU")
    val labelGcam = JLabel("Google Camera")
    val buttonGcam = JButton("Download")
    val buttonChecker = JButton("2API Checker")
    val labelANX = JLabel("ANX Camera")
    val buttonANX = JButton("Official Site")
    val buttonANXDownload = JButton("Download")
    val buttonStart = JButton("Start")
    val buttonChooseOne = JButton("Select APK")
    val buttonCheck = JButton("Get list of packages")
    val buttonEnable = JButton("Enable")
    val buttonUninstall = JButton("Uninstall")
    val buttonDisable = JButton("Disable")
    val buttonInstallOne = JButton("Install")
    val buttonChoseAll = JButton("Select Folder")
    val buttonRunCommand = JButton("Run")
    val labelErase = JLabel("Erase partition")
    val labelInstallRecovery = JLabel("Install or boot recovery")
    val labelEnterFastbootCommand = JLabel("Enter other command")
    val labelOutputFastbootCommand = JLabel("Output:")
    val checkBoxPartitionBoot = JCheckBox("Boot")
    val checkBoxPartitionRadio = JCheckBox("Radio")
    val checkBoxPartitionRecovery = JCheckBox("Recovery")
    val checkBoxPartitionCache = JCheckBox("Cache")
    val checkBoxPartitionData = JCheckBox("Data")
    val checkBoxPartitionSystem = JCheckBox("System")
    val buttonRunCommandFastboot = JButton("Run")
    val buttonErase = JButton("Erase")
    val buttonChoseRecovery = JButton("Select Recovery")
    val buttonInstallRecovery = JButton("Install")
    val buttonBootToRecovery = JButton("Boot")
    val buttonChooseZip = JButton("Select Zip")
    val buttonSdkDownload = JButton("Install")
    val buttonUpdate = JButton("Download")
    val labelInstallZip = JLabel("Install zip")
    val recoveryPanel = JPanel()
    val buttonInstallZip = JButton("Install")
    val buttonGetLogs = JButton("Save logs")

    //    val menuBar = JMenuBar()
//    val fileMenu = JMenu("Program")
//    val settingsMenu = JMenuItem("Settings")
//    val aboutItem = JMenuItem("About")
//    val exitItem = JMenuItem("Exit")
    init {
//        settingsMenu.addActionListener {
//
//        }
//        fileMenu.add(settingsMenu)
//
//
//        fileMenu.add(aboutItem)
//
//        aboutItem.addActionListener {
//
//        }
//
//        fileMenu.addSeparator()
//
//
//        fileMenu.add(exitItem)
//
//        exitItem.addActionListener {
//            Runtime.getRuntime().exec("adb kill-server")
//            exitProcess(0) }
//
//        menuBar.add(fileMenu)
//
//        frame.jMenuBar = menuBar
        buttonInstallZip.bounds = Rectangle(5, 25, 285, 50)
        buttonInstallZip.isFocusable = false
        recoveryPanel.add(buttonInstallZip)
        buttonGetLogs.bounds = Rectangle(295, 25, 120, 25)
        buttonGetLogs.isFocusable = false
        recoveryPanel.add(buttonGetLogs)
        recoveryPanel.layout = null

        labelInstallZip.bounds = Rectangle(7, 5, 250, 20)
        recoveryPanel.add(labelInstallZip)

        buttonChooseZip.bounds = Rectangle(5, 80, 285, 50)
        buttonChooseZip.isFocusable = false
        recoveryPanel.add(buttonChooseZip)
        buttonBootToRecovery.bounds = Rectangle(152, 80, 138, 50)
        buttonBootToRecovery.isFocusable = false
        fastbootPanel.add(buttonBootToRecovery)
        buttonInstallRecovery.bounds = Rectangle(5, 80, 138, 50)
        buttonInstallRecovery.isFocusable = false
        fastbootPanel.add(buttonInstallRecovery)
        buttonChoseRecovery.bounds = Rectangle(5, 25, 285, 50)
        buttonChoseRecovery.isFocusable = false
        fastbootPanel.add(buttonChoseRecovery)
        buttonErase.bounds = Rectangle(300, 178, 137, 25)
        buttonErase.isFocusable = false
        fastbootPanel.add(buttonErase)
        buttonRunCommandFastboot.bounds = Rectangle(173, 513, 140, 25)
        buttonRunCommandFastboot.isFocusable = false
        fastbootPanel.add(buttonRunCommandFastboot)
        checkBoxPartitionSystem.bounds = Rectangle(300, 30, 100, 20)
        fastbootPanel.add(checkBoxPartitionSystem)


        checkBoxPartitionData.bounds = Rectangle(300, 55, 100, 20)
        fastbootPanel.add(checkBoxPartitionData)


        checkBoxPartitionCache.bounds = Rectangle(300, 80, 100, 20)
        fastbootPanel.add(checkBoxPartitionCache)


        checkBoxPartitionRecovery.bounds = Rectangle(300, 105, 100, 20)
        fastbootPanel.add(checkBoxPartitionRecovery)


        checkBoxPartitionBoot.bounds = Rectangle(300, 130, 100, 20)
        fastbootPanel.add(checkBoxPartitionBoot)


        checkBoxPartitionRadio.bounds = Rectangle(300, 155, 100, 20)
        fastbootPanel.add(checkBoxPartitionRadio)
        labelOutputFastbootCommand.bounds = Rectangle(317, 425, 250, 20)
        fastbootPanel.add(labelOutputFastbootCommand)


        labelEnterFastbootCommand.bounds = Rectangle(7, 425, 250, 20)
        fastbootPanel.add(labelEnterFastbootCommand)


        labelInstallRecovery.bounds = Rectangle(7, 5, 250, 20)
        fastbootPanel.add(labelInstallRecovery)


        labelErase.bounds = Rectangle(300, 5, 250, 20)
        fastbootPanel.add(labelErase)
        buttonRunCommand.bounds = Rectangle(173, 513, 140, 25)
        buttonRunCommand.isFocusable = false
        adbPanel.add(buttonRunCommand)

        buttonChoseAll.bounds = Rectangle(5, 80, 285, 50)
        buttonChoseAll.isFocusable = false
        adbPanel.add(buttonChoseAll)
        buttonInstallOne.bounds = Rectangle(5, 200, 285, 50)
        buttonInstallOne.isFocusable = false
        adbPanel.add(buttonInstallOne)
        buttonDisable.bounds = Rectangle(328, 320, 176, 50)
        buttonDisable.isFocusable = false
        adbPanel.add(buttonDisable)
        buttonUninstall.bounds = Rectangle(513, 320, 176, 50)
        buttonUninstall.isFocusable = false
        adbPanel.add(buttonUninstall)
        buttonEnable.bounds = Rectangle(698, 320, 180, 50)
        buttonEnable.isFocusable = false
        adbPanel.add(buttonEnable)
        buttonCheck.bounds = Rectangle(328, 260, 270, 50)
        buttonCheck.isFocusable = false
        adbPanel.add(buttonCheck)
        buttonChooseOne.bounds = Rectangle(5, 255, 285, 50)
        buttonChooseOne.isFocusable = false
        adbPanel.add(buttonChooseOne)

        buttonStart.bounds = Rectangle(5, 431, 150, 35)
        buttonStart.isFocusable = false
        logsPanel.add(buttonStart)
        linksPanel.layout = null

        ////////LinksPanel////////

        ////ROMs////

        labelRoms.bounds = Rectangle(220, 5, 140, 30)
        labelRoms.font = labelRoms.font.deriveFont(22.0f)
        linksPanel.add(labelRoms)


        labelPE.bounds = Rectangle(10, 45, 110, 20)
        linksPanel.add(labelPE)

        buttonPE.bounds = Rectangle(10, 65, 110, 25)
        buttonPE.isFocusable = false
        linksPanel.add(buttonPE)
        buttonPE.addActionListener { runUrl("https://download.pixelexperience.org/") }

        buttonPEDownload.bounds = Rectangle(10, 90, 110, 25)
        buttonPEDownload.isEnabled = false
        linksPanel.add(buttonPEDownload)

        labelHavocOS.bounds = Rectangle(10, 125, 100, 20)
        linksPanel.add(labelHavocOS)

        buttonHavocOS.bounds = Rectangle(10, 145, 110, 25)
        buttonHavocOS.isEnabled = false
        linksPanel.add(buttonHavocOS)

        buttonHavocOSDownload.bounds = Rectangle(10, 170, 110, 25)
        buttonHavocOSDownload.isFocusable = false
        linksPanel.add(buttonHavocOSDownload)
        buttonHavocOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/havoc-os/files/") }

        labelcrDroid.bounds = Rectangle(10, 205, 100, 20)
        linksPanel.add(labelcrDroid)

        buttoncrDroid.bounds = Rectangle(10, 225, 110, 25)
        buttoncrDroid.isFocusable = false
        linksPanel.add(buttoncrDroid)
        buttoncrDroid.addActionListener { runUrl("https://crdroid.net/") }

        buttoncrDroidDownload.bounds = Rectangle(10, 250, 110, 25)
        buttoncrDroidDownload.isFocusable = false
        linksPanel.add(buttoncrDroidDownload)
        buttoncrDroidDownload.addActionListener { runUrl("https://sourceforge.net/projects/crdroid/files/") }

        labelLineageOS.bounds = Rectangle(10, 285, 100, 20)
        linksPanel.add(labelLineageOS)

        buttonLineageOS.bounds = Rectangle(10, 305, 110, 25)
        buttonLineageOS.isFocusable = false
        linksPanel.add(buttonLineageOS)
        buttonLineageOS.addActionListener { runUrl("https://lineageos.org/") }

        buttonLineageOSDownload.bounds = Rectangle(10, 330, 110, 25)
        buttonLineageOSDownload.isFocusable = false
        linksPanel.add(buttonLineageOSDownload)
        buttonLineageOSDownload.addActionListener { runUrl("https://download.lineageos.org/") }

        labelParanoid.bounds = Rectangle(10, 365, 120, 20)
        linksPanel.add(labelParanoid)

        buttonParanoid.bounds = Rectangle(10, 385, 110, 25)
        buttonParanoid.isFocusable = false
        linksPanel.add(buttonParanoid)
        buttonParanoid.addActionListener { runUrl("https://paranoidandroid.co/") }

        buttonParanoidDownload.bounds = Rectangle(10, 410, 110, 25)
        buttonParanoidDownload.isEnabled = false
        linksPanel.add(buttonParanoidDownload)

        labelDerpFest.bounds = Rectangle(10, 445, 120, 20)
        linksPanel.add(labelDerpFest)

        buttonDerpFest.bounds = Rectangle(10, 465, 110, 25)
        buttonDerpFest.isFocusable = false
        linksPanel.add(buttonDerpFest)
        buttonDerpFest.addActionListener { runUrl("https://derpfest.org/") }

        buttonDerpFestDownload.bounds = Rectangle(10, 490, 110, 25)
        buttonDerpFestDownload.isEnabled = false
        linksPanel.add(buttonDerpFestDownload)

        labelZenX.bounds = Rectangle(130, 45, 100, 20)
        linksPanel.add(labelZenX)

        buttonZenX.bounds = Rectangle(130, 65, 110, 25)
        buttonZenX.isEnabled = false
        linksPanel.add(buttonZenX)

        buttonZenXDownload.bounds = Rectangle(130, 90, 110, 25)
        buttonZenXDownload.isFocusable = false
        linksPanel.add(buttonZenXDownload)
        buttonZenXDownload.addActionListener { runUrl("https://sourceforge.net/projects/zenx-os/files/") }
        labelEvolutionX.bounds = Rectangle(130, 125, 100, 20)
        linksPanel.add(labelEvolutionX)

        buttonEvolutionX.bounds = Rectangle(130, 145, 110, 25)
        buttonEvolutionX.isFocusable = false
        linksPanel.add(buttonEvolutionX)
        buttonEvolutionX.addActionListener { runUrl("https://evolution-x.org/#/") }

        buttonEvolutionXDownload.bounds = Rectangle(130, 170, 110, 25)
        buttonEvolutionXDownload.isFocusable = false
        linksPanel.add(buttonEvolutionXDownload)
        buttonEvolutionXDownload.addActionListener { runUrl("https://sourceforge.net/projects/evolution-x/files/") }

        labelCorvusOS.bounds = Rectangle(130, 205, 100, 20)
        linksPanel.add(labelCorvusOS)

        buttonCorvusOS.bounds = Rectangle(130, 225, 110, 25)
        buttonCorvusOS.isEnabled = false
        linksPanel.add(buttonCorvusOS)

        buttonCorvusOSDownload.bounds = Rectangle(130, 250, 110, 25)
        buttonCorvusOSDownload.isFocusable = false
        linksPanel.add(buttonCorvusOSDownload)
        buttonCorvusOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/corvus-os/files/") }

        labelIon.bounds = Rectangle(130, 285, 100, 20)
        linksPanel.add(labelIon)

        buttonIon.bounds = Rectangle(130, 305, 110, 25)
        buttonIon.isEnabled = false
        linksPanel.add(buttonIon)

        buttonIonDownload.bounds = Rectangle(130, 330, 110, 25)
        buttonIonDownload.isFocusable = false
        linksPanel.add(buttonIonDownload)
        buttonIonDownload.addActionListener { runUrl("https://sourceforge.net/projects/i-o-n/files/") }

        labelMoKee.bounds = Rectangle(130, 365, 100, 20)
        linksPanel.add(labelMoKee)

        buttonMoKee.bounds = Rectangle(130, 385, 110, 25)
        buttonMoKee.isFocusable = false
        linksPanel.add(buttonMoKee)
        buttonMoKee.addActionListener { runUrl("https://www.mokeedev.com/en/") }

        buttonMoKeeDownload.bounds = Rectangle(130, 410, 110, 25)
        buttonMoKeeDownload.isFocusable = false
        linksPanel.add(buttonMoKeeDownload)
        buttonMoKeeDownload.addActionListener { runUrl("https://download.mokeedev.com/") }

        labelBlissOS.bounds = Rectangle(130, 445, 100, 20)
        linksPanel.add(labelBlissOS)

        buttonBlissOS.bounds = Rectangle(130, 465, 110, 25)
        buttonBlissOS.isFocusable = false
        linksPanel.add(buttonBlissOS)
        buttonBlissOS.addActionListener { runUrl("https://blissroms.com/") }

        buttonBlissOSDownload.bounds = Rectangle(130, 490, 110, 25)
        buttonBlissOSDownload.isFocusable = false
        linksPanel.add(buttonBlissOSDownload)
        buttonBlissOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/blissroms/files/") }

        labelMSMXtended.bounds = Rectangle(250, 45, 100, 20)
        linksPanel.add(labelMSMXtended)

        buttonMSMXtended.bounds = Rectangle(250, 65, 110, 25)
        buttonMSMXtended.isFocusable = false
        linksPanel.add(buttonMSMXtended)
        buttonMSMXtended.addActionListener { runUrl("https://msmxtended.org/") }

        buttonMSMXtendedDownload.bounds = Rectangle(250, 90, 110, 25)
        buttonMSMXtendedDownload.isFocusable = false
        linksPanel.add(buttonMSMXtendedDownload)
        buttonMSMXtendedDownload.addActionListener { runUrl("https://sourceforge.net/projects/xtended/files/") }

        labelAOSPExtended.bounds = Rectangle(250, 125, 120, 20)
        linksPanel.add(labelAOSPExtended)

        buttonAOSPExtended.bounds = Rectangle(250, 145, 110, 25)
        buttonAOSPExtended.isFocusable = false
        linksPanel.add(buttonAOSPExtended)
        buttonAOSPExtended.addActionListener { runUrl("https://www.aospextended.com/") }

        buttonAOSPExtendedDownload.bounds = Rectangle(250, 170, 110, 25)
        buttonAOSPExtendedDownload.isFocusable = false
        linksPanel.add(buttonAOSPExtendedDownload)
        buttonAOSPExtendedDownload.addActionListener { runUrl("https://downloads.aospextended.com/") }

        labelAICPRom.bounds = Rectangle(250, 205, 100, 20)
        linksPanel.add(labelAICPRom)

        buttonAICPRom.bounds = Rectangle(250, 225, 110, 25)
        buttonAICPRom.isFocusable = false
        linksPanel.add(buttonAICPRom)
        buttonAICPRom.addActionListener { runUrl("https://dwnld.aicp-rom.com/") }

        buttonAICPRomDownload.bounds = Rectangle(250, 250, 110, 25)
        buttonAICPRomDownload.isEnabled = false
        linksPanel.add(buttonAICPRomDownload)

        labelArrowOS.bounds = Rectangle(250, 285, 100, 20)
        linksPanel.add(labelArrowOS)

        buttonArrowOS.bounds = Rectangle(250, 305, 110, 25)
        buttonArrowOS.isFocusable = false
        linksPanel.add(buttonArrowOS)
        buttonArrowOS.addActionListener { runUrl("https://arrowos.net/") }

        buttonArrowOSDownload.bounds = Rectangle(250, 330, 110, 25)
        buttonArrowOSDownload.isFocusable = false
        linksPanel.add(buttonArrowOSDownload)
        buttonArrowOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/arrow-os/files/") }

        labelCarbonROM.bounds = Rectangle(250, 365, 100, 20)
        linksPanel.add(labelCarbonROM)

        buttonCarbonROM.bounds = Rectangle(250, 385, 110, 25)
        buttonCarbonROM.isFocusable = false
        linksPanel.add(buttonCarbonROM)
        buttonCarbonROM.addActionListener { runUrl("https://carbonrom.org/") }

        buttonCarbonROMDownload.bounds = Rectangle(250, 410, 110, 25)
        buttonCarbonROMDownload.isFocusable = false
        linksPanel.add(buttonCarbonROMDownload)
        buttonCarbonROMDownload.addActionListener { runUrl("https://get.carbonrom.org/") }

        labelRevengeOS.bounds = Rectangle(250, 445, 100, 20)
        linksPanel.add(labelRevengeOS)

        buttonRevengeOS.bounds = Rectangle(250, 465, 110, 25)
        buttonRevengeOS.isEnabled = false
        linksPanel.add(buttonRevengeOS)

        buttonRevengeOSDownload.bounds = Rectangle(250, 490, 110, 25)
        buttonRevengeOSDownload.isFocusable = false
        linksPanel.add(buttonRevengeOSDownload)
        buttonRevengeOSDownload.addActionListener { runUrl("https://get.revengeos.com/") }

        labelXiaomiEU.bounds = Rectangle(370, 45, 100, 20)
        linksPanel.add(labelXiaomiEU)

        buttonXiaomiEU.bounds = Rectangle(370, 65, 110, 25)
        buttonXiaomiEU.isFocusable = false
        linksPanel.add(buttonXiaomiEU)
        buttonXiaomiEU.addActionListener { runUrl("https://xiaomi.eu/community/") }

        buttonXiaomiEUDownload.bounds = Rectangle(370, 90, 110, 25)
        buttonXiaomiEUDownload.isFocusable = false
        linksPanel.add(buttonXiaomiEUDownload)
        buttonXiaomiEUDownload.addActionListener { runUrl("https://sourceforge.net/projects/xiaomi-eu-multilang-miui-roms/files/xiaomi.eu/") }

        labelMasik.bounds = Rectangle(370, 125, 100, 20)
        linksPanel.add(labelMasik)

        buttonMasik.bounds = Rectangle(370, 145, 110, 25)
        buttonMasik.isFocusable = false
        linksPanel.add(buttonMasik)
        buttonMasik.addActionListener { runUrl("https://sites.google.com/view/masikupdates") }

        buttonMasikDownload.bounds = Rectangle(370, 170, 110, 25)
        buttonMasikDownload.isEnabled = false
        linksPanel.add(buttonMasikDownload)

        labelMiRoom.bounds = Rectangle(370, 205, 100, 20)
        linksPanel.add(labelMiRoom)

        buttonMiRoom.bounds = Rectangle(370, 225, 110, 25)
        buttonMiRoom.isFocusable = false
        linksPanel.add(buttonMiRoom)
        buttonMiRoom.addActionListener { runUrl("https://portal.mi-room.ru/roms/") }

        buttonMiRoomDownload.bounds = Rectangle(370, 250, 110, 25)
        buttonMiRoomDownload.isFocusable = false
        linksPanel.add(buttonMiRoomDownload)
        buttonMiRoomDownload.addActionListener { runUrl("https://sourceforge.net/projects/miroom/files/") }

        labelMiuiMix.bounds = Rectangle(370, 285, 100, 20)
        linksPanel.add(labelMiuiMix)

        buttonMiuiMix.bounds = Rectangle(370, 305, 110, 25)
        buttonMiuiMix.isEnabled = false
        linksPanel.add(buttonMiuiMix)

        buttonMiuiMixDownload.bounds = Rectangle(370, 330, 110, 25)
        buttonMiuiMixDownload.isFocusable = false
        linksPanel.add(buttonMiuiMixDownload)
        buttonMiuiMixDownload.addActionListener { runUrl("https://t.me/s/MiuiMix") }

        labelRevOS.bounds = Rectangle(370, 365, 100, 20)
        linksPanel.add(labelRevOS)

        buttonRevOS.bounds = Rectangle(370, 385, 110, 25)
        buttonRevOS.isFocusable = false
        linksPanel.add(buttonRevOS)
        buttonRevOS.addActionListener { runUrl("https://revtechs.me/") }

        buttonRevOSDownload.bounds = Rectangle(370, 410, 110, 25)
        buttonRevOSDownload.isFocusable = false
        linksPanel.add(buttonRevOSDownload)
        buttonRevOSDownload.addActionListener { runUrl("https://sourceforge.net/projects/revos/files/") }

        labelMiGlobe.bounds = Rectangle(370, 445, 100, 20)
        linksPanel.add(labelMiGlobe)

        buttonMiGlobe.bounds = Rectangle(370, 465, 110, 25)
        buttonMiGlobe.isFocusable = false
        linksPanel.add(buttonMiGlobe)
        buttonMiGlobe.addActionListener { runUrl("https://mi-globe.com/miui-firmware-rom-builder/") }

        buttonMiGlobeDownload.bounds = Rectangle(370, 490, 110, 25)
        buttonMiGlobeDownload.isEnabled = false
        linksPanel.add(buttonMiGlobeDownload)
        ////ROMs////

        ////GoogleApps////
        labelGapps.bounds = Rectangle(620, 5, 140, 30)
        labelGapps.font = labelGapps.font.deriveFont(22.0f)
        linksPanel.add(labelGapps)

        labelOpenGapps.bounds = Rectangle(630, 45, 100, 20)
        linksPanel.add(labelOpenGapps)

        buttonOpenGapps.bounds = Rectangle(630, 65, 110, 25)
        buttonOpenGapps.isFocusable = false
        linksPanel.add(buttonOpenGapps)
        buttonOpenGapps.addActionListener { runUrl("https://opengapps.org/") }

        buttonOpenGappsDownload.bounds = Rectangle(630, 90, 110, 25)
        buttonOpenGappsDownload.isFocusable = false
        linksPanel.add(buttonOpenGappsDownload)

        labelBiTGApps.bounds = Rectangle(630, 125, 100, 20)
        linksPanel.add(labelBiTGApps)

        buttonBiTGApps.bounds = Rectangle(630, 145, 110, 25)
        buttonBiTGApps.isFocusable = false
        linksPanel.add(buttonBiTGApps)

        buttonBiTGAppsDownload.bounds = Rectangle(630, 170, 110, 25)
        buttonBiTGAppsDownload.isFocusable = false
        linksPanel.add(buttonBiTGAppsDownload)
        buttonBiTGAppsDownload.addActionListener { runUrl("https://bitgapps.cf/") }

        labelFlameGApps.bounds = Rectangle(630, 205, 100, 20)
        linksPanel.add(labelFlameGApps)

        buttonFlameGApps.bounds = Rectangle(630, 225, 110, 25)
        buttonFlameGApps.isFocusable = false
        linksPanel.add(buttonFlameGApps)
        buttonFlameGApps.addActionListener { runUrl("https://flamegapps.github.io/") }

        buttonFlameGAppsDownload.bounds = Rectangle(630, 250, 110, 25)
        buttonFlameGAppsDownload.isFocusable = false
        linksPanel.add(buttonFlameGAppsDownload)
        buttonFlameGAppsDownload.addActionListener { runUrl("https://sourceforge.net/projects/flamegapps/files/") }

        labelNikGApps.bounds = Rectangle(630, 285, 100, 20)
        linksPanel.add(labelNikGApps)

        buttonNikGApps.bounds = Rectangle(630, 305, 110, 25)
        buttonNikGApps.isFocusable = false
        linksPanel.add(buttonNikGApps)
        buttonNikGApps.addActionListener { runUrl("https://nikgapps.com/") }

        buttonNikGAppsDownload.bounds = Rectangle(630, 330, 110, 25)
        buttonNikGAppsDownload.isFocusable = false
        linksPanel.add(buttonNikGAppsDownload)
        buttonNikGAppsDownload.addActionListener { runUrl("https://sourceforge.net/projects/nikgapps/files/Releases/") }
        ////GoogleApps////

        ////Recovery////
        labelRecovery.bounds = Rectangle(508, 5, 140, 30)
        labelRecovery.font = labelGapps.font.deriveFont(22.0f)
        linksPanel.add(labelRecovery)

        labelTwrp.bounds = Rectangle(500, 45, 100, 20)
        linksPanel.add(labelTwrp)

        buttonTwrp.bounds = Rectangle(500, 65, 110, 25)
        buttonTwrp.isFocusable = false
        linksPanel.add(buttonTwrp)
        buttonTwrp.addActionListener { runUrl("https://twrp.me/") }

        buttonTwrpDownload.bounds = Rectangle(500, 90, 110, 25)
        buttonTwrpDownload.isEnabled = false
        linksPanel.add(buttonTwrpDownload)

        labelOrangeFox.bounds = Rectangle(500, 125, 100, 20)
        linksPanel.add(labelOrangeFox)

        buttonOrangeFox.bounds = Rectangle(500, 145, 110, 25)
        buttonOrangeFox.isFocusable = false
        linksPanel.add(buttonOrangeFox)
        buttonOrangeFox.addActionListener { runUrl("https://wiki.orangefox.tech/en/home") }

        buttonOrangeFoxDownload.bounds = Rectangle(500, 170, 110, 25)
        buttonOrangeFoxDownload.isFocusable = false
        linksPanel.add(buttonOrangeFoxDownload)
        buttonOrangeFoxDownload.addActionListener { runUrl("https://sourceforge.net/projects/orangefox/files/") }

        labelSkyHawk.bounds = Rectangle(500, 205, 100, 20)
        linksPanel.add(labelSkyHawk)

        buttonSkyHawk.bounds = Rectangle(500, 225, 110, 25)
        buttonSkyHawk.isFocusable = false
        linksPanel.add(buttonSkyHawk)
        buttonSkyHawk.addActionListener { runUrl("https://skyhawk-recovery-project.github.io/#/") }

        buttonSkyHawkDownload.bounds = Rectangle(500, 250, 110, 25)
        buttonSkyHawkDownload.isFocusable = false
        linksPanel.add(buttonSkyHawkDownload)
        buttonSkyHawkDownload.addActionListener { runUrl("https://sourceforge.net/projects/shrp/files/") }

        labelPitchBlack.bounds = Rectangle(500, 285, 100, 20)
        linksPanel.add(labelPitchBlack)

        buttonPitchBlack.bounds = Rectangle(500, 305, 110, 25)
        buttonPitchBlack.isFocusable = false
        linksPanel.add(buttonPitchBlack)
        buttonPitchBlack.addActionListener { runUrl("https://pbrp.ml/") }

        buttonPitchBlackDownload.bounds = Rectangle(500, 330, 110, 25)
        buttonPitchBlackDownload.isFocusable = false
        linksPanel.add(buttonPitchBlackDownload)
        buttonPitchBlackDownload.addActionListener { runUrl("https://sourceforge.net/projects/pbrp/files/") }
        ////Recovery////

        ////Other////
        labelOther.bounds = Rectangle(780, 5, 140, 30)
        labelOther.font = labelGapps.font.deriveFont(22.0f)
        linksPanel.add(labelOther)

        labelMagisk.bounds = Rectangle(760, 45, 100, 20)
        linksPanel.add(labelMagisk)

        buttonMagisk.bounds = Rectangle(760, 65, 110, 25)
        buttonMagisk.isFocusable = false
        linksPanel.add(buttonMagisk)
        buttonMagisk.addActionListener { runUrl("https://github.com/topjohnwu/Magisk/releases/") }


        buttonSuperSU.bounds = Rectangle(760, 90, 110, 25)
        buttonSuperSU.isFocusable = false
        linksPanel.add(buttonSuperSU)
        buttonSuperSU.addActionListener { runUrl("https://download.chainfire.eu/1220/SuperSU/SR5-SuperSU-v2.82-SR5-20171001224502.zip") }

        labelGcam.bounds = Rectangle(760, 125, 120, 20)
        linksPanel.add(labelGcam)

        buttonGcam.bounds = Rectangle(760, 145, 110, 25)
        buttonGcam.isFocusable = false
        linksPanel.add(buttonGcam)
        buttonGcam.addActionListener { runUrl("https://www.celsoazevedo.com/files/android/google-camera/") }

        buttonChecker.bounds = Rectangle(760, 170, 110, 25)
        buttonChecker.isFocusable = false
        linksPanel.add(buttonChecker)
        buttonChecker.addActionListener { runUrl("https://www.apkmirror.com/apk/march-media-labs/camera2-api-probe/") }

        labelANX.bounds = Rectangle(760, 205, 100, 20)
        linksPanel.add(labelANX)

        buttonANX.bounds = Rectangle(760, 225, 110, 25)
        buttonANX.isFocusable = false
        linksPanel.add(buttonANX)
        buttonANX.addActionListener { runUrl("https://camera.aeonax.com/") }

        buttonANXDownload.bounds = Rectangle(760, 250, 110, 25)
        buttonANXDownload.isFocusable = false
        linksPanel.add(buttonANXDownload)
        buttonANXDownload.addActionListener { runUrl("https://sourceforge.net/projects/anxcamera/files/") }

        radioButtonAll.bounds = Rectangle(328, 35, 80, 20)
        adbPanel.add(radioButtonAll)
        groupApps.add(radioButtonAll)


        radioButtonDisabled.bounds = Rectangle(418, 35, 120, 20)
        adbPanel.add(radioButtonDisabled)
        groupApps.add(radioButtonDisabled)


        radioButtonSystem.bounds = Rectangle(538, 35, 120, 20)
        adbPanel.add(radioButtonSystem)
        groupApps.add(radioButtonSystem)


        radioButtonEnabled.bounds = Rectangle(658, 35, 100, 20)
        adbPanel.add(radioButtonEnabled)
        groupApps.add(radioButtonEnabled)


        radioButtonThird.bounds = Rectangle(778, 35, 120, 20)
        adbPanel.add(radioButtonThird)
        groupApps.add(radioButtonThird)

        ////CheckBox////

        ////Button////


        buttonInstallAll.bounds = Rectangle(5, 25, 285, 50)
        buttonInstallAll.isFocusable = false
        adbPanel.add(buttonInstallAll)
        labelInstallAll.bounds = Rectangle(7, 5, 250, 20)
        adbPanel.add(labelInstallAll)


        labelInstallOne.bounds = Rectangle(7, 165, 250, 50)
        adbPanel.add(labelInstallOne)


        labelSelectedOne.bounds = Rectangle(7, 305, 250, 20)
        adbPanel.add(labelSelectedOne)


        labelSelectedAll.bounds = Rectangle(7, 115, 250, 50)
        adbPanel.add(labelSelectedAll)


        labelEnterAdbCommand.bounds = Rectangle(7, 425, 250, 20)
        adbPanel.add(labelEnterAdbCommand)


        labelOutputAdbCommand.bounds = Rectangle(317, 425, 250, 20)
        adbPanel.add(labelOutputAdbCommand)
        textAreaInput.bounds = Rectangle(608, 263, 267, 45)
        adbPanel.add(textAreaInput)
        textAreaInput.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (textAreaInput.isEnabled)
                    textAreaInput.text = ""
            }
        })
        textAreaInput.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                textAreaInput.text = "You can enter app package here"
            }
        })
        dialogUnauthorizedDevice.setSize(400, 330)
        dialogUnauthorizedDevice.isResizable = false
        dialogUnauthorizedDevice.layout = null
        dialogUnauthorizedDevice.setLocationRelativeTo(null)
        dialogUnauthorizedDevice.rootPane.border = BorderFactory.createLineBorder(Color.decode("#585858"))

        labelUnauthorized.bounds = Rectangle(20, 5, 400, 35)
        dialogUnauthorizedDevice.add(labelUnauthorized)


        labelSample.bounds = Rectangle(23, 50, 400, 15)
        dialogUnauthorizedDevice.add(labelSample)


        labelScreenshotUnauthorized.bounds = Rectangle(0, 65, 400, 220)
        dialogUnauthorizedDevice.add(labelScreenshotUnauthorized)





        dialogMultipleDevice.setSize(300, 100)
        dialogMultipleDevice.isResizable = false
        dialogMultipleDevice.layout = null
        dialogMultipleDevice.setLocationRelativeTo(null)
        dialogMultipleDevice.rootPane.border = BorderFactory.createLineBorder(Color.decode("#585858"))

        labelMultipleDevice.bounds = Rectangle(20, 5, 400, 35)
        dialogMultipleDevice.add(labelMultipleDevice)

        dialogSdkDownload.setSize(380, 100)
        dialogSdkDownload.isResizable = false
        dialogSdkDownload.layout = null
        dialogSdkDownload.setLocationRelativeTo(null)
        dialogSdkDownload.rootPane.border = BorderFactory.createLineBorder(Color.decode("#585858"))

        labelSdkDownload.bounds = Rectangle(20, 5, 400, 35)
        dialogSdkDownload.add(labelSdkDownload)

        buttonSdkDownload.bounds = Rectangle(260, 40, 100, 25)
        dialogSdkDownload.add(buttonSdkDownload)


        dialogUpdate.setSize(423, 110)
        dialogUpdate.isResizable = false
        dialogUpdate.layout = null
        dialogUpdate.setLocationRelativeTo(null)
        dialogUpdate.rootPane.border = BorderFactory.createLineBorder(Color.decode("#585858"))

        labelUpdate.bounds = Rectangle(20, 5, 400, 35)
        dialogUpdate.add(labelUpdate)

        labelUpdateVersion.bounds = Rectangle(20, 35, 400, 35)
        dialogUpdate.add(labelUpdateVersion)

        buttonUpdate.bounds = Rectangle(310, 45, 100, 25)
        dialogUpdate.add(buttonUpdate)


        buttonStop.bounds = Rectangle(5, 468, 150, 35)
        buttonStop.isFocusable = false
        buttonStop.isEnabled = false
        logsPanel.add(buttonStop)
        buttonSave.bounds = Rectangle(5, 505, 150, 35)
        buttonSave.isFocusable = false
        buttonSave.isEnabled = false
        logsPanel.add(buttonSave)
        radioButtonVerbose.bounds = Rectangle(165, 436, 80, 20)
        logsPanel.add(radioButtonVerbose)
        group.add(radioButtonVerbose)


        radioButtonDebug.bounds = Rectangle(265, 436, 80, 20)
        logsPanel.add(radioButtonDebug)
        group.add(radioButtonDebug)


        radioButtonInfo.bounds = Rectangle(365, 436, 80, 20)
        radioButtonInfo.isSelected = true
        logsPanel.add(radioButtonInfo)
        group.add(radioButtonInfo)


        radioButtonWarning.bounds = Rectangle(465, 436, 80, 20)
        logsPanel.add(radioButtonWarning)
        group.add(radioButtonWarning)


        radioButtonError.bounds = Rectangle(565, 436, 80, 20)
        logsPanel.add(radioButtonError)
        group.add(radioButtonError)


        radioButtonFatal.bounds = Rectangle(665, 436, 80, 20)
        logsPanel.add(radioButtonFatal)
        group.add(radioButtonFatal)


        radioButtonSilent.bounds = Rectangle(765, 436, 80, 20)
        logsPanel.add(radioButtonSilent)
        group.add(radioButtonSilent)
        frame.setSize(1205, 610)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = null
        frame.isResizable = false
        frame.setLocationRelativeTo(null)
        frame.iconImage = ImageIcon(this.javaClass.getResource("/icon/frameIcon.png")).image
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                Runtime.getRuntime().exec("${SdkDir}adb kill-server")
            }
        })


        fastbootPanel.layout = null



        BootloaderFastbootInfoPanel.layout = null
        BootloaderFastbootInfoPanel.isVisible = false
        BootloaderFastbootInfoPanel.setBounds(10, 15, 290, 45)

        title12.titleJustification = TitledBorder.LEFT;
        BootloaderFastbootInfoPanel.border = title12
        deviceInfoPanel.add(BootloaderFastbootInfoPanel)


        labelUnlock.bounds = Rectangle(15, 16, 60, 20)
        labelUnlock.font = labelUnlock.font.deriveFont(14.0f)
        BootloaderFastbootInfoPanel.add(labelUnlock)


        labelUnlockValue.bounds = Rectangle(80, 16, 205, 20)
        labelUnlockValue.font = labelUnlockValue.font.deriveFont(12.0f)
        BootloaderFastbootInfoPanel.add(labelUnlockValue)



        softFastbootInfoPanel.layout = null
        softFastbootInfoPanel.setBounds(10, 65, 290, 65)

        title9.titleJustification = TitledBorder.LEFT;
        softFastbootInfoPanel.border = title9
        deviceInfoPanel.add(softFastbootInfoPanel)


        labelFastbootCodename.bounds = Rectangle(15, 16, 70, 20)
        labelFastbootCodename.font = labelFastbootCodename.font.deriveFont(14.0f)
        softFastbootInfoPanel.add(labelFastbootCodename)


        labelFastbootCodenameValue.bounds = Rectangle(90, 16, 190, 20)
        labelFastbootCodenameValue.font = labelFastbootCodenameValue.font.deriveFont(12.0f)
        softFastbootInfoPanel.add(labelFastbootCodenameValue)


        labelFastbootSN.bounds = Rectangle(15, 36, 90, 20)
        labelFastbootSN.font = labelFastbootSN.font.deriveFont(14.0f)
        softFastbootInfoPanel.add(labelFastbootSN)


        labelFastbootSNValue.bounds = Rectangle(110, 36, 180, 20)
        labelFastbootSNValue.font = labelFastbootSNValue.font.deriveFont(12.0f)
        softFastbootInfoPanel.add(labelFastbootSNValue)


        StorageFastbootInfoPanel.layout = null
        StorageFastbootInfoPanel.setBounds(10, 135, 290, 285)

        title93.titleJustification = TitledBorder.LEFT;
        StorageFastbootInfoPanel.border = title93
        deviceInfoPanel.add(StorageFastbootInfoPanel)


        labelSystemFS.bounds = Rectangle(15, 16, 130, 20)
        labelSystemFS.font = labelSystemFS.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelSystemFS)


        labelSystemFSValue.bounds = Rectangle(145, 16, 140, 20)
        labelSystemFSValue.font = labelSystemFSValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelSystemFSValue)



        labelSystemCapacity.bounds = Rectangle(15, 36, 150, 20)
        labelSystemCapacity.font = labelSystemCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelSystemCapacity)


        labelSystemCapacityValue.bounds = Rectangle(165, 36, 120, 20)
        labelSystemCapacityValue.font = labelSystemCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelSystemCapacityValue)


        labelDataFS.bounds = Rectangle(15, 56, 115, 20)
        labelDataFS.font = labelDataFS.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelDataFS)


        labelDataFSValue.bounds = Rectangle(130, 56, 150, 20)
        labelDataFSValue.font = labelDataFSValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelDataFSValue)



        labelDataCapacity.bounds = Rectangle(15, 76, 125, 20)
        labelDataCapacity.font = labelDataCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelDataCapacity)


        labelDataCapacityValue.bounds = Rectangle(145, 76, 140, 20)
        labelDataCapacityValue.font = labelDataCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelDataCapacityValue)


        labelBootFS.bounds = Rectangle(15, 96, 118, 20)
        labelBootFS.font = labelBootFS.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelBootFS)


        labelBootFSValue.bounds = Rectangle(133, 96, 145, 20)
        labelBootFSValue.font = labelBootFSValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelBootFSValue)



        labelBootCapacity.bounds = Rectangle(15, 116, 135, 20)
        labelBootCapacity.font = labelBootCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelBootCapacity)


        labelBootCapacityValue.bounds = Rectangle(150, 116, 135, 20)
        labelBootCapacityValue.font = labelBootCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelBootCapacityValue)


        labelRecoveryFS.bounds = Rectangle(15, 136, 140, 20)
        labelRecoveryFS.font = labelRecoveryFS.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelRecoveryFS)


        labelRecoveryFSValue.bounds = Rectangle(155, 136, 130, 20)
        labelRecoveryFSValue.font = labelRecoveryFSValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelRecoveryFSValue)



        labelRecoveryCapacity.bounds = Rectangle(15, 156, 150, 20)
        labelRecoveryCapacity.font = labelRecoveryCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelRecoveryCapacity)


        labelRecoveryCapacityValue.bounds = Rectangle(170, 156, 115, 20)
        labelRecoveryCapacityValue.font = labelRecoveryCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelRecoveryCapacityValue)


        labelCacheFS.bounds = Rectangle(15, 176, 120, 20)
        labelCacheFS.font = labelCacheFS.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelCacheFS)


        labelCacheFSValue.bounds = Rectangle(135, 176, 145, 20)
        labelCacheFSValue.font = labelCacheFSValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelCacheFSValue)



        labelCacheCapacity.bounds = Rectangle(15, 196, 140, 20)
        labelCacheCapacity.font = labelCacheCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelCacheCapacity)


        labelCacheCapacityValue.bounds = Rectangle(155, 196, 130, 20)
        labelCacheCapacityValue.font = labelCacheCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelCacheCapacityValue)


        labelVendorFS.bounds = Rectangle(15, 216, 130, 20)
        labelVendorFS.font = labelVendorFS.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelVendorFS)


        labelVendorFSValue.bounds = Rectangle(145, 216, 130, 20)
        labelVendorFSValue.font = labelVendorFSValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelVendorFSValue)



        labelVendorCapacity.bounds = Rectangle(15, 236, 140, 20)
        labelVendorCapacity.font = labelVendorCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelVendorCapacity)


        labelVendorCapacityValue.bounds = Rectangle(160, 236, 125, 20)
        labelVendorCapacityValue.font = labelVendorCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelVendorCapacityValue)


        labelAllCapacity.bounds = Rectangle(15, 256, 115, 20)
        labelAllCapacity.font = labelAllCapacity.font.deriveFont(14.0f)
        StorageFastbootInfoPanel.add(labelAllCapacity)


        labelAllCapacityValue.bounds = Rectangle(130, 256, 145, 20)
        labelAllCapacityValue.font = labelAllCapacityValue.font.deriveFont(12.0f)
        StorageFastbootInfoPanel.add(labelAllCapacityValue)



        deviceControlPanel.layout = null
        deviceControlPanel.setBounds(5, 385, 310, 85)

        title2.titleJustification = TitledBorder.CENTER;
        deviceControlPanel.border = title2
        frame.add(deviceControlPanel)


        deviceConnection.layout = null
        deviceConnection.setBounds(5, 475, 310, 100)

        title3.titleJustification = TitledBorder.CENTER;
        deviceConnection.border = title3
        frame.add(deviceConnection)

        adbPanel.layout = null


        labelUSB.bounds = Rectangle(15, 16, 50, 20)
        labelUSB.font = labelUSB.font.deriveFont(14.0f)
        deviceConnection.add(labelUSB)

        labelTCP.bounds = Rectangle(15, 36, 50, 20)
        labelTCP.font = labelTCP.font.deriveFont(14.0f)
        deviceConnection.add(labelTCP)


        deviceInfoPanel.layout = null
        deviceInfoPanel.setBounds(5, 5, 310, 376)

        title1.titleJustification = TitledBorder.CENTER;
        deviceInfoPanel.border = title1
        frame.add(deviceInfoPanel)
        boardInfoPanel.layout = null
        boardInfoPanel.setBounds(10, 15, 290, 185)

        title5.titleJustification = TitledBorder.LEFT;
        boardInfoPanel.border = title5
        deviceInfoPanel.add(boardInfoPanel)

        labelManufacturerValue.bounds = Rectangle(110, 16, 170, 20)
        labelManufacturerValue.font = labelManufacturerValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelManufacturerValue)

        labelBrandValue.bounds = Rectangle(60, 36, 225, 20)
        labelBrandValue.font = labelBrandValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelBrandValue)

        labelModelValue.bounds = Rectangle(65, 56, 220, 20)
        labelModelValue.font = labelModelValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelModelValue)

        labelCodenameValue.bounds = Rectangle(90, 76, 195, 20)
        labelCodenameValue.font = labelCodenameValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelCodenameValue)

        labelCPUValue.bounds = Rectangle(50, 96, 230, 20)
        labelCPUValue.font = labelCPUValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelCPUValue)

        labelCPUAValue.bounds = Rectangle(130, 116, 150, 20)
        labelCPUAValue.font = labelCPUAValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelCPUAValue)

        labelSNValue.bounds = Rectangle(110, 136, 170, 20)
        labelSNValue.font = labelSNValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelSNValue)

        labelGsmOperatorValue.bounds = Rectangle(125, 156, 155, 20)
        labelGsmOperatorValue.font = labelGsmOperatorValue.font.deriveFont(12.0f)
        boardInfoPanel.add(labelGsmOperatorValue)


        labelManufacturer.bounds = Rectangle(15, 16, 90, 20)
        labelManufacturer.font = labelManufacturer.font.deriveFont(14.0f)
        boardInfoPanel.add(labelManufacturer)

        labelBrand.bounds = Rectangle(15, 36, 40, 20)
        labelBrand.font = labelBrand.font.deriveFont(14.0f)
        boardInfoPanel.add(labelBrand)

        labelModel.bounds = Rectangle(15, 56, 45, 20)
        labelModel.font = labelModel.font.deriveFont(14.0f)
        boardInfoPanel.add(labelModel)

        labelCodename.bounds = Rectangle(15, 76, 70, 20)
        labelCodename.font = labelCodename.font.deriveFont(14.0f)
        boardInfoPanel.add(labelCodename)

        labelCPU.bounds = Rectangle(15, 96, 30, 20)
        labelCPU.font = labelCPU.font.deriveFont(14.0f)
        boardInfoPanel.add(labelCPU)

        labelCPUA.bounds = Rectangle(15, 116, 110, 20)
        labelCPUA.font = labelCPUA.font.deriveFont(14.0f)
        boardInfoPanel.add(labelCPUA)

        labelSN.bounds = Rectangle(15, 136, 90, 20)
        labelSN.font = labelSN.font.deriveFont(14.0f)
        boardInfoPanel.add(labelSN)

        labelGsmOperator.bounds = Rectangle(15, 156, 110, 20)
        labelGsmOperator.font = labelGsmOperator.font.deriveFont(14.0f)
        boardInfoPanel.add(labelGsmOperator)


        softInfoPanel.layout = null
        softInfoPanel.setBounds(10, 205, 290, 160)

        title6.titleJustification = TitledBorder.LEFT;
        softInfoPanel.border = title6
        deviceInfoPanel.add(softInfoPanel)


        labelFingerprint.bounds = Rectangle(15, 15, 90, 20)
        labelFingerprint.font = labelFingerprint.font.deriveFont(14.0f)
        softInfoPanel.add(labelFingerprint)


        labelFingerprintValue.bounds = Rectangle(90, 15, 195, 20)
        labelFingerprintValue.font = labelFingerprintValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelFingerprintValue)


        labelVersionRelease.bounds = Rectangle(15, 34, 110, 20)
        labelVersionRelease.font = labelVersionRelease.font.deriveFont(14.0f)
        softInfoPanel.add(labelVersionRelease)


        labelVersionReleaseValue.bounds = Rectangle(120, 34, 160, 20)
        labelVersionReleaseValue.font = labelVersionReleaseValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelVersionReleaseValue)


        labelSDK.bounds = Rectangle(15, 54, 95, 20)
        labelSDK.font = labelSDK.font.deriveFont(14.0f)
        softInfoPanel.add(labelSDK)


        labelSDKValue.bounds = Rectangle(100, 54, 180, 20)
        labelSDKValue.font = labelSDKValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelSDKValue)


        labelSecurityPatch.bounds = Rectangle(15, 73, 90, 20)
        labelSecurityPatch.font = labelSecurityPatch.font.deriveFont(14.0f)
        softInfoPanel.add(labelSecurityPatch)


        labelSecurityPatchValue.bounds = Rectangle(110, 73, 170, 20)
        labelSecurityPatchValue.font = labelSecurityPatchValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelSecurityPatchValue)


        labelLanguage.bounds = Rectangle(15, 93, 70, 20)
        labelLanguage.font = labelLanguage.font.deriveFont(14.0f)
        softInfoPanel.add(labelLanguage)


        labelLanguageValue.bounds = Rectangle(85, 93, 195, 20)
        labelLanguageValue.font = labelLanguageValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelLanguageValue)


        labelSelinux.bounds = Rectangle(15, 113, 50, 18)
        labelSelinux.font = labelSelinux.font.deriveFont(14.0f)
        softInfoPanel.add(labelSelinux)


        labelSelinuxValue.bounds = Rectangle(65, 113, 210, 18)
        labelSelinuxValue.font = labelSelinuxValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelSelinuxValue)



        labelTreble.bounds = Rectangle(15, 131, 140, 20)
        labelTreble.font = labelTreble.font.deriveFont(14.0f)
        softInfoPanel.add(labelTreble)


        labelTrebleValue.bounds = Rectangle(158, 131, 125, 20)
        labelTrebleValue.font = labelTrebleValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelTrebleValue)

        labelSecureBoot.bounds = Rectangle(15, 150, 140, 20)
        labelSecureBoot.font = labelSecureBoot.font.deriveFont(14.0f)
        softInfoPanel.add(labelSecureBoot)
        labelSecureBoot.isVisible = false


        labelSecureBootValue.bounds = Rectangle(98, 150, 125, 20)
        labelSecureBootValue.font = labelSecureBootValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelSecureBootValue)
        labelSecureBootValue.isVisible = false

        labelDeviceHostname.bounds = Rectangle(15, 170, 140, 20)
        labelDeviceHostname.font = labelDeviceHostname.font.deriveFont(14.0f)
        softInfoPanel.add(labelDeviceHostname)
        labelSecureBoot.isVisible = false


        labelDeviceHostnameValue.bounds = Rectangle(120, 170, 120, 20)
        labelDeviceHostnameValue.font = labelDeviceHostnameValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelDeviceHostnameValue)
        labelSecureBootValue.isVisible = false

        labelLocations.bounds = Rectangle(15, 190, 140, 20)
        labelLocations.font = labelLocations.font.deriveFont(14.0f)
        softInfoPanel.add(labelLocations)
        labelSecureBoot.isVisible = false


        labelLocationsValue.bounds = Rectangle(158, 190, 125, 20)
        labelLocationsValue.font = labelLocationsValue.font.deriveFont(12.0f)
        softInfoPanel.add(labelLocationsValue)
        labelSecureBootValue.isVisible = false



        scrollPaneLogs.setBounds(5, 5, 870, 425)
        scrollPaneLogs.setViewportView(listLogs)
        logsPanel.add(scrollPaneLogs)
        ////ScrollPane////


        scrollPane.setViewportView(list)
        scrollPane.setBounds(328, 55, 550, 195)
        adbPanel.add(scrollPane)


        labelICon.bounds = Rectangle(330, 9, 20, 20)
        adbPanel.add(labelICon)


        textFieldIPa.bounds = Rectangle(355, 7, 200, 25)
        textFieldIPa.isFocusable = false
        adbPanel.add(textFieldIPa)

        labelUSBConnection.bounds = Rectangle(47, 17, 200, 21)
        labelUSBConnection.font = labelUSB.font.deriveFont(12.0f)
        labelUSBConnection.icon = iconNo


        labelTCPConnection.bounds = Rectangle(64, 36, 200, 21)
        labelTCPConnection.font = labelTCP.font.deriveFont(12.0f)
        labelTCPConnection.icon = iconNo
        deviceConnection.add(labelTCPConnection)
        deviceConnection.add(labelUSBConnection)



        labelIP.bounds = Rectangle(27, 65, 20, 25)
        labelIP.font = labelIP.font.deriveFont(15.0f)
        deviceConnection.add(labelIP)


        labelConnect.bounds = Rectangle(255, 65, 100, 23)
        labelConnect.font = labelIP.font.deriveFont(13.0f)
        deviceConnection.add(labelConnect)


        buttonIpConnect.bounds = Rectangle(147, 65, 100, 25)
        buttonIpConnect.isFocusable = false
        deviceConnection.add(buttonIpConnect)


        textFieldIP.bounds = Rectangle(45, 65, 100, 25)
        textFieldIP.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent?) {
                textFieldIP.caretPosition = textFieldIP.text.length
            }
        })
        deviceConnection.add(textFieldIP)

        tabbedpane.setBounds(320, 0, 895, 580)


        logsPanel.layout = null


        buttonPowerOff.bounds = Rectangle(13, 45, 120, 25)
        buttonPowerOff.isFocusable = false
        buttonPowerOff.isEnabled = false
        deviceControlPanel.add(buttonPowerOff)


        buttonFastbootReboot.bounds = Rectangle(134, 45, 163, 25)
        buttonFastbootReboot.isFocusable = false
        buttonFastbootReboot.isEnabled = false
        deviceControlPanel.add(buttonFastbootReboot)


        buttonRecoveryReboot.bounds = Rectangle(134, 20, 163, 25)
        buttonRecoveryReboot.isFocusable = false
        buttonRecoveryReboot.isEnabled = false
        deviceControlPanel.add(buttonRecoveryReboot)


        buttonReboot.bounds = Rectangle(13, 20, 120, 25)
        buttonReboot.isFocusable = false
        buttonReboot.isEnabled = false
        deviceControlPanel.add(buttonReboot)


        textAreaCommandFastbootInput.bounds = Rectangle(6, 445, 305, 65)

        scroll24.setBounds(6, 445, 305, 65)
        fastbootPanel.add(scroll24)


        textAreaCommandFastbootOutput.bounds = Rectangle(315, 443, 560, 98)

        scroll2.setBounds(315, 443, 560, 98)
        fastbootPanel.add(scroll2)

        textAreaCommandInput.bounds = Rectangle(6, 445, 305, 65)

        scroll23.setBounds(6, 445, 305, 65)
        adbPanel.add(scroll23)

        textAreaCommandOutput.bounds = Rectangle(315, 443, 560, 98)

        scroll.setBounds(315, 443, 560, 98)
        adbPanel.add(scroll)

        DatagramSocket().use { socket ->
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
            sdf = "${socket.localAddress.hostAddress.substringBeforeLast('.')}."
        }
        try {
            textFieldIP.text = sdf
        } catch (e: Exception) {
        }
        tabbedpane.add("App Manager", adbPanel)
        tabbedpane.add("Logcat", logsPanel)
        tabbedpane.add("Fastboot", fastbootPanel)
        tabbedpane.add("Recovery", recoveryPanel)
        tabbedpane.add("Links", linksPanel)

        val components: Array<Component> = fastbootPanel.components
        for (component in components) {
            component.isEnabled = false
        }
        val components2: Array<Component> = adbPanel.components
        for (component in components2) {
            component.isEnabled = false
        }
        val components3: Array<Component> = logsPanel.components
        for (component in components3) {
            component.isEnabled = false
        }
        val components4: Array<Component> = recoveryPanel.components
        for (component in components4) {
            component.isEnabled = false
        }
        frame.add(tabbedpane)
    }

}