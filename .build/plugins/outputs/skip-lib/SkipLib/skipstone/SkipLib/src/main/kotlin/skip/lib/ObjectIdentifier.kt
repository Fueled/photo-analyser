// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib


class ObjectIdentifier {
    internal val object_: Any

    override fun equals(other: Any?): Boolean {
        if (other !is ObjectIdentifier) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.object_ === rhs.object_
    }

    internal constructor(object_: Any) {
        this.object_ = object_.sref()
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, object_)
        return result
    }

    companion object {
    }
}

