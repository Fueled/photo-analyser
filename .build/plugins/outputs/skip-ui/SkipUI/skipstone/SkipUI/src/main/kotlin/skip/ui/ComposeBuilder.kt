// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.runtime.Composable

/// Used to wrap the content of SwiftUI `@ViewBuilders` for rendering by Compose.
class ComposeBuilder: View {
    private val content: @Composable (ComposeContext) -> ComposeResult

    /// Construct with static content.
    ///
    /// Used primarily when manually constructing views for internal use.
    constructor(view: View): this(content = l@{ context -> return@l view.Compose(context = context) }) {
    }

    /// Constructor.
    ///
    /// The supplied `content` is the content to compose. When transpiling SwiftUI code, this is the logic embedded in the user's `body` and within each container view in
    /// that `body`, as well as within other `@ViewBuilders`.
    ///
    /// - Note: Returning a result from `content` is important. This prevents Compose from recomposing `content` on its own. Instead, a change that would recompose
    ///   `content` elevates to our void `ComposeContent` function. This allows us to prepare for recompositions, e.g. making the proper callbacks to the context's `composer`.
    constructor(content: @Composable (ComposeContext) -> ComposeResult) {
        this.content = content
    }

    @Composable
    override fun Compose(context: ComposeContext): ComposeResult {
        // If there is a composer that should recompose its caller, we execute it here so that its result escapes.
        // Otherwise we wait for ComposeContent where recomposes don't affect the caller
        val matchtarget_0 = context.composer as? SideEffectComposer
        if (matchtarget_0 != null) {
            val composer = matchtarget_0
            return content(context)
        } else {
            ComposeContent(context)
            return ComposeResult.ok
        }
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val matchtarget_1 = context.composer as? RenderingComposer
        if (matchtarget_1 != null) {
            val composer = matchtarget_1
            composer.willCompose()
            val result = content(context)
            composer.didCompose(result = result)
        } else {
            content(context)
        }
    }

    /// Use a custom composer to collect the views composed within this view.
    @Composable
    fun collectViews(context: ComposeContext): Array<View> {
        var views: Array<View> = arrayOf()
        val viewCollectingContext = context.content(composer = SideEffectComposer l@{ view, _ ->
            views.append(view)
            return@l ComposeResult.ok
        })
        content(viewCollectingContext)
        return views.sref()
    }

    companion object {

        /// If the result of the given block is a `ComposeBuilder` return it, else create a `ComposeBuilder` whose content is the
        /// resulting view.
        fun from(content: () -> View): ComposeBuilder {
            val view = content()
            return view as? ComposeBuilder ?: ComposeBuilder(view = view)
        }
    }
}
