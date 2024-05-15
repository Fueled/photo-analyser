// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

class TextField: View {
    internal val text: Binding<String>
    internal val label: ComposeBuilder
    internal val prompt: Text?
    internal val isSecure: Boolean

    constructor(text: Binding<String>, prompt: Text? = null, isSecure: Boolean = false, label: () -> View) {
        this.text = text.sref()
        this.label = ComposeBuilder.from(label)
        this.prompt = prompt
        this.isSecure = isSecure
    }

    constructor(title: String, text: Binding<String>, prompt: Text? = null): this(text = text, prompt = prompt, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, text: Binding<String>, prompt: Text? = null): this(text = text, prompt = prompt, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(titleKey: LocalizedStringKey, text: Binding<String>, axis: Axis): this(titleKey, text = text) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(titleKey: LocalizedStringKey, text: Binding<String>, prompt: Text?, axis: Axis): this(titleKey, text = text, prompt = prompt) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(title: String, text: Binding<String>, axis: Axis): this(title, text = text) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(title: String, text: Binding<String>, prompt: Text?, axis: Axis): this(title, text = text, prompt = prompt) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(text: Binding<String>, prompt: Text? = null, axis: Axis, label: () -> View): this(text = text, prompt = prompt, label = label) {
    }

    @ExperimentalMaterial3Api
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val contentContext = context.content()
        val keyboardOptions = (EnvironmentValues.shared._keyboardOptions ?: KeyboardOptions.Default).sref()
        val keyboardActions = KeyboardActions(EnvironmentValues.shared._onSubmitState, LocalFocusManager.current)
        val colors = Companion.colors(context = context)
        val visualTransformation = (if (isSecure) PasswordVisualTransformation() else VisualTransformation.None).sref()
        OutlinedTextField(value = text.wrappedValue, onValueChange = { it -> text.wrappedValue = it }, placeholder = { -> Companion.Placeholder(prompt = prompt ?: label, context = contentContext) }, modifier = context.modifier.fillWidth(), enabled = EnvironmentValues.shared.isEnabled, singleLine = true, keyboardOptions = keyboardOptions, keyboardActions = keyboardActions, colors = colors, visualTransformation = visualTransformation)
    }

    companion object {

        @Composable
        internal fun textColor(enabled: Boolean, context: ComposeContext): androidx.compose.ui.graphics.Color {
            val color = EnvironmentValues.shared._foregroundStyle?.asColor(opacity = 1.0, animationContext = context) ?: Color.primary.colorImpl()
            if (enabled) {
                return color
            } else {
                return color.copy(alpha = ContentAlpha.disabled)
            }
        }

        @ExperimentalMaterial3Api
        @Composable
        internal fun colors(context: ComposeContext): TextFieldColors {
            val textColor = textColor(enabled = true, context = context)
            val disabledTextColor = textColor(enabled = false, context = context)
            val isPlainStyle = EnvironmentValues.shared._textFieldStyle == TextFieldStyle.plain
            if (isPlainStyle) {
                val clearColor = androidx.compose.ui.graphics.Color.Transparent.sref()
                val matchtarget_0 = EnvironmentValues.shared._tint
                if (matchtarget_0 != null) {
                    val tint = matchtarget_0
                    val tintColor = tint.colorImpl()
                    return TextFieldDefaults.outlinedTextFieldColors(focusedTextColor = textColor, unfocusedTextColor = textColor, disabledTextColor = disabledTextColor, cursorColor = tintColor, focusedBorderColor = clearColor, unfocusedBorderColor = clearColor, disabledBorderColor = clearColor, errorBorderColor = clearColor)
                } else {
                    return TextFieldDefaults.outlinedTextFieldColors(focusedTextColor = textColor, unfocusedTextColor = textColor, disabledTextColor = disabledTextColor, focusedBorderColor = clearColor, unfocusedBorderColor = clearColor, disabledBorderColor = clearColor, errorBorderColor = clearColor)
                }
            } else {
                val matchtarget_1 = EnvironmentValues.shared._tint
                if (matchtarget_1 != null) {
                    val tint = matchtarget_1
                    val tintColor = tint.colorImpl()
                    return TextFieldDefaults.outlinedTextFieldColors(focusedTextColor = textColor, unfocusedTextColor = textColor, disabledTextColor = disabledTextColor, cursorColor = tintColor, focusedBorderColor = tintColor)
                } else {
                    return TextFieldDefaults.outlinedTextFieldColors(focusedTextColor = textColor, unfocusedTextColor = textColor, disabledTextColor = disabledTextColor)
                }
            }
        }

        @Composable
        internal fun Placeholder(prompt: View?, context: ComposeContext) {
            if (prompt == null) {
                return
            }
            EnvironmentValues.shared.setValues({ it ->
                it.set_foregroundStyle(Color(colorImpl = { -> Color.primary.colorImpl().copy(alpha = ContentAlpha.disabled) }))
            }, in_ = { -> prompt.Compose(context = context) })
        }
    }
}

class TextFieldStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TextFieldStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = TextFieldStyle(rawValue = 0)
        val roundedBorder = TextFieldStyle(rawValue = 1)
        val plain = TextFieldStyle(rawValue = 2)
    }
}

/// State for `onSubmit` actions.
internal class OnSubmitState {
    internal val actions: Array<Tuple2<SubmitTriggers, () -> Unit>>

    internal constructor(triggers: SubmitTriggers, action: () -> Unit) {
        actions = arrayOf(Tuple2(triggers.sref(), action))
    }

    private constructor(actions: Array<Tuple2<SubmitTriggers, () -> Unit>>) {
        this.actions = actions.sref()
    }

    internal fun appending(triggers: SubmitTriggers, action: () -> Unit): OnSubmitState = OnSubmitState(actions = actions + arrayOf(Tuple2(triggers.sref(), action)))

    internal fun appending(state: OnSubmitState): OnSubmitState = OnSubmitState(actions = actions + state.actions)

    internal fun onSubmit(trigger: SubmitTriggers) {
        for (action in actions.sref()) {
            if (action.element0.contains(trigger)) {
                action.element1()
            }
        }
    }
}

/// Create keyboard actions that execute the given submit state.
internal fun KeyboardActions(submitState: OnSubmitState?, clearFocusWith: FocusManager? = null): KeyboardActions {
    return KeyboardActions(onDone = { ->
        clearFocusWith?.clearFocus()
        submitState?.onSubmit(trigger = SubmitTriggers.text)
    }, onGo = { ->
        clearFocusWith?.clearFocus()
        submitState?.onSubmit(trigger = SubmitTriggers.text)
    }, onNext = { ->
        clearFocusWith?.clearFocus()
        submitState?.onSubmit(trigger = SubmitTriggers.text)
    }, onPrevious = { ->
        clearFocusWith?.clearFocus()
        submitState?.onSubmit(trigger = SubmitTriggers.text)
    }, onSearch = { ->
        clearFocusWith?.clearFocus()
        submitState?.onSubmit(trigger = SubmitTriggers.search)
    }, onSend = { ->
        clearFocusWith?.clearFocus()
        submitState?.onSubmit(trigger = SubmitTriggers.text)
    })
}

