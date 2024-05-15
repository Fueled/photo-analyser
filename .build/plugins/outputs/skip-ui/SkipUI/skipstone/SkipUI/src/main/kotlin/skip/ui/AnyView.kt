// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

class AnyView: View {
    private val view: View

    constructor(view: View) {
        this.view = view.sref()
    }

    constructor(erasing: View, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        val view = erasing
        this.view = view.sref()
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        view.Compose(context = context)
    }

    companion object {
    }
}
