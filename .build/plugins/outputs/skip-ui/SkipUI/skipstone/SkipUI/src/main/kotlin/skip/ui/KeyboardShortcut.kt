// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class KeyboardShortcut: Sendable {
    enum class Localization: Sendable {
        automatic,
        withoutMirroring,
        custom;

        companion object {
        }
    }

    val key: KeyEquivalent
    val modifiers: EventModifiers
    val localization: KeyboardShortcut.Localization

    constructor(key: KeyEquivalent, modifiers: EventModifiers = EventModifiers.command, localization: KeyboardShortcut.Localization = KeyboardShortcut.Localization.automatic) {
        this.key = key
        this.modifiers = modifiers.sref()
        this.localization = localization
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KeyboardShortcut) return false
        return key == other.key && modifiers == other.modifiers && localization == other.localization
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, key)
        result = Hasher.combine(result, modifiers)
        result = Hasher.combine(result, localization)
        return result
    }

    companion object {

        val defaultAction = KeyboardShortcut(KeyEquivalent.return_, modifiers = EventModifiers.of())
        val cancelAction = KeyboardShortcut(KeyEquivalent.escape, modifiers = EventModifiers.of())
    }
}
