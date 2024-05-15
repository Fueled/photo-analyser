// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class Alignment: Sendable, MutableStruct {
    var horizontal: HorizontalAlignment
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var vertical: VerticalAlignment
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    /// Return the Compose alignment equivalent.
    internal fun asComposeAlignment(): androidx.compose.ui.Alignment {
        when (this) {
            Alignment.leading, Alignment.leadingFirstTextBaseline, Alignment.leadingLastTextBaseline -> return androidx.compose.ui.Alignment.CenterStart.sref()
            Alignment.trailing, Alignment.trailingFirstTextBaseline, Alignment.trailingLastTextBaseline -> return androidx.compose.ui.Alignment.CenterEnd.sref()
            Alignment.top -> return androidx.compose.ui.Alignment.TopCenter.sref()
            Alignment.bottom -> return androidx.compose.ui.Alignment.BottomCenter.sref()
            Alignment.topLeading -> return androidx.compose.ui.Alignment.TopStart.sref()
            Alignment.topTrailing -> return androidx.compose.ui.Alignment.TopEnd.sref()
            Alignment.bottomLeading -> return androidx.compose.ui.Alignment.BottomStart.sref()
            Alignment.bottomTrailing -> return androidx.compose.ui.Alignment.BottomEnd.sref()
            else -> return androidx.compose.ui.Alignment.Center.sref()
        }
    }

    constructor(horizontal: HorizontalAlignment, vertical: VerticalAlignment) {
        this.horizontal = horizontal
        this.vertical = vertical
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Alignment(horizontal, vertical)

    override fun equals(other: Any?): Boolean {
        if (other !is Alignment) return false
        return horizontal == other.horizontal && vertical == other.vertical
    }

    companion object {

        val center = Alignment(horizontal = HorizontalAlignment.center, vertical = VerticalAlignment.center)
        val leading = Alignment(horizontal = HorizontalAlignment.leading, vertical = VerticalAlignment.center)
        val trailing = Alignment(horizontal = HorizontalAlignment.trailing, vertical = VerticalAlignment.center)
        val top = Alignment(horizontal = HorizontalAlignment.center, vertical = VerticalAlignment.top)
        val bottom = Alignment(horizontal = HorizontalAlignment.leading, vertical = VerticalAlignment.bottom)
        val topLeading = Alignment(horizontal = HorizontalAlignment.leading, vertical = VerticalAlignment.top)
        val topTrailing = Alignment(horizontal = HorizontalAlignment.trailing, vertical = VerticalAlignment.top)
        val bottomLeading = Alignment(horizontal = HorizontalAlignment.leading, vertical = VerticalAlignment.bottom)
        val bottomTrailing = Alignment(horizontal = HorizontalAlignment.trailing, vertical = VerticalAlignment.bottom)

        var centerFirstTextBaseline = Alignment(horizontal = HorizontalAlignment.center, vertical = VerticalAlignment.firstTextBaseline)
            get() = field.sref({ this.centerFirstTextBaseline = it })
            set(newValue) {
                field = newValue.sref()
            }
        var centerLastTextBaseline = Alignment(horizontal = HorizontalAlignment.center, vertical = VerticalAlignment.lastTextBaseline)
            get() = field.sref({ this.centerLastTextBaseline = it })
            set(newValue) {
                field = newValue.sref()
            }
        var leadingFirstTextBaseline = Alignment(horizontal = HorizontalAlignment.leading, vertical = VerticalAlignment.firstTextBaseline)
            get() = field.sref({ this.leadingFirstTextBaseline = it })
            set(newValue) {
                field = newValue.sref()
            }
        var leadingLastTextBaseline = Alignment(horizontal = HorizontalAlignment.leading, vertical = VerticalAlignment.lastTextBaseline)
            get() = field.sref({ this.leadingLastTextBaseline = it })
            set(newValue) {
                field = newValue.sref()
            }
        var trailingFirstTextBaseline = Alignment(horizontal = HorizontalAlignment.trailing, vertical = VerticalAlignment.firstTextBaseline)
            get() = field.sref({ this.trailingFirstTextBaseline = it })
            set(newValue) {
                field = newValue.sref()
            }
        var trailingLastTextBaseline = Alignment(horizontal = HorizontalAlignment.trailing, vertical = VerticalAlignment.lastTextBaseline)
            get() = field.sref({ this.trailingLastTextBaseline = it })
            set(newValue) {
                field = newValue.sref()
            }
    }
}
