fun getProp() {
	val deviceProps = exec("adb", "shell getprop", output = true)
	val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [")
	Manufacturer = if (lineValue1 == deviceProps) "Unknown" else lineValue1.substringBefore(']')
	val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [")
	Brand = if (lineValue2 == deviceProps) "Unknown" else lineValue2.substringBefore(']')
	val lineValue3 = deviceProps.substringAfter("ro.product.model]: [")
	Model = if (lineValue3 == deviceProps) "Unknown" else lineValue3.substringBefore(']')
	val lineValue4 = deviceProps.substringAfter("ro.product.name]: [")
	Codename = if (lineValue4 == deviceProps) "Unknown" else lineValue4.substringBefore(']')
	val lineValue5 = deviceProps.substringAfter("ro.product.board]: [")
	CPU = if (lineValue5 == deviceProps) "Unknown" else lineValue5.substringBefore(']')
	val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [")
	CPUArch = if (lineValue6 == deviceProps) "Unknown" else lineValue6.substringBefore(']')
	val lineValue7 = deviceProps.substringAfter("ro.serialno]: [")
	SN = if (lineValue7 == deviceProps) "Unknown" else lineValue7.substringBefore(']')
	val lineValue8 = deviceProps.substringAfter("gsm.operator.alpha]: [")
	GsmOperator = if (lineValue8 == deviceProps || lineValue8 == ",") "Unknown" else lineValue8.substringBefore(']')
	val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [")
	Fingerprint = if (lineValue9 == deviceProps) "Unknown" else lineValue9.substringBefore(']')
	val lineValue10 = deviceProps.substringAfter("ro.build.version.release]: [")
	VersionRelease = if (lineValue10 == deviceProps) "Unknown" else lineValue10.substringBefore(']')
	val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [")
	SDK = if (lineValue11 == deviceProps) "Unknown" else lineValue11.substringBefore(']')
	val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [")
	SecurityPatch = if (lineValue12 == deviceProps) "Unknown" else lineValue12.substringBefore(']')
	val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [")
	Language = if (lineValue13 == deviceProps) "Unknown" else lineValue13.substringBefore(']')
	val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [")
	Selinux =
		if (lineValue14 == deviceProps || "DEVICE" in lineValue14) "Unknown" else lineValue14.substringBefore(']')
	val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [")
	Treble = if (lineValue15 == deviceProps) "Unknown" else lineValue15.substringBefore(']')
	model.addRow(arrayOf("Manufacturer", Manufacturer))
	model.addRow(arrayOf("Brand", Brand))
	model.addRow(arrayOf("Model", Model))
	model.addRow(arrayOf("Codename", Codename))
	model.addRow(arrayOf("CPU", CPU))
	model.addRow(arrayOf("CPU Architecture", CPUArch))
	model.addRow(arrayOf("Serial Number", SN))
	model.addRow(arrayOf("Cellular Provider", GsmOperator))
	model.addRow(arrayOf("Fingerprint", Fingerprint))
	model.addRow(arrayOf("Android Version", VersionRelease))
	model.addRow(arrayOf("SDK Version", SDK))
	model.addRow(arrayOf("Security Patch", SecurityPatch))
	model.addRow(arrayOf("Language", Language))
	model.addRow(arrayOf("Selinux", Selinux))
	model.addRow(arrayOf("Project Treble", Treble))
}

fun getPropFastboot() {
	val fastbootProps = exec("fastboot", "getvar all", output = true, streamType = "Error")
	Unlock = fastbootProps.substringAfter("(bootloader) unlocked:").substringBefore("(bootloader) ").trimMargin()
	FastbootCodename =
		fastbootProps.substringAfter("(bootloader) product:").substringBefore("(bootloader) ").trimMargin()
	FastbootSN =
		fastbootProps.substringAfter("(bootloader) serialno:").substringBefore("(bootloader) ").trimMargin()
	SystemFS = fastbootProps.substringAfter("(bootloader) partition-type:system:").substringBefore("(bootloader) ")
		.trimMargin()
	val systemDec =
		fastbootProps.substringAfter("(bootloader) partition-size:system: 0x").substringBefore("(bootloader) ")
			.trimMargin()
	SystemCapacity = (java.lang.Long.parseLong(systemDec, 16) / 1048576).toString().trimMargin()
	DataFS = fastbootProps.substringAfter("(bootloader) partition-type:userdata:").substringBefore("(bootloader) ")
		.trimMargin()
	val dataDec =
		fastbootProps.substringAfter("(bootloader) partition-size:userdata: 0x").substringBefore("(bootloader) ")
			.trimMargin()
	DataCapacity = (java.lang.Long.parseLong(dataDec, 16) / 1048576).toString().trimMargin()
	BootFS = fastbootProps.substringAfter("(bootloader) partition-type:boot:").substringBefore("(bootloader) ")
		.trimMargin()
	val bootDec =
		fastbootProps.substringAfter("(bootloader) partition-size:boot: 0x").substringBefore("(bootloader) ")
			.trimMargin()
	BootCapacity = (java.lang.Long.parseLong(bootDec, 16) / 1048576).toString().trimMargin()
	RecoveryFS =
		fastbootProps.substringAfter("(bootloader) partition-type:recovery:").substringBefore("(bootloader) ")
			.trimMargin()
	val recoveryDec =
		fastbootProps.substringAfter("(bootloader) partition-size:recovery: 0x").substringBefore("(bootloader) ")
			.trimMargin()
	RecoveryCapacity = (java.lang.Long.parseLong(recoveryDec, 16) / 1048576).toString().trimMargin()
	CacheFS = fastbootProps.substringAfter("(bootloader) partition-type:cache:").substringBefore("(bootloader) ")
		.trimMargin()
	val cacheDec =
		fastbootProps.substringAfter("(bootloader) partition-size:cache: 0x").substringBefore("(bootloader) ")
			.trimMargin()
	CacheCapacity = (java.lang.Long.parseLong(cacheDec, 16) / 1048576).toString().trimMargin()
	VendorFS = fastbootProps.substringAfter("(bootloader) partition-type:vendor:").substringBefore("(bootloader) ")
		.trimMargin()
	val vendorDec =
		fastbootProps.substringAfter("(bootloader) partition-size:vendor: 0x").substringBefore("(bootloader) ")
			.trimMargin()
	VendorCapacity = (java.lang.Long.parseLong(vendorDec, 16) / 1048576).toString()
	AllCapacity =
		(SystemCapacity.toInt() + DataCapacity.toInt() + BootCapacity.toInt() + RecoveryCapacity.toInt() + CacheCapacity.toInt() + VendorCapacity.toInt()).toString()
	model.addRow(arrayOf("Unlocked", if (Unlock != "< waiting for any device >") Unlock else "-"))
	model.addRow(
		arrayOf(
			"Codename",
			if (FastbootCodename != "< waiting for any device >") FastbootCodename else "-"
		)
	)
	model.addRow(arrayOf("Serial Number", if (FastbootSN != "< waiting for any device >") FastbootSN else "-"))
	model.addRow(arrayOf("/system File system:", if (SystemFS != "< waiting for any device >") SystemFS else "-"))
	model.addRow(
		arrayOf(
			"/system Capacity (MB):",
			if (SystemCapacity != "< waiting for any device >") SystemCapacity else "-"
		)
	)
	model.addRow(arrayOf("/data File system:", if (DataFS != "< waiting for any device >") DataFS else "-"))
	model.addRow(
		arrayOf(
			"/data Capacity (MB):",
			if (DataCapacity != "< waiting for any device >") DataCapacity else "-"
		)
	)
	model.addRow(arrayOf("/boot File system:", if (BootFS != "< waiting for any device >") BootFS else "-"))
	model.addRow(
		arrayOf(
			"/boot Capacity (MB):",
			if (BootCapacity != "< waiting for any device >") BootCapacity else "-"
		)
	)
	model.addRow(
		arrayOf(
			"/recovery File system:",
			if (RecoveryFS != "< waiting for any device >") RecoveryFS else "-"
		)
	)
	model.addRow(
		arrayOf(
			"/recovery Capacity (MB):",
			if (RecoveryCapacity != "< waiting for any device >") RecoveryCapacity else "-"
		)
	)
	model.addRow(arrayOf("/cache File system:", if (CacheFS != "< waiting for any device >") CacheFS else "-"))
	model.addRow(
		arrayOf(
			"/cache Capacity (MB):",
			if (CacheCapacity != "< waiting for any device >") CacheCapacity else "-"
		)
	)
	model.addRow(arrayOf("/vendor File system:", if (VendorFS != "< waiting for any device >") VendorFS else "-"))
	model.addRow(
		arrayOf(
			"/vendor Capacity (MB):",
			if (VendorCapacity != "< waiting for any device >") VendorCapacity else "-"
		)
	)
	model.addRow(
		arrayOf(
			"All Capacity (MB):",
			if (AllCapacity != "< waiting for any device >") AllCapacity else "-"
		)
	)
}

fun getPropRecovery() {
	val deviceProps = exec("adb", "shell getprop", output = true)
	val lineValue1 = deviceProps.substringAfter("ro.product.manufacturer]: [")
	Manufacturer = if (lineValue1 == deviceProps) "Unknown" else lineValue1.substringBefore(']')
	val lineValue2 = deviceProps.substringAfter("ro.product.brand]: [")
	Brand = if (lineValue2 == deviceProps) "Unknown" else lineValue2.substringBefore(']')
	val lineValue3 = deviceProps.substringAfter("ro.product.model]: [")
	Model = if (lineValue3 == deviceProps) "Unknown" else lineValue3.substringBefore(']')
	val lineValue4 = deviceProps.substringAfter("ro.product.name]: [")
	Codename = if (lineValue4 == deviceProps) "Unknown" else lineValue4.substringBefore(']')
	val lineValue5 = deviceProps.substringAfter("ro.boot.hardware]: [")
	CPU = if (lineValue5 == deviceProps) "Unknown" else lineValue5.substringBefore(']')
	val lineValue6 = deviceProps.substringAfter("ro.product.cpu.abi]: [")
	CPUArch = if (lineValue6 == deviceProps) "Unknown" else lineValue6.substringBefore(']')
	val lineValue7 = deviceProps.substringAfter("ro.serialno]: [")
	SN = if (lineValue7 == deviceProps) "Unknown" else lineValue7.substringBefore(']')
	val lineValue8 = deviceProps.substringAfter("sys.usb.state]: [")
	GsmOperator = if (lineValue8 == deviceProps) "Unknown" else lineValue8.substringBefore(']')
	val lineValue9 = deviceProps.substringAfter("ro.build.fingerprint]: [")
	Fingerprint = if (lineValue9 == deviceProps) "Unknown" else lineValue9.substringBefore(']')
	var lineValue10 = deviceProps.substringAfter("ro.orangefox.version]: [").substringBefore(']')
	if (lineValue10.isNotBlank()) VersionRelease = lineValue10 else {
		lineValue10 = deviceProps.substringAfter("ro.twrp.version]: [")
		VersionRelease = if (lineValue10 == deviceProps) "Unknown" else lineValue10.substringBefore(']')
	}
	val lineValue11 = deviceProps.substringAfter("ro.build.version.sdk]: [")
	SDK = if (lineValue11 == deviceProps) "Unknown" else lineValue11.substringBefore(']')
	val lineValue12 = deviceProps.substringAfter("ro.build.version.security_patch]: [")
	SecurityPatch = if (lineValue12 == deviceProps) "Unknown" else lineValue12.substringBefore(']')
	val lineValue13 = deviceProps.substringAfter("ro.product.locale]: [")
	Language = if (lineValue13 == deviceProps) "Unknown" else lineValue13.substringBefore(']')
	val lineValue14 = deviceProps.substringAfter("ro.boot.selinux]: [")
	Selinux = if (lineValue14 == deviceProps) "Unknown" else lineValue14.substringBefore(']')
	val lineValue15 = deviceProps.substringAfter("ro.treble.enabled]: [")
	Treble = if (lineValue15 == deviceProps) "Unknown" else lineValue15.substringBefore(']')
	val lineValue16 = deviceProps.substringAfter("ro.boot.secureboot]: [")
	SecureBoot = if (lineValue16 == deviceProps) "Unknown" else {
		if (lineValue16.substringBefore(']') == "1") "true" else "false"
	}
	val lineValue17 = deviceProps.substringAfter("ro.build.host]: [")
	DeviceHost = if (lineValue17 == deviceProps) "Unknown" else lineValue17.substringBefore(']')
	val lineValue18 = deviceProps.substringAfter("ro.allow.mock.location]: [")
	MockLocation = if (lineValue18 == deviceProps) "Unknown" else {
		if (lineValue18.substringBefore(']') == "1") "true" else "false"
	}
	val lineValue19 = deviceProps.substringAfter("ro.build.id]: [")
	Language = if (lineValue19 == deviceProps) "Unknown" else lineValue19.substringBefore(']')
	model.addRow(arrayOf("Manufacturer", Manufacturer))
	model.addRow(arrayOf("Brand", Brand))
	model.addRow(arrayOf("Model", Model))
	model.addRow(arrayOf("Codename", Codename))
	model.addRow(arrayOf("CPU", CPU))
	model.addRow(arrayOf("CPU Architecture", CPUArch))
	model.addRow(arrayOf("Serial Number", SN))
	model.addRow(arrayOf("USB Mode", GsmOperator))
	model.addRow(arrayOf("Fingerprint", Fingerprint))
	model.addRow(arrayOf("Recovery Version", VersionRelease))
	model.addRow(arrayOf("SDK Version", SDK))
	model.addRow(arrayOf("SecurityPatch", SecurityPatch))
	model.addRow(arrayOf("Build ID", Language))
	model.addRow(arrayOf("Selinux", Selinux))
	model.addRow(arrayOf("Project Treble", Treble))
	model.addRow(arrayOf("Secure Boot", SecureBoot))
	model.addRow(arrayOf("Build Hostname", DeviceHost))
	model.addRow(arrayOf("Mock Locations", MockLocation))
}