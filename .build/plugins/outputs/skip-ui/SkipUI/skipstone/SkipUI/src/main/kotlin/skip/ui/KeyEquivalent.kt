// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class KeyEquivalent: Sendable {
    val character: Char

    constructor(character: Char) {
        this.character = character
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KeyEquivalent) return false
        return character == other.character
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, character)
        return result
    }

    companion object {

        /// Up Arrow (U+F700)
        val upArrow = KeyEquivalent('\uF700')

        /// Down Arrow (U+F701)
        val downArrow = KeyEquivalent('\uF701')

        /// Left Arrow (U+F702)
        val leftArrow = KeyEquivalent('\uF702')

        /// Right Arrow (U+F703)
        val rightArrow = KeyEquivalent('\uF703')

        /// Escape (U+001B)
        val escape = KeyEquivalent('\u001B')

        /// Delete (U+0008)
        val delete = KeyEquivalent('\u0008')

        /// Delete Forward (U+F728)
        val deleteForward = KeyEquivalent('\uF728')

        /// Home (U+F729)
        val home = KeyEquivalent('\uF729')

        /// End (U+F72B)
        val end = KeyEquivalent('\uF72B')

        /// Page Up (U+F72C)
        val pageUp = KeyEquivalent('\uF72C')

        /// Page Down (U+F72D)
        val pageDown = KeyEquivalent('\uF72D')

        /// Clear (U+F739)
        val clear = KeyEquivalent('\uF739')

        /// Tab (U+0009)
        val tab = KeyEquivalent('\u0009')

        /// Space (U+0020)
        val space = KeyEquivalent('\u0020')

        /// Return (U+000D)
        val return_ = KeyEquivalent('\u000D')
    }
}

