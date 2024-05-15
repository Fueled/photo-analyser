// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.foundation.*
import androidx.compose.runtime.Composable

// Use a class to be able to update our openURL action on compose by reference.
class Link: View {
    internal val content: Button
    internal var openURL = OpenURLAction.default

    constructor(destination: URL, label: () -> View) {
        content = Button(action = { -> this.openURL(destination) }, label = label)
    }

    constructor(titleKey: LocalizedStringKey, destination: URL): this(destination = destination, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, destination: URL): this(destination = destination, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        openURL = EnvironmentValues.shared.openURL
        content.Compose(context = context)
    }

    companion object {
    }
}
