// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


open class ProcessInfo {

    internal constructor() {
    }

    /// The Android context for the process, which should have been set on app launch, and will fall back on using an Android test context.
    open val androidContext: android.content.Context
        get() = androidContextstorage!!
    private val androidContextstorage: android.content.Context?
        get() {
            // androidx.compose.ui.platform.LocalContext.current could be used, but it is @Composable and so can't be called from a static context
            return launchContext ?: testContext
        }

    private val testContext: android.content.Context
        get() {
            // fallback to assuming we are running in a test environment
            // we don't have a compile dependency on android test, so we need to load using reflection
            // androidx.test.core.app.ApplicationProvider.getApplicationContext()
            return Class.forName("androidx.test.core.app.ApplicationProvider")
                .getDeclaredMethod("getApplicationContext")
                .invoke(null) as android.content.Context
        }

    private var launchContext: android.content.Context? = null
        get() = field.sref({ this.launchContext = it })
        set(newValue) {
            field = newValue.sref()
        }

    open val globallyUniqueString: String
        get() = UUID().description

    private val systemProperties: Dictionary<String, String> = Companion.buildSystemProperties()

    open val environment: Dictionary<String, String>
        get() = systemProperties

    open val processIdentifier: Int
        get() {
            try {
                return android.os.Process.myPid()
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
                // seems to happen in Robolectric tests
                // return java.lang.ProcessHandle.current().pid().toInt() // JDK9+, so doesn't compile
                // JMX name is "pid@hostname" (e.g., "57924@zap.local")
                // return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split(separator: "@").first?.toLong() ?? -1
                return -1
            }
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val processName: String
        get() {
            fatalError()
        }

    open val arguments: Array<String>
        get() = arrayOf() // no arguments on Android

    open val hostName: String
        get() {
            // Android 30+: NetworkOnMainThreadException
            return java.net.InetAddress.getLocalHost().hostName
        }

    open val processorCount: Int
        get() = Runtime.getRuntime().availableProcessors()

    open val operatingSystemVersionString: String
        get() = android.os.Build.VERSION.RELEASE

    open val isMacCatalystApp: Boolean
        get() = false

    open val isiOSAppOnMac: Boolean
        get() = false

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val userName: String
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val fullUserName: String
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun disableSuddenTermination() = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun enableSuddenTermination() = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun disableAutomaticTermination(value: String) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun enableAutomaticTermination(value: String) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val automaticTerminationSupportEnabled: Boolean
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val operatingSystemVersion: ProcessInfo.OperatingSystemVersion
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun isOperatingSystemAtLeast(value: ProcessInfo.OperatingSystemVersion): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun operatingSystem(): Int {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun operatingSystemName(): String {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val activeProcessorCount: Int
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val physicalMemory: ULong
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val systemUptime: Double
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun beginActivity(options: ProcessInfo.ActivityOptions, reason: String): Any {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun endActivity(value: Any) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun performActivity(options: ProcessInfo.ActivityOptions, reason: String, using: () -> Unit) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun performExpiringActivity(withReason: String, using: (Boolean) -> Unit) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val thermalState: ProcessInfo.ThermalState
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val isLowPowerModeEnabled: Boolean
        get() {
            fatalError()
        }

    class ActivityOptions: OptionSet<ProcessInfo.ActivityOptions, Int>, RawRepresentable<Int>, MutableStruct {
        override var rawValue: Int

        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): ProcessInfo.ActivityOptions = ActivityOptions(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: ProcessInfo.ActivityOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ProcessInfo.ActivityOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = ProcessInfo.ActivityOptions(this as MutableStruct)

        private fun assignfrom(target: ProcessInfo.ActivityOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            var idleDisplaySleepDisabled = ProcessInfo.ActivityOptions(rawValue = 1)
                get() = field.sref({ this.idleDisplaySleepDisabled = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var idleSystemSleepDisabled = ProcessInfo.ActivityOptions(rawValue = 2)
                get() = field.sref({ this.idleSystemSleepDisabled = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var suddenTerminationDisabled = ProcessInfo.ActivityOptions(rawValue = 4)
                get() = field.sref({ this.suddenTerminationDisabled = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var automaticTerminationDisabled = ProcessInfo.ActivityOptions(rawValue = 8)
                get() = field.sref({ this.automaticTerminationDisabled = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var userInitiated = ProcessInfo.ActivityOptions(rawValue = 16)
                get() = field.sref({ this.userInitiated = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var userInteractive = ProcessInfo.ActivityOptions(rawValue = 32)
                get() = field.sref({ this.userInteractive = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var userInitiatedAllowingIdleSystemSleep = ProcessInfo.ActivityOptions(rawValue = 64)
                get() = field.sref({ this.userInitiatedAllowingIdleSystemSleep = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var background = ProcessInfo.ActivityOptions(rawValue = 128)
                get() = field.sref({ this.background = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var latencyCritical = ProcessInfo.ActivityOptions(rawValue = 256)
                get() = field.sref({ this.latencyCritical = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var animationTrackingEnabled = ProcessInfo.ActivityOptions(rawValue = 512)
                get() = field.sref({ this.animationTrackingEnabled = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var trackingEnabled = ProcessInfo.ActivityOptions(rawValue = 1024)
                get() = field.sref({ this.trackingEnabled = it })
                set(newValue) {
                    field = newValue.sref()
                }

            fun of(vararg options: ProcessInfo.ActivityOptions): ProcessInfo.ActivityOptions {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return ActivityOptions(rawValue = value)
            }
        }
    }

    class OperatingSystemVersion: MutableStruct {
        var majorVersion: Int
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var minorVersion: Int
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var patchVersion: Int
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(majorVersion: Int = 0, minorVersion: Int = 0, patchVersion: Int = 0) {
            this.majorVersion = majorVersion
            this.minorVersion = minorVersion
            this.patchVersion = patchVersion
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ProcessInfo.OperatingSystemVersion
            this.majorVersion = copy.majorVersion
            this.minorVersion = copy.minorVersion
            this.patchVersion = copy.patchVersion
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = ProcessInfo.OperatingSystemVersion(this as MutableStruct)

        companion object {
        }
    }

    enum class ThermalState(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        nominal(0),
        fair(1),
        serious(2),
        critical(3);

        companion object {
        }
    }

    companion object: CompanionClass() {
        /// The global `processInfo` must be set manually at app launch with `skip.foundation.ProcessInfo.launch(context)`
        /// Otherwise error: `skip.lib.ErrorException: kotlin.UninitializedPropertyAccessException: lateinit property processInfo has not been initialized`
        override var processInfo: ProcessInfo = ProcessInfo()

        /// Called when an app is launched to store the global context from the `android.app.Application` subclass.
        override fun launch(context: android.content.Context) {
            ProcessInfo.processInfo.launchContext = context
        }

        private fun buildSystemProperties(): Dictionary<String, String> {
            var dict: Dictionary<String, String> = dictionaryOf()
            // The system properties contains the System environment (which, on Android, doesn't contain much of interest),
            for ((key, value) in System.getenv()) {
                dict[key] = value
            }

            // as well as the Java System.getProperties()
            // there are only a few system properties on the Android emulator: java.io.tmpdir, user.home, and http.agent "Dalvik/2.1.0 (Linux; U; Android 13; sdk_gphone64_arm64 Build/ TE1A.220922.021)"
            for ((key, value) in System.getProperties()) {
                dict[key.toString()] = value.toString()
            }

            // there are more system properties than are listed in the getProperties() keys, so also fetch sepcific individual known property keys
            for (key in arrayOf(
                "os.version",
                "java.vendor",
                "java.version",
                "user.home",
                "user.name",
                "file.separator",
                "line.separator",
                "java.class.path",
                "java.library.path",
                "java.class.version",
                "java.vm.name",
                "java.vm.version",
                "java.vm.vendor",
                "java.ext.dirs",
                "java.io.tmpdir",
                "java.specification.version",
                "java.specification.vendor",
                "java.specification.name",
                "java.home",
                "user.dir"
            )) {
                dict[key] = System.getProperty(key)
            }

            // and finally add in some android build constants so clients have a Foundation-compatible way of testing for the Android build number, ec.

            dict["android.os.Build.BOARD"] = android.os.Build.BOARD // The name of the underlying board, like "goldfish".
            dict["android.os.Build.BOOTLOADER"] = android.os.Build.BOOTLOADER // The system bootloader version number.
            dict["android.os.Build.BRAND"] = android.os.Build.BRAND // The consumer-visible brand with which the product/hardware will be associated, if any.
            dict["android.os.Build.DEVICE"] = android.os.Build.DEVICE // The name of the industrial design.
            dict["android.os.Build.DISPLAY"] = android.os.Build.DISPLAY // A build ID string meant for displaying to the user
            dict["android.os.Build.FINGERPRINT"] = android.os.Build.FINGERPRINT // A string that uniquely identifies this build.
            dict["android.os.Build.HARDWARE"] = android.os.Build.HARDWARE // The name of the hardware (from the kernel command line or /proc).
            dict["android.os.Build.HOST"] = android.os.Build.HOST // A string that uniquely identifies this build.
            dict["android.os.Build.ID"] = android.os.Build.ID // Either a changelist number, or a label like "M4-rc20".
            dict["android.os.Build.MANUFACTURER"] = android.os.Build.MANUFACTURER // The manufacturer of the product/hardware.
            dict["android.os.Build.MODEL"] = android.os.Build.MODEL // The end-user-visible name for the end product.
            //dict["android.os.Build.ODM_SKU"] = android.os.Build.ODM_SKU // The SKU of the device as set by the original design manufacturer (ODM). // API 31: java.lang.NoSuchFieldError: ODM_SKU
            dict["android.os.Build.PRODUCT"] = android.os.Build.PRODUCT // The name of the overall product.
            //dict["android.os.Build.SKU"] = android.os.Build.SKU // The SKU of the hardware (from the kernel command line). // API 31: java.lang.NoSuchFieldError: SKU
            //dict["android.os.Build.SOC_MANUFACTURER"] = android.os.Build.SOC_MANUFACTURER // The manufacturer of the device's primary system-on-chip. // API 31: java.lang.NoSuchFieldError: SOC_MANUFACTURER
            //dict["android.os.Build.SOC_MODEL"] = android.os.Build.SOC_MODEL // The model name of the device's primary system-on-chip. // API 31
            dict["android.os.Build.TAGS"] = android.os.Build.TAGS // Comma-separated tags describing the build, like "unsigned,debug".
            dict["android.os.Build.TYPE"] = android.os.Build.TYPE // The type of build, like "user" or "eng".
            dict["android.os.Build.USER"] = android.os.Build.USER // The user


            dict["android.os.Build.TIME"] = android.os.Build.TIME.toString() //  The time at which the build was produced, given in milliseconds since the UNIX epoch.

            //        dict["android.os.Build.SUPPORTED_32_BIT_ABIS"] = android.os.Build.SUPPORTED_32_BIT_ABIS.joinToString(",") // An ordered list of 32 bit ABIs supported by this device.
            //        dict["android.os.Build.SUPPORTED_64_BIT_ABIS"] = android.os.Build.SUPPORTED_64_BIT_ABIS.joinToString(",") // An ordered list of 64 bit ABIs supported by this device.
            //        dict["android.os.Build.SUPPORTED_ABIS"] = android.os.Build.SUPPORTED_ABIS.joinToString(",") // An ordered list of ABIs supported by this device.

            dict["android.os.Build.VERSION.BASE_OS"] = android.os.Build.VERSION.BASE_OS // The base OS build the product is based on.
            dict["android.os.Build.VERSION.CODENAME"] = android.os.Build.VERSION.CODENAME // The current development codename, or the string "REL" if this is a release build.
            dict["android.os.Build.VERSION.INCREMENTAL"] = android.os.Build.VERSION.INCREMENTAL // The internal value used by the underlying source control to represent this build. E.g., a perforce changelist number or a git hash.
            dict["android.os.Build.VERSION.PREVIEW_SDK_INT"] = android.os.Build.VERSION.PREVIEW_SDK_INT.description // The developer preview revision of a prerelease SDK. This value will always be 0 on production platform builds/devices.
            dict["android.os.Build.VERSION.RELEASE"] = android.os.Build.VERSION.RELEASE // The user-visible version string. E.g., "1.0" or "3.4b5" or "bananas". This field is an opaque string. Do not assume that its value has any particular structure or that values of RELEASE from different releases can be somehow ordered.
            dict["android.os.Build.VERSION.SDK_INT"] = android.os.Build.VERSION.SDK_INT.description // The SDK version of the software currently running on this hardware device. This value never changes while a device is booted, but it may increase when the hardware manufacturer provides an OTA update.
            dict["android.os.Build.VERSION.SECURITY_PATCH"] = android.os.Build.VERSION.SECURITY_PATCH // The user-visible security patch level. This value represents the date when the device most recently applied a security patch.

            return dict.sref()
        }

        override fun ThermalState(rawValue: Int): ProcessInfo.ThermalState? {
            return when (rawValue) {
                0 -> ThermalState.nominal
                1 -> ThermalState.fair
                2 -> ThermalState.serious
                3 -> ThermalState.critical
                else -> null
            }
        }
    }
    open class CompanionClass {
        open var processInfo: ProcessInfo
            get() = ProcessInfo.processInfo
            set(newValue) {
                ProcessInfo.processInfo = newValue
            }
        open fun launch(context: android.content.Context) = ProcessInfo.launch(context = context)
        open fun ThermalState(rawValue: Int): ProcessInfo.ThermalState? = ProcessInfo.ThermalState(rawValue = rawValue)
    }
}

