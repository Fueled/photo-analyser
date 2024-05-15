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

import kotlin.reflect.full.*

internal typealias JSONDecoderValue = Any

open class JSONDecoder: TopLevelDecoder<Data> {
    sealed class DateDecodingStrategy {
        class DeferredToDateCase: DateDecodingStrategy() {
        }
        class SecondsSince1970Case: DateDecodingStrategy() {
        }
        class MillisecondsSince1970Case: DateDecodingStrategy() {
        }
        class Iso8601Case: DateDecodingStrategy() {
        }
        class FormattedCase(val associated0: DateFormatter): DateDecodingStrategy() {
        }
        class CustomCase(val associated0: (Decoder) -> Date): DateDecodingStrategy() {
        }

        companion object {
            val deferredToDate: DateDecodingStrategy = DeferredToDateCase()
            val secondsSince1970: DateDecodingStrategy = SecondsSince1970Case()
            val millisecondsSince1970: DateDecodingStrategy = MillisecondsSince1970Case()
            val iso8601: DateDecodingStrategy = Iso8601Case()
            fun formatted(associated0: DateFormatter): DateDecodingStrategy = FormattedCase(associated0)
            fun custom(associated0: (Decoder) -> Date): DateDecodingStrategy = CustomCase(associated0)
        }
    }

    sealed class DataDecodingStrategy {
        class DeferredToDataCase: DataDecodingStrategy() {
        }
        class Base64Case: DataDecodingStrategy() {
        }
        class CustomCase(val associated0: (Decoder) -> Data): DataDecodingStrategy() {
        }

        companion object {
            val deferredToData: DataDecodingStrategy = DeferredToDataCase()
            val base64: DataDecodingStrategy = Base64Case()
            fun custom(associated0: (Decoder) -> Data): DataDecodingStrategy = CustomCase(associated0)
        }
    }

    sealed class NonConformingFloatDecodingStrategy {
        class ThrowCase: NonConformingFloatDecodingStrategy() {
        }
        class ConvertFromStringCase(val associated0: String, val associated1: String, val associated2: String): NonConformingFloatDecodingStrategy() {
            val positiveInfinity = associated0
            val negativeInfinity = associated1
            val nan = associated2
        }

        companion object {
            val throw_: NonConformingFloatDecodingStrategy = ThrowCase()
            fun convertFromString(positiveInfinity: String, negativeInfinity: String, nan: String): NonConformingFloatDecodingStrategy = ConvertFromStringCase(positiveInfinity, negativeInfinity, nan)
        }
    }

    sealed class KeyDecodingStrategy {
        class UseDefaultKeysCase: KeyDecodingStrategy() {
        }
        class ConvertFromSnakeCaseCase: KeyDecodingStrategy() {
        }
        class CustomCase(val associated0: (Array<CodingKey>) -> CodingKey): KeyDecodingStrategy() {
        }

        companion object {
            val useDefaultKeys: KeyDecodingStrategy = UseDefaultKeysCase()
            val convertFromSnakeCase: KeyDecodingStrategy = ConvertFromSnakeCaseCase()
            fun custom(associated0: (Array<CodingKey>) -> CodingKey): KeyDecodingStrategy = CustomCase(associated0)


            internal fun _convertFromSnakeCase(key: String): String {
                return fatalError("SKIP TODO: JSON snakeCase")
            }
        }
    }

    open var dateDecodingStrategy: JSONDecoder.DateDecodingStrategy = JSONDecoder.DateDecodingStrategy.deferredToDate
    open var dataDecodingStrategy: JSONDecoder.DataDecodingStrategy = JSONDecoder.DataDecodingStrategy.base64
    open var nonConformingFloatDecodingStrategy: JSONDecoder.NonConformingFloatDecodingStrategy = JSONDecoder.NonConformingFloatDecodingStrategy.throw_
    open var keyDecodingStrategy: JSONDecoder.KeyDecodingStrategy = JSONDecoder.KeyDecodingStrategy.useDefaultKeys
    open var userInfo: Dictionary<CodingUserInfoKey, Any> = dictionaryOf()
        get() = field.sref({ this.userInfo = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal class _Options {
        internal val dateDecodingStrategy: JSONDecoder.DateDecodingStrategy
        internal val dataDecodingStrategy: JSONDecoder.DataDecodingStrategy
        internal val nonConformingFloatDecodingStrategy: JSONDecoder.NonConformingFloatDecodingStrategy
        internal val keyDecodingStrategy: JSONDecoder.KeyDecodingStrategy
        internal val userInfo: Dictionary<CodingUserInfoKey, Any>

        constructor(dateDecodingStrategy: JSONDecoder.DateDecodingStrategy, dataDecodingStrategy: JSONDecoder.DataDecodingStrategy, nonConformingFloatDecodingStrategy: JSONDecoder.NonConformingFloatDecodingStrategy, keyDecodingStrategy: JSONDecoder.KeyDecodingStrategy, userInfo: Dictionary<CodingUserInfoKey, Any>) {
            this.dateDecodingStrategy = dateDecodingStrategy
            this.dataDecodingStrategy = dataDecodingStrategy
            this.nonConformingFloatDecodingStrategy = nonConformingFloatDecodingStrategy
            this.keyDecodingStrategy = keyDecodingStrategy
            this.userInfo = userInfo.sref()
        }
    }

    internal open val options: JSONDecoder._Options
        get() = _Options(dateDecodingStrategy = dateDecodingStrategy, dataDecodingStrategy = dataDecodingStrategy, nonConformingFloatDecodingStrategy = nonConformingFloatDecodingStrategy, keyDecodingStrategy = keyDecodingStrategy, userInfo = userInfo)

    constructor() {
    }

    // Our TopLevelDecoder superclass handles the decode calls. We just have to produce the decoder
    override fun decoder(from: Data): Decoder {
        val data = from
        try {
            var parser = JSONParser(bytes = data.bytes)
            val json = parser.parseSwiftValue()
            return JSONDecoderImpl(userInfo = this.userInfo, from = json, codingPath = arrayOf(), options = this.options)
        } catch (error: JSONError        ) {
            throw DecodingError.dataCorrupted(DecodingError.Context(codingPath = arrayOf(), debugDescription = "The given data was not valid JSON.", underlyingError = error))
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            throw error as Throwable
        }
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

private class JSONDecoderImpl: Decoder {
    override val codingPath: Array<CodingKey>
    override val userInfo: Dictionary<CodingUserInfoKey, Any>
    internal val json: Any
    internal val options: JSONDecoder._Options

    internal constructor(userInfo: Dictionary<CodingUserInfoKey, Any>, from: Any, codingPath: Array<CodingKey>, options: JSONDecoder._Options) {
        val json = from
        this.userInfo = userInfo.sref()
        this.codingPath = codingPath.sref()
        this.json = json.sref()
        this.options = options
    }

    override fun <Key> container(keyedBy: KClass<Key>): KeyedDecodingContainer<Key> where Key: CodingKey {
        val _keyType = keyedBy
        val dictionary: Dictionary<String, Any>?
        val isNull: Boolean
        dictionary = (json as? Dictionary<String, Any>).sref()
        isNull = json is NSNull
        if (dictionary != null) {
            val container = JSONKeyedDecodingContainer<Key>(keyedBy = _keyType, impl = this, codingPath = codingPath, dictionary = dictionary)
            return KeyedDecodingContainer(container)
        } else if (isNull) {
            throw DecodingError.valueNotFound(Dictionary::class, DecodingError.Context(codingPath = this.codingPath, debugDescription = "Cannot get keyed decoding container -- found null value instead"))
        } else {
            throw DecodingError.typeMismatch(Dictionary::class, DecodingError.Context(codingPath = this.codingPath, debugDescription = "Expected to decode Dictionary<String, Any> but found ${this.json} instead."))
        }
    }

    override fun unkeyedContainer(): UnkeyedDecodingContainer {
        val array: Array<Any>?
        val isNull: Boolean
        array = (json as? Array<Any>).sref()
        isNull = json is NSNull
        if (array != null) {
            val container = JSONUnkeyedDecodingContainer(impl = this, codingPath = this.codingPath, array = array)
            return UnkeyedDecodingContainer(container)
        } else if (isNull) {
            throw DecodingError.valueNotFound(Array::class, DecodingError.Context(codingPath = this.codingPath, debugDescription = "Cannot get unkeyed decoding container -- found null value instead"))
        } else {
            throw DecodingError.typeMismatch(Array::class, DecodingError.Context(codingPath = this.codingPath, debugDescription = "Expected to decode ${Array::class} but found ${this.json} instead."))
        }
    }

    override fun singleValueContainer(): SingleValueDecodingContainer {
        val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
        return SingleValueDecodingContainer(container)
    }

    internal fun <T> unwrap(as_: KClass<T>): T where T: Any {
        val type = as_
        if (type == Date::class) {
            return (this.unwrapDate() as T).sref()
        }
        if (type == Data::class) {
            return (this.unwrapData() as T).sref()
        }
        if (type == URL::class) {
            return (this.unwrapURL() as T).sref()
        }

        val decodableCompanion = type.companionObjectInstance as? DecodableCompanion<*>
if (decodableCompanion != null) {
    return decodableCompanion.init(from = this) as T
}

        throw createTypeMismatchError(type = Decodable::class, value = this.json)
    }

    private fun unwrapDate(): Date {
        val matchtarget_0 = this.options.dateDecodingStrategy
        when (matchtarget_0) {
            is JSONDecoder.DateDecodingStrategy.DeferredToDateCase -> {
                return Date(from = this)
            }
            is JSONDecoder.DateDecodingStrategy.SecondsSince1970Case -> {
                val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
                val double = container.decode(Double::class)
                return Date(timeIntervalSince1970 = double)
            }
            is JSONDecoder.DateDecodingStrategy.MillisecondsSince1970Case -> {
                val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
                val double = container.decode(Double::class)
                return Date(timeIntervalSince1970 = double / 1000.0)
            }
            is JSONDecoder.DateDecodingStrategy.Iso8601Case -> {
                val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
                val string = container.decode(String::class)
                val date_0 = _iso8601Formatter.date(from = string)
                if (date_0 == null) {
                    throw DecodingError.dataCorrupted(DecodingError.Context(codingPath = this.codingPath, debugDescription = "Expected date string to be ISO8601-formatted."))
                }
                return date_0.sref()
            }
            is JSONDecoder.DateDecodingStrategy.FormattedCase -> {
                val formatter = matchtarget_0.associated0
                val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
                val string = container.decode(String::class)
                val date_1 = formatter.date(from = string)
                if (date_1 == null) {
                    throw DecodingError.dataCorrupted(DecodingError.Context(codingPath = this.codingPath, debugDescription = "Date string does not match format expected by formatter."))
                }
                return date_1.sref()
            }
            is JSONDecoder.DateDecodingStrategy.CustomCase -> {
                val closure = matchtarget_0.associated0
                return closure(this)
            }
        }
    }

    private fun unwrapData(): Data {
        val matchtarget_1 = this.options.dataDecodingStrategy
        when (matchtarget_1) {
            is JSONDecoder.DataDecodingStrategy.DeferredToDataCase -> {
                return Data(from = this)
            }
            is JSONDecoder.DataDecodingStrategy.Base64Case -> {
                val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
                val string = container.decode(String::class)
                val data_0 = (try { Data(base64Encoded = string) } catch (_: NullReturnException) { null })
                if (data_0 == null) {
                    throw DecodingError.dataCorrupted(DecodingError.Context(codingPath = this.codingPath, debugDescription = "Encountered Data is not valid Base64."))
                }
                return data_0.sref()
            }
            is JSONDecoder.DataDecodingStrategy.CustomCase -> {
                val closure = matchtarget_1.associated0
                return closure(this)
            }
        }
    }

    private fun unwrapURL(): URL {
        val container = JSONSingleValueDecodingContainer(impl = this, codingPath = this.codingPath, json = this.json)
        val string = container.decode(String::class)
        val url_0 = (try { URL(string = string) } catch (_: NullReturnException) { null })
        if (url_0 == null) {
            throw DecodingError.dataCorrupted(DecodingError.Context(codingPath = this.codingPath, debugDescription = "Invalid URL string."))
        }
        return url_0.sref()
    }

    internal fun codingPath(with: CodingKey?): Array<CodingKey> {
        val additionalKey = with
        var path = this.codingPath.sref()
        if (additionalKey != null) {
            path.append(additionalKey)
        }
        return path.sref()
    }

    internal fun createTypeMismatchError(type: KClass<*>, for_: CodingKey? = null, value: Any): DecodingError {
        val additionalKey = for_
        return DecodingError.typeMismatch(type, DecodingError.Context(codingPath = this.codingPath(with = additionalKey), debugDescription = "Expected to decode ${type} but found ${value} instead."))
    }
}

private class JSONSingleValueDecodingContainer: SingleValueDecodingContainerProtocol {
    internal val impl: JSONDecoderImpl
    internal val value: Any
    override val codingPath: Array<CodingKey>

    internal constructor(impl: JSONDecoderImpl, codingPath: Array<CodingKey>, json: Any) {
        this.impl = impl
        this.codingPath = codingPath.sref()
        this.value = json.sref()
    }

    override fun decodeNil(): Boolean = decodeAsNil(value, impl = impl)

    override fun decode(type: KClass<Boolean>): Boolean {
        return decodeAsBool(value, impl = impl)
    }

    override fun decode(type: KClass<String>): String {
        return decodeAsString(value, impl = impl)
    }

    override fun decode(type: KClass<Double>): Double {
        return decodeAsDouble(value, impl = impl)
    }

    override fun decode(type: KClass<Float>): Float {
        return decodeAsFloat(value, impl = impl)
    }

    override fun decode(type: KClass<Byte>): Byte {
        return decodeAsInt8(value, impl = impl)
    }

    override fun decode(type: KClass<Short>): Short {
        return decodeAsInt16(value, impl = impl)
    }

    override fun decode(type: KClass<Int>): Int {
        return decodeAsInt32(value, impl = impl)
    }

    override fun decode(type: KClass<Long>): Long {
        return decodeAsInt64(value, impl = impl)
    }

    override fun decode(type: KClass<UByte>): UByte {
        return decodeAsUInt8(value, impl = impl)
    }

    override fun decode(type: KClass<UShort>): UShort {
        return decodeAsUInt16(value, impl = impl)
    }

    override fun decode(type: KClass<UInt>): UInt {
        return decodeAsUInt32(value, impl = impl)
    }

    override fun decode(type: KClass<ULong>): ULong {
        return decodeAsUInt64(value, impl = impl)
    }

    override fun <T> decode(type: KClass<T>): T where T: Any {
        return this.impl.unwrap(as_ = type)
    }

    override fun <NestedKey> nestedContainer(keyedBy: KClass<NestedKey>): KeyedDecodingContainer<NestedKey> where NestedKey: CodingKey {
        val type = keyedBy
        val decoder = decoderForValue()
        val container = decoder.container(keyedBy = type)
        return container
    }

    override fun nestedUnkeyedContainer(): UnkeyedDecodingContainer {
        val decoder = decoderForValue()
        val container = decoder.unkeyedContainer()
        return container
    }

    private fun decoderForValue(): JSONDecoderImpl = JSONDecoderImpl(userInfo = this.impl.userInfo, from = this.value, codingPath = this.codingPath, options = this.impl.options)
}

internal typealias JSONDecoderKey = CodingKey

private class JSONKeyedDecodingContainer<Key>: KeyedDecodingContainerProtocol<CodingKey> where Key: CodingKey {
    internal val impl: JSONDecoderImpl
    override val codingPath: Array<CodingKey>
    internal val dictionary: Dictionary<String, Any>

    internal constructor(keyedBy: KClass<*>, impl: JSONDecoderImpl, codingPath: Array<CodingKey>, dictionary: Dictionary<String, Any>) {
        this.impl = impl
        this.codingPath = codingPath.sref()
        val decodeKeys = keyedBy == DictionaryCodingKey::class
        if (decodeKeys) {
            val matchtarget_2 = impl.options.keyDecodingStrategy
            when (matchtarget_2) {
                is JSONDecoder.KeyDecodingStrategy.UseDefaultKeysCase -> this.dictionary = dictionary.sref()
                is JSONDecoder.KeyDecodingStrategy.ConvertFromSnakeCaseCase -> {
                    // Convert the snake case keys in the container to camel case.
                    // If we hit a duplicate key after conversion, then we'll use the first one we saw.
                    // Effectively an undefined behavior with JSON dictionaries.
                    var converted = Dictionary<String, Any>()
                    dictionary.forEach { entry -> converted[JSONDecoder.KeyDecodingStrategy._convertFromSnakeCase(entry.key)] = entry.value }
                    this.dictionary = converted.sref()
                }
                is JSONDecoder.KeyDecodingStrategy.CustomCase -> {
                    val converter = matchtarget_2.associated0
                    var converted = Dictionary<String, Any>()
                    for ((key, value) in dictionary.sref()) {
                        var pathForKey = codingPath.sref()
                        pathForKey.append(_JSONKey(stringValue = key))
                        converted[converter(pathForKey).stringValue] = value.sref()
                    }
                    this.dictionary = converted.sref()
                }
            }
        } else {
            this.dictionary = dictionary.sref()
        }
    }

    override val allKeys: Array<CodingKey>
        get() {
            return this.dictionary.keys.compactMap { it -> (try { _JSONKey(stringValue = it) } catch (_: NullReturnException) { null }) }
        }

    override fun contains(key: CodingKey): Boolean {
        dictionary[key.stringValue].sref()?.let { _ ->
            return true
        }
        return false
    }

    override fun decodeNil(forKey: CodingKey): Boolean {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsNil(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Boolean>, forKey: CodingKey): Boolean {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsBool(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<String>, forKey: CodingKey): String {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsString(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Double>, forKey: CodingKey): Double {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsDouble(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Float>, forKey: CodingKey): Float {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsFloat(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Byte>, forKey: CodingKey): Byte {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsInt8(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Short>, forKey: CodingKey): Short {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsInt16(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Int>, forKey: CodingKey): Int {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsInt32(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<Long>, forKey: CodingKey): Long {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsInt64(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<UByte>, forKey: CodingKey): UByte {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsUInt8(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<UShort>, forKey: CodingKey): UShort {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsUInt16(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<UInt>, forKey: CodingKey): UInt {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsUInt32(value, forKey = key, impl = this.impl)
    }

    override fun decode(type: KClass<ULong>, forKey: CodingKey): ULong {
        val key = forKey
        val value = getValue(forKey = key)
        return decodeAsUInt64(value, forKey = key, impl = this.impl)
    }

    override fun <T> decode(type: KClass<T>, forKey: CodingKey): T where T: Any {
        val key = forKey
        val newDecoder = decoderForKey(key)
        return newDecoder.unwrap(as_ = type)
    }

    override fun <NestedKey> nestedContainer(keyedBy: KClass<NestedKey>, forKey: CodingKey): KeyedDecodingContainer<NestedKey> where NestedKey: CodingKey {
        val type = keyedBy
        val key = forKey
        return decoderForKey(key).container(keyedBy = type)
    }

    override fun nestedUnkeyedContainer(forKey: CodingKey): UnkeyedDecodingContainer {
        val key = forKey
        return decoderForKey(key).unkeyedContainer()
    }

    override fun superDecoder(): Decoder = decoderForKeyNoThrow(_JSONKey._super)

    override fun superDecoder(forKey: CodingKey): Decoder {
        val key = forKey
        return decoderForKeyNoThrow(key)
    }

    private inline fun <reified LocalKey> decoderForKey(key: LocalKey): JSONDecoderImpl where LocalKey: CodingKey {
        val value = getValue(forKey = key)
        var newPath = this.codingPath.sref()
        newPath.append(key)

        return JSONDecoderImpl(userInfo = this.impl.userInfo, from = value, codingPath = newPath, options = this.impl.options)
    }

    private inline fun <reified LocalKey> decoderForKeyNoThrow(key: LocalKey): JSONDecoderImpl where LocalKey: CodingKey {
        var value: Any
        try {
            value = getValue(forKey = key)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            // if there no value for this key then return a null value
            value = NSNull.null_
        }
        var newPath = this.codingPath.sref()
        newPath.append(key)

        return JSONDecoderImpl(userInfo = this.impl.userInfo, from = value, codingPath = newPath, options = this.impl.options)
    }

    private fun getValue(forKey: CodingKey): Any {
        val key = forKey
        val value_0 = dictionary[key.stringValue].sref()
        if (value_0 == null) {
            throw DecodingError.keyNotFound(key, DecodingError.Context(codingPath = this.codingPath, debugDescription = "No value associated with key ${key} (\"${key.stringValue}\")."))
        }
        return value_0.sref()
    }
}

private class JSONUnkeyedDecodingContainer: UnkeyedDecodingContainerProtocol, MutableStruct {
    internal val impl: JSONDecoderImpl
    override val codingPath: Array<CodingKey>
    internal val array: Array<Any>

    override val count: Int?
        get() = this.array.count
    override val isAtEnd: Boolean
        get() = this.currentIndex >= (this.count ?: 0)
    override var currentIndex = 0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    internal constructor(impl: JSONDecoderImpl, codingPath: Array<CodingKey>, array: Array<Any>) {
        this.impl = impl
        this.codingPath = codingPath.sref()
        this.array = array.sref()
    }

    override fun decodeNil(): Boolean {
        willmutate()
        try {
            val value = getNextValue(ofType = Never::class)
            if (decodeAsNil(value, impl = this.impl)) {
                this.currentIndex += 1
                return true
            }
            return false
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Boolean>): Boolean {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Boolean::class)
            val ret = decodeAsBool(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<String>): String {
        willmutate()
        try {
            val value = this.getNextValue(ofType = String::class)
            val ret = decodeAsString(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Double>): Double {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Double::class)
            val ret = decodeAsDouble(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Float>): Float {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Float::class)
            val ret = decodeAsFloat(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Byte>): Byte {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Byte::class)
            val ret = decodeAsInt8(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Short>): Short {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Short::class)
            val ret = decodeAsInt16(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Int>): Int {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Int::class)
            val ret = decodeAsInt32(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<Long>): Long {
        willmutate()
        try {
            val value = this.getNextValue(ofType = Long::class)
            val ret = decodeAsInt64(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<UByte>): UByte {
        willmutate()
        try {
            val value = this.getNextValue(ofType = UByte::class)
            val ret = decodeAsUInt8(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<UShort>): UShort {
        willmutate()
        try {
            val value = this.getNextValue(ofType = UShort::class)
            val ret = decodeAsUInt16(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<UInt>): UInt {
        willmutate()
        try {
            val value = this.getNextValue(ofType = UInt::class)
            val ret = decodeAsUInt32(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun decode(type: KClass<ULong>): ULong {
        willmutate()
        try {
            val value = this.getNextValue(ofType = ULong::class)
            val ret = decodeAsUInt64(value, forKey = _JSONKey(index = currentIndex), impl = this.impl)
            this.currentIndex += 1
            return ret
        } finally {
            didmutate()
        }
    }

    override fun <T> decode(type: KClass<T>): T where T: Any {
        willmutate()
        try {
            val newDecoder = decoderForNextElement(ofType = type)
            val result = newDecoder.unwrap(as_ = type)
            this.currentIndex += 1
            return result.sref()
        } finally {
            didmutate()
        }
    }

    override fun <NestedKey> nestedContainer(keyedBy: KClass<NestedKey>): KeyedDecodingContainer<NestedKey> where NestedKey: CodingKey {
        val type = keyedBy
        willmutate()
        try {
            val decoder = decoderForNextElement(ofType = KeyedDecodingContainer::class)
            val container = decoder.container(keyedBy = type)
            this.currentIndex += 1
            return container
        } finally {
            didmutate()
        }
    }

    override fun nestedUnkeyedContainer(): UnkeyedDecodingContainer {
        willmutate()
        try {
            val decoder = decoderForNextElement(ofType = UnkeyedDecodingContainer::class)
            val container = decoder.unkeyedContainer()
            this.currentIndex += 1
            return container
        } finally {
            didmutate()
        }
    }

    override fun superDecoder(): Decoder {
        willmutate()
        try {
            val decoder = decoderForNextElement(ofType = Decoder::class)
            this.currentIndex += 1
            return decoder
        } finally {
            didmutate()
        }
    }

    private fun <T> decoderForNextElement(ofType: KClass<T>): JSONDecoderImpl where T: Any {
        willmutate()
        try {
            val value = this.getNextValue(ofType = ofType)
            val newPath = (this.codingPath + arrayOf(_JSONKey(index = this.currentIndex))).sref()

            return JSONDecoderImpl(userInfo = this.impl.userInfo, from = value, codingPath = newPath, options = this.impl.options)
        } finally {
            didmutate()
        }
    }

    private fun <T> getNextValue(ofType: KClass<T>): Any where T: Any {
        if (this.isAtEnd) {
            var message = "Unkeyed container is at end."
            if (ofType == UnkeyedDecodingContainer::class) {
                message = "Cannot get nested unkeyed container -- unkeyed container is at end."
            }
            if (ofType == Decoder::class) {
                message = "Cannot get superDecoder() -- unkeyed container is at end."
            }

            var path = this.codingPath.sref()
            path.append(_JSONKey(index = this.currentIndex))

            throw DecodingError.valueNotFound(ofType, DecodingError.Context(codingPath = path, debugDescription = message, underlyingError = null))
        }
        return this.array[this.currentIndex].sref()
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as JSONUnkeyedDecodingContainer
        this.impl = copy.impl
        this.codingPath = copy.codingPath
        this.array = copy.array
        this.currentIndex = copy.currentIndex
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = JSONUnkeyedDecodingContainer(this as MutableStruct)
}

private fun decodeAsNil(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Boolean {
    val key = forKey
    return value is NSNull
}

private fun decodeAsBool(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Boolean {
    val key = forKey
    val bool = value as? Boolean
    if (bool == null) {
        throw impl.createTypeMismatchError(type = Boolean::class, for_ = key, value = value)
    }
    return bool
}

private fun decodeAsString(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): String {
    val key = forKey
    val string: String? = if (value is NSNull) null else value.toString()
    if (string == null) {
        throw impl.createTypeMismatchError(type = String::class, value = value)
    }
    return string
}

private fun decodeAsDouble(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Double {
    val key = forKey
    return checkNumericOptional(Double(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsFloat(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Float {
    val key = forKey
    return checkNumericOptional(Float(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsInt(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Int {
    val key = forKey
    return checkNumericOptional(Int(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsInt8(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Byte {
    val key = forKey
    return checkNumericOptional(Byte(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsInt16(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Short {
    val key = forKey
    return checkNumericOptional(Short(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsInt32(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Int {
    val key = forKey
    return checkNumericOptional(Int(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsInt64(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Long {
    val key = forKey
    return checkNumericOptional(Long(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsUInt(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): UInt {
    val key = forKey
    return checkNumericOptional(UInt(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsUInt8(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): UByte {
    val key = forKey
    return checkNumericOptional(UByte(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsUInt16(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): UShort {
    val key = forKey
    return checkNumericOptional(UShort(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsUInt32(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): UInt {
    val key = forKey
    return checkNumericOptional(UInt(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeAsUInt64(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): ULong {
    val key = forKey
    return checkNumericOptional(ULong(decodeNumeric(value, forKey = key, impl = impl)), forKey = key, impl = impl)
}

private fun decodeNumeric(value: Any, forKey: CodingKey? = null, impl: JSONDecoderImpl): Number {
    val key = forKey
    val number_0 = (value as? Number).sref()
    if (number_0 == null) {
        throw DecodingError.typeMismatch(Int::class, DecodingError.Context(codingPath = impl.codingPath(with = key), debugDescription = "Expected to decode number but found ${value} instead."))
    }
    return number_0.sref()
}

private fun <T> checkNumericOptional(value: T?, forKey: CodingKey? = null, impl: JSONDecoderImpl): T {
    val key = forKey
    if (value == null) {
        throw DecodingError.typeMismatch(Int::class, DecodingError.Context(codingPath = impl.codingPath(with = key), debugDescription = "Unable to parse as expected numeric type"))
    }
    return value.sref()
}

