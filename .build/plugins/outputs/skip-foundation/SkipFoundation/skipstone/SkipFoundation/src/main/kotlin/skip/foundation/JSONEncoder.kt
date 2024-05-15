// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import kotlin.reflect.KClass
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


open class JSONEncoder: TopLevelEncoder<Data> {
    class OutputFormatting: OptionSet<JSONEncoder.OutputFormatting, UInt>, MutableStruct {
        override var rawValue: UInt

        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): JSONEncoder.OutputFormatting = OutputFormatting(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: JSONEncoder.OutputFormatting) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONEncoder.OutputFormatting
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = JSONEncoder.OutputFormatting(this as MutableStruct)

        private fun assignfrom(target: JSONEncoder.OutputFormatting) {
            this.rawValue = target.rawValue
        }

        companion object {

            val prettyPrinted = OutputFormatting(rawValue = 1U shl 0)
            val sortedKeys = OutputFormatting(rawValue = 1U shl 1)
            val withoutEscapingSlashes = OutputFormatting(rawValue = 1U shl 3)

            fun of(vararg options: JSONEncoder.OutputFormatting): JSONEncoder.OutputFormatting {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return OutputFormatting(rawValue = value)
            }
        }
    }

    sealed class DateEncodingStrategy {
        class DeferredToDateCase: DateEncodingStrategy() {
        }
        class SecondsSince1970Case: DateEncodingStrategy() {
        }
        class MillisecondsSince1970Case: DateEncodingStrategy() {
        }
        class Iso8601Case: DateEncodingStrategy() {
        }
        class FormattedCase(val associated0: DateFormatter): DateEncodingStrategy() {
        }
        class CustomCase(val associated0: (Date, Encoder) -> Unit): DateEncodingStrategy() {
        }

        companion object {
            val deferredToDate: DateEncodingStrategy = DeferredToDateCase()
            val secondsSince1970: DateEncodingStrategy = SecondsSince1970Case()
            val millisecondsSince1970: DateEncodingStrategy = MillisecondsSince1970Case()
            val iso8601: DateEncodingStrategy = Iso8601Case()
            fun formatted(associated0: DateFormatter): DateEncodingStrategy = FormattedCase(associated0)
            fun custom(associated0: (Date, Encoder) -> Unit): DateEncodingStrategy = CustomCase(associated0)
        }
    }

    sealed class DataEncodingStrategy {
        class DeferredToDataCase: DataEncodingStrategy() {
        }
        class Base64Case: DataEncodingStrategy() {
        }
        class CustomCase(val associated0: (Data, Encoder) -> Unit): DataEncodingStrategy() {
        }

        companion object {
            val deferredToData: DataEncodingStrategy = DeferredToDataCase()
            val base64: DataEncodingStrategy = Base64Case()
            fun custom(associated0: (Data, Encoder) -> Unit): DataEncodingStrategy = CustomCase(associated0)
        }
    }

    sealed class NonConformingFloatEncodingStrategy {
        class ThrowCase: NonConformingFloatEncodingStrategy() {
        }
        class ConvertToStringCase(val associated0: String, val associated1: String, val associated2: String): NonConformingFloatEncodingStrategy() {
            val positiveInfinity = associated0
            val negativeInfinity = associated1
            val nan = associated2
        }

        companion object {
            val throw_: NonConformingFloatEncodingStrategy = ThrowCase()
            fun convertToString(positiveInfinity: String, negativeInfinity: String, nan: String): NonConformingFloatEncodingStrategy = ConvertToStringCase(positiveInfinity, negativeInfinity, nan)
        }
    }

    sealed class KeyEncodingStrategy {
        class UseDefaultKeysCase: KeyEncodingStrategy() {
        }
        class ConvertToSnakeCaseCase: KeyEncodingStrategy() {
        }
        class CustomCase(val associated0: (Array<CodingKey>) -> CodingKey): KeyEncodingStrategy() {
        }

        companion object {
            val useDefaultKeys: KeyEncodingStrategy = UseDefaultKeysCase()
            val convertToSnakeCase: KeyEncodingStrategy = ConvertToSnakeCaseCase()
            fun custom(associated0: (Array<CodingKey>) -> CodingKey): KeyEncodingStrategy = CustomCase(associated0)


            internal fun _convertToSnakeCase(stringKey: String): String {
                if (stringKey.isEmpty) {
                    return stringKey
                }
                fatalError("SKIP TODO: JSON snakeCase")
                //            var words: [Range<String.Index>] = []
                //            // The general idea of this algorithm is to split words on transition from lower to upper case, then on transition of >1 upper case characters to lowercase
                //            //
                //            // myProperty -> my_property
                //            // myURLProperty -> my_url_property
                //            //
                //            // We assume, per Swift naming conventions, that the first character of the key is lowercase.
                //            var wordStart = stringKey.startIndex
                //            var searchRange = stringKey.index(after: wordStart)..<stringKey.endIndex
                //
                //            // Find next uppercase character
                //            while let upperCaseRange = stringKey.rangeOfCharacter(from: .uppercaseLetters, options: [], range: searchRange) {
                //                let untilUpperCase = wordStart..<upperCaseRange.lowerBound
                //                words.append(untilUpperCase)
                //
                //                // Find next lowercase character
                //                searchRange = upperCaseRange.lowerBound..<searchRange.upperBound
                //                guard let lowerCaseRange = stringKey.rangeOfCharacter(from: .lowercaseLetters, options: [], range: searchRange) else {
                //                    // There are no more lower case letters. Just end here.
                //                    wordStart = searchRange.lowerBound
                //                    break
                //                }
                //
                //                // Is the next lowercase letter more than 1 after the uppercase? If so, we encountered a group of uppercase letters that we should treat as its own word
                //                let nextCharacterAfterCapital = stringKey.index(after: upperCaseRange.lowerBound)
                //                if lowerCaseRange.lowerBound == nextCharacterAfterCapital {
                //                    // The next character after capital is a lower case character and therefore not a word boundary.
                //                    // Continue searching for the next upper case for the boundary.
                //                    wordStart = upperCaseRange.lowerBound
                //                } else {
                //                    // There was a range of >1 capital letters. Turn those into a word, stopping at the capital before the lower case character.
                //                    let beforeLowerIndex = stringKey.index(before: lowerCaseRange.lowerBound)
                //                    words.append(upperCaseRange.lowerBound..<beforeLowerIndex)
                //
                //                    // Next word starts at the capital before the lowercase we just found
                //                    wordStart = beforeLowerIndex
                //                }
                //                searchRange = lowerCaseRange.upperBound..<searchRange.upperBound
                //            }
                //            words.append(wordStart..<searchRange.upperBound)
                //            let result = words.map({ (range) in
                //                return stringKey[range].lowercased()
                //            }).joined(separator: "_")
                //            return result
            }
        }
    }

    open var outputFormatting: JSONEncoder.OutputFormatting = JSONEncoder.OutputFormatting.of()
        get() = field.sref({ this.outputFormatting = it })
        set(newValue) {
            field = newValue.sref()
        }
    open var dateEncodingStrategy: JSONEncoder.DateEncodingStrategy = JSONEncoder.DateEncodingStrategy.deferredToDate
    open var dataEncodingStrategy: JSONEncoder.DataEncodingStrategy = JSONEncoder.DataEncodingStrategy.base64
    open var nonConformingFloatEncodingStrategy: JSONEncoder.NonConformingFloatEncodingStrategy = JSONEncoder.NonConformingFloatEncodingStrategy.throw_
    open var keyEncodingStrategy: JSONEncoder.KeyEncodingStrategy = JSONEncoder.KeyEncodingStrategy.useDefaultKeys
    open var userInfo: Dictionary<CodingUserInfoKey, Any> = dictionaryOf()
        get() = field.sref({ this.userInfo = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal class _Options {
        internal val dateEncodingStrategy: JSONEncoder.DateEncodingStrategy
        internal val dataEncodingStrategy: JSONEncoder.DataEncodingStrategy
        internal val nonConformingFloatEncodingStrategy: JSONEncoder.NonConformingFloatEncodingStrategy
        internal val keyEncodingStrategy: JSONEncoder.KeyEncodingStrategy
        internal val userInfo: Dictionary<CodingUserInfoKey, Any>

        constructor(dateEncodingStrategy: JSONEncoder.DateEncodingStrategy, dataEncodingStrategy: JSONEncoder.DataEncodingStrategy, nonConformingFloatEncodingStrategy: JSONEncoder.NonConformingFloatEncodingStrategy, keyEncodingStrategy: JSONEncoder.KeyEncodingStrategy, userInfo: Dictionary<CodingUserInfoKey, Any>) {
            this.dateEncodingStrategy = dateEncodingStrategy
            this.dataEncodingStrategy = dataEncodingStrategy
            this.nonConformingFloatEncodingStrategy = nonConformingFloatEncodingStrategy
            this.keyEncodingStrategy = keyEncodingStrategy
            this.userInfo = userInfo.sref()
        }
    }

    internal open val options: JSONEncoder._Options
        get() = _Options(dateEncodingStrategy = dateEncodingStrategy, dataEncodingStrategy = dataEncodingStrategy, nonConformingFloatEncodingStrategy = nonConformingFloatEncodingStrategy, keyEncodingStrategy = keyEncodingStrategy, userInfo = userInfo)

    constructor() {
    }

    // Our TopLevelEncoder superclass handles the encode calls. We just have to produce the encoder
    override fun encoder(): Encoder = JSONEncoderImpl(options = this.options, codingPath = arrayOf())

    override fun output(from: Encoder): Data {
        val encoder = from
        val value_0 = (encoder as JSONEncoderImpl).value
        if (value_0 == null) {
            throw EncodingError.invalidValue("?", EncodingError.Context(codingPath = arrayOf(), debugDescription = "Top-level did not encode any values."))
        }
        val writer = JSONValue.Writer(options = this.outputFormatting)
        val bytes = writer.writeValue(value_0)
        return Data(bytes = bytes)
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

private sealed class JSONFuture {
    class ValueCase(val associated0: JSONValue): JSONFuture() {
    }
    class EncoderCase(val associated0: JSONEncoderImpl): JSONFuture() {
    }
    class NestedArrayCase(val associated0: JSONFuture.RefArray): JSONFuture() {
    }
    class NestedObjectCase(val associated0: JSONFuture.RefObject): JSONFuture() {
    }

    internal open class RefArray {
        internal var array: Array<JSONFuture> = arrayOf()
            get() = field.sref({ this.array = it })
            private set(newValue) {
                field = newValue.sref()
            }

        internal constructor() {
        }

        internal open fun append(element: JSONValue): Unit = this.array.append(JSONFuture.value(element))

        internal open fun append(encoder: JSONEncoderImpl): Unit = this.array.append(JSONFuture.encoder(encoder))

        internal open fun appendArray(): JSONFuture.RefArray {
            val array = RefArray()
            this.array.append(JSONFuture.nestedArray(array))
            return array
        }

        internal open fun appendObject(): JSONFuture.RefObject {
            val object_ = RefObject()
            this.array.append(JSONFuture.nestedObject(object_))
            return object_
        }

        internal open val values: Array<JSONValue>
            get() {
                return this.array.map l@{ future ->
                    when (future) {
                        is JSONFuture.ValueCase -> {
                            val value = future.associated0
                            return@l value
                        }
                        is JSONFuture.NestedArrayCase -> {
                            val array = future.associated0
                            return@l JSONValue.array(array.values)
                        }
                        is JSONFuture.NestedObjectCase -> {
                            val obj = future.associated0
                            return@l JSONValue.object_(obj.values)
                        }
                        is JSONFuture.EncoderCase -> {
                            val encoder = future.associated0
                            return@l encoder.value ?: JSONValue.object_(dictionaryOf())
                        }
                    }
                }
            }
    }

    internal open class RefObject {
        internal var dict: Dictionary<String, JSONFuture> = dictionaryOf()
            get() = field.sref({ this.dict = it })
            private set(newValue) {
                field = newValue.sref()
            }

        internal constructor() {
        }

        internal open fun set(value: JSONValue, for_: String) {
            val key = for_
            this.dict[key] = JSONFuture.value(value)
        }

        internal open fun setArray(for_: String): JSONFuture.RefArray {
            val key = for_
            val matchtarget_0 = this.dict[key]
            when (matchtarget_0) {
                is JSONFuture.EncoderCase -> preconditionFailure("For key \"${key}\" an encoder has already been created.")
                is JSONFuture.NestedObjectCase -> preconditionFailure("For key \"${key}\" a keyed container has already been created.")
                is JSONFuture.NestedArrayCase -> {
                    val array = matchtarget_0.associated0
                    return array
                }
                else -> {
                    val array = RefArray()
                    dict[key] = JSONFuture.nestedArray(array)
                    return array
                }
            }
        }

        internal open fun setObject(for_: String): JSONFuture.RefObject {
            val key = for_
            val matchtarget_1 = this.dict[key]
            when (matchtarget_1) {
                is JSONFuture.EncoderCase -> preconditionFailure("For key \"${key}\" an encoder has already been created.")
                is JSONFuture.NestedObjectCase -> {
                    val obj = matchtarget_1.associated0
                    return obj
                }
                is JSONFuture.NestedArrayCase -> preconditionFailure("For key \"${key}\" a unkeyed container has already been created.")
                else -> {
                    val object_ = RefObject()
                    dict[key] = JSONFuture.nestedObject(object_)
                    return object_
                }
            }
        }

        internal open fun set(encoder: JSONEncoderImpl, for_: String) {
            val key = for_
            when (this.dict[key]) {
                is JSONFuture.EncoderCase -> preconditionFailure("For key \"${key}\" an encoder has already been created.")
                is JSONFuture.NestedObjectCase -> preconditionFailure("For key \"${key}\" a keyed container has already been created.")
                is JSONFuture.NestedArrayCase -> preconditionFailure("For key \"${key}\" a unkeyed container has already been created.")
                else -> dict[key] = JSONFuture.encoder(encoder)
            }
        }

        internal open val values: Dictionary<String, JSONValue>
            get() {
                return this.dict.mapValues l@{ future ->
                    when (future) {
                        is JSONFuture.ValueCase -> {
                            val value = future.associated0
                            return@l value
                        }
                        is JSONFuture.NestedArrayCase -> {
                            val array = future.associated0
                            return@l JSONValue.array(array.values)
                        }
                        is JSONFuture.NestedObjectCase -> {
                            val obj = future.associated0
                            return@l JSONValue.object_(obj.values)
                        }
                        is JSONFuture.EncoderCase -> {
                            val encoder = future.associated0
                            return@l encoder.value ?: JSONValue.object_(dictionaryOf())
                        }
                    }
                }
            }
    }

    companion object {
        fun value(associated0: JSONValue): JSONFuture = ValueCase(associated0)
        fun encoder(associated0: JSONEncoderImpl): JSONFuture = EncoderCase(associated0)
        fun nestedArray(associated0: JSONFuture.RefArray): JSONFuture = NestedArrayCase(associated0)
        fun nestedObject(associated0: JSONFuture.RefObject): JSONFuture = NestedObjectCase(associated0)
    }
}

private class JSONEncoderImpl: Encoder, _SpecialTreatmentEncoder {
    override val options: JSONEncoder._Options
    override val codingPath: Array<CodingKey>
    override val userInfo: Dictionary<CodingUserInfoKey, Any>
        get() = options.userInfo

    internal var singleValue: JSONValue? = null
    internal var array: JSONFuture.RefArray? = null
    internal var object_: JSONFuture.RefObject? = null

    internal val value: JSONValue?
        get() {
            this.object_?.let { obj ->
                return JSONValue.object_(obj.values)
            }
            this.array?.let { arr ->
                return JSONValue.array(arr.values)
            }
            return this.singleValue
        }

    internal constructor(options: JSONEncoder._Options, codingPath: Array<CodingKey>) {
        this.options = options
        this.codingPath = codingPath.sref()
    }

    override fun <Key> container(keyedBy: KClass<Key>): KeyedEncodingContainer<Key> where Key: CodingKey {
        val keyType = keyedBy
        object_?.let { _ ->
            val container = JSONKeyedEncodingContainer<Key>(keyedBy = keyType, impl = this, codingPath = codingPath)
            return KeyedEncodingContainer(container)
        }
        if ((this.singleValue != null) || (this.array != null)) {
            preconditionFailure()
        }
        this.object_ = JSONFuture.RefObject()
        val container = JSONKeyedEncodingContainer<Key>(keyedBy = keyType, impl = this, codingPath = codingPath)
        return KeyedEncodingContainer(container)
    }

    override fun unkeyedContainer(): UnkeyedEncodingContainer {
        array?.let { _ ->
            val container = JSONUnkeyedEncodingContainer(impl = this, codingPath = this.codingPath)
            return UnkeyedEncodingContainer(container)
        }
        if ((this.singleValue != null) || (this.object_ != null)) {
            preconditionFailure()
        }
        this.array = JSONFuture.RefArray()
        val container = JSONUnkeyedEncodingContainer(impl = this, codingPath = this.codingPath)
        return UnkeyedEncodingContainer(container)
    }

    override fun singleValueContainer(): SingleValueEncodingContainer {
        if ((this.object_ != null) || (this.array != null)) {
            preconditionFailure()
        }
        val container = JSONSingleValueEncodingContainer(impl = this, codingPath = this.codingPath)
        return SingleValueEncodingContainer(container)
    }

    override val impl: JSONEncoderImpl
        get() = this

    internal open fun wrapUntyped(encodable: Encodable): JSONValue {
        when (encodable) {
            is Date -> {
                val date = encodable.sref()
                return this.wrapDate(date, for_ = null)
            }
            is Data -> {
                val data = encodable.sref()
                return this.wrapData(data, for_ = null)
            }
            is URL -> {
                val url = encodable.sref()
                return JSONValue.string(url.absoluteString)
            }
            else -> {
                encodable.encode(to = this)
                return this.value ?: JSONValue.object_(dictionaryOf())
            }
        }
    }
}

private interface _SpecialTreatmentEncoder {
    val codingPath: Array<CodingKey>
    val options: JSONEncoder._Options
    val impl: JSONEncoderImpl

    fun wrapFloat(float: Any, for_: CodingKey?): JSONValue {
        val additionalKey = for_

        var string = float.description
        if (string.hasSuffix(".0")) {
            string = String(string.dropLast(2))
        }
        return JSONValue.number(string)
    }

    fun <E> wrapEncodable(encodable: E, for_: CodingKey?): JSONValue? where E: Any {
        val additionalKey = for_
        when (encodable) {
            is Date -> {
                val date = encodable.sref()
                return this.wrapDate(date, for_ = additionalKey)
            }
            is Data -> {
                val data = encodable.sref()
                return this.wrapData(data, for_ = additionalKey)
            }
            is URL -> {
                val url = encodable.sref()
                return JSONValue.string(url.absoluteString)
            }
            else -> {
                val encoder = this.getEncoder(for_ = additionalKey)
                (encodable as Encodable).encode(encoder)
                return encoder.value
            }
        }
    }

    fun wrapDate(date: Date, for_: CodingKey?): JSONValue {
        val additionalKey = for_
        val matchtarget_2 = this.options.dateEncodingStrategy
        when (matchtarget_2) {
            is JSONEncoder.DateEncodingStrategy.DeferredToDateCase -> {
                val encoder = this.getEncoder(for_ = additionalKey)
                date.encode(to = encoder)
                return encoder.value ?: JSONValue.null_
            }
            is JSONEncoder.DateEncodingStrategy.SecondsSince1970Case -> return JSONValue.number(Long(date.timeIntervalSince1970).description)
            is JSONEncoder.DateEncodingStrategy.MillisecondsSince1970Case -> return JSONValue.number((Long(date.timeIntervalSince1970) * 1000).description)
            is JSONEncoder.DateEncodingStrategy.Iso8601Case -> return JSONValue.string(_iso8601Formatter.string(from = date))
            is JSONEncoder.DateEncodingStrategy.FormattedCase -> {
                val formatter = matchtarget_2.associated0
                return JSONValue.string(formatter.string(from = date))
            }
            is JSONEncoder.DateEncodingStrategy.CustomCase -> {
                val closure = matchtarget_2.associated0
                val encoder = this.getEncoder(for_ = additionalKey)
                closure(date, encoder)
                return encoder.value ?: JSONValue.object_(dictionaryOf())
            }
        }
    }

    fun wrapData(data: Data, for_: CodingKey?): JSONValue {
        val additionalKey = for_
        val matchtarget_3 = this.options.dataEncodingStrategy
        when (matchtarget_3) {
            is JSONEncoder.DataEncodingStrategy.DeferredToDataCase -> {
                val encoder = this.getEncoder(for_ = additionalKey)
                data.encode(to = encoder)
                return encoder.value ?: JSONValue.null_
            }
            is JSONEncoder.DataEncodingStrategy.Base64Case -> {
                val base64 = data.base64EncodedString()
                return JSONValue.string(base64)
            }
            is JSONEncoder.DataEncodingStrategy.CustomCase -> {
                val closure = matchtarget_3.associated0
                val encoder = this.getEncoder(for_ = additionalKey)
                closure(data, encoder)
                return encoder.value ?: JSONValue.object_(dictionaryOf())
            }
        }
    }

    fun getEncoder(for_: CodingKey?): JSONEncoderImpl {
        val additionalKey = for_
        if (additionalKey != null) {
            var newCodingPath = this.codingPath.sref()
            newCodingPath.append(additionalKey)
            return JSONEncoderImpl(options = this.options, codingPath = newCodingPath)
        }
        return this.impl
    }
}

internal typealias JSONEncoderKey = CodingKey

private class JSONKeyedEncodingContainer<Key>: KeyedEncodingContainerProtocol<CodingKey>, _SpecialTreatmentEncoder, MutableStruct where Key: CodingKey {

    override val impl: JSONEncoderImpl
    internal val object_: JSONFuture.RefObject
    override val codingPath: Array<CodingKey>
    internal val encodeKeys: Boolean

    private var firstValueWritten: Boolean = false
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    override val options: JSONEncoder._Options
        get() = this.impl.options

    internal constructor(keyedBy: KClass<*>, impl: JSONEncoderImpl, codingPath: Array<CodingKey>) {
        this.impl = impl
        this.object_ = impl.object_!!
        this.codingPath = codingPath.sref()
        this.encodeKeys = keyedBy == DictionaryCodingKey::class
    }

    internal constructor(keyedBy: KClass<*>, impl: JSONEncoderImpl, object_: JSONFuture.RefObject, codingPath: Array<CodingKey>) {
        this.impl = impl
        this.object_ = object_
        this.codingPath = codingPath.sref()
        this.encodeKeys = keyedBy == DictionaryCodingKey::class
    }

    private fun _converted(key: CodingKey): CodingKey {
        if (!encodeKeys) {
            return key.sref()
        }
        val matchtarget_4 = this.options.keyEncodingStrategy
        when (matchtarget_4) {
            is JSONEncoder.KeyEncodingStrategy.UseDefaultKeysCase -> return key.sref()
            is JSONEncoder.KeyEncodingStrategy.ConvertToSnakeCaseCase -> {
                val newKeyString = JSONEncoder.KeyEncodingStrategy._convertToSnakeCase(key.stringValue)
                return _JSONKey(stringValue = newKeyString, intValue = key.intValue)
            }
            is JSONEncoder.KeyEncodingStrategy.CustomCase -> {
                val converter = matchtarget_4.associated0
                return converter(codingPath + arrayOf(key))
            }
        }
    }

    override fun encodeNil(forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            this.object_.set(JSONValue.null_, for_ = this._converted(key).stringValue)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Boolean, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            this.object_.set(JSONValue.bool(value), for_ = this._converted(key).stringValue)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: String, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            this.object_.set(JSONValue.string(value), for_ = this._converted(key).stringValue)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Double, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFloatingPoint(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Float, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFloatingPoint(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Byte, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Short, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Int, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Long, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UByte, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UShort, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UInt, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: ULong, forKey: CodingKey) {
        val key = forKey
        willmutate()
        try {
            encodeFixedWidthInteger(value, key = this._converted(key))
        } finally {
            didmutate()
        }
    }

    override fun <T> encode(value: T?, forKey: CodingKey) where T: Any {
        val key = forKey
        willmutate()
        try {
            val convertedKey = this._converted(key)
            if (value == null) {
                this.object_.set(JSONValue.null_, for_ = convertedKey.stringValue)
            } else {
                val encoded = this.wrapEncodable(value, for_ = convertedKey)
                this.object_.set(encoded ?: JSONValue.object_(dictionaryOf()), for_ = convertedKey.stringValue)
            }
        } finally {
            didmutate()
        }
    }

    override fun <NestedKey> nestedContainer(keyedBy: KClass<NestedKey>, forKey: CodingKey): KeyedEncodingContainer<NestedKey> where NestedKey: CodingKey {
        val nestedKeyType = keyedBy
        val key = forKey
        willmutate()
        try {
            val convertedKey = this._converted(key)
            val newPath = (this.codingPath + arrayOf(convertedKey)).sref()
            val object_ = this.object_.setObject(for_ = convertedKey.stringValue)
            val nestedContainer = JSONKeyedEncodingContainer<NestedKey>(keyedBy = nestedKeyType, impl = impl, object_ = object_, codingPath = newPath)
            return KeyedEncodingContainer(nestedContainer)
        } finally {
            didmutate()
        }
    }

    override fun nestedUnkeyedContainer(forKey: CodingKey): UnkeyedEncodingContainer {
        val key = forKey
        willmutate()
        try {
            val convertedKey = this._converted(key)
            val newPath = (this.codingPath + arrayOf(convertedKey)).sref()
            val array = this.object_.setArray(for_ = convertedKey.stringValue)
            val nestedContainer = JSONUnkeyedEncodingContainer(impl = impl, array = array, codingPath = newPath)
            return UnkeyedEncodingContainer(nestedContainer)
        } finally {
            didmutate()
        }
    }

    override fun superEncoder(): Encoder {
        willmutate()
        try {
            val newEncoder: JSONEncoderImpl = this.getEncoder(for_ = _JSONKey._super)
            this.object_.set(newEncoder, for_ = _JSONKey._super.stringValue)
            return newEncoder
        } finally {
            didmutate()
        }
    }

    override fun superEncoder(forKey: CodingKey): Encoder {
        val key = forKey
        willmutate()
        try {
            val convertedKey = this._converted(key)
            val newEncoder = this.getEncoder(for_ = convertedKey)
            this.object_.set(newEncoder, for_ = convertedKey.stringValue)
            return newEncoder
        } finally {
            didmutate()
        }
    }

    private fun encodeFloatingPoint(float: Any, key: CodingKey) {
        willmutate()
        try {
            val value = this.wrapFloat(float, for_ = key)
            this.object_.set(value, for_ = key.stringValue)
        } finally {
            didmutate()
        }
    }

    private fun encodeFixedWidthInteger(value: Any, key: CodingKey) {
        willmutate()
        try {
            this.object_.set(JSONValue.number(value.description), for_ = key.stringValue)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONKeyedEncodingContainer<Key>
        this.impl = copy.impl
        this.object_ = copy.object_
        this.codingPath = copy.codingPath
        this.encodeKeys = copy.encodeKeys
        this.firstValueWritten = copy.firstValueWritten
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = JSONKeyedEncodingContainer<Key>(this as MutableStruct)
}

private class JSONUnkeyedEncodingContainer: UnkeyedEncodingContainerProtocol, _SpecialTreatmentEncoder, MutableStruct {
    override val impl: JSONEncoderImpl
    internal val array: JSONFuture.RefArray
    override val codingPath: Array<CodingKey>

    override val count: Int
        get() = this.array.array.count
    private var firstValueWritten: Boolean = false
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    override val options: JSONEncoder._Options
        get() = this.impl.options

    internal constructor(impl: JSONEncoderImpl, codingPath: Array<CodingKey>) {
        this.impl = impl
        this.array = impl.array!!
        this.codingPath = codingPath.sref()
    }

    internal constructor(impl: JSONEncoderImpl, array: JSONFuture.RefArray, codingPath: Array<CodingKey>) {
        this.impl = impl
        this.array = array
        this.codingPath = codingPath.sref()
    }

    override fun encodeNil() {
        willmutate()
        try {
            this.array.append(JSONValue.null_)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Boolean) {
        willmutate()
        try {
            this.array.append(JSONValue.bool(value))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: String) {
        willmutate()
        try {
            this.array.append(JSONValue.string(value))
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Double) {
        willmutate()
        try {
            encodeFloatingPoint(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Float) {
        willmutate()
        try {
            encodeFloatingPoint(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Byte) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Short) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Int) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Long) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UByte) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UShort) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UInt) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: ULong) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun <T> encode(value: T) where T: Any {
        willmutate()
        try {
            val key = _JSONKey(stringValue = "Index ${this.count}", intValue = this.count)
            val encoded = this.wrapEncodable(value, for_ = key)
            this.array.append(encoded ?: JSONValue.object_(dictionaryOf()))
        } finally {
            didmutate()
        }
    }

    override fun <NestedKey> nestedContainer(keyedBy: KClass<NestedKey>): KeyedEncodingContainer<NestedKey> where NestedKey: CodingKey {
        val nestedKeyType = keyedBy
        willmutate()
        try {
            val newPath = (this.codingPath + arrayOf(_JSONKey(index = this.count))).sref()
            val object_ = this.array.appendObject()
            val nestedContainer = JSONKeyedEncodingContainer<NestedKey>(keyedBy = nestedKeyType, impl = impl, object_ = object_, codingPath = newPath)
            return KeyedEncodingContainer(nestedContainer)
        } finally {
            didmutate()
        }
    }

    override fun nestedUnkeyedContainer(): UnkeyedEncodingContainer {
        willmutate()
        try {
            val newPath = (this.codingPath + arrayOf(_JSONKey(index = this.count))).sref()
            val array = this.array.appendArray()
            val nestedContainer = JSONUnkeyedEncodingContainer(impl = impl, array = array, codingPath = newPath)
            return UnkeyedEncodingContainer(nestedContainer)
        } finally {
            didmutate()
        }
    }

    override fun superEncoder(): Encoder {
        willmutate()
        try {
            val encoder: JSONEncoderImpl = this.getEncoder(for_ = _JSONKey(index = this.count))
            this.array.append(encoder)
            return encoder
        } finally {
            didmutate()
        }
    }

    private fun encodeFixedWidthInteger(value: Any) {
        willmutate()
        try {
            this.array.append(JSONValue.number(value.description))
        } finally {
            didmutate()
        }
    }

    private fun encodeFloatingPoint(float: Any) {
        willmutate()
        try {
            val value: JSONValue = this.wrapFloat(float, for_ = _JSONKey(index = this.count))
            this.array.append(value)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONUnkeyedEncodingContainer
        this.impl = copy.impl
        this.array = copy.array
        this.codingPath = copy.codingPath
        this.firstValueWritten = copy.firstValueWritten
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = JSONUnkeyedEncodingContainer(this as MutableStruct)
}

private class JSONSingleValueEncodingContainer: SingleValueEncodingContainerProtocol, _SpecialTreatmentEncoder, MutableStruct {
    override val impl: JSONEncoderImpl
    override val codingPath: Array<CodingKey>

    private var firstValueWritten: Boolean = false
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    override val options: JSONEncoder._Options
        get() = this.impl.options

    internal constructor(impl: JSONEncoderImpl, codingPath: Array<CodingKey>) {
        this.impl = impl
        this.codingPath = codingPath.sref()
    }

    override fun encodeNil() {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            this.impl.singleValue = JSONValue.null_
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Boolean) {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            this.impl.singleValue = JSONValue.bool(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Byte) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Short) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Int) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Long) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UByte) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UShort) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: UInt) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: ULong) {
        willmutate()
        try {
            encodeFixedWidthInteger(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Float) {
        willmutate()
        try {
            encodeFloatingPoint(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: Double) {
        willmutate()
        try {
            encodeFloatingPoint(value)
        } finally {
            didmutate()
        }
    }

    override fun encode(value: String) {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            this.impl.singleValue = JSONValue.string(value)
        } finally {
            didmutate()
        }
    }

    override fun <T> encode(value: T) where T: Any {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            this.impl.singleValue = this.wrapEncodable(value, for_ = null)
        } finally {
            didmutate()
        }
    }

    override fun <NestedKey> nestedContainer(keyedBy: KClass<NestedKey>): KeyedEncodingContainer<NestedKey> where NestedKey: CodingKey {
        val nestedKeyType = keyedBy
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            return impl.container(keyedBy = nestedKeyType)
        } finally {
            didmutate()
        }
    }

    override fun nestedUnkeyedContainer(): UnkeyedEncodingContainer {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            return impl.unkeyedContainer()
        } finally {
            didmutate()
        }
    }

    internal fun preconditionCanEncodeNewValue(): Unit = precondition(this.impl.singleValue == null, "Attempt to encode value through single value container when previously value already encoded.")

    private fun encodeFixedWidthInteger(value: Any) {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            this.impl.singleValue = JSONValue.number(value.description)
        } finally {
            didmutate()
        }
    }

    private fun encodeFloatingPoint(float: Any) {
        willmutate()
        try {
            this.preconditionCanEncodeNewValue()
            val value = this.wrapFloat(float, for_ = null)
            this.impl.singleValue = value
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONSingleValueEncodingContainer
        this.impl = copy.impl
        this.codingPath = copy.codingPath
        this.firstValueWritten = copy.firstValueWritten
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = JSONSingleValueEncodingContainer(this as MutableStruct)
}



internal class _JSONKey: CodingKey {
    override val stringValue: String
    override val intValue: Int?

    constructor(stringValue: String) {
        this.stringValue = stringValue
        this.intValue = null
    }

    constructor(intValue: Int) {
        this.stringValue = "${intValue}"
        this.intValue = intValue
    }

    constructor(stringValue: String, intValue: Int?) {
        this.stringValue = stringValue
        this.intValue = intValue
    }

    internal constructor(index: Int, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.stringValue = "Index ${index}"
        this.intValue = index
    }

    override val rawValue: String
        get() = stringValue

    companion object {

        internal val _super = _JSONKey(stringValue = "super")
    }
}

internal var _iso8601Formatter: DateFormatter = linvoke l@{ ->
    val formatter = ISO8601DateFormatter()
    //    formatter.formatOptions = ISO8601DateFormatter.Options.withInternetDateTime
    return@l formatter
}

