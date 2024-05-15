// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

enum class Visibility: CaseIterable, Sendable {
    automatic,
    visible,
    hidden;

    companion object: CaseIterableCompanion<Visibility> {
        override val allCases: Array<Visibility>
            get() = arrayOf(automatic, visible, hidden)
    }
}
