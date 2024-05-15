// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

enum class ControlSize: CaseIterable, Sendable {
    mini,
    small,
    regular,
    large,
    extraLarge;

    companion object: CaseIterableCompanion<ControlSize> {
        override val allCases: Array<ControlSize>
            get() = arrayOf(mini, small, regular, large, extraLarge)
    }
}
