// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.layout.Row
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Toggle: View {
    internal val isOn: Binding<Boolean>
    internal val label: View

    constructor(isOn: Binding<Boolean>, label: () -> View) {
        this.isOn = isOn.sref()
        this.label = label()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(sources: Any, isOn: (Any) -> Binding<Boolean>, label: () -> View): this(isOn = isOn(0), label = label) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(titleKey: LocalizedStringKey, sources: Any, isOn: (Any) -> Binding<Boolean>): this(isOn = isOn(0), label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(title: String, sources: Any, isOn: (Any) -> Binding<Boolean>): this(isOn = isOn(0), label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, isOn: Binding<Boolean>): this(isOn = isOn, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, isOn: Binding<Boolean>): this(isOn = isOn, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val colors: SwitchColors
        val matchtarget_0 = EnvironmentValues.shared._tint
        if (matchtarget_0 != null) {
            val tint = matchtarget_0
            val tintColor = tint.colorImpl()
            colors = SwitchDefaults.colors(checkedTrackColor = tintColor, disabledCheckedTrackColor = tintColor.copy(alpha = ContentAlpha.disabled))
        } else {
            colors = SwitchDefaults.colors()
        }
        if (EnvironmentValues.shared._labelsHidden) {
            PaddingLayout(padding = EdgeInsets(top = -6.0, leading = 0.0, bottom = -6.0, trailing = 0.0), context = context) { context ->
                Switch(modifier = context.modifier, checked = isOn.wrappedValue, onCheckedChange = { it -> isOn.wrappedValue = it }, enabled = EnvironmentValues.shared.isEnabled, colors = colors)
            }
        } else {
            val contentContext = context.content()
            ComposeContainer(modifier = context.modifier, fillWidth = true) { modifier ->
                Row(modifier = modifier, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { ->
                    label.Compose(context = contentContext)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1.0f))
                    PaddingLayout(padding = EdgeInsets(top = -6.0, leading = 0.0, bottom = -6.0, trailing = 0.0), context = context) { context ->
                        Switch(checked = isOn.wrappedValue, onCheckedChange = { it -> isOn.wrappedValue = it }, enabled = EnvironmentValues.shared.isEnabled, colors = colors)
                    }
                }
            }
        }
    }

    companion object {
    }
}

class ToggleStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ToggleStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = ButtonStyle(rawValue = 0)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val button = ButtonStyle(rawValue = 1)

        val switch = ButtonStyle(rawValue = 2)
    }
}

