// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


open class ISO8601DateFormatter: DateFormatter {
    constructor(): super() {
        this.dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"
        this.timeZone = (try { TimeZone(identifier = "UTC") } catch (_: NullReturnException) { null })
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var formatOptions: ISO8601DateFormatter.Options = Options(rawValue = 0U)
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    class Options: OptionSet<ISO8601DateFormatter.Options, UInt>, MutableStruct {
        override var rawValue: UInt
        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): ISO8601DateFormatter.Options = Options(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: ISO8601DateFormatter.Options) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ISO8601DateFormatter.Options
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = ISO8601DateFormatter.Options(this as MutableStruct)

        private fun assignfrom(target: ISO8601DateFormatter.Options) {
            this.rawValue = target.rawValue
        }

        companion object {
            val withYear = ISO8601DateFormatter.Options(rawValue = 1U shl 0)
            val withMonth = ISO8601DateFormatter.Options(rawValue = 1U shl 1)
            val withWeekOfYear = ISO8601DateFormatter.Options(rawValue = 1U shl 2)
            val withDay = ISO8601DateFormatter.Options(rawValue = 1U shl 4)
            val withTime = ISO8601DateFormatter.Options(rawValue = 1U shl 5)
            val withTimeZone = ISO8601DateFormatter.Options(rawValue = 1U shl 6)
            val withSpaceBetweenDateAndTime = ISO8601DateFormatter.Options(rawValue = 1U shl 7)
            val withDashSeparatorInDate = ISO8601DateFormatter.Options(rawValue = 1U shl 8)
            val withColonSeparatorInTime = ISO8601DateFormatter.Options(rawValue = 1U shl 9)
            val withColonSeparatorInTimeZone = ISO8601DateFormatter.Options(rawValue = 1U shl 10)
            val withFractionalSeconds = ISO8601DateFormatter.Options(rawValue = 1U shl 11)
            val withFullDate = ISO8601DateFormatter.Options(rawValue = withYear.rawValue + withMonth.rawValue + withDay.rawValue + withDashSeparatorInDate.rawValue)
            val withFullTime = ISO8601DateFormatter.Options(rawValue = withTime.rawValue + withTimeZone.rawValue + withColonSeparatorInTime.rawValue + withColonSeparatorInTimeZone.rawValue)
            val withInternetDateTime = ISO8601DateFormatter.Options(rawValue = withFullDate.rawValue + withFullTime.rawValue)

            fun of(vararg options: ISO8601DateFormatter.Options): ISO8601DateFormatter.Options {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return Options(rawValue = value)
            }
        }
    }

    companion object: CompanionClass() {

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun string(from: Date, timeZone: TimeZone, formatOptions: ISO8601DateFormatter.Options): String {
            val date = from
            fatalError()
        }

        override fun string(from: Date, timeZone: TimeZone): String {
            val date = from
            val formatter = ISO8601DateFormatter()
            formatter.timeZone = timeZone
            return formatter.string(from = date)
        }
    }
    open class CompanionClass: DateFormatter.CompanionClass() {
        open fun string(from: Date, timeZone: TimeZone): String = ISO8601DateFormatter.string(from = from, timeZone = timeZone)
    }
}

