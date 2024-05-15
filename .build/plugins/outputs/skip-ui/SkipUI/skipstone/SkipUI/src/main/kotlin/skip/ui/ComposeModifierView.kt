// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

/// Recognized modifier roles.
enum class ComposeModifierRole {
    accessibility,
    editActions,
    gesture,
    id,
    listItem,
    spacing,
    tabItem,
    tag,
    transition,
    unspecified,
    zIndex;

    companion object {
    }
}

/// Used internally by modifiers to apply changes to the context supplied to modified views.
internal open class ComposeModifierView: View {
    internal val view: View
    internal val role: ComposeModifierRole
    internal open var action: (@Composable (InOut<ComposeContext>) -> ComposeResult)? = null
    internal open var composeContent: (@Composable (View, ComposeContext) -> Unit)? = null

    /// Constructor for subclasses.
    internal constructor(view: View, role: ComposeModifierRole = ComposeModifierRole.unspecified) {
        // Don't copy view
        this.view = view
        this.role = role
    }

    /// A modfiier that performs an action, optionally modifying the `ComposeContext` passed to the modified view.
    internal constructor(targetView: View, role: ComposeModifierRole = ComposeModifierRole.unspecified, action: @Composable (InOut<ComposeContext>) -> ComposeResult): this(view = targetView, role = role) {
        this.action = action
    }

    /// A modifier that takes over the composition of the modified view.
    internal constructor(contentView: View, role: ComposeModifierRole = ComposeModifierRole.unspecified, composeContent: @Composable (View, ComposeContext) -> Unit): this(view = contentView, role = role) {
        this.composeContent = composeContent
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val matchtarget_0 = composeContent
        if (matchtarget_0 != null) {
            val composeContent = matchtarget_0
            composeContent(view, context)
        } else {
            val matchtarget_1 = action
            if (matchtarget_1 != null) {
                val action = matchtarget_1
                var context = context.sref()
                action(InOut({ context }, { context = it }))
                view.Compose(context = context)
            } else {
                view.Compose(context = context)
            }
        }
    }

    override fun <R> strippingModifiers(until: (ComposeModifierRole) -> Boolean, perform: (View?) -> R): R {
        if (until(role)) {
            return perform(this)
        } else {
            return view.strippingModifiers(until = until, perform = perform)
        }
    }
}
