// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import android.content.Context
import android.graphics.Typeface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class Font: Sendable {
    val fontImpl: @Composable () -> androidx.compose.ui.text.TextStyle

    constructor(fontImpl: @Composable () -> androidx.compose.ui.text.TextStyle) {
        this.fontImpl = fontImpl
    }

    enum class TextStyle: CaseIterable, Sendable {
        largeTitle,
        title,
        title2,
        title3,
        headline,
        subheadline,
        body,
        callout,
        footnote,
        caption,
        caption2;

        companion object: CaseIterableCompanion<Font.TextStyle> {
            override val allCases: Array<Font.TextStyle>
                get() = arrayOf(largeTitle, title, title2, title3, headline, subheadline, body, callout, footnote, caption, caption2)
        }
    }


    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(font: Any) {
        fontImpl = { -> MaterialTheme.typography.bodyMedium }
    }

    fun italic(): Font {
        return Font(fontImpl = { -> fontImpl().copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic) })
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun smallCaps(): Font {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun lowercaseSmallCaps(): Font {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun uppercaseSmallCaps(): Font {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun monospacedDigit(): Font {
        fatalError()
    }

    fun weight(weight: Font.Weight): Font {
        return Font(fontImpl = { -> fontImpl().copy(fontWeight = Companion.fontWeight(for_ = weight)) })
    }


    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun width(width: Font.Width): Font {
        fatalError()
    }

    fun bold(): Font = weight(Weight.bold)

    fun monospaced(): Font = design(Design.monospaced)

    internal fun design(design: Font.Design?): Font {
        return Font(fontImpl = { -> fontImpl().copy(fontFamily = Companion.fontFamily(for_ = Design.monospaced)) })
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun leading(leading: Font.Leading): Font {
        fatalError()
    }

    class Weight: Sendable {
        internal val value: Double

        internal constructor(value: Double) {
            this.value = value
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Font.Weight) return false
            return value == other.value
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, value)
            return result
        }

        companion object {
            val ultraLight = Weight(value = -0.8)
            val thin = Weight(value = -0.6)
            val light = Weight(value = -0.4)
            val regular = Weight(value = 0.0)
            val medium = Weight(value = 0.23)
            val semibold = Weight(value = 0.3)
            val bold = Weight(value = 0.4)
            val heavy = Weight(value = 0.56)
            val black = Weight(value = 0.62)
        }
    }

    class Width: Sendable, MutableStruct {
        var value: Double
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(value: Double) {
            this.value = value
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Font.Width
            this.value = copy.value
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Font.Width(this as MutableStruct)

        override fun equals(other: Any?): Boolean {
            if (other !is Font.Width) return false
            return value == other.value
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, value)
            return result
        }

        companion object {

            val compressed = Width(0.8)
            val condensed = Width(0.9)
            val standard = Width(1.0)
            val expanded = Width(1.2)
        }
    }

    enum class Leading: Sendable {
        standard,
        tight,
        loose;

        companion object {
        }
    }

    enum class Design: Sendable {
        default,
        serif,
        rounded,
        monospaced;

        companion object {
        }
    }


    override fun equals(other: Any?): Boolean {
        if (other !is Font) return false
        return fontImpl == other.fontImpl
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, fontImpl)
        return result
    }

    companion object {

        // M3: Default Font Size/Line Height
        // displayLarge: Roboto 57/64
        // displayMedium: Roboto 45/52
        // displaySmall: Roboto 36/44
        // headlineLarge: Roboto 32/40
        // headlineMedium: Roboto 28/36
        // headlineSmall: Roboto 24/32
        // titleLarge: New-Roboto Medium 22/28
        // titleMedium: Roboto Medium 16/24
        // titleSmall: Roboto Medium 14/20
        // bodyLarge: Roboto 16/24
        // bodyMedium: Roboto 14/20
        // bodySmall: Roboto 12/16
        // labelLarge: Roboto Medium 14/20
        // labelMedium: Roboto Medium 12/16
        // labelSmall: New Roboto Medium 11/16

        // manual offsets are applied to the default font sizes to get them to line up with SwiftUI default sizes; see TextTests.swift

        val largeTitle = Font(fontImpl = { -> adjust(MaterialTheme.typography.titleLarge, by = Float(+9.0 + 1.0)) })

        val title = Font(fontImpl = { -> adjust(MaterialTheme.typography.headlineMedium, by = Float(-2.0)) })

        val title2 = Font(fontImpl = { -> adjust(MaterialTheme.typography.headlineSmall, by = Float(-5.0 + 1.0)) })

        val title3 = Font(fontImpl = { -> adjust(MaterialTheme.typography.headlineSmall, by = Float(-6.0)) })

        val headline = Font(fontImpl = { -> adjust(MaterialTheme.typography.titleMedium, by = 0.0f) })

        val subheadline = Font(fontImpl = { -> adjust(MaterialTheme.typography.titleSmall, by = 0.0f) })

        val body = Font(fontImpl = { -> adjust(MaterialTheme.typography.bodyLarge, by = 0.0f) })

        val callout = Font(fontImpl = { -> adjust(MaterialTheme.typography.bodyMedium, by = Float(+1.0)) })

        val footnote = Font(fontImpl = { -> adjust(MaterialTheme.typography.bodySmall, by = Float(+0.0)) })

        val caption = Font(fontImpl = { -> adjust(MaterialTheme.typography.bodySmall, by = Float(-0.75)) })

        val caption2 = Font(fontImpl = { -> adjust(MaterialTheme.typography.bodySmall, by = Float(-1.0)) })

        private fun adjust(style: androidx.compose.ui.text.TextStyle, by: Float): androidx.compose.ui.text.TextStyle {
            val amount = by
            return if (amount == 0.0f) style else style.copy(fontSize = (style.fontSize.value + amount).sp)
        }

        fun system(style: Font.TextStyle, design: Font.Design? = null, weight: Font.Weight? = null): Font {
            val font: Font
            when (style) {
                Font.TextStyle.largeTitle -> font = Font.largeTitle
                Font.TextStyle.title -> font = Font.title
                Font.TextStyle.title2 -> font = Font.title2
                Font.TextStyle.title3 -> font = Font.title3
                Font.TextStyle.headline -> font = Font.headline
                Font.TextStyle.subheadline -> font = Font.subheadline
                Font.TextStyle.body -> font = Font.body
                Font.TextStyle.callout -> font = Font.callout
                Font.TextStyle.footnote -> font = Font.footnote
                Font.TextStyle.caption -> font = Font.caption
                Font.TextStyle.caption2 -> font = Font.caption2
            }
            if (weight == null && design == null) {
                return font
            }
            return Font(fontImpl = { -> font.fontImpl().copy(fontWeight = fontWeight(for_ = weight), fontFamily = fontFamily(for_ = design)) })
        }

        fun system(size: Double, weight: Font.Weight? = null, design: Font.Design? = null): Font {
            return Font(fontImpl = { -> androidx.compose.ui.text.TextStyle(fontSize = size.sp, fontWeight = fontWeight(for_ = weight), fontFamily = fontFamily(for_ = design)) })
        }
        private fun findNamedFont(fontName: String, ctx: Context): FontFamily? {
            // Android font names are lowercased and separated by "_" characters, since Android resource names can take only alphanumeric characters.
            // Font lookups on Android reference the font's filename, whereas SwiftUI references the font's Postscript name
            // So the best way to have the same font lookup code work on both platforms is to name the
            // font with PS name "Some Poscript Font-Bold" as "some_postscript_font_bold.ttf", and then both iOS and Android
            // can reference it by the postscript name
            val name = fontName.lowercased().replace(" ", "_").replace("-", "_")

            //android.util.Log.i("SkipUI", "finding font: \(name)")

            // look up the font in the resource bundle for custom embedded fonts
            val fid = ctx.resources.getIdentifier(name, "font", ctx.packageName)
            if (fid == 0) {
                // try to fall back on system installed fonts like "courier"
                Typeface.create(name, Typeface.NORMAL)?.let { typeface ->
                    //android.util.Log.i("SkipUI", "found font: \(typeface)")
                    return FontFamily(typeface)
                }

                android.util.Log.w("SkipUI", "unable to find font named: ${fontName} (${name})")
                return null
            }

            ctx.resources.getFont(fid)?.let { customTypeface ->
                return FontFamily(customTypeface)
            }

            android.util.Log.w("SkipUI", "unable to find font named: ${name}")
            return null
        }

        fun custom(name: String, size: Double): Font {
            return Font(fontImpl = { -> androidx.compose.ui.text.TextStyle(fontFamily = Companion.findNamedFont(name, ctx = LocalContext.current), fontSize = size.sp) })
        }

        fun custom(name: String, size: Double, relativeTo: Font.TextStyle): Font {
            val textStyle = relativeTo
            val systemFont = system(textStyle)
            return Font(fontImpl = { ->
                val absoluteSize = systemFont.fontImpl().fontSize.value + size
                androidx.compose.ui.text.TextStyle(fontFamily = Companion.findNamedFont(name, ctx = LocalContext.current), fontSize = absoluteSize.sp)
            })
        }

        fun custom(name: String, fixedSize: Double, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): Font = Font.custom(name, size = fixedSize)
        private fun fontWeight(for_: Font.Weight?): FontWeight? {
            val weight = for_
            when (weight) {
                null -> return null
                Font.Weight.ultraLight -> return FontWeight.Thin.sref()
                Font.Weight.thin -> return FontWeight.ExtraLight.sref()
                Font.Weight.light -> return FontWeight.Light.sref()
                Font.Weight.regular -> return FontWeight.Normal.sref()
                Font.Weight.medium -> return FontWeight.Medium.sref()
                Font.Weight.semibold -> return FontWeight.SemiBold.sref()
                Font.Weight.bold -> return FontWeight.Bold.sref()
                Font.Weight.heavy -> return FontWeight.ExtraBold.sref()
                Font.Weight.black -> return FontWeight.Black.sref()
                else -> return FontWeight.Normal.sref()
            }
        }
        private fun fontFamily(for_: Font.Design?): FontFamily? {
            val design = for_
            when (design) {
                null -> return null
                Font.Design.default -> return FontFamily.Default.sref()
                Font.Design.serif -> return FontFamily.Serif.sref()
                Font.Design.rounded -> return FontFamily.Cursive.sref()
                Font.Design.monospaced -> return FontFamily.Monospace.sref()
            }
        }
    }
}

enum class LegibilityWeight: Sendable {
    regular,
    bold;

    companion object {
    }
}

class RedactionReasons: OptionSet<RedactionReasons, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): RedactionReasons = RedactionReasons(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: RedactionReasons) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as RedactionReasons
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = RedactionReasons(this as MutableStruct)

    private fun assignfrom(target: RedactionReasons) {
        this.rawValue = target.rawValue
    }

    companion object {

        val placeholder = RedactionReasons(rawValue = 1 shl 0)
        val privacy = RedactionReasons(rawValue = 1 shl 1)
        val invalidated = RedactionReasons(rawValue = 1 shl 2)

        fun of(vararg options: RedactionReasons): RedactionReasons {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return RedactionReasons(rawValue = value)
        }
    }
}

