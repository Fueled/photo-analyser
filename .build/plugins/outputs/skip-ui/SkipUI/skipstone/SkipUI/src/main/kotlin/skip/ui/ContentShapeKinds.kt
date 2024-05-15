// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class ContentShapeKinds: OptionSet<ContentShapeKinds, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): ContentShapeKinds = ContentShapeKinds(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: ContentShapeKinds) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ContentShapeKinds
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ContentShapeKinds(this as MutableStruct)

    private fun assignfrom(target: ContentShapeKinds) {
        this.rawValue = target.rawValue
    }

    companion object {

        val interaction = ContentShapeKinds(rawValue = 1 shl 0)
        val dragPreview = ContentShapeKinds(rawValue = 1 shl 1)
        val contextMenuPreview = ContentShapeKinds(rawValue = 1 shl 2)
        val hoverEffect = ContentShapeKinds(rawValue = 1 shl 3)
        val accessibility = ContentShapeKinds(rawValue = 1 shl 4)

        fun of(vararg options: ContentShapeKinds): ContentShapeKinds {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return ContentShapeKinds(rawValue = value)
        }
    }
}
