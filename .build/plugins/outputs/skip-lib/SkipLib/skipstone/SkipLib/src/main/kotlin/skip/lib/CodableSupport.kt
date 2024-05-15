// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib

import kotlin.reflect.KClass
import skip.lib.Array


interface CodingKey: CustomDebugStringConvertible, Sendable {
    val rawValue: String
    val stringValue: String
        get() = rawValue

    val intValue: Int?
        get() = Int(rawValue)

    val description: String
        get() = rawValue

    override val debugDescription: String
        get() = rawValue
}

interface CodingKeyRepresentable {
    val codingKey: CodingKey
}

class CodingUserInfoKey: RawRepresentable<String>, Sendable {
    override val rawValue: String
    constructor(rawValue: String) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CodingUserInfoKey) return false
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

sealed class EncodingError: Exception(), Error {

    class InvalidValueCase(val associated0: Any, val associated1: EncodingError.Context): EncodingError() {
    }
    class Context: Sendable {
        val codingPath: Array<CodingKey>
        val debugDescription: String
        val underlyingError: Error?

        constructor(codingPath: Array<CodingKey>, debugDescription: String, underlyingError: Error? = null) {
            this.codingPath = codingPath.sref()
            this.debugDescription = debugDescription
            this.underlyingError = underlyingError.sref()
        }

        companion object {
        }
    }

    companion object {
        fun invalidValue(associated0: Any, associated1: EncodingError.Context): EncodingError = InvalidValueCase(associated0, associated1)
    }
}

sealed class DecodingError: Exception(), Error {

    class TypeMismatchCase(val associated0: KClass<*>, val associated1: DecodingError.Context): DecodingError() {
    }
    class ValueNotFoundCase(val associated0: KClass<*>, val associated1: DecodingError.Context): DecodingError() {
    }
    class KeyNotFoundCase(val associated0: CodingKey, val associated1: DecodingError.Context): DecodingError() {
    }
    class DataCorruptedCase(val associated0: DecodingError.Context): DecodingError() {
    }
    class Context: Sendable {
        val codingPath: Array<CodingKey>
        val debugDescription: String
        val underlyingError: Error?

        constructor(codingPath: Array<CodingKey>, debugDescription: String, underlyingError: Error? = null) {
            this.codingPath = codingPath.sref()
            this.debugDescription = debugDescription
            this.underlyingError = underlyingError.sref()
        }

        companion object {
        }
    }

    companion object {
        fun typeMismatch(associated0: KClass<*>, associated1: DecodingError.Context): DecodingError = TypeMismatchCase(associated0, associated1)
        fun valueNotFound(associated0: KClass<*>, associated1: DecodingError.Context): DecodingError = ValueNotFoundCase(associated0, associated1)
        fun keyNotFound(associated0: CodingKey, associated1: DecodingError.Context): DecodingError = KeyNotFoundCase(associated0, associated1)
        fun dataCorrupted(associated0: DecodingError.Context): DecodingError = DataCorruptedCase(associated0)
    }
}

