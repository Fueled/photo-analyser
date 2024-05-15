// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


typealias NSDate = Date
typealias TimeInterval = Double
typealias CFTimeInterval = Double

// Mimic the constructor for `TimeInterval()` with an Int.
fun TimeInterval(seconds: Int): Double = seconds.toDouble()

typealias CFAbsoluteTime = Double

fun CFAbsoluteTimeGetCurrent(): Double = Date.timeIntervalSinceReferenceDate

class Date: Comparable<Date>, Codable, KotlinConverting<java.util.Date>, MutableStruct {
    internal var platformValue: java.util.Date
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor() {
        this.platformValue = java.util.Date()
    }

    constructor(platformValue: java.util.Date) {
        this.platformValue = platformValue
    }

    constructor(from: Decoder) {
        val decoder = from
        val container = decoder.singleValueContainer()
        val timeIntervalSinceReferenceDate = container.decode(Double::class)
        this.platformValue = java.util.Date(((timeIntervalSinceReferenceDate + Date.timeIntervalBetween1970AndReferenceDate) * 1000.0).toLong())
    }

    override fun encode(to: Encoder) {
        val encoder = to
        var container = encoder.singleValueContainer()
        container.encode(this.timeIntervalSinceReferenceDate)
    }

    constructor(timeIntervalSince1970: Double, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.platformValue = java.util.Date((timeIntervalSince1970 * 1000.0).toLong())
    }

    constructor(timeIntervalSince1970: Int, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): this(timeIntervalSince1970 = TimeInterval(timeIntervalSince1970)) {
    }

    constructor(timeIntervalSinceReferenceDate: Double) {
        this.platformValue = java.util.Date(((timeIntervalSinceReferenceDate + Date.timeIntervalBetween1970AndReferenceDate) * 1000.0).toLong())
    }

    constructor(timeIntervalSinceReferenceDate: Int): this(timeIntervalSinceReferenceDate = TimeInterval(timeIntervalSinceReferenceDate)) {
    }

    constructor(timeInterval: Double, since: Date): this(timeIntervalSince1970 = timeInterval + since.timeIntervalSince1970) {
    }

    constructor(timeInterval: Int, since: Date): this(timeInterval = TimeInterval(timeInterval), since = since) {
    }

    constructor(timeIntervalSinceNow: Double, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null, @Suppress("UNUSED_PARAMETER") unusedp_1: Nothing? = null): this(timeInterval = timeIntervalSinceNow, since = Date()) {
    }

    constructor(timeIntervalSinceNow: Int, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null, @Suppress("UNUSED_PARAMETER") unusedp_1: Nothing? = null): this(timeIntervalSinceNow = TimeInterval(timeIntervalSinceNow)) {
    }

    /// Useful for converting to Java's `long` time representation
    val currentTimeMillis: Long
        get() = platformValue.getTime()

    val description: String
        get() = description(with = null)

    fun description(with: Locale?): String {
        val locale = with
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", (locale ?: Locale.current).platformValue)
        fmt.setTimeZone(TimeZone.gmt.platformValue)
        return fmt.format(platformValue)
    }

    override fun compareTo(other: Date): Int {
        if (this == other) return 0
        fun islessthan(lhs: Date, rhs: Date): Boolean {
            return lhs.platformValue < rhs.platformValue
        }
        return if (islessthan(this, other)) -1 else 1
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Date) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.platformValue == rhs.platformValue
    }

    override fun hashCode(): Int {
        var hasher = Hasher()
        hash(into = InOut<Hasher>({ hasher }, { hasher = it }))
        return hasher.finalize()
    }
    fun hash(into: InOut<Hasher>) {
        val hasher = into
        hasher.value.combine(platformValue)
    }

    val timeIntervalSince1970: Double
        get() = currentTimeMillis.toDouble() / 1000.0

    val timeIntervalSinceReferenceDate: Double
        get() = timeIntervalSince1970 - Date.timeIntervalBetween1970AndReferenceDate

    fun timeIntervalSince(date: Date): Double = timeIntervalSince1970 - date.timeIntervalSince1970

    val timeIntervalSinceNow: Double
        get() = timeIntervalSince1970 - Date().timeIntervalSince1970

    fun addingTimeInterval(timeInterval: Double): Date = Date(timeInterval = timeInterval, since = this)

    fun addingTimeInterval(timeInterval: Int): Date = Date(timeInterval = timeInterval, since = this)

    fun addTimeInterval(timeInterval: Double) {
        willmutate()
        try {
            assignfrom(addingTimeInterval(timeInterval))
        } finally {
            didmutate()
        }
    }

    fun addTimeInterval(timeInterval: Int) {
        willmutate()
        try {
            assignfrom(addingTimeInterval(timeInterval))
        } finally {
            didmutate()
        }
    }

    fun ISO8601Format(style: Date.ISO8601FormatStyle = Date.ISO8601FormatStyle.iso8601): String {
        // TODO: use the style parameters
        // local time zone specific
        // return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.getDefault()).format(platformValue)
        var dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        dateFormat.timeZone = java.util.TimeZone.getTimeZone("GMT")
        return dateFormat.format(platformValue)
    }

    class ISO8601FormatStyle: Sendable, MutableStruct {

        enum class TimeZoneSeparator(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<String> {
            colon("colon"),
            omitted("omitted");

            companion object {
            }
        }

        enum class DateSeparator(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<String> {
            dash("dash"),
            omitted("omitted");

            companion object {
            }
        }

        enum class TimeSeparator(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<String> {
            colon("colon"),
            omitted("omitted");

            companion object {
            }
        }

        enum class DateTimeSeparator(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<String> {
            space("space"),
            standard("standard");

            companion object {
            }
        }

        var timeSeparator: Date.ISO8601FormatStyle.TimeSeparator
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var includingFractionalSeconds: Boolean
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var timeZoneSeparator: Date.ISO8601FormatStyle.TimeZoneSeparator
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var dateSeparator: Date.ISO8601FormatStyle.DateSeparator
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var dateTimeSeparator: Date.ISO8601FormatStyle.DateTimeSeparator
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var timeZone: TimeZone
            get() = field.sref({ this.timeZone = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(dateSeparator: Date.ISO8601FormatStyle.DateSeparator = Date.ISO8601FormatStyle.DateSeparator.dash, dateTimeSeparator: Date.ISO8601FormatStyle.DateTimeSeparator = Date.ISO8601FormatStyle.DateTimeSeparator.standard, timeSeparator: Date.ISO8601FormatStyle.TimeSeparator = Date.ISO8601FormatStyle.TimeSeparator.colon, timeZoneSeparator: Date.ISO8601FormatStyle.TimeZoneSeparator = Date.ISO8601FormatStyle.TimeZoneSeparator.omitted, includingFractionalSeconds: Boolean = false, timeZone: TimeZone = TimeZone(secondsFromGMT = 0)) {
            this.dateSeparator = dateSeparator
            this.dateTimeSeparator = dateTimeSeparator
            this.timeSeparator = timeSeparator
            this.timeZoneSeparator = timeZoneSeparator
            this.includingFractionalSeconds = includingFractionalSeconds
            this.timeZone = timeZone
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Date.ISO8601FormatStyle
            this.timeSeparator = copy.timeSeparator
            this.includingFractionalSeconds = copy.includingFractionalSeconds
            this.timeZoneSeparator = copy.timeZoneSeparator
            this.dateSeparator = copy.dateSeparator
            this.dateTimeSeparator = copy.dateTimeSeparator
            this.timeZone = copy.timeZone
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Date.ISO8601FormatStyle(this as MutableStruct)

        companion object {
            val iso8601 = ISO8601FormatStyle()

            fun TimeZoneSeparator(rawValue: String): Date.ISO8601FormatStyle.TimeZoneSeparator? {
                return when (rawValue) {
                    "colon" -> TimeZoneSeparator.colon
                    "omitted" -> TimeZoneSeparator.omitted
                    else -> null
                }
            }

            fun DateSeparator(rawValue: String): Date.ISO8601FormatStyle.DateSeparator? {
                return when (rawValue) {
                    "dash" -> DateSeparator.dash
                    "omitted" -> DateSeparator.omitted
                    else -> null
                }
            }

            fun TimeSeparator(rawValue: String): Date.ISO8601FormatStyle.TimeSeparator? {
                return when (rawValue) {
                    "colon" -> TimeSeparator.colon
                    "omitted" -> TimeSeparator.omitted
                    else -> null
                }
            }

            fun DateTimeSeparator(rawValue: String): Date.ISO8601FormatStyle.DateTimeSeparator? {
                return when (rawValue) {
                    "space" -> DateTimeSeparator.space
                    "standard" -> DateTimeSeparator.standard
                    else -> null
                }
            }
        }
    }

    override fun kotlin(nocopy: Boolean): java.util.Date = (if (nocopy) platformValue else platformValue.clone() as java.util.Date).sref()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Date
        this.platformValue = copy.platformValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Date(this as MutableStruct)

    private fun assignfrom(target: Date) {
        this.platformValue = target.platformValue
    }

    override fun toString(): String = description

    companion object: DecodableCompanion<Date> {

        val timeIntervalBetween1970AndReferenceDate: Double = 978307200.0

        val timeIntervalSinceReferenceDate: Double
            get() = (System.currentTimeMillis().toDouble() / 1000.0) - timeIntervalBetween1970AndReferenceDate

        val distantPast = Date(timeIntervalSince1970 = -62135769600.0)
        val distantFuture = Date(timeIntervalSince1970 = 64092211200.0)

        val now: Date
            get() = Date()

        override fun init(from: Decoder): Date = Date(from = from)
    }
}

