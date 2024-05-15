// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array

// This code is adapted from https://github.com/apple/swift-corelibs-foundation/blob/main/Tests/Foundation/Tests which has the following license:

//===----------------------------------------------------------------------===//
//
// This source file is part of the Swift.org open source project
//
// Copyright (c) 2014 - 2021 Apple Inc. and the Swift project authors
// Licensed under Apache License v2.0 with Runtime Library Exception
//
// See https://swift.org/LICENSE.txt for license information
// See https://swift.org/CONTRIBUTORS.txt for the list of Swift project authors
//
//===----------------------------------------------------------------------===//


open class JSONSerialization {
    class ReadingOptions: OptionSet<JSONSerialization.ReadingOptions, UInt>, MutableStruct {
        override var rawValue: UInt
        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): JSONSerialization.ReadingOptions = ReadingOptions(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: JSONSerialization.ReadingOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONSerialization.ReadingOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = JSONSerialization.ReadingOptions(this as MutableStruct)

        private fun assignfrom(target: JSONSerialization.ReadingOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            val mutableContainers = ReadingOptions(rawValue = 1U shl 0)
            val mutableLeaves = ReadingOptions(rawValue = 1U shl 1)
            val fragmentsAllowed = ReadingOptions(rawValue = 1U shl 2)

            fun of(vararg options: JSONSerialization.ReadingOptions): JSONSerialization.ReadingOptions {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return ReadingOptions(rawValue = value)
            }
        }
    }

    class WritingOptions: OptionSet<JSONSerialization.WritingOptions, UInt>, MutableStruct {
        override var rawValue: UInt
        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): JSONSerialization.WritingOptions = WritingOptions(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: JSONSerialization.WritingOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONSerialization.WritingOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = JSONSerialization.WritingOptions(this as MutableStruct)

        private fun assignfrom(target: JSONSerialization.WritingOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            val prettyPrinted = WritingOptions(rawValue = 1U shl 0)
            val sortedKeys = WritingOptions(rawValue = 1U shl 1)
            val fragmentsAllowed = WritingOptions(rawValue = 1U shl 2)
            val withoutEscapingSlashes = WritingOptions(rawValue = 1U shl 3)

            fun of(vararg options: JSONSerialization.WritingOptions): JSONSerialization.WritingOptions {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return WritingOptions(rawValue = value)
            }
        }
    }


    companion object: CompanionClass() {

        override val maximumRecursionDepth = 512

        override fun isValidJSONObject(obj: Any): Boolean {
            var recursionDepth = 0

            fun isValidJSONObjectInternal(obj: Any?): Boolean {
                var deferaction_0: (() -> Unit)? = null
                try {
                    if (recursionDepth >= JSONSerialization.maximumRecursionDepth) {
                        return false
                    }
                    recursionDepth += 1
                    deferaction_0 = {
                        recursionDepth -= 1
                    }
                    if (obj == null) {
                        return true
                    }

                    val isCastingWithoutBridging = false

                    if (!isCastingWithoutBridging) {
                        if (obj is String || obj is NSNull || obj is Int || obj is Boolean || obj is UInt || obj is Byte || obj is Short || obj is Int || obj is Long || obj is UByte || obj is UShort || obj is UInt || obj is ULong) {
                            return true
                        }
                    }

                    // object is a Double and is not NaN or infinity
                    (obj as? Double)?.let { number ->
                        return number.isFinite()
                    }
                    // object is a Float and is not NaN or infinity
                    (obj as? Float)?.let { number ->
                        return number.isFinite()
                    }


                    // object is Swift.Array
                    (obj as? Array<Any?>).sref()?.let { array ->
                        for (element in array.sref()) {
                            if (!isValidJSONObjectInternal(element)) {
                                return false
                            }
                        }
                        return true
                    }

                    // object is Swift.Dictionary
                    (obj as? Dictionary<String, Any?>).sref()?.let { dictionary ->
                        for ((_, value) in dictionary.sref()) {
                            if (!isValidJSONObjectInternal(value)) {
                                return false
                            }
                        }
                        return true
                    }


                    // invalid object
                    return false
                } finally {
                    deferaction_0?.invoke()
                }
            }
            if (obj !is Array<*> && obj !is Dictionary<*, *>) {
                return false
            }

            return isValidJSONObjectInternal(obj)
        }

        override fun _data(withJSONObject: Any, options: JSONSerialization.WritingOptions, stream: Boolean): Data {
            val value = withJSONObject
            val opt = options
            var jsonStr = Array<UByte>()

            var writer = JSONWriter(options = opt, writer = { str: String? ->
                if (str != null) {
                    jsonStr.append(contentsOf = str.utf8)
                }
            })


            val matchtarget_0 = value as? Array<Any>
            if (matchtarget_0 != null) {
                val container = matchtarget_0
                writer.serializeJSON(container)
            } else {
                val matchtarget_1 = value as? Dictionary<AnyHashable, Any>
                if (matchtarget_1 != null) {
                    val container = matchtarget_1
                    writer.serializeJSON(container)
                } else {
                    if (!opt.contains(WritingOptions.fragmentsAllowed)) {
                        fatalError("Top-level object was not Array or Dictionary")
                    }
                    writer.serializeJSON(value)
                }
            }

            val count = jsonStr.count
            count
            return Data(jsonStr) // Data(bytes: &jsonStr, count: count)
        }

        override fun data(withJSONObject: Any, options: JSONSerialization.WritingOptions): Data {
            val value = withJSONObject
            val opt = options
            return _data(withJSONObject = value, options = opt, stream = false)
        }

        override fun jsonObject(with: Data, options: JSONSerialization.ReadingOptions): Any {
            val data = with
            val opt = options
            try {
                val bytes = data.bytes.sref()
                val (encoding, advanceBy) = JSONSerialization.detectEncoding(bytes)
                var parser: JSONParser
                if (encoding == StringEncoding.utf8) {
                    if (advanceBy == 0) {
                        parser = JSONParser(bytes = bytes)
                    } else {
                        parser = JSONParser(bytes = Array(bytes[advanceBy until bytes.count]))
                    }
                } else {
                    val utf8String_0 = String(bytes = Array(bytes[advanceBy until bytes.count]), encoding = encoding)
                    if (utf8String_0 == null) {
                        throw JSONError.cannotConvertInputDataToUTF8
                    }
                    parser = JSONParser(bytes = Array(utf8String_0.utf8))
                }

                val value = parser.parseSwiftValue()
                if (!opt.contains(JSONSerialization.ReadingOptions.fragmentsAllowed) && !(value is Array<*> || value is Dictionary<*, *>)) {
                    throw JSONError.singleFragmentFoundButNotAllowed
                }
                return value.sref()
            } catch (error: JSONError            ) {
                when (error) {
                    is JSONError.CannotConvertInputDataToUTF8Case -> {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Cannot convert input string to valid utf8 input.")
                        ))
                    }
                    is JSONError.UnexpectedEndOfFileCase -> {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Unexpected end of file during JSON parse.")
                        ))
                    }
                    is JSONError.UnexpectedCharacterCase -> {
                        val characterIndex = error.associated1
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Invalid value around character ${characterIndex}.")
                        ))
                    }
                    is JSONError.ExpectedLowSurrogateUTF8SequenceAfterHighSurrogateCase -> {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Unexpected end of file during string parse (expected low-surrogate code point but did not find one).")
                        ))
                    }
                    is JSONError.CouldNotCreateUnicodeScalarFromUInt32Case -> {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Unable to convert hex escape sequence (no high character) to UTF8-encoded character.")
                        ))
                    }
                    is JSONError.UnexpectedEscapedCharacterCase -> {
                        val index = error.associated2
                        // we lower the failure index by one to match the darwin implementations counting
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Invalid escape sequence around character ${index - 1}.")
                        ))
                    }
                    is JSONError.SingleFragmentFoundButNotAllowedCase -> {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "JSON text did not start with array or object and option to allow fragments not set.")
                        ))
                    }
                    is JSONError.TooManyNestedArraysOrDictionariesCase -> {
                        val characterIndex = error.characterIndex
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, "Too many nested arrays or dictionaries around character ${characterIndex + 1}.")
                        ))
                    }
                    is JSONError.InvalidHexDigitSequenceCase -> {
                        val string = error.associated0
                        val index = error.index
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, """Invalid hex encoded sequence in "${string}" at ${index}.""")
                        ))
                    }
                    is JSONError.UnescapedControlCharacterInStringCase -> {
                        val index = error.index
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, """Unescaped control character around character ${index}.""")
                        ))
                    }
                    is JSONError.NumberWithLeadingZeroCase -> {
                        val index = error.index
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, """Number with leading zero around character ${index}.""")
                        ))
                    }
                    is JSONError.NumberIsNotRepresentableInSwiftCase -> {
                        val parsed = error.parsed
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, """Number ${parsed} is not representable in Swift.""")
                        ))
                    }
                    is JSONError.InvalidUTF8SequenceCase -> {
                        val data = error.associated0
                        val index = error.characterIndex
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(
                            Tuple2(NSDebugDescriptionErrorKey, """Invalid UTF-8 sequence ${data} starting from character ${index}.""")
                        ))
                    }
                }
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
                throw NSError(domain = NSCocoaErrorDomain, code = 0, userInfo = dictionaryOf(
                    Tuple2(NSDebugDescriptionErrorKey, "JSON parse error: ${error}")
                ))
            }
        }

        private fun detectEncoding(bytes: Array<UByte>): Tuple2<StringEncoding, Int> {
            // According to RFC8259, the text encoding in JSON must be UTF8 in nonclosed systems
            // https://tools.ietf.org/html/rfc8259#section-8.1
            // However, since Darwin Foundation supports utf16 and utf32, so should Swift Foundation.

            // First let's check if we can determine the encoding based on a leading Byte Ordering Mark
            // (BOM).
            if (bytes.count >= 4) {
                if (bytes.starts(with = Companion.utf8BOM)) {
                    return Tuple2(StringEncoding.utf8, 3)
                }
                if (bytes.starts(with = Companion.utf32BigEndianBOM)) {
                    return Tuple2(StringEncoding.utf32BigEndian, 4)
                }
                if (bytes.starts(with = Companion.utf32LittleEndianBOM)) {
                    return Tuple2(StringEncoding.utf32LittleEndian, 4)
                }
                if (bytes.starts(with = arrayOf(UByte(0xFF), UByte(0xFE)))) {
                    return Tuple2(StringEncoding.utf16LittleEndian, 2)
                }
                if (bytes.starts(with = arrayOf(UByte(0xFE), UByte(0xFF)))) {
                    return Tuple2(StringEncoding.utf16BigEndian, 2)
                }
            }

            // If there is no BOM present, we might be able to determine the encoding based on
            // occurences of null bytes.
            if (bytes.count >= 4) {
                if (bytes[0] == UByte(0) && bytes[1] == UByte(0) && bytes[2] == UByte(0)) {
                    return Tuple2(StringEncoding.utf32BigEndian, 0)
                } else if (bytes[1] == UByte(0) && bytes[2] == UByte(0) && bytes[3] == UByte(0)) {
                    return Tuple2(StringEncoding.utf32LittleEndian, 0)
                } else if (bytes[0] == UByte(0) && bytes[2] == UByte(0)) {
                    return Tuple2(StringEncoding.utf16BigEndian, 0)
                } else if (bytes[1] == UByte(0) && bytes[3] == UByte(0)) {
                    return Tuple2(StringEncoding.utf16LittleEndian, 0)
                }
            } else if (bytes.count >= 2) {
                if (bytes[0] == UByte(0)) {
                    return Tuple2(StringEncoding.utf16BigEndian, 0)
                } else if (bytes[1] == UByte(0)) {
                    return Tuple2(StringEncoding.utf16LittleEndian, 0)
                }
            }
            return Tuple2(StringEncoding.utf8, 0)
        }

        // These static properties don't look very nice, but we need them to
        // workaround: https://bugs.swift.org/browse/SR-14102
        private val utf8BOM: Array<UByte> = arrayOf(UByte(0xEF), UByte(0xBB), UByte(0xBF))
        private val utf32BigEndianBOM: Array<UByte> = arrayOf(UByte(0x00), UByte(0x00), UByte(0xFE), UByte(0xFF))
        private val utf32LittleEndianBOM: Array<UByte> = arrayOf(UByte(0xFF), UByte(0xFE), UByte(0x00), UByte(0x00))
        private val utf16BigEndianBOM: Array<UByte> = arrayOf(UByte(0xFF), UByte(0xFE))
        private val utf16LittleEndianBOM: Array<UByte> = arrayOf(UByte(0xFE), UByte(0xFF))
    }
    open class CompanionClass {
        internal open val maximumRecursionDepth
            get() = JSONSerialization.maximumRecursionDepth
        open fun isValidJSONObject(obj: Any): Boolean = JSONSerialization.isValidJSONObject(obj)
        internal open fun _data(withJSONObject: Any, options: JSONSerialization.WritingOptions, stream: Boolean): Data = JSONSerialization._data(withJSONObject = withJSONObject, options = options, stream = stream)
        open fun data(withJSONObject: Any, options: JSONSerialization.WritingOptions = WritingOptions(rawValue = 0U)): Data = JSONSerialization.data(withJSONObject = withJSONObject, options = options)
        open fun jsonObject(with: Data, options: JSONSerialization.ReadingOptions = ReadingOptions(rawValue = 0U)): Any = JSONSerialization.jsonObject(with = with, options = options)
    }
}

//MARK: - JSONSerializer
private class JSONWriter: MutableStruct {

    internal var indent = 0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    internal val pretty: Boolean
    internal val sortedKeys: Boolean
    internal val withoutEscapingSlashes: Boolean
    internal val writer: (String?) -> Unit

    internal constructor(options: JSONSerialization.WritingOptions, writer: (String?) -> Unit) {
        this.indentAmount = 2
        pretty = options.contains(JSONSerialization.WritingOptions.prettyPrinted)
        sortedKeys = options.contains(JSONSerialization.WritingOptions.sortedKeys)
        withoutEscapingSlashes = options.contains(JSONSerialization.WritingOptions.withoutEscapingSlashes)
        this.writer = writer
    }

    internal fun serializeJSON(object_: Any?) {
        willmutate()
        try {

            var toSerialize = object_.sref()

            val obj_0 = toSerialize.sref()
            if (obj_0 == null) {
                serializeNull()
                return
            }

            // For better performance, the most expensive conditions to evaluate should be last.
            val matchtarget_2 = (obj_0)
            when (matchtarget_2) {
                is String -> {
                    val str = matchtarget_2
                    serializeString(str)
                }
                is Boolean -> {
                    val boolValue = matchtarget_2
                    writer(boolValue.description)
                }
                is Int -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is Byte -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is Short -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is Int -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is Long -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is UInt -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is UByte -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is UShort -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is UInt -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is ULong -> {
                    val num = matchtarget_2
                    writer(num.description)
                }
                is Array<*> -> {
                    val array = matchtarget_2
                    serializeArray(array as Array<Any?>)
                }
                is Dictionary<*, *> -> {
                    val dict = matchtarget_2
                    serializeDictionary(dict as Dictionary<AnyHashable, Any?>)
                }
                is Float -> {
                    val num = matchtarget_2
                    serializeFloat(Double(num))
                }
                is Double -> {
                    val num = matchtarget_2
                    serializeFloat(num)
                }
                is NSNull -> {
                    serializeNull()
                }
                else -> {
                    throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(Tuple2(NSDebugDescriptionErrorKey, "Invalid object cannot be serialized")))
                }
            }
        } finally {
            didmutate()
        }
    }

    internal fun serializeString(str: String) {
        writer("\"")
        for (scalar in str) {
            when (scalar) {
                '\"' -> writer("\\\"") // U+0022 quotation mark
                '\\' -> writer("\\\\") // U+005C reverse solidus
                '/' -> {
                    if (!withoutEscapingSlashes) {
                        writer("\\")
                    }
                    writer("/") // U+002F solidus
                }
                '\n' -> writer("\\n") // U+000A line feed
                '\r' -> writer("\\r") // U+000D carriage return
                '\t' -> writer("\\t") // U+0009 tab
                else -> writer(String(scalar))
            }
        }
        writer("\"")
    }

    private fun serializeFloat(num: Double) {
        if (!num.isFinite) {
            throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(Tuple2(NSDebugDescriptionErrorKey, "Invalid number value (${num}) in JSON write")))
        }
        var str = num.description
        if (str.hasSuffix(".0")) {
            str = String(str.dropLast(2))
        }
        writer(str)
    }

    internal fun serializeArray(array: Array<Any?>) {
        willmutate()
        try {
            writer("[")
            if (pretty) {
                writer("\n")
                incIndent()
            }

            var first = true
            for (elem in array.sref()) {
                if (first) {
                    first = false
                } else if (pretty) {
                    writer(",\n")
                } else {
                    writer(",")
                }
                if (pretty) {
                    writeIndent()
                }
                serializeJSON(elem)
            }
            if (pretty) {
                writer("\n")
                decAndWriteIndent()
            }
            writer("]")
        } finally {
            didmutate()
        }
    }

    internal fun serializeDictionary(dict: Dictionary<AnyHashable, Any?>) {
        willmutate()
        try {
            writer("{")
            if (pretty) {
                writer("\n")
                incIndent()
                if (dict.count > 0) {
                    writeIndent()
                }
            }

            var first = true

            fun serializeDictionaryElement(key: AnyHashable, value: Any?) {
                if (first) {
                    first = false
                } else if (pretty) {
                    writer(",\n")
                    writeIndent()
                } else {
                    writer(",")
                }

                val matchtarget_3 = key as? String
                if (matchtarget_3 != null) {
                    val key = matchtarget_3
                    serializeString(key)
                } else {
                    throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(Tuple2(NSDebugDescriptionErrorKey, "NSDictionary key must be NSString")))
                }
                if (pretty) writer(" : ") else writer(":")
                serializeJSON(value)
            }

            if (sortedKeys) {
                val elems = dict.sorted(by = l@{ a, b ->
                    val a_0 = a.key as? String
                    if (a_0 == null) {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(Tuple2(NSDebugDescriptionErrorKey, "NSDictionary key must be NSString")))
                    }
                    val b_0 = b.key as? String
                    if (b_0 == null) {
                        throw NSError(domain = NSCocoaErrorDomain, code = CocoaError.propertyListReadCorrupt.rawValue, userInfo = dictionaryOf(Tuple2(NSDebugDescriptionErrorKey, "NSDictionary key must be NSString")))
                    }
                    return@l a_0 < b_0
                })
                for (elem in elems.sref()) {
                    serializeDictionaryElement(key = elem.key, value = elem.value)
                }
            } else {
                for ((key, value) in dict.sref()) {
                    serializeDictionaryElement(key = key, value = value)
                }
            }

            if (pretty) {
                writer("\n")
                decAndWriteIndent()
            }
            writer("}")
        } finally {
            didmutate()
        }
    }

    internal fun serializeNull(): Unit = writer("null")

    internal val indentAmount: Int

    internal fun incIndent() {
        willmutate()
        try {
            indent += indentAmount
        } finally {
            didmutate()
        }
    }

    internal fun incAndWriteIndent() {
        willmutate()
        try {
            indent += indentAmount
            writeIndent()
        } finally {
            didmutate()
        }
    }

    internal fun decAndWriteIndent() {
        willmutate()
        try {
            indent -= indentAmount
            writeIndent()
        } finally {
            didmutate()
        }
    }

    internal fun writeIndent() {
        for (unusedbinding in 0 until indent) {
            writer(" ")
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONWriter
        this.indent = copy.indent
        this.pretty = copy.pretty
        this.sortedKeys = copy.sortedKeys
        this.withoutEscapingSlashes = copy.withoutEscapingSlashes
        this.writer = copy.writer
        this.indentAmount = copy.indentAmount
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = JSONWriter(this as MutableStruct)
}

sealed class JSONValue {
    class StringCase(val associated0: String): JSONValue() {
        override fun equals(other: Any?): Boolean {
            if (other !is StringCase) return false
            return associated0 == other.associated0
        }
    }
    class NumberCase(val associated0: String): JSONValue() {
        override fun equals(other: Any?): Boolean {
            if (other !is NumberCase) return false
            return associated0 == other.associated0
        }
    }
    class BoolCase(val associated0: Boolean): JSONValue() {
        override fun equals(other: Any?): Boolean {
            if (other !is BoolCase) return false
            return associated0 == other.associated0
        }
    }
    class NullCase: JSONValue() {
    }

    class ArrayCase(val associated0: Array<JSONValue>): JSONValue() {
        override fun equals(other: Any?): Boolean {
            if (other !is ArrayCase) return false
            return associated0 == other.associated0
        }
    }
    class ObjectCase(val associated0: Dictionary<String, JSONValue>): JSONValue() {
        override fun equals(other: Any?): Boolean {
            if (other !is ObjectCase) return false
            return associated0 == other.associated0
        }
    }

    val isValue: Boolean
        get() {
            when (this) {
                is JSONValue.ArrayCase, is JSONValue.ObjectCase -> return false
                is JSONValue.NullCase, is JSONValue.NumberCase, is JSONValue.StringCase, is JSONValue.BoolCase -> return true
            }
        }

    val isContainer: Boolean
        get() {
            when (this) {
                is JSONValue.ArrayCase, is JSONValue.ObjectCase -> return true
                is JSONValue.NullCase, is JSONValue.NumberCase, is JSONValue.StringCase, is JSONValue.BoolCase -> return false
            }
        }

    val debugDataTypeDescription: String
        get() {
            when (this) {
                is JSONValue.ArrayCase -> return "an array"
                is JSONValue.BoolCase -> return "bool"
                is JSONValue.NumberCase -> return "a number"
                is JSONValue.StringCase -> return "a string"
                is JSONValue.ObjectCase -> return "a dictionary"
                is JSONValue.NullCase -> return "null"
            }
        }

    internal class Writer {
        internal val options: JSONEncoder.OutputFormatting

        internal constructor(options: JSONEncoder.OutputFormatting) {
            this.options = options.sref()
        }

        internal fun writeValue(value: JSONValue): Array<UByte> {
            var bytes = Array<UByte>()
            if (this.options.contains(JSONEncoder.OutputFormatting.prettyPrinted)) {
                this.writeValuePretty(value, into = InOut({ bytes }, { bytes = it }))
            } else {
                this.writeValue(value, into = InOut({ bytes }, { bytes = it }))
            }
            return bytes.sref()
        }

        private fun writeValue(value: JSONValue, into: InOut<Array<UByte>>) {
            val bytes = into
            when (value) {
                is JSONValue.NullCase -> bytes.value.append(contentsOf = UInt8Array_null)
                is JSONValue.BoolCase -> {
                    val b = value.associated0
                    if (b) {
                        bytes.value.append(contentsOf = UInt8Array_true)
                    } else {
                        bytes.value.append(contentsOf = UInt8Array_false)
                    }
                }
                is JSONValue.StringCase -> {
                    val string = value.associated0
                    this.encodeString(string, to = InOut({ bytes.value }, { bytes.value = it }))
                }
                is JSONValue.NumberCase -> {
                    val string = value.associated0
                    bytes.value.append(contentsOf = string.utf8)
                }
                is JSONValue.ArrayCase -> {
                    val array = value.associated0
                    var iterator = array.makeIterator()
                    bytes.value.append(UInt8_openbracket)
                    // we don't like branching, this is why we have this extra
                    iterator.next()?.let { first ->
                        this.writeValue(first, into = InOut({ bytes.value }, { bytes.value = it }))
                    }
                    while (true) {
                        val item_0 = iterator.next()
                        if (item_0 == null) {
                            break
                        }
                        bytes.value.append(UInt8_comma)
                        this.writeValue(item_0, into = InOut({ bytes.value }, { bytes.value = it }))
                    }
                    bytes.value.append(UInt8_closebracket)
                }
                is JSONValue.ObjectCase -> {
                    val dict = value.associated0
                    if (options.contains(JSONEncoder.OutputFormatting.sortedKeys)) {
                        val sorted = Array(dict).sorted { it, it_1 -> it.key < it_1.key }
                        this.writeObject(sorted, into = InOut({ bytes.value }, { bytes.value = it }))
                    } else {
                        this.writeObject(Array(dict), into = InOut({ bytes.value }, { bytes.value = it }))
                    }
                }
            }
        }

        private fun writeObject(object_: Array<Tuple2<String, JSONValue>>, into: InOut<Array<UByte>>, depth: Int = 0) {
            val bytes = into
            var iterator = object_.makeIterator()
            bytes.value.append(UInt8_openbrace)
            iterator.next()?.let { (key, value) ->
                this.encodeString(key, to = InOut({ bytes.value }, { bytes.value = it }))
                bytes.value.append(UInt8_colon)
                this.writeValue(value, into = InOut({ bytes.value }, { bytes.value = it }))
            }
            while (true) {
                val matchtarget_4 = iterator.next()
                if (matchtarget_4 != null) {
                    val (key, value) = matchtarget_4
                    bytes.value.append(UInt8_comma)
                    // key
                    this.encodeString(key, to = InOut({ bytes.value }, { bytes.value = it }))
                    bytes.value.append(UInt8_colon)

                    this.writeValue(value, into = InOut({ bytes.value }, { bytes.value = it }))
                } else {
                    break
                }
            }
            bytes.value.append(UInt8_closebrace)
        }

        private fun addInset(to: InOut<Array<UByte>>, depth: Int) {
            val bytes = to
            for (unusedbinding in 0 until depth) {
                bytes.value.append(UInt8_space)
                bytes.value.append(UInt8_space)
            }
        }

        private fun writeValuePretty(value: JSONValue, into: InOut<Array<UByte>>, depth: Int = 0) {
            val bytes = into
            when (value) {
                is JSONValue.NullCase -> bytes.value.append(contentsOf = UInt8Array_null)
                is JSONValue.BoolCase -> {
                    val b = value.associated0
                    if (b == true) {
                        bytes.value.append(contentsOf = UInt8Array_true)
                    } else {
                        bytes.value.append(contentsOf = UInt8Array_false)
                    }
                }
                is JSONValue.StringCase -> {
                    val string = value.associated0
                    this.encodeString(string, to = InOut({ bytes.value }, { bytes.value = it }))
                }
                is JSONValue.NumberCase -> {
                    val string = value.associated0
                    bytes.value.append(contentsOf = string.utf8)
                }
                is JSONValue.ArrayCase -> {
                    val array = value.associated0
                    var iterator = array.makeIterator()
                    bytes.value.append(contentsOf = arrayOf(UInt8_openbracket, UInt8_newline))
                    iterator.next()?.let { first ->
                        this.addInset(to = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                        this.writeValuePretty(first, into = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                    }
                    while (true) {
                        val item_1 = iterator.next()
                        if (item_1 == null) {
                            break
                        }
                        bytes.value.append(contentsOf = arrayOf(UInt8_comma, UInt8_newline))
                        this.addInset(to = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                        this.writeValuePretty(item_1, into = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                    }
                    bytes.value.append(UInt8_newline)
                    this.addInset(to = InOut({ bytes.value }, { bytes.value = it }), depth = depth)
                    bytes.value.append(UInt8_closebracket)
                }
                is JSONValue.ObjectCase -> {
                    val dict = value.associated0
                    if (options.contains(JSONEncoder.OutputFormatting.sortedKeys)) {
                        val sorted = Array(dict).sorted { it, it_1 -> it.key < it_1.key }
                        this.writePrettyObject(sorted, into = InOut({ bytes.value }, { bytes.value = it }), depth = depth)
                    } else {
                        this.writePrettyObject(Array(dict), into = InOut({ bytes.value }, { bytes.value = it }), depth = depth)
                    }
                }
            }
        }

        private fun writePrettyObject(object_: Array<Tuple2<String, JSONValue>>, into: InOut<Array<UByte>>, depth: Int = 0) {
            val bytes = into
            var iterator = object_.makeIterator()
            bytes.value.append(contentsOf = arrayOf(UInt8_openbrace, UInt8_newline))
            iterator.next()?.let { (key, value) ->
                this.addInset(to = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                this.encodeString(key, to = InOut({ bytes.value }, { bytes.value = it }))
                bytes.value.append(contentsOf = arrayOf(UInt8_space, UInt8_colon, UInt8_space))
                this.writeValuePretty(value, into = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
            }
            while (true) {
                val matchtarget_5 = iterator.next()
                if (matchtarget_5 != null) {
                    val (key, value) = matchtarget_5
                    bytes.value.append(contentsOf = arrayOf(UInt8_comma, UInt8_newline))
                    this.addInset(to = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                    // key
                    this.encodeString(key, to = InOut({ bytes.value }, { bytes.value = it }))
                    bytes.value.append(contentsOf = arrayOf(UInt8_space, UInt8_colon, UInt8_space))
                    // value
                    this.writeValuePretty(value, into = InOut({ bytes.value }, { bytes.value = it }), depth = depth + 1)
                } else {
                    break
                }
            }
            bytes.value.append(UInt8_newline)
            this.addInset(to = InOut({ bytes.value }, { bytes.value = it }), depth = depth)
            bytes.value.append(UInt8_closebrace)
        }

        private fun encodeString(string: String, to: InOut<Array<UByte>>) {
            val bytes = to
            bytes.value.append(UByte(ascii = "\""))
            val stringBytes = string.utf8.sref()
            var startCopyIndex = stringBytes.startIndex
            var nextIndex = startCopyIndex

            while (nextIndex != stringBytes.endIndex) {
                when (stringBytes[nextIndex]) {
                    in UByte(0) until UByte(32), UByte(ascii = "\""), UByte(ascii = "\\") -> {
                        // All Unicode characters may be placed within the
                        // quotation marks, except for the characters that MUST be escaped:
                        // quotation mark, reverse solidus, and the control characters (U+0000
                        // through U+001F).
                        // https://tools.ietf.org/html/rfc8259#section-7

                        // copy the current range over
                        bytes.value.append(contentsOf = stringBytes[startCopyIndex until nextIndex])
                        when (stringBytes[nextIndex]) {
                            UByte(ascii = "\"") -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UInt8_quote))
                            UByte(ascii = "\\") -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UInt8_backslash))
                            UByte(0x08) -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UByte(ascii = "b")) as Array<UByte>)
                            UByte(0x0C) -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UByte(ascii = "f")))
                            UByte(0x0A) -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UByte(ascii = "n")))
                            UByte(0x0D) -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UByte(ascii = "r")))
                            UByte(0x09) -> bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UByte(ascii = "t")))
                            else -> {
                                fun valueToAscii(value: UByte): UByte {
                                    when (value) {
                                        in UByte(0)..UByte(9) -> return UByte(value + UByte(ascii = "0"))
                                        in UByte(10)..UByte(15) -> return UByte(value - UByte(10) + UByte(ascii = "a"))
                                        else -> preconditionFailure()
                                    }
                                }
                                bytes.value.append(UByte(ascii = "\\"))
                                bytes.value.append(UByte(ascii = "u"))
                                bytes.value.append(UByte(ascii = "0"))
                                bytes.value.append(UByte(ascii = "0"))
                                val first = stringBytes[nextIndex] / UByte(16)
                                val remaining = stringBytes[nextIndex] % UByte(16)
                                bytes.value.append(valueToAscii(UByte(first)))
                                bytes.value.append(valueToAscii(UByte(remaining)))
                            }
                        }

                        nextIndex = stringBytes.index(after = nextIndex)
                        startCopyIndex = nextIndex
                    }
                    UByte(ascii = "/") -> {
                        if (options.contains(JSONEncoder.OutputFormatting.withoutEscapingSlashes) == false) {
                            bytes.value.append(contentsOf = stringBytes[startCopyIndex until nextIndex])
                            bytes.value.append(contentsOf = arrayOf(UInt8_backslash, UByte(ascii = "/")))
                            nextIndex = stringBytes.index(after = nextIndex)
                            startCopyIndex = nextIndex
                        } else {
                            nextIndex = stringBytes.index(after = nextIndex)
                        }
                    }
                    else -> nextIndex = stringBytes.index(after = nextIndex)
                }
            }
            bytes.value.append(contentsOf = stringBytes[startCopyIndex until nextIndex])
            bytes.value.append(UByte(ascii = "\""))
        }
    }

    companion object {
        fun string(associated0: String): JSONValue = StringCase(associated0)
        fun number(associated0: String): JSONValue = NumberCase(associated0)
        fun bool(associated0: Boolean): JSONValue = BoolCase(associated0)
        val null_: JSONValue = NullCase()
        fun array(associated0: Array<JSONValue>): JSONValue = ArrayCase(associated0)
        fun object_(associated0: Dictionary<String, JSONValue>): JSONValue = ObjectCase(associated0)
    }
}

