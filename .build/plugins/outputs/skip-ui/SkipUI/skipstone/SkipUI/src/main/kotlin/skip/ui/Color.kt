// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class Color: ShapeStyle, Sendable {
    val colorImpl: @Composable () -> androidx.compose.ui.graphics.Color

    constructor(colorImpl: @Composable () -> androidx.compose.ui.graphics.Color) {
        this.colorImpl = colorImpl
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val animatable = colorImpl().asAnimatable(context = context)
        val modifier = context.modifier.background(animatable.value).fillSize(expandContainer = false)
        Box(modifier = modifier)
    }

    // MARK: - ShapeStyle

    @Composable
    override fun asColor(opacity: Double, animationContext: ComposeContext?): androidx.compose.ui.graphics.Color? {
        val color = this.opacity(opacity).colorImpl()
        if (animationContext != null) {
            return color.asAnimatable(context = animationContext).value
        } else {
            return color
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(color: Any) {
        colorImpl = { -> androidx.compose.ui.graphics.Color.White }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(cgColor: Any, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        colorImpl = { -> androidx.compose.ui.graphics.Color.White }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val cgColor: Any?
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun resolve(in_: Any): Color.Resolved {
        val environment = in_
        fatalError()
    }

    // MARK: -

    enum class RGBColorSpace: Sendable {
        sRGB,
        sRGBLinear,
        displayP3;

        companion object {
        }
    }

    constructor(red: Double, green: Double, blue: Double, opacity: Double = 1.0) {
        colorImpl = { -> androidx.compose.ui.graphics.Color(red = Float(red), green = Float(green), blue = Float(blue), alpha = Float(opacity)) }
    }

    constructor(colorSpace: Color.RGBColorSpace, red: Double, green: Double, blue: Double, opacity: Double = 1.0): this(red = red, green = green, blue = blue, opacity = opacity) {
    }

    constructor(white: Double, opacity: Double = 1.0): this(red = white, green = white, blue = white, opacity = opacity) {
    }

    constructor(colorSpace: Color.RGBColorSpace, white: Double, opacity: Double = 1.0): this(white = white, opacity = opacity) {
    }

    constructor(hue: Double, saturation: Double, brightness: Double, opacity: Double = 1.0, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        colorImpl = { -> androidx.compose.ui.graphics.Color.hsl(hue = Float(hue), saturation = Float(saturation), lightness = Float(brightness), alpha = Float(opacity)) }
    }

    // MARK: -

    class Resolved: MutableStruct {
        var red: Float
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var green: Float
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var blue: Float
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var opacity: Float
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(red: Float, green: Float, blue: Float, opacity: Float) {
            this.red = red
            this.green = green
            this.blue = blue
            this.opacity = opacity
        }

        constructor(colorSpace: Color.RGBColorSpace, red: Float, green: Float, blue: Float, opacity: Float): this(red = red, green = green, blue = blue, opacity = opacity) {
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val cgColor: Any
            get() {
                fatalError()
            }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Color.Resolved
            this.red = copy.red
            this.green = copy.green
            this.blue = copy.blue
            this.opacity = copy.opacity
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Color.Resolved(this as MutableStruct)

        override fun equals(other: Any?): Boolean {
            if (other !is Color.Resolved) return false
            return red == other.red && green == other.green && blue == other.blue && opacity == other.opacity
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, red)
            result = Hasher.combine(result, green)
            result = Hasher.combine(result, blue)
            result = Hasher.combine(result, opacity)
            return result
        }

        companion object {
        }
    }

    constructor(resolved: Color.Resolved): this(red = Double(resolved.red), green = Double(resolved.green), blue = Double(resolved.blue), opacity = Double(resolved.opacity)) {
    }


    // MARK: -

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(name: String, bundle: Any? = null) {
        colorImpl = { -> androidx.compose.ui.graphics.Color.White }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(uiColor: Any, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null, @Suppress("UNUSED_PARAMETER") unusedp_1: Nothing? = null) {
        colorImpl = { -> androidx.compose.ui.graphics.Color.White }
    }

    // MARK: -

    override fun opacity(opacity: Double): Color {
        if (opacity == 1.0) {
            return this
        }
        return Color(colorImpl = l@{ ->
            val color = colorImpl()
            return@l color.copy(alpha = color.alpha * Float(opacity))
        })
    }

    val gradient: AnyGradient
        get() {
            // Create a SwiftUI-like gradient by varying the saturation of this color
            val startColorImpl: @Composable () -> androidx.compose.ui.graphics.Color = { ->
                val color = colorImpl()
                val hsv = FloatArray(3)
                android.graphics.Color.RGBToHSV(Int(color.red * 255), Int(color.green * 255), Int(color.blue * 255), hsv)
                androidx.compose.ui.graphics.Color.hsv(hsv[0], hsv[1] * 0.75f, hsv[2], alpha = color.alpha)
            }
            val endColorImpl: @Composable () -> androidx.compose.ui.graphics.Color = { ->
                val color = colorImpl()
                val hsv = FloatArray(3)
                android.graphics.Color.RGBToHSV(Int(color.red * 255), Int(color.green * 255), Int(color.blue * 255), hsv)
                androidx.compose.ui.graphics.Color.hsv(hsv[0], min(1.0f, hsv[1] * (1.0f / 0.75f)), hsv[2], alpha = color.alpha)
            }
            return AnyGradient(gradient = Gradient(colors = arrayOf(Color(colorImpl = startColorImpl), Color(colorImpl = endColorImpl))))
        }

    override fun equals(other: Any?): Boolean {
        if (other !is Color) return false
        return colorImpl == other.colorImpl
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, colorImpl)
        return result
    }

    companion object: ShapeStyleCompanion {

        // MARK: -

        val accentColor: Color
            get() {
                return Color(colorImpl = { -> MaterialTheme.colorScheme.primary })
            }
        internal val background = Color(colorImpl = { -> MaterialTheme.colorScheme.surface })

        /// Matches Android's default bottom bar color.
        internal val systemBarBackground: Color = Color(colorImpl = { -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp) })

        /// Use for e.g. grouped table backgrounds, etc.
        internal val systemBackground: Color = systemBarBackground

        /// Use for overlays like alerts and action sheets.
        internal val overlayBackground: Color = Color(colorImpl = { -> MaterialTheme.colorScheme.surface.copy(alpha = 0.9f) })

        internal val placeholder = Color(colorImpl = { ->
            // Close to iOS's AsyncImage placeholder values
            ComposeColor(light = 0xFFDDDDDD, dark = 0xFF777777)
        })

        internal val _primary = Color(colorImpl = { -> MaterialTheme.colorScheme.onBackground })
        internal val _secondary = Color(colorImpl = { -> MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium) })
        internal val _clear = Color(colorImpl = { -> androidx.compose.ui.graphics.Color.Transparent })
        internal val _white = Color(colorImpl = { -> androidx.compose.ui.graphics.Color.White })
        internal val _black = Color(colorImpl = { -> androidx.compose.ui.graphics.Color.Black })
        internal val _gray = Color(colorImpl = { -> ComposeColor(light = 0xFF8E8E93, dark = 0xFF8E8E93) })
        internal val _red = Color(colorImpl = { -> ComposeColor(light = 0xFFFF3B30, dark = 0xFFFF453A) })
        internal val _orange = Color(colorImpl = { -> ComposeColor(light = 0xFFFF9500, dark = 0xFFFF9F0A) })
        internal val _yellow = Color(colorImpl = { -> ComposeColor(light = 0xFFFFCC00, dark = 0xFFFFD60A) })
        internal val _green = Color(colorImpl = { -> ComposeColor(light = 0xFF34C759, dark = 0xFF30D158) })
        internal val _mint = Color(colorImpl = { -> ComposeColor(light = 0xFF00C7BE, dark = 0xFF63E6E2) })
        internal val _teal = Color(colorImpl = { -> ComposeColor(light = 0xFF30B0C7, dark = 0xFF40C8E0) })
        internal val _cyan = Color(colorImpl = { -> ComposeColor(light = 0xFF32ADE6, dark = 0xFF64D2FF) })
        internal val _blue = Color(colorImpl = { -> ComposeColor(light = 0xFF007AFF, dark = 0xFF0A84FF) })
        internal val _indigo = Color(colorImpl = { -> ComposeColor(light = 0xFF5856D6, dark = 0xFF5E5CE6) })
        internal val _purple = Color(colorImpl = { -> ComposeColor(light = 0xFFAF52DE, dark = 0xFFBF5AF2) })
        internal val _pink = Color(colorImpl = { -> ComposeColor(light = 0xFFFF2D55, dark = 0xFFFF375F) })
        internal val _brown = Color(colorImpl = { -> ComposeColor(light = 0xFFA2845E, dark = 0xFFAC8E68) })

        val primary: Color
            get() = Color._primary
        val secondary: Color
            get() = Color._secondary
        val clear: Color
            get() = Color._clear
        val white: Color
            get() = Color._white
        val black: Color
            get() = Color._black
        val gray: Color
            get() = Color._gray
        val red: Color
            get() = Color._red
        val orange: Color
            get() = Color._orange
        val yellow: Color
            get() = Color._yellow
        val green: Color
            get() = Color._green
        val mint: Color
            get() = Color._mint
        val teal: Color
            get() = Color._teal
        val cyan: Color
            get() = Color._cyan
        val blue: Color
            get() = Color._blue
        val indigo: Color
            get() = Color._indigo
        val purple: Color
            get() = Color._purple
        val pink: Color
            get() = Color._pink
        val brown: Color
            get() = Color._brown
    }
}

/// Returns the given color value based on whether the view is in dark mode or light mode.
@Composable
private fun ComposeColor(light: Long, dark: Long): androidx.compose.ui.graphics.Color {
    // TODO: EnvironmentValues.shared.colorMode == .dark ? dark : light
    return androidx.compose.ui.graphics.Color(if (isSystemInDarkTheme()) dark else light)
}

