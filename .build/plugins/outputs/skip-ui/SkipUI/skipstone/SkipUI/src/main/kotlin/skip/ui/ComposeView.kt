// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

/// Used to directly wrap user Compose content.
///
/// - Seealso: `ComposeBuilder`
class ComposeView: View {
    private val content: @Composable (ComposeContext) -> Unit

    /// Constructor.
    ///
    /// The supplied `content` is the content to compose.
    constructor(content: @Composable (ComposeContext) -> Unit) {
        this.content = content
    }

    @Composable
    override fun ComposeContent(context: ComposeContext): Unit = content(context)

    companion object {
    }
}
