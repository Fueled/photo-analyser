// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.Set

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class TabView: View {
    internal val selection: Binding<Any>?
    internal val content: ComposeBuilder

    constructor(content: () -> View) {
        this.selection = null
        this.content = ComposeBuilder.from(content)
    }

    constructor(selection: Any?, content: () -> View) {
        this.selection = (selection as Binding<Any>?).sref()
        this.content = ComposeBuilder.from(content)
    }

    @ExperimentalMaterial3Api
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val tabItemContext = context.content()
        var tabViews: Array<View> = arrayOf()
        EnvironmentValues.shared.setValues({ it -> it.set_placement(ViewPlacement.tagged) }, in_ = { ->
            tabViews = content.collectViews(context = tabItemContext).filter { it -> !it.isSwiftUIEmptyView }
        })

        val navController = rememberNavController()
        val currentRoute = currentRoute(for_ = navController)
        if ((selection != null) && (currentRoute != null) && (selection.wrappedValue != tagValue(route = currentRoute, in_ = tabViews))) {
            route(tagValue = selection.wrappedValue, in_ = tabViews)?.let { route ->
                navigate(controller = navController, route = route)
            }
        }

        val preferenceUpdates = remember { -> mutableStateOf(0) }
        preferenceUpdates.value.sref() // Read so that it can trigger recompose on change
        val recompose = { -> preferenceUpdates.value += 1 }

        val tabBarPreferences = rememberSaveable(stateSaver = context.stateSaver as Saver<ToolbarBarPreferences, Any>) { -> mutableStateOf(TabBarPreferenceKey.defaultValue) }
        val tabBarPreferencesPreference = Preference<ToolbarBarPreferences>(key = TabBarPreferenceKey::class, update = { it -> tabBarPreferences.value = it }, recompose = recompose)
        val preferences: Array<Preference<*>> = arrayOf(tabBarPreferencesPreference)
        PreferenceValues.shared.syncingPreference(key = PreferredColorSchemePreferenceKey::class, recompose = recompose)?.let { colorSchemeSyncingPreference ->
            preferences.append(colorSchemeSyncingPreference)
        }

        // Perform an invisible compose pass to gather preference and layout information. Otherwise we may see the content render one
        // way then immediately re-render with different UI
        var modifier = context.modifier
        val hasComposed = remember { -> mutableStateOf(false) }
        if (!hasComposed.value) {
            modifier = modifier.alpha(0.0f)
        }

        val safeArea = EnvironmentValues.shared._safeArea
        val density = LocalDensity.current.sref()
        val bottomBarTopPx = remember { ->
            // Default our initial value to the expected value, which helps avoid visual artifacts as we measure actual values and
            // recompose with adjusted layouts
            if (safeArea != null) {
                mutableStateOf(with(density) { -> safeArea.presentationBoundsPx.bottom - 80.dp.toPx() })
            } else {
                mutableStateOf(0.0f)
            }
        }
        val bottomBar: @Composable () -> Unit = l@{ ->
            if (tabBarPreferences.value.visibility == Visibility.hidden) {
                bottomBarTopPx.value = 0.0f
                return@l
            }
            var tabBarModifier = Modifier.fillMaxWidth()
                .onGloballyPositioned { it ->
                    val topPx = it.boundsInWindow().top.sref()
                    if (topPx > 0.0f) {
                        bottomBarTopPx.value = topPx
                    }
                }
            val colorScheme = tabBarPreferences.value.colorScheme ?: ColorScheme.fromMaterialTheme()
            val indicatorColor = if (colorScheme == ColorScheme.dark) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f) else androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.2f)
            val tabBarBackgroundColor: androidx.compose.ui.graphics.Color
            val tabBarItemColors: NavigationBarItemColors
            if (tabBarPreferences.value.backgroundVisibility == Visibility.hidden) {
                tabBarBackgroundColor = Color.clear.colorImpl()
                tabBarItemColors = NavigationBarItemDefaults.colors(indicatorColor = indicatorColor)
            } else {
                val matchtarget_0 = tabBarPreferences.value.background
                if (matchtarget_0 != null) {
                    val background = matchtarget_0
                    val matchtarget_1 = background.asColor(opacity = 1.0, animationContext = null)
                    if (matchtarget_1 != null) {
                        val color = matchtarget_1
                        tabBarBackgroundColor = color
                    } else {
                        tabBarBackgroundColor = Color.clear.colorImpl()
                        background.asBrush(opacity = 1.0, animationContext = null)?.let { brush ->
                            tabBarModifier = tabBarModifier.background(brush)
                        }
                    }
                    tabBarItemColors = NavigationBarItemDefaults.colors(indicatorColor = indicatorColor)
                } else {
                    tabBarBackgroundColor = Color.systemBarBackground.colorImpl()
                    tabBarItemColors = NavigationBarItemDefaults.colors()
                }
            }
            val materialColorScheme = (tabBarPreferences.value.colorScheme?.asMaterialTheme() ?: MaterialTheme.colorScheme).sref()
            MaterialTheme(colorScheme = materialColorScheme) { ->
                NavigationBar(modifier = tabBarModifier, containerColor = tabBarBackgroundColor) { ->
                    for (tabIndex in 0 until tabViews.count) {
                        val route = String(describing = tabIndex)
                        val tabItem = tabViews[tabIndex].strippingModifiers(until = { it -> it == ComposeModifierRole.tabItem }, perform = { it -> it as? TabItemModifierView })
                        NavigationBarItem(colors = tabBarItemColors, icon = { ->
                            tabItem?.ComposeImage(context = tabItemContext)
                        }, label = { ->
                            tabItem?.ComposeTitle(context = tabItemContext)
                        }, selected = route == currentRoute, onClick = { ->
                            if (selection != null) {
                                val matchtarget_2 = tagValue(route = route, in_ = tabViews)
                                if (matchtarget_2 != null) {
                                    val tagValue = matchtarget_2
                                    selection.wrappedValue = tagValue
                                } else {
                                    navigate(controller = navController, route = route)
                                }
                            } else {
                                navigate(controller = navController, route = route)
                            }
                        })
                    }
                }
            }
        }

        // When we layout, extend into the safe area if it is due to system bars, not into any app chrome. We extend
        // into the top bar too so that tab content can also extend into the top area without getting cut off during
        // tab switches
        var ignoresSafeAreaEdges: Edge.Set = Edge.Set.of(Edge.Set.bottom, Edge.Set.top)
        ignoresSafeAreaEdges.formIntersection(safeArea?.absoluteSystemBarEdges ?: Edge.Set.of())
        IgnoresSafeAreaLayout(edges = ignoresSafeAreaEdges, context = context.content(modifier = modifier)) { context ->
            ComposeContainer(modifier = context.modifier, fillWidth = true, fillHeight = true) { modifier ->
                // Don't use a Scaffold: it clips content beyond its bounds and prevents .ignoresSafeArea modifiers from working
                Column(modifier = modifier.background(Color.background.colorImpl())) { ->
                    NavHost(navController, modifier = Modifier.fillMaxWidth().weight(1.0f), startDestination = "0", enterTransition = { -> EnterTransition.None }, exitTransition = { -> ExitTransition.None }) { ->
                        // Use a constant number of routes. Changing routes causes a NavHost to reset its state
                        for (tabIndex in 0 until 100) {
                            composable(String(describing = tabIndex)) { _ ->
                                val contentSafeArea = safeArea?.insetting(Edge.bottom, to = bottomBarTopPx.value)
                                // Inset manually where our container ignored the safe area, but we aren't showing a bar
                                val topPadding = (if (ignoresSafeAreaEdges.contains(Edge.Set.top)) WindowInsets.systemBars.asPaddingValues().calculateTopPadding() else 0.dp).sref()
                                val bottomPadding = (if (bottomBarTopPx.value <= 0.0f && ignoresSafeAreaEdges.contains(Edge.Set.bottom)) WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() else 0.dp).sref()
                                val contentModifier = Modifier.fillMaxSize().padding(top = topPadding, bottom = bottomPadding)
                                Box(modifier = contentModifier, contentAlignment = androidx.compose.ui.Alignment.Center) { ->
                                    EnvironmentValues.shared.setValues({ it ->
                                        if (contentSafeArea != null) {
                                            it.set_safeArea(contentSafeArea)
                                        }
                                    }, in_ = { ->
                                        PreferenceValues.shared.collectPreferences(preferences) { ->
                                            // Use a custom composer to only render the tabIndex'th view
                                            content.Compose(context = context.content(composer = TabIndexComposer(index = tabIndex)))
                                        }
                                    })
                                    hasComposed.value = true
                                }
                            }
                        }
                    }
                    bottomBar()
                }
            }
        }
    }

    private fun tagValue(route: String, in_: Array<View>): Any? {
        val tabViews = in_
        val tabIndex_0 = Int(string = route)
        if ((tabIndex_0 == null) || (tabIndex_0 < 0) || (tabIndex_0 >= tabViews.count)) {
            return null
        }
        return TagModifierView.strip(from = tabViews[tabIndex_0], role = ComposeModifierRole.tag)?.value.sref()
    }

    private fun route(tagValue: Any, in_: Array<View>): String? {
        val tabViews = in_
        for (tabIndex in 0 until tabViews.count) {
            val tabTagValue = TagModifierView.strip(from = tabViews[tabIndex], role = ComposeModifierRole.tag)?.value.sref()
            if (tagValue == tabTagValue) {
                return String(describing = tabIndex)
            }
        }
        return null
    }

    private fun navigate(controller: NavHostController, route: String) {
        val navController = controller
        navController.navigate(route) { ->
            popUpTo(navController.graph.startDestinationId) { -> saveState = true }
            // Avoid multiple copies of the same destination when reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    @Composable
    private fun currentRoute(for_: NavHostController): String? {
        val navController = for_
        // In your BottomNavigation composable, get the current NavBackStackEntry using the currentBackStackEntryAsState() function. This entry gives you access to the current NavDestination. The selected state of each BottomNavigationItem can then be determined by comparing the item's route with the route of the current destination and its parent destinations (to handle cases when you are using nested navigation) via the NavDestination hierarchy.
        return navController.currentBackStackEntryAsState().value?.destination?.route
    }

    companion object {
    }
}

internal class TabBarPreferenceKey: PreferenceKey<ToolbarBarPreferences> {

    companion object: PreferenceKeyCompanion<ToolbarBarPreferences> {
        override val defaultValue = ToolbarBarPreferences()
        override fun reduce(value: InOut<ToolbarBarPreferences>, nextValue: () -> ToolbarBarPreferences) {
            value.value = value.value.reduce(nextValue())
        }
    }
}

internal class TabItemModifierView: ComposeModifierView {
    internal val label: ComposeBuilder

    internal constructor(view: View, label: () -> View): super(view = view, role = ComposeModifierRole.tabItem) {
        this.label = ComposeBuilder.from(label)
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        view.Compose(context = context)
    }

    @Composable
    internal fun ComposeTitle(context: ComposeContext) {
        label.Compose(context = context.content(composer = RenderingComposer { view, context ->
            val stripped = view.strippingModifiers { it -> it }
            val matchtarget_3 = stripped as? Label
            if (matchtarget_3 != null) {
                val label = matchtarget_3
                label.ComposeTitle(context = context(false))
            } else if (stripped is Text) {
                view.ComposeContent(context = context(false))
            }
        }))
    }

    @Composable
    internal fun ComposeImage(context: ComposeContext) {
        label.Compose(context = context.content(composer = RenderingComposer { view, context ->
            val stripped = view.strippingModifiers { it -> it }
            val matchtarget_4 = stripped as? Label
            if (matchtarget_4 != null) {
                val label = matchtarget_4
                label.ComposeImage(context = context(false))
            } else if (stripped is Image) {
                view.ComposeContent(context = context(false))
            }
        }))
    }
}

internal class TabIndexComposer: RenderingComposer {
    internal val index: Int
    internal var currentIndex = 0

    internal constructor(index: Int): super() {
        this.index = index
    }

    override fun willCompose() {
        currentIndex = 0
    }

    @Composable
    override fun Compose(view: View, context: (Boolean) -> ComposeContext) {
        if (view.isSwiftUIEmptyView) {
            return
        }
        if (currentIndex == index) {
            view.ComposeContent(context = context(false))
        }
        currentIndex += 1
    }

    companion object: RenderingComposer.CompanionClass() {
    }
}

class TabViewStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TabViewStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = TabViewStyle(rawValue = 0)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val page = TabViewStyle(rawValue = 1)
    }
}

