// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import skip.foundation.LocalizedStringResource
import skip.foundation.Bundle
import skip.foundation.Locale

class Text: View {
    private val textView: _Text
    private val modifiedView: View

    constructor(verbatim: String) {
        textView = _Text(verbatim = verbatim, key = null, tableName = null, bundle = null)
        modifiedView = textView
    }

    constructor(key: LocalizedStringKey, tableName: String? = null, bundle: Bundle? = Bundle.main, comment: String? = null) {
        textView = _Text(verbatim = null, key = key, tableName = tableName, bundle = bundle)
        modifiedView = textView
    }

    constructor(key: String, tableName: String? = null, bundle: Bundle? = Bundle.main, comment: String? = null) {
        textView = _Text(verbatim = null, key = LocalizedStringKey(stringLiteral = key), tableName = tableName, bundle = bundle)
        modifiedView = textView
    }

    internal constructor(textView: _Text, modifiedView: View) {
        this.textView = textView
        // Don't copy view
        this.modifiedView = modifiedView
    }

    /// Interpret the key against the given bundle and the environment's current locale.
    @Composable
    fun localizedTextString(): String = textView.localizedTextString()

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        modifiedView.Compose(context = context)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Text) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.textView == rhs.textView
    }

    // Text-specific implementations of View modifiers

    override fun accessibilityLabel(label: Text): Text = Text(textView = textView, modifiedView = modifiedView.accessibilityLabel(label))

    override fun accessibilityLabel(label: String): Text = Text(textView = textView, modifiedView = modifiedView.accessibilityLabel(label))

    override fun foregroundColor(color: Color?): Text = Text(textView = textView, modifiedView = modifiedView.foregroundColor(color))

    override fun foregroundStyle(style: ShapeStyle): Text = Text(textView = textView, modifiedView = modifiedView.foregroundStyle(style))

    override fun font(font: Font?): Text = Text(textView = textView, modifiedView = modifiedView.font(font))

    override fun fontWeight(weight: Font.Weight?): Text = Text(textView = textView, modifiedView = modifiedView.fontWeight(weight))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun fontWidth(width: Font.Width?): Text = this

    override fun bold(isActive: Boolean): Text = Text(textView = textView, modifiedView = modifiedView.bold(isActive))

    override fun italic(isActive: Boolean): Text = Text(textView = textView, modifiedView = modifiedView.italic(isActive))

    override fun monospaced(isActive: Boolean): Text = Text(textView = textView, modifiedView = modifiedView.monospaced(isActive))

    override fun fontDesign(design: Font.Design?): Text = Text(textView = textView, modifiedView = modifiedView.fontDesign(design))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun monospacedDigit(): Text = this

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun strikethrough(isActive: Boolean, pattern: Text.LineStyle.Pattern, color: Color?): Text = this

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun underline(isActive: Boolean, pattern: Text.LineStyle.Pattern, color: Color?): Text = this

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun kerning(kerning: Double): Text = this

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun tracking(tracking: Double): Text = this

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    override fun baselineOffset(baselineOffset: Double): Text = this

    enum class Case: Sendable {
        uppercase,
        lowercase;

        companion object {
        }
    }

    class LineStyle: Sendable {
        val pattern: Text.LineStyle.Pattern
        val color: Color?

        constructor(pattern: Text.LineStyle.Pattern = Text.LineStyle.Pattern.solid, color: Color? = null) {
            this.pattern = pattern
            this.color = color
        }

        enum class Pattern: Sendable {
            solid,
            dot,
            dash,
            dashot,
            dashDotDot;

            companion object {
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Text.LineStyle) return false
            return pattern == other.pattern && color == other.color
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, pattern)
            result = Hasher.combine(result, color)
            return result
        }

        companion object {

            val single = Text.LineStyle()
        }
    }

    enum class Scale: Sendable {
        default,
        secondary;

        companion object {
        }
    }

    enum class TruncationMode: Sendable {
        head,
        tail,
        middle;

        companion object {
        }
    }

    companion object {
    }
}

internal class _Text: View {
    internal val verbatim: String?
    internal val key: LocalizedStringKey?
    internal val tableName: String?
    internal val bundle: Bundle?

    @Composable
    internal fun localizedTextString(): String {
        this.verbatim?.let { verbatim ->
            return verbatim
        }
        val key_0 = this.key.sref()
        if (key_0 == null) {
            return ""
        }

        val locfmt = EnvironmentValues.shared.locale.localize(key = key_0.patternFormat, value = null, bundle = this.bundle, tableName = this.tableName)

        // re-interpret the placeholder strings in the resulting localized string with the string interpolation's values
        val replaced = String(format = locfmt ?: key_0.patternFormat, key_0.stringInterpolation.values)
        return replaced
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        var font: Font
        var text = this.localizedTextString()
        val matchtarget_0 = EnvironmentValues.shared.font
        if (matchtarget_0 != null) {
            val environmentFont = matchtarget_0
            font = environmentFont
        } else {
            val matchtarget_1 = EnvironmentValues.shared._listSectionHeaderStyle
            if (matchtarget_1 != null) {
                val sectionHeaderStyle = matchtarget_1
                font = Font.callout
                if (sectionHeaderStyle == ListStyle.plain) {
                    font = font.bold()
                } else {
                    text = text.uppercased()
                }
            } else {
                val matchtarget_2 = EnvironmentValues.shared._listSectionFooterStyle
                if (matchtarget_2 != null) {
                    val sectionFooterStyle = matchtarget_2
                    if (sectionFooterStyle != ListStyle.plain) {
                        font = Font.footnote
                    } else {
                        font = Font(fontImpl = { -> LocalTextStyle.current })
                    }
                } else {
                    font = Font(fontImpl = { -> LocalTextStyle.current })
                }
            }
        }
        EnvironmentValues.shared._fontWeight?.let { weight ->
            font = font.weight(weight)
        }
        EnvironmentValues.shared._fontDesign?.let { design ->
            font = font.design(design)
        }
        if (EnvironmentValues.shared._isItalic) {
            font = font.italic()
        }

        var textColor: androidx.compose.ui.graphics.Color? = null
        var textBrush: Brush? = null
        val matchtarget_3 = EnvironmentValues.shared._foregroundStyle
        if (matchtarget_3 != null) {
            val foregroundStyle = matchtarget_3
            val matchtarget_4 = foregroundStyle.asColor(opacity = 1.0, animationContext = context)
            if (matchtarget_4 != null) {
                val color = matchtarget_4
                textColor = color
            } else {
                textBrush = foregroundStyle.asBrush(opacity = 1.0, animationContext = context)
            }
        } else if (EnvironmentValues.shared._listSectionHeaderStyle != null) {
            textColor = Color.secondary.colorImpl()
        } else {
            val matchtarget_5 = EnvironmentValues.shared._listSectionFooterStyle
            if (matchtarget_5 != null) {
                val sectionFooterStyle = matchtarget_5
                if (sectionFooterStyle != ListStyle.plain) {
                    textColor = Color.secondary.colorImpl()
                } else {
                    textColor = if (EnvironmentValues.shared._placement.contains(ViewPlacement.systemTextColor)) androidx.compose.ui.graphics.Color.Unspecified else Color.primary.colorImpl()
                }
            } else {
                textColor = if (EnvironmentValues.shared._placement.contains(ViewPlacement.systemTextColor)) androidx.compose.ui.graphics.Color.Unspecified else Color.primary.colorImpl()
            }
        }
        val textAlign = EnvironmentValues.shared.multilineTextAlignment.asTextAlign()
        val maxLines = max(1, EnvironmentValues.shared.lineLimit ?: Int.MAX_VALUE)
        var style = font.fontImpl()
        // Trim the line height padding to mirror SwiftUI.Text layout. For now we only do this here on the Text component
        // rather than in Font to de-risk this aberration from Compose default text style behavior
        style = style.copy(lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.Both))
        if (textBrush != null) {
            style = style.copy(brush = textBrush)
        }
        val animatable = style.asAnimatable(context = context)
        if (textColor != null) {
            androidx.compose.material3.Text(text = text, modifier = context.modifier, color = textColor, maxLines = maxLines, style = animatable.value, textAlign = textAlign)
        } else {
            androidx.compose.material3.Text(text = text, modifier = context.modifier, maxLines = maxLines, style = animatable.value, textAlign = textAlign)
        }
    }

    constructor(verbatim: String? = null, key: LocalizedStringKey? = null, tableName: String? = null, bundle: Bundle? = null) {
        this.verbatim = verbatim
        this.key = key.sref()
        this.tableName = tableName
        this.bundle = bundle.sref()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is _Text) return false
        return verbatim == other.verbatim && key == other.key && tableName == other.tableName && bundle == other.bundle
    }
}

enum class TextAlignment: CaseIterable, Sendable {
    leading,
    center,
    trailing;

    /// Convert this enum to a Compose `TextAlign` value.
    fun asTextAlign(): TextAlign {
        return when (this) {
            TextAlignment.leading -> TextAlign.Start
            TextAlignment.center -> TextAlign.Center
            TextAlignment.trailing -> TextAlign.End
        }
    }

    companion object: CaseIterableCompanion<TextAlignment> {
        override val allCases: Array<TextAlignment>
            get() = arrayOf(leading, center, trailing)
    }
}

