// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Set

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/// Compose a view with the given frame.
@Composable
internal fun FrameLayout(view: View, context: ComposeContext, width: Double?, height: Double?, alignment: Alignment) {
    var modifier = context.modifier
    if (width != null) {
        modifier = modifier.requiredWidth(width.dp)
    }
    if (height != null) {
        modifier = modifier.requiredHeight(height.dp)
    }

    // If our content has a zIndex, we need to pull it into our modifiers so that it applies within the original
    // parent container. Otherwise the Box we use below would hide it
    view.strippingModifiers(until = { it -> it == ComposeModifierRole.zIndex }, perform = { it -> it as? ZIndexModifierView })?.let { zIndex ->
        modifier = zIndex.consume(with = modifier)
    }

    val isContainerView = view.strippingModifiers(perform = { it -> it is HStack || it is VStack || it is ZStack })
    ComposeContainer(modifier = modifier, fixedWidth = width != null, fixedHeight = height != null) { modifier ->
        // Apply the sizing modifier directly to containers, which would otherwise fit their size to their content instead
        if (isContainerView) {
            val contentContext = context.content(modifier = modifier)
            view.Compose(context = contentContext)
        } else {
            val contentContext = context.content()
            Box(modifier = modifier, contentAlignment = alignment.asComposeAlignment()) { -> view.Compose(context = contentContext) }
        }
    }
}

/// Compose a view with the given frame.
@Composable
internal fun FrameLayout(view: View, context: ComposeContext, minWidth: Double?, idealWidth: Double?, maxWidth: Double?, minHeight: Double?, idealHeight: Double?, maxHeight: Double?, alignment: Alignment) {
    val scrollAxes = EnvironmentValues.shared._scrollAxes.sref()
    var modifier = context.modifier
    if (maxWidth == Double.infinity) {
        modifier = modifier.fillWidth(expandContainer = !scrollAxes.contains(Axis.Set.horizontal))
        if ((minWidth != null) && (minWidth > 0.0)) {
            modifier = modifier.requiredWidthIn(min = minWidth.dp)
        }
    } else if (minWidth != null || maxWidth != null) {
        modifier = modifier.requiredWidthIn(min = if (minWidth != null) minWidth!!.dp else Dp.Unspecified, max = if (maxWidth != null) maxWidth!!.dp else Dp.Unspecified)
    }
    if (maxHeight == Double.infinity) {
        modifier = modifier.fillHeight(expandContainer = !scrollAxes.contains(Axis.Set.vertical))
        if ((minHeight != null) && (minHeight > 0.0)) {
            modifier = modifier.requiredHeightIn(min = minHeight.dp)
        }
    } else if (minHeight != null || maxHeight != null) {
        modifier = modifier.requiredHeightIn(min = if (minHeight != null) minHeight!!.dp else Dp.Unspecified, max = if (maxHeight != null) maxHeight!!.dp else Dp.Unspecified)
    }
    val isContainerView = view.strippingModifiers(perform = { it -> it is HStack || it is VStack || it is ZStack })
    ComposeContainer(modifier = modifier, fixedWidth = maxWidth != null && maxWidth != Double.infinity, fixedHeight = maxHeight != null && maxHeight != Double.infinity) { modifier ->
        // Apply the sizing modifier directly to containers, which would otherwise fit their size to their content instead
        if (isContainerView) {
            val contentContext = context.content(modifier = modifier)
            view.Compose(context = contentContext)
        } else {
            val contentContext = context.content()
            Box(modifier = modifier, contentAlignment = alignment.asComposeAlignment()) { -> view.Compose(context = contentContext) }
        }
    }
}

/// Compose a view with the given background.
@Composable
internal fun BackgroundLayout(view: View, context: ComposeContext, background: View, alignment: Alignment) {
    TargetViewLayout(context = context, isOverlay = false, alignment = alignment, target = { it -> view.Compose(context = it) }, dependent = { it -> background.Compose(context = it) })
}

/// Compose a view with the given overlay.
@Composable
internal fun OverlayLayout(view: View, context: ComposeContext, overlay: View, alignment: Alignment) {
    TargetViewLayout(context = context, isOverlay = true, alignment = alignment, target = { it -> view.Compose(context = it) }, dependent = { it -> overlay.Compose(context = it) })
}

@Composable
internal fun TargetViewLayout(context: ComposeContext, isOverlay: Boolean, alignment: Alignment, target: @Composable (ComposeContext) -> Unit, dependent: @Composable (ComposeContext) -> Unit) {
    val contentContext = context.content()
    Layout(modifier = context.modifier, content = { ->
        target(contentContext)
        // Dependent view lays out with fixed bounds dictated by the target view size
        ComposeContainer(fixedWidth = true, fixedHeight = true) { modifier -> dependent(context.content(modifier = modifier)) }
    }) l@{ measurables, constraints ->
        if (measurables.isEmpty()) {
            return@l layout(width = 0, height = 0) { ->  }
        }
        // Base layout entirely on the target view size
        val targetPlaceable = measurables[0].measure(constraints)
        val dependentConstraints = Constraints(maxWidth = targetPlaceable.width, maxHeight = targetPlaceable.height)
        val dependentPlaceables = measurables.drop(1).map { it -> it.measure(dependentConstraints) }
        layout(width = targetPlaceable.width, height = targetPlaceable.height) { ->
            if (!isOverlay) {
                for (dependentPlaceable in dependentPlaceables.sref()) {
                    val (x, y) = placeView(width = dependentPlaceable.width, height = dependentPlaceable.height, inWidth = targetPlaceable.width, inHeight = targetPlaceable.height, alignment = alignment)
                    dependentPlaceable.placeRelative(x = x, y = y)
                }
            }
            targetPlaceable.placeRelative(x = 0, y = 0)
            if (isOverlay) {
                for (dependentPlaceable in dependentPlaceables.sref()) {
                    val (x, y) = placeView(width = dependentPlaceable.width, height = dependentPlaceable.height, inWidth = targetPlaceable.width, inHeight = targetPlaceable.height, alignment = alignment)
                    dependentPlaceable.placeRelative(x = x, y = y)
                }
            }
        }
    }
}

/// Layout the given view to ignore the given safe areas.
@Composable
internal fun IgnoresSafeAreaLayout(view: View, edges: Edge.Set, context: ComposeContext) {
    IgnoresSafeAreaLayout(edges = edges, context = context) { it -> view.Compose(it) }
}

@Composable
internal fun IgnoresSafeAreaLayout(edges: Edge.Set, context: ComposeContext, target: @Composable (ComposeContext) -> Unit) {
    val safeArea_0 = EnvironmentValues.shared._safeArea
    if (safeArea_0 == null) {
        target(context)
        return
    }

    var (safeLeft, safeTop, safeRight, safeBottom) = safeArea_0.safeBoundsPx.sref()
    var topPx = 0
    if (edges.contains(Edge.Set.top)) {
        topPx = Int(safeArea_0.safeBoundsPx.top - safeArea_0.presentationBoundsPx.top)
        safeTop = safeArea_0.presentationBoundsPx.top.sref()
    }
    var bottomPx = 0
    if (edges.contains(Edge.Set.bottom)) {
        bottomPx = Int(safeArea_0.presentationBoundsPx.bottom - safeArea_0.safeBoundsPx.bottom)
        safeBottom = safeArea_0.presentationBoundsPx.bottom.sref()
    }
    var leadingPx = 0
    if (edges.contains(Edge.Set.leading)) {
        if (LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl) {
            leadingPx = Int(safeArea_0.presentationBoundsPx.right - safeArea_0.safeBoundsPx.right)
            safeRight = safeArea_0.presentationBoundsPx.right.sref()
        } else {
            leadingPx = Int(safeArea_0.safeBoundsPx.left - safeArea_0.presentationBoundsPx.left)
            safeLeft = safeArea_0.presentationBoundsPx.left.sref()
        }
    }
    var trailingPx = 0
    if (edges.contains(Edge.Set.trailing)) {
        if (LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl) {
            trailingPx = Int(safeArea_0.safeBoundsPx.left - safeArea_0.presentationBoundsPx.left)
            safeLeft = safeArea_0.presentationBoundsPx.left.sref()
        } else {
            trailingPx = Int(safeArea_0.presentationBoundsPx.right - safeArea_0.safeBoundsPx.right)
            safeRight = safeArea_0.presentationBoundsPx.right.sref()
        }
    }

    val contentSafeBounds = Rect(top = safeTop, left = safeLeft, bottom = safeBottom, right = safeRight)
    val contentSafeArea = SafeArea(presentation = safeArea_0.presentationBoundsPx, safe = contentSafeBounds, absoluteSystemBars = safeArea_0.absoluteSystemBarEdges)
    EnvironmentValues.shared.setValues({ it -> it.set_safeArea(contentSafeArea) }, in_ = { ->
        Layout(content = { -> target(context) }) l@{ measurables, constraints ->
            if (measurables.isEmpty()) {
                return@l layout(width = 0, height = 0) { ->  }
            }
            val updatedConstraints = constraints.copy(maxWidth = constraints.maxWidth + leadingPx + trailingPx, maxHeight = constraints.maxHeight + topPx + bottomPx)
            val targetPlaceables = measurables.map { it -> it.measure(updatedConstraints) }
            layout(width = targetPlaceables[0].width, height = targetPlaceables[0].height) { ->
                // Layout will center extra space by default
                val relativeTopPx = topPx - ((topPx + bottomPx) / 2)
                val relativeLeadingPx = leadingPx - ((leadingPx + trailingPx) / 2)
                for (targetPlaceable in targetPlaceables.sref()) {
                    targetPlaceable.placeRelative(x = -relativeLeadingPx, y = -relativeTopPx)
                }
            }
        }
    })
}

/// Layout the given view with the given padding.
@Composable
internal fun PaddingLayout(view: View, padding: EdgeInsets, context: ComposeContext) {
    PaddingLayout(padding = padding, context = context) { it -> view.Compose(it) }
}

@Composable
internal fun PaddingLayout(padding: EdgeInsets, context: ComposeContext, target: @Composable (ComposeContext) -> Unit) {
    val density = LocalDensity.current.sref()
    val topPx = with(density) { -> padding.top.dp.roundToPx() }
    val bottomPx = with(density) { -> padding.bottom.dp.roundToPx() }
    val leadingPx = with(density) { -> padding.leading.dp.roundToPx() }
    val trailingPx = with(density) { -> padding.trailing.dp.roundToPx() }
    Layout(modifier = context.modifier, content = { -> target(context.content()) }) l@{ measurables, constraints ->
        if (measurables.isEmpty()) {
            return@l layout(width = 0, height = 0) { ->  }
        }
        val updatedConstraints = constraints.copy(minWidth = constraint(constraints.minWidth, subtracting = leadingPx + trailingPx), minHeight = constraint(constraints.minHeight, subtracting = topPx + bottomPx), maxWidth = constraint(constraints.maxWidth, subtracting = leadingPx + trailingPx), maxHeight = constraint(constraints.maxHeight, subtracting = topPx + bottomPx))
        val targetPlaceables = measurables.map { it -> it.measure(updatedConstraints) }
        layout(width = targetPlaceables[0].width + leadingPx + trailingPx, height = targetPlaceables[0].height + topPx + bottomPx) { ->
            for (targetPlaceable in targetPlaceables.sref()) {
                targetPlaceable.placeRelative(x = leadingPx, y = topPx)
            }
        }
    }
}

private fun constraint(value: Int, subtracting: Int): Int {
    if (value == Int.MAX_VALUE) {
        return value
    }
    return max(0, value - subtracting)
}

private fun placeView(width: Int, height: Int, inWidth: Int, inHeight: Int, alignment: Alignment): Tuple2<Int, Int> {
    val centerX = (inWidth - width) / 2
    val centerY = (inHeight - height) / 2
    when (alignment) {
        Alignment.leading, Alignment.leadingFirstTextBaseline, Alignment.leadingLastTextBaseline -> return Tuple2(0, centerY)
        Alignment.trailing, Alignment.trailingFirstTextBaseline, Alignment.trailingLastTextBaseline -> return Tuple2(inWidth - width, centerY)
        Alignment.top -> return Tuple2(centerX, 0)
        Alignment.bottom -> return Tuple2(centerX, inHeight - height)
        Alignment.topLeading -> return Tuple2(0, 0)
        Alignment.topTrailing -> return Tuple2(inWidth - width, 0)
        Alignment.bottomLeading -> return Tuple2(0, inHeight - height)
        Alignment.bottomTrailing -> return Tuple2(inWidth - width, inHeight - height)
        else -> return Tuple2(centerX, centerY)
    }
}

