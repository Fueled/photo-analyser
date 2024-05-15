// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array
import skip.lib.Set


typealias NSURL = URL

class URL: Codable, KotlinConverting<java.net.URL>, MutableStruct {
    // TODO: Switch away from java.net.URL, which can only be constructed with a limited set of supported protocols.
    // When we switch, make sure to update the networking code in URLSession to re-map ws/wss websocket URLs to http
    // rather than doing that in our string constructor
    internal var platformValue: java.net.URL
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    private var isDirectoryFlag: Boolean?

    var baseURL: URL?

    constructor(platformValue: java.net.URL, isDirectory: Boolean? = null, baseURL: URL? = null) {
        this.platformValue = platformValue
        this.isDirectoryFlag = isDirectory
        this.baseURL = baseURL.sref()
    }

    constructor(url: URL) {
        this.platformValue = url.platformValue
        this.isDirectoryFlag = url.isDirectoryFlag
        this.baseURL = url.baseURL.sref()
    }

    constructor(string: String, relativeTo: URL? = null) {
        val baseURL = relativeTo
        // Java doesn't support ws/wss URLs
        var sanitizedString = string
        if (sanitizedString.hasPrefix("ws://")) {
            sanitizedString = "http" + String(sanitizedString.dropFirst("ws".count))
        } else if (sanitizedString.hasPrefix("wss://")) {
            sanitizedString = "https" + String(sanitizedString.dropFirst("wss".count))
        }

        try {
            val url = java.net.URL(relativeTo?.platformValue, sanitizedString) // throws on malformed
            // use the same logic as the constructor so that `URL(fileURLWithPath: "/tmp/") == URL(string: "file:///tmp/")`
            val isDirectory = url.protocol == "file" && string.hasSuffix("/")
            this.platformValue = url
            this.isDirectoryFlag = isDirectory
            this.baseURL = baseURL.sref()
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            throw NullReturnException()
        }
    }

    constructor(fileURLWithPath: String, isDirectory: Boolean? = null, relativeTo: URL? = null) {
        val path = fileURLWithPath
        val base = relativeTo
        this.platformValue = java.net.URL("file://" + path) // TODO: escaping
        this.baseURL = base.sref() // TODO: base resolution
        this.isDirectoryFlag = isDirectory ?: path.hasSuffix("/") // TODO: should we hit the file system like NSURL does?
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal constructor(fileURLWithFileSystemRepresentation: Any, isDirectory: Boolean, relativeTo: URL? = null, unusedp: Nothing? = null) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(fileReferenceLiteralResourceName: String) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal constructor(resolvingBookmarkData: Data, options: Any? = null, relativeTo: URL? = null, bookmarkDataIsStale: InOut<Boolean>) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal constructor(resolvingAliasFileAt: URL, options: Any? = null) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal constructor(resource: URLResource) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal constructor(parseInput: Any, strategy: Any, unusedp: Nothing? = null) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(dataRepresentation: Data, relativeTo: URL?, isAbsolute: Boolean) {
        this.platformValue = java.net.URL("")
        this.baseURL = null
        this.isDirectoryFlag = false
    }

    constructor(from: Decoder) {
        val decoder = from
        val container = decoder.singleValueContainer()
        val assignfrom = URL(string = container.decode(String::class))
        this.platformValue = assignfrom.platformValue
        this.isDirectoryFlag = assignfrom.isDirectoryFlag
        this.baseURL = assignfrom.baseURL
    }

    override fun encode(to: Encoder) {
        val encoder = to
        val container = encoder.singleValueContainer()
        container.encode(absoluteString)
    }

    val description: String
        get() = platformValue.description

    /// Converts this URL to a `java.nio.file.Path`.
    fun toPath(): java.nio.file.Path = java.nio.file.Paths.get(platformValue.toURI())

    val host: String?
        get() = platformValue.host

    fun host(percentEncoded: Boolean = true): String? {
        val host_0 = this.host
        if (host_0 == null) {
            return null
        }
        if (percentEncoded) {
            return host_0.addingPercentEncoding(withAllowedCharacters = CharacterSet.urlPathAllowed)
        } else {
            return host_0
        }
    }

    val hasDirectoryPath: Boolean
        get() = this.isDirectoryFlag == true

    val path: String
        get() = platformValue.path

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val port: Int?
        get() {
            return fatalError("TODO: implement port")
        }

    val scheme: String?
        get() = platformValue.protocol

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val query: String?
        get() {
            return fatalError("TODO: implement query")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val user: String?
        get() {
            return fatalError("TODO: implement user")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val password: String?
        get() {
            return fatalError("TODO: implement password")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val fragment: String?
        get() {
            return fatalError("TODO: implement fragment")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val dataRepresentation: Data
        get() {
            fatalError()
        }

    val standardized: URL
        get() = URL(platformValue = toPath().normalize().toUri().toURL())

    val absoluteString: String
        get() = platformValue.toExternalForm()

    val lastPathComponent: String
        get() = pathComponents.lastOrNull() ?: ""

    val pathExtension: String
        get() {
            val parts = Array((lastPathComponent ?: "").split(separator = '.'))
            if (parts.count >= 2) {
                return parts.last!!
            } else {
                return ""
            }
        }

    val isFileURL: Boolean
        get() = platformValue.protocol == "file"

    val pathComponents: Array<String>
        get() {
            val path: String = platformValue.path
            return Array(path.split(separator = '/')).filter { it -> !it.isEmpty }
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val relativePath: String
        get() {
            return fatalError("TODO: implement relativePath")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val relativeString: String
        get() {
            return fatalError("TODO: implement relativeString")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val standardizedFileURL: URL
        get() {
            return fatalError("TODO: implement standardizedFileURL")
        }

    fun standardize() {
        willmutate()
        try {
            assignfrom(standardized)
        } finally {
            didmutate()
        }
    }

    val absoluteURL: URL
        get() = this

    fun appendingPathComponent(pathComponent: String): URL {
        var url = this.platformValue.toExternalForm()
        if (!url.hasSuffix("/")) {
            url = url + "/"
        }
        url = url + pathComponent
        return URL(platformValue = java.net.URL(url))
    }

    fun appendingPathComponent(pathComponent: String, isDirectory: Boolean): URL {
        var url = this.platformValue.toExternalForm()
        if (!url.hasSuffix("/")) {
            url = url + "/"
        }
        url = url + pathComponent
        return URL(platformValue = java.net.URL(url), isDirectory = isDirectory)
    }

    fun appendPathComponent(pathComponent: String) {
        willmutate()
        try {
            assignfrom(appendingPathComponent(pathComponent))
        } finally {
            didmutate()
        }
    }

    fun appendPathComponent(pathComponent: String, isDirectory: Boolean) {
        willmutate()
        try {
            assignfrom(appendingPathComponent(pathComponent, isDirectory = isDirectory))
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun appendingPathComponent(pathComponent: String, conformingTo: Any): URL {
        val type = conformingTo
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun appendPathComponent(pathComponent: String, conformingTo: Any) {
        val type = conformingTo
        willmutate()
        try {
            fatalError()
        } finally {
            didmutate()
        }
    }

    fun appendingPathExtension(pathExtension: String): URL {
        var url = this.platformValue.toExternalForm()
        url = url + "." + pathExtension
        return URL(platformValue = java.net.URL(url))
    }

    fun appendPathExtension(pathExtension: String) {
        willmutate()
        try {
            assignfrom(appendingPathExtension(pathExtension))
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun appendingPathExtension(for_: Any, unusedp: Nothing? = null): URL {
        val type = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun appendPathExtension(for_: Any, unusedp: Nothing? = null) {
        val type = for_
        willmutate()
        try {
            fatalError()
        } finally {
            didmutate()
        }
    }

    fun deletingLastPathComponent(): URL {
        var url = this.platformValue.toExternalForm()
        while (url.hasSuffix("/") && !url.isEmpty) {
            url = url.dropLast(1)
        }
        while (!url.hasSuffix("/") && !url.isEmpty) {
            url = url.dropLast(1)
        }
        return URL(platformValue = java.net.URL(url))
    }

    fun deleteLastPathComponent() {
        willmutate()
        try {
            assignfrom(deletingLastPathComponent())
        } finally {
            didmutate()
        }
    }

    fun deletingPathExtension(): URL {
        val ext = pathExtension
        var url = this.platformValue.toExternalForm()
        while (url.hasSuffix("/")) {
            url = url.dropLast(1)
        }
        if (url.hasSuffix("." + ext)) {
            url = url.dropLast(ext.count + 1)
        }
        return URL(platformValue = java.net.URL(url))
    }

    fun deletePathExtension() {
        willmutate()
        try {
            assignfrom(deletingPathExtension())
        } finally {
            didmutate()
        }
    }

    fun resolvingSymlinksInPath(): URL {
        if (isFileURL == false) {
            return this.sref()
        }
        val originalPath = toPath()
        //if !java.nio.file.Files.isSymbolicLink(originalPath) {
        //    return self // not a link
        //} else {
        //    let normalized = java.nio.file.Files.readSymbolicLink(originalPath).normalize()
        //    return URL(platformValue: normalized.toUri().toURL())
        //}
        try {
            return URL(platformValue = originalPath.toRealPath().toUri().toURL())
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            // this will fail if the file does not exist, but Foundation expects it to return the path itself
            return this.sref()
        }
    }

    fun resolveSymlinksInPath() {
        willmutate()
        try {
            assignfrom(resolvingSymlinksInPath())
        } finally {
            didmutate()
        }
    }

    fun checkResourceIsReachable(): Boolean {
        if (!isFileURL) {
            // “This method is currently applicable only to URLs for file system resources. For other URL types, `false` is returned.”
            return false
        }
        // check whether the resource can be reached by opening and closing a connection
        platformValue.openConnection().getInputStream().close()
        return true
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun resourceValues(forKeys: Set<URLResourceKey>): URLResourceValues {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun setResourceValues(values: URLResourceValues) {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun removeCachedResourceValue(forKey: URLResourceKey) {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun setTemporaryResourceValue(value: Any, forKey: URLResourceKey) {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun removeAllCachedResourceValues() {
        willmutate()
        try {
            fatalError("TODO: implement removeAllCachedResourceValues")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun bookmarkData(options: Any, includingResourceValuesForKeys: Set<URLResourceKey>?, relativeTo: URL?): Data {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val resourceBytes: Any
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val lines: Any
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun checkPromisedItemIsReachable(): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun promisedItemResourceValues(forKeys: Set<URLResourceKey>): URLResourceValues {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun startAccessingSecurityScopedResource(): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun stopAccessingSecurityScopedResource() {
        fatalError()
    }

    override fun kotlin(nocopy: Boolean): java.net.URL = platformValue.sref()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as URL
        this.platformValue = copy.platformValue
        this.isDirectoryFlag = copy.isDirectoryFlag
        this.baseURL = copy.baseURL
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = URL(this as MutableStruct)

    private fun assignfrom(target: URL) {
        this.platformValue = target.platformValue
        this.isDirectoryFlag = target.isDirectoryFlag
        this.baseURL = target.baseURL
    }

    override fun toString(): String = description

    override fun equals(other: Any?): Boolean {
        if (other !is URL) return false
        return platformValue == other.platformValue && isDirectoryFlag == other.isDirectoryFlag && baseURL == other.baseURL
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, platformValue)
        result = Hasher.combine(result, isDirectoryFlag)
        result = Hasher.combine(result, baseURL)
        return result
    }

    companion object: DecodableCompanion<URL> {

        fun currentDirectory(): URL = URL(fileURLWithPath = System.getProperty("user.dir"), isDirectory = true)

        val homeDirectory: URL
            get() = URL(fileURLWithPath = System.getProperty("user.home"), isDirectory = true)

        val temporaryDirectory: URL
            get() = URL(fileURLWithPath = NSTemporaryDirectory(), isDirectory = true)

        val cachesDirectory: URL
            get() = URL(platformValue = ProcessInfo.processInfo.androidContext.getCacheDir().toURL(), isDirectory = true)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val applicationDirectory: URL
            get() {
                return fatalError("applicationDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val libraryDirectory: URL
            get() {
                return fatalError("libraryDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val userDirectory: URL
            get() {
                return fatalError("desktopDirectory unimplemented in Skip")
            }

        val documentsDirectory: URL
            get() = URL(platformValue = ProcessInfo.processInfo.androidContext.getFilesDir().toURL(), isDirectory = true)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val desktopDirectory: URL
            get() {
                return fatalError("desktopDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val applicationSupportDirectory: URL
            get() {
                return fatalError("applicationSupportDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val downloadsDirectory: URL
            get() {
                return fatalError("downloadsDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val moviesDirectory: URL
            get() {
                return fatalError("moviesDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val musicDirectory: URL
            get() {
                return fatalError("musicDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val picturesDirectory: URL
            get() {
                return fatalError("picturesDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val sharedPublicDirectory: URL
            get() {
                return fatalError("sharedPublicDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val trashDirectory: URL
            get() {
                return fatalError("trashDirectory unimplemented in Skip")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun bookmarkData(withContentsOf: URL): Data {
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun writeBookmarkData(data: Data, to: URL) {
            fatalError()
        }

        override fun init(from: Decoder): URL = URL(from = from)
    }
}

class URLResource {
    val bundle: Bundle
    val name: String
    val subdirectory: String?
    val locale: Locale

    constructor(name: String, subdirectory: String? = null, locale: Locale = Locale.current, bundle: Bundle = Bundle.main) {
        this.bundle = bundle
        this.name = name
        this.subdirectory = subdirectory
        this.locale = locale
    }

    override fun equals(other: Any?): Boolean {
        if (other !is URLResource) return false
        return bundle == other.bundle && name == other.name && subdirectory == other.subdirectory && locale == other.locale
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, bundle)
        result = Hasher.combine(result, name)
        result = Hasher.combine(result, subdirectory)
        result = Hasher.combine(result, locale)
        return result
    }

    companion object {
    }
}

class URLResourceKey: RawRepresentable<String> {
    override val rawValue: String

    constructor(rawValue: String) {
        this.rawValue = rawValue
    }

    constructor(rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is URLResourceKey) return false
        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, rawValue)
        return result
    }

    companion object {
    }
}

class URLResourceValues: MutableStruct {
    var allValues: Dictionary<URLResourceKey, Any>
        get() = field.sref({ this.allValues = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(allValues: Dictionary<URLResourceKey, Any>) {
        this.allValues = allValues
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = URLResourceValues(allValues)

    companion object {
    }
}

