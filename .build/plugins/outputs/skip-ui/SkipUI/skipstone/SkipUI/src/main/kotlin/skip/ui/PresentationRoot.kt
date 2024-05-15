// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.Set

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

/// The root of a presentation, such as the root presentation or a sheet.
@Composable
fun PresentationRoot(defaultColorScheme: ColorScheme? = null, absoluteSystemBarEdges: Edge.Set = Edge.Set.all, context: ComposeContext, content: @Composable (ComposeContext) -> Unit) {
    val systemBarEdges = absoluteSystemBarEdges
    val preferenceUpdates = remember { -> mutableStateOf(0) }
    preferenceUpdates.value.sref() // Read so that it can trigger recompose on change
    val recompose = { -> preferenceUpdates.value += 1 }

    val preferredColorScheme = rememberSaveable(stateSaver = context.stateSaver as Saver<PreferredColorScheme, Any>) { -> mutableStateOf(PreferredColorSchemePreferenceKey.defaultValue) }
    val preferredColorSchemePreference = Preference<PreferredColorScheme>(key = PreferredColorSchemePreferenceKey::class, update = { it -> preferredColorScheme.value = it }, recompose = recompose)
    PreferenceValues.shared.collectPreferences(arrayOf(preferredColorSchemePreference)) { ->
        val materialColorScheme = (preferredColorScheme.value.colorScheme?.asMaterialTheme() ?: defaultColorScheme?.asMaterialTheme() ?: MaterialTheme.colorScheme).sref()
        MaterialTheme(colorScheme = materialColorScheme) { ->
            val presentationBounds = remember { -> mutableStateOf(Rect.Zero) }
            val density = LocalDensity.current.sref()
            val layoutDirection = LocalLayoutDirection.current.sref()
            val rootModifier = Modifier
                .background(Color.background.colorImpl())
                .fillMaxSize()
                .onGloballyPositioned { it -> presentationBounds.value = it.boundsInWindow() }
            Box(modifier = rootModifier) l@{ ->
                if (presentationBounds.value == Rect.Zero) {
                    return@l
                }
                // Cannot get accurate WindowInsets until we're in the content box
                var (safeLeft, safeTop, safeRight, safeBottom) = presentationBounds.value.sref()
                if (systemBarEdges.contains(Edge.Set.leading)) {
                    safeLeft += WindowInsets.systemBars.getLeft(density, layoutDirection)
                }
                if (systemBarEdges.contains(Edge.Set.top)) {
                    safeTop += WindowInsets.systemBars.getTop(density)
                }
                if (systemBarEdges.contains(Edge.Set.trailing)) {
                    safeRight -= WindowInsets.systemBars.getRight(density, layoutDirection)
                }
                if (systemBarEdges.contains(Edge.Set.bottom)) {
                    safeBottom -= WindowInsets.systemBars.getBottom(density)
                }
                val safeBounds = Rect(left = safeLeft, top = safeTop, right = safeRight, bottom = safeBottom)
                val safeArea = SafeArea(presentation = presentationBounds.value, safe = safeBounds, absoluteSystemBars = systemBarEdges)
                EnvironmentValues.shared.setValues({ it ->
                    // Detect whether the app is edge to edge mode based on whether the bounds extend past the safe areas
                    if (it._isEdgeToEdge == null) {
                        it.set_isEdgeToEdge(safeBounds != presentationBounds.value)
                    }
                    it.set_safeArea(safeArea)
                }, in_ = { ->
                    Box(modifier = Modifier.fillMaxSize().padding(safeArea), contentAlignment = androidx.compose.ui.Alignment.Center.sref()) { -> content(context) }
                })
            }
        }
    }
}
