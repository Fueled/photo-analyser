// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


open class Bundle {

    private val location: LocalizedStringResource.BundleDescription

    constructor(location: LocalizedStringResource.BundleDescription) {
        this.location = location
    }

    constructor(path: String): this(location = LocalizedStringResource.BundleDescription.atURL(URL(fileURLWithPath = path))) {
    }

    constructor(url: URL): this(location = LocalizedStringResource.BundleDescription.atURL(url)) {
    }

    constructor(for_: AnyClass): this(location = LocalizedStringResource.BundleDescription.forClass(for_)) {
    }

    constructor(): this(location = LocalizedStringResource.BundleDescription.forClass(Bundle::class)) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(identifier: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): this(location = LocalizedStringResource.BundleDescription.main) {
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Bundle) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.location == rhs.location
    }

    override fun hashCode(): Int {
        var hasher = Hasher()
        hash(into = InOut<Hasher>({ hasher }, { hasher = it }))
        return hasher.finalize()
    }
    open fun hash(into: InOut<Hasher>) {
        val hasher = into
        hasher.value.combine(location.hashCode())
    }

    open val description: String
        get() = location.description

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun load(): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val isLoaded: Boolean
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun unload(): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun preflight() = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun loadAndReturnError() = Unit

    open val bundleURL: URL
        get() {
            val loc: LocalizedStringResource.BundleDescription = location
            when (loc) {
                is LocalizedStringResource.BundleDescription.AtURLCase -> {
                    val url = loc.associated0
                    return url
                }
                is LocalizedStringResource.BundleDescription.MainCase, is LocalizedStringResource.BundleDescription.ForClassCase -> {
                    return relativeBundleURL(Companion.resourceIndexFileName)!!
                        .deletingLastPathComponent()
                }
            }
        }

    /// Creates a relative path to the given bundle URL
    private fun relativeBundleURL(path: String): URL? {
        val loc: LocalizedStringResource.BundleDescription = location
        when (loc) {
            is LocalizedStringResource.BundleDescription.MainCase -> {
                val appContext = ProcessInfo.processInfo.androidContext.sref()
                val className = appContext.getApplicationInfo().className.sref()
                // className can be null when running in emulator unit tests
                if (className == null) {
                    return null
                }
                // ClassLoader will be something like: dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/~~TsW3puiwg61p2gVvq_TiHQ==/skip.ui.test-6R4Fcu0a4CkedPWcML2mGA==/base.apk"],nativeLibraryDirectories=[/data/app/~~TsW3puiwg61p2gVvq_TiHQ==/skip.ui.test-6R4Fcu0a4CkedPWcML2mGA==/lib/arm64, /system/lib64, /system_ext/lib64]]]
                val appClass = Class.forName(className, true, appContext.getClassLoader() ?: Thread.currentThread().getContextClassLoader())
                return relativeBundleURL(path = path, forClass = appClass)
            }
            is LocalizedStringResource.BundleDescription.AtURLCase -> {
                val url = loc.associated0
                return url.appendingPathComponent(path)
            }
            is LocalizedStringResource.BundleDescription.ForClassCase -> {
                val cls = loc.associated0
                return relativeBundleURL(path = path, forClass = cls.java)
            }
        }
    }

    private fun relativeBundleURL(path: String, forClass: Class<*>): URL? {
        try {
            val rpath = "Resources/" + path
            val resURL = forClass.getResource(rpath)
            return URL(platformValue = resURL)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            // getResource throws when it cannot find the resource, but it doesn't handle directories
            // such as .lproj folders; so manually scan the resources.lst elements, and if any
            // appear to be a directory, then just return that relative URL without validating its existance

            if (path == Companion.resourceIndexFileName) {
                return null // if the resources index itself is not found (which will be the case when the project has no resources), then do not try to load it
            }

            if (this.resourcesIndex.contains(where = { it -> it.hasPrefix(path + "/") })) {
                return resourcesFolderURL?.appendingPathComponent(path, isDirectory = true)
            }
            return null
        }
    }

    open val resourceURL: URL?
        get() = bundleURL // FIXME: this is probably not correct

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val executableURL: URL?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun url(forAuxiliaryExecutable: String): URL? {
        val executableName = forAuxiliaryExecutable
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val privateFrameworksURL: URL?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val sharedFrameworksURL: URL?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val sharedSupportURL: URL?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val builtInPlugInsURL: URL?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val appStoreReceiptURL: URL?
        get() {
            fatalError()
        }

    open val bundlePath: String
        get() = bundleURL.path

    open val resourcePath: String?
        get() {
            return resourceURL?.path
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val executablePath: String?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun path(forAuxiliaryExecutable: String): String? {
        val executableName = forAuxiliaryExecutable
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val privateFrameworksPath: String?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val sharedFrameworksPath: String?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val sharedSupportPath: String?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val builtInPlugInsPath: String?
        get() {
            fatalError()
        }

    open fun url(forResource: String? = null, withExtension: String? = null, subdirectory: String? = null, localization: String? = null): URL? {
        // similar behavior to: https://github.com/apple/swift-corelibs-foundation/blob/69ab3975ea636d1322ad19bbcea38ce78b65b26a/CoreFoundation/PlugIn.subproj/CFBundle_Resources.c#L1114
        var res = forResource ?: ""
        if ((withExtension != null) && !withExtension.isEmpty) {
            // TODO: If `forResource` is nil, we are expected to find the first file in the bundle whose extension matches
            res += "." + withExtension
        } else if (res.isEmpty) {
            return null
        }
        if (localization != null) {
            //let lprojExtension = "lproj" // _CFBundleLprojExtension
            var lprojExtensionWithDot = ".lproj" // _CFBundleLprojExtensionWithDot
            res = localization + lprojExtensionWithDot + "/" + res
        }
        if (subdirectory != null) {
            res = subdirectory + "/" + res
        }

        return relativeBundleURL(path = res)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun urls(forResourcesWithExtension: String?, subdirectory: String? = null, localization: String? = null): Array<URL>? {
        val ext = forResourcesWithExtension
        val subpath = subdirectory
        val localizationName = localization
        fatalError()
    }

    open fun path(forResource: String? = null, ofType: String? = null, inDirectory: String? = null, forLocalization: String? = null): String? {
        return url(forResource = forResource, withExtension = ofType, subdirectory = inDirectory, localization = forLocalization)?.path
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun paths(forResourcesOfType: String?, inDirectory: String? = null, forLocalization: String? = null): Array<String> {
        val ext = forResourcesOfType
        val subpath = inDirectory
        val localizationName = forLocalization
        fatalError()
    }

    /// The URL for the `resources.lst` resources index file that is created by the transpiler when converting resources files.
    private val resourcesIndexURL: URL?
        get() = url(forResource = Companion.resourceIndexFileName)

    /// The path to the base folder of the `Resources/` directory.
    ///
    /// In Robolectric, this will be a simple file system directory URL.
    /// On Android it will be something like `jar:file:/data/app/~~GrNJyKuGMG-gs4i97rlqHg==/skip.ui.test-5w0MhfIK6rNxUpG8yMuXgg==/base.apk!/skip/ui/Resources/`
    private val resourcesFolderURL: URL?
        get() {
            return resourcesIndexURL?.deletingLastPathComponent()
        }

    /// Loads the resources index stored in the `resources.lst` file at the root of the resources folder.
    private var resourcesIndex: Array<String>
        get() {
            if (!resourcesIndexinitialized) {
                resourcesIndexstorage = linvoke l@{ ->
                    val resourceListURL_0 = this.resourcesIndexURL.sref()
                    if (resourceListURL_0 == null) {
                        return@l arrayOf()
                    }
                    val resourceList = Data(contentsOf = resourceListURL_0)
                    val resourceListString_0 = String(data = resourceList, encoding = StringEncoding.utf8)
                    if (resourceListString_0 == null) {
                        return@l arrayOf()
                    }
                    val resourcePaths = resourceListString_0.components(separatedBy = "\n")

                    return@l resourcePaths
                }
                resourcesIndexinitialized = true
            }
            return resourcesIndexstorage.sref({ this.resourcesIndex = it })
        }
        set(newValue) {
            resourcesIndexstorage = newValue.sref()
            resourcesIndexinitialized = true
        }
    private lateinit var resourcesIndexstorage: Array<String>
    private var resourcesIndexinitialized = false

    /// We default to en as the development localization
    open val developmentLocalization: String
        get() = "en"

    /// Identify the Bundle's localizations by the presence of a `LOCNAME.lproj/` folder in index of the root of the resources folder
    open var localizations: Array<String>
        get() {
            if (!localizationsinitialized) {
                localizationsstorage = { ->
                    resourcesIndex
                        .compactMap({ it -> it.components(separatedBy = "/").first })
                        .filter({ it -> it.hasSuffix(".lproj") })
                        .map({ it -> it.dropLast(".lproj".count) })
                }()
                localizationsinitialized = true
            }
            return localizationsstorage.sref({ this.localizations = it })
        }
        set(newValue) {
            localizationsstorage = newValue.sref()
            localizationsinitialized = true
        }
    private lateinit var localizationsstorage: Array<String>
    private var localizationsinitialized = false

    /// The localized strings tables for this bundle
    private var localizedTables: Dictionary<String, Dictionary<String, String>?> = dictionaryOf()
        get() = field.sref({ this.localizedTables = it })
        set(newValue) {
            field = newValue.sref()
        }

    open fun localizedString(forKey: String, value: String?, table: String?): String {
        val key = forKey
        val tableName = table
        return synchronized(this) l@{ ->
            val table = tableName ?: "Localizable"
            val matchtarget_0 = localizedTables[table]
            if (matchtarget_0 != null) {
                val localizedTable = matchtarget_0
                return@l localizedTable?.get(key) ?: value ?: key
            } else {
                val resURL = url(forResource = table, withExtension = "strings")
                val locTable = if (resURL == null) null else try { PropertyListSerialization.propertyList(from = Data(contentsOf = resURL!!), format = null) } catch (_: Throwable) { null }
                localizedTables[key] = locTable.sref()
                return@l locTable?.get(key) ?: value ?: key
            }
        }
    }

    /// The individual loaded bundles by locale
    private var localizedBundles: Dictionary<Locale, Bundle?> = dictionaryOf()
        get() = field.sref({ this.localizedBundles = it })
        set(newValue) {
            field = newValue.sref()
        }

    /// Looks up the Bundle for the given locale and returns it, caching the result in the process.
    open fun localizedBundle(locale: Locale): Bundle {
        return synchronized(this) l@{ ->
            this.localizedBundles[locale]?.let { cached ->
                return@l cached ?: this
            }

            var locBundle: Bundle? = null
            // for each identifier, attempt to load the Localizable.strings to see if it exists
            for (localeid in locale.localeSearchTags.sref()) {
                //print("trying localeid: \(localeid)")
                if (locBundle == null) {
                    this.url(forResource = "Localizable", withExtension = "strings", subdirectory = null, localization = localeid)?.let { locstrURL ->
                        (try { (try { Bundle(url = locstrURL.deletingLastPathComponent()) } catch (_: NullReturnException) { null }) } catch (_: Throwable) { null })?.let { locBundleLocal ->
                            locBundle = locBundleLocal
                        }
                    }
                }
            }

            // cache the result of the lookup (even if it is nil)
            this.localizedBundles[locale] = locBundle

            // fall back to the top-level bundle, if available
            return@l locBundle ?: this
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val preferredLocalizations: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val bundleIdentifier: String?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val infoDictionary: Dictionary<String, Any>?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val localizedInfoDictionary: Dictionary<String, Any>?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun object_(forInfoDictionaryKey: String): Any? {
        val key = forInfoDictionaryKey
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun classNamed(className: String): AnyClass? {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val principalClass: AnyClass?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val executableArchitectures: Array<java.lang.Number>?
        get() {
            fatalError()
        }

    companion object: CompanionClass() {
        override val main = Bundle(location = LocalizedStringResource.BundleDescription.main)

        /// Each package will generate its own `Bundle.module` extension to access the local bundle.
        override val module: Bundle
            get() = _bundleModule
        private val _bundleModule = Bundle(for_ = Bundle::class)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val allBundles: Array<Bundle>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val allFrameworks: Array<Bundle>
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun url(forResource: String?, withExtension: String? = null, subdirectory: String? = null, in_: URL): URL? {
            val name = forResource
            val ext = withExtension
            val subpath = subdirectory
            val bundleURL = in_
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun path(forResource: String?, ofType: String?, inDirectory: String): String? {
            val name = forResource
            val ext = ofType
            val bundlePath = inDirectory
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun paths(forResourcesOfType: String?, inDirectory: String): Array<String> {
            val ext = forResourcesOfType
            val bundlePath = inDirectory
            fatalError()
        }

        private val resourceIndexFileName = "resources.lst"

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun preferredLocalizations(from: Array<String>, forPreferences: Array<String>? = null): Array<String> {
            val localizationsArray = from
            val preferencesArray = forPreferences
            fatalError()
        }
    }
    open class CompanionClass {
        open val main
            get() = Bundle.main
        internal open val module: Bundle
            get() = Bundle.module
    }
}

fun NSLocalizedString(key: String, tableName: String? = null, bundle: Bundle? = Bundle.main, value: String? = null, comment: String): String = (bundle ?: Bundle.main).localizedString(forKey = key, value = value, table = tableName)

