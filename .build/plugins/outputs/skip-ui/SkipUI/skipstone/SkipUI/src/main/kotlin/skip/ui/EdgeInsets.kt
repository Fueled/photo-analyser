// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*


class EdgeInsets: Sendable, MutableStruct {
    var top: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var leading: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var bottom: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var trailing: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(top: Double = 0.0, leading: Double = 0.0, bottom: Double = 0.0, trailing: Double = 0.0) {
        this.top = top
        this.leading = leading
        this.bottom = bottom
        this.trailing = trailing
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as EdgeInsets
        this.top = copy.top
        this.leading = copy.leading
        this.bottom = copy.bottom
        this.trailing = copy.trailing
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = EdgeInsets(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is EdgeInsets) return false
        return top == other.top && leading == other.leading && bottom == other.bottom && trailing == other.trailing
    }

    companion object {
    }
}
