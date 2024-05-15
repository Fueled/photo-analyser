// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import kotlin.reflect.KClass
import skip.lib.*
import skip.lib.Array
import skip.lib.Collection
import skip.lib.Set

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import skip.foundation.*
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.async
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path.Companion.combine
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.IconButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import kotlin.reflect.full.companionObjectInstance
import androidx.compose.runtime.ProvidedValue
import skip.model.*
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import skip.foundation.LocalizedStringResource
import skip.foundation.Bundle
import skip.foundation.Locale
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

interface View {
    fun body(): View = EmptyView()

    fun animation(animation: Animation?, value: Any?): View {
        return ComposeModifierView(contentView = this) { view, context ->
            val rememberedValue = rememberSaveable(stateSaver = context.stateSaver as Saver<Any?, Any>) { -> mutableStateOf(value) }
            val hasChangedValue = rememberSaveable(stateSaver = context.stateSaver as Saver<Boolean, Any>) { -> mutableStateOf(false) }
            val isValueChange = rememberedValue.value != value
            if (isValueChange) {
                rememberedValue.value = value
                hasChangedValue.value = true
            }
            EnvironmentValues.shared.setValues({ it ->
                // Pass down an infinite repeating animation every time, because it always overrides any withAnimation spec
                if (isValueChange || (animation?.isInfinite == true && hasChangedValue.value)) {
                    it.set_animation(animation)
                }
            }, in_ = { -> view.Compose(context = context) })
        }
    }

    fun animation(animation: Animation?): View {
        return environment({ it -> EnvironmentValues.shared.set_animation(it) }, animation)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun springLoadingBehavior(behavior: SpringLoadingBehavior): View = this.sref()

    fun transition(t: AnyTransition): View = TransitionModifierView(view = this, transition = t.transition)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contentTransition(transition: Any): View {
        fatalError()
    }
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scenePadding(edges: Edge.Set = Edge.Set.all): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scenePadding(padding: ScenePadding, edges: Edge.Set = Edge.Set.all): View = this.sref()
    fun colorScheme(colorScheme: ColorScheme): View {
        return ComposeModifierView(contentView = this) { view, context ->
            MaterialTheme(colorScheme = colorScheme.asMaterialTheme()) { -> view.Compose(context = context) }
        }
    }

    fun preferredColorScheme(colorScheme: ColorScheme?): View = preference(key = PreferredColorSchemePreferenceKey::class, value = PreferredColorScheme(colorScheme = colorScheme))
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun alert(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, actions: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun alert(title: String, isPresented: Binding<Boolean>, actions: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun alert(title: Text, isPresented: Binding<Boolean>, actions: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun alert(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, actions: () -> View, message: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun alert(title: String, isPresented: Binding<Boolean>, actions: () -> View, message: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun alert(title: Text, isPresented: Binding<Boolean>, actions: () -> View, message: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> alert(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, presenting: T?, actions: (T) -> View): View {
        val data = presenting
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> alert(title: String, isPresented: Binding<Boolean>, presenting: T?, actions: (T) -> View): View {
        val data = presenting
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> alert(title: Text, isPresented: Binding<Boolean>, presenting: T?, actions: (T) -> View): View {
        val data = presenting
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> alert(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, presenting: T?, actions: (T) -> View, message: (T) -> View): View {
        val data = presenting
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> alert(title: String, isPresented: Binding<Boolean>, presenting: T?, actions: (T) -> View, message: (T) -> View): View {
        val data = presenting
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> alert(title: Text, isPresented: Binding<Boolean>, presenting: T?, actions: (T) -> View, message: (T) -> View): View {
        val data = presenting
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <E> alert(isPresented: Binding<Boolean>, error: E?, actions: () -> View): View where E: LocalizedError = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <E> alert(isPresented: Binding<Boolean>, error: E?, actions: (E) -> View, message: (E) -> View): View where E: LocalizedError = this.sref()
    fun deleteDisabled(isDisabled: Boolean): View = EditActionsModifierView(view = this, isDeleteDisabled = isDisabled)

    fun moveDisabled(isDisabled: Boolean): View = EditActionsModifierView(view = this, isMoveDisabled = isDisabled)
    fun menuStyle(style: MenuStyle): View = this.sref()

    fun menuActionDismissBehavior(behavior: MenuActionDismissBehavior): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun menuIndicator(visibility: Visibility): View = this.sref()

    fun menuOrder(order: MenuOrder): View = this.sref()
    fun searchable(text: Binding<String>, placement: SearchFieldPlacement = SearchFieldPlacement.automatic, prompt: Text? = null): View {
        return ComposeModifierView(contentView = this) { view, context ->
            val submitState = EnvironmentValues.shared._onSubmitState
            val isSearching = rememberSaveable(stateSaver = context.stateSaver as Saver<Boolean, Any>) { -> mutableStateOf(false) }
            val isOnNavigationStack = view.strippingModifiers { it -> it is NavigationStack<*> }
            val state = SearchableState(text = text, prompt = prompt, submitState = submitState, isSearching = isSearching, isOnNavigationStack = isOnNavigationStack)
            EnvironmentValues.shared.setValues({ it -> it.set_searchableState(state) }, in_ = { -> view.Compose(context = context) })
        }
    }

    fun searchable(text: Binding<String>, placement: SearchFieldPlacement = SearchFieldPlacement.automatic, prompt: LocalizedStringKey): View = searchable(text = text, placement = placement, prompt = Text(prompt))

    fun searchable(text: Binding<String>, placement: SearchFieldPlacement = SearchFieldPlacement.automatic, prompt: String): View = searchable(text = text, placement = placement, prompt = Text(verbatim = prompt))
    fun toolbar(content: () -> View): View = preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(content = arrayOf(content())))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun toolbar(id: String, content: () -> View): View = this.sref()

    fun toolbar(visibility: Visibility, vararg for_: ToolbarPlacement): View {
        val bars = Array(for_.asIterable())
        var view = this
        if (bars.contains(ToolbarPlacement.tabBar)) {
            view = view.preference(key = TabBarPreferenceKey::class, value = ToolbarBarPreferences(visibility = visibility))
        }
        if (bars.contains(where = { it -> it != ToolbarPlacement.tabBar })) {
            view = view.preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(visibility = visibility, for_ = bars))
        }
        return view.sref()
    }

    fun toolbarBackground(style: ShapeStyle, vararg for_: ToolbarPlacement): View {
        val bars = Array(for_.asIterable())
        var view = this
        if (bars.contains(ToolbarPlacement.tabBar)) {
            view = view.preference(key = TabBarPreferenceKey::class, value = ToolbarBarPreferences(background = style))
        }
        if (bars.contains(where = { it -> it != ToolbarPlacement.tabBar })) {
            view = view.preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(background = style, for_ = bars))
        }
        return view.sref()
    }

    fun toolbarBackground(visibility: Visibility, vararg for_: ToolbarPlacement): View {
        val bars = Array(for_.asIterable())
        var view = this
        if (bars.contains(ToolbarPlacement.tabBar)) {
            view = view.preference(key = TabBarPreferenceKey::class, value = ToolbarBarPreferences(backgroundVisibility = visibility))
        }
        if (bars.contains(where = { it -> it != ToolbarPlacement.tabBar })) {
            view = view.preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(backgroundVisibility = visibility, for_ = bars))
        }
        return view.sref()
    }

    fun toolbarColorScheme(colorScheme: ColorScheme?, vararg for_: ToolbarPlacement): View {
        val bars = Array(for_.asIterable())
        var view = this
        if (bars.contains(ToolbarPlacement.tabBar)) {
            view = view.preference(key = TabBarPreferenceKey::class, value = ToolbarBarPreferences(colorScheme = colorScheme))
        }
        if (bars.contains(where = { it -> it != ToolbarPlacement.tabBar })) {
            view = view.preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(colorScheme = colorScheme, for_ = bars))
        }
        return view.sref()
    }

    fun toolbarTitleDisplayMode(mode: ToolbarTitleDisplayMode): View = preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(titleDisplayMode = mode))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun toolbarTitleMenu(content: () -> View): View = preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(titleMenu = content()))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun toolbarRole(role: ToolbarRole): View = this.sref()
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun imageScale(scale: Image.Scale): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun allowedDynamicRange(range: Image.DynamicRange?): View = this.sref()
    fun progressViewStyle(style: ProgressViewStyle): View {
        return environment({ it -> EnvironmentValues.shared.set_progressViewStyle(it) }, style)
    }
    fun disclosureGroupStyle(style: DisclosureGroupStyle): View {
        // We only support the single .automatic style
        return this.sref()
    }
    fun formStyle(style: FormStyle): View = this.sref()
    fun listRowBackground(view: Any?): View = ListItemModifierView(view = this, background = view as View?)

    fun listRowSeparator(visibility: Visibility, edges: VerticalEdge.Set = VerticalEdge.Set.all): View = ListItemModifierView(view = this, separator = visibility)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listRowSeparatorTint(color: Color?, edges: VerticalEdge.Set = VerticalEdge.Set.all): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listSectionSeparator(visibility: Visibility, edges: VerticalEdge.Set = VerticalEdge.Set.all): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listSectionSeparatorTint(color: Color?, edges: VerticalEdge.Set = VerticalEdge.Set.all): View = this.sref()

    fun listStyle(style: ListStyle): View {
        return environment({ it -> EnvironmentValues.shared.set_listStyle(it) }, style)
    }

    fun listItemTint(tint: Color?): View {
        return environment({ it -> EnvironmentValues.shared.set_listItemTint(it) }, tint)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listItemTint(tint: ListItemTint?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listRowInsets(insets: EdgeInsets?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listRowSpacing(spacing: Double?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listSectionSpacing(spacing: ListSectionSpacing): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun listSectionSpacing(spacing: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun swipeActions(edge: HorizontalEdge = HorizontalEdge.trailing, allowsFullSwipe: Boolean = true, content: () -> View): View = this.sref()
    fun navigationBarBackButtonHidden(hidesBackButton: Boolean = true): View = preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(backButtonHidden = hidesBackButton))

    fun navigationBarTitleDisplayMode(displayMode: NavigationBarItem.TitleDisplayMode): View {
        val toolbarTitleDisplayMode: ToolbarTitleDisplayMode
        when (displayMode) {
            NavigationBarItem.TitleDisplayMode.automatic -> toolbarTitleDisplayMode = ToolbarTitleDisplayMode.automatic
            NavigationBarItem.TitleDisplayMode.inline -> toolbarTitleDisplayMode = ToolbarTitleDisplayMode.inline
            NavigationBarItem.TitleDisplayMode.large -> toolbarTitleDisplayMode = ToolbarTitleDisplayMode.large
        }
        return preference(key = ToolbarPreferenceKey::class, value = ToolbarPreferences(titleDisplayMode = toolbarTitleDisplayMode))
    }

    fun <D> navigationDestination(for_: KClass<D>, destination: (D) -> View): View where D: Any {
        val data = for_
        val destinations: Dictionary<KClass<*>, NavigationDestination> = dictionaryOf(Tuple2(data, NavigationDestination(destination = { it -> destination(it as D) })))
        return preference(key = NavigationDestinationsPreferenceKey::class, value = destinations)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> navigationDestination(isPresented: Binding<Boolean>, destination: () -> V): View where V: View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <D, C> navigationDestination(item: Binding<D?>, destination: (D) -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun navigationDocument(url: URL): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun navigationSplitViewColumnWidth(width: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun navigationSplitViewColumnWidth(min: Double? = null, ideal: Double, max: Double? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun navigationSplitViewStyle(style: NavigationSplitViewStyle): View = this.sref()

    fun navigationTitle(title: Text): View = preference(key = NavigationTitlePreferenceKey::class, value = title)

    fun navigationTitle(title: LocalizedStringKey): View = preference(key = NavigationTitlePreferenceKey::class, value = Text(title))

    fun navigationTitle(title: String): View = preference(key = NavigationTitlePreferenceKey::class, value = Text(verbatim = title))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun navigationTitle(title: Binding<String>): View = this.sref()
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contentMargins(edges: Edge.Set = Edge.Set.all, insets: EdgeInsets, for_: ContentMarginPlacement = ContentMarginPlacement.automatic): View {
        val placement = for_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contentMargins(edges: Edge.Set = Edge.Set.all, length: Double?, for_: ContentMarginPlacement = ContentMarginPlacement.automatic): View {
        val placement = for_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contentMargins(length: Double, for_: ContentMarginPlacement = ContentMarginPlacement.automatic): View {
        val placement = for_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollBounceBehavior(behavior: ScrollBounceBehavior, axes: Axis.Set = Axis.Set.of(Axis.Set.vertical)): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollClipDisabled(disabled: Boolean = true): View = this.sref()

    fun scrollContentBackground(visibility: Visibility): View {
        return environment({ it -> EnvironmentValues.shared.set_scrollContentBackground(it) }, visibility)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollDismissesKeyboard(mode: ScrollDismissesKeyboardMode): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollDisabled(disabled: Boolean): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollIndicators(visibility: ScrollIndicatorVisibility, axes: Axis.Set = Axis.Set.of(Axis.Set.vertical, Axis.Set.horizontal)): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollIndicatorsFlash(onAppear: Boolean): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollIndicatorsFlash(trigger: Equatable): View {
        val value = trigger
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollPosition(id: Binding<Hashable?>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollPosition(initialAnchor: UnitPoint?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollTarget(isEnabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollTargetBehavior(behavior: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scrollTargetLayout(isEnabled: Boolean = true): View = this.sref()
    fun tabItem(label: () -> View): View = TabItemModifierView(view = this, label = label)

    fun tabViewStyle(style: TabViewStyle): View {
        // We only support .automatic
        return this.sref()
    }
    fun buttonStyle(style: ButtonStyle): View {
        return environment({ it -> EnvironmentValues.shared.set_buttonStyle(it) }, style)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun buttonRepeatBehavior(behavior: ButtonRepeatBehavior): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun buttonBorderShape(shape: Any): View = this.sref()
    fun controlGroupStyle(style: ControlGroupStyle): View = this.sref()
    fun datePickerStyle(style: DatePickerStyle): View {
        // We only support .automatic / .compact
        return this.sref()
    }
    fun pickerStyle(style: PickerStyle): View {
        return environment({ it -> EnvironmentValues.shared.set_pickerStyle(it) }, style)
    }
    fun toggleStyle(style: ToggleStyle): View {
        // We only support Android's Switch control
        return this.sref()
    }

    fun environmentObject(object_: Any): View = environmentObject(type = type(of = object_), object_ = object_)

    // Must be public to allow access from our inline `environment` function.
    fun environmentObject(type: KClass<*>, object_: Any?): View {
        return ComposeModifierView(contentView = this) { view, context ->
            val compositionLocal = EnvironmentValues.shared.objectCompositionLocal(type = type)
            val value = (object_ ?: Unit).sref()
            val provided = compositionLocal provides value
            CompositionLocalProvider(provided) { -> view.Compose(context = context) }
        }
    }

    // We rely on the transpiler to turn the `WriteableKeyPath` provided in code into a `setValue` closure
    fun <V> environment(setValue: (V) -> Unit, value: V): View {
        return ComposeModifierView(contentView = this) { view, context ->
            EnvironmentValues.shared.setValues({ _ -> setValue(value) }, in_ = { -> view.Compose(context = context) })
        }
    }
    fun preference(key: KClass<*>, value: Any): View {
        return ComposeModifierView(targetView = this) l@{ _ ->
            PreferenceValues.shared.reducePreference(key = key, value = value)
            return@l ComposeResult.ok
        }
    }
    fun modifier(viewModifier: ViewModifier): View {
        return ComposeModifierView(contentView = this) { view, context -> viewModifier.Compose(content = view, context = context) }
    }
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun symbolEffect(effect: Any, options: Any? = null, isActive: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun symbolEffect(effect: Any, options: Any? = null, value: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun symbolRenderingMode(mode: SymbolRenderingMode?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun symbolVariant(variant: SymbolVariants): View = this.sref()
    fun confirmationDialog(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, actions: () -> View): View = confirmationDialog(Text(titleKey), isPresented = isPresented, titleVisibility = titleVisibility, actions = actions)

    fun confirmationDialog(title: String, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, actions: () -> View): View = confirmationDialog(Text(verbatim = title), isPresented = isPresented, titleVisibility = titleVisibility, actions = actions)

    fun confirmationDialog(title: Text, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, actions: () -> View): View {
        return PresentationModifierView(view = this) { context -> ConfirmationDialogPresentation(title = if (titleVisibility != Visibility.visible) null else title, isPresented = isPresented, context = context, actions = actions()) }
    }

    fun confirmationDialog(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, actions: () -> View, message: () -> View): View = confirmationDialog(Text(titleKey), isPresented = isPresented, titleVisibility = titleVisibility, actions = actions, message = message)

    fun confirmationDialog(title: String, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, actions: () -> View, message: () -> View): View = confirmationDialog(Text(verbatim = title), isPresented = isPresented, titleVisibility = titleVisibility, actions = actions, message = message)

    fun confirmationDialog(title: Text, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, actions: () -> View, message: () -> View): View {
        return PresentationModifierView(view = this) { context -> ConfirmationDialogPresentation(title = if (titleVisibility != Visibility.visible) null else title, isPresented = isPresented, context = context, actions = actions(), message = message()) }
    }

    fun <T> confirmationDialog(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, presenting: T?, actions: (T) -> View): View {
        val data = presenting
        return confirmationDialog(Text(titleKey), isPresented = isPresented, titleVisibility = titleVisibility, presenting = data, actions = actions)
    }

    fun <T> confirmationDialog(title: String, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, presenting: T?, actions: (T) -> View): View {
        val data = presenting
        return confirmationDialog(Text(verbatim = title), isPresented = isPresented, titleVisibility = titleVisibility, presenting = data, actions = actions)
    }

    fun <T> confirmationDialog(title: Text, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, presenting: T?, actions: (T) -> View): View {
        val data = presenting
        val actionsWithData: () -> View
        if (data != null) {
            actionsWithData = { -> actions(data) }
        } else {
            actionsWithData = { -> EmptyView() }
        }
        return confirmationDialog(title, isPresented = isPresented, titleVisibility = titleVisibility, actions = actionsWithData)
    }

    fun <T> confirmationDialog(titleKey: LocalizedStringKey, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, presenting: T?, actions: (T) -> View, message: (T) -> View): View {
        val data = presenting
        return confirmationDialog(Text(titleKey), isPresented = isPresented, titleVisibility = titleVisibility, presenting = data, actions = actions, message = message)
    }

    fun <T> confirmationDialog(title: String, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, presenting: T?, actions: (T) -> View, message: (T) -> View): View {
        val data = presenting
        return confirmationDialog(Text(verbatim = title), isPresented = isPresented, titleVisibility = titleVisibility, presenting = data, actions = actions, message = message)
    }

    fun <T> confirmationDialog(title: Text, isPresented: Binding<Boolean>, titleVisibility: Visibility = Visibility.automatic, presenting: T?, actions: (T) -> View, message: (T) -> View): View {
        val data = presenting
        val actionsWithData: () -> View
        val messageWithData: () -> View
        if (data != null) {
            actionsWithData = { -> actions(data) }
            messageWithData = { -> message(data) }
        } else {
            actionsWithData = { -> EmptyView() }
            messageWithData = { -> EmptyView() }
        }
        return confirmationDialog(title, isPresented = isPresented, titleVisibility = titleVisibility, actions = actionsWithData, message = messageWithData)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <Item> fullScreenCover(item: Binding<Item?>, onDismiss: (() -> Unit)? = null, content: (Item) -> View): View = this.sref()

    fun fullScreenCover(isPresented: Binding<Boolean>, onDismiss: (() -> Unit)? = null, content: () -> View): View {
        return PresentationModifierView(view = this) { context -> SheetPresentation(isPresented = isPresented, isFullScreen = true, context = context, content = content, onDismiss = onDismiss) }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationDetents(detents: Set<PresentationDetent>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationDetents(detents: Set<PresentationDetent>, selection: Binding<PresentationDetent>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationDragIndicator(visibility: Visibility): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationBackgroundInteraction(interaction: PresentationBackgroundInteraction): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationCompactAdaptation(adaptation: PresentationAdaptation): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationCompactAdaptation(horizontal: PresentationAdaptation, vertical: PresentationAdaptation): View {
        val horizontalAdaptation = horizontal
        val verticalAdaptation = vertical
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationCornerRadius(cornerRadius: Double?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationContentInteraction(behavior: PresentationContentInteraction): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationBackground(style: ShapeStyle): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun presentationBackground(alignment: Alignment = Alignment.center, content: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <Item> sheet(item: Binding<Item?>, onDismiss: (() -> Unit)? = null, content: (Item) -> View): View = this.sref()

    fun sheet(isPresented: Binding<Boolean>, onDismiss: (() -> Unit)? = null, content: () -> View): View {
        return PresentationModifierView(view = this) { context -> SheetPresentation(isPresented = isPresented, isFullScreen = false, context = context, content = content, onDismiss = onDismiss) }
    }
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defaultAppStorage(store: UserDefaults): View = this.sref()
    fun accessibilityIdentifier(identifier: String): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            it.value.modifier = it.value.modifier.testTag(identifier)
            return@l ComposeResult.ok
        }
    }

    fun accessibilityLabel(label: Text): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            val description = label.localizedTextString()
            it.value.modifier = it.value.modifier.semantics { -> contentDescription = description }
            return@l ComposeResult.ok
        }
    }

    fun accessibilityLabel(label: String): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            it.value.modifier = it.value.modifier.semantics { -> contentDescription = label }
            return@l ComposeResult.ok
        }
    }

    fun accessibilityLabel(key: LocalizedStringKey): View = accessibilityLabel(Text(key))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(key: AccessibilityCustomContentKey, value: Text?, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(key: AccessibilityCustomContentKey, valueKey: LocalizedStringKey, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(key: AccessibilityCustomContentKey, value: String, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(label: Text, value: Text, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(labelKey: LocalizedStringKey, value: Text, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(labelKey: LocalizedStringKey, valueKey: LocalizedStringKey, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityCustomContent(labelKey: LocalizedStringKey, value: String, importance: Any? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityInputLabels(inputLabels: Any): View {
        // Accepts [Text], [LocalizedStringKey], or [String]
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityHint(hint: Text): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityHint(hintKey: LocalizedStringKey): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityHint(hint: String): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityAction(actionKind: AccessibilityActionKind = AccessibilityActionKind.default, handler: () -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityAction(named: Text, handler: () -> Unit): View {
        val name = named
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityAction(action: () -> Unit, label: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityActions(content: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityAction(named: LocalizedStringKey, handler: () -> Unit): View {
        val nameKey = named
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityAction(named: String, handler: () -> Unit): View {
        val name = named
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(label: Text, entries: () -> AccessibilityRotorContent): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(systemRotor: AccessibilitySystemRotor, entries: () -> AccessibilityRotorContent): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel> accessibilityRotor(rotorLabel: Text, entries: Array<EntryModel>, entryLabel: Any): View where EntryModel: Identifiable<*> = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel, ID> accessibilityRotor(rotorLabel: Text, entries: Array<EntryModel>, entryID: Any, entryLabel: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel> accessibilityRotor(systemRotor: AccessibilitySystemRotor, entries: Array<EntryModel>, entryLabel: Any): View where EntryModel: Identifiable<*> = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel, ID> accessibilityRotor(systemRotor: AccessibilitySystemRotor, entries: Array<EntryModel>, entryID: Any, entryLabel: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(label: Text, textRanges: Array<IntRange>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(systemRotor: AccessibilitySystemRotor, textRanges: Array<IntRange>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(labelKey: LocalizedStringKey, entries: () -> AccessibilityRotorContent): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(label: String, entries: () -> AccessibilityRotorContent): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel> accessibilityRotor(rotorLabelKey: LocalizedStringKey, entries: Array<EntryModel>, entryLabel: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel> accessibilityRotor(rotorLabel: String, entries: Array<EntryModel>, entryLabel: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel, ID> accessibilityRotor(rotorLabelKey: LocalizedStringKey, entries: Array<EntryModel>, entryID: Any, entryLabel: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <EntryModel, ID> accessibilityRotor(rotorLabel: String, entries: Array<EntryModel>, entryID: Any, entryLabel: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(labelKey: LocalizedStringKey, textRanges: Array<IntRange>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotor(label: String, textRanges: Array<IntRange>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityIgnoresInvertColors(active: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityZoomAction(handler: (AccessibilityZoomGestureAction) -> Unit): View = this.sref()

    @OptIn(ExperimentalComposeUiApi::class)
    fun accessibilityHidden(hidden: Boolean): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            it.value.modifier = it.value.modifier.semantics { ->
                if (hidden) {
                    invisibleToUser()
                }
            }
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityDirectTouch(isDirectTouchArea: Boolean = true, options: AccessibilityDirectTouchOptions = AccessibilityDirectTouchOptions.of()): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRotorEntry(id: AnyHashable, in_: Namespace.ID): View {
        val namespace = in_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityChartDescriptor(representable: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <Value> accessibilityFocused(binding: Any, equals: Value): View {
        val value = equals
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityFocused(condition: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRespondsToUserInteraction(respondsToUserInteraction: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityActivationPoint(activationPoint: CGPoint): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityActivationPoint(activationPoint: UnitPoint): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilitySortPriority(sortPriority: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> accessibilityShowsLargeContentViewer(largeContentView: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityShowsLargeContentViewer(): View = this.sref()

    fun accessibilityAddTraits(traits: AccessibilityTraits): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            if (traits.contains(AccessibilityTraits.isButton)) {
                it.value.modifier = it.value.modifier.semantics { -> role = androidx.compose.ui.semantics.Role.Button.sref() }
            }
            if (traits.contains(AccessibilityTraits.isHeader)) {
                it.value.modifier = it.value.modifier.semantics { -> heading() }
            }
            if (traits.contains(AccessibilityTraits.isSelected)) {
                it.value.modifier = it.value.modifier.semantics { -> selected = true }
            }
            if (traits.contains(AccessibilityTraits.isImage)) {
                it.value.modifier = it.value.modifier.semantics { -> role = androidx.compose.ui.semantics.Role.Image.sref() }
            }
            if (traits.contains(AccessibilityTraits.isModal)) {
                it.value.modifier = it.value.modifier.semantics { -> popup() }
            }
            if (traits.contains(AccessibilityTraits.isToggle)) {
                it.value.modifier = it.value.modifier.semantics { -> role = androidx.compose.ui.semantics.Role.Switch.sref() }
            }
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRemoveTraits(traits: AccessibilityTraits): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityLinkedGroup(id: AnyHashable, in_: Namespace.ID): View {
        val namespace = in_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityLabeledPair(role: AccessibilityLabeledPairRole, id: AnyHashable, in_: Namespace.ID): View {
        val namespace = in_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityRepresentation(representation: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityChildren(children: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityTextContentType(value: AccessibilityTextContentType): View = this.sref()

    fun accessibilityHeading(level: AccessibilityHeadingLevel): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            it.value.modifier = it.value.modifier.semantics { -> heading() }
            return@l ComposeResult.ok
        }
    }

    fun accessibilityValue(value: Text): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            val description = value.localizedTextString()
            it.value.modifier = it.value.modifier.semantics { -> stateDescription = description }
            return@l ComposeResult.ok
        }
    }

    fun accessibilityValue(value: String): View {
        return ComposeModifierView(targetView = this, role = ComposeModifierRole.accessibility) l@{ it ->
            it.value.modifier = it.value.modifier.semantics { -> stateDescription = value }
            return@l ComposeResult.ok
        }
    }

    fun accessibilityValue(key: LocalizedStringKey): View = accessibilityValue(Text(key))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityScrollAction(handler: (Edge) -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityElement(children: AccessibilityChildBehavior = AccessibilityChildBehavior.ignore): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun accessibilityAdjustableAction(handler: (AccessibilityAdjustmentDirection) -> Unit): View = this.sref()
    fun <V> gesture(gesture: Gesture<V>): View = GestureModifierView(view = this, gesture = gesture as Gesture<Any>)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> gesture(gesture: Gesture<V>, including: GestureMask): View {
        val mask = including
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> highPriorityGesture(gesture: Gesture<V>, including: GestureMask = GestureMask.all): View {
        val mask = including
        return this.sref()
    }

    fun onLongPressGesture(minimumDuration: Double = 0.5, maximumDistance: Double = Double(10.0), perform: () -> Unit): View {
        val action = perform
        val longPressGesture = LongPressGesture(minimumDuration = minimumDuration, maximumDistance = maximumDistance)
        return gesture(longPressGesture.onEnded({ _ -> action() }))
    }

    fun onLongPressGesture(minimumDuration: Double = 0.5, maximumDistance: Double = Double(10.0), perform: () -> Unit, onPressingChanged: (Boolean) -> Unit): View {
        val action = perform
        val longPressGesture = LongPressGesture(minimumDuration = minimumDuration, maximumDistance = maximumDistance)
        return gesture(longPressGesture.onChanged(onPressingChanged).onEnded({ _ -> action() }))
    }

    fun onTapGesture(count: Int = 1, perform: (CGPoint) -> Unit): View {
        val action = perform
        val tapGesture = TapGesture(count = count)
        var modified = tapGesture.modified.sref()
        modified.onEndedWithLocation = action
        return gesture(modified)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun onTapGesture(count: Int = 1, coordinateSpace: CoordinateSpaceProtocol, perform: (CGPoint) -> Unit): View {
        val action = perform
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> simultaneousGesture(gesture: Gesture<V>, including: GestureMask = GestureMask.all): View {
        val mask = including
        return this.sref()
    }
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun userActivity(activityType: String, isActive: Boolean = true, update: (Any) -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <P> userActivity(activityType: String, element: P?, update: (P, Any) -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun onContinueUserActivity(activityType: String, perform: (Any) -> Unit): View {
        val action = perform
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun onOpenURL(perform: (URL) -> Unit): View {
        val action = perform
        return this.sref()
    }
    fun labelStyle(style: LabelStyle): View {
        // We only support .automatic
        return this.sref()
    }

    fun labeledContentStyle(style: LabeledContentStyle): View = this.sref()
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun allowsTightening(flag: Boolean): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun baselineOffset(baselineOffset: Double): View = this.sref()

    fun bold(isActive: Boolean = true): View = fontWeight(if (isActive) Font.Weight.bold else null)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dynamicTypeSize(size: DynamicTypeSize): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dynamicTypeSize(range: IntRange): View = this.sref()

    fun font(font: Font?): View {
        return environment({ it -> EnvironmentValues.shared.setfont(it) }, font)
    }

    fun fontDesign(design: Font.Design?): View {
        return environment({ it -> EnvironmentValues.shared.set_fontDesign(it) }, design)
    }

    fun fontWeight(weight: Font.Weight?): View {
        return environment({ it -> EnvironmentValues.shared.set_fontWeight(it) }, weight)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fontWidth(width: Font.Width?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun invalidatableContent(invalidatable: Boolean = true): View = this.sref()

    fun italic(isActive: Boolean = true): View {
        return environment({ it -> EnvironmentValues.shared.set_isItalic(it) }, isActive)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun kerning(kerning: Double): View = this.sref()

    fun lineLimit(number: Int?): View {
        return environment({ it -> EnvironmentValues.shared.setlineLimit(it) }, number)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun lineLimit(limit: IntRange): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun lineLimit(limit: Int, reservesSpace: Boolean): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun lineSpacing(lineSpacing: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun monospacedDigit(): View = this.sref()

    fun monospaced(isActive: Boolean = true): View = fontDesign(if (isActive) Font.Design.monospaced else null)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun minimumScaleFactor(factor: Double): View = this.sref()

    fun multilineTextAlignment(alignment: TextAlignment): View {
        return environment({ it -> EnvironmentValues.shared.setmultilineTextAlignment(it) }, alignment)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun privacySensitive(sensitive: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun redacted(reason: RedactionReasons): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun speechAlwaysIncludesPunctuation(value: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun speechSpellsOutCharacters(value: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun speechAdjustedPitch(value: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun speechAnnouncementsQueued(value: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun strikethrough(isActive: Boolean = true, pattern: Text.LineStyle.Pattern = Text.LineStyle.Pattern.solid, color: Color? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun textCase(textCase: Text.Case?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun textScale(scale: Text.Scale, isEnabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun textSelection(selectability: TextSelectability): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun tracking(tracking: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun truncationMode(mode: Text.TruncationMode): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun underline(isActive: Boolean = true, pattern: Text.LineStyle.Pattern = Text.LineStyle.Pattern.solid, color: Color? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun unredacted(): View = this.sref()
    fun textEditorStyle(style: TextEditorStyle): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun findNavigator(isPresented: Binding<Boolean>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun findDisabled(isDisabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun replaceDisabled(isDisabled: Boolean = true): View = this.sref()
    fun autocorrectionDisabled(disable: Boolean = true): View {
        return keyboardOptionsModifierView l@{ options -> return@l if (options == null) KeyboardOptions(autoCorrect = !disable) else options.copy(autoCorrect = !disable) }
    }

    fun keyboardType(type: UIKeyboardType): View {
        val keyboardType = type.asComposeKeyboardType()
        return keyboardOptionsModifierView l@{ options -> return@l if (options == null) KeyboardOptions(keyboardType = keyboardType) else options.copy(keyboardType = keyboardType) }
    }

    fun onSubmit(of: SubmitTriggers = SubmitTriggers.text, action: () -> Unit): View {
        val triggers = of
        return ComposeModifierView(contentView = this) { view, context ->
            val state = EnvironmentValues.shared._onSubmitState
            val updatedState = if (state == null) OnSubmitState(triggers = triggers, action = action) else state!!.appending(triggers = triggers, action = action)
            EnvironmentValues.shared.setValues({ it -> it.set_onSubmitState(updatedState) }, in_ = { -> view.Compose(context = context) })
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun submitScope(isBlocking: Boolean = true): View = this.sref()

    fun submitLabel(submitLabel: SubmitLabel): View {
        val imeAction = submitLabel.asImeAction()
        return keyboardOptionsModifierView l@{ options -> return@l if (options == null) KeyboardOptions(imeAction = imeAction) else options.copy(imeAction = imeAction) }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun textContentType(textContentType: UITextContentType?): View = this.sref()

    fun textFieldStyle(style: TextFieldStyle): View {
        return environment({ it -> EnvironmentValues.shared.set_textFieldStyle(it) }, style)
    }

    fun textInputAutocapitalization(autocapitalization: TextInputAutocapitalization?): View {
        val capitalization = (autocapitalization ?: TextInputAutocapitalization.sentences).asKeyboardCapitalization()
        return keyboardOptionsModifierView l@{ options -> return@l if (options == null) KeyboardOptions(capitalization = capitalization) else options.copy(capitalization = capitalization) }
    }

    /// Return a modifier view that updates the environment's keyboard options.
    fun keyboardOptionsModifierView(update: (KeyboardOptions?) -> KeyboardOptions): View {
        return ComposeModifierView(contentView = this) { view, context ->
            val options = EnvironmentValues.shared._keyboardOptions.sref()
            val updatedOptions = update(options)
            EnvironmentValues.shared.setValues({ it -> it.set_keyboardOptions(updatedOptions) }, in_ = { -> view.Compose(context = context) })
        }
    }
    fun <P, Output> onReceive(publisher: P, perform: (Output) -> Unit): View where P: Publisher<Output, *> {
        val action = perform
        return ComposeModifierView(targetView = this) l@{ _ ->
            val latestAction = rememberUpdatedState(action)
            val subscription = remember { ->
                publisher.sink { output -> latestAction.value(output) }
            }
            DisposableEffect(subscription) { ->
                onDispose { -> subscription.cancel() }
            }
            return@l ComposeResult.ok
        }
    }
    /// Compose this view without an existing context - typically called when integrating a SwiftUI view tree into pure Compose.
    @Composable
    fun Compose(): ComposeResult = Compose(context = ComposeContext())

    /// Calls to `Compose` are added by the transpiler.
    @Composable
    fun Compose(context: ComposeContext): ComposeResult {
        val matchtarget_0 = context.composer
        if (matchtarget_0 != null) {
            val composer = matchtarget_0
            val composerContext: (Boolean) -> ComposeContext = l@{ retain ->
                if (retain) {
                    return@l context
                }
                var context = context.sref()
                context.composer = null
                return@l context
            }
            val matchtarget_1 = composer as? RenderingComposer
            if (matchtarget_1 != null) {
                val renderingComposer = matchtarget_1
                renderingComposer.Compose(this, composerContext)
                return ComposeResult.ok
            } else {
                val matchtarget_2 = composer as? SideEffectComposer
                if (matchtarget_2 != null) {
                    val sideEffectComposer = matchtarget_2
                    return sideEffectComposer.Compose(this, composerContext)
                } else {
                    return ComposeResult.ok
                }
            }
        } else {
            ComposeContent(context = context)
            return ComposeResult.ok
        }
    }

    /// Compose this view's content.
    @Composable
    fun ComposeContent(context: ComposeContext) {
        StateTracking.pushBody()
        body().ComposeContent(context)
        StateTracking.popBody()
    }

    /// Whether this is an empty view.
    val isSwiftUIEmptyView: Boolean
        get() = this is EmptyView

    /// Whether this is a builtin SwiftUI view.
    val isSwiftUIModuleView: Boolean
        get() = javaClass.name.startsWith("skip.ui.")

    /// Strip modifier views.
    ///
    /// - Parameter until: Return `true` to stop stripping at a modifier with a given role.
    fun <R> strippingModifiers(until: (ComposeModifierRole) -> Boolean = { _ -> false }, perform: (View?) -> R): R = perform(this)
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun allowsHitTesting(enabled: Boolean): View = this.sref()

    fun aspectRatio(ratio: Double? = null, contentMode: ContentMode): View {
        return environment({ it -> EnvironmentValues.shared.set_aspectRatio(it) }, Tuple2(ratio, contentMode))
    }

    fun aspectRatio(size: CGSize, contentMode: ContentMode): View = aspectRatio(size.width / size.height, contentMode = contentMode)

    fun background(background: View, alignment: Alignment = Alignment.center): View {
        return ComposeModifierView(contentView = this) { view, context -> BackgroundLayout(view = view, context = context, background = background, alignment = alignment) }
    }

    fun background(alignment: Alignment = Alignment.center, content: () -> View): View = background(content(), alignment = alignment)

    fun background(ignoresSafeAreaEdges: Edge.Set = Edge.Set.all): View {
        val edges = ignoresSafeAreaEdges
        return this.background(BackgroundStyle.shared, ignoresSafeAreaEdges = edges)
    }

    fun background(style: ShapeStyle, ignoresSafeAreaEdges: Edge.Set = Edge.Set.all): View {
        val edges = ignoresSafeAreaEdges
        return ComposeModifierView(targetView = this) l@{ it ->
            val matchtarget_3 = style.asColor(opacity = 1.0, animationContext = it.value)
            if (matchtarget_3 != null) {
                val color = matchtarget_3
                it.value.modifier = it.value.modifier.background(color)
            } else {
                style.asBrush(opacity = 1.0, animationContext = it.value)?.let { brush ->
                    it.value.modifier = it.value.modifier.background(brush)
                }
            }
            return@l ComposeResult.ok
        }
    }

    fun background(in_: Shape, fillStyle: FillStyle = FillStyle()): View {
        val shape = in_
        return background(BackgroundStyle.shared, in_ = shape, fillStyle = fillStyle)
    }

    fun background(style: ShapeStyle, in_: Shape, fillStyle: FillStyle = FillStyle()): View {
        val shape = in_
        return background(content = { ->
            ComposeBuilder { composectx: ComposeContext ->
                shape.fill(style).Compose(composectx)
                ComposeResult.ok
            }
        })
    }

    fun backgroundStyle(style: ShapeStyle): View {
        return environment({ it -> EnvironmentValues.shared.setbackgroundStyle(it) }, style)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun badge(count: Int): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun badge(label: Text?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun badge(key: LocalizedStringKey): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun badge(label: String): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun badgeProminence(prominence: BadgeProminence): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun blendMode(blendMode: BlendMode): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun blur(radius: Double, opaque: Boolean = false): View = this.sref()

    fun border(style: ShapeStyle, width: Double = 1.0): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            val matchtarget_4 = style.asColor(opacity = 1.0, animationContext = it.value)
            if (matchtarget_4 != null) {
                val color = matchtarget_4
                it.value.modifier = it.value.modifier.border(width = width.dp, color = color)
            } else {
                style.asBrush(opacity = 1.0, animationContext = it.value)?.let { brush ->
                    it.value.modifier = it.value.modifier.border(BorderStroke(width = width.dp, brush = brush))
                }
            }
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun brightness(amount: Double): View = this.sref()

    fun clipShape(shape: Shape, style: FillStyle = FillStyle()): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            it.value.modifier = it.value.modifier.clip(shape.asComposeShape(density = LocalDensity.current))
            return@l ComposeResult.ok
        }
    }

    fun clipped(antialiased: Boolean = false): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            it.value.modifier = it.value.modifier.clipToBounds()
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun colorInvert(): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun colorMultiply(color: Color): View = this.sref()

    fun compositingGroup(): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun containerBackground(style: ShapeStyle, for_: ContainerBackgroundPlacement): View {
        val container = for_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun containerBackground(for_: ContainerBackgroundPlacement, alignment: Alignment = Alignment.center, content: () -> View): View {
        val container = for_
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun containerRelativeFrame(axes: Axis.Set, alignment: Alignment = Alignment.center): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun containerRelativeFrame(axes: Axis.Set, count: Int, span: Int = 1, spacing: Double, alignment: Alignment = Alignment.center): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun containerRelativeFrame(axes: Axis.Set, alignment: Alignment = Alignment.center, length: (Double, Axis) -> Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> containerShape(shape: Shape): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contentShape(shape: Shape, eoFill: Boolean = false): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contentShape(kind: ContentShapeKinds, shape: Shape, eoFill: Boolean = false): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contextMenu(menuItems: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contextMenu(menuItems: () -> View, preview: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <I> contextMenu(forSelectionType: KClass<*>? = null, menu: (Set<I>) -> View, primaryAction: ((Set<I>) -> Unit)? = null): View {
        val itemType = forSelectionType
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun contrast(amount: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun controlSize(controlSize: ControlSize): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun coordinateSpace(name: NamedCoordinateSpace): View = this.sref()

    fun cornerRadius(radius: Double, antialiased: Boolean = true): View = clipShape(RoundedRectangle(cornerRadius = radius))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defaultHoverEffect(effect: HoverEffect?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defersSystemGestures(on: Edge.Set): View {
        val edges = on
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dialogSuppressionToggle(titleKey: LocalizedStringKey, isSuppressed: Binding<Boolean>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dialogSuppression(title: String, isSuppressed: Binding<Boolean>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dialogSuppressionToggle(label: Text, isSuppressed: Binding<Boolean>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dialogSuppressionToggle(isSuppressed: Binding<Boolean>): View = this.sref()

    fun disabled(disabled: Boolean): View {
        return environment({ it -> EnvironmentValues.shared.setisEnabled(it) }, !disabled)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun drawingGroup(opaque: Boolean = false, colorMode: ColorRenderingMode = ColorRenderingMode.nonLinear): View = this.sref()

    fun equatable(): View = EquatableView(content = this)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fileMover(isPresented: Binding<Boolean>, file: URL?, onCompletion: (Result<URL, Error>) -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fileMover(isPresented: Binding<Boolean>, files: Collection<URL>, onCompletion: (Result<Array<URL>, Error>) -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fileMover(isPresented: Binding<Boolean>, file: URL?, onCompletion: (Result<URL, Error>) -> Unit, onCancellation: () -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fileMover(isPresented: Binding<Boolean>, files: Collection<URL>, onCompletion: (Result<Array<URL>, Error>) -> Unit, onCancellation: () -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fixedSize(horizontal: Boolean, vertical: Boolean): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun fixedSize(): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun flipsForRightToLeftLayoutDirection(enabled: Boolean): View = this.sref()

    fun foregroundColor(color: Color?): View {
        return environment({ it -> EnvironmentValues.shared.set_foregroundStyle(it) }, color)
    }

    fun foregroundStyle(style: ShapeStyle): View {
        return environment({ it -> EnvironmentValues.shared.set_foregroundStyle(it) }, style)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun foregroundStyle(primary: ShapeStyle, secondary: ShapeStyle): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun foregroundStyle(primary: ShapeStyle, secondary: ShapeStyle, tertiary: ShapeStyle): View = this.sref()

    fun frame(width: Double? = null, height: Double? = null, alignment: Alignment = Alignment.center): View {
        return ComposeModifierView(contentView = this) { view, context ->
            val animatable = Tuple2(Float(width ?: 0.0), Float(height ?: 0.0)).asAnimatable(context = context)
            FrameLayout(view = view, context = context, width = if (width == null) null else Double(animatable.value.element0), height = if (height == null) null else Double(animatable.value.element1), alignment = alignment)
        }
    }

    fun frame(minWidth: Double? = null, idealWidth: Double? = null, maxWidth: Double? = null, minHeight: Double? = null, idealHeight: Double? = null, maxHeight: Double? = null, alignment: Alignment = Alignment.center): View {
        return ComposeModifierView(contentView = this) { view, context -> FrameLayout(view = view, context = context, minWidth = minWidth, idealWidth = idealWidth, maxWidth = maxWidth, minHeight = minHeight, idealHeight = idealHeight, maxHeight = maxHeight, alignment = alignment) }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun grayscale(amount: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun handlesExternalEvents(preferring: Set<String>, allowing: Set<String>): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun headerProminence(prominence: Prominence): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun help(textKey: LocalizedStringKey): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun help(text: Text): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun help(text: String): View = this.sref()

    fun hidden(): View = opacity(0.0)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun hoverEffect(effect: HoverEffect = HoverEffect.automatic, isEnabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun hoverEffectDisabled(disabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun hueRotation(angle: Angle): View {
        // NOTE: animatable property
        return this.sref()
    }

    fun id(id: Any): View = TagModifierView(view = this, value = id, role = ComposeModifierRole.id)

    fun ignoresSafeArea(regions: SafeAreaRegions = SafeAreaRegions.all, edges: Edge.Set = Edge.Set.all): View {
        return ComposeModifierView(contentView = this) { view, context -> IgnoresSafeAreaLayout(view = view, edges = edges, context = context) }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun inspector(isPresented: Binding<Boolean>, content: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun inspectorColumnWidth(min: Double? = null, ideal: Double, max: Double? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun inspectorColumnWidth(width: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun interactionActivityTrackingTag(tag: String): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun interactiveDismissDisabled(isDisabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun keyboardShortcut(key: KeyEquivalent, modifiers: EventModifiers = EventModifiers.command): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun keyboardShortcut(shortcut: KeyboardShortcut?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun keyboardShortcut(key: KeyEquivalent, modifiers: EventModifiers = EventModifiers.command, localization: KeyboardShortcut.Localization): View = this.sref()

    fun labelsHidden(): View {
        return environment({ it -> EnvironmentValues.shared.set_labelsHidden(it) }, true)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun layoutDirectionBehavior(behavior: LayoutDirectionBehavior): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun layoutPriority(value: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun luminanceToAlpha(): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun mask(alignment: Alignment = Alignment.center, mask: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <ID> matchedGeometryEffect(id: Hashable, in_: Any, properties: MatchedGeometryProperties = MatchedGeometryProperties.frame, anchor: UnitPoint = UnitPoint.center, isSource: Boolean = true): View {
        val namespace = in_
        return this.sref()
    }

    fun offset(offset: CGSize): View = this.offset(x = offset.width, y = offset.height)

    fun offset(x: Double = 0.0, y: Double = 0.0): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            val density = LocalDensity.current.sref()
            val animatable = Tuple2(Float(x), Float(y)).asAnimatable(context = it.value)
            val offsetPx = with(density) { -> IntOffset(animatable.value.element0.dp.roundToPx(), animatable.value.element1.dp.roundToPx()) }
            it.value.modifier = it.value.modifier.offset { -> offsetPx }
            return@l ComposeResult.ok
        }
    }

    fun onAppear(perform: (() -> Unit)? = null): View {
        val action = perform
        return ComposeModifierView(targetView = this) l@{ _ ->
            val hasAppeared = remember { -> mutableStateOf(false) }
            if (!hasAppeared.value) {
                hasAppeared.value = true
                SideEffect { -> action?.invoke() }
            }
            return@l ComposeResult.ok
        }
    }

    fun <V> onChange(of: V, perform: (V) -> Unit): View {
        val value = of
        val action = perform
        return ComposeModifierView(targetView = this) l@{ context ->
            val rememberedValue = rememberSaveable(stateSaver = context.value.stateSaver as Saver<V, Any>) { -> mutableStateOf(value) }
            if (rememberedValue.value != value) {
                rememberedValue.value = value
                SideEffect { -> action(value) }
            }
            return@l ComposeResult.ok
        }
    }

    fun <V> onChange(of: V, initial: Boolean = false, action: (V, V) -> Unit): View {
        val value = of
        return ComposeModifierView(targetView = this) l@{ context ->
            val rememberedValue = rememberSaveable(stateSaver = context.value.stateSaver as Saver<V, Any>) { -> mutableStateOf(value) }
            val rememberedInitial = rememberSaveable(stateSaver = context.value.stateSaver as Saver<Boolean, Any>) { -> mutableStateOf(true) }

            val isInitial = rememberedInitial.value.sref()
            rememberedInitial.value = false

            val oldValue = rememberedValue.value.sref()
            val isUpdate = oldValue != value
            if (isUpdate) {
                rememberedValue.value = value
            }

            if ((initial && isInitial) || isUpdate) {
                SideEffect { -> action(oldValue, value) }
            }
            return@l ComposeResult.ok
        }
    }

    fun <V> onChange(of: V?, initial: Boolean = false, action: () -> Unit): View {
        val value = of
        return ComposeModifierView(targetView = this) l@{ context ->
            val rememberedValue = rememberSaveable(stateSaver = context.value.stateSaver as Saver<V?, Any>) { -> mutableStateOf(value) }
            val rememberedInitial = rememberSaveable(stateSaver = context.value.stateSaver as Saver<Boolean, Any>) { -> mutableStateOf(true) }

            val isInitial = rememberedInitial.value.sref()
            rememberedInitial.value = false

            val oldValue = rememberedValue.value.sref()
            val isUpdate = oldValue != value
            if (isUpdate) {
                rememberedValue.value = value
            }

            if ((initial && isInitial) || isUpdate) {
                SideEffect { -> action() }
            }
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun onContinuousHover(coordinateSpace: CoordinateSpaceProtocol = LocalCoordinateSpace.local, perform: (HoverPhase) -> Unit): View {
        val action = perform
        return this.sref()
    }

    fun onDisappear(perform: (() -> Unit)? = null): View {
        val action = perform
        return ComposeModifierView(targetView = this) l@{ _ ->
            val disposeAction = rememberUpdatedState(action)
            DisposableEffect(true) { ->
                onDispose { -> disposeAction.value?.invoke() }
            }
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun onHover(perform: (Boolean) -> Unit): View {
        val action = perform
        return this.sref()
    }

    fun opacity(opacity: Double): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            val animatable = Float(opacity).asAnimatable(context = it.value)
            it.value.modifier = it.value.modifier.graphicsLayer { -> alpha = animatable.value.sref() }
            return@l ComposeResult.ok
        }
    }

    fun overlay(alignment: Alignment = Alignment.center, content: () -> View): View {
        val overlay = content()
        return ComposeModifierView(contentView = this) { view, context -> OverlayLayout(view = view, context = context, overlay = overlay, alignment = alignment) }
    }

    fun overlay(style: ShapeStyle, ignoresSafeAreaEdges: Edge.Set = Edge.Set.all): View {
        val edges = ignoresSafeAreaEdges
        return overlay(style, in_ = Rectangle())
    }

    fun overlay(style: ShapeStyle, in_: Shape, fillStyle: FillStyle = FillStyle()): View {
        val shape = in_
        return overlay(content = { ->
            ComposeBuilder { composectx: ComposeContext ->
                shape.fill(style).Compose(composectx)
                ComposeResult.ok
            }
        })
    }

    fun padding(insets: EdgeInsets): View {
        return ComposeModifierView(contentView = this, role = ComposeModifierRole.spacing) { view, context -> PaddingLayout(view = view, padding = insets, context = context) }
    }

    fun padding(edges: Edge.Set, length: Double? = null): View {
        var padding = EdgeInsets()
        if (edges.contains(Edge.Set.top)) {
            padding.top = length ?: 16.0
        }
        if (edges.contains(Edge.Set.bottom)) {
            padding.bottom = length ?: 16.0
        }
        if (edges.contains(Edge.Set.leading)) {
            padding.leading = length ?: 16.0
        }
        if (edges.contains(Edge.Set.trailing)) {
            padding.trailing = length ?: 16.0
        }
        return padding(padding)
    }

    fun padding(length: Double? = null): View = padding(Edge.Set.all, length)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun persistentSystemOverlays(visibility: Visibility): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun position(position: CGPoint): View {
        // NOTE: animatable property
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun position(x: Double = 0.0, y: Double = 0.0): View {
        // NOTE: animatable
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun projectionEffect(transform: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <Item> popover(item: Binding<Item?>, attachmentAnchor: Any? = null, arrowEdge: Edge = Edge.top, content: (Item) -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun popover(isPresented: Binding<Boolean>, attachmentAnchor: Any? = null, arrowEdge: Edge = Edge.top, content: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun refreshable(action: suspend () -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun renameAction(isFocused: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun renameAction(action: () -> Unit): View = this.sref()

    fun rotationEffect(angle: Angle): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            val animatable = Float(angle.degrees).asAnimatable(context = it.value)
            it.value.modifier = it.value.modifier.rotate(animatable.value)
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun rotationEffect(angle: Angle, anchor: UnitPoint): View {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun rotation3DEffect(angle: Angle, axis: Tuple3<Double, Double, Double>, anchor: UnitPoint = UnitPoint.center, anchorZ: Double = 0.0, perspective: Double = 1.0): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun safeAreaInset(edge: VerticalEdge, alignment: HorizontalAlignment = HorizontalAlignment.center, spacing: Double? = null, content: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun safeAreaInset(edge: HorizontalEdge, alignment: VerticalAlignment = VerticalAlignment.center, spacing: Double? = null, content: () -> View): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun safeAreaPadding(insets: EdgeInsets): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun safeAreaPadding(edges: Edge.Set = Edge.Set.all, length: Double? = null): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun safeAreaPadding(length: Double): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun saturation(amount: Double): View = this.sref()

    fun scaledToFit(): View = aspectRatio(null, contentMode = ContentMode.fit)

    fun scaledToFill(): View = aspectRatio(null, contentMode = ContentMode.fill)

    fun scaleEffect(scale: CGSize): View = scaleEffect(x = scale.width, y = scale.height)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scaleEffect(scale: CGSize, anchor: UnitPoint): View = scaleEffect(x = scale.width, y = scale.height)

    fun scaleEffect(s: Double): View = scaleEffect(x = s, y = s)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scaleEffect(s: Double, anchor: UnitPoint): View = scaleEffect(x = s, y = s)

    fun scaleEffect(x: Double = 1.0, y: Double = 1.0): View {
        return ComposeModifierView(targetView = this) l@{ it ->
            val animatable = Tuple2(Float(x), Float(y)).asAnimatable(context = it.value)
            it.value.modifier = it.value.modifier.scale(scaleX = animatable.value.element0, scaleY = animatable.value.element1)
            return@l ComposeResult.ok
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun scaleEffect(x: Double = 1.0, y: Double = 1.0, anchor: UnitPoint): View = scaleEffect(x = x, y = y)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun selectionDisabled(isDisabled: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun sensoryFeedback(feedback: SensoryFeedback, trigger: Equatable): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> sensoryFeedback(feedback: SensoryFeedback, trigger: T, condition: (T, T) -> Boolean): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <T> sensoryFeedback(trigger: T, feedback: (T, T) -> SensoryFeedback?): View = this.sref()

    fun shadow(color: Color = Color(white = 0.0, opacity = 0.33), radius: Double, x: Double = 0.0, y: Double = 0.0): View {
        return ComposeModifierView(contentView = this) { view, context ->
            // See Shadowed.kt
            Shadowed(context = context, color = color.colorImpl(), offsetX = x.dp, offsetY = y.dp, blurRadius = radius.dp) { context -> view.Compose(context = context) }
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun statusBarHidden(hidden: Boolean = true): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun symbolEffectsRemoved(isEnabled: Boolean = true): View = this.sref()

    fun tag(tag: Any): View = TagModifierView(view = this, value = tag, role = ComposeModifierRole.tag)

    fun task(priority: TaskPriority = TaskPriority.userInitiated, action: suspend () -> Unit): View = task(id = 0, priority = priority, action)

    fun task(id: Any, priority: TaskPriority = TaskPriority.userInitiated, action: suspend () -> Unit): View {
        val value = id
        return ComposeModifierView(targetView = this) l@{ _ ->
            val handler = rememberUpdatedState(action)
            LaunchedEffect(value) { -> handler.value() }
            return@l ComposeResult.ok
        }
    }

    fun tint(color: Color?): View {
        return environment({ it -> EnvironmentValues.shared.set_tint(it) }, color)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun tint(tint: ShapeStyle?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun transformEffect(transform: Any): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> transformEnvironment(keyPath: Any, transform: (InOut<V>) -> Unit): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun typeSelectEquivalent(text: Text?): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun typeSelectEquivalent(stringKey: LocalizedStringKey): View = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun typeSelectEquivalent(string: String): View = this.sref()

    fun zIndex(value: Double): View = ZIndexModifierView(targetView = this, zIndex = value)
}

// Use inline final func to get reified generic type
inline fun <reified T> View.environment(object_: T?): View = environmentObject(type = T::class, object_ = object_)


/// Used to mark views with a tag or ID.
internal class TagModifierView: ComposeModifierView {
    internal val value: Any

    internal constructor(view: View, value: Any, role: ComposeModifierRole): super(view = view, role = role) {
        this.value = value.sref()
    }

    companion object {

        /// Extract the existing tag modifier view from the given view's modifiers.
        internal fun strip(from: View, role: ComposeModifierRole): TagModifierView? {
            val view = from
            return view.strippingModifiers(until = { it -> it == role }, perform = { it -> it as? TagModifierView })
        }
    }
}

/// Use a special modifier for `zIndex` so that the artificial parent container created by `.frame` can
/// pull the `zIndex` value into its own modifiers.
///
/// Otherwise the extra frame container hides the `zIndex` value from this view's logical parent container.
///
/// - Seealso: `FrameLayout`
internal class ZIndexModifierView: ComposeModifierView {
    private var zIndex: Double

    internal constructor(targetView: View, zIndex: Double): super(targetView = targetView, role = ComposeModifierRole.zIndex, action = l@{ it ->
        if (zIndex != 0.0) {
            it.value.modifier = it.value.modifier.zIndex(Float(zIndex))
        }
        return@l ComposeResult.ok
    }) {
        this.zIndex = zIndex
    }

    /// Move the application of the `zIndex` to the given modifier, erasing it from this view.
    internal fun consume(with: Modifier): Modifier {
        val modifier = with
        if (zIndex == 0.0) {
            return modifier
        }
        val zIndexModifier = modifier.zIndex(Float(zIndex))
        zIndex = 0.0
        return zIndexModifier
    }
}

