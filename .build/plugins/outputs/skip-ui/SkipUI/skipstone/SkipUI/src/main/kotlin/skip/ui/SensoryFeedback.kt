// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class SensoryFeedback: RawRepresentable<Int>, Sendable {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    enum class Weight: Sendable {
        light,
        medium,
        heavy;

        companion object {
        }
    }

    enum class Flexibility: Sendable {
        rigid,
        solid,
        soft;

        companion object {
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SensoryFeedback) return false
        return rawValue == other.rawValue
    }

    companion object {

        val success = SensoryFeedback(rawValue = 1)
        val warning = SensoryFeedback(rawValue = 2)
        val error = SensoryFeedback(rawValue = 3)
        val selection = SensoryFeedback(rawValue = 4)
        val increase = SensoryFeedback(rawValue = 5)
        val decrease = SensoryFeedback(rawValue = 6)
        val start = SensoryFeedback(rawValue = 7)
        val stop = SensoryFeedback(rawValue = 8)
        val alignment = SensoryFeedback(rawValue = 9)
        val levelChange = SensoryFeedback(rawValue = 10)
        val impact = SensoryFeedback(rawValue = 11)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun impact(weight: SensoryFeedback.Weight = SensoryFeedback.Weight.medium, intensity: Double = 1.0): SensoryFeedback {
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun impact(flexibility: SensoryFeedback.Flexibility, intensity: Double = 1.0): SensoryFeedback {
            fatalError()
        }
    }
}
