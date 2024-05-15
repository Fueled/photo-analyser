// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


class DateInterval: Comparable<DateInterval>, Codable {
    val start: Date

    val end: Date
        get() = start.addingTimeInterval(duration)

    val duration: Double

    constructor(): this(start = Date(), duration = 0.0) {
    }

    constructor(start: Date, end: Date): this(start = start, duration = end.timeIntervalSince1970 - start.timeIntervalSince1970) {
    }

    constructor(start: Date, duration: Double) {
        this.start = start.sref()
        this.duration = duration
    }

    fun intersects(dateInterval: DateInterval): Boolean = intersection(with = dateInterval) != null

    fun intersection(with: DateInterval): DateInterval? {
        val dateInterval = with
        val start = max(this.start, dateInterval.start)
        val end = min(this.end, dateInterval.end)
        if (start > end) {
            return null
        }
        return DateInterval(start = start, end = end)
    }

    fun contains(date: Date): Boolean = start <= date && end >= date

    override fun equals(other: Any?): Boolean {
        if (other !is DateInterval) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.start == rhs.start && lhs.duration == rhs.duration
    }

    override fun compareTo(other: DateInterval): Int {
        if (this == other) return 0
        fun islessthan(lhs: DateInterval, rhs: DateInterval): Boolean {
            return lhs.start < rhs.start || (lhs.start == rhs.start && lhs.duration < rhs.duration)
        }
        return if (islessthan(this, other)) -1 else 1
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, start)
        result = Hasher.combine(result, duration)
        return result
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        start("start"),
        duration("duration");
    }

    override fun encode(to: Encoder) {
        val container = to.container(keyedBy = CodingKeys::class)
        container.encode(start, forKey = CodingKeys.start)
        container.encode(duration, forKey = CodingKeys.duration)
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.start = container.decode(Date::class, forKey = CodingKeys.start)
        this.duration = container.decode(Double::class, forKey = CodingKeys.duration)
    }

    companion object: DecodableCompanion<DateInterval> {
        override fun init(from: Decoder): DateInterval = DateInterval(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "start" -> CodingKeys.start
                "duration" -> CodingKeys.duration
                else -> null
            }
        }
    }
}

