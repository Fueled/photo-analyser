// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array
import skip.lib.Set


// Needed to expose `clone`:
fun java.util.Calendar.clone(): java.util.Calendar { return this.clone() as java.util.Calendar }

class Calendar: Codable, KotlinConverting<java.util.Calendar>, MutableStruct {
    internal var platformValue: java.util.Calendar
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(platformValue: java.util.Calendar) {
        this.platformValue = platformValue
        this.locale = Locale.current
    }

    constructor(identifier: Calendar.Identifier) {
        this.platformValue = Companion.platformValue(for_ = identifier)
        this.locale = Locale.current
    }

    constructor(from: Decoder) {
        val decoder = from
        val container = decoder.singleValueContainer()
        val identifier = container.decode(Calendar.Identifier::class)
        this.platformValue = Companion.platformValue(for_ = identifier)
        this.locale = Locale.current
    }

    override fun encode(to: Encoder) {
        val encoder = to
        var container = encoder.singleValueContainer()
        container.encode(identifier)
    }

    var locale: Locale
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    var timeZone: TimeZone
        get() = TimeZone(platformValue.getTimeZone()).sref({ this.timeZone = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            platformValue.setTimeZone(newValue.platformValue)
        }

    val description: String
        get() = platformValue.description

    val identifier: Calendar.Identifier
        get() {
            // TODO: non-gregorian calendar
            if (gregorianCalendar != null) {
                return Calendar.Identifier.gregorian
            } else {
                return Calendar.Identifier.iso8601
            }
        }

    internal fun toDate(): Date = Date(platformValue = platformValue.getTime())

    private val dateFormatSymbols: java.text.DateFormatSymbols
        get() = java.text.DateFormatSymbols.getInstance(locale.platformValue)

    private val gregorianCalendar: java.util.GregorianCalendar?
        get() = platformValue as? java.util.GregorianCalendar

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val firstWeekday: Int
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val minimumDaysInFirstWeek: Int
        get() {
            fatalError()
        }

    val eraSymbols: Array<String>
        get() = Array(dateFormatSymbols.getEras().toList())

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val longEraSymbols: Array<String>
        get() {
            fatalError()
        }

    val monthSymbols: Array<String>
        get() {
            // The java.text.DateFormatSymbols.getInstance().getMonths() method in Java returns an array of 13 symbols because it includes both the 12 months of the year and an additional symbol
            // some documentation says the blank symbol is at index 0, but other tests show it at the end, so just pare it out
            return Array(dateFormatSymbols.getMonths().toList()).filter({ it ->
                it?.isEmpty == false
            })
        }

    val shortMonthSymbols: Array<String>
        get() {
            return Array(dateFormatSymbols.getShortMonths().toList()).filter({ it ->
                it?.isEmpty == false
            })
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val veryShortMonthSymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val standaloneMonthSymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val shortStandaloneMonthSymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val veryShortStandaloneMonthSymbols: Array<String>
        get() {
            fatalError()
        }

    val weekdaySymbols: Array<String>
        get() {
            return Array(dateFormatSymbols.getWeekdays().toList()).filter({ it ->
                it?.isEmpty == false
            })
        }

    val shortWeekdaySymbols: Array<String>
        get() {
            return Array(dateFormatSymbols.getShortWeekdays().toList()).filter({ it ->
                it?.isEmpty == false
            })
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val veryShortWeekdaySymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val standaloneWeekdaySymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val shortStandaloneWeekdaySymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val veryShortStandaloneWeekdaySymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val quarterSymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val shortQuarterSymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val standaloneQuarterSymbols: Array<String>
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val shortStandaloneQuarterSymbols: Array<String>
        get() {
            fatalError()
        }

    val amSymbol: String
        get() = dateFormatSymbols.getAmPmStrings()[0]

    val pmSymbol: String
        get() = dateFormatSymbols.getAmPmStrings()[1]

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun minimumRange(of: Calendar.Component): IntRange? {
        val component = of
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun maximumRange(of: Calendar.Component): IntRange? {
        val component = of
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun range(of: Calendar.Component, in_: Calendar.Component, for_: Date): IntRange? {
        val smaller = of
        val larger = in_
        val date = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dateInterval(of: Calendar.Component, start: InOut<Date>, interval: InOut<Double>, for_: Date): Boolean {
        val component = of
        val date = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dateInterval(of: Calendar.Component, for_: Date): DateInterval? {
        val component = of
        val date = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun ordinality(of: Calendar.Component, in_: Calendar.Component, for_: Date): Int? {
        val smaller = of
        val larger = in_
        val date = for_
        fatalError()
    }

    fun date(from: DateComponents): Date? {
        val components = from
        // TODO: Need to set `this` calendar in the components.calendar
        return Date(platformValue = components.createCalendarComponents().getTime())
    }

    fun dateComponents(in_: TimeZone? = null, from: Date): DateComponents {
        val zone = in_
        val date = from
        return DateComponents(fromCalendar = this, in_ = zone ?: this.timeZone, from = date)
    }

    fun dateComponents(components: Set<Calendar.Component>, from: Date, to: Date): DateComponents {
        val start = from
        val end = to
        return DateComponents(fromCalendar = this, in_ = null, from = start, to = end)
    }

    fun dateComponents(components: Set<Calendar.Component>, from: Date): DateComponents {
        val date = from
        return DateComponents(fromCalendar = this, in_ = null, from = date, with = components)
    }

    fun date(byAdding: DateComponents, to: Date, wrappingComponents: Boolean = false): Date? {
        val components = byAdding
        val date = to
        var comps = DateComponents(fromCalendar = this, in_ = this.timeZone, from = date)
        comps.add(components)
        return date(from = comps)
    }

    fun date(byAdding: Calendar.Component, value: Int, to: Date, wrappingComponents: Boolean = false): Date? {
        val component = byAdding
        val date = to
        var comps = DateComponents(fromCalendar = this, in_ = this.timeZone, from = date)
        comps.addValue(value, for_ = component)
        return date(from = comps)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun component(component: Calendar.Component, from: Date): Int {
        val date = from
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun startOfDay(for_: Date): Date {
        val date = for_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun compare(date1: Date, to: Date, toGranularity: Calendar.Component): ComparisonResult {
        val date2 = to
        val component = toGranularity
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun isDate(date1: Date, equalTo: Date, toGranularity: Calendar.Component): Boolean {
        val date2 = equalTo
        val component = toGranularity
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun isDate(date1: Date, inSameDayAs: Date): Boolean {
        val date2 = inSameDayAs
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun isDateInToday(date: Date): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun isDateInYesterday(date: Date): Boolean {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun isDateInTomorrow(date: Date): Boolean {
        fatalError()
    }

    fun isDateInWeekend(date: Date): Boolean {
        val components = dateComponents(from = date)
        return components.weekday == java.util.Calendar.SATURDAY || components.weekday == java.util.Calendar.SUNDAY
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dateIntervalOfWeekend(containing: Date, start: InOut<Date>, interval: InOut<Double>): Boolean {
        val date = containing
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dateIntervalOfWeekend(containing: Date): DateInterval? {
        val date = containing
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun nextWeekend(startingAfter: Date, start: InOut<Date>, interval: InOut<Double>, direction: Calendar.SearchDirection = Calendar.SearchDirection.forward): Boolean {
        val date = startingAfter
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun nextWeekend(startingAfter: Date, direction: Calendar.SearchDirection = Calendar.SearchDirection.forward): DateInterval? {
        val date = startingAfter
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun enumerateDates(startingAfter: Date, matching: DateComponents, matchingPolicy: Calendar.MatchingPolicy, repeatedTimePolicy: Calendar.RepeatedTimePolicy = Calendar.RepeatedTimePolicy.first, direction: Calendar.SearchDirection = Calendar.SearchDirection.forward, using: (Date?, Boolean, InOut<Boolean>) -> Unit) {
        val start = startingAfter
        val components = matching
        val block = using
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun nextDate(after: Date, matching: DateComponents, matchingPolicy: Calendar.MatchingPolicy, repeatedTimePolicy: Calendar.RepeatedTimePolicy = Calendar.RepeatedTimePolicy.first, direction: Calendar.SearchDirection = Calendar.SearchDirection.forward): Date? {
        val date = after
        val components = matching
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun date(bySetting: Calendar.Component, value: Int, of: Date): Date? {
        val component = bySetting
        val date = of
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun date(bySettingHour: Int, minute: Int, second: Int, of: Date, matchingPolicy: Calendar.MatchingPolicy = Calendar.MatchingPolicy.nextTime, repeatedTimePolicy: Calendar.RepeatedTimePolicy = Calendar.RepeatedTimePolicy.first, direction: Calendar.SearchDirection = Calendar.SearchDirection.forward): Date? {
        val hour = bySettingHour
        val date = of
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun date(date: Date, matchesComponents: DateComponents): Boolean {
        val components = matchesComponents
        fatalError()
    }

    enum class Component: Sendable {
        era,
        year,
        month,
        day,
        hour,
        minute,
        second,
        weekday,
        weekdayOrdinal,
        quarter,
        weekOfMonth,
        weekOfYear,
        yearForWeekOfYear,
        nanosecond,
        calendar,
        timeZone;

        companion object {
        }
    }

    /// Calendar supports many different kinds of calendars. Each is identified by an identifier here.
    enum class Identifier(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Codable, Sendable, RawRepresentable<Int> {
        /// The common calendar in Europe, the Western Hemisphere, and elsewhere.
        gregorian(0),
        buddhist(1),
        chinese(2),
        coptic(3),
        ethiopicAmeteMihret(4),
        ethiopicAmeteAlem(5),
        hebrew(6),
        iso8601(7),
        indian(8),
        islamic(9),
        islamicCivil(10),
        japanese(11),
        persian(12),
        republicOfChina(13),
        islamicTabular(14),
        islamicUmmAlQura(15);

        override fun encode(to: Encoder) {
            val container = to.singleValueContainer()
            container.encode(rawValue)
        }

        companion object: DecodableCompanion<Calendar.Identifier> {
            override fun init(from: Decoder): Calendar.Identifier = Calendar.Identifier(from = from)
        }
    }

    enum class SearchDirection: Sendable {
        forward,
        backward;

        companion object {
        }
    }

    enum class RepeatedTimePolicy: Sendable {
        first,
        last;

        companion object {
        }
    }

    enum class MatchingPolicy: Sendable {
        nextTime,
        nextTimePreservingSmallerComponents,
        previousTimePreservingSmallerComponents,
        strict;

        companion object {
        }
    }

    override fun kotlin(nocopy: Boolean): java.util.Calendar = (if (nocopy) platformValue else platformValue.clone() as java.util.Calendar).sref()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Calendar
        this.platformValue = copy.platformValue
        this.locale = copy.locale
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Calendar(this as MutableStruct)

    override fun toString(): String = description

    override fun equals(other: Any?): Boolean {
        if (other !is Calendar) return false
        return platformValue == other.platformValue && locale == other.locale
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, platformValue)
        result = Hasher.combine(result, locale)
        return result
    }

    companion object: DecodableCompanion<Calendar> {

        val current: Calendar
            get() = Calendar(platformValue = java.util.Calendar.getInstance())

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val autoupdatingCurrent: Calendar
            get() {
                fatalError()
            }

        private fun platformValue(for_: Calendar.Identifier): java.util.Calendar {
            val identifier = for_
            when (identifier) {
                Calendar.Identifier.gregorian -> return java.util.GregorianCalendar()
                Calendar.Identifier.iso8601 -> return java.util.Calendar.getInstance()
                else -> {
                    // TODO: how to support the other calendars?
                    return java.util.Calendar.getInstance()
                }
            }
        }

        override fun init(from: Decoder): Calendar = Calendar(from = from)

        fun Identifier(from: Decoder): Calendar.Identifier {
            val container = from.singleValueContainer()
            val rawValue = container.decode(Int::class)
            return Identifier(rawValue = rawValue) ?: throw ErrorException(cause = NullPointerException())
        }

        fun Identifier(rawValue: Int): Calendar.Identifier? {
            return when (rawValue) {
                0 -> Identifier.gregorian
                1 -> Identifier.buddhist
                2 -> Identifier.chinese
                3 -> Identifier.coptic
                4 -> Identifier.ethiopicAmeteMihret
                5 -> Identifier.ethiopicAmeteAlem
                6 -> Identifier.hebrew
                7 -> Identifier.iso8601
                8 -> Identifier.indian
                9 -> Identifier.islamic
                10 -> Identifier.islamicCivil
                11 -> Identifier.japanese
                12 -> Identifier.persian
                13 -> Identifier.republicOfChina
                14 -> Identifier.islamicTabular
                15 -> Identifier.islamicUmmAlQura
                else -> null
            }
        }
    }
}

// Shims for testing
internal open class NSCalendar: java.lang.Object() {
    internal class Options {
    }

    internal enum class Unit {
        era,
        year,
        month,
        day,
        hour,
        minute,
        second,
        weekday,
        weekdayOrdinal,
        quarter,
        weekOfMonth,
        weekOfYear,
        yearForWeekOfYear,
        nanosecond,
        calendar,
        timeZone;
    }

    internal enum class Identifier {
        gregorian,
        buddhist,
        chinese,
        coptic,
        ethiopicAmeteMihret,
        ethiopicAmeteAlem,
        hebrew,
        ISO8601,
        indian,
        islamic,
        islamicCivil,
        japanese,
        persian,
        republicOfChina,
        islamicTabular,
        islamicUmmAlQura;
    }
}

