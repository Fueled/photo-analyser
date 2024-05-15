// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.model.*
import androidx.compose.runtime.Composable

interface ViewModifier {
    fun body(content: View): View = content

    /// Compose this modifier's content.
    @Composable
    fun Compose(content: View, context: ComposeContext) {
        // Unfortunately we can't use try/finally around a @Composable invocation
        StateTracking.pushBody()
        body(content = content).Compose(context = context)
        StateTracking.popBody()
    }
}


