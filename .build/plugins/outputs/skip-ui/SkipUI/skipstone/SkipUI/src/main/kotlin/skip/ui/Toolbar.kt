// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.runtime.Composable

interface ToolbarContent: View {
}

interface CustomizableToolbarContent: ToolbarContent {

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defaultCustomization(defaultVisibility: Visibility = Visibility.automatic, options: ToolbarCustomizationOptions = ToolbarCustomizationOptions.of()): CustomizableToolbarContent = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun customizationBehavior(behavior: ToolbarCustomizationBehavior): CustomizableToolbarContent = this.sref()
}

// We base our toolbar content on `View` rather than a custom protocol so that we can reuse the
// `@ViewBuilder` logic built into the transpiler. The Swift compiler will guarantee that the
// only allowed toolbar content are types that conform to `ToolbarContent`

class ToolbarItem: CustomizableToolbarContent {
    internal val placement: ToolbarItemPlacement
    internal val content: ComposeBuilder

    constructor(placement: ToolbarItemPlacement = ToolbarItemPlacement.automatic, content: () -> View) {
        this.placement = placement
        this.content = ComposeBuilder.from(content)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(id: String, placement: ToolbarItemPlacement = ToolbarItemPlacement.automatic, content: () -> View) {
        this.placement = placement
        this.content = ComposeBuilder.from(content)
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        content.Compose(context = context)
    }

    companion object {
    }
}

class ToolbarItemGroup: CustomizableToolbarContent, View {
    internal val placement: ToolbarItemPlacement
    internal val content: ComposeBuilder

    constructor(placement: ToolbarItemPlacement = ToolbarItemPlacement.automatic, content: () -> View) {
        this.placement = placement
        this.content = ComposeBuilder.from(content)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(placement: ToolbarItemPlacement = ToolbarItemPlacement.automatic, content: () -> View, label: () -> View) {
        this.placement = placement
        this.content = ComposeBuilder.from(content)
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        content.Compose(context = context)
    }

    companion object {
    }
}

class ToolbarTitleMenu: CustomizableToolbarContent, View {
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor() {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(content: () -> View) {
    }


    companion object {
    }
}

enum class ToolbarCustomizationBehavior: Sendable {
    default,
    reorderable,
    disabled;

    companion object {
    }
}

enum class ToolbarItemPlacement {
    automatic,
    principal,
    navigation,
    primaryAction,
    secondaryAction,
    status,
    confirmationAction,
    cancellationAction,
    destructiveAction,
    keyboard,
    topBarLeading,
    topBarTrailing,
    bottomBar,
    navigationBarLeading,
    navigationBarTrailing;

    companion object {
    }
}

enum class ToolbarPlacement {
    automatic,
    bottomBar,
    navigationBar,
    tabBar;

    companion object {
    }
}

enum class ToolbarRole: Sendable {
    automatic,
    navigationStack,
    browser,
    editor;

    companion object {
    }
}

enum class ToolbarTitleDisplayMode {
    automatic,
    large,
    inlineLarge,
    inline;

    companion object {
    }
}

class ToolbarCustomizationOptions: OptionSet<ToolbarCustomizationOptions, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): ToolbarCustomizationOptions = ToolbarCustomizationOptions(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: ToolbarCustomizationOptions) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ToolbarCustomizationOptions
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ToolbarCustomizationOptions(this as MutableStruct)

    private fun assignfrom(target: ToolbarCustomizationOptions) {
        this.rawValue = target.rawValue
    }

    companion object {

        var alwaysAvailable = ToolbarCustomizationOptions(rawValue = 1 shl 0)
            get() = field.sref({ this.alwaysAvailable = it })
            set(newValue) {
                field = newValue.sref()
            }

        fun of(vararg options: ToolbarCustomizationOptions): ToolbarCustomizationOptions {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return ToolbarCustomizationOptions(rawValue = value)
        }
    }
}

internal class ToolbarPreferenceKey: PreferenceKey<ToolbarPreferences> {

    companion object: PreferenceKeyCompanion<ToolbarPreferences> {
        override val defaultValue = ToolbarPreferences()
        override fun reduce(value: InOut<ToolbarPreferences>, nextValue: () -> ToolbarPreferences) {
            value.value = value.value.reduce(nextValue())
        }
    }
}

internal class ToolbarPreferences {
    internal val content: Array<View>?
    internal val titleDisplayMode: ToolbarTitleDisplayMode?
    internal val titleMenu: View?
    internal val backButtonHidden: Boolean?
    internal val navigationBar: ToolbarBarPreferences?
    internal val bottomBar: ToolbarBarPreferences?

    internal constructor(content: Array<View>? = null, titleDisplayMode: ToolbarTitleDisplayMode? = null, titleMenu: View? = null, backButtonHidden: Boolean? = null, navigationBar: ToolbarBarPreferences? = null, bottomBar: ToolbarBarPreferences? = null) {
        this.content = content.sref()
        this.titleDisplayMode = titleDisplayMode
        this.titleMenu = titleMenu.sref()
        this.backButtonHidden = backButtonHidden
        this.navigationBar = navigationBar
        this.bottomBar = bottomBar
    }

    internal constructor(visibility: Visibility? = null, background: ShapeStyle? = null, backgroundVisibility: Visibility? = null, colorScheme: ColorScheme? = null, for_: Array<ToolbarPlacement>) {
        val bars = for_
        val barPreferences = ToolbarBarPreferences(visibility = visibility, background = background, backgroundVisibility = backgroundVisibility, colorScheme = colorScheme)
        var navigationBar: ToolbarBarPreferences? = null
        var bottomBar: ToolbarBarPreferences? = null
        for (bar in bars.sref()) {
            for (unusedi in 0..0) {
                when (bar) {
                    ToolbarPlacement.automatic, ToolbarPlacement.navigationBar -> navigationBar = barPreferences
                    ToolbarPlacement.bottomBar -> bottomBar = barPreferences
                    ToolbarPlacement.tabBar -> break
                }
            }
        }
        this.navigationBar = navigationBar
        this.bottomBar = bottomBar
        this.content = null
        this.titleDisplayMode = null
        this.titleMenu = null
        this.backButtonHidden = null
    }

    internal fun reduce(next: ToolbarPreferences): ToolbarPreferences {
        val rcontent: Array<View>?
        val matchtarget_0 = next.content
        if (matchtarget_0 != null) {
            val ncontent = matchtarget_0
            if (content != null) {
                rcontent = (content + ncontent).sref()
            } else {
                rcontent = (next.content ?: content).sref()
            }
        } else {
            rcontent = (next.content ?: content).sref()
        }
        return ToolbarPreferences(content = rcontent, titleDisplayMode = next.titleDisplayMode ?: titleDisplayMode, titleMenu = next.titleMenu ?: titleMenu, backButtonHidden = next.backButtonHidden ?: backButtonHidden, navigationBar = reduceBar(navigationBar, next.navigationBar), bottomBar = reduceBar(bottomBar, next.bottomBar))
    }

    private fun reduceBar(bar: ToolbarBarPreferences?, next: ToolbarBarPreferences?): ToolbarBarPreferences? {
        if ((bar != null) && (next != null)) {
            return bar.reduce(next)
        } else {
            return next ?: bar
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ToolbarPreferences) {
            return false
        }
        val lhs = this
        val rhs = other
        if ((lhs.titleDisplayMode != rhs.titleDisplayMode) || (lhs.backButtonHidden != rhs.backButtonHidden) || (lhs.navigationBar != rhs.navigationBar) || (lhs.bottomBar != rhs.bottomBar)) {
            return false
        }
        if (((lhs.content?.count ?: 0) != (rhs.content?.count ?: 0)) || ((lhs.titleMenu != null) != (rhs.titleMenu != null))) {
            return false
        }
        // Don't compare on views because they will never compare equal. Toolbar block will get re-evaluated on
        // change to any state it accesses
        return true
    }
}

internal class ToolbarBarPreferences {
    internal val visibility: Visibility?
    internal val background: ShapeStyle?
    internal val backgroundVisibility: Visibility?
    internal val colorScheme: ColorScheme?

    internal fun reduce(next: ToolbarBarPreferences): ToolbarBarPreferences = ToolbarBarPreferences(visibility = next.visibility ?: visibility, background = next.background ?: background, backgroundVisibility = next.backgroundVisibility ?: backgroundVisibility, colorScheme = next.colorScheme ?: colorScheme)

    override fun equals(other: Any?): Boolean {
        if (other !is ToolbarBarPreferences) {
            return false
        }
        val lhs = this
        val rhs = other
        // Don't compare on background because it will never compare equal
        return lhs.visibility == rhs.visibility && lhs.backgroundVisibility == rhs.backgroundVisibility && (lhs.background != null) == (rhs.background != null) && lhs.colorScheme == rhs.colorScheme
    }

    constructor(visibility: Visibility? = null, background: ShapeStyle? = null, backgroundVisibility: Visibility? = null, colorScheme: ColorScheme? = null) {
        this.visibility = visibility
        this.background = background.sref()
        this.backgroundVisibility = backgroundVisibility
        this.colorScheme = colorScheme
    }
}

internal class ToolbarItems {
    internal val content: Array<View>

    @Composable
    internal fun filterTopBarLeading(): Array<View> {
        return filter(expandGroups = false) { it -> it == ToolbarItemPlacement.topBarLeading || it == ToolbarItemPlacement.navigationBarLeading }
    }

    @Composable
    internal fun filterTopBarTrailing(): Array<View> {
        return filter(expandGroups = false) l@{ it ->
            when (it) {
                ToolbarItemPlacement.automatic, ToolbarItemPlacement.principal, ToolbarItemPlacement.primaryAction, ToolbarItemPlacement.secondaryAction, ToolbarItemPlacement.topBarTrailing, ToolbarItemPlacement.navigationBarTrailing -> return@l true
                else -> return@l false
            }
        }
    }

    @Composable
    internal fun filterBottomBar(): Array<View> {
        var views = filter(expandGroups = true) { it -> it == ToolbarItemPlacement.bottomBar }
        // SwiftUI inserts a spacer between the first and remaining items
        if (views.count > 1 && !views.contains(where = { it ->
            it.strippingModifiers { it -> it is Spacer }
        })) {
            views.insert(Spacer(), at = 1)
        }
        return views.sref()
    }

    @Composable
    private fun filter(expandGroups: Boolean, placement: (ToolbarItemPlacement) -> Boolean): Array<View> {
        val filtered = mutableListOf<View>()
        val context = ComposeContext(composer = SideEffectComposer { view, context -> filter(view = view, expandGroups = expandGroups, placement = placement, filtered = filtered, context = context) })
        content.forEach { it -> it.Compose(context = context) }
        return Array(filtered, nocopy = true)
    }

    @Composable
    private fun filter(view: View, expandGroups: Boolean, placement: (ToolbarItemPlacement) -> Boolean, filtered: MutableList<View>, context: (Boolean) -> ComposeContext): ComposeResult {
        val matchtarget_1 = view as? ToolbarItemGroup
        if (matchtarget_1 != null) {
            val itemGroup = matchtarget_1
            if (placement(itemGroup.placement)) {
                if (expandGroups) {
                    itemGroup.content.collectViews(context = context(false))
                        .filter { it -> !it.isSwiftUIEmptyView }
                        .forEach { it -> filtered.add(it) }
                } else {
                    filtered.add(itemGroup)
                }
            }
        } else {
            val matchtarget_2 = view as? ToolbarItem
            if (matchtarget_2 != null) {
                val item = matchtarget_2
                if (placement(item.placement)) {
                    filtered.add(item)
                }
            } else {
                val matchtarget_3 = view as? ToolbarContent
                if (matchtarget_3 != null) {
                    val toolbarContent = matchtarget_3
                    // Create a builder that is able to collect the view's internal content by calling ComposeContent
                    val contentBuilder = ComposeBuilder(content = { it ->
                        view.ComposeContent(context = it)
                        ComposeResult.ok
                    })
                    for (view in contentBuilder.collectViews(context = context(false))) {
                        filter(view = view, expandGroups = expandGroups, placement = placement, filtered = filtered, context = context)
                    }
                } else if (placement(ToolbarItemPlacement.automatic) && !view.isSwiftUIEmptyView) {
                    filtered.add(view)
                }
            }
        }
        return ComposeResult.ok
    }

    constructor(content: Array<View>) {
        this.content = content.sref()
    }
}

