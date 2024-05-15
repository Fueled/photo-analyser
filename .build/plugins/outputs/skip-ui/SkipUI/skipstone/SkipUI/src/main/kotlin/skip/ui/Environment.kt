// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import kotlin.reflect.KClass
import skip.lib.*
import skip.lib.Set

import skip.foundation.*

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.reflect.full.companionObjectInstance

interface EnvironmentKey<Value> {
}

/// Added to `EnvironmentKey` companion objects.
interface EnvironmentKeyCompanion<Value> {
    val defaultValue: Value
}

// Model as a class because our implementation only holds the global environment keys, and so does not need to copy.
// Each key handles its own scoping of values using Android's `CompositionLocal` system
class EnvironmentValues {

    // We type erase all keys and values. The alternative would be to reify these functions.
    internal val compositionLocals: MutableMap<Any, ProvidableCompositionLocal<Any>> = mutableMapOf()
    internal val lastSetValues: MutableMap<ProvidableCompositionLocal<Any>, Any> = mutableMapOf()
    internal val lastSetActions: MutableList<@Composable () -> Unit> = mutableListOf()

    /// Retrieve an environment value by its `EnvironmentKey`.
    @Composable operator fun <Key, Value> get(key: KClass<Key>): Value where Key: EnvironmentKey<Value> {
        val compositionLocal = valueCompositionLocal(key = key)
        return (compositionLocal.current as Value).sref()
    }

    /// Retrieve an environment object by type.
    @Composable fun <ObjectType> environmentObject(type: KClass<ObjectType>): ObjectType? where ObjectType: Any {
        val compositionLocal = objectCompositionLocal(type = type)
        val value = compositionLocal.current.sref()
        return (if (value == Unit) null else value as ObjectType).sref()
    }

    /// Set environment values.
    ///
    /// - Seealso: ``View/environment(_:)``
    /// - Warning: Setting environment values should only be done within the `execute` block of this function.
    @Composable
    internal fun setValues(execute: @Composable (EnvironmentValues) -> Unit, in_: @Composable () -> Unit) {
        val content = in_
        // Set the values in EnvironmentValues to keep any user-defined setter logic in place, then retrieve and clear the last set values
        execute(this)
        for (action in lastSetActions.sref()) {
            action()
        }
        lastSetActions.clear()
        val provided = lastSetValues.map { entry ->
            val element = entry.key provides entry.value
            element
        }.toTypedArray()
        lastSetValues.clear()
        CompositionLocalProvider(*provided) { -> content() }
    }

    // On set we populate our `lastSetValues` map, which our `setValues` function reads from and then clears after
    // packaging the values for sending to downstream Composables. This should be safe to do even on this effectively
    // global object because it should only be occurring sequentially on the main thread.

    operator fun <Key, Value> set(key: KClass<Key>, value: Value) where Key: EnvironmentKey<Value>, Value: Any {
        val compositionLocal = valueCompositionLocal(key = key)
        lastSetValues[compositionLocal] = value.sref()
    }

    /// The Compose `CompositionLocal` for the given environment value key type.
    fun valueCompositionLocal(key: KClass<*>): ProvidableCompositionLocal<Any> {
        val defaultValue = { (key.companionObjectInstance as EnvironmentKeyCompanion<*>).defaultValue }
        return compositionLocal(key = key, defaultValue = defaultValue)
    }

    /// The Compose `CompositionLocal` for the given environment object type.
    fun objectCompositionLocal(type: KClass<*>): ProvidableCompositionLocal<Any> {
        return compositionLocal(key = type, defaultValue = { -> null })
    }

    internal fun compositionLocal(key: AnyHashable, defaultValue: () -> Any?): ProvidableCompositionLocal<Any> {
        compositionLocals[key].sref()?.let { value ->
            return value.sref()
        }
        val value = compositionLocalOf { -> defaultValue() ?: Unit }
        compositionLocals[key] = value.sref()
        return value.sref()
    }

    @Composable
    private fun builtinValue(key: AnyHashable, defaultValue: () -> Any?): Any? {
        val compositionLocal = compositionLocal(key = key, defaultValue = defaultValue)
        val current = compositionLocal.current.sref()
        return (if (current == Unit) null else current).sref()
    }

    private fun setBuiltinValue(key: AnyHashable, value: Any?, defaultValue: () -> Any?) {
        val compositionLocal = compositionLocal(key = key, defaultValue = defaultValue)
        lastSetValues[compositionLocal] = (value ?: Unit).sref()
    }

    // MARK: - Public values

    open val autocorrectionDisabled: Boolean
        @Composable
        get() {
            return _keyboardOptions?.autoCorrect == false
        }

    open val backgroundStyle: ShapeStyle?
        @Composable
        get() {
            return (builtinValue(key = "backgroundStyle", defaultValue = { -> null }) as ShapeStyle?).sref()
        }
    fun setbackgroundStyle(newValue: ShapeStyle?) {
        setBuiltinValue(key = "backgroundStyle", value = if (newValue is BackgroundStyle) null else newValue, defaultValue = { -> null })
    }

    open val colorScheme: ColorScheme
        @Composable
        get() = ColorScheme.fromMaterialTheme()
    fun setcolorScheme(newValue: ColorScheme) {
        // Implemented as a special case in .colorScheme and .preferredColorScheme, because Compose forces us to go through MaterialTheme
        // rather than exposing its LocalColorScheme.current provider
    }

    open val dismiss: DismissAction
        @Composable
        get() {
            return builtinValue(key = "dismiss", defaultValue = { -> DismissAction.default }) as DismissAction
        }
    fun setdismiss(newValue: DismissAction) {
        setBuiltinValue(key = "dismiss", value = newValue, defaultValue = { -> DismissAction.default })
    }

    open val font: Font?
        @Composable
        get() {
            return builtinValue(key = "font", defaultValue = { -> null }) as Font?
        }
    fun setfont(newValue: Font?) {
        setBuiltinValue(key = "font", value = newValue, defaultValue = { -> null })
    }

    open val isEnabled: Boolean
        @Composable
        get() {
            return builtinValue(key = "isEnabled", defaultValue = { -> true }) as Boolean
        }
    fun setisEnabled(newValue: Boolean) {
        setBuiltinValue(key = "isEnabled", value = newValue, defaultValue = { -> true })
    }

    open val isSearching: Boolean
        @Composable
        get() {
            return _searchableState?.isSearching?.value == true
        }

    open val layoutDirection: LayoutDirection
        @Composable
        get() = if (LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl) LayoutDirection.rightToLeft else LayoutDirection.leftToRight
    fun setlayoutDirection(newValue: LayoutDirection) {
        lastSetValues[LocalLayoutDirection as ProvidableCompositionLocal<Any>] = (if (newValue == LayoutDirection.rightToLeft) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr).sref()
    }

    open val lineLimit: Int?
        @Composable
        get() {
            return builtinValue(key = "lineLimit", defaultValue = { -> null }) as Int?
        }
    fun setlineLimit(newValue: Int?) {
        setBuiltinValue(key = "lineLimit", value = newValue, defaultValue = { -> null })
    }

    open val locale: Locale
        @Composable
        get() = Locale(LocalConfiguration.current.locales[0])
    fun setlocale(newValue: Locale) {
        val action: @Composable () -> Unit = { ->
            // Requires a @Composable context to copy LocalConfiguration.current
            val configuration = Configuration(LocalConfiguration.current)
            configuration.setLocale(newValue.kotlin())
            lastSetValues[LocalConfiguration as ProvidableCompositionLocal<Any>] = configuration.sref()
        }
        lastSetActions.add(action)
    }

    open val multilineTextAlignment: TextAlignment
        @Composable
        get() {
            return builtinValue(key = "multilineTextAlignment", defaultValue = { -> TextAlignment.leading }) as TextAlignment
        }
    fun setmultilineTextAlignment(newValue: TextAlignment) {
        setBuiltinValue(key = "multilineTextAlignment", value = newValue, defaultValue = { -> TextAlignment.leading })
    }

    open val openURL: OpenURLAction
        @Composable
        get() {
            val uriHandler = LocalUriHandler.current.sref()
            val openURLAction = builtinValue(key = "openURL", defaultValue = { -> OpenURLAction.default }) as OpenURLAction
            return OpenURLAction(handler = openURLAction.handler, systemHandler = { it -> uriHandler.openUri(it.absoluteString) })
        }
    fun setopenURL(newValue: OpenURLAction) {
        setBuiltinValue(key = "openURL", value = newValue, defaultValue = { -> OpenURLAction.default })
    }

    open val redactionReasons: RedactionReasons
        @Composable
        get() {
            return (builtinValue(key = "redactionReasons", defaultValue = { -> RedactionReasons(rawValue = 0) }) as RedactionReasons).sref()
        }
    fun setredactionReasons(newValue: RedactionReasons) {
        setBuiltinValue(key = "redactionReasons", value = newValue, defaultValue = { -> RedactionReasons(rawValue = 0) })
    }

    open val timeZone: TimeZone
        @Composable
        get() {
            return (builtinValue(key = "timeZone", defaultValue = { -> TimeZone.current }) as TimeZone).sref()
        }
    fun settimeZone(newValue: TimeZone) {
        setBuiltinValue(key = "timeZone", value = newValue, defaultValue = { -> TimeZone.current })
    }

    /* Not yet supported
    var accessibilityDimFlashingLights: Bool
    var accessibilityDifferentiateWithoutColor: Bool
    var accessibilityEnabled: Bool
    var accessibilityInvertColors: Bool
    var accessibilityLargeContentViewerEnabled: Bool
    var accessibilityPlayAnimatedImages: Bool
    var accessibilityPrefersHeadAnchorAlternative: Bool
    var accessibilityQuickActionsEnabled: Bool
    var accessibilityReduceMotion: Bool
    var accessibilityReduceTransparency: Bool
    var accessibilityShowButtonShapes: Bool
    var accessibilitySwitchControlEnabled: Bool
    var accessibilityVoiceOverEnabled: Bool
    var legibilityWeight: LegibilityWeight?

    var dismissSearch: DismissSearchAction
    var dismissWindow: DismissWindowAction
    var openImmersiveSpace: OpenImmersiveSpaceAction
    var dismissImmersiveSpace: DismissImmersiveSpaceAction
    var newDocument: NewDocumentAction
    var openDocument: OpenDocumentAction
    var openWindow: OpenWindowAction
    var purchase: PurchaseAction
    var refresh: RefreshAction?
    var rename: RenameAction?
    var resetFocus: ResetFocusAction
    var authorizationController: AuthorizationController
    var webAuthenticationSession: WebAuthenticationSession

    var buttonRepeatBehavior: ButtonRepeatBehavior
    var controlSize: ControlSize
    var controlActiveState: ControlActiveState
    var defaultWheelPickerItemHeight: CGFloat
    var keyboardShortcut: KeyboardShortcut?
    var menuIndicatorVisibility: Visibility
    var menuOrder: MenuOrder
    var searchSuggestionsPlacement: SearchSuggestionsPlacement
    var colorSchemeContrast: ColorSchemeContrast
    var displayScale: CGFloat
    var horizontalSizeClass: UserInterfaceSizeClass?
    var imageScale: Image.Scale
    var pixelLength: CGFloat
    var sidebarRowSize: SidebarRowSize
    var verticalSizeClass: UserInterfaceSizeClass?
    var calendar: Calendar
    var documentConfiguration: DocumentConfiguration?
    var managedObjectContext: NSManagedObjectContext
    var modelContext: ModelContext
    var undoManager: UndoManager?

    var isScrollEnabled: Bool
    var horizontalScrollIndicatorVisibility: ScrollIndicatorVisibility
    var verticalScrollIndicatorVisibility: ScrollIndicatorVisibility
    var scrollDismissesKeyboardMode: ScrollDismissesKeyboardMode
    var horizontalScrollBounceBehavior: ScrollBounceBehavior
    var verticalScrollBounceBehavior: ScrollBounceBehavior

    var editMode: Binding<EditMode>?
    var isActivityFullscreen: Bool
    var isFocused: Bool
    var isHoverEffectEnabled: Bool
    var isLuminanceReduced: Bool
    var isPresented: Bool
    var isSceneCaptured: Bool
    var scenePhase: ScenePhase
    var supportsMultipleWindows: Bool

    var displayStoreKitMessage: DisplayMessageAction
    var requestReview: RequestReviewAction

    var allowsTightening: Bool
    var dynamicTypeSize: DynamicTypeSize
    var lineSpacing: CGFloat
    var minimumScaleFactor: CGFloat
    var textCase: Text.Case?
    var truncationMode: Text.TruncationMode

    var allowedDynamicRange: Image.DynamicRange?
    var backgroundMaterial: Material?
    var backgroundProminence: BackgroundProminence
    var badgeProminence: BadgeProminence
    var contentTransition: ContentTransition
    var contentTransitionAddsDrawingGroup: Bool
    var defaultMinListHeaderHeight: CGFloat?
    var defaultMinListRowHeight: CGFloat
    var isFocusEffectEnabled: Bool
    var headerProminence: Prominence
    var physicalMetrics: PhysicalMetricsConverter
    var springLoadingBehavior: SpringLoadingBehavior
    var symbolRenderingMode: SymbolRenderingMode?
    var symbolVariants: SymbolVariants

    var showsWidgetContainerBackground: Bool
    var showsWidgetLabel: Bool
    var widgetFamily: WidgetFamily
    var widgetRenderingMode: WidgetRenderingMode
    var widgetContentMargins: EdgeInsets
    */

    // MARK: - Internal values

    internal open val _animation: Animation?
        @Composable
        get() {
            return (builtinValue(key = "_animation", defaultValue = { -> null }) as Animation?).sref()
        }
    internal fun set_animation(newValue: Animation?) {
        setBuiltinValue(key = "_animation", value = newValue, defaultValue = { -> null })
    }

    internal open val _aspectRatio: Tuple2<Double?, ContentMode>?
        @Composable
        get() {
            return builtinValue(key = "_aspectRatio", defaultValue = { -> null }) as Tuple2<Double?, ContentMode>?
        }
    internal fun set_aspectRatio(newValue: Tuple2<Double?, ContentMode>?) {
        setBuiltinValue(key = "_aspectRatio", value = newValue, defaultValue = { -> null })
    }

    internal open val _buttonStyle: ButtonStyle?
        @Composable
        get() {
            return builtinValue(key = "_buttonStyle", defaultValue = { -> null }) as ButtonStyle?
        }
    internal fun set_buttonStyle(newValue: ButtonStyle?) {
        setBuiltinValue(key = "_buttonStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _fillHeight: (@Composable (Boolean) -> Modifier)?
        @Composable
        get() {
            return builtinValue(key = "_fillHeight", defaultValue = { -> null }) as (@Composable (Boolean) -> Modifier)?
        }
    internal fun set_fillHeight(newValue: (@Composable (Boolean) -> Modifier)?) {
        setBuiltinValue(key = "_fillHeight", value = newValue, defaultValue = { -> null })
    }

    internal open val _fillWidth: (@Composable (Boolean) -> Modifier)?
        @Composable
        get() {
            return builtinValue(key = "_fillWidth", defaultValue = { -> null }) as (@Composable (Boolean) -> Modifier)?
        }
    internal fun set_fillWidth(newValue: (@Composable (Boolean) -> Modifier)?) {
        setBuiltinValue(key = "_fillWidth", value = newValue, defaultValue = { -> null })
    }

    internal open val _fillHeightModifier: Modifier?
        @Composable
        get() {
            return builtinValue(key = "_fillHeightModifier", defaultValue = { -> null }) as Modifier?
        }
    internal fun set_fillHeightModifier(newValue: Modifier?) {
        setBuiltinValue(key = "_fillHeightModifier", value = newValue, defaultValue = { -> null })
    }

    internal open val _fillWidthModifier: Modifier?
        @Composable
        get() {
            return builtinValue(key = "_fillWidthModifier", defaultValue = { -> null }) as Modifier?
        }
    internal fun set_fillWidthModifier(newValue: Modifier?) {
        setBuiltinValue(key = "_fillWidthModifier", value = newValue, defaultValue = { -> null })
    }

    internal open val _fontDesign: Font.Design?
        @Composable
        get() {
            return builtinValue(key = "_fontDesign", defaultValue = { -> null }) as Font.Design?
        }
    internal fun set_fontDesign(newValue: Font.Design?) {
        setBuiltinValue(key = "_fontDesign", value = newValue, defaultValue = { -> null })
    }

    internal open val _fontWeight: Font.Weight?
        @Composable
        get() {
            return builtinValue(key = "_fontWeight", defaultValue = { -> null }) as Font.Weight?
        }
    internal fun set_fontWeight(newValue: Font.Weight?) {
        setBuiltinValue(key = "_fontWeight", value = newValue, defaultValue = { -> null })
    }

    internal open val _foregroundStyle: ShapeStyle?
        @Composable
        get() {
            return (builtinValue(key = "_foregroundStyle", defaultValue = { -> null }) as ShapeStyle?).sref()
        }
    internal fun set_foregroundStyle(newValue: ShapeStyle?) {
        setBuiltinValue(key = "_foregroundStyle", value = if (newValue is ForegroundStyle) null else newValue, defaultValue = { -> null })
    }

    internal open val _isEdgeToEdge: Boolean?
        @Composable
        get() {
            return builtinValue(key = "_isEdgeToEdge", defaultValue = { -> null }) as Boolean?
        }
    internal fun set_isEdgeToEdge(newValue: Boolean?) {
        setBuiltinValue(key = "_isEdgeToEdge", value = newValue, defaultValue = { -> null })
    }

    internal open val _isItalic: Boolean
        @Composable
        get() {
            return builtinValue(key = "_isItalic", defaultValue = { -> false }) as Boolean
        }
    internal fun set_isItalic(newValue: Boolean) {
        setBuiltinValue(key = "_isItalic", value = newValue, defaultValue = { -> false })
    }

    internal open val _keyboardOptions: KeyboardOptions?
        @Composable
        get() {
            return (builtinValue(key = "_keyboardOptions", defaultValue = { -> null }) as KeyboardOptions?).sref()
        }
    internal fun set_keyboardOptions(newValue: KeyboardOptions?) {
        setBuiltinValue(key = "_keyboardOptions", value = newValue, defaultValue = { -> null })
    }

    internal open val _labelsHidden: Boolean
        @Composable
        get() {
            return builtinValue(key = "_labelsHidden", defaultValue = { -> false }) as Boolean
        }
    internal fun set_labelsHidden(newValue: Boolean) {
        setBuiltinValue(key = "_labelsHidden", value = newValue, defaultValue = { -> false })
    }

    internal open val _layoutAxis: Axis?
        @Composable
        get() {
            return builtinValue(key = "_layoutAxis", defaultValue = { -> null }) as Axis?
        }
    internal fun set_layoutAxis(newValue: Axis?) {
        setBuiltinValue(key = "_layoutAxis", value = newValue, defaultValue = { -> null })
    }

    internal open val _listItemTint: Color?
        @Composable
        get() {
            return builtinValue(key = "_listItemTint", defaultValue = { -> null }) as Color?
        }
    internal fun set_listItemTint(newValue: Color?) {
        setBuiltinValue(key = "_listItemTint", value = newValue, defaultValue = { -> null })
    }

    internal open val _listSectionHeaderStyle: ListStyle?
        @Composable
        get() {
            return builtinValue(key = "_listSectionHeaderStyle", defaultValue = { -> null }) as ListStyle?
        }
    internal fun set_listSectionHeaderStyle(newValue: ListStyle?) {
        setBuiltinValue(key = "_listSectionHeaderStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _listSectionFooterStyle: ListStyle?
        @Composable
        get() {
            return builtinValue(key = "_listSectionFooterStyle", defaultValue = { -> null }) as ListStyle?
        }
    internal fun set_listSectionFooterStyle(newValue: ListStyle?) {
        setBuiltinValue(key = "_listSectionFooterStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _listStyle: ListStyle?
        @Composable
        get() {
            return builtinValue(key = "_listStyle", defaultValue = { -> null }) as ListStyle?
        }
    internal fun set_listStyle(newValue: ListStyle?) {
        setBuiltinValue(key = "_listStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _onSubmitState: OnSubmitState?
        @Composable
        get() {
            return builtinValue(key = "_onSubmitState", defaultValue = { -> null }) as OnSubmitState?
        }
    internal fun set_onSubmitState(newValue: OnSubmitState?) {
        setBuiltinValue(key = "_onSubmitState", value = newValue, defaultValue = { -> null })
    }

    internal open val _pickerStyle: PickerStyle?
        @Composable
        get() {
            return builtinValue(key = "_pickerStyle", defaultValue = { -> null }) as PickerStyle?
        }
    internal fun set_pickerStyle(newValue: PickerStyle?) {
        setBuiltinValue(key = "_pickerStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _placement: ViewPlacement
        @Composable
        get() {
            return (builtinValue(key = "_placement", defaultValue = { -> ViewPlacement(rawValue = 0) }) as ViewPlacement).sref()
        }
    internal fun set_placement(newValue: ViewPlacement) {
        setBuiltinValue(key = "_placement", value = newValue, defaultValue = { -> ViewPlacement(rawValue = 0) })
    }

    internal open val _progressViewStyle: ProgressViewStyle?
        @Composable
        get() {
            return builtinValue(key = "_progressViewStyle", defaultValue = { -> null }) as ProgressViewStyle?
        }
    internal fun set_progressViewStyle(newValue: ProgressViewStyle?) {
        setBuiltinValue(key = "_progressViewStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _safeArea: SafeArea?
        @Composable
        get() {
            return builtinValue(key = "_safeArea", defaultValue = { -> null }) as SafeArea?
        }
    internal fun set_safeArea(newValue: SafeArea?) {
        setBuiltinValue(key = "_safeArea", value = newValue, defaultValue = { -> null })
    }

    internal open val _scrollAxes: Axis.Set
        @Composable
        get() {
            return (builtinValue(key = "_scrollAxes", defaultValue = { -> Axis.Set(rawValue = 0) }) as Axis.Set).sref()
        }
    internal fun set_scrollAxes(newValue: Axis.Set) {
        setBuiltinValue(key = "_scrollAxes", value = newValue, defaultValue = { -> Axis.Set(rawValue = 0) })
    }

    internal open val _scrollContentBackground: Visibility?
        @Composable
        get() {
            return builtinValue(key = "_scrollContentBackground", defaultValue = { -> null }) as Visibility?
        }
    internal fun set_scrollContentBackground(newValue: Visibility?) {
        setBuiltinValue(key = "_scrollContentBackground", value = newValue, defaultValue = { -> null })
    }

    internal open val _searchableState: SearchableState?
        @Composable
        get() {
            return builtinValue(key = "_searchableState", defaultValue = { -> null }) as SearchableState?
        }
    internal fun set_searchableState(newValue: SearchableState?) {
        setBuiltinValue(key = "_searchableState", value = newValue, defaultValue = { -> null })
    }

    internal open val _sheetDepth: Int
        @Composable
        get() {
            return builtinValue(key = "_sheetDepth", defaultValue = { -> 0 }) as Int
        }
    internal fun set_sheetDepth(newValue: Int) {
        setBuiltinValue(key = "_sheetDepth", value = newValue, defaultValue = { -> 0 })
    }

    internal open val _textFieldStyle: TextFieldStyle?
        @Composable
        get() {
            return builtinValue(key = "_textFieldStyle", defaultValue = { -> null }) as TextFieldStyle?
        }
    internal fun set_textFieldStyle(newValue: TextFieldStyle?) {
        setBuiltinValue(key = "_textFieldStyle", value = newValue, defaultValue = { -> null })
    }

    internal open val _tint: Color?
        @Composable
        get() {
            return builtinValue(key = "_tint", defaultValue = { -> null }) as Color?
        }
    internal fun set_tint(newValue: Color?) {
        setBuiltinValue(key = "_tint", value = newValue, defaultValue = { -> null })
    }

    companion object {
        val shared = EnvironmentValues()
    }
}



