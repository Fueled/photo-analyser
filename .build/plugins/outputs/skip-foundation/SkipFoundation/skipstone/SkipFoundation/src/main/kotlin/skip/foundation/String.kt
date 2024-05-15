// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


import net.thauvin.erik.urlencoder.UrlEncoderUtil

typealias NSString = kotlin.String
fun NSString(string: String): kotlin.String = string

fun strlen(string: String): Int = string.count

fun strncmp(str1: String, str2: String): Int = if (str1.toLowerCase() == str2.toLowerCase()) 0 else 1

val String.capitalized: String
    get() {
        return split(separator = ' ', omittingEmptySubsequences = false)
            .joinToString(separator = " ") { it ->
                it.replaceFirstChar { it -> it.titlecase() }
            }
    }

val String.deletingLastPathComponent: String
    get() {
        val lastSeparatorIndex = lastIndexOf("/")
        if (lastSeparatorIndex == -1 || (lastSeparatorIndex == 0 && this.length == 1)) {
            return this
        }
        val newPath = substring(0, lastSeparatorIndex)
        val newLastSeparatorIndex = newPath.lastIndexOf("/")
        if (newLastSeparatorIndex == -1) {
            return newPath
        } else {
            return newPath.substring(0, newLastSeparatorIndex + 1)
        }
    }

fun String.replacingOccurrences(of: String, with: String): String {
    val search = of
    val replacement = with
    return replace(search, replacement)
}

fun String.components(separatedBy: String): Array<String> {
    val separator = separatedBy
    return Array(split(separator, ignoreCase = false))
}

fun String.trimmingCharacters(in_: CharacterSet): String {
    val set = in_
    return trim { it -> set.platformValue.contains(UInt(it.code)) }
}

fun String.addingPercentEncoding(withAllowedCharacters: CharacterSet): String? {
    val allowedCharacters = withAllowedCharacters
    return UrlEncoderUtil.encode(this, allowedCharacters.platformValue, spaceToPlus = true)
}

val String.removingPercentEncoding: String?
    get() = UrlEncoderUtil.decode(this, plusToSpace = true)

val String.utf8Data: Data
    get() = data(using = StringEncoding.utf8) ?: Data()

fun String.data(using: StringEncoding, allowLossyConversion: Boolean = true): Data? {
    if (using == StringEncoding.utf16) {
        return Data(this.utf16) // Darwin is little-endian while Java is big-endian
    } else if (using == StringEncoding.utf32) {
        return Data(this.utf32) // Darwin is little-endian while Java is big-endian
    } else {
        val bytes = toByteArray(using.rawValue)
        return Data(platformValue = bytes)
    }
}

val String.utf8: Array<UByte>
    get() {
        // TODO: there should be a faster way to convert a string to a UInt8 array
        return Array(toByteArray(StringEncoding.utf8.rawValue).toUByteArray())
    }

val String.utf16: Array<UByte>
    get() {
        // Darwin is little-endian while Java is big-endian
        // encoding difference with UTF16: https://github.com/google/j2objc/issues/403
        // so we manually use utf16LittleEndian (no BOM) then add back in the byte-order mark (the first two bytes)
        return arrayOf(UByte(0xFF), UByte(0xFE)) + Array(toByteArray(StringEncoding.utf16LittleEndian.rawValue).toUByteArray())
    }

val String.utf32: Array<UByte>
    get() {
        // manually use utf32LittleEndian (no BOM) then add back in the byte-order mark (the first two bytes)
        return arrayOf(UByte(0xFF), UByte(0xFE), UByte(0x00), UByte(0x00)) + Array(toByteArray(StringEncoding.utf32LittleEndian.rawValue).toUByteArray())
    }

val String.unicodeScalars: Array<UByte>
    get() = Array(toByteArray(StringEncoding.utf8.rawValue).toUByteArray())

fun String.write(to: URL, atomically: Boolean, encoding: StringEncoding) {
    val url = to
    val useAuxiliaryFile = atomically
    val enc = encoding
    var opts: Array<java.nio.file.StandardOpenOption> = arrayOf()
    opts.append(java.nio.file.StandardOpenOption.CREATE)
    opts.append(java.nio.file.StandardOpenOption.WRITE)
    if (useAuxiliaryFile) {
        opts.append(java.nio.file.StandardOpenOption.DSYNC)
        opts.append(java.nio.file.StandardOpenOption.SYNC)
    }
    java.nio.file.Files.write(platformFilePath(for_ = url), this.data(using = enc)?.platformValue, *(opts.toList().toTypedArray()))
}

fun String.write(toFile: String, atomically: Boolean, encoding: StringEncoding) {
    val path = toFile
    val useAuxiliaryFile = atomically
    val enc = encoding
    var opts: Array<java.nio.file.StandardOpenOption> = arrayOf()
    opts.append(java.nio.file.StandardOpenOption.CREATE)
    opts.append(java.nio.file.StandardOpenOption.WRITE)
    if (useAuxiliaryFile) {
        opts.append(java.nio.file.StandardOpenOption.DSYNC)
        opts.append(java.nio.file.StandardOpenOption.SYNC)
    }
    java.nio.file.Files.write(platformFilePath(for_ = path), this.data(using = enc)?.platformValue, *(opts.toList().toTypedArray()))
}

fun String(data: Data, encoding: StringEncoding): String? = (java.lang.String(data.platformValue, encoding.rawValue) as kotlin.String?).sref()

fun String(bytes: Array<UByte>, encoding: StringEncoding): String? {
    val byteArray = ByteArray(size = bytes.count) l@{ it -> return@l bytes[it].toByte() }
    return byteArray.toString(encoding.rawValue)
}

fun String(contentsOf: URL): String = contentsOf.platformValue.readText()

