// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class Namespace {
    constructor() {
    }

    val wrappedValue: Namespace.ID
        get() {
            fatalError()
        }

    class ID {
        override fun equals(other: Any?): Boolean = other is Namespace.ID

        override fun hashCode(): Int = "Namespace.ID".hashCode()

        companion object {
        }
    }

    companion object {
    }
}
