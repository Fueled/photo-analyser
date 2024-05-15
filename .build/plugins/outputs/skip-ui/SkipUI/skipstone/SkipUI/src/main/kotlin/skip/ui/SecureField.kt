// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

class SecureField: View {
    internal val textField: TextField

    constructor(text: Binding<String>, prompt: Text? = null, label: () -> View) {
        textField = TextField(text = text, prompt = prompt, isSecure = true, label = label)
    }

    constructor(title: String, text: Binding<String>, prompt: Text? = null): this(text = text, prompt = prompt, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, text: Binding<String>, prompt: Text? = null): this(text = text, prompt = prompt, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        textField.Compose(context = context)
    }

    companion object {
    }
}
