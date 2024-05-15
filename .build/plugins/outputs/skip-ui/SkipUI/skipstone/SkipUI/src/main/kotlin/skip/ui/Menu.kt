// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Use a class to avoid copying so that we can update our toggleMenu action on the current instance
class Menu: View {
    internal val content: ComposeBuilder
    internal val label: ComposeBuilder
    internal val primaryAction: (() -> Unit)?
    internal var toggleMenu: () -> Unit = { ->  }

    constructor(content: () -> View, label: () -> View) {
        this.content = ComposeBuilder.from(content)
        this.label = ComposeBuilder(view = Button(action = { -> this.toggleMenu() }, label = label))
        this.primaryAction = null
    }

    constructor(titleKey: LocalizedStringKey, content: () -> View): this(content = content, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, content: () -> View): this(content = content, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(content: () -> View, label: () -> View, primaryAction: () -> Unit) {
        this.content = ComposeBuilder.from(content)
        // We don't use a Button because we can't attach a long press detector to it
        // So currently, any Menu with a primaryAction ignores .buttonStyle
        this.label = ComposeBuilder.from(label)
        this.primaryAction = primaryAction
    }

    constructor(titleKey: LocalizedStringKey, content: () -> View, primaryAction: () -> Unit): this(content = content, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }, primaryAction = primaryAction) {
    }

    constructor(title: String, content: () -> View, primaryAction: () -> Unit): this(content = content, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }, primaryAction = primaryAction) {

    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val contentContext = context.content()
        val isEnabled = EnvironmentValues.shared.isEnabled
        ComposeContainer(eraseAxis = true, modifier = context.modifier) { modifier ->
            Box(modifier = modifier) { ->
                val matchtarget_0 = primaryAction
                if (matchtarget_0 != null) {
                    val primaryAction = matchtarget_0
                    val primaryActionModifier = Modifier.combinedClickable(enabled = isEnabled, onLongClick = { -> toggleMenu() }, onClick = primaryAction)
                    ComposeTextButton(label = label, context = context.content(modifier = primaryActionModifier))
                } else {
                    label.Compose(context = contentContext)
                }
                if (isEnabled) {
                    // We default to displaying our own content, but if the user selects a nested menu we can present
                    // that instead. The nested menu selection is cleared on dismiss
                    val isMenuExpanded = remember { -> mutableStateOf(false) }
                    val nestedMenu = remember { -> mutableStateOf<Menu?>(null) }
                    val coroutineScope = rememberCoroutineScope()
                    toggleMenu = { ->
                        nestedMenu.value = null
                        isMenuExpanded.value = !isMenuExpanded.value
                    }
                    val replaceMenu: (Menu?) -> Unit = { menu ->
                        coroutineScope.launch { ->
                            delay(200) // Allow menu item selection animation to be visible
                            isMenuExpanded.value = false
                            delay(100) // Otherwise we see a flash of the primary menu on nested menu dismiss
                            nestedMenu.value = null
                            if (menu != null) {
                                nestedMenu.value = menu
                                isMenuExpanded.value = true
                            }
                        }
                    }
                    DropdownMenu(expanded = isMenuExpanded.value, onDismissRequest = { ->
                        isMenuExpanded.value = false
                        coroutineScope.launch { ->
                            delay(100) // Otherwise we see a flash of the primary menu on nested menu dismiss
                            nestedMenu.value = null
                        }
                    }) { ->
                        val itemViews = (nestedMenu.value?.content ?: content).collectViews(context = context)
                        Companion.ComposeDropdownMenuItems(for_ = itemViews, context = contentContext, replaceMenu = replaceMenu)
                    }
                } else {
                    toggleMenu = { ->  }
                }
            }
        }
    }

    companion object {

        @Composable
        internal fun ComposeDropdownMenuItems(for_: Array<View>, selection: Hashable? = null, context: ComposeContext, replaceMenu: (Menu?) -> Unit) {
            val itemViews = for_
            for (itemView in itemViews.sref()) {
                itemView.strippingModifiers(perform = { it -> it })?.let { strippedItemView ->
                    val matchtarget_1 = strippedItemView as? Button
                    if (matchtarget_1 != null) {
                        val button = matchtarget_1
                        val isSelected: Boolean?
                        val matchtarget_2 = itemView as? TagModifierView
                        if (matchtarget_2 != null) {
                            val tagView = matchtarget_2
                            if (tagView.role == ComposeModifierRole.tag) {
                                isSelected = tagView.value == selection
                            } else {
                                isSelected = null
                            }
                        } else {
                            isSelected = null
                        }
                        ComposeDropdownMenuItem(for_ = button.label, context = context, isSelected = isSelected) { ->
                            button.action()
                            replaceMenu(null)
                        }
                    } else {
                        val matchtarget_3 = strippedItemView as? Text
                        if (matchtarget_3 != null) {
                            val text = matchtarget_3
                            DropdownMenuItem(text = { -> text.Compose(context = context) }, onClick = { ->  }, enabled = false)
                        } else {
                            val matchtarget_4 = strippedItemView as? Section
                            if (matchtarget_4 != null) {
                                val section = matchtarget_4
                                section.header?.let { header ->
                                    DropdownMenuItem(text = { -> header.Compose(context = context) }, onClick = { ->  }, enabled = false)
                                }
                                val sectionViews = section.content.collectViews(context = context)
                                ComposeDropdownMenuItems(for_ = sectionViews, context = context, replaceMenu = replaceMenu)
                                Divider().Compose(context = context)
                            } else {
                                val matchtarget_5 = strippedItemView as? Menu
                                if (matchtarget_5 != null) {
                                    val menu = matchtarget_5
                                    menu.label.collectViews(context = context).first?.strippingModifiers(perform = { it -> it as? Button })?.let { button ->
                                        ComposeDropdownMenuItem(for_ = button.label, context = context) { -> replaceMenu(menu) }
                                    }
                                } else {
                                    // Dividers are also supported... maybe other view types?
                                    itemView.Compose(context = context)
                                }
                            }
                        }
                    }
                }
            }
        }

        @Composable
        private fun ComposeDropdownMenuItem(for_: ComposeBuilder, context: ComposeContext, isSelected: Boolean? = null, action: () -> Unit) {
            val view = for_
            val label = view.collectViews(context = context).first?.strippingModifiers(perform = { it -> it as? Label })
            if (isSelected != null) {
                val selectedIcon: @Composable () -> Unit
                if (isSelected) {
                    selectedIcon = { -> Icon(imageVector = Icons.Outlined.Check, contentDescription = "selected") }
                } else {
                    selectedIcon = { ->  }
                }
                if (label != null) {
                    DropdownMenuItem(text = { -> label.ComposeTitle(context = context) }, leadingIcon = selectedIcon, trailingIcon = { -> label.ComposeImage(context = context) }, onClick = action)
                } else {
                    DropdownMenuItem(text = { -> view.Compose(context = context) }, leadingIcon = selectedIcon, onClick = action)
                }
            } else if (label != null) {
                DropdownMenuItem(text = { -> label.ComposeTitle(context = context) }, trailingIcon = { -> label.ComposeImage(context = context) }, onClick = action)
            } else {
                DropdownMenuItem(text = { -> view.Compose(context = context) }, onClick = action)
            }
        }
    }
}

class MenuStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MenuStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = MenuStyle(rawValue = 0)
        val button = MenuStyle(rawValue = 1)
    }
}

class MenuActionDismissBehavior: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MenuActionDismissBehavior) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = MenuActionDismissBehavior(rawValue = 0)
        val enabled = MenuActionDismissBehavior(rawValue = 0)
        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val disabled = MenuActionDismissBehavior(rawValue = 1)
    }
}

class MenuOrder: RawRepresentable<Int>, Sendable {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MenuOrder) return false
        return rawValue == other.rawValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, rawValue)
        return result
    }

    companion object {

        val automatic = MenuOrder(rawValue = 0)
        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val priority = MenuOrder(rawValue = 1)
        val fixed = MenuOrder(rawValue = 2)
    }
}

