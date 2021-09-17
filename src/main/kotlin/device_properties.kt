fun getProp() {
	val deviceProps = exec("adb", "shell getprop", output = true)
	val adbProps = mapOf(
		"Manufacturer" to "ro.product.manufacturer",
		"Brand" to "ro.product.brand",
		"Model" to "ro.product.model",
		"Codename" to "ro.product.name",
		"CPU" to "ro.product.board",
		"CPU Architecture" to "ro.product.cpu.abi",
		"Serial Number" to "ro.serialno",
		"Cellular Provider" to "gsm.operator.alpha",
		"Fingerprint" to "ro.build.fingerprint",
		"Android Version" to "ro.build.version.release",
		"SDK Version" to "ro.build.version.sdk",
		"Security Patch" to "ro.build.version.security_patch",
		"Language" to "ro.product.locale",
		"Selinux" to "ro.boot.selinux",
		"Project Treble" to "ro.treble.enabled"
	)
	addAdbProp(deviceProps, adbProps)
}

fun getPropFastboot() {
	val fastbootProps = exec("fastboot", "getvar all", output = true, streamType = "Error")
	val fastCaps = mapOf(
		"/system Capacity (MB):" to "partition-size:system: 0x",
		"/data Capacity (MB):" to "partition-size:userdata: 0x",
		"/boot Capacity (MB):" to "partition-size:boot: 0x",
		"/recovery Capacity (MB):" to "partition-size:recovery: 0x",
		"/cache Capacity (MB):" to "partition-size:cache: 0x",
		"/vendor Capacity (MB):" to "partition-size:vendor: 0x"
	)
	val fastProps1 = mapOf(
		"Unlocked" to "unlocked:",
		"Codename" to "product:",
		"Serial Number" to "serialno:",
		"/system File system:" to "partition-type:system:",
		"/data File system:" to "partition-type:userdata:",
		"/boot File system:" to "partition-type:boot:",
		"/recovery File system:" to "partition-type:recovery:",
		"/cache File system:" to "partition-type:cache:",
		"/vendor File system:" to "partition-type:vendor",
	)
	getFastProp1(fastbootProps, fastProps1)
	getFastPropCaps(fastbootProps, fastCaps)
}

fun getPropRecovery() {
	val deviceProps = exec("adb", "shell getprop", output = true)
	val adbProps = mapOf(
		"Manufacturer" to "ro.product.manufacturer",
		"Brand" to "ro.product.brand",
		"Model" to "ro.product.model",
		"Codename" to "ro.product.name",
		"CPU" to "ro.boot.hardware",
		"CPU Architecture" to "ro.product.cpu.abi",
		"Serial Number" to "ro.serialno",
		"USB Mode" to "sys.usb.state",
		"Fingerprint" to "ro.build.fingerprint",
		"Recovery Version" to "ro.orangefox.version",
		"Recovery Version" to "ro.twrp.version",
		"SDK Version" to "ro.build.version.sdk",
		"Security Patch" to "ro.build.version.security_patch",
		"Build ID" to "ro.build.id",
		"Selinux" to "ro.boot.selinux",
		"Secure Boot" to "ro.boot.secureboot",
		"Project Treble" to "ro.treble.enabled",
		"Build Hostname" to "ro.build.host",
		"Mock Locations" to "ro.allow.mock.location"
	)
	addAdbProp(deviceProps, adbProps)
}

fun addAdbProp(deviceProps: String, properties: Map<String, String>) {
	for (i in properties)
		if (deviceProps.indexOf(i.value) != -1) {
			val property = deviceProps.substringAfter("${i.value}]: [").substringBefore(']')
			device_properties_model.addRow(arrayOf(i.key, (property)))
		}
}

fun getFastProp1(deviceProps: String, properties: Map<String, String>) {
	for (i in properties)
		if (deviceProps.indexOf(i.value) != -1) {
			val property =
				deviceProps.substringAfter("(bootloader) ${i.value}").substringBefore("(bootloader) ").trimMargin()
			device_properties_model.addRow(arrayOf(i.key, (property)))
		}
}

fun getFastPropCaps(deviceProps: String, properties: Map<String, String>) {
	for (i in properties)
		if (deviceProps.indexOf(i.value) != -1) {
			val property =
				deviceProps.substringAfter("(bootloader) ${i.value}").substringBefore("(bootloader) ").trimMargin()
			device_properties_model.addRow(arrayOf(i.key, ((java.lang.Long.parseLong(property, 16) / 1048576).toString().trimMargin())))
		}
}