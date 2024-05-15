// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.unit.dp

class GridItem: Sendable, MutableStruct {
    sealed class Size: Sendable {
        class FixedCase(val associated0: Double): Size() {
        }
        class FlexibleCase(val associated0: Double, val associated1: Double): Size() {
            val minimum = associated0
            val maximum = associated1
        }
        class AdaptiveCase(val associated0: Double, val associated1: Double): Size() {
            val minimum = associated0
            val maximum = associated1
        }

        companion object {
            fun fixed(associated0: Double): Size = FixedCase(associated0)
            fun flexible(minimum: Double = 10.0, maximum: Double = Double.infinity): Size = FlexibleCase(minimum, maximum)
            fun adaptive(minimum: Double, maximum: Double = Double.infinity): Size = AdaptiveCase(minimum, maximum)
        }
    }

    var size: GridItem.Size
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var spacing: Double? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var alignment: Alignment? = null
        get() = field.sref({ this.alignment = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(size: GridItem.Size = GridItem.Size.flexible(), spacing: Double? = null, alignment: Alignment? = null) {
        this.size = size
        this.spacing = spacing
        this.alignment = alignment
    }


    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as GridItem
        this.size = copy.size
        this.spacing = copy.spacing
        this.alignment = copy.alignment
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = GridItem(this as MutableStruct)

    companion object {
        internal fun asGridCells(items: Array<GridItem>): Tuple3<GridCells, Alignment?, Double?> {
            if (items.isEmpty) {
                return Tuple3(GridCells.Fixed(count = 1), null, null)
            }
            // There is no way to match the flexibility of SwiftUI grid specs, which can mix styles. We use the first style
            val gridCells = linvoke l@{
                val matchtarget_0 = items[0].size
                when (matchtarget_0) {
                    is GridItem.Size.AdaptiveCase -> {
                        val minimum = matchtarget_0.associated0
                        val maximum = matchtarget_0.associated1
                        return@l GridCells.Adaptive(minSize = minimum.dp)
                    }
                    is GridItem.Size.FixedCase -> {
                        val size = matchtarget_0.associated0
                        return@l GridCells.FixedSize(size = size.dp)
                    }
                    is GridItem.Size.FlexibleCase -> {
                        val minimum = matchtarget_0.associated0
                        val maximum = matchtarget_0.associated1
                        return@l GridCells.Fixed(count = items.count)
                    }
                }
            }
            return Tuple3(gridCells.sref(), items[0].alignment.sref(), items[0].spacing)
        }
    }
}

