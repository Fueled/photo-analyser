// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.foundation.*
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity

// Use a class to be able to update our openURL action on compose by reference.
class ShareLink: View {

    internal val text: Text
    internal val subject: Text?
    internal val message: Text?
    internal val content: Button
    internal var action: () -> Unit

    internal constructor(text: Text, subject: Text? = null, message: Text? = null, label: () -> View) {
        this.text = text
        this.subject = subject
        this.message = message
        this.action = { ->  }
        this.content = Button(action = { -> this.action() }, label = label)
    }

    constructor(item: URL, subject: Text? = null, message: Text? = null, label: () -> View): this(text = Text(item.absoluteString), subject = subject, message = message, label = label) {
    }

    constructor(item: String, subject: Text? = null, message: Text? = null, label: () -> View): this(text = Text(item), subject = subject, message = message, label = label) {
    }

    constructor(item: URL, subject: Text? = null, message: Text? = null): this(text = Text(item.absoluteString), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Image(systemName = Companion.defaultSystemImageName).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(item: String, subject: Text? = null, message: Text? = null): this(text = Text(item), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Image(systemName = Companion.defaultSystemImageName).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, item: URL, subject: Text? = null, message: Text? = null): this(text = Text(item.absoluteString), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Label(titleKey, systemImage = Companion.defaultSystemImageName).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(titleKey: LocalizedStringKey, item: String, subject: Text? = null, message: Text? = null): this(text = Text(item), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Label(titleKey, systemImage = Companion.defaultSystemImageName).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, item: URL, subject: Text? = null, message: Text? = null): this(text = Text(item.absoluteString), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Label(title, systemImage = Companion.defaultSystemImageName).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, item: String, subject: Text? = null, message: Text? = null): this(text = Text(item), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Label(title, systemImage = Companion.defaultSystemImageName).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: Text, item: URL, subject: Text? = null, message: Text? = null): this(text = Text(item.absoluteString), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Label(title = { ->
                ComposeBuilder { composectx: ComposeContext ->
                    title.Compose(composectx)
                    ComposeResult.ok
                }
            }, icon = { ->
                ComposeBuilder { composectx: ComposeContext ->
                    Image(systemName = Companion.defaultSystemImageName).Compose(composectx)
                    ComposeResult.ok
                }
            }).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: Text, item: String, subject: Text? = null, message: Text? = null): this(text = Text(item), subject = subject, message = message, label = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Label(title = { ->
                ComposeBuilder { composectx: ComposeContext ->
                    title.Compose(composectx)
                    ComposeResult.ok
                }
            }, icon = { ->
                ComposeBuilder { composectx: ComposeContext ->
                    Image(systemName = Companion.defaultSystemImageName).Compose(composectx)
                    ComposeResult.ok
                }
            }).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val localContext = LocalContext.current.sref()

        val intent = Intent().apply { ->
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text.localizedTextString())
            if (subject != null) {
                putExtra(Intent.EXTRA_SUBJECT, subject.localizedTextString())
            }
            type = "text/plain"
        }

        action = { ->
            val shareIntent = Intent.createChooser(intent, null)
            localContext.startActivity(shareIntent)
        }
        content.Compose(context = context)
    }

    companion object {
        private val defaultSystemImageName = "square.and.arrow.up"
    }
}

