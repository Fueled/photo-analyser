// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

class TextEditor: View {
    internal val text: Binding<String>

    constructor(text: Binding<String>) {
        this.text = text.sref()
    }

    @ExperimentalMaterial3Api
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val contentContext = context.content()
        val keyboardOptions = (EnvironmentValues.shared._keyboardOptions ?: KeyboardOptions.Default).sref()
        val keyboardActions = KeyboardActions(EnvironmentValues.shared._onSubmitState, LocalFocusManager.current)
        val visualTransformation = VisualTransformation.None.sref()
        TextField(value = text.wrappedValue, onValueChange = { it -> text.wrappedValue = it }, modifier = context.modifier.fillWidth(), enabled = EnvironmentValues.shared.isEnabled, singleLine = false, keyboardOptions = keyboardOptions, keyboardActions = keyboardActions, visualTransformation = visualTransformation)
    }

    companion object {
    }
}

class TextEditorStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TextEditorStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = TextEditorStyle(rawValue = 0)
        val plain = TextEditorStyle(rawValue = 1)
    }
}

