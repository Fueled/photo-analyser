// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


/// e.g.: `String(localized: "Done", table: nil, bundle: Bundle.module, locale: Locale(identifier: "en"), comment: nil)`
fun String(localized: StringLocalizationValue, table: String? = null, bundle: Bundle? = null, locale: Locale = Locale.current, comment: String? = null): String {
    val keyAndValue = localized
    val key = keyAndValue.patternFormat // interpolated string: "Hello \(name)" keyed as: "Hello %@"
    val localized = bundle?.localizedString(forKey = key, value = null, table = table) ?: key

    // re-interpret the placeholder strings in the resulting localized string with the string interpolation's values
    val replaced = String(format = localized, keyAndValue.stringInterpolation.values)
    return replaced
}

fun String(localized: String, table: String? = null, bundle: Bundle? = null, locale: Locale = Locale.current, comment: String? = null): String {
    val key = localized
    return bundle?.localizedString(forKey = key, value = null, table = table) ?: key
}

class StringLocalizationValue: ExpressibleByStringInterpolation<StringLocalizationValue.ValueStringInterpolation>, MutableStruct {

    internal var stringInterpolation: StringLocalizationValue.ValueStringInterpolation
        get() = field.sref({ this.stringInterpolation = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(value: String) {
        var interp = StringLocalizationValue.ValueStringInterpolation(literalCapacity = 0, interpolationCount = 0)
        interp.appendLiteral(value)
        this.stringInterpolation = interp
    }

    constructor(stringLiteral: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        val value = stringLiteral
        var interp = StringLocalizationValue.ValueStringInterpolation(literalCapacity = 0, interpolationCount = 0)
        interp.appendLiteral(value)
        this.stringInterpolation = interp
    }

    constructor(stringInterpolation: StringLocalizationValue.ValueStringInterpolation) {
        this.stringInterpolation = stringInterpolation
    }

    /// Returns the pattern string to use for looking up localized values in the `.xcstrings` file
    val patternFormat: String
        get() = stringInterpolation.pattern.joined(separator = "")

    class ValueStringInterpolation: StringInterpolationProtocol, MutableStruct {

        internal var values: Array<Any> = arrayOf()
            get() = field.sref({ this.values = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        internal var pattern: Array<String> = arrayOf()
            get() = field.sref({ this.pattern = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(literalCapacity: Int, interpolationCount: Int) {
        }

        override fun appendLiteral(literal: String) {
            willmutate()
            try {
                pattern.append(literal)
            } finally {
                didmutate()
            }
        }

        fun appendInterpolation(string: String) {
            willmutate()
            try {
                values.append(string)
                pattern.append("%@")
            } finally {
                didmutate()
            }
        }

        override fun <T> appendInterpolation(value: T) {
            willmutate()
            try {
                values.append(value as Any)
                when (value) {
                    is Int -> pattern.append("%lld")
                    is Short -> pattern.append("%d")
                    is Long -> pattern.append("%lld")
                    is UInt -> pattern.append("%llu")
                    is UShort -> pattern.append("%u")
                    is ULong -> pattern.append("%llu")
                    is Double -> pattern.append("%lf")
                    is Float -> pattern.append("%f")
                    else -> pattern.append("%@")
                }
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as StringLocalizationValue.ValueStringInterpolation
            this.values = copy.values
            this.pattern = copy.pattern
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = StringLocalizationValue.ValueStringInterpolation(this as MutableStruct)

        companion object {
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as StringLocalizationValue
        this.stringInterpolation = copy.stringInterpolation
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = StringLocalizationValue(this as MutableStruct)

    companion object {
    }
}

