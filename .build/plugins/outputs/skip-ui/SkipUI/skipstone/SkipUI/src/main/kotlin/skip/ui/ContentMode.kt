// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

enum class ContentMode: CaseIterable, Sendable {
    fit,
    fill;

    companion object: CaseIterableCompanion<ContentMode> {
        override val allCases: Array<ContentMode>
            get() = arrayOf(fit, fill)
    }
}
