// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

enum class ColorScheme: CaseIterable, Sendable {
    light,
    dark;


    /// Return the material color scheme for this scheme.
    @Composable
    fun asMaterialTheme(): androidx.compose.material3.ColorScheme {
        val context = LocalContext.current.sref()
        val isDarkMode = this == ColorScheme.dark
        // Dynamic color is available on Android 12+
        val isDynamicColor = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
        if (isDynamicColor) {
            return if (this == ColorScheme.dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            return if (this == ColorScheme.dark) darkColorScheme() else lightColorScheme()
        }
    }

    companion object: CaseIterableCompanion<ColorScheme> {
        /// Return the color scheme for the current material color scheme.
        @Composable
        fun fromMaterialTheme(): ColorScheme {
            // Material3 doesn't have a built-in light vs dark property, so use the luminance of the background
            return if (MaterialTheme.colorScheme.background.luminance() > 0.5f) ColorScheme.light else ColorScheme.dark
        }

        override val allCases: Array<ColorScheme>
            get() = arrayOf(light, dark)
    }
}

internal class PreferredColorSchemePreferenceKey: PreferenceKey<PreferredColorScheme> {

    companion object: PreferenceKeyCompanion<PreferredColorScheme> {
        override val defaultValue = PreferredColorScheme(colorScheme = null)
        override fun reduce(value: InOut<PreferredColorScheme>, nextValue: () -> PreferredColorScheme) {
            value.value = nextValue()
        }
    }
}

internal class PreferredColorScheme {
    internal val colorScheme: ColorScheme?

    constructor(colorScheme: ColorScheme? = null) {
        this.colorScheme = colorScheme
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PreferredColorScheme) return false
        return colorScheme == other.colorScheme
    }
}
