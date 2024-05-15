// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor

// Note: ShapeStyle does not extend View in SwiftUI, but most concrete ShapeStyles do and it helps us disambiguate calls
// to functions that are overloaded on both View and ShapeStyle, like some .background(...) variants
interface ShapeStyle: View, Sendable {
    @Composable
    fun asColor(opacity: Double, animationContext: ComposeContext?): androidx.compose.ui.graphics.Color? = null

    @Composable
    fun asBrush(opacity: Double, animationContext: ComposeContext?): Brush? {
        val color_0 = asColor(opacity = opacity, animationContext = animationContext)
        if (color_0 == null) {
            return null
        }
        return SolidColor(color_0)
    }
}
interface ShapeStyleCompanion {
}


class AnyShapeStyle: ShapeStyle {
    internal val style: ShapeStyle
    internal val opacity: Double

    constructor(style: ShapeStyle, opacity: Double = 1.0) {
        this.style = style.sref()
        this.opacity = opacity
    }

    @Composable
    override fun asColor(opacity: Double, animationContext: ComposeContext?): androidx.compose.ui.graphics.Color? = style.asColor(opacity = opacity * this.opacity, animationContext = animationContext)

    @Composable
    override fun asBrush(opacity: Double, animationContext: ComposeContext?): Brush? = style.asBrush(opacity = opacity * this.opacity, animationContext = animationContext)

    companion object: ShapeStyleCompanion {
    }
}

class ForegroundStyle: ShapeStyle {

    constructor() {
    }

    @Composable
    override fun asColor(opacity: Double, animationContext: ComposeContext?): androidx.compose.ui.graphics.Color? {
        return EnvironmentValues.shared._foregroundStyle?.asColor(opacity = opacity, animationContext = animationContext)
    }

    @Composable
    override fun asBrush(opacity: Double, animationContext: ComposeContext?): Brush? {
        return EnvironmentValues.shared._foregroundStyle?.asBrush(opacity = opacity, animationContext = animationContext)
    }

    companion object: ShapeStyleCompanion {
        internal val shared = ForegroundStyle()

        val foreground: ForegroundStyle
            get() = ForegroundStyle.shared
    }
}

class BackgroundStyle: ShapeStyle {

    constructor() {
    }

    @Composable
    override fun asColor(opacity: Double, animationContext: ComposeContext?): androidx.compose.ui.graphics.Color? {
        val matchtarget_0 = EnvironmentValues.shared.backgroundStyle
        if (matchtarget_0 != null) {
            val style = matchtarget_0
            return style.asColor(opacity = opacity, animationContext = animationContext)
        } else {
            return Color.background.asColor(opacity = opacity, animationContext = null)
        }
    }

    @Composable
    override fun asBrush(opacity: Double, animationContext: ComposeContext?): Brush? {
        val matchtarget_1 = EnvironmentValues.shared.backgroundStyle
        if (matchtarget_1 != null) {
            val style = matchtarget_1
            return style.asBrush(opacity = opacity, animationContext = animationContext)
        } else {
            return Color.background.asBrush(opacity = opacity, animationContext = null)
        }
    }

    companion object: ShapeStyleCompanion {
        internal val shared = BackgroundStyle()

        val background: BackgroundStyle
            get() = BackgroundStyle.shared
    }
}

class FillStyle: Sendable, MutableStruct {
    var isEOFilled: Boolean
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var isAntialiased: Boolean
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(eoFill: Boolean = false, antialiased: Boolean = true) {
        this.isEOFilled = eoFill
        this.isAntialiased = antialiased
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as FillStyle
        this.isEOFilled = copy.isEOFilled
        this.isAntialiased = copy.isAntialiased
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = FillStyle(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is FillStyle) return false
        return isEOFilled == other.isEOFilled && isAntialiased == other.isAntialiased
    }

    companion object {
    }
}

enum class RoundedCornerStyle: Sendable {
    circular,
    continuous;

    companion object {
    }
}

