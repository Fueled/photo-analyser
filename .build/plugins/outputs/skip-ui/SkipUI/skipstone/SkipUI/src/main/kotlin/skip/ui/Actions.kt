// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.foundation.*

class DismissAction {
    internal val action: () -> Unit

    operator fun invoke(): Unit = action()

    internal constructor(action: () -> Unit) {
        this.action = action
    }

    companion object {

        internal val default = DismissAction(action = { ->  })
    }
}

class OpenURLAction {
    class Result {
        internal val rawValue: Int
        internal val url: URL?

        internal constructor(rawValue: Int, url: URL? = null) {
            this.rawValue = rawValue
            this.url = url.sref()
        }

        companion object {

            val handled = Result(rawValue = 0)
            val discarded = Result(rawValue = 1)
            val systemAction = Result(rawValue = 2)
            fun systemAction(url: URL): OpenURLAction.Result = Result(rawValue = 2, url = url)
        }
    }

    internal val handler: (URL) -> OpenURLAction.Result
    internal val systemHandler: ((URL) -> Unit)?

    constructor(handler: (URL) -> OpenURLAction.Result) {
        this.handler = handler
        this.systemHandler = null
    }

    constructor(handler: (URL) -> OpenURLAction.Result, systemHandler: (URL) -> Unit) {
        this.handler = handler
        this.systemHandler = systemHandler
    }

    operator fun invoke(url: URL) {
        invoke(url, completion = { _ ->  })
    }

    operator fun invoke(url: URL, completion: (Boolean) -> Unit) {
        val result = handler(url)
        if (result.rawValue == Result.handled.rawValue) {
            completion(true)
        } else if (result.rawValue == Result.discarded.rawValue) {
            completion(false)
        } else if (result.rawValue == Result.systemAction.rawValue) {
            val matchtarget_0 = systemHandler
            if (matchtarget_0 != null) {
                val systemHandler = matchtarget_0
                val openURL = (result.url ?: url).sref()
                try {
                    systemHandler(openURL)
                } catch (error: Throwable) {
                    @Suppress("NAME_SHADOWING") val error = error.aserror()
                    completion(false)
                }
            } else {
                completion(false)
            }
        }
    }

    companion object {

        internal val default: OpenURLAction = OpenURLAction(handler = { _ -> Result.systemAction })
    }
}

