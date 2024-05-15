// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import skip.foundation.*

class LocalizedStringKey: ExpressibleByStringInterpolation<LocalizedStringKey.StringInterpolation>, MutableStruct {

    internal var stringInterpolation: LocalizedStringKey.StringInterpolation
        get() = field.sref({ this.stringInterpolation = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }


    constructor(stringLiteral: String) {
        val value = stringLiteral
        var interp = LocalizedStringKey.StringInterpolation(literalCapacity = 0, interpolationCount = 0)
        interp.appendLiteral(value)
        this.stringInterpolation = interp
    }

    constructor(stringInterpolation: LocalizedStringKey.StringInterpolation) {
        this.stringInterpolation = stringInterpolation
    }

    /// Returns the pattern string to use for looking up localized values in the `.xcstrings` file
    val patternFormat: String
        get() = stringInterpolation.pattern.joined(separator = "")


    class StringInterpolation: StringInterpolationProtocol, MutableStruct {

        internal var values: Array<AnyHashable> = arrayOf()
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
                values.append(value as AnyHashable)
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

        //public mutating func appendInterpolation(_ string: String) { fatalError() }
        //public mutating func appendInterpolation<Subject>(_ subject: Subject, formatter: Formatter? = nil) where Subject : ReferenceConvertible { fatalError() }
        //public mutating func appendInterpolation<Subject>(_ subject: Subject, formatter: Formatter? = nil) where Subject : NSObject { fatalError() }
        //public mutating func appendInterpolation<F>(_ input: F.FormatInput, format: F) where F : FormatStyle, F.FormatInput : Equatable, F.FormatOutput == String { fatalError() }
        //public mutating func appendInterpolation<T>(_ value: T) where T : _FormatSpecifiable { fatalError() }
        //public mutating func appendInterpolation<T>(_ value: T, specifier: String) where T : _FormatSpecifiable { fatalError() }
        //public mutating func appendInterpolation(_ text: Text) { fatalError() }
        //public mutating func appendInterpolation(_ attributedString: AttributedString) { fatalError() }
        //public mutating func appendInterpolation(_ image: Image) { fatalError() }
        //public mutating func appendInterpolation(_ date: Date, style: Text.DateStyle) { fatalError() }
        //public mutating func appendInterpolation(_ dates: ClosedRange<Date>) { fatalError() }
        //public mutating func appendInterpolation(_ interval: DateInterval) { fatalError() }
        //public mutating func appendInterpolation(timerInterval: ClosedRange<Date>, pauseTime: Date? = nil, countsDown: Bool = true, showsHours: Bool = true) { fatalError() }
        //public mutating func appendInterpolation(_ resource: LocalizedStringResource) { fatalError() }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as LocalizedStringKey.StringInterpolation
            this.values = copy.values
            this.pattern = copy.pattern
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = LocalizedStringKey.StringInterpolation(this as MutableStruct)

        override fun equals(other: Any?): Boolean {
            if (other !is LocalizedStringKey.StringInterpolation) return false
            return values == other.values && pattern == other.pattern
        }

        companion object {
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as LocalizedStringKey
        this.stringInterpolation = copy.stringInterpolation
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = LocalizedStringKey(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is LocalizedStringKey) return false
        return stringInterpolation == other.stringInterpolation
    }

    companion object {
    }
}
