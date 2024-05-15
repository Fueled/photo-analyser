// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


internal open class JSONParser {
    internal open var reader: org.json.JSONTokener
        get() = field.sref({ this.reader = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal constructor(bytes: Array<UByte>) {
        this.reader = org.json.JSONTokener(String(data = Data(bytes), encoding = StringEncoding.utf8) ?: "")
    }

    open fun parseJSONValue(): JSONValue {
        return createJSONValue(from = reader.nextValue())
    }

    open fun parseSwiftValue(): Any {
        return createSwiftValue(from = reader.nextValue())
    }

    /// https://developer.android.com/reference/org/json/JSONTokener#nextValue()
    /// a JSONObject, JSONArray, String, Boolean, Integer, Long, Double or JSONObject#NULL.
    internal open fun createSwiftValue(from: Any): Any {
        val token = from
        if (token === null || token === org.json.JSONObject.NULL) {
            return NSNull.null_
        } else {
            when (token) {
                is java.math.BigDecimal -> {
                    val bd = token.sref()
                    val matchtarget_0 = Double(bd.toString())
                    if (matchtarget_0 != null) {
                        val dbl = matchtarget_0
                        return dbl.sref()
                    } else {
                        throw JSONError.numberIsNotRepresentableInSwift(parsed = bd.toString())
                    }
                }
                is org.json.JSONObject -> {
                    val obj = token.sref()
                    var dict = Dictionary<String, Any>()
                    for (key in obj.keys()) {
                        dict[key] = createSwiftValue(from = obj.get(key))
                    }
                    return dict.sref()
                }
                is org.json.JSONArray -> {
                    val arr = token.sref()
                    var array = Array<Any>()
                    for (i in 0 until arr.length()) {
                        array.append(createSwiftValue(from = arr.get(i)))
                    }
                    return array.sref()
                }
                else -> return token.sref()
            }
        }
    }

    internal open fun createJSONValue(from: Any): JSONValue {
        val token = from
        if (token === null || token === org.json.JSONObject.NULL) {
            return JSONValue.null_
        } else {
            when (token) {
                is String -> {
                    val str = token
                    return JSONValue.string(str)
                }
                is Long -> {
                    val lng = token.sref()
                    return JSONValue.number(lng.toString())
                }
                is Integer -> {
                    val int = token.sref()
                    return JSONValue.number(int.toString())
                }
                is Double -> {
                    val dbl = token
                    return JSONValue.number(dbl.toString())
                }
                is java.math.BigDecimal -> {
                    val bd = token.sref()
                    return JSONValue.number(bd.toString())
                }
                is Boolean -> {
                    val bol = token.sref()
                    return JSONValue.bool(bol)
                }
                is org.json.JSONObject -> {
                    val obj = token.sref()
                    var dict = Dictionary<String, JSONValue>()
                    for (key in obj.keys()) {
                        dict[key] = createJSONValue(from = obj.get(key))
                    }
                    return JSONValue.object_(dict)
                }
                is org.json.JSONArray -> {
                    val arr = token.sref()
                    var array = Array<JSONValue>()
                    for (i in 0 until arr.length()) {
                        array.append(createJSONValue(from = arr.get(i)))
                    }
                    return JSONValue.array(array)
                }
                else -> {
                    fatalError("Unhandled JSON type: ${type(of = token)}")
                }
            }
        }
    }
}

/// Mimics the constructor `UInt8(ascii:)`
internal fun UByte(ascii: String): UByte = ascii.first().toByte().toUByte()

/// Mimics the constructor `UInt8(ascii:)`
internal fun UInt8(ascii: String): UByte = ascii.first().toByte().toUByte()

internal val UInt8_space: UByte = UByte(ascii = " ")
internal val UInt8_return: UByte = UByte(ascii = "\r")
internal val UInt8_newline: UByte = UByte(ascii = "\n")
internal val UInt8_tab: UByte = UByte(ascii = "\t")

internal val UInt8_colon: UByte = UByte(ascii = ":")
internal val UInt8_comma: UByte = UByte(ascii = ",")

internal val UInt8_openbrace: UByte = UByte(ascii = "{")
internal val UInt8_closebrace: UByte = UByte(ascii = "}")

internal val UInt8_openbracket: UByte = UByte(ascii = "[")
internal val UInt8_closebracket: UByte = UByte(ascii = "]")

internal val UInt8_quote: UByte = UByte(ascii = "\"")
internal val UInt8_backslash: UByte = UByte(ascii = "\\")

internal val UInt8Array_true: Array<UByte> = arrayOf(UByte(ascii = "t"), UByte(ascii = "r"), UByte(ascii = "u"), UByte(ascii = "e"))
internal val UInt8Array_false: Array<UByte> = arrayOf(UByte(ascii = "f"), UByte(ascii = "a"), UByte(ascii = "l"), UByte(ascii = "s"), UByte(ascii = "e"))
internal val UInt8Array_null: Array<UByte> = arrayOf(UByte(ascii = "n"), UByte(ascii = "u"), UByte(ascii = "l"), UByte(ascii = "l"))

internal sealed class JSONError: Exception(), Error {
    class CannotConvertInputDataToUTF8Case: JSONError() {
        override fun equals(other: Any?): Boolean = other is CannotConvertInputDataToUTF8Case
    }
    class UnexpectedCharacterCase(val associated0: UByte, val associated1: Int): JSONError() {
        val ascii = associated0
        val characterIndex = associated1

        override fun equals(other: Any?): Boolean {
            if (other !is UnexpectedCharacterCase) return false
            return associated0 == other.associated0 && associated1 == other.associated1
        }
    }
    class UnexpectedEndOfFileCase: JSONError() {
        override fun equals(other: Any?): Boolean = other is UnexpectedEndOfFileCase
    }
    class TooManyNestedArraysOrDictionariesCase(val associated0: Int): JSONError() {
        val characterIndex = associated0

        override fun equals(other: Any?): Boolean {
            if (other !is TooManyNestedArraysOrDictionariesCase) return false
            return associated0 == other.associated0
        }
    }
    class InvalidHexDigitSequenceCase(val associated0: String, val associated1: Int): JSONError() {
        val index = associated1

        override fun equals(other: Any?): Boolean {
            if (other !is InvalidHexDigitSequenceCase) return false
            return associated0 == other.associated0 && associated1 == other.associated1
        }
    }
    class UnexpectedEscapedCharacterCase(val associated0: UByte, val associated1: String, val associated2: Int): JSONError() {
        val ascii = associated0
        val in_ = associated1
        val index = associated2

        override fun equals(other: Any?): Boolean {
            if (other !is UnexpectedEscapedCharacterCase) return false
            return associated0 == other.associated0 && associated1 == other.associated1 && associated2 == other.associated2
        }
    }
    class UnescapedControlCharacterInStringCase(val associated0: UByte, val associated1: String, val associated2: Int): JSONError() {
        val ascii = associated0
        val in_ = associated1
        val index = associated2

        override fun equals(other: Any?): Boolean {
            if (other !is UnescapedControlCharacterInStringCase) return false
            return associated0 == other.associated0 && associated1 == other.associated1 && associated2 == other.associated2
        }
    }
    class ExpectedLowSurrogateUTF8SequenceAfterHighSurrogateCase(val associated0: String, val associated1: Int): JSONError() {
        val in_ = associated0
        val index = associated1

        override fun equals(other: Any?): Boolean {
            if (other !is ExpectedLowSurrogateUTF8SequenceAfterHighSurrogateCase) return false
            return associated0 == other.associated0 && associated1 == other.associated1
        }
    }
    class CouldNotCreateUnicodeScalarFromUInt32Case(val associated0: String, val associated1: Int, val associated2: UInt): JSONError() {
        val in_ = associated0
        val index = associated1
        val unicodeScalarValue = associated2

        override fun equals(other: Any?): Boolean {
            if (other !is CouldNotCreateUnicodeScalarFromUInt32Case) return false
            return associated0 == other.associated0 && associated1 == other.associated1 && associated2 == other.associated2
        }
    }
    class NumberWithLeadingZeroCase(val associated0: Int): JSONError() {
        val index = associated0

        override fun equals(other: Any?): Boolean {
            if (other !is NumberWithLeadingZeroCase) return false
            return associated0 == other.associated0
        }
    }
    class NumberIsNotRepresentableInSwiftCase(val associated0: String): JSONError() {
        val parsed = associated0

        override fun equals(other: Any?): Boolean {
            if (other !is NumberIsNotRepresentableInSwiftCase) return false
            return associated0 == other.associated0
        }
    }
    class SingleFragmentFoundButNotAllowedCase: JSONError() {
        override fun equals(other: Any?): Boolean = other is SingleFragmentFoundButNotAllowedCase
    }
    class InvalidUTF8SequenceCase(val associated0: Data, val associated1: Int): JSONError() {
        val characterIndex = associated1

        override fun equals(other: Any?): Boolean {
            if (other !is InvalidUTF8SequenceCase) return false
            return associated0 == other.associated0 && associated1 == other.associated1
        }
    }

    companion object {
        val cannotConvertInputDataToUTF8: JSONError
            get() = CannotConvertInputDataToUTF8Case()
        fun unexpectedCharacter(ascii: UByte, characterIndex: Int): JSONError = UnexpectedCharacterCase(ascii, characterIndex)
        val unexpectedEndOfFile: JSONError
            get() = UnexpectedEndOfFileCase()
        fun tooManyNestedArraysOrDictionaries(characterIndex: Int): JSONError = TooManyNestedArraysOrDictionariesCase(characterIndex)
        fun invalidHexDigitSequence(associated0: String, index: Int): JSONError = InvalidHexDigitSequenceCase(associated0, index)
        fun unexpectedEscapedCharacter(ascii: UByte, in_: String, index: Int): JSONError = UnexpectedEscapedCharacterCase(ascii, in_, index)
        fun unescapedControlCharacterInString(ascii: UByte, in_: String, index: Int): JSONError = UnescapedControlCharacterInStringCase(ascii, in_, index)
        fun expectedLowSurrogateUTF8SequenceAfterHighSurrogate(in_: String, index: Int): JSONError = ExpectedLowSurrogateUTF8SequenceAfterHighSurrogateCase(in_, index)
        fun couldNotCreateUnicodeScalarFromUInt32(in_: String, index: Int, unicodeScalarValue: UInt): JSONError = CouldNotCreateUnicodeScalarFromUInt32Case(in_, index, unicodeScalarValue)
        fun numberWithLeadingZero(index: Int): JSONError = NumberWithLeadingZeroCase(index)
        fun numberIsNotRepresentableInSwift(parsed: String): JSONError = NumberIsNotRepresentableInSwiftCase(parsed)
        val singleFragmentFoundButNotAllowed: JSONError
            get() = SingleFragmentFoundButNotAllowedCase()
        fun invalidUTF8Sequence(associated0: Data, characterIndex: Int): JSONError = InvalidUTF8SequenceCase(associated0, characterIndex)
    }
}

