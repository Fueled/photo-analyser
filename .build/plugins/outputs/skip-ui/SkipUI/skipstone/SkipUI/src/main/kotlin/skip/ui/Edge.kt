// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.Set

enum class Edge(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CaseIterable, Sendable, RawRepresentable<Int> {
    top(1),
    leading(2),
    bottom(4),
    trailing(8);

    class Set: OptionSet<Edge.Set, Int>, Sendable, MutableStruct {
        override var rawValue: Int

        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        constructor(e: Edge) {
            this.rawValue = e.rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): Edge.Set = Set(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: Edge.Set) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Edge.Set
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Edge.Set(this as MutableStruct)

        private fun assignfrom(target: Edge.Set) {
            this.rawValue = target.rawValue
        }

        companion object {

            val top: Edge.Set = Edge.Set(Edge.top)
            val leading: Edge.Set = Edge.Set(Edge.leading)
            val bottom: Edge.Set = Edge.Set(Edge.bottom)
            val trailing: Edge.Set = Edge.Set(Edge.trailing)

            val all: Edge.Set = Edge.Set(rawValue = 15)
            val horizontal: Edge.Set = Edge.Set(rawValue = 10)
            val vertical: Edge.Set = Edge.Set(rawValue = 5)

            fun of(vararg options: Edge.Set): Edge.Set {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return Set(rawValue = value)
            }
        }
    }

    companion object: CaseIterableCompanion<Edge> {
        override val allCases: Array<Edge>
            get() = arrayOf(top, leading, bottom, trailing)
    }
}

fun Edge(rawValue: Int): Edge? {
    return when (rawValue) {
        1 -> Edge.top
        2 -> Edge.leading
        4 -> Edge.bottom
        8 -> Edge.trailing
        else -> null
    }
}
