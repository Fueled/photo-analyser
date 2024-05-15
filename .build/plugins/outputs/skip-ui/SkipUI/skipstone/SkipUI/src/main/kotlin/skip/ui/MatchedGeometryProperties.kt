// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class MatchedGeometryProperties: OptionSet<MatchedGeometryProperties, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): MatchedGeometryProperties = MatchedGeometryProperties(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: MatchedGeometryProperties) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as MatchedGeometryProperties
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = MatchedGeometryProperties(this as MutableStruct)

    private fun assignfrom(target: MatchedGeometryProperties) {
        this.rawValue = target.rawValue
    }

    companion object {

        val position = MatchedGeometryProperties(rawValue = 1 shl 0)
        val size = MatchedGeometryProperties(rawValue = 1 shl 1)
        val frame = MatchedGeometryProperties(rawValue = 1 shl 2)

        fun of(vararg options: MatchedGeometryProperties): MatchedGeometryProperties {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return MatchedGeometryProperties(rawValue = value)
        }
    }
}
