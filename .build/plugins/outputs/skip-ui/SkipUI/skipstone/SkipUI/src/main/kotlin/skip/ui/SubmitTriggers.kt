// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class SubmitTriggers: OptionSet<SubmitTriggers, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): SubmitTriggers = SubmitTriggers(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: SubmitTriggers) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as SubmitTriggers
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = SubmitTriggers(this as MutableStruct)

    private fun assignfrom(target: SubmitTriggers) {
        this.rawValue = target.rawValue
    }

    companion object {

        val text = SubmitTriggers(rawValue = 1 shl 0)
        val search = SubmitTriggers(rawValue = 1 shl 1)

        fun of(vararg options: SubmitTriggers): SubmitTriggers {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return SubmitTriggers(rawValue = value)
        }
    }
}
