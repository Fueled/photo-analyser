// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

enum class CGLineCap(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<Int> {
    butt(0),
    round(1),
    square(2);


    companion object {
    }
}

fun CGLineCap(rawValue: Int): CGLineCap? {
    return when (rawValue) {
        0 -> CGLineCap.butt
        1 -> CGLineCap.round
        2 -> CGLineCap.square
        else -> null
    }
}
enum class CGLineJoin(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<Int> {
    miter(0),
    round(1),
    bevel(2);

    companion object {
    }
}

fun CGLineJoin(rawValue: Int): CGLineJoin? {
    return when (rawValue) {
        0 -> CGLineJoin.miter
        1 -> CGLineJoin.round
        2 -> CGLineJoin.bevel
        else -> null
    }
}

class StrokeStyle: Sendable, MutableStruct {
    var lineWidth: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var lineCap: CGLineCap
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var lineJoin: CGLineJoin
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var miterLimit: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var dash: Array<Double>
        get() = field.sref({ this.dash = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    var dashPhase: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(lineWidth: Double = 1.0, lineCap: CGLineCap = CGLineCap.butt, lineJoin: CGLineJoin = CGLineJoin.miter, miterLimit: Double = 10.0, dash: Array<Double> = arrayOf(), dashPhase: Double = 0.0) {
        this.lineWidth = lineWidth
        this.lineCap = lineCap
        this.lineJoin = lineJoin
        this.miterLimit = miterLimit
        this.dash = dash
        this.dashPhase = dashPhase
    }

    @Composable
    internal fun asDrawStyle(): DrawStyle {
        val density = LocalDensity.current.sref()
        val widthPx = with(density) { -> lineWidth.dp.toPx() }

        val cap: StrokeCap
        when (lineCap) {
            CGLineCap.butt -> cap = StrokeCap.Butt.sref()
            CGLineCap.round -> cap = StrokeCap.Round.sref()
            CGLineCap.square -> cap = StrokeCap.Square.sref()
        }

        val join: StrokeJoin
        when (lineJoin) {
            CGLineJoin.bevel -> join = StrokeJoin.Bevel.sref()
            CGLineJoin.round -> join = StrokeJoin.Round.sref()
            CGLineJoin.miter -> join = StrokeJoin.Miter.sref()
        }

        var pathEffect: PathEffect? = null
        if (!dash.isEmpty) {
            val intervals = FloatArray(max(2, dash.count)) { element ->
                with(density) { -> dash[min(element, dash.count - 1)].dp.toPx() }
            }
            val phase = with(density) { -> dashPhase.dp.toPx() }
            pathEffect = PathEffect.dashPathEffect(intervals, phase)
        }
        return Stroke(width = widthPx.sref(), miter = Float(miterLimit), cap, join, pathEffect)
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as StrokeStyle
        this.lineWidth = copy.lineWidth
        this.lineCap = copy.lineCap
        this.lineJoin = copy.lineJoin
        this.miterLimit = copy.miterLimit
        this.dash = copy.dash
        this.dashPhase = copy.dashPhase
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = StrokeStyle(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is StrokeStyle) return false
        return lineWidth == other.lineWidth && lineCap == other.lineCap && lineJoin == other.lineJoin && miterLimit == other.miterLimit && dash == other.dash && dashPhase == other.dashPhase
    }

    companion object {
    }
}
