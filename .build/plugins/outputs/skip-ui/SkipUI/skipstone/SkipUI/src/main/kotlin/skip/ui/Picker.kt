// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Picker<SelectionValue>: View, ListItemAdapting {
    internal val selection: Binding<SelectionValue>
    internal val label: ComposeBuilder
    internal val content: ComposeBuilder

    constructor(selection: Binding<SelectionValue>, content: () -> View, label: () -> View) {
        this.selection = selection.sref()
        this.content = ComposeBuilder.from(content)
        this.label = ComposeBuilder.from(label)
    }

    constructor(titleKey: LocalizedStringKey, selection: Binding<SelectionValue>, content: () -> View): this(selection = selection, content = content, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, selection: Binding<SelectionValue>, content: () -> View): this(selection = selection, content = content, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val views = taggedViews(context = context)
        val style = EnvironmentValues.shared._pickerStyle ?: PickerStyle.automatic
        if (EnvironmentValues.shared._labelsHidden || style != PickerStyle.navigationLink) {
            // Most picker styles do not display their label outside of a Form (see ComposeListItem)
            ComposeSelectedValue(views = views, context = context, style = style)
        } else {
            // Navigation link style outside of a List. This style does display its label
            val contentContext = context.content()
            val navigator = LocalNavigator.current.sref()
            val title = titleFromLabel(context = contentContext)
            val modifier = context.modifier.clickable(onClick = { ->
                navigator?.navigateToView(PickerSelectionView(views = views, selection = selection, title = title))
            }, enabled = EnvironmentValues.shared.isEnabled)
            ComposeContainer(modifier = modifier, fillWidth = true) { modifier ->
                Row(modifier = modifier, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { ->
                    ComposeTextButton(label = label, context = contentContext)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1.0f))
                    ComposeSelectedValue(views = views, context = contentContext, style = style, performsAction = false)
                }
            }
        }
    }

    @Composable
    private fun ComposeSelectedValue(views: Array<TagModifierView>, context: ComposeContext, style: PickerStyle, performsAction: Boolean = true) {
        val selectedValueView = views.first { it -> it.value == selection.wrappedValue } ?: EmptyView()
        val selectedValueLabel: View
        val isMenu: Boolean
        if (style == PickerStyle.automatic || style == PickerStyle.menu) {
            selectedValueLabel = HStack(spacing = 2.0) { ->
                ComposeBuilder { composectx: ComposeContext ->
                    selectedValueView.Compose(composectx)
                    Image(systemName = "chevron.down").accessibilityHidden(true).Compose(composectx)
                    ComposeResult.ok
                }
            }
            isMenu = true
        } else {
            selectedValueLabel = selectedValueView
            isMenu = false
        }
        if (performsAction) {
            val isMenuExpanded = remember { -> mutableStateOf(false) }
            Box { ->
                ComposeTextButton(label = selectedValueLabel, context = context) { -> isMenuExpanded.value = !isMenuExpanded.value }
                if (isMenu) {
                    ComposePickerSelectionMenu(views = views, isExpanded = isMenuExpanded, context = context.content())
                }
            }
        } else {
            var foregroundStyle = EnvironmentValues.shared._tint ?: Color(colorImpl = { -> androidx.compose.ui.graphics.Color.Gray })
            if (!EnvironmentValues.shared.isEnabled) {
                foregroundStyle = foregroundStyle.opacity(Double(ContentAlpha.disabled))
            }
            selectedValueLabel.foregroundStyle(foregroundStyle).Compose(context = context)
        }
    }

    @Composable
    override fun shouldComposeListItem(): Boolean = true

    @Composable
    override fun ComposeListItem(context: ComposeContext, contentModifier: Modifier) {
        val views = taggedViews(context = context)
        val style = EnvironmentValues.shared._pickerStyle ?: PickerStyle.automatic
        var isMenu = false
        val isMenuExpanded = remember { -> mutableStateOf(false) }
        val onClick: () -> Unit
        if (style == PickerStyle.navigationLink) {
            val navigator = LocalNavigator.current.sref()
            val title = titleFromLabel(context = context)
            onClick = { ->
                navigator?.navigateToView(PickerSelectionView(views = views, selection = selection, title = title))
            }
        } else {
            isMenu = true
            onClick = { -> isMenuExpanded.value = !isMenuExpanded.value }
        }
        val modifier = Modifier.clickable(onClick = onClick, enabled = EnvironmentValues.shared.isEnabled).then(contentModifier)
        Row(modifier = modifier, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { ->
            if (!EnvironmentValues.shared._labelsHidden) {
                label.Compose(context = context)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1.0f))
            }
            Box { ->
                ComposeSelectedValue(views = views, context = context, style = style, performsAction = false)
                if (isMenu) {
                    ComposePickerSelectionMenu(views = views, isExpanded = isMenuExpanded, context = context)
                }
            }
            if (style == PickerStyle.navigationLink) {
                NavigationLink.ComposeChevron()
            }
        }
    }

    @Composable
    private fun ComposePickerSelectionMenu(views: Array<TagModifierView>, isExpanded: MutableState<Boolean>, context: ComposeContext) {
        // Create selectable views from the *content* of each tag view, preserving the enclosing tag
        val menuItems = views.map l@{ tagView ->
            val button = Button(action = { -> selection.wrappedValue = tagView.value as SelectionValue }, label = { ->
                ComposeBuilder { composectx: ComposeContext ->
                    tagView.view.Compose(composectx)
                    ComposeResult.ok
                }
            })
            return@l TagModifierView(view = button, value = tagView.value, role = ComposeModifierRole.tag) as View
        }
        DropdownMenu(expanded = isExpanded.value, onDismissRequest = { -> isExpanded.value = false }) { ->
            val coroutineScope = rememberCoroutineScope()
            Menu.ComposeDropdownMenuItems(for_ = menuItems, selection = selection.wrappedValue, context = context, replaceMenu = { _ ->
                coroutineScope.launch { ->
                    delay(200) // Allow menu item selection animation to be visible
                    isExpanded.value = false
                }
            })
        }
    }

    @Composable
    private fun taggedViews(context: ComposeContext): Array<TagModifierView> {
        var views: Array<TagModifierView> = arrayOf()
        EnvironmentValues.shared.setValues({ it -> it.set_placement(ViewPlacement.tagged) }, in_ = { ->
            views = content.collectViews(context = context).compactMap { it -> TagModifierView.strip(from = it, role = ComposeModifierRole.tag) }
        })
        return views.sref()
    }

    @Composable
    private fun titleFromLabel(context: ComposeContext): Text {
        return label.collectViews(context = context).compactMap { it ->
            it.strippingModifiers(perform = { it -> it as? Text })
        }.first ?: Text(verbatim = String(describing = selection.wrappedValue))
    }

    companion object {
    }
}

class PickerStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PickerStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = PickerStyle(rawValue = 1)
        val navigationLink = PickerStyle(rawValue = 2)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val segmented = PickerStyle(rawValue = 3)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val inline = PickerStyle(rawValue = 4)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val wheel = PickerStyle(rawValue = 5)

        val menu = PickerStyle(rawValue = 6)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val palette = PickerStyle(rawValue = 7)
    }
}

internal class PickerSelectionView<SelectionValue>: View {
    internal val views: Array<TagModifierView>
    internal val selection: Binding<SelectionValue>
    internal val title: Text
    private var selectionValue: SelectionValue
        get() = _selectionValue.wrappedValue.sref({ this.selectionValue = it })
        set(newValue) {
            _selectionValue.wrappedValue = newValue.sref()
        }
    private var _selectionValue: skip.ui.State<SelectionValue>
    private lateinit var dismiss: DismissAction

    internal constructor(views: Array<TagModifierView>, selection: Binding<SelectionValue>, title: Text) {
        this.views = views.sref()
        this.selection = selection.sref()
        this.title = title
        this._selectionValue = State(initialValue = selection.wrappedValue)
    }

    override fun body(): View {
        return ComposeBuilder { composectx: ComposeContext ->
            List { ->
                ComposeBuilder { composectx: ComposeContext ->
                    ForEach(0 until views.count) { index ->
                        ComposeBuilder { composectx: ComposeContext ->
                            rowView(label = views[index]).Compose(composectx)
                            ComposeResult.ok
                        }
                    }.Compose(composectx)
                    ComposeResult.ok
                }
            }
            .navigationTitle(title).Compose(composectx)
        }
    }

    @Composable
    @Suppress("UNCHECKED_CAST")
    override fun ComposeContent(composectx: ComposeContext) {
        val rememberedselectionValue by rememberSaveable(stateSaver = composectx.stateSaver as Saver<skip.ui.State<SelectionValue>, Any>) { mutableStateOf(_selectionValue) }
        _selectionValue = rememberedselectionValue

        dismiss = EnvironmentValues.shared.dismiss

        super.ComposeContent(composectx)
    }

    private fun rowView(label: TagModifierView): View {
        return ComposeBuilder { composectx: ComposeContext ->
            Button(action = { ->
                selection.wrappedValue = label.value as SelectionValue
                selectionValue = selection.wrappedValue // Update the checkmark in the UI while we dismiss
                dismiss()
            }, label = { ->
                ComposeBuilder { composectx: ComposeContext ->
                    HStack { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            label.Compose(composectx)
                            Spacer().Compose(composectx)
                            if (label.value == selection.wrappedValue) {
                                Image(systemName = "checkmark")
                                    .foregroundStyle(EnvironmentValues.shared._tint ?: Color.accentColor).Compose(composectx)
                            }
                            ComposeResult.ok
                        }
                    }.Compose(composectx)
                    ComposeResult.ok
                }
            })
            .buttonStyle(ButtonStyle.plain).Compose(composectx)
        }
    }
}

