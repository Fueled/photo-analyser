// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.Set

enum class VerticalEdge(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CaseIterable, Codable, RawRepresentable<Int> {
    top(1),
    bottom(2);

    class Set: OptionSet<VerticalEdge.Set, Int>, Sendable, MutableStruct {
        override var rawValue: Int

        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        constructor(e: VerticalEdge): this(rawValue = e.rawValue) {
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): VerticalEdge.Set = Set(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: VerticalEdge.Set) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as VerticalEdge.Set
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = VerticalEdge.Set(this as MutableStruct)

        private fun assignfrom(target: VerticalEdge.Set) {
            this.rawValue = target.rawValue
        }

        companion object {

            val top: VerticalEdge.Set = VerticalEdge.Set(VerticalEdge.top)
            val bottom: VerticalEdge.Set = VerticalEdge.Set(VerticalEdge.bottom)
            val all: VerticalEdge.Set = VerticalEdge.Set(rawValue = 3)

            fun of(vararg options: VerticalEdge.Set): VerticalEdge.Set {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return Set(rawValue = value)
            }
        }
    }

    override fun encode(to: Encoder) {
        val container = to.singleValueContainer()
        container.encode(rawValue)
    }

    companion object: CaseIterableCompanion<VerticalEdge>, DecodableCompanion<VerticalEdge> {
        override fun init(from: Decoder): VerticalEdge = VerticalEdge(from = from)

        override val allCases: Array<VerticalEdge>
            get() = arrayOf(top, bottom)
    }
}

fun VerticalEdge(rawValue: Int): VerticalEdge? {
    return when (rawValue) {
        1 -> VerticalEdge.top
        2 -> VerticalEdge.bottom
        else -> null
    }
}

fun VerticalEdge(from: Decoder): VerticalEdge {
    val container = from.singleValueContainer()
    val rawValue = container.decode(Int::class)
    return VerticalEdge(rawValue = rawValue) ?: throw ErrorException(cause = NullPointerException())
}
