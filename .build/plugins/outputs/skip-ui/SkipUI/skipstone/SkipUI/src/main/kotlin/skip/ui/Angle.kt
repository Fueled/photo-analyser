// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class Angle: Sendable, Comparable<Angle>, MutableStruct {

    var radians: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var degrees: Double
        get() = Companion.radiansToDegrees(radians)
        set(newValue) {
            radians = Companion.degreesToRadians(newValue)
        }

    constructor() {
        this.radians = 0.0
    }

    constructor(radians: Double) {
        this.radians = radians
    }

    constructor(degrees: Double, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.radians = Companion.degreesToRadians(degrees)
    }

    override fun compareTo(other: Angle): Int {
        if (this == other) return 0
        fun islessthan(lhs: Angle, rhs: Angle): Boolean {
            return lhs.radians < rhs.radians
        }
        return if (islessthan(this, other)) -1 else 1
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Angle
        this.radians = copy.radians
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Angle(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is Angle) return false
        return radians == other.radians
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, radians)
        return result
    }

    companion object {
        var zero = Angle()
            get() = field.sref({ this.zero = it })
            set(newValue) {
                field = newValue.sref()
            }

        fun radians(radians: Double): Angle = Angle(radians = radians)

        fun degrees(degrees: Double): Angle = Angle(degrees = degrees)

        private fun radiansToDegrees(radians: Double): Double = radians * 180 / Double.pi

        private fun degreesToRadians(degrees: Double): Double = degrees * Double.pi / 180
    }
}

