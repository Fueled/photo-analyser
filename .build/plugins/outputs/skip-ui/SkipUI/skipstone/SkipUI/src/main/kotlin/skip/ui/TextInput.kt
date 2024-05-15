// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.ui.text.input.KeyboardCapitalization

enum class TextInputAutocapitalization: Sendable {
    never,
    words,
    sentences,
    characters;

    internal fun asKeyboardCapitalization(): KeyboardCapitalization {
        when (this) {
            TextInputAutocapitalization.never -> return KeyboardCapitalization.None.sref()
            TextInputAutocapitalization.words -> return KeyboardCapitalization.Words.sref()
            TextInputAutocapitalization.sentences -> return KeyboardCapitalization.Sentences.sref()
            TextInputAutocapitalization.characters -> return KeyboardCapitalization.Characters.sref()
        }
    }

    companion object {
    }
}

