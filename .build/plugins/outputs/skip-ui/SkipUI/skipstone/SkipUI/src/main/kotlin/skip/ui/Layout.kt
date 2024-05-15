// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

enum class LayoutDirection: CaseIterable, Sendable {
    leftToRight,
    rightToLeft;

    companion object: CaseIterableCompanion<LayoutDirection> {
        override val allCases: Array<LayoutDirection>
            get() = arrayOf(leftToRight, rightToLeft)
    }
}

sealed class LayoutDirectionBehavior: Sendable {
    class FixedCase: LayoutDirectionBehavior() {
    }
    class MirrorsCase(val associated0: LayoutDirection): LayoutDirectionBehavior() {
        val in_ = associated0

        override fun equals(other: Any?): Boolean {
            if (other !is MirrorsCase) return false
            return associated0 == other.associated0
        }
        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, associated0)
            return result
        }
    }

    companion object {
        val fixed: LayoutDirectionBehavior = FixedCase()
        fun mirrors(in_: LayoutDirection): LayoutDirectionBehavior = MirrorsCase(in_)


        var mirrors = LayoutDirectionBehavior.mirrors(in_ = LayoutDirection.rightToLeft)
    }
}

