// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*


class ProposedViewSize: Sendable, MutableStruct {
    var width: Double? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var height: Double? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(width: Double?, height: Double?) {
        this.width = width
        this.height = height
    }

    constructor(size: CGSize): this(width = size.width, height = size.height) {
    }

    fun replacingUnspecifiedDimensions(by: CGSize = CGSize(width = 10.0, height = 10.0)): CGSize {
        val size = by
        return CGSize(width = if (width == null) size.width else width!!, height = if (height == null) size.height else height!!)
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ProposedViewSize
        this.width = copy.width
        this.height = copy.height
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ProposedViewSize(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is ProposedViewSize) return false
        return width == other.width && height == other.height
    }

    companion object {

        val zero: ProposedViewSize = ProposedViewSize(width = 0.0, height = 0.0)
        val unspecified: ProposedViewSize = ProposedViewSize(width = null, height = null)
        val infinity: ProposedViewSize = ProposedViewSize(width = Double.infinity, height = Double.infinity)
    }
}
