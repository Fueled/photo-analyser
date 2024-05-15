// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


class StringEncoding: RawRepresentable<java.nio.charset.Charset> {

    override val rawValue: java.nio.charset.Charset

    constructor(rawValue: java.nio.charset.Charset, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.rawValue = rawValue.sref()
    }

    constructor(rawValue: java.nio.charset.Charset) {
        this.rawValue = rawValue.sref()
    }

    val description: String
        get() = rawValue.description

    override fun equals(other: Any?): Boolean {
        if (other !is StringEncoding) return false
        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, rawValue)
        return result
    }

    companion object {
        val utf8 = StringEncoding(rawValue = Charsets.UTF_8)
        val utf16 = StringEncoding(rawValue = Charsets.UTF_16)
        val utf16LittleEndian = StringEncoding(rawValue = Charsets.UTF_16LE)
        val utf16BigEndian = StringEncoding(rawValue = Charsets.UTF_16BE)
        val utf32 = StringEncoding(rawValue = Charsets.UTF_32)
        val utf32LittleEndian = StringEncoding(rawValue = Charsets.UTF_32LE)
        val utf32BigEndian = StringEncoding(rawValue = Charsets.UTF_32BE)
    }
}

