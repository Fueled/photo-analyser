// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

/// Allow views to specialize based on their placement.
internal class ViewPlacement: RawRepresentable<Int>, OptionSet<ViewPlacement, Int>, MutableStruct {
    override var rawValue: Int

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): ViewPlacement = ViewPlacement(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: ViewPlacement) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ViewPlacement(rawValue)

    private fun assignfrom(target: ViewPlacement) {
        this.rawValue = target.rawValue
    }

    companion object {

        internal val systemTextColor = ViewPlacement(rawValue = 1)
        internal val tagged = ViewPlacement(rawValue = 2)
        internal val toolbar = ViewPlacement(rawValue = 4)

        fun of(vararg options: ViewPlacement): ViewPlacement {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return ViewPlacement(rawValue = value)
        }
    }
}
