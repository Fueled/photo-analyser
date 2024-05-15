// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import kotlin.reflect.KClass
import skip.lib.*
import skip.lib.Array
import skip.lib.Set

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/// Common corner radius for our overlay presentations.
internal val overlayPresentationCornerRadius = 16.0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SheetPresentation(isPresented: Binding<Boolean>, isFullScreen: Boolean, context: ComposeContext, content: () -> View, onDismiss: (() -> Unit)?) {
    if (HandlePresentationOrientationChange(isPresented = isPresented)) {
        return
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (isPresented.get() || sheetState.isVisible) {
        val contentView = ComposeBuilder.from(content)
        val topInset = remember { -> mutableStateOf(0.dp) }
        val topCornerSize = if (isFullScreen) CornerSize(0.dp) else CornerSize(overlayPresentationCornerRadius.dp)
        val shape = with(LocalDensity.current) { -> RoundedCornerShapeWithTopOffset(offset = topInset.value.toPx(), topStart = topCornerSize, topEnd = topCornerSize) }
        val coroutineScope = rememberCoroutineScope()
        val onDismissRequest = { ->
            if (isFullScreen) {
                // Veto attempts to dismiss fullscreen modal via swipe or back button by re-showing
                if (isPresented.get()) {
                    coroutineScope.launch { -> sheetState.show() }
                }
            } else {
                isPresented.set(false)
            }
        }
        ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState, containerColor = androidx.compose.ui.graphics.Color.Unspecified, shape = shape, dragHandle = null, windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)) { ->
            val isEdgeToEdge = EnvironmentValues.shared._isEdgeToEdge == true
            val sheetDepth = EnvironmentValues.shared._sheetDepth
            val orientation = LocalConfiguration.current.orientation.sref()
            var systemBarEdges: Edge.Set = Edge.Set.all.sref()
            if (!isFullScreen && orientation != ORIENTATION_LANDSCAPE) {
                systemBarEdges.remove(Edge.Set.top)
                // We have to delay access to WindowInsets until inside the ModalBottomSheet composable to get accurate values
                val topBarHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                var inset = (topBarHeight + (24 * sheetDepth).dp).sref()
                if (!isEdgeToEdge) {
                    inset += 24.dp.sref()
                    systemBarEdges.remove(Edge.Set.bottom)
                }
                topInset.value = inset
                // Push the presentation root content area down an equal amount
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(inset))
            } else if (!isEdgeToEdge) {
                systemBarEdges.remove(Edge.Set.top)
                systemBarEdges.remove(Edge.Set.bottom)
                val inset = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                topInset.value = inset
                // Push the presentation root content area below the top bar
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(inset))
            }

            Box(modifier = Modifier.weight(1.0f)) { ->
                // Place outside of PresentationRoot recomposes
                val stateSaver = remember { -> ComposeStateSaver() }
                val presentationContext = context.content(stateSaver = stateSaver)
                // Place inside of ModalBottomSheet, which renders content async
                PresentationRoot(context = presentationContext, absoluteSystemBarEdges = systemBarEdges) { context ->
                    EnvironmentValues.shared.setValues({ it ->
                        if (!isFullScreen) {
                            it.set_sheetDepth(sheetDepth + 1)
                        }
                        it.setdismiss(DismissAction(action = { -> isPresented.set(false) }))
                    }, in_ = { -> contentView.Compose(context = context) })
                }
            }

            if (!isEdgeToEdge) {
                // Move the presentation root content area above the bottom bar
                val inset = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(inset))
            }
        }
    }
    if (!isPresented.get()) {
        LaunchedEffect(true) { ->
            if (sheetState.targetValue != SheetValue.Hidden) {
                sheetState.hide()
                onDismiss?.invoke()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConfirmationDialogPresentation(title: Text?, isPresented: Binding<Boolean>, context: ComposeContext, actions: View, message: View? = null) {
    if (HandlePresentationOrientationChange(isPresented = isPresented)) {
        return
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (isPresented.get() || sheetState.isVisible) {
        // Collect buttons and message text
        val actionViews: Array<View>
        val matchtarget_0 = actions as? ComposeBuilder
        if (matchtarget_0 != null) {
            val composeBuilder = matchtarget_0
            actionViews = composeBuilder.collectViews(context = context)
        } else {
            actionViews = arrayOf(actions)
        }
        val buttons = actionViews.compactMap { it ->
            it.strippingModifiers { it -> it as? Button }
        }
        val messageViews: Array<View>
        val matchtarget_1 = message as? ComposeBuilder
        if (matchtarget_1 != null) {
            val composeBuilder = matchtarget_1
            messageViews = composeBuilder.collectViews(context = context)
        } else if (message != null) {
            messageViews = arrayOf(message)
        } else {
            messageViews = arrayOf()
        }
        val messageText = messageViews.compactMap { it ->
            it.strippingModifiers { it -> it as? Text }
        }.first.sref()

        ModalBottomSheet(onDismissRequest = { -> isPresented.set(false) }, sheetState = sheetState, containerColor = androidx.compose.ui.graphics.Color.Transparent, dragHandle = null, windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)) { ->
            // Add padding to always keep the sheet away from the top of the screen. It should tap to dismiss like the background
            val interactionSource = remember { -> MutableInteractionSource() }
            Box(modifier = Modifier.fillMaxWidth().height(128.dp).clickable(interactionSource = interactionSource, indication = null, onClick = { -> isPresented.set(false) }))

            val stateSaver = remember { -> ComposeStateSaver() }
            val scrollState = rememberScrollState()
            val isEdgeToEdge = EnvironmentValues.shared._isEdgeToEdge == true
            val bottomSystemBarPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
            val modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = if (isEdgeToEdge) 0.dp else bottomSystemBarPadding)
                .clip(shape = RoundedCornerShape(topStart = overlayPresentationCornerRadius.dp, topEnd = overlayPresentationCornerRadius.dp))
                .background(Color.overlayBackground.colorImpl())
                .padding(bottom = if (isEdgeToEdge) bottomSystemBarPadding else 0.dp)
                .verticalScroll(scrollState)
            val contentContext = context.content(stateSaver = stateSaver)
            Column(modifier = modifier, horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) { -> ComposeConfirmationDialog(title = title, context = contentContext, isPresented = isPresented, buttons = buttons, message = messageText) }
        }
    }
    if (!isPresented.get()) {
        LaunchedEffect(true) { ->
            if (sheetState.targetValue != SheetValue.Hidden) {
                sheetState.hide()
            }
        }
    }
}

@Composable
internal fun ComposeConfirmationDialog(title: Text?, context: ComposeContext, isPresented: Binding<Boolean>, buttons: Array<Button>, message: Text?) {
    val padding = 16.dp.sref()
    if (title != null) {
        androidx.compose.material3.Text(modifier = Modifier.padding(horizontal = padding, vertical = 8.dp), color = Color.secondary.colorImpl(), text = title.localizedTextString(), style = Font.callout.bold().fontImpl())
    }
    if (message != null) {
        androidx.compose.material3.Text(modifier = Modifier.padding(start = padding, top = 8.dp, end = padding, bottom = padding), color = Color.secondary.colorImpl(), text = message.localizedTextString(), style = Font.callout.fontImpl())
    }
    if (title != null || message != null) {
        androidx.compose.material3.Divider()
    }

    val buttonModifier = Modifier.padding(horizontal = padding, vertical = padding)
    val buttonFont = Font.title3
    val tint = (EnvironmentValues.shared._tint ?: Color.accentColor).colorImpl()
    if (buttons.isEmpty) {
        ConfirmationDialogButton(action = { -> isPresented.set(false) }) { -> androidx.compose.material3.Text(modifier = buttonModifier, color = tint, text = stringResource(android.R.string.ok), style = buttonFont.fontImpl()) }
        return
    }

    var cancelButton: Button? = null
    for (button in buttons.sref()) {
        if (button.role == ButtonRole.cancel) {
            cancelButton = button
            continue
        }
        ConfirmationDialogButton(action = { ->
            isPresented.set(false)
            button.action()
        }) { ->
            val text = button.label.collectViews(context = context).compactMap { it ->
                it.strippingModifiers { it -> it as? Text }
            }.first.sref()
            val color = (if (button.role == ButtonRole.destructive) Color.red.colorImpl() else tint).sref()
            androidx.compose.material3.Text(modifier = buttonModifier, color = color, text = text?.localizedTextString() ?: "", maxLines = 1, style = buttonFont.fontImpl())
        }
        androidx.compose.material3.Divider()
    }
    if (cancelButton != null) {
        ConfirmationDialogButton(action = { ->
            isPresented.set(false)
            cancelButton.action()
        }) { ->
            val text = cancelButton.label.collectViews(context = context).compactMap { it ->
                it.strippingModifiers { it -> it as? Text }
            }.first.sref()
            androidx.compose.material3.Text(modifier = buttonModifier, color = tint, text = text?.localizedTextString() ?: "", maxLines = 1, style = buttonFont.bold().fontImpl())
        }
    } else {
        ConfirmationDialogButton(action = { -> isPresented.set(false) }) { -> androidx.compose.material3.Text(modifier = buttonModifier, color = tint, text = stringResource(android.R.string.cancel), style = buttonFont.bold().fontImpl()) }
    }
}

@Composable
internal fun ConfirmationDialogButton(action: () -> Unit, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().requiredHeightIn(min = 60.dp).clickable(onClick = action), contentAlignment = androidx.compose.ui.Alignment.Center) { -> content() }
}

/// Handle orientation changes in our various presentations.
///
/// Sheets deform on rotation, so we re-present in the new orientation.
@Composable
internal fun HandlePresentationOrientationChange(isPresented: Binding<Boolean>): Boolean {
    val orientation = rememberUpdatedState(LocalConfiguration.current.orientation)
    val rememberedOrientation = remember { -> mutableStateOf(orientation.value) }
    if (orientation.value == rememberedOrientation.value) {
        return false
    }
    LaunchedEffect(orientation.value, rememberedOrientation.value) { ->
        if (isPresented.get() && orientation.value != rememberedOrientation.value) {
            isPresented.set(false)
            isPresented.set(true)
        }
        rememberedOrientation.value = orientation.value
    }
    return true
}

enum class PresentationAdaptation: Sendable {
    automatic,
    none,
    popover,
    sheet,
    fullScreenCover;

    companion object {
    }
}

class PresentationBackgroundInteraction: Sendable {
    internal val enabled: Boolean?
    internal val upThrough: PresentationDetent?

    internal constructor(enabled: Boolean? = null, upThrough: PresentationDetent? = null) {
        this.enabled = enabled
        this.upThrough = upThrough
    }

    companion object {

        val automatic = PresentationBackgroundInteraction(enabled = null, upThrough = null)

        val enabled = PresentationBackgroundInteraction(enabled = true, upThrough = null)

        fun enabled(upThrough: PresentationDetent): PresentationBackgroundInteraction = PresentationBackgroundInteraction(enabled = true, upThrough = upThrough)

        val disabled = PresentationBackgroundInteraction(enabled = false, upThrough = null)
    }
}

enum class PresentationContentInteraction: Sendable {
    automatic,
    resizes,
    scrolls;

    companion object {
    }
}

sealed class PresentationDetent: Sendable {
    class MediumCase: PresentationDetent() {
    }
    class LargeCase: PresentationDetent() {
    }
    class FractionCase(val associated0: Double): PresentationDetent() {
    }
    class HeightCase(val associated0: Double): PresentationDetent() {
    }
    class CustomCase(val associated0: KClass<*>): PresentationDetent() {
    }

    class Context {
        val maxDetentValue: Double

        constructor(maxDetentValue: Double) {
            this.maxDetentValue = maxDetentValue
        }

        //        public subscript<T>(dynamicMember keyPath: KeyPath<EnvironmentValues, T>) -> T { get { fatalError() } }

        companion object {
        }
    }

    override fun hashCode(): Int {
        var hasher = Hasher()
        hash(into = InOut<Hasher>({ hasher }, { hasher = it }))
        return hasher.finalize()
    }
    fun hash(into: InOut<Hasher>) {
        val hasher = into
        when (this) {
            is PresentationDetent.MediumCase -> hasher.value.combine(1)
            is PresentationDetent.LargeCase -> hasher.value.combine(2)
            is PresentationDetent.FractionCase -> {
                val fraction = this.associated0
                hasher.value.combine(3)
                hasher.value.combine(fraction)
            }
            is PresentationDetent.HeightCase -> {
                val height = this.associated0
                hasher.value.combine(4)
                hasher.value.combine(height)
            }
            is PresentationDetent.CustomCase -> {
                val type = this.associated0
                hasher.value.combine(String(describing = type))
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PresentationDetent) {
            return false
        }
        val lhs = this
        val rhs = other
        when (lhs) {
            is PresentationDetent.MediumCase -> {
                if (rhs is PresentationDetent.MediumCase) {
                    return true
                } else {
                    return false
                }
            }
            is PresentationDetent.LargeCase -> {
                if (rhs is PresentationDetent.LargeCase) {
                    return true
                } else {
                    return false
                }
            }
            is PresentationDetent.FractionCase -> {
                val fraction1 = lhs.associated0
                if (rhs is PresentationDetent.FractionCase) {
                    val fraction2 = rhs.associated0
                    return fraction1 == fraction2
                } else {
                    return false
                }
            }
            is PresentationDetent.HeightCase -> {
                val height1 = lhs.associated0
                if (rhs is PresentationDetent.HeightCase) {
                    val height2 = rhs.associated0
                    return height1 == height2
                } else {
                    return false
                }
            }
            is PresentationDetent.CustomCase -> {
                val type1 = lhs.associated0
                if (rhs is PresentationDetent.CustomCase) {
                    val type2 = rhs.associated0
                    return type1 == type2
                } else {
                    return false
                }
            }
        }
    }

    companion object {
        val medium: PresentationDetent = MediumCase()
        val large: PresentationDetent = LargeCase()
        fun fraction(associated0: Double): PresentationDetent = FractionCase(associated0)
        fun height(associated0: Double): PresentationDetent = HeightCase(associated0)
        fun custom(associated0: KClass<*>): PresentationDetent = CustomCase(associated0)
    }
}

interface CustomPresentationDetent {
}

internal class PresentationModifierView: ComposeModifierView {
    private val presentation: @Composable (ComposeContext) -> Unit

    internal constructor(view: View, presentation: @Composable (ComposeContext) -> Unit): super(view = view) {
        this.presentation = presentation
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        EnvironmentValues.shared.setValues({ it ->
            // Clear environment state that should not transfer to presentations
            it.set_animation(null)
            it.set_searchableState(null)
        }, in_ = { -> presentation(context.content()) })
        view.Compose(context = context)
    }
}

/// Used to chop off the empty area Compose adds above the content of a bottom sheet modal, and to round the rop corners.
internal class RoundedCornerShapeWithTopOffset: CornerBasedShape {
    private val offset: Float

    internal constructor(offset: Float, topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize = CornerSize(0.dp), bottomStart: CornerSize = CornerSize(0.dp)): super(topStart = topStart, topEnd = topEnd, bottomEnd = bottomEnd, bottomStart = bottomStart) {
        this.offset = offset
    }

    override fun copy(topStart: CornerSize, topEnd: CornerSize, bottomEnd: CornerSize, bottomStart: CornerSize): RoundedCornerShapeWithTopOffset = RoundedCornerShapeWithTopOffset(offset = offset, topStart = topStart, topEnd = topEnd, bottomEnd = bottomEnd, bottomStart = bottomStart)

    override fun createOutline(size: Size, topStart: Float, topEnd: Float, bottomEnd: Float, bottomStart: Float, layoutDirection: androidx.compose.ui.unit.LayoutDirection): Outline {
        val rect = Rect(offset = Offset(x = 0.0f, y = offset), size = size)
        val topLeft = CornerRadius(if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Ltr) topStart else topEnd)
        val topRight = CornerRadius(if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Ltr) topStart else topEnd)
        val bottomRight = CornerRadius(if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Ltr) bottomEnd else bottomStart)
        val bottomLeft = CornerRadius(if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Ltr) bottomStart else bottomEnd)
        return Outline.Rounded(RoundRect(rect = rect, topLeft = topLeft, topRight = topRight, bottomRight = bottomRight, bottomLeft = bottomLeft))
    }
}
