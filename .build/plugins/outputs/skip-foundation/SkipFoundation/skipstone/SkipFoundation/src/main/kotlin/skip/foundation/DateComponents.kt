// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array
import skip.lib.Set


typealias NSDateComponents = DateComponents

class DateComponents: Codable, MutableStruct {
    // There is no direct analogue to DateComponents in Java (other then java.util.Calendar), so we store the individual properties here

    var calendar: Calendar? = null
        get() = field.sref({ this.calendar = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    var timeZone: TimeZone? = null
        get() = field.sref({ this.timeZone = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    var era: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var year: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var month: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var day: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var hour: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var minute: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var second: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var nanosecond: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var weekday: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var weekdayOrdinal: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var quarter: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var weekOfMonth: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var weekOfYear: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var yearForWeekOfYear: Int? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(calendar: Calendar? = null, timeZone: TimeZone? = null, era: Int? = null, year: Int? = null, month: Int? = null, day: Int? = null, hour: Int? = null, minute: Int? = null, second: Int? = null, nanosecond: Int? = null, weekday: Int? = null, weekdayOrdinal: Int? = null, quarter: Int? = null, weekOfMonth: Int? = null, weekOfYear: Int? = null, yearForWeekOfYear: Int? = null) {
        this.calendar = calendar
        this.timeZone = timeZone
        this.era = era
        this.year = year
        this.month = month
        this.day = day
        this.hour = hour
        this.minute = minute
        this.second = second
        this.nanosecond = nanosecond
        this.weekday = weekday
        this.weekdayOrdinal = weekdayOrdinal
        this.quarter = quarter
        this.weekOfMonth = weekOfMonth
        this.weekOfYear = weekOfYear
        this.yearForWeekOfYear = yearForWeekOfYear
    }

    internal constructor(fromCalendar: Calendar, in_: TimeZone? = null, from: Date? = null, to: Date? = null, with: Set<Calendar.Component>? = null) {
        val calendar = fromCalendar
        val zone = in_
        val date = from
        val endDate = to
        val components = with
        val platformCal = (calendar.platformValue.clone() as java.util.Calendar).sref()

        if (date != null) {
            platformCal.time = date.platformValue
        }

        if (zone != null) {
            platformCal.timeZone = zone.platformValue
        }

        if (components?.contains(Calendar.Component.era) != false) {
            if (endDate != null) {
                // TODO: if components.contains(.year) { dc.year = Int(ucal_getFieldDifference(ucalendar, goal, UCAL_YEAR, &status)) }
                fatalError("TODO: Skip DateComponents field differences")
            } else {
                this.era = platformCal.get(java.util.Calendar.ERA)
            }
        }
        if (components?.contains(Calendar.Component.year) != false) {
            this.year = platformCal.get(java.util.Calendar.YEAR)
        }
        if (components?.contains(Calendar.Component.month) != false) {
            this.month = platformCal.get(java.util.Calendar.MONTH) + 1
        }
        if (components?.contains(Calendar.Component.day) != false) {
            this.day = platformCal.get(java.util.Calendar.DATE) // i.e., DAY_OF_MONTH
        }
        if (components?.contains(Calendar.Component.hour) != false) {
            this.hour = platformCal.get(java.util.Calendar.HOUR_OF_DAY)
        }
        if (components?.contains(Calendar.Component.minute) != false) {
            this.minute = platformCal.get(java.util.Calendar.MINUTE)
        }
        if (components?.contains(Calendar.Component.second) != false) {
            this.second = platformCal.get(java.util.Calendar.SECOND)
        }
        if (components?.contains(Calendar.Component.weekday) != false) {
            this.weekday = platformCal.get(java.util.Calendar.DAY_OF_WEEK)
        }
        if (components?.contains(Calendar.Component.weekOfMonth) != false) {
            this.weekOfMonth = platformCal.get(java.util.Calendar.WEEK_OF_MONTH)
        }
        if (components?.contains(Calendar.Component.weekOfYear) != false) {
            this.weekOfYear = platformCal.get(java.util.Calendar.WEEK_OF_YEAR)
        }

        // unsupported fields in java.util.Calendar:
        //self.nanosecond = platformCal.get(java.util.Calendar.NANOSECOND)
        //self.weekdayOrdinal = platformCal.get(java.util.Calendar.WEEKDAYORDINAL)
        //self.quarter = platformCal.get(java.util.Calendar.QUARTER)
        //self.yearForWeekOfYear = platformCal.get(java.util.Calendar.YEARFORWEEKOFYEAR)
    }

    /// Builds a java.util.Calendar from the fields.
    internal fun createCalendarComponents(): java.util.Calendar {
        val c: java.util.Calendar = (this.calendar?.platformValue ?: java.util.Calendar.getInstance()).sref()
        val cal: java.util.Calendar = ((c as java.util.Calendar).clone() as java.util.Calendar).sref()

        cal.setTimeInMillis(0) // clear the time and set the fields afresh

        val matchtarget_0 = this.timeZone
        if (matchtarget_0 != null) {
            val timeZone = matchtarget_0
            cal.setTimeZone(timeZone.platformValue)
        } else {
            cal.setTimeZone(TimeZone.current.platformValue)
        }

        this.era?.let { era ->
            cal.set(java.util.Calendar.ERA, era)
        }
        this.year?.let { year ->
            cal.set(java.util.Calendar.YEAR, year)
        }
        this.month?.let { month ->
            // Foundation starts at 1, but Java: “Field number for get and set indicating the month. This is a calendar-specific value. The first month of the year in the Gregorian and Julian calendars is JANUARY which is 0; the last depends on the number of months in a year.”
            cal.set(java.util.Calendar.MONTH, month - 1)
        }
        this.day?.let { day ->
            cal.set(java.util.Calendar.DATE, day) // i.e., DAY_OF_MONTH
        }
        this.hour?.let { hour ->
            cal.set(java.util.Calendar.HOUR_OF_DAY, hour)
        }
        this.minute?.let { minute ->
            cal.set(java.util.Calendar.MINUTE, minute)
        }
        this.second?.let { second ->
            cal.set(java.util.Calendar.SECOND, second)
        }
        this.nanosecond?.let { nanosecond ->
            //cal.set(java.util.Calendar.NANOSECOND, nanosecond)
            fatalError("Skip Date Components.nanosecond unsupported in Skip")
        }
        this.weekday?.let { weekday ->
            cal.set(java.util.Calendar.DAY_OF_WEEK, weekday)
        }
        this.weekdayOrdinal?.let { weekdayOrdinal ->
            //cal.set(java.util.Calendar.WEEKDAYORDINAL, weekdayOrdinal)
            fatalError("Skip Date Components.weekdayOrdinal unsupported in Skip")
        }
        this.quarter?.let { quarter ->
            //cal.set(java.util.Calendar.QUARTER, quarter)
            fatalError("Skip Date Components.quarter unsupported in Skip")
        }
        this.weekOfMonth?.let { weekOfMonth ->
            cal.set(java.util.Calendar.WEEK_OF_MONTH, weekOfMonth)
        }
        this.weekOfYear?.let { weekOfYear ->
            cal.set(java.util.Calendar.WEEK_OF_YEAR, weekOfYear)
        }
        this.yearForWeekOfYear?.let { yearForWeekOfYear ->
            //cal.set(java.util.Calendar.YEARFORWEEKOFYEAR, yearForWeekOfYear)
            fatalError("Skip Date Components.yearForWeekOfYear unsupported in Skip")
        }

        return cal.sref()
    }

    fun setValue(value: Int?, for_: Calendar.Component) {
        val component = for_
        willmutate()
        try {
            for (unusedi in 0..0) {
                when (component) {
                    Calendar.Component.era -> this.era = value
                    Calendar.Component.year -> this.year = value
                    Calendar.Component.month -> this.month = value
                    Calendar.Component.day -> this.day = value
                    Calendar.Component.hour -> this.hour = value
                    Calendar.Component.minute -> this.minute = value
                    Calendar.Component.second -> this.second = value
                    Calendar.Component.weekday -> this.weekday = value
                    Calendar.Component.weekdayOrdinal -> this.weekdayOrdinal = value
                    Calendar.Component.quarter -> this.quarter = value
                    Calendar.Component.weekOfMonth -> this.weekOfMonth = value
                    Calendar.Component.weekOfYear -> this.weekOfYear = value
                    Calendar.Component.yearForWeekOfYear -> this.yearForWeekOfYear = value
                    Calendar.Component.nanosecond -> this.nanosecond = value
                    Calendar.Component.calendar, Calendar.Component.timeZone -> {
                        // Do nothing
                        break
                    }
                }
            }
        } finally {
            didmutate()
        }
    }

    fun add(components: DateComponents) {
        willmutate()
        try {
            val cal = createCalendarComponents()

            components.era?.let { value ->
                cal.roll(java.util.Calendar.ERA, value)
            }
            components.year?.let { value ->
                cal.roll(java.util.Calendar.YEAR, value)
            }
            components.quarter?.let { value ->
                //cal.roll(java.util.Calendar.QUARTER, value)
                fatalError("Skip DateComponents.quarter unsupported in Skip")
            }
            components.month?.let { value ->
                cal.roll(java.util.Calendar.MONTH, value)
            }
            components.weekday?.let { value ->
                cal.roll(java.util.Calendar.DAY_OF_WEEK, value)
            }
            components.weekdayOrdinal?.let { value ->
                //cal.roll(java.util.Calendar.WEEKDAYORDINAL, value)
                fatalError("Skip DateComponents.weekdayOrdinal unsupported in Skip")
            }
            components.weekOfMonth?.let { value ->
                cal.roll(java.util.Calendar.WEEK_OF_MONTH, value)
            }
            components.weekOfYear?.let { value ->
                cal.roll(java.util.Calendar.WEEK_OF_YEAR, value)
            }
            components.yearForWeekOfYear?.let { value ->
                //cal.roll(java.util.Calendar.YEARFORWEEKOFYEAR, value)
                fatalError("Skip DateComponents.yearForWeekOfYear unsupported in Skip")
            }
            components.day?.let { value ->
                cal.roll(java.util.Calendar.DATE, value) // i.e., DAY_OF_MONTH
            }
            components.hour?.let { value ->
                cal.roll(java.util.Calendar.HOUR_OF_DAY, value)
            }
            components.minute?.let { value ->
                cal.roll(java.util.Calendar.MINUTE, value)
            }
            components.second?.let { value ->
                cal.roll(java.util.Calendar.SECOND, value)
            }
            components.nanosecond?.let { value ->
                fatalError("Skip DateComponents.nanosecond unsupported in Skip")
            }
            assignfrom(DateComponents(fromCalendar = Calendar(platformValue = cal)))
        } finally {
            didmutate()
        }
    }

    fun addValue(value: Int, for_: Calendar.Component) {
        val component = for_
        willmutate()
        try {
            val cal = createCalendarComponents()

            for (unusedi in 0..0) {
                when (component) {
                    Calendar.Component.era -> cal.roll(java.util.Calendar.ERA, value)
                    Calendar.Component.year -> cal.roll(java.util.Calendar.YEAR, value)
                    Calendar.Component.month -> cal.roll(java.util.Calendar.MONTH, value)
                    Calendar.Component.day -> cal.roll(java.util.Calendar.DATE, value) // i.e., DAY_OF_MONTH
                    Calendar.Component.hour -> cal.roll(java.util.Calendar.HOUR_OF_DAY, value)
                    Calendar.Component.minute -> cal.roll(java.util.Calendar.MINUTE, value)
                    Calendar.Component.second -> cal.roll(java.util.Calendar.SECOND, value)
                    Calendar.Component.weekday -> cal.roll(java.util.Calendar.DAY_OF_WEEK, value)
                    Calendar.Component.weekdayOrdinal -> {
                        //cal.roll(java.util.Calendar.WEEKDAYORDINAL, value)
                        fatalError("Skip DateComponents.weekdayOrdinal unsupported in Skip")
                    }
                    Calendar.Component.quarter -> {
                        //cal.roll(java.util.Calendar.QUARTER, value)
                        fatalError("Skip DateComponents.quarter unsupported in Skip")
                    }
                    Calendar.Component.weekOfMonth -> cal.roll(java.util.Calendar.WEEK_OF_MONTH, value)
                    Calendar.Component.weekOfYear -> cal.roll(java.util.Calendar.WEEK_OF_YEAR, value)
                    Calendar.Component.yearForWeekOfYear -> {
                        //cal.roll(java.util.Calendar.YEARFORWEEKOFYEAR, value)
                        fatalError("Skip DateComponents.yearForWeekOfYear unsupported in Skip")
                    }
                    Calendar.Component.nanosecond -> break // unsupported
                    Calendar.Component.calendar, Calendar.Component.timeZone -> {
                        // Do nothing
                        break
                    }
                    else -> break
                }
            }
            assignfrom(DateComponents(fromCalendar = Calendar(platformValue = cal)))
        } finally {
            didmutate()
        }
    }

    fun value(for_: Calendar.Component): Int? {
        val component = for_
        when (component) {
            Calendar.Component.era -> return this.era
            Calendar.Component.year -> return this.year
            Calendar.Component.month -> return this.month
            Calendar.Component.day -> return this.day
            Calendar.Component.hour -> return this.hour
            Calendar.Component.minute -> return this.minute
            Calendar.Component.second -> return this.second
            Calendar.Component.weekday -> return this.weekday
            Calendar.Component.weekdayOrdinal -> return this.weekdayOrdinal
            Calendar.Component.quarter -> return this.quarter
            Calendar.Component.weekOfMonth -> return this.weekOfMonth
            Calendar.Component.weekOfYear -> return this.weekOfYear
            Calendar.Component.yearForWeekOfYear -> return this.yearForWeekOfYear
            Calendar.Component.nanosecond -> return this.nanosecond
            Calendar.Component.calendar, Calendar.Component.timeZone -> return null
        }
    }

    val description: String
        get() {
            var strs: Array<String> = arrayOf()
            this.calendar.sref()?.let { calendar ->
                strs.append("calendar ${calendar}")
            }
            this.timeZone.sref()?.let { timeZone ->
                strs.append("timeZone ${timeZone}")
            }
            this.era?.let { era ->
                strs.append("era ${era}")
            }
            this.year?.let { year ->
                strs.append("year ${year}")
            }
            this.month?.let { month ->
                strs.append("month ${month}")
            }
            this.day?.let { day ->
                strs.append("day ${day}")
            }
            this.hour?.let { hour ->
                strs.append("hour ${hour}")
            }
            this.minute?.let { minute ->
                strs.append("minute ${minute}")
            }
            this.second?.let { second ->
                strs.append("second ${second}")
            }
            this.nanosecond?.let { nanosecond ->
                strs.append("nanosecond ${nanosecond}")
            }
            this.weekday?.let { weekday ->
                strs.append("weekday ${weekday}")
            }
            this.weekdayOrdinal?.let { weekdayOrdinal ->
                strs.append("weekdayOrdinal ${weekdayOrdinal}")
            }
            this.quarter?.let { quarter ->
                strs.append("quarter ${quarter}")
            }
            this.weekOfMonth?.let { weekOfMonth ->
                strs.append("weekOfMonth ${weekOfMonth}")
            }
            this.weekOfYear?.let { weekOfYear ->
                strs.append("weekOfYear ${weekOfYear}")
            }
            this.yearForWeekOfYear?.let { yearForWeekOfYear ->
                strs.append("yearForWeekOfYear ${yearForWeekOfYear}")
            }
            return strs.joined(separator = " ")
        }

    val isValidDate: Boolean
        get() {
            val calendar_0 = this.calendar.sref()
            if (calendar_0 == null) {
                return false
            }
            return isValidDate(in_ = calendar_0)
        }

    fun isValidDate(in_: Calendar): Boolean {
        val calendar = in_
        // TODO: re-use implementation from: https://github.com/apple/swift-foundation/blob/68c2466c613a77d6c4453f3a06496a5da79a0cb9/Sources/FoundationInternationalization/DateComponents.swift#LL327C1-L328C1

        val cal = createCalendarComponents()
        return cal.getActualMinimum(java.util.Calendar.DAY_OF_MONTH) <= cal.get(java.util.Calendar.DAY_OF_MONTH) && cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) >= cal.get(java.util.Calendar.DAY_OF_MONTH) && cal.getActualMinimum(java.util.Calendar.MONTH) <= cal.get(java.util.Calendar.MONTH) + (if (cal.get(java.util.Calendar.MONTH) == 2) (if ((cal as? java.util.GregorianCalendar)?.isLeapYear(this.year ?: -1) == true) 0 else 1) else 0) && cal.getActualMaximum(java.util.Calendar.MONTH) >= cal.get(java.util.Calendar.MONTH) && cal.getActualMinimum(java.util.Calendar.YEAR) <= cal.get(java.util.Calendar.YEAR) && cal.getActualMaximum(java.util.Calendar.YEAR) >= cal.get(java.util.Calendar.YEAR)
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as DateComponents
        this.calendar = copy.calendar
        this.timeZone = copy.timeZone
        this.era = copy.era
        this.year = copy.year
        this.month = copy.month
        this.day = copy.day
        this.hour = copy.hour
        this.minute = copy.minute
        this.second = copy.second
        this.nanosecond = copy.nanosecond
        this.weekday = copy.weekday
        this.weekdayOrdinal = copy.weekdayOrdinal
        this.quarter = copy.quarter
        this.weekOfMonth = copy.weekOfMonth
        this.weekOfYear = copy.weekOfYear
        this.yearForWeekOfYear = copy.yearForWeekOfYear
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = DateComponents(this as MutableStruct)

    private fun assignfrom(target: DateComponents) {
        this.calendar = target.calendar
        this.timeZone = target.timeZone
        this.era = target.era
        this.year = target.year
        this.month = target.month
        this.day = target.day
        this.hour = target.hour
        this.minute = target.minute
        this.second = target.second
        this.nanosecond = target.nanosecond
        this.weekday = target.weekday
        this.weekdayOrdinal = target.weekdayOrdinal
        this.quarter = target.quarter
        this.weekOfMonth = target.weekOfMonth
        this.weekOfYear = target.weekOfYear
        this.yearForWeekOfYear = target.yearForWeekOfYear
    }

    override fun toString(): String = description

    override fun equals(other: Any?): Boolean {
        if (other !is DateComponents) return false
        return calendar == other.calendar && timeZone == other.timeZone && era == other.era && year == other.year && month == other.month && day == other.day && hour == other.hour && minute == other.minute && second == other.second && nanosecond == other.nanosecond && weekday == other.weekday && weekdayOrdinal == other.weekdayOrdinal && quarter == other.quarter && weekOfMonth == other.weekOfMonth && weekOfYear == other.weekOfYear && yearForWeekOfYear == other.yearForWeekOfYear
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, calendar)
        result = Hasher.combine(result, timeZone)
        result = Hasher.combine(result, era)
        result = Hasher.combine(result, year)
        result = Hasher.combine(result, month)
        result = Hasher.combine(result, day)
        result = Hasher.combine(result, hour)
        result = Hasher.combine(result, minute)
        result = Hasher.combine(result, second)
        result = Hasher.combine(result, nanosecond)
        result = Hasher.combine(result, weekday)
        result = Hasher.combine(result, weekdayOrdinal)
        result = Hasher.combine(result, quarter)
        result = Hasher.combine(result, weekOfMonth)
        result = Hasher.combine(result, weekOfYear)
        result = Hasher.combine(result, yearForWeekOfYear)
        return result
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        calendar("calendar"),
        timeZone("timeZone"),
        era("era"),
        year("year"),
        month("month"),
        day("day"),
        hour("hour"),
        minute("minute"),
        second("second"),
        nanosecond("nanosecond"),
        weekday("weekday"),
        weekdayOrdinal("weekdayOrdinal"),
        quarter("quarter"),
        weekOfMonth("weekOfMonth"),
        weekOfYear("weekOfYear"),
        yearForWeekOfYear("yearForWeekOfYear");
    }

    override fun encode(to: Encoder) {
        val container = to.container(keyedBy = CodingKeys::class)
        container.encodeIfPresent(calendar, forKey = CodingKeys.calendar)
        container.encodeIfPresent(timeZone, forKey = CodingKeys.timeZone)
        container.encodeIfPresent(era, forKey = CodingKeys.era)
        container.encodeIfPresent(year, forKey = CodingKeys.year)
        container.encodeIfPresent(month, forKey = CodingKeys.month)
        container.encodeIfPresent(day, forKey = CodingKeys.day)
        container.encodeIfPresent(hour, forKey = CodingKeys.hour)
        container.encodeIfPresent(minute, forKey = CodingKeys.minute)
        container.encodeIfPresent(second, forKey = CodingKeys.second)
        container.encodeIfPresent(nanosecond, forKey = CodingKeys.nanosecond)
        container.encodeIfPresent(weekday, forKey = CodingKeys.weekday)
        container.encodeIfPresent(weekdayOrdinal, forKey = CodingKeys.weekdayOrdinal)
        container.encodeIfPresent(quarter, forKey = CodingKeys.quarter)
        container.encodeIfPresent(weekOfMonth, forKey = CodingKeys.weekOfMonth)
        container.encodeIfPresent(weekOfYear, forKey = CodingKeys.weekOfYear)
        container.encodeIfPresent(yearForWeekOfYear, forKey = CodingKeys.yearForWeekOfYear)
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.calendar = container.decodeIfPresent(Calendar::class, forKey = CodingKeys.calendar)
        this.timeZone = container.decodeIfPresent(TimeZone::class, forKey = CodingKeys.timeZone)
        this.era = container.decodeIfPresent(Int::class, forKey = CodingKeys.era)
        this.year = container.decodeIfPresent(Int::class, forKey = CodingKeys.year)
        this.month = container.decodeIfPresent(Int::class, forKey = CodingKeys.month)
        this.day = container.decodeIfPresent(Int::class, forKey = CodingKeys.day)
        this.hour = container.decodeIfPresent(Int::class, forKey = CodingKeys.hour)
        this.minute = container.decodeIfPresent(Int::class, forKey = CodingKeys.minute)
        this.second = container.decodeIfPresent(Int::class, forKey = CodingKeys.second)
        this.nanosecond = container.decodeIfPresent(Int::class, forKey = CodingKeys.nanosecond)
        this.weekday = container.decodeIfPresent(Int::class, forKey = CodingKeys.weekday)
        this.weekdayOrdinal = container.decodeIfPresent(Int::class, forKey = CodingKeys.weekdayOrdinal)
        this.quarter = container.decodeIfPresent(Int::class, forKey = CodingKeys.quarter)
        this.weekOfMonth = container.decodeIfPresent(Int::class, forKey = CodingKeys.weekOfMonth)
        this.weekOfYear = container.decodeIfPresent(Int::class, forKey = CodingKeys.weekOfYear)
        this.yearForWeekOfYear = container.decodeIfPresent(Int::class, forKey = CodingKeys.yearForWeekOfYear)
    }

    companion object: DecodableCompanion<DateComponents> {
        override fun init(from: Decoder): DateComponents = DateComponents(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "calendar" -> CodingKeys.calendar
                "timeZone" -> CodingKeys.timeZone
                "era" -> CodingKeys.era
                "year" -> CodingKeys.year
                "month" -> CodingKeys.month
                "day" -> CodingKeys.day
                "hour" -> CodingKeys.hour
                "minute" -> CodingKeys.minute
                "second" -> CodingKeys.second
                "nanosecond" -> CodingKeys.nanosecond
                "weekday" -> CodingKeys.weekday
                "weekdayOrdinal" -> CodingKeys.weekdayOrdinal
                "quarter" -> CodingKeys.quarter
                "weekOfMonth" -> CodingKeys.weekOfMonth
                "weekOfYear" -> CodingKeys.weekOfYear
                "yearForWeekOfYear" -> CodingKeys.yearForWeekOfYear
                else -> null
            }
        }
    }
}

