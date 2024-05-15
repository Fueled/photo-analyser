// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

class UnitPoint: Sendable, MutableStruct {
    var x: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var y: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(x: Double = 0.0, y: Double = 0.0) {
        this.x = x
        this.y = y
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = UnitPoint(x, y)

    override fun equals(other: Any?): Boolean {
        if (other !is UnitPoint) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, x)
        result = Hasher.combine(result, y)
        return result
    }

    companion object {

        val zero = UnitPoint(x = 0.0, y = 0.0)
        val center = UnitPoint(x = 0.5, y = 0.5)
        val leading = UnitPoint(x = 0.0, y = 0.5)
        val trailing = UnitPoint(x = 1.0, y = 0.5)
        val top = UnitPoint(x = 0.5, y = 0.0)
        val bottom = UnitPoint(x = 0.5, y = 1.0)
        val topLeading = UnitPoint(x = 0.0, y = 0.0)
        val topTrailing = UnitPoint(x = 1.0, y = 0.0)
        val bottomLeading = UnitPoint(x = 0.0, y = 1.0)
        val bottomTrailing = UnitPoint(x = 1.0, y = 1.0)
    }
}

class UnitCurve: Sendable {
    private val startControlPoint: UnitPoint
    private val endControlPoint: UnitPoint

    constructor(startControlPoint: UnitPoint, endControlPoint: UnitPoint) {
        this.startControlPoint = startControlPoint.sref()
        this.endControlPoint = endControlPoint.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun value(at: Double): Double {
        val progress = at
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun velocity(at: Double): Double {
        val progress = at
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val inverse: UnitCurve
        get() {
            fatalError()
        }

    fun asEasing(): Easing = CubicBezierEasing(Float(startControlPoint.x), Float(startControlPoint.y), Float(endControlPoint.x), Float(endControlPoint.y))

    override fun equals(other: Any?): Boolean {
        if (other !is UnitCurve) return false
        return startControlPoint == other.startControlPoint && endControlPoint == other.endControlPoint
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, startControlPoint)
        result = Hasher.combine(result, endControlPoint)
        return result
    }

    companion object {

        fun bezier(startControlPoint: UnitPoint, endControlPoint: UnitPoint): UnitCurve = UnitCurve(startControlPoint = startControlPoint, endControlPoint = endControlPoint)

        val easeInOut = UnitCurve(startControlPoint = UnitPoint(x = 0.42, y = 0.0), endControlPoint = UnitPoint(x = 0.58, y = 1.0))

        val easeIn = UnitCurve(startControlPoint = UnitPoint(x = 0.42, y = 0.0), endControlPoint = UnitPoint(x = 1.0, y = 1.0))

        val easeOut = UnitCurve(startControlPoint = UnitPoint(x = 0.0, y = 0.0), endControlPoint = UnitPoint(x = 0.58, y = 1.0))

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val circularEaseIn: UnitCurve
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val circularEaseOut: UnitCurve
            get() {
                fatalError()
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val circularEaseInOut: UnitCurve
            get() {
                fatalError()
            }

        val linear = UnitCurve(startControlPoint = UnitPoint(x = 0.0, y = 0.0), endControlPoint = UnitPoint(x = 1.0, y = 1.0))
    }
}
