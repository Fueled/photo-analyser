// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.ui.text.input.ImeAction

enum class SubmitLabel: Sendable {
    done,
    go,
    send,
    join,
    route,
    search,
    return_,
    next,
    continue_;

    internal fun asImeAction(): ImeAction {
        when (this) {
            SubmitLabel.done -> return ImeAction.Done.sref()
            SubmitLabel.go -> return ImeAction.Go.sref()
            SubmitLabel.send -> return ImeAction.Send.sref()
            SubmitLabel.join -> return ImeAction.Go.sref()
            SubmitLabel.route -> return ImeAction.Go.sref()
            SubmitLabel.search -> return ImeAction.Search.sref()
            SubmitLabel.return_ -> return ImeAction.Default.sref()
            SubmitLabel.next -> return ImeAction.Next.sref()
            SubmitLabel.continue_ -> return ImeAction.Next.sref()
        }
    }

    companion object {
    }
}
