// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Modifier

/// Context to provide modifiers, parent, etc to composables.
class ComposeContext: MutableStruct {
    /// Modifiers to apply.
    var modifier: Modifier
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    /// Mechanism for a parent view to change how a child view is composed.
    var composer: Composer? = null
        get() = field.sref({ this.composer = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    /// Use in conjunction with `rememberSaveable` to store view state.
    var stateSaver: Saver<Any?, Any>
        get() = field.sref({ this.stateSaver = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    /// The context to pass to child content of a container view.
    ///
    /// By default, modifiers and the `composer` are reset for child content.
    fun content(modifier: Modifier = Modifier, composer: Composer? = null, stateSaver: Saver<Any?, Any>? = null): ComposeContext {
        var context = this.sref()
        context.modifier = modifier
        context.composer = composer
        context.stateSaver = stateSaver ?: this.stateSaver
        return context.sref()
    }

    constructor(modifier: Modifier = Modifier, composer: Composer? = null, stateSaver: Saver<Any?, Any> = ComposeStateSaver()) {
        this.modifier = modifier
        this.composer = composer
        this.stateSaver = stateSaver
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ComposeContext(modifier, composer, stateSaver)

    companion object {
    }
}

/// The result of composing content.
///
/// Reserved for future use. Having a return value also expands recomposition scope. See `ComposeBuilder` for details.
class ComposeResult {

    companion object {
        val ok = ComposeResult()
    }
}

/// Mechanism for a parent view to change how a child view is composed.
interface Composer {
}

/// Base type for composers that render content.
open class RenderingComposer: Composer {
    private val compose: (@Composable (View, (Boolean) -> ComposeContext) -> Unit)?

    /// Optionally provide a compose block to execute instead of subclassing.
    ///
    /// - Note: This is a separate method from the default constructor rather than giving `compose` a default value to work around Kotlin runtime
    ///   crashes related to using composable closures.
    internal constructor(compose: @Composable (View, (Boolean) -> ComposeContext) -> Unit) {
        this.compose = compose
    }

    internal constructor() {
        this.compose = null
    }

    /// Called before a `ComposeBuilder` composes its content.
    open fun willCompose() = Unit

    /// Called after a `ComposeBuilder` composes its content.
    open fun didCompose(result: ComposeResult) = Unit

    /// Compose the given view's content.
    ///
    /// - Parameter context: The context to use to render the view, optionally retaining this composer.
    @Composable
    open fun Compose(view: View, context: (Boolean) -> ComposeContext) {
        val matchtarget_0 = compose
        if (matchtarget_0 != null) {
            val compose = matchtarget_0
            compose(view, context)
        } else {
            view.ComposeContent(context = context(false))
        }
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

/// Base type for composers that are used for side effects.
///
/// Side effect composers are escaping, meaning that if the internal content needs to recompose, the calling context will also recompose.
open class SideEffectComposer: Composer {
    private val compose: (@Composable (View, (Boolean) -> ComposeContext) -> ComposeResult)?

    /// Optionally provide a compose block to execute instead of subclassing.
    ///
    /// - Note: This is a separate method from the default constructor rather than giving `compose` a default value to work around Kotlin runtime
    ///   crashes related to using composable closures.
    internal constructor(compose: @Composable (View, (Boolean) -> ComposeContext) -> ComposeResult) {
        this.compose = compose
    }

    internal constructor() {
        this.compose = null
    }

    @Composable
    open fun Compose(view: View, context: (Boolean) -> ComposeContext): ComposeResult {
        val matchtarget_1 = compose
        if (matchtarget_1 != null) {
            val compose = matchtarget_1
            return compose(view, context)
        } else {
            return ComposeResult.ok
        }
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

