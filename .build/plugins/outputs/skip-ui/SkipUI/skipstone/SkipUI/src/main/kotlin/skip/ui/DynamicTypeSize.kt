// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

sealed class DynamicTypeSize(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Comparable<DynamicTypeSize>, CaseIterable, Sendable, RawRepresentable<Int> {
    class XSmallCase: DynamicTypeSize(0) {
    }
    class SmallCase: DynamicTypeSize(1) {
    }
    class MediumCase: DynamicTypeSize(2) {
    }
    class LargeCase: DynamicTypeSize(3) {
    }
    class XLargeCase: DynamicTypeSize(4) {
    }
    class XxLargeCase: DynamicTypeSize(5) {
    }
    class XxxLargeCase: DynamicTypeSize(6) {
    }
    class Accessibility1Case: DynamicTypeSize(7) {
    }
    class Accessibility2Case: DynamicTypeSize(8) {
    }
    class Accessibility3Case: DynamicTypeSize(9) {
    }
    class Accessibility4Case: DynamicTypeSize(10) {
    }
    class Accessibility5Case: DynamicTypeSize(11) {
    }

    val isAccessibilitySize: Boolean
        get() = rawValue >= DynamicTypeSize.accessibility1.rawValue

    override fun compareTo(other: DynamicTypeSize): Int {
        if (this == other) return 0
        fun islessthan(a: DynamicTypeSize, b: DynamicTypeSize): Boolean {
            return a.rawValue < b.rawValue
        }
        return if (islessthan(this, other)) -1 else 1
    }

    companion object: CaseIterableCompanion<DynamicTypeSize> {
        val xSmall: DynamicTypeSize = XSmallCase()
        val small: DynamicTypeSize = SmallCase()
        val medium: DynamicTypeSize = MediumCase()
        val large: DynamicTypeSize = LargeCase()
        val xLarge: DynamicTypeSize = XLargeCase()
        val xxLarge: DynamicTypeSize = XxLargeCase()
        val xxxLarge: DynamicTypeSize = XxxLargeCase()
        val accessibility1: DynamicTypeSize = Accessibility1Case()
        val accessibility2: DynamicTypeSize = Accessibility2Case()
        val accessibility3: DynamicTypeSize = Accessibility3Case()
        val accessibility4: DynamicTypeSize = Accessibility4Case()
        val accessibility5: DynamicTypeSize = Accessibility5Case()

        override val allCases: Array<DynamicTypeSize>
            get() = arrayOf(xSmall, small, medium, large, xLarge, xxLarge, xxxLarge, accessibility1, accessibility2, accessibility3, accessibility4, accessibility5)
    }
}

fun DynamicTypeSize(rawValue: Int): DynamicTypeSize? {
    return when (rawValue) {
        0 -> DynamicTypeSize.xSmall
        1 -> DynamicTypeSize.small
        2 -> DynamicTypeSize.medium
        3 -> DynamicTypeSize.large
        4 -> DynamicTypeSize.xLarge
        5 -> DynamicTypeSize.xxLarge
        6 -> DynamicTypeSize.xxxLarge
        7 -> DynamicTypeSize.accessibility1
        8 -> DynamicTypeSize.accessibility2
        9 -> DynamicTypeSize.accessibility3
        10 -> DynamicTypeSize.accessibility4
        11 -> DynamicTypeSize.accessibility5
        else -> null
    }
}
