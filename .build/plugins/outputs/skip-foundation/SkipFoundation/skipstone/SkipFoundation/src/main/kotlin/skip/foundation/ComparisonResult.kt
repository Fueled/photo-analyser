// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


enum class ComparisonResult(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
    ascending(-1),
    same(0),
    descending(1);

    companion object {
    }
}

fun ComparisonResult(rawValue: Int): ComparisonResult? {
    return when (rawValue) {
        -1 -> ComparisonResult.ascending
        0 -> ComparisonResult.same
        1 -> ComparisonResult.descending
        else -> null
    }
}

