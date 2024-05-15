// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Set

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect

class SafeAreaRegions: OptionSet<SafeAreaRegions, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): SafeAreaRegions = SafeAreaRegions(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: SafeAreaRegions) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as SafeAreaRegions
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = SafeAreaRegions(this as MutableStruct)

    private fun assignfrom(target: SafeAreaRegions) {
        this.rawValue = target.rawValue
    }

    companion object {

        val container = SafeAreaRegions(rawValue = 1)
        val keyboard = SafeAreaRegions(rawValue = 2)
        val all = SafeAreaRegions(rawValue = 3)

        fun of(vararg options: SafeAreaRegions): SafeAreaRegions {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return SafeAreaRegions(rawValue = value)
        }
    }
}


/// Track safe area.
internal class SafeArea {
    /// Total bounds of presentation root.
    internal val presentationBoundsPx: Rect

    /// Safe bounds of presentation root.
    internal val safeBoundsPx: Rect

    /// The edges whose safe area is solely due to system bars.
    internal val absoluteSystemBarEdges: Edge.Set

    internal constructor(presentation: Rect, safe: Rect, absoluteSystemBars: Edge.Set = Edge.Set.of()) {
        this.presentationBoundsPx = presentation.sref()
        this.safeBoundsPx = safe.sref()
        this.absoluteSystemBarEdges = absoluteSystemBars.sref()
    }

    /// Update the safe area.
    @Composable
    internal fun insetting(edge: Edge, to: Float): SafeArea {
        val value = to
        if (value <= 0.0f) {
            return this
        }
        var systemBarEdges = absoluteSystemBarEdges.sref()
        var (safeLeft, safeTop, safeRight, safeBottom) = safeBoundsPx.sref()
        when (edge) {
            Edge.top -> {
                safeTop = value
                systemBarEdges.remove(Edge.Set.top)
            }
            Edge.bottom -> {
                safeBottom = value
                systemBarEdges.remove(Edge.Set.bottom)
            }
            Edge.leading -> {
                safeLeft = value
                systemBarEdges.remove(Edge.Set.leading)
            }
            Edge.trailing -> {
                safeRight = value
                systemBarEdges.remove(Edge.Set.trailing)
            }
        }
        return SafeArea(presentation = presentationBoundsPx, safe = Rect(top = safeTop, left = safeLeft, bottom = safeBottom, right = safeRight), absoluteSystemBars = systemBarEdges)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SafeArea) return false
        return presentationBoundsPx == other.presentationBoundsPx && safeBoundsPx == other.safeBoundsPx && absoluteSystemBarEdges == other.absoluteSystemBarEdges
    }
}
