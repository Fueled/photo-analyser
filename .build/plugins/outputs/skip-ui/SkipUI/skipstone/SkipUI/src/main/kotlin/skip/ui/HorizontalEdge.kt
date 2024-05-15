// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.Set

enum class HorizontalEdge(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CaseIterable, Codable, Sendable, RawRepresentable<Int> {
    leading(1),
    trailing(2);

    class Set: OptionSet<HorizontalEdge.Set, Int>, MutableStruct {
        override var rawValue: Int

        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        constructor(edge: HorizontalEdge) {
            this.rawValue = edge.rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): HorizontalEdge.Set = Set(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: HorizontalEdge.Set) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as HorizontalEdge.Set
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = HorizontalEdge.Set(this as MutableStruct)

        private fun assignfrom(target: HorizontalEdge.Set) {
            this.rawValue = target.rawValue
        }

        companion object {

            val leading: HorizontalEdge.Set = HorizontalEdge.Set(rawValue = 1)
            val trailing: HorizontalEdge.Set = HorizontalEdge.Set(rawValue = 2)
            val all: HorizontalEdge.Set = HorizontalEdge.Set(rawValue = 3)

            fun of(vararg options: HorizontalEdge.Set): HorizontalEdge.Set {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return Set(rawValue = value)
            }
        }
    }

    override fun encode(to: Encoder) {
        val container = to.singleValueContainer()
        container.encode(rawValue)
    }

    companion object: CaseIterableCompanion<HorizontalEdge>, DecodableCompanion<HorizontalEdge> {
        override fun init(from: Decoder): HorizontalEdge = HorizontalEdge(from = from)

        override val allCases: Array<HorizontalEdge>
            get() = arrayOf(leading, trailing)
    }
}

fun HorizontalEdge(rawValue: Int): HorizontalEdge? {
    return when (rawValue) {
        1 -> HorizontalEdge.leading
        2 -> HorizontalEdge.trailing
        else -> null
    }
}

fun HorizontalEdge(from: Decoder): HorizontalEdge {
    val container = from.singleValueContainer()
    val rawValue = container.decode(Int::class)
    return HorizontalEdge(rawValue = rawValue) ?: throw ErrorException(cause = NullPointerException())
}
