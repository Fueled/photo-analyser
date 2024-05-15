// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Button: View, ListItemAdapting {
    internal val action: () -> Unit
    internal val label: ComposeBuilder
    internal val role: ButtonRole?

    constructor(action: () -> Unit, label: () -> View): this(role = null, action = action, label = label) {
    }

    constructor(title: String, action: () -> Unit): this(action = action, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, action: () -> Unit): this(action = action, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(role: ButtonRole?, action: () -> Unit, label: () -> View) {
        this.role = role
        this.action = action
        this.label = ComposeBuilder.from(label)
    }

    constructor(title: String, role: ButtonRole?, action: () -> Unit): this(role = role, action = action, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, role: ButtonRole?, action: () -> Unit): this(role = role, action = action, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val buttonStyle = EnvironmentValues.shared._buttonStyle
        ComposeContainer(modifier = context.modifier) { modifier ->
            when (buttonStyle) {
                ButtonStyle.bordered -> {
                    val tint = if (role == ButtonRole.destructive) Color.red else EnvironmentValues.shared._tint
                    val colors: ButtonColors
                    if (tint != null) {
                        val tintColor = tint.colorImpl()
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = tintColor.copy(alpha = 0.15f), contentColor = tintColor, disabledContainerColor = tintColor.copy(alpha = 0.15f), disabledContentColor = tintColor.copy(alpha = ContentAlpha.medium))
                    } else {
                        colors = ButtonDefaults.filledTonalButtonColors()
                    }
                    val placement = EnvironmentValues.shared._placement.sref()
                    val contentContext = context.content()
                    EnvironmentValues.shared.setValues({ it -> it.set_placement(placement.union(ViewPlacement.systemTextColor)) }, in_ = { ->
                        FilledTonalButton(onClick = action, modifier = modifier, enabled = EnvironmentValues.shared.isEnabled, colors = colors) { -> label.Compose(context = contentContext) }
                    })
                }
                ButtonStyle.borderedProminent -> {
                    val tint = if (role == ButtonRole.destructive) Color.red else EnvironmentValues.shared._tint
                    val colors: ButtonColors
                    if (tint != null) {
                        val tintColor = tint.colorImpl()
                        colors = ButtonDefaults.buttonColors(containerColor = tintColor, disabledContainerColor = tintColor.copy(alpha = ContentAlpha.disabled))
                    } else {
                        colors = ButtonDefaults.buttonColors()
                    }
                    val placement = EnvironmentValues.shared._placement.sref()
                    val contentContext = context.content()
                    EnvironmentValues.shared.setValues({ it -> it.set_placement(placement.union(ViewPlacement.systemTextColor)) }, in_ = { ->
                        androidx.compose.material3.Button(onClick = action, modifier = modifier, enabled = EnvironmentValues.shared.isEnabled, colors = colors) { -> label.Compose(context = contentContext) }
                    })
                }
                ButtonStyle.plain -> ComposeTextButton(label = label, context = context.content(modifier = modifier), role = role, isPlain = true, action = action)
                else -> ComposeTextButton(label = label, context = context.content(modifier = modifier), role = role, action = action)
            }
        }
    }

    @Composable
    override fun shouldComposeListItem(): Boolean {
        val buttonStyle = EnvironmentValues.shared._buttonStyle
        return buttonStyle == null || buttonStyle == ButtonStyle.automatic || buttonStyle == ButtonStyle.plain
    }

    @Composable
    override fun ComposeListItem(context: ComposeContext, contentModifier: Modifier) {
        Box(modifier = Modifier.clickable(onClick = action, enabled = EnvironmentValues.shared.isEnabled).then(contentModifier), contentAlignment = androidx.compose.ui.Alignment.CenterStart) { -> ComposeTextButton(label = label, context = context, isPlain = EnvironmentValues.shared._buttonStyle == ButtonStyle.plain, role = role) }
    }

    companion object {
    }
}

/// Render a plain-style button.
@Composable
internal fun ComposeTextButton(label: View, context: ComposeContext, role: ButtonRole? = null, isPlain: Boolean = false, action: (() -> Unit)? = null) {
    var foregroundStyle: ShapeStyle
    if (role == ButtonRole.destructive) {
        foregroundStyle = Color.red
    } else {
        foregroundStyle = (EnvironmentValues.shared._foregroundStyle ?: (if (isPlain) Color.primary else (EnvironmentValues.shared._tint ?: Color.accentColor))).sref()
    }
    val isEnabled = EnvironmentValues.shared.isEnabled
    if (!isEnabled) {
        val disabledAlpha = Double(ContentAlpha.disabled)
        foregroundStyle = AnyShapeStyle(foregroundStyle, opacity = disabledAlpha)
    }

    var modifier = context.modifier
    if (action != null) {
        modifier = modifier.clickable(onClick = action, enabled = isEnabled)
    }
    val contentContext = context.content(modifier = modifier)

    EnvironmentValues.shared.setValues({ it -> it.set_foregroundStyle(foregroundStyle) }, in_ = { -> label.Compose(context = contentContext) })
}

class ButtonStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ButtonStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = ButtonStyle(rawValue = 0)
        val plain = ButtonStyle(rawValue = 1)
        val borderless = ButtonStyle(rawValue = 2)
        val bordered = ButtonStyle(rawValue = 3)
        val borderedProminent = ButtonStyle(rawValue = 4)
    }
}

enum class ButtonRepeatBehavior: Sendable {
    automatic,
    enabled,
    disabled;

    companion object {
    }
}

enum class ButtonRole: Sendable {
    destructive,
    cancel;

    companion object {
    }
}

