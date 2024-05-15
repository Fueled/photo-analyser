// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

/// Fill available remaining width.
///
/// This is a replacement for `fillMaxWidth` designed for situations when the composable may be in an `HStack` and does not want to push other items out.
///
/// - Warning: Containers with child content should use the `ComposeContainer` composable instead.
@Composable
fun Modifier.fillWidth(expandContainer: Boolean = true): Modifier {
    val fill_0 = EnvironmentValues.shared._fillWidth
    if (fill_0 == null) {
        return fillMaxWidth()
    }
    return then(fill_0(expandContainer))
}

/// Fill available remaining height.
///
/// This is a replacement for `fillMaxHeight` designed for situations when the composable may be in an `VStack` and does not want to push other items out.
///
/// - Warning: Containers with child content should use the `ComposeContainer` composable instead.
@Composable
fun Modifier.fillHeight(expandContainer: Boolean = true): Modifier {
    val fill_1 = EnvironmentValues.shared._fillHeight
    if (fill_1 == null) {
        return fillMaxHeight()
    }
    return then(fill_1(expandContainer))
}

/// Fill available remaining size.
///
/// This is a replacement for `fillMaxSize` designed for situations when the composable may be in an `HStack` or `VStack` and does not want to push other items out.
///
/// - Warning: Containers with child content should use the `ComposeContainer` composable instead.
@Composable
fun Modifier.fillSize(expandContainer: Boolean = true): Modifier = fillWidth(expandContainer = expandContainer).fillHeight(expandContainer = expandContainer)

/// Add padding equivalent to the given safe area.
@Composable
internal fun Modifier.padding(safeArea: SafeArea): Modifier {
    val density = LocalDensity.current.sref()
    val layoutDirection = LocalLayoutDirection.current.sref()
    val top = with(density) { -> (safeArea.safeBoundsPx.top - safeArea.presentationBoundsPx.top).toDp() }
    val left = with(density) { -> (safeArea.safeBoundsPx.left - safeArea.presentationBoundsPx.left).toDp() }
    val bottom = with(density) { -> (safeArea.presentationBoundsPx.bottom - safeArea.safeBoundsPx.bottom).toDp() }
    val right = with(density) { -> (safeArea.presentationBoundsPx.right - safeArea.safeBoundsPx.right).toDp() }
    val start = (if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) right else left).sref()
    val end = (if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) left else right).sref()
    return this.padding(top = top, start = start, bottom = bottom, end = end)
}

/// Convert padding values to edge insets in `dp` units.
@Composable
fun PaddingValues.asEdgeInsets(): EdgeInsets {
    val layoutDirection = (if (EnvironmentValues.shared.layoutDirection == LayoutDirection.rightToLeft) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr).sref()
    val top = Double(calculateTopPadding().value)
    val left = Double(calculateLeftPadding(layoutDirection).value)
    val bottom = Double(calculateBottomPadding().value)
    val right = Double(calculateRightPadding(layoutDirection).value)
    return EdgeInsets(top = top, leading = left, bottom = bottom, trailing = right)
}
