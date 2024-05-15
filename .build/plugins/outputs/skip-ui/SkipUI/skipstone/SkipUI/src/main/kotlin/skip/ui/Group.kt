// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

class Group: View {
    internal val content: ComposeBuilder

    constructor(content: () -> View) {
        this.content = ComposeBuilder.from(content)
    }

    @Composable
    override fun Compose(context: ComposeContext): ComposeResult {
        ComposeContent(context = context)
        return ComposeResult.ok
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        content.Compose(context = context)
    }

    companion object {
    }
}
