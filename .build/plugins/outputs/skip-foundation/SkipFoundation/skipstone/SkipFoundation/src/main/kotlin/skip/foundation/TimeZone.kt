// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


typealias NSTimeZone = TimeZone

class TimeZone: Codable, Sendable, KotlinConverting<java.util.TimeZone>, MutableStruct {
    internal var platformValue: java.util.TimeZone
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(platformValue: java.util.TimeZone) {
        this.platformValue = platformValue
    }

    constructor(identifier: String) {
        val tz_0 = java.util.TimeZone.getTimeZone(identifier)
        if (tz_0 == null) {
            throw NullReturnException()
        }
        this.platformValue = tz_0
    }

    constructor(abbreviation: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        val identifier_0 = Companion.abbreviationDictionary[abbreviation]
        if (identifier_0 == null) {
        }
        val tz_1 = java.util.TimeZone.getTimeZone(identifier_0)
        if (tz_1 == null) {
            throw NullReturnException()
        }
        this.platformValue = tz_1
    }

    constructor(secondsFromGMT: Int) {
        val seconds = secondsFromGMT
        // java.time.ZoneId is more modern, but doesn't seem to be able to vend a java.util.TimeZone
        // guard let tz = PlatformTimeZone.getTimeZone(java.time.ZoneId.ofOffset(seconds))

        //let timeZoneId = seconds >= 0
        //    ? String.format("GMT+%02d:%02d", seconds / 3600, (seconds % 3600) / 60)
        //    : String.format("GMT-%02d:%02d", -seconds / 3600, (-seconds % 3600) / 60)
        //guard let tz = PlatformTimeZone.getTimeZone(timeZoneId) else {
        //    return nil
        //}

        this.platformValue = java.util.SimpleTimeZone(seconds, "GMT")
    }

    constructor(from: Decoder) {
        val decoder = from
        val container = decoder.singleValueContainer()
        val identifier = container.decode(String::class)
        this.platformValue = java.util.TimeZone.getTimeZone(identifier)
    }

    override fun encode(to: Encoder) {
        val encoder = to
        var container = encoder.singleValueContainer()
        container.encode(identifier)
    }

    val identifier: String
        get() = platformValue.getID()

    fun abbreviation(for_: Date = Date()): String? {
        val date = for_
        return platformValue.getDisplayName(true, java.util.TimeZone.SHORT)
    }

    fun secondsFromGMT(for_: Date = Date()): Int {
        val date = for_
        return platformValue.getOffset(date.currentTimeMillis) / 1000 // offset is in milliseconds
    }

    val description: String
        get() = platformValue.description

    fun isDaylightSavingTime(for_: Date = Date()): Boolean {
        val date = for_
        return platformValue.toZoneId().rules.isDaylightSavings(java.time.ZonedDateTime.ofInstant(date.platformValue.toInstant(), platformValue.toZoneId()).toInstant())
    }

    fun daylightSavingTimeOffset(for_: Date = Date()): Double {
        val date = for_
        return if (isDaylightSavingTime(for_ = date)) java.time.ZonedDateTime.ofInstant(date.platformValue.toInstant(), platformValue.toZoneId()).offset.getTotalSeconds().toDouble() else 0.0
    }

    val nextDaylightSavingTimeTransition: Date?
        get() = nextDaylightSavingTimeTransition(after = Date())

    fun nextDaylightSavingTimeTransition(after: Date): Date? {
        val date = after
        // testSkipModule(): java.lang.NullPointerException: Cannot invoke "java.time.zone.ZoneOffsetTransition.getInstant()" because the return value of "java.time.zone.ZoneRules.nextTransition(java.time.Instant)" is null
        val zonedDateTime = java.time.ZonedDateTime.ofInstant(date.platformValue.toInstant(), platformValue.toZoneId())
        val transition_0 = platformValue.toZoneId().rules.nextTransition(zonedDateTime.toInstant())
        if (transition_0 == null) {
            return null
        }
        return Date(platformValue = java.util.Date.from(transition_0.getInstant()))
    }

    fun localizedName(for_: TimeZone.NameStyle, locale: Locale?): String? {
        val style = for_
        when (style) {
            TimeZone.NameStyle.generic -> {
                return platformValue.toZoneId().getDisplayName(java.time.format.TextStyle.FULL, locale?.platformValue)
            }
            TimeZone.NameStyle.standard -> {
                return platformValue.toZoneId().getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, locale?.platformValue)
            }
            TimeZone.NameStyle.shortStandard -> {
                return platformValue.toZoneId().getDisplayName(java.time.format.TextStyle.SHORT_STANDALONE, locale?.platformValue)
            }
            TimeZone.NameStyle.daylightSaving -> {
                return platformValue.toZoneId().getDisplayName(java.time.format.TextStyle.FULL, locale?.platformValue)
            }
            TimeZone.NameStyle.shortDaylightSaving -> {
                return platformValue.toZoneId().getDisplayName(java.time.format.TextStyle.SHORT, locale?.platformValue)
            }
            TimeZone.NameStyle.shortGeneric -> {
                return platformValue.toZoneId().getDisplayName(java.time.format.TextStyle.SHORT, locale?.platformValue)
            }
        }
    }

    enum class NameStyle(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        standard(0),
        shortStandard(1),
        daylightSaving(2),
        shortDaylightSaving(3),
        generic(4),
        shortGeneric(5);

        companion object {
        }
    }

    override fun kotlin(nocopy: Boolean): java.util.TimeZone = (if (nocopy) platformValue else platformValue.clone() as java.util.TimeZone).sref()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as TimeZone
        this.platformValue = copy.platformValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = TimeZone(this as MutableStruct)

    override fun toString(): String = description

    override fun equals(other: Any?): Boolean {
        if (other !is TimeZone) return false
        return platformValue == other.platformValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, platformValue)
        return result
    }

    companion object: DecodableCompanion<TimeZone> {

        val current: TimeZone
            get() = TimeZone(platformValue = java.util.TimeZone.getDefault())

        var default: TimeZone
            get() = TimeZone(platformValue = java.util.TimeZone.getDefault()).sref({ this.default = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                java.util.TimeZone.setDefault(newValue.platformValue)
            }

        val system: TimeZone
            get() = TimeZone(platformValue = java.util.TimeZone.getDefault())

        val local: TimeZone
            get() = TimeZone(platformValue = java.util.TimeZone.getDefault())

        val autoupdatingCurrent: TimeZone
            get() = TimeZone(platformValue = java.util.TimeZone.getDefault())

        val gmt: TimeZone
            get() = TimeZone(platformValue = java.util.TimeZone.getTimeZone("GMT"))

        val knownTimeZoneIdentifiers: Array<String>
            get() = Array(java.time.ZoneId.getAvailableZoneIds())

        val knownTimeZoneNames: Array<String>
            get() = Array(java.time.ZoneId.getAvailableZoneIds())

        var abbreviationDictionary: Dictionary<String, String> = dictionaryOf()
            get() = field.sref({ this.abbreviationDictionary = it })
            set(newValue) {
                field = newValue.sref()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val timeZoneDataVersion: String
            get() {
                return fatalError("TODO: TimeZone")
            }

        override fun init(from: Decoder): TimeZone = TimeZone(from = from)

        fun NameStyle(rawValue: Int): TimeZone.NameStyle? {
            return when (rawValue) {
                0 -> NameStyle.standard
                1 -> NameStyle.shortStandard
                2 -> NameStyle.daylightSaving
                3 -> NameStyle.shortDaylightSaving
                4 -> NameStyle.generic
                5 -> NameStyle.shortGeneric
                else -> null
            }
        }
    }
}

