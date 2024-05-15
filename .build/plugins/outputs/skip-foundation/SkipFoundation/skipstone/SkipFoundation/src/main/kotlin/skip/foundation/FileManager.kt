// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array
import skip.lib.Set


internal fun platformFilePath(for_: URL): java.nio.file.Path {
    val url = for_
    return url.toPath()
}

internal fun platformFilePath(for_: String): java.nio.file.Path {
    val path = for_
    return java.nio.file.Paths.get(path)
}

open class FileManager {

    open val temporaryDirectory: URL
        get() = URL(fileURLWithPath = NSTemporaryDirectory(), isDirectory = true)

    open fun createSymbolicLink(at: URL, withDestinationURL: URL) {
        val url = at
        val destinationURL = withDestinationURL
        java.nio.file.Files.createSymbolicLink(platformFilePath(for_ = url), platformFilePath(for_ = destinationURL))
    }

    open fun createSymbolicLink(atPath: String, withDestinationPath: String) {
        val path = atPath
        val destinationPath = withDestinationPath
        java.nio.file.Files.createSymbolicLink(platformFilePath(for_ = path), platformFilePath(for_ = destinationPath))
    }

    open fun createDirectory(at: URL, withIntermediateDirectories: Boolean, attributes: Dictionary<FileAttributeKey, Any>? = null) {
        val url = at
        val p = platformFilePath(for_ = url)
        if (withIntermediateDirectories == true) {
            java.nio.file.Files.createDirectories(p)
        } else {
            java.nio.file.Files.createDirectory(p)
        }
        if (attributes != null) {
            setAttributes(attributes, ofItemAtPath = p.toString())
        }
    }

    open fun createDirectory(atPath: String, withIntermediateDirectories: Boolean, attributes: Dictionary<FileAttributeKey, Any>? = null) {
        val path = atPath
        if (withIntermediateDirectories == true) {
            java.nio.file.Files.createDirectories(platformFilePath(for_ = path))
        } else {
            java.nio.file.Files.createDirectory(platformFilePath(for_ = path))
        }
        if (attributes != null) {
            setAttributes(attributes, ofItemAtPath = path)
        }
    }

    open fun destinationOfSymbolicLink(atPath: String): String {
        val path = atPath
        return java.nio.file.Files.readSymbolicLink(platformFilePath(for_ = path)).toString()
    }

    open fun attributesOfItem(atPath: String): Dictionary<FileAttributeKey, Any> {
        val path = atPath
        // As a convenience, NSDictionary provides a set of methods (declared as a category on NSDictionary) for quickly and efficiently obtaining attribute information from the returned dictionary: fileGroupOwnerAccountName(), fileModificationDate(), fileOwnerAccountName(), filePosixPermissions(), fileSize(), fileSystemFileNumber(), fileSystemNumber(), and fileType().

        val p = platformFilePath(for_ = path)

        var attrs: Dictionary<FileAttributeKey, Any> = Dictionary<FileAttributeKey, Any>()
        val battrs = java.nio.file.Files.readAttributes(p, java.nio.file.attribute.BasicFileAttributes::class.java)

        val size = battrs.size()
        attrs[FileAttributeKey.size] = size.sref()
        val creationTime = battrs.creationTime()
        attrs[FileAttributeKey.creationDate] = Date(java.util.Date(creationTime.toMillis()))
        val lastModifiedTime = battrs.lastModifiedTime()
        attrs[FileAttributeKey.modificationDate] = Date(java.util.Date(creationTime.toMillis()))
        //let lastAccessTime = battrs.lastAccessTime()

        val isDirectory = battrs.isDirectory()
        val isRegularFile = battrs.isRegularFile()
        val isSymbolicLink = battrs.isSymbolicLink()
        if (isDirectory) {
            attrs[FileAttributeKey.type] = FileAttributeType.typeDirectory
        } else if (isSymbolicLink) {
            attrs[FileAttributeKey.type] = FileAttributeType.typeSymbolicLink
        } else if (isRegularFile) {
            attrs[FileAttributeKey.type] = FileAttributeType.typeRegular
        } else {
            // TODO: typeCharacterSpecial and typeBlockSpecial and typeSocket
            attrs[FileAttributeKey.type] = FileAttributeType.typeUnknown
        }

        val fileKey = battrs.fileKey()
        // let referenceCount = fileKey.referenceCount()
        // attrs[FileAttributeKey.referenceCount] = 1 // TODO: is there a way to find this in Java?

        val isOther = battrs.isOther()

        if (java.nio.file.Files.getFileAttributeView(p, java.nio.file.attribute.PosixFileAttributeView::class.java) != null) {
            val pattrs = java.nio.file.Files.readAttributes(p, java.nio.file.attribute.PosixFileAttributes::class.java)
            val owner = pattrs.owner()
            val ownerName = owner.getName()
            attrs[FileAttributeKey.ownerAccountName] = ownerName.sref()
            // attrs[FileAttributeKey.ownerAccountID] = owner.uid

            val group = pattrs.owner()
            val groupName = group.getName()
            attrs[FileAttributeKey.groupOwnerAccountName] = groupName.sref()
            // attrs[FileAttributeKey.groupOwnerAccountID] = group.gid

            val permissions = pattrs.permissions()
            var perm = 0
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.OWNER_READ)) {
                perm = perm or 256
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.OWNER_WRITE)) {
                perm = perm or 128
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE)) {
                perm = perm or 64
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.GROUP_READ)) {
                perm = perm or 32
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.GROUP_WRITE)) {
                perm = perm or 16
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE)) {
                perm = perm or 8
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.OTHERS_READ)) {
                perm = perm or 4
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE)) {
                perm = perm or 2
            }
            if (permissions.contains(java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE)) {
                perm = perm or 1
            }
            attrs[FileAttributeKey.posixPermissions] = perm
        }

        return attrs.sref()
    }

    open fun setAttributes(attributes: Dictionary<FileAttributeKey, Any>, ofItemAtPath: String) {
        val path = ofItemAtPath
        for ((key, value) in attributes.sref()) {
            when (key) {
                FileAttributeKey.posixPermissions -> {
                    val number = (value as Number).toInt()
                    var permissions = Set<java.nio.file.attribute.PosixFilePermission>()
                    if (((number and 256) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.OWNER_READ)
                    }
                    if (((number and 128) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.OWNER_WRITE)
                    }
                    if (((number and 64) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE)
                    }
                    if (((number and 32) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.GROUP_READ)
                    }
                    if (((number and 16) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.GROUP_WRITE)
                    }
                    if (((number and 8) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE)
                    }
                    if (((number and 4) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.OTHERS_READ)
                    }
                    if (((number and 2) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE)
                    }
                    if (((number and 1) != 0)) {
                        permissions.insert(java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE)
                    }
                    java.nio.file.Files.setPosixFilePermissions(platformFilePath(for_ = path), permissions.toSet())
                }
                FileAttributeKey.modificationDate -> {
                    (value as? Date).sref()?.let { date ->
                        java.nio.file.Files.setLastModifiedTime(platformFilePath(for_ = path), java.nio.file.attribute.FileTime.fromMillis(Long(date.timeIntervalSince1970 * 1000.0)))
                    }
                }
                else -> {
                    // unhandled keys are expected to be ignored by test_setFileAttributes
                    continue
                }
            }
        }
    }

    open fun createFile(atPath: String, contents: Data? = null, attributes: Dictionary<FileAttributeKey, Any>? = null): Boolean {
        val path = atPath
        try {
            java.nio.file.Files.write(platformFilePath(for_ = path), (contents ?: Data(platformValue = kotlin.ByteArray(size = 0))).platformValue)
            if (attributes != null) {
                setAttributes(attributes, ofItemAtPath = path)
            }
            return true
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            return false
        }
    }

    open fun copyItem(atPath: String, toPath: String) {
        val path = atPath
        copy(from = platformFilePath(for_ = path), to = platformFilePath(for_ = toPath), recursive = true)
    }

    open fun copyItem(at: URL, to: URL) {
        val url = at
        copy(from = platformFilePath(for_ = url), to = platformFilePath(for_ = to), recursive = true)
    }

    open fun moveItem(atPath: String, toPath: String) {
        val path = atPath
        java.nio.file.Files.move(platformFilePath(for_ = path), platformFilePath(for_ = toPath))
    }

    open fun moveItem(at: URL, to: URL) {
        val path = at
        java.nio.file.Files.move(path.toPath(), to.toPath())
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun contentsEqual(atPath: String, andPath: String): Boolean {
        val path1 = atPath
        val path2 = andPath
        // TODO: recursively compare folders and files, taking into account special files; see https://github.com/apple/swift-corelibs-foundation/blob/818de4858f3c3f72f75d25fbe94d2388ca653f18/Sources/Foundation/FileManager%2BPOSIX.swift#L997
        return fatalError("contentsEqual is unimplemented in Skip")
    }

    @Deprecated("changeCurrentDirectoryPath is unavailable in Skip: the current directory cannot be changed in the JVM", level = DeprecationLevel.ERROR)
    open fun changeCurrentDirectoryPath(path: String): Boolean {
        return fatalError("FileManager.changeCurrentDirectoryPath unavailable")
    }

    open val currentDirectoryPath: String
        get() = System.getProperty("user.dir")

    private fun checkCancelled() {
        Task.checkCancellation()
    }

    private fun delete(path: java.nio.file.Path, recursive: Boolean) {
        if (!recursive) {
            java.nio.file.Files.delete(path)
        } else {

            // Cannot use java.nio.file.Files.walk for recursive delete because it doesn't list directories post-visit
            //for file in java.nio.file.Files.walk(path) {
            //    java.nio.file.Files.delete(file)
            //}

            java.nio.file.Files.walkFileTree(path, object : java.nio.file.SimpleFileVisitor<java.nio.file.Path>() {
    override fun visitFile(file: java.nio.file.Path, attrs: java.nio.file.attribute.BasicFileAttributes): java.nio.file.FileVisitResult {
        checkCancelled()
        java.nio.file.Files.delete(file)
        return java.nio.file.FileVisitResult.CONTINUE
    }

    override fun postVisitDirectory(dir: java.nio.file.Path, exc: java.io.IOException?): java.nio.file.FileVisitResult {
        checkCancelled()
        java.nio.file.Files.delete(dir)
        return java.nio.file.FileVisitResult.CONTINUE
    }
 })
        }
    }

    private fun copy(from: java.nio.file.Path, to: java.nio.file.Path, recursive: Boolean) {
        val src = from
        val dest = to
        if (!recursive) {
            java.nio.file.Files.copy(src, dest)
        } else {
            java.nio.file.Files.walkFileTree(src, object : java.nio.file.SimpleFileVisitor<java.nio.file.Path>() {
    override fun visitFile(file: java.nio.file.Path, attrs: java.nio.file.attribute.BasicFileAttributes): java.nio.file.FileVisitResult {
        checkCancelled()
        java.nio.file.Files.copy(from, dest.resolve(src.relativize(file)), java.nio.file.StandardCopyOption.REPLACE_EXISTING, java.nio.file.StandardCopyOption.COPY_ATTRIBUTES, java.nio.file.LinkOption.NOFOLLOW_LINKS)
        return java.nio.file.FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: java.nio.file.Path, attrs: java.nio.file.attribute.BasicFileAttributes): java.nio.file.FileVisitResult {
        checkCancelled()
        java.nio.file.Files.createDirectories(dest.resolve(src.relativize(dir)))
        return java.nio.file.FileVisitResult.CONTINUE
    }
 })
        }
    }

    open fun subpathsOfDirectory(atPath: String): Array<String> {
        val path = atPath
        var subpaths: Array<String> = arrayOf()
        val p = platformFilePath(for_ = path)
        for (file in java.nio.file.Files.walk(p)) {
            if (file != p) {
                val relpath = p.relativize(file.normalize())
                subpaths.append(relpath.toString())
            }
        }
        return subpaths.sref()
    }

    open fun subpaths(atPath: String): Array<String>? {
        val path = atPath
        return try { subpathsOfDirectory(atPath = path) } catch (_: Throwable) { null }
    }

    open fun removeItem(atPath: String) {
        val path = atPath
        delete(path = platformFilePath(for_ = path), recursive = true)
    }

    open fun removeItem(at: URL) {
        val url = at
        delete(path = platformFilePath(for_ = url), recursive = true)
    }

    open fun fileExists(atPath: String): Boolean {
        val path = atPath
        return java.nio.file.Files.exists(java.nio.file.Paths.get(path))
    }

    open fun fileExists(atPath: String, isDirectory: InOut<ObjCBool>): Boolean {
        val path = atPath
        val p = platformFilePath(for_ = path)
        if (java.nio.file.Files.isDirectory(p)) {
            isDirectory.value = ObjCBool(true)
            return true
        } else if (java.nio.file.Files.exists(p)) {
            isDirectory.value = ObjCBool(false)
            return true
        } else {
            return false
        }
    }

    open fun isReadableFile(atPath: String): Boolean {
        val path = atPath
        return java.nio.file.Files.isReadable(platformFilePath(for_ = path))
    }

    open fun isExecutableFile(atPath: String): Boolean {
        val path = atPath
        return java.nio.file.Files.isExecutable(platformFilePath(for_ = path))
    }

    open fun isDeletableFile(atPath: String): Boolean {
        val path = atPath
        val p = platformFilePath(for_ = path)
        if (!java.nio.file.Files.isWritable(p)) {
            return false
        }
        // also check whether the parent path is writable
        if (!java.nio.file.Files.isWritable(p.getParent())) {
            return false
        }
        return true
    }

    open fun isWritableFile(atPath: String): Boolean {
        val path = atPath
        return java.nio.file.Files.isWritable(platformFilePath(for_ = path))
    }

    open fun contentsOfDirectory(at: URL, includingPropertiesForKeys: Array<URLResourceKey>?): Array<URL> {
        val url = at
        // https://developer.android.com/reference/kotlin/java/nio/file/Files
        val shallowFiles = java.nio.file.Files.list(platformFilePath(for_ = url)).collect(java.util.stream.Collectors.toList())
        val contents = shallowFiles.map { it -> URL(platformValue = it.toUri().toURL()) }
        return Array(contents)
    }

    open fun contentsOfDirectory(atPath: String): Array<String> {
        val path = atPath
        // https://developer.android.com/reference/kotlin/java/nio/file/Files
        val files = java.nio.file.Files.list(platformFilePath(for_ = path)).collect(java.util.stream.Collectors.toList())
        val contents = files.map { it -> it.toFile().getName() }
        return Array(contents)
    }

    open fun url(for_: FileManager.SearchPathDirectory, in_: FileManager.SearchPathDomainMask, appropriateFor: URL?, create: Boolean): URL {
        val directory = for_
        val domain = in_
        val url = appropriateFor
        val shouldCreate = create
        val ctx = ProcessInfo.processInfo.androidContext.sref()
        when (directory) {
            FileManager.SearchPathDirectory.documentDirectory -> return URL.documentsDirectory.sref()
            FileManager.SearchPathDirectory.cachesDirectory -> return URL.cachesDirectory.sref()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun mountedVolumeURLs(includingResourceValuesForKeys: Array<URLResourceKey>?, options: FileManager.VolumeEnumerationOptions = FileManager.VolumeEnumerationOptions.of()): Array<URL>? {
        val propertyKeys = includingResourceValuesForKeys
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun urls(for_: FileManager.SearchPathDirectory, in_: FileManager.SearchPathDomainMask): Array<URL> {
        val directory = for_
        val domainMask = in_
        return arrayOf()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun getRelationship(outRelationship: Any, ofDirectoryAt: URL, toItemAt: URL) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun getRelationship(outRelationship: Any, of: FileManager.SearchPathDirectory, in_: FileManager.SearchPathDomainMask, toItemAt: URL) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var delegate: Any? = null
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        } /* FileManagerDelegate? */


    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun attributesOfFileSystem(forPath: String): Dictionary<FileAttributeKey, Any> {
        val path = forPath
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun linkItem(atPath: String, toPath: String) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun linkItem(at: URL, to: URL) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun trashItem(at: URL, resultingItemURL: Any?) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun displayName(atPath: String): String {
        val path = atPath
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun componentsToDisplay(forPath: String): Array<String>? {
        val path = forPath
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun enumerator(atPath: String): Any? {
        val path = atPath
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun contents(atPath: String): Data? {
        val path = atPath
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun fileSystemRepresentation(withPath: String): Any {
        val path = withPath
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun string(withFileSystemRepresentation: Any, length: Int): String {
        val str = withFileSystemRepresentation
        val len = length
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun replaceItem(at: URL, withItemAt: URL, backupItemName: String?, options: FileManager.ItemReplacementOptions = FileManager.ItemReplacementOptions.of(), resultingItemURL: Any?) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun setUbiquitous(flag: Boolean, itemAt: URL, destinationURL: URL) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun isUbiquitousItem(at: URL): Boolean {
        val url = at
        return false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun startDownloadingUbiquitousItem(at: URL) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun evictUbiquitousItem(at: URL) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun url(forUbiquityContainerIdentifier: String?): URL? {
        val containerIdentifier = forUbiquityContainerIdentifier
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun url(forPublishingUbiquitousItemAt: URL, expiration: Any?): URL {
        val url = forPublishingUbiquitousItemAt
        val outDate = expiration
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val ubiquityIdentityToken: Any?
        get() = null

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun getFileProviderServicesForItem(at: URL, completionHandler: (Dictionary<AnyHashable, Any>?, Error?) -> Unit) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open suspend fun fileProviderServicesForItem(at: URL): Dictionary<AnyHashable, Any> = Async.run {
        val url = at
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun containerURL(forSecurityApplicationGroupIdentifier: String): URL? {
        val groupIdentifier = forSecurityApplicationGroupIdentifier
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun replaceItemAt(originalItemURL: URL, withItemAt: URL, backupItemName: String? = null, options: FileManager.ItemReplacementOptions = FileManager.ItemReplacementOptions.of()): URL? {
        val newItemURL = withItemAt
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun enumerator(at: URL, includingPropertiesForKeys: Array<URLResourceKey>?, options: FileManager.DirectoryEnumerationOptions = FileManager.DirectoryEnumerationOptions.of(), errorHandler: ((URL, Error) -> Boolean)? = null): Any? {
        val url = at
        val keys = includingPropertiesForKeys
        val mask = options
        val handler = errorHandler
        return null
    }

    class DirectoryEnumerationOptions: OptionSet<FileManager.DirectoryEnumerationOptions, UInt>, MutableStruct {
        override var rawValue: UInt

        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): FileManager.DirectoryEnumerationOptions = DirectoryEnumerationOptions(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: FileManager.DirectoryEnumerationOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as FileManager.DirectoryEnumerationOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = FileManager.DirectoryEnumerationOptions(this as MutableStruct)

        private fun assignfrom(target: FileManager.DirectoryEnumerationOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            var skipsSubdirectoryDescendants = DirectoryEnumerationOptions(rawValue = 1U)
                get() = field.sref({ this.skipsSubdirectoryDescendants = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var skipsPackageDescendants = DirectoryEnumerationOptions(rawValue = 2U)
                get() = field.sref({ this.skipsPackageDescendants = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var skipsHiddenFiles = DirectoryEnumerationOptions(rawValue = 4U)
                get() = field.sref({ this.skipsHiddenFiles = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var includesDirectoriesPostOrder = DirectoryEnumerationOptions(rawValue = 8U)
                get() = field.sref({ this.includesDirectoriesPostOrder = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var producesRelativePathURLs = DirectoryEnumerationOptions(rawValue = 16U)
                get() = field.sref({ this.producesRelativePathURLs = it })
                set(newValue) {
                    field = newValue.sref()
                }

            fun of(vararg options: FileManager.DirectoryEnumerationOptions): FileManager.DirectoryEnumerationOptions {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return DirectoryEnumerationOptions(rawValue = value)
            }
        }
    }

    class ItemReplacementOptions: OptionSet<FileManager.ItemReplacementOptions, UInt>, MutableStruct {
        override var rawValue: UInt

        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): FileManager.ItemReplacementOptions = ItemReplacementOptions(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: FileManager.ItemReplacementOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as FileManager.ItemReplacementOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = FileManager.ItemReplacementOptions(this as MutableStruct)

        private fun assignfrom(target: FileManager.ItemReplacementOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            var usingNewMetadataOnly = ItemReplacementOptions(rawValue = 1U)
                get() = field.sref({ this.usingNewMetadataOnly = it })
                set(newValue) {
                    field = newValue.sref()
                }
            var withoutDeletingBackupItem = ItemReplacementOptions(rawValue = 2U)
                get() = field.sref({ this.withoutDeletingBackupItem = it })
                set(newValue) {
                    field = newValue.sref()
                }

            fun of(vararg options: FileManager.ItemReplacementOptions): FileManager.ItemReplacementOptions {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return ItemReplacementOptions(rawValue = value)
            }
        }
    }

    enum class SearchPathDirectory(override val rawValue: UInt, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<UInt> {
        documentDirectory(UInt(0)),
        cachesDirectory(UInt(1));

        companion object {
        }
    }

    enum class SearchPathDomainMask(override val rawValue: UInt, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<UInt> {
        userDomainMask(UInt(1));
        //case localDomainMask = 2
        //case networkDomainMask = 4
        //case systemDomainMask = 8
        //case allDomainMask = 0x0fff

        companion object {
        }
    }

    class VolumeEnumerationOptions: OptionSet<FileManager.VolumeEnumerationOptions, UInt>, MutableStruct {
        override var rawValue: UInt

        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): FileManager.VolumeEnumerationOptions = VolumeEnumerationOptions(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: FileManager.VolumeEnumerationOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as FileManager.VolumeEnumerationOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = FileManager.VolumeEnumerationOptions(this as MutableStruct)

        private fun assignfrom(target: FileManager.VolumeEnumerationOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            val skipHiddenVolumes = VolumeEnumerationOptions(rawValue = 1U)
            val produceFileReferenceURLs = VolumeEnumerationOptions(rawValue = 2U)

            fun of(vararg options: FileManager.VolumeEnumerationOptions): FileManager.VolumeEnumerationOptions {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return VolumeEnumerationOptions(rawValue = value)
            }
        }
    }

    companion object: CompanionClass() {
        override val default = FileManager()

        override fun SearchPathDirectory(rawValue: UInt): FileManager.SearchPathDirectory? {
            return when (rawValue) {
                UInt(0) -> SearchPathDirectory.documentDirectory
                UInt(1) -> SearchPathDirectory.cachesDirectory
                else -> null
            }
        }

        override fun SearchPathDomainMask(rawValue: UInt): FileManager.SearchPathDomainMask? {
            return when (rawValue) {
                UInt(1) -> SearchPathDomainMask.userDomainMask
                else -> null
            }
        }
    }
    open class CompanionClass {
        open val default
            get() = FileManager.default
        open fun SearchPathDirectory(rawValue: UInt): FileManager.SearchPathDirectory? = FileManager.SearchPathDirectory(rawValue = rawValue)
        open fun SearchPathDomainMask(rawValue: UInt): FileManager.SearchPathDomainMask? = FileManager.SearchPathDomainMask(rawValue = rawValue)
    }
}

class FileAttributeType: RawRepresentable<String> {
    override val rawValue: String
    constructor(rawValue: String) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FileAttributeType) return false
        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, rawValue)
        return result
    }

    companion object {

        val typeDirectory: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeDirectory")
        val typeRegular: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeRegular")
        val typeSymbolicLink: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeSymbolicLink")
        val typeSocket: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeSocket")
        val typeCharacterSpecial: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeCharacterSpecial")
        val typeBlockSpecial: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeBlockSpecial")
        val typeUnknown: FileAttributeType = FileAttributeType(rawValue = "NSFileTypeUnknown")
    }
}

class FileAttributeKey: RawRepresentable<String> {
    override val rawValue: String
    constructor(rawValue: String) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FileAttributeKey) return false
        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, rawValue)
        return result
    }

    companion object {

        val appendOnly: FileAttributeKey = FileAttributeKey(rawValue = "NSFileAppendOnly")
        val creationDate: FileAttributeKey = FileAttributeKey(rawValue = "NSFileCreationDate")
        val deviceIdentifier: FileAttributeKey = FileAttributeKey(rawValue = "NSFileDeviceIdentifier")
        val extensionHidden: FileAttributeKey = FileAttributeKey(rawValue = "NSFileExtensionHidden")
        val groupOwnerAccountID: FileAttributeKey = FileAttributeKey(rawValue = "NSFileGroupOwnerAccountID")
        val groupOwnerAccountName: FileAttributeKey = FileAttributeKey(rawValue = "NSFileGroupOwnerAccountName")
        val hfsCreatorCode: FileAttributeKey = FileAttributeKey(rawValue = "NSFileHFSCreatorCode")
        val hfsTypeCode: FileAttributeKey = FileAttributeKey(rawValue = "NSFileHFSTypeCode")
        val immutable: FileAttributeKey = FileAttributeKey(rawValue = "NSFileImmutable")
        val modificationDate: FileAttributeKey = FileAttributeKey(rawValue = "NSFileModificationDate")
        val ownerAccountID: FileAttributeKey = FileAttributeKey(rawValue = "NSFileOwnerAccountID")
        val ownerAccountName: FileAttributeKey = FileAttributeKey(rawValue = "NSFileOwnerAccountName")
        val posixPermissions: FileAttributeKey = FileAttributeKey(rawValue = "NSFilePosixPermissions")
        val protectionKey: FileAttributeKey = FileAttributeKey(rawValue = "NSFileProtectionKey")
        val referenceCount: FileAttributeKey = FileAttributeKey(rawValue = "NSFileReferenceCount")
        val systemFileNumber: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSystemFileNumber")
        val systemFreeNodes: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSystemFreeNodes")
        val systemFreeSize: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSystemFreeSize")
        val systemNodes: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSystemNodes")
        val systemNumber: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSystemNumber")
        val systemSize: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSystemSize")
        val type: FileAttributeKey = FileAttributeKey(rawValue = "NSFileType")
        val size: FileAttributeKey = FileAttributeKey(rawValue = "NSFileSize")
        val busy: FileAttributeKey = FileAttributeKey(rawValue = "NSFileBusy")
    }
}

fun NSTemporaryDirectory(): String = _NSTemporaryDirectory
private val _NSTemporaryDirectoryBase: String = java.lang.System.getProperty("java.io.tmpdir")
private val _NSTemporaryDirectory: String = if (_NSTemporaryDirectoryBase.hasSuffix("/")) _NSTemporaryDirectoryBase else (_NSTemporaryDirectoryBase + "/") // Android doesn't always end with "/", which is expected by foundation

/// The user's home directory.
fun NSHomeDirectory(): String = _NSHomeDirectory
private val _NSHomeDirectory: String = java.lang.System.getProperty("user.home")

/// The current user name.
fun NSUserName(): String = _NSUserName
private val _NSUserName: String = java.lang.System.getProperty("user.name")

class FileProtectionType: RawRepresentable<String> {
    override val rawValue: String

    constructor(rawValue: String) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FileProtectionType) return false
        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, rawValue)
        return result
    }

    companion object {

        val none = FileProtectionType(rawValue = "none")
        val complete = FileProtectionType(rawValue = "complete")
        val completeUnlessOpen = FileProtectionType(rawValue = "completeUnlessOpen")
        val completeUntilFirstUserAuthentication = FileProtectionType(rawValue = "completeUntilFirstUserAuthentication")
        val completeWhenUserInactive = FileProtectionType(rawValue = "completeWhenUserInactive")
    }
}

internal class UnableToDeleteFileError: Exception, Error {
    internal val path: String

    constructor(path: String): super() {
        this.path = path
    }
}

internal class UnableToCreateDirectory: Exception, Error {
    internal val path: String

    constructor(path: String): super() {
        this.path = path
    }
}

