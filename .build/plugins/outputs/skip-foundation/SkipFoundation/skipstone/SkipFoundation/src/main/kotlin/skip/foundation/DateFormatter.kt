// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


open class DateFormatter: Formatter {
    internal open val platformValue: java.text.DateFormat
        get() {
            if (_platformValue == null) {
                _platformValue = createDateFormat()
            }
            return _platformValue!!
        }
    private var _platformValue: java.text.DateFormat? = null
        get() = field.sref({ this._platformValue = it })
        set(newValue) {
            field = newValue.sref()
        }

    private fun createDateFormat(): java.text.DateFormat {
        val formatter: java.text.DateFormat
        if (_dateFormat != null || _dateFormatTemplate != null) {
            val simpleFormat: java.text.SimpleDateFormat
            val matchtarget_0 = _dateFormat
            if (matchtarget_0 != null) {
                val dateFormat = matchtarget_0
                val matchtarget_1 = _locale
                if (matchtarget_1 != null) {
                    val locale = matchtarget_1
                    simpleFormat = java.text.SimpleDateFormat(dateFormat, locale.platformValue)
                } else {
                    simpleFormat = java.text.SimpleDateFormat(dateFormat)
                }
            } else {
                val matchtarget_2 = _locale
                if (matchtarget_2 != null) {
                    val locale = matchtarget_2
                    // Provide some pattern that we'll override when we apply our template, because it's the only way to also pass a locale
                    simpleFormat = java.text.SimpleDateFormat("yyyy.MM.dd", locale.platformValue)
                } else {
                    simpleFormat = java.text.SimpleDateFormat()
                }
            }
            _dateFormatTemplate?.let { dateFormatTemplate ->
                simpleFormat.applyLocalizedPattern(dateFormatTemplate)
            }
            formatter = simpleFormat.sref()
        } else {
            val dstyle = platformStyle(for_ = dateStyle)
            val tstyle = platformStyle(for_ = timeStyle)
            if (dateStyle != DateFormatter.Style.none && timeStyle != DateFormatter.Style.none) {
                val matchtarget_3 = _locale
                if (matchtarget_3 != null) {
                    val locale = matchtarget_3
                    formatter = java.text.DateFormat.getDateTimeInstance(dstyle, tstyle, locale.platformValue)
                } else {
                    formatter = java.text.DateFormat.getDateTimeInstance(dstyle, tstyle)
                }
            } else if (dateStyle != DateFormatter.Style.none) {
                val matchtarget_4 = _locale
                if (matchtarget_4 != null) {
                    val locale = matchtarget_4
                    formatter = java.text.DateFormat.getDateInstance(dstyle, locale.platformValue)
                } else {
                    formatter = java.text.DateFormat.getDateInstance(dstyle)
                }
            } else if (timeStyle != DateFormatter.Style.none) {
                val matchtarget_5 = _locale
                if (matchtarget_5 != null) {
                    val locale = matchtarget_5
                    formatter = java.text.DateFormat.getTimeInstance(tstyle, locale.platformValue)
                } else {
                    formatter = java.text.DateFormat.getTimeInstance(tstyle)
                }
            } else {
                // There is no way to create a platform format with the equivalent of .none
                val matchtarget_6 = _locale
                if (matchtarget_6 != null) {
                    val locale = matchtarget_6
                    formatter = java.text.DateFormat.getDateTimeInstance(dstyle, tstyle, locale.platformValue)
                } else {
                    formatter = java.text.DateFormat.getDateTimeInstance(dstyle, tstyle)
                }
            }
        }

        formatter.isLenient = isLenient
        _timeZone.sref()?.let { timeZone ->
            formatter.timeZone = timeZone.platformValue
        }
        _calendar.sref()?.let { calendar ->
            formatter.calendar = calendar.platformValue
        }
        return formatter.sref()
    }

    private fun platformStyle(for_: DateFormatter.Style): Int {
        val style = for_
        when (style) {
            DateFormatter.Style.none -> return java.text.DateFormat.DEFAULT
            DateFormatter.Style.short -> return java.text.DateFormat.SHORT
            DateFormatter.Style.medium -> return java.text.DateFormat.MEDIUM
            DateFormatter.Style.long -> return java.text.DateFormat.LONG
            DateFormatter.Style.full -> return java.text.DateFormat.FULL
        }
    }

    internal constructor(platformValue: java.text.DateFormat): super() {
        suppresssideeffects = true
        try {
            _platformValue = platformValue
            isLenient = platformValue.isLenient
            _dateFormat = (platformValue as? java.text.SimpleDateFormat)?.toPattern() ?: ""
            _timeZone = TimeZone(platformValue = platformValue.timeZone)
            _calendar = Calendar(platformValue = platformValue.calendar)
        } finally {
            suppresssideeffects = false
        }
    }

    constructor(): super() {
    }

    open val description: String
        get() = platformValue.toString()

    enum class Style(override val rawValue: UInt, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<UInt> {
        none(UInt(0)),
        short(UInt(1)),
        medium(UInt(2)),
        long(UInt(3)),
        full(UInt(4));

        companion object {
        }
    }

    open var dateStyle: DateFormatter.Style = DateFormatter.Style.none
        set(newValue) {
            field = newValue
            if (!suppresssideeffects) {
                _platformValue = null // No way to set without creating a new instance
            }
        }

    open var timeStyle: DateFormatter.Style = DateFormatter.Style.none
        set(newValue) {
            field = newValue
            if (!suppresssideeffects) {
                _platformValue = null // No way to set without creating a new instance
            }
        }

    enum class Behavior(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        default(0),
        behavior10_0(1000),
        behavior10_4(1040);

        companion object {
        }
    }

    open var isLenient = false
        set(newValue) {
            field = newValue
            if (!suppresssideeffects) {
                _platformValue?.isLenient = isLenient
            }
        }

    open var dateFormat: String
        get() {
            // Return whatever platform formatter is actually using
            return (platformValue as? java.text.SimpleDateFormat)?.toPattern() ?: ""
        }
        set(newValue) {
            _dateFormat = newValue
            _dateFormatTemplate = null
            val matchtarget_7 = _platformValue as? java.text.SimpleDateFormat
            if (matchtarget_7 != null) {
                val simpleFormat = matchtarget_7
                simpleFormat.applyPattern(newValue)
            } else {
                _platformValue = null
            }
        }
    private var _dateFormat: String? = null

    open fun setLocalizedDateFormatFromTemplate(dateFormatTemplate: String) {
        _dateFormatTemplate = dateFormatTemplate
        _dateFormat = null
        val matchtarget_8 = _platformValue as? java.text.SimpleDateFormat
        if (matchtarget_8 != null) {
            val simpleFormat = matchtarget_8
            simpleFormat.applyLocalizedPattern(dateFormatTemplate)
        } else {
            _platformValue = null
        }
    }
    private var _dateFormatTemplate: String? = null

    open var timeZone: TimeZone?
        get() {
            // Return whatever platform formatter is actually using
            return TimeZone(platformValue = platformValue.timeZone).sref({ this.timeZone = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            _timeZone = newValue
            _timeZone.sref()?.let { _timeZone ->
                _platformValue.sref()?.let { _platformValue ->
                    _platformValue.timeZone = _timeZone.platformValue
                }
            }
        }
    private var _timeZone: TimeZone? = null
        get() = field.sref({ this._timeZone = it })
        set(newValue) {
            field = newValue.sref()
        }

    open var locale: Locale?
        get() = _locale ?: Locale.current
        set(newValue) {
            _locale = newValue
            _platformValue = null // No way to set without creating a new instance
        }
    private var _locale: Locale? = null

    open var calendar: Calendar?
        get() {
            // Return whatever platform formatter is actually using
            return Calendar(platformValue = platformValue.calendar).sref({ this.calendar = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            _calendar = newValue
            _calendar.sref()?.let { _calendar ->
                _platformValue.sref()?.let { _platformValue ->
                    _platformValue.calendar = _calendar.platformValue
                }
            }
        }
    private var _calendar: Calendar? = null
        get() = field.sref({ this._calendar = it })
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var generatesCalendarDates = false

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var formatterBehavior = Behavior.default

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var twoDigitStartDate: Date? = null
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var defaultDate: Date? = null
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var eraSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var monthSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var shortMonthSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var weekdaySymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var shortWeekdaySymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var amSymbol: String = ""

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var pmSymbol = ""

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var longEraSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var veryShortMonthSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var standaloneMonthSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var shortStandaloneMonthSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var veryShortStandaloneMonthSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var veryShortWeekdaySymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var standaloneWeekdaySymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var shortStandaloneWeekdaySymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var veryShortStandaloneWeekdaySymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var quarterSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var shortQuarterSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var standaloneQuarterSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var shortStandaloneQuarterSymbols: Array<String> = arrayOf()
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var gregorianStartDate: Date? = null
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var doesRelativeDateFormatting = false

    open fun date(from: String): Date? {
        val string = from
        val matchtarget_9 = try { platformValue.parse(string) } catch (_: Throwable) { null }
        if (matchtarget_9 != null) {
            val date = matchtarget_9
            return Date(platformValue = date)
        } else {
            return null
        }
    }

    open fun string(from: Date): String {
        val date = from
        return platformValue.format(date.platformValue)
    }

    override fun string(for_: Any?): String? {
        val obj = for_
        val date_0 = (obj as? Date).sref()
        if (date_0 == null) {
            return null
        }
        return string(from = date_0)
    }

    private var suppresssideeffects = false

    companion object: CompanionClass() {

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        var defaultFormatterBehavior = Behavior.default

        override fun dateFormat(fromTemplate: String, options: Int, locale: Locale?): String? {
            val fmt = DateFormatter()
            fmt.locale = locale
            fmt.setLocalizedDateFormatFromTemplate(fromTemplate)
            return (fmt.platformValue as? java.text.SimpleDateFormat)?.toLocalizedPattern()
        }

        override fun localizedString(from: Date, dateStyle: DateFormatter.Style, timeStyle: DateFormatter.Style): String {
            val date = from
            val dstyle = dateStyle
            val tstyle = timeStyle
            val fmt = DateFormatter()
            fmt.dateStyle = dateStyle
            fmt.timeStyle = timeStyle
            return fmt.string(from = date)
        }

        override fun Style(rawValue: UInt): DateFormatter.Style? {
            return when (rawValue) {
                UInt(0) -> Style.none
                UInt(1) -> Style.short
                UInt(2) -> Style.medium
                UInt(3) -> Style.long
                UInt(4) -> Style.full
                else -> null
            }
        }

        override fun Behavior(rawValue: Int): DateFormatter.Behavior? {
            return when (rawValue) {
                0 -> Behavior.default
                1000 -> Behavior.behavior10_0
                1040 -> Behavior.behavior10_4
                else -> null
            }
        }
    }
    open class CompanionClass: Formatter.CompanionClass() {
        open fun dateFormat(fromTemplate: String, options: Int, locale: Locale?): String? = DateFormatter.dateFormat(fromTemplate = fromTemplate, options = options, locale = locale)
        open fun localizedString(from: Date, dateStyle: DateFormatter.Style, timeStyle: DateFormatter.Style): String = DateFormatter.localizedString(from = from, dateStyle = dateStyle, timeStyle = timeStyle)
        open fun Style(rawValue: UInt): DateFormatter.Style? = DateFormatter.Style(rawValue = rawValue)
        open fun Behavior(rawValue: Int): DateFormatter.Behavior? = DateFormatter.Behavior(rawValue = rawValue)
    }
}

