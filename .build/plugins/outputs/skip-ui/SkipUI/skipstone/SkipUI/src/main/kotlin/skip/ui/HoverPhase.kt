// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*


sealed class HoverPhase: Sendable {
    class ActiveCase(val associated0: CGPoint): HoverPhase() {
        override fun equals(other: Any?): Boolean {
            if (other !is ActiveCase) return false
            return associated0 == other.associated0
        }
    }
    class EndedCase: HoverPhase() {
    }

    companion object {
        fun active(associated0: CGPoint): HoverPhase = ActiveCase(associated0)
        val ended: HoverPhase = EndedCase()
    }
}
