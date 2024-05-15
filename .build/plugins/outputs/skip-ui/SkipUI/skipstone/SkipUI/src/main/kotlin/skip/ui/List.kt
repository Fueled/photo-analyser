// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.MutableCollection
import skip.lib.Set

import skip.foundation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path.Companion.combine
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

/// Corner radius for list sections.
internal val listSectionnCornerRadius = 8.0

class List: View {
    internal val fixedContent: ComposeBuilder?
    internal val forEach: ForEach?

    internal constructor(fixedContent: View? = null, identifier: ((Any) -> AnyHashable)? = null, indexRange: IntRange? = null, indexedContent: ((Int) -> View)? = null, objects: RandomAccessCollection<Any>? = null, objectContent: ((Any) -> View)? = null, objectsBinding: Binding<RandomAccessCollection<Any>>? = null, objectsBindingContent: ((Binding<RandomAccessCollection<Any>>, Int) -> View)? = null, editActions: EditActions = EditActions.of()) {
        if (fixedContent != null) {
            this.fixedContent = fixedContent as? ComposeBuilder ?: ComposeBuilder(view = fixedContent)
        } else {
            this.fixedContent = null
        }
        if (indexRange != null) {
            this.forEach = ForEach(identifier = identifier, indexRange = indexRange, indexedContent = indexedContent)
        } else if (objects != null) {
            this.forEach = ForEach(identifier = identifier, objects = objects, objectContent = objectContent)
        } else if (objectsBinding != null) {
            this.forEach = ForEach(identifier = identifier, objectsBinding = objectsBinding, objectsBindingContent = objectsBindingContent, editActions = editActions)
        } else {
            this.forEach = null
        }
    }

    constructor(content: () -> View): this(fixedContent = content()) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(selection: Binding<Any>, content: () -> View): this(fixedContent = content()) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val style = EnvironmentValues.shared._listStyle ?: ListStyle.automatic
        val backgroundVisibility = EnvironmentValues.shared._scrollContentBackground ?: Visibility.visible
        val styling = ListStyling(style = style, backgroundVisibility = backgroundVisibility)
        val itemContext = context.content()

        // When we layout, extend into safe areas that are due to system bars, not into any app chrome
        val safeArea = EnvironmentValues.shared._safeArea
        var ignoresSafeAreaEdges: Edge.Set = Edge.Set.of(Edge.Set.top, Edge.Set.bottom)
        ignoresSafeAreaEdges.formIntersection(safeArea?.absoluteSystemBarEdges ?: Edge.Set.of())
        IgnoresSafeAreaLayout(edges = ignoresSafeAreaEdges, context = context) { context ->
            ComposeContainer(scrollAxes = Axis.Set.vertical, modifier = context.modifier, fillWidth = true, fillHeight = true, then = Modifier.background(BackgroundColor(styling = styling, isItem = false))) { modifier ->
                Box(modifier = modifier) { -> ComposeList(context = itemContext, styling = styling, safeArea = safeArea) }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ComposeList(context: ComposeContext, styling: ListStyling, safeArea: SafeArea?) {
        // Collect all top-level views to compose. The LazyColumn itself is not a composable context, so we have to execute
        // our content's Compose function to collect its views before entering the LazyColumn body, then use LazyColumn's
        // LazyListScope functions to compose individual items
        val collectingComposer = LazyItemCollectingComposer()
        val viewsCollector = context.content(composer = collectingComposer)
        if (forEach != null) {
            forEach.appendLazyItemViews(to = collectingComposer.views, appendingContext = viewsCollector)
        } else if (fixedContent != null) {
            fixedContent.Compose(context = viewsCollector)
        }

        var modifier = context.modifier
        if (styling.style != ListStyle.plain) {
            modifier = modifier.padding(start = Companion.horizontalInset.dp, end = Companion.horizontalInset.dp)
        }
        modifier = modifier.fillWidth()

        val searchableState = EnvironmentValues.shared._searchableState
        val isSearchable = searchableState?.isOnNavigationStack == false

        // Remember the factory because we use it in the remembered reorderable state
        val factoryContext = remember { -> mutableStateOf(LazyItemFactoryContext()) }
        val moveTrigger = remember { -> mutableStateOf(0) }
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = if (isSearchable) 1 else 0)
        val reorderableState = rememberReorderableLazyListState(listState = listState, onMove = { from, to ->
            // Trigger recompose on move, but don't read the trigger state until we're inside the list content to limit its scope
            factoryContext.value.move(from = from.index, to = to.index, trigger = { it -> moveTrigger.value = it })
        }, onDragEnd = { _, _ -> factoryContext.value.commitMove() }, canDragOver = { candidate, dragging -> factoryContext.value.canMove(from = dragging.index, to = candidate.index) })
        modifier = modifier.reorderable(reorderableState)

        // Integrate with our scroll-to-top navigation bar taps
        val coroutineScope = rememberCoroutineScope()
        PreferenceValues.shared.reducePreference(key = ScrollToTopPreferenceKey::class, value = { ->
            coroutineScope.launch { -> reorderableState.listState.animateScrollToItem(0) }
        })

        val density = LocalDensity.current.sref()
        val headerSafeAreaHeight = headerSafeAreaHeight(safeArea, density = density)
        val footerSafeAreaHeight = footerSafeAreaHeight(safeArea, density = density)
        val hasHeader = styling.style != ListStyle.plain || headerSafeAreaHeight.value > 0
        val hasFooter = styling.style != ListStyle.plain || footerSafeAreaHeight.value > 0
        LazyColumn(state = reorderableState.listState, modifier = modifier) { ->
            val sectionHeaderContext = context.content(composer = RenderingComposer { view, context -> ComposeSectionHeader(view = view, context = context(false), styling = styling, isTop = false) })
            val topSectionHeaderContext = context.content(composer = RenderingComposer { view, context -> ComposeSectionHeader(view = view, context = context(false), styling = styling, isTop = true) })
            val sectionFooterContext = context.content(composer = RenderingComposer { view, context -> ComposeSectionFooter(view = view, context = context(false), styling = styling) })

            // Read move trigger here so that a move will recompose list content
            moveTrigger.value.sref()
            // Animate list operations. If we're searching, however, we disable animation to prevent weird
            // animations during search filtering. This is ugly and not robust, but it works in most cases
            val shouldAnimateItems: @Composable () -> Boolean = l@{ ->
                val searchableState_0 = EnvironmentValues.shared._searchableState
                if ((searchableState_0 == null) || !searchableState_0.isSearching.value) {
                    return@l true
                }
                if (!searchableState_0.isOnNavigationStack) {
                    return@l false
                }
                // When the .searchable modifier is on the NavigationStack, assume we're the target if we're the root
                return@l LocalNavigator.current?.isRoot != true
            }

            // Initialize the factory context with closures that use the LazyListScope to generate items
            var startItemIndex = if (hasHeader) 1 else 0 // Header inset
            if (isSearchable) {
                startItemIndex += 1 // Search field
            }
            factoryContext.value.initialize(startItemIndex = startItemIndex, item = { view ->
                item { ->
                    val itemModifier: Modifier = if (shouldAnimateItems()) Modifier.animateItemPlacement() else Modifier
                    val itemContext = context.content(composer = RenderingComposer { view, context -> ComposeItem(view = view, context = context(false), modifier = itemModifier, styling = styling) })
                    view.Compose(context = itemContext)
                }
            }, indexedItems = { range, identifier, offset, onDelete, onMove, factory ->
                val count = (range.endExclusive - range.start).sref()
                val key: ((Int) -> String)? = if (identifier == null) null else { it -> composeBundleString(for_ = identifier!!(factoryContext.value.remapIndex(it, from = offset))) }
                items(count = count, key = key) { index ->
                    val keyValue = key?.invoke(index + range.start) // Key closure already remaps index
                    val index = factoryContext.value.remapIndex(index, from = offset)
                    val itemModifier: Modifier = if (shouldAnimateItems()) Modifier.animateItemPlacement() else Modifier
                    val editableItemContext = context.content(composer = RenderingComposer { view, context -> ComposeEditableItem(view = view, context = context(false), modifier = itemModifier, styling = styling, key = keyValue, index = index, onDelete = onDelete, onMove = onMove, reorderableState = reorderableState) })
                    factory(index + range.start).Compose(context = editableItemContext)
                }
            }, objectItems = { objects, identifier, offset, onDelete, onMove, factory ->
                val key: (Int) -> String = { it -> composeBundleString(for_ = identifier(objects[factoryContext.value.remapIndex(it, from = offset)])) }
                items(count = objects.count, key = key) { index ->
                    val keyValue = key(index) // Key closure already remaps index
                    val index = factoryContext.value.remapIndex(index, from = offset)
                    val itemModifier: Modifier = if (shouldAnimateItems()) Modifier.animateItemPlacement() else Modifier
                    val editableItemContext = context.content(composer = RenderingComposer { view, context -> ComposeEditableItem(view = view, context = context(false), modifier = itemModifier, styling = styling, key = keyValue, index = index, onDelete = onDelete, onMove = onMove, reorderableState = reorderableState) })
                    factory(objects[index]).Compose(context = editableItemContext)
                }
            }, objectBindingItems = { objectsBinding, identifier, offset, editActions, onDelete, onMove, factory ->
                val key: (Int) -> String = { it -> composeBundleString(for_ = identifier(objectsBinding.wrappedValue[factoryContext.value.remapIndex(it, from = offset)])) }
                items(count = objectsBinding.wrappedValue.count, key = key) { index ->
                    val keyValue = key(index) // Key closure already remaps index
                    val index = factoryContext.value.remapIndex(index, from = offset)
                    val itemModifier: Modifier = if (shouldAnimateItems()) Modifier.animateItemPlacement() else Modifier
                    val editableItemContext = context.content(composer = RenderingComposer { view, context -> ComposeEditableItem(view = view, context = context(false), modifier = itemModifier, styling = styling, objectsBinding = objectsBinding, key = keyValue, index = index, editActions = editActions, onDelete = onDelete, onMove = onMove, reorderableState = reorderableState) })
                    factory(objectsBinding, index).Compose(context = editableItemContext)
                }
            }, sectionHeader = { view ->
                val context = (if (view === collectingComposer.views.firstOrNull()) topSectionHeaderContext else sectionHeaderContext).sref()
                if (styling.style == ListStyle.plain) {
                    stickyHeader { -> view.Compose(context = context) }
                } else {
                    item { -> view.Compose(context = context) }
                }
            }, sectionFooter = { view ->
                item { -> view.Compose(context = sectionFooterContext) }
            })

            if (isSearchable) {
                item { -> ComposeSearchField(state = searchableState!!, context = context, styling = styling) }
            }
            if (hasHeader) {
                val hasTopSection = collectingComposer.views.firstOrNull() is LazySectionHeader
                item { -> ComposeHeader(styling = styling, safeAreaHeight = headerSafeAreaHeight, hasTopSection = hasTopSection) }
            }
            for (view in collectingComposer.views.sref()) {
                val matchtarget_0 = view as? LazyItemFactory
                if (matchtarget_0 != null) {
                    val factory = matchtarget_0
                    factory.composeLazyItems(context = factoryContext.value)
                } else {
                    factoryContext.value.item(view)
                }
            }
            if (hasFooter) {
                val hasBottomSection = collectingComposer.views.lastOrNull() is LazySectionFooter
                item { -> ComposeFooter(styling = styling, safeAreaHeight = footerSafeAreaHeight, hasBottomSection = hasBottomSection) }
            }
        }
    }

    private fun headerSafeAreaHeight(safeArea: SafeArea?, density: Density): Dp {
        if ((safeArea == null) || (!safeArea.absoluteSystemBarEdges.contains(Edge.Set.top) || safeArea.safeBoundsPx.top <= safeArea.presentationBoundsPx.top)) {
            return 0.dp.sref()
        }
        return with(density) { -> (safeArea.safeBoundsPx.top - safeArea.presentationBoundsPx.top).toDp() }
    }

    private fun footerSafeAreaHeight(safeArea: SafeArea?, density: Density): Dp {
        if ((safeArea == null) || (!safeArea.absoluteSystemBarEdges.contains(Edge.Set.bottom) || safeArea.presentationBoundsPx.bottom <= safeArea.safeBoundsPx.bottom)) {
            return 0.dp.sref()
        }
        return with(density) { -> (safeArea.presentationBoundsPx.bottom - safeArea.safeBoundsPx.bottom).toDp() }
    }

    @Composable
    private fun ComposeItem(view: View, context: ComposeContext, modifier: Modifier = Modifier, styling: ListStyling, isItem: Boolean = true) {
        if (view.isSwiftUIEmptyView) {
            return
        }

        val itemModifierView = view.strippingModifiers(until = { it -> it == ComposeModifierRole.listItem }, perform = { it -> it as? ListItemModifierView })
        var itemModifier: Modifier = Modifier
        if (itemModifierView?.background == null) {
            itemModifier = itemModifier.background(BackgroundColor(styling = styling.withStyle(ListStyle.plain), isItem = isItem))
        }

        // The given modifiers include elevation shadow for dragging, etc that need to go before the others
        val containerContext = context.content(modifier = modifier.then(itemModifier).then(context.modifier))
        val contentModifier = Modifier.padding(horizontal = Companion.horizontalItemInset.dp, vertical = Companion.verticalItemInset.dp).fillWidth().requiredHeightIn(min = Companion.minimumItemHeight.dp)
        val composeContainer: @Composable (ComposeContext) -> Unit = { context ->
            Column(modifier = context.modifier) { ->
                // Note that we're calling the same view's Compose function again with a new context
                view.Compose(context = context.content(composer = ListItemComposer(contentModifier = contentModifier)))
                if (itemModifierView?.separator != Visibility.hidden) {
                    ComposeSeparator()
                }
            }
        }

        val matchtarget_1 = itemModifierView?.background
        if (matchtarget_1 != null) {
            val background = matchtarget_1
            TargetViewLayout(context = containerContext, isOverlay = false, alignment = Alignment.center, target = composeContainer, dependent = { it -> background.Compose(context = it) })
        } else {
            composeContainer(containerContext)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposeEditableItem(view: View, context: ComposeContext, modifier: Modifier, styling: ListStyling, objectsBinding: Binding<RandomAccessCollection<Any>>? = null, key: String?, index: Int, editActions: EditActions = EditActions.of(), onDelete: ((IntSet) -> Unit)?, onMove: ((IntSet, Int) -> Unit)?, reorderableState: ReorderableLazyListState) {
        if (view.isSwiftUIEmptyView) {
            return
        }
        if (key == null) {
            ComposeItem(view = view, context = context, modifier = modifier, styling = styling)
            return
        }
        val editActionsModifiers = EditActionsModifierView.unwrap(view = view)
        val isDeleteEnabled = (editActions.contains(EditActions.delete) || onDelete != null) && editActionsModifiers?.isDeleteDisabled != true
        val isMoveEnabled = (editActions.contains(EditActions.move) || onMove != null) && editActionsModifiers?.isMoveDisabled != true
        if (!isDeleteEnabled && !isMoveEnabled) {
            ComposeItem(view = view, context = context, modifier = modifier, styling = styling)
            return
        }

        if (isDeleteEnabled) {
            val rememberedOnDelete = rememberUpdatedState({ ->
                if (onDelete != null) {
                    onDelete(IntSet(integer = index))
                } else if ((objectsBinding != null) && (objectsBinding.wrappedValue.count > index)) {
                    (objectsBinding.wrappedValue as? RangeReplaceableCollection<Any>)?.remove(at = index)
                }
            })
            val coroutineScope = rememberCoroutineScope()
            val positionalThreshold = with(LocalDensity.current) { -> 164.dp.toPx() }
            val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = l@{ it ->
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    coroutineScope.launch { -> rememberedOnDelete.value() }
                }
                return@l false
            }, positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold.sref())

            val content: @Composable (Modifier) -> Unit = { it ->
                SwipeToDismiss(state = dismissState, directions = kotlin.collections.setOf(SwipeToDismissBoxValue.EndToStart), modifier = it, background = { ->
                    val trashVector = Image.composeImageVector(named = "trash")!!
                    Box(modifier = Modifier.background(androidx.compose.ui.graphics.Color.Red).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.CenterEnd) { -> Icon(imageVector = trashVector, contentDescription = "Delete", modifier = Modifier.padding(end = 24.dp), tint = androidx.compose.ui.graphics.Color.White) }
                }, dismissContent = { -> ComposeItem(view = view, context = context, styling = styling) })
            }
            if (isMoveEnabled) {
                ComposeReorderableItem(reorderableState = reorderableState, key = key, modifier = modifier, content = content)
            } else {
                content(modifier)
            }
        } else {
            ComposeReorderableItem(reorderableState = reorderableState, key = key, modifier = modifier) { it -> ComposeItem(view = view, context = context, modifier = it, styling = styling) }
        }
    }

    @Composable
    private fun ComposeReorderableItem(reorderableState: ReorderableLazyListState, key: String, modifier: Modifier, content: @Composable (Modifier) -> Unit) {
        ReorderableItem(state = reorderableState, key = key, defaultDraggingModifier = modifier) { dragging ->
            var itemModifier = Modifier.detectReorderAfterLongPress(reorderableState)
            if (dragging) {
                val elevation = animateDpAsState(8.dp)
                itemModifier = itemModifier.shadow(elevation.value)
            }
            content(itemModifier)
        }
    }

    @Composable
    private fun ComposeSeparator(): Unit = Box(modifier = Modifier.padding(start = Companion.horizontalItemInset.dp).fillWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant))

    @Composable
    private fun ComposeSectionHeader(view: View, context: ComposeContext, styling: ListStyling, isTop: Boolean) {
        if (!isTop && styling.style != ListStyle.plain) {
            // Vertical padding
            ComposeFooter(styling = styling, safeAreaHeight = 0.dp, hasBottomSection = true)
        }
        val backgroundColor = BackgroundColor(styling = styling.withStyle(ListStyle.automatic), isItem = false)
        val modifier = Modifier
            .zIndex(0.5f)
            .background(backgroundColor)
            .then(context.modifier)
        var contentModifier = Modifier.fillWidth()
        if (isTop && styling.style != ListStyle.plain) {
            contentModifier = contentModifier.padding(start = Companion.horizontalItemInset.dp, top = 0.dp, end = Companion.horizontalItemInset.dp, bottom = Companion.verticalItemInset.dp)
        } else {
            contentModifier = contentModifier.padding(horizontal = Companion.horizontalItemInset.dp, vertical = Companion.verticalItemInset.dp)
        }
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.BottomCenter) { ->
            Column(modifier = Modifier.fillWidth()) { ->
                EnvironmentValues.shared.setValues({ it -> it.set_listSectionHeaderStyle(styling.style) }, in_ = { -> view.Compose(context = context.content(modifier = contentModifier)) })
            }
            if (styling.style != ListStyle.plain) {
                ComposeRoundedCorners(isTop = true, fill = backgroundColor)
            }
        }
    }

    @Composable
    private fun ComposeSectionFooter(view: View, context: ComposeContext, styling: ListStyling) {
        if (styling.style == ListStyle.plain) {
            ComposeItem(view = view, context = context, styling = styling, isItem = false)
        } else {
            val backgroundColor = BackgroundColor(styling = styling, isItem = false)
            val modifier = Modifier.offset(y = -1.dp)
                .zIndex(0.5f)
                .background(backgroundColor)
                .then(context.modifier)
            val contentModifier = Modifier.fillWidth().padding(horizontal = Companion.horizontalItemInset.dp, vertical = Companion.verticalItemInset.dp)
            Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.TopCenter) { ->
                Column(modifier = Modifier.fillWidth().heightIn(min = 1.dp)) { ->
                    EnvironmentValues.shared.setValues({ it -> it.set_listSectionFooterStyle(styling.style) }, in_ = { -> view.Compose(context = context.content(modifier = contentModifier)) })
                }
                ComposeRoundedCorners(isTop = false, fill = backgroundColor)
            }
        }
    }

    /// - Warning: Only call for non-.plain styles or with a positive safe area height. This is distinct from having this function detect
    /// .plain and zero-height and return without rendering. That causes .plain style lists to have a weird rubber banding effect on overscroll.
    @Composable
    private fun ComposeHeader(styling: ListStyling, safeAreaHeight: Dp, hasTopSection: Boolean) {
        var height = safeAreaHeight.sref()
        if (styling.style != ListStyle.plain) {
            height += Companion.verticalInset.dp.sref()
        }
        val backgroundColor = BackgroundColor(styling = styling, isItem = false)
        val modifier = Modifier.fillWidth()
            .height(height)
            .zIndex(0.5f)
            .background(backgroundColor)
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.BottomCenter) { ->
            if (!hasTopSection && styling.style != ListStyle.plain) {
                ComposeRoundedCorners(isTop = true, fill = backgroundColor)
            }
        }
    }

    /// - Warning: Only call for non-.plain styles or with a positive safe area height. This is distinct from having this function detect
    /// .plain and zero-height and return without rendering. That causes .plain style lists to have a weird rubber banding effect on overscroll.
    @Composable
    private fun ComposeFooter(styling: ListStyling, safeAreaHeight: Dp, hasBottomSection: Boolean) {
        var height = safeAreaHeight.sref()
        var offset = 0.dp.sref()
        if (styling.style != ListStyle.plain) {
            height += Companion.verticalInset.dp.sref()
            offset = (-1.dp).sref() // Cover last row's divider
        }
        val backgroundColor = BackgroundColor(styling = styling, isItem = false)
        val modifier = Modifier.fillWidth()
            .height(height)
            .offset(y = offset)
            .zIndex(0.5f)
            .background(backgroundColor)
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.TopCenter) { ->
            if (!hasBottomSection && styling.style != ListStyle.plain) {
                ComposeRoundedCorners(isTop = false, fill = backgroundColor)
            }
        }
    }

    @Composable
    private fun ComposeRoundedCorners(isTop: Boolean, fill: androidx.compose.ui.graphics.Color) {
        val shape = GenericShape { size, _ ->
            val rect = Rect(left = 0.0f, top = 0.0f, right = size.width, bottom = size.height)
            val rectPath = androidx.compose.ui.graphics.Path()
            rectPath.addRect(rect)
            val roundRect: RoundRect
            if (isTop) {
                roundRect = RoundRect(rect, topLeft = CornerRadius(size.height), topRight = CornerRadius(size.height))
            } else {
                roundRect = RoundRect(rect, bottomLeft = CornerRadius(size.height), bottomRight = CornerRadius(size.height))
            }
            val roundedRectPath = androidx.compose.ui.graphics.Path()
            roundedRectPath.addRoundRect(roundRect)
            addPath(combine(PathOperation.Difference, rectPath, roundedRectPath))
        }
        val offset = (if (isTop) listSectionnCornerRadius.dp else -listSectionnCornerRadius.dp).sref()
        val modifier = Modifier
            .fillMaxWidth()
            .height(listSectionnCornerRadius.dp)
            .offset(y = offset)
            .clip(shape)
            .background(fill)
        Box(modifier = modifier)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ComposeSearchField(state: SearchableState, context: ComposeContext, styling: ListStyling) {
        var modifier = Modifier.background(BackgroundColor(styling = styling, isItem = false))
        if (styling.style == ListStyle.plain) {
            modifier = modifier.padding(start = Companion.horizontalInset.dp, end = Companion.horizontalInset.dp)
        } else {
            modifier = modifier.padding(top = Companion.verticalInset.dp)
        }
        modifier = modifier.fillMaxWidth()
        SearchField(state = state, context = context.content(modifier = modifier))
    }

    @Composable
    private fun BackgroundColor(styling: ListStyling, isItem: Boolean): androidx.compose.ui.graphics.Color {
        if (!isItem && styling.backgroundVisibility == Visibility.hidden) {
            return Color.clear.colorImpl()
        } else if (styling.style == ListStyle.plain) {
            return Color.background.colorImpl()
        } else {
            return Color.systemBackground.colorImpl()
        }
    }

    companion object {

        private val horizontalInset = 16.0
        private val verticalInset = 16.0
        private val minimumItemHeight = 32.0
        private val horizontalItemInset = 16.0
        private val verticalItemInset = 8.0
    }
}

// Kotlin does not support generic constructor parameters, so we have to model many List constructors as functions

//extension List {
//    public init<Data, RowContent>(_ data: Data, @ViewBuilder rowContent: @escaping (Data.Element) -> RowContent) where Content == ForEach<Data, Data.Element.ID, RowContent>, Data : RandomAccessCollection, RowContent : View, Data.Element : Identifiable
//}
fun <ObjectType> List(data: RandomAccessCollection<ObjectType>, rowContent: (ObjectType) -> View): List {
    return List(identifier = { it -> (it as Identifiable<Hashable>).id }, objects = data as RandomAccessCollection<Any>, objectContent = { it -> rowContent(it as ObjectType) })
}

//extension List {
//    public init<Data, ID, RowContent>(_ data: Data, id: KeyPath<Data.Element, ID>, @ViewBuilder rowContent: @escaping (Data.Element) -> RowContent) where Content == ForEach<Data, ID, RowContent>, Data : RandomAccessCollection, ID : Hashable, RowContent : View
//}
fun <ObjectType> List(data: RandomAccessCollection<ObjectType>, id: (ObjectType) -> AnyHashable, rowContent: (ObjectType) -> View): List where ObjectType: Any {
    return List(identifier = { it -> id(it as ObjectType) }, objects = data as RandomAccessCollection<Any>, objectContent = { it -> rowContent(it as ObjectType) })
}
fun List(data: IntRange, id: ((Int) -> AnyHashable)? = null, rowContent: (Int) -> View): List {
    return List(identifier = if (id == null) null else { it -> id!!(it as Int) }, indexRange = data, indexedContent = rowContent)
}

//extension List {
//  public init<Data, RowContent>(_ data: Binding<Data>, editActions: EditActions /* <Data> */, @ViewBuilder rowContent: @escaping (Binding<Data.Element>) -> RowContent) where Content == ForEach<IndexedIdentifierCollection<Data, Data.Element.ID>, Data.Element.ID, EditableCollectionContent<RowContent, Data>>, Data : MutableCollection, Data : RandomAccessCollection, RowContent : View, Data.Element : Identifiable, Data.Index : Hashable
//}
fun <Data, ObjectType> List(data: Binding<Data>, editActions: EditActions = EditActions.of(), rowContent: (Binding<ObjectType>) -> View): List where Data: RandomAccessCollection<ObjectType> {
    return List(identifier = { it -> (it as Identifiable<Hashable>).id }, objectsBinding = data as Binding<RandomAccessCollection<Any>>, objectsBindingContent = l@{ data, index ->
        val binding = Binding<ObjectType>(get = { -> data.wrappedValue[index] as ObjectType }, set = { it -> (data.wrappedValue as skip.lib.MutableCollection<ObjectType>)[index] = it.sref() })
        return@l rowContent(binding)
    }, editActions = editActions)
}

//extension List {
//  public init<Data, ID, RowContent>(_ data: Binding<Data>, id: KeyPath<Data.Element, ID>, editActions: EditActions /* <Data> */, @ViewBuilder rowContent: @escaping (Binding<Data.Element>) -> RowContent) where Content == ForEach<IndexedIdentifierCollection<Data, ID>, ID, EditableCollectionContent<RowContent, Data>>, Data : MutableCollection, Data : RandomAccessCollection, ID : Hashable, RowContent : View, Data.Index : Hashable
//}
fun <Data, ObjectType> List(data: Binding<Data>, id: (ObjectType) -> AnyHashable, editActions: EditActions = EditActions.of(), rowContent: (Binding<ObjectType>) -> View): List where Data: RandomAccessCollection<ObjectType> {
    return List(identifier = { it -> id(it as ObjectType) }, objectsBinding = data as Binding<RandomAccessCollection<Any>>, objectsBindingContent = l@{ data, index ->
        val binding = Binding<ObjectType>(get = { -> data.wrappedValue[index] as ObjectType }, set = { it -> (data.wrappedValue as skip.lib.MutableCollection<ObjectType>)[index] = it.sref() })
        return@l rowContent(binding)
    }, editActions = editActions)
}

internal class ListStyling {
    internal val style: ListStyle
    internal val backgroundVisibility: Visibility

    internal fun withStyle(style: ListStyle): ListStyling = ListStyling(style = style, backgroundVisibility = backgroundVisibility)

    constructor(style: ListStyle, backgroundVisibility: Visibility) {
        this.style = style
        this.backgroundVisibility = backgroundVisibility
    }
}

/// Adopted by views that adapt when used as a list item.
internal interface ListItemAdapting {
    /// Whether to compose this view as a list item or to use the standard compose pipeline.
    @Composable
    fun shouldComposeListItem(): Boolean

    /// Compose this view as a list item.
    @Composable
    fun ComposeListItem(context: ComposeContext, contentModifier: Modifier)
}

internal class ListItemComposer: RenderingComposer {
    internal val contentModifier: Modifier

    internal constructor(contentModifier: Modifier): super() {
        this.contentModifier = contentModifier
    }

    @Composable
    override fun Compose(view: View, context: (Boolean) -> ComposeContext) {
        val matchtarget_2 = view as? ListItemAdapting
        if (matchtarget_2 != null) {
            val listItemAdapting = matchtarget_2
            if (listItemAdapting.shouldComposeListItem()) {
                listItemAdapting.ComposeListItem(context = context(false), contentModifier = contentModifier)
            } else if (view is ComposeModifierView || !view.isSwiftUIModuleView) {
                // Allow content of modifier views and custom views to adapt to list item rendering
                view.ComposeContent(context = context(true))
            } else {
                Box(modifier = contentModifier, contentAlignment = androidx.compose.ui.Alignment.CenterStart) { -> view.ComposeContent(context = context(false)) }
            }
        } else if (view is ComposeModifierView || !view.isSwiftUIModuleView) {
            // Allow content of modifier views and custom views to adapt to list item rendering
            view.ComposeContent(context = context(true))
        } else {
            Box(modifier = contentModifier, contentAlignment = androidx.compose.ui.Alignment.CenterStart) { -> view.ComposeContent(context = context(false)) }
        }
    }

    companion object: RenderingComposer.CompanionClass() {
    }
}

class ListStyle: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ListStyle) return false
        return rawValue == other.rawValue
    }

    companion object {

        val automatic = ListStyle(rawValue = 0)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val sidebar = ListStyle(rawValue = 1)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val insetGrouped = ListStyle(rawValue = 2)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val grouped = ListStyle(rawValue = 3)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val inset = ListStyle(rawValue = 4)

        val plain = ListStyle(rawValue = 5)
    }
}

sealed class ListItemTint: Sendable {
    class FixedCase(val associated0: Color): ListItemTint() {
    }
    class PreferredCase(val associated0: Color): ListItemTint() {
    }
    class MonochromeCase: ListItemTint() {
    }

    companion object {
        fun fixed(associated0: Color): ListItemTint = FixedCase(associated0)
        fun preferred(associated0: Color): ListItemTint = PreferredCase(associated0)
        val monochrome: ListItemTint = MonochromeCase()
    }
}

sealed class ListSectionSpacing: Sendable {
    class DefaultCase: ListSectionSpacing() {
    }
    class CompactCase: ListSectionSpacing() {
    }
    class CustomCase(val associated0: Double): ListSectionSpacing() {
    }

    companion object {
        val default: ListSectionSpacing = DefaultCase()
        val compact: ListSectionSpacing = CompactCase()
        fun custom(associated0: Double): ListSectionSpacing = CustomCase(associated0)
    }
}

internal class ListItemModifierView: ComposeModifierView, ListItemAdapting {
    internal var background: View? = null
        get() = field.sref({ this.background = it })
        set(newValue) {
            field = newValue.sref()
        }
    internal var separator: Visibility? = null

    internal constructor(view: View, background: View? = null, separator: Visibility? = null): super(view = view, role = ComposeModifierRole.listItem) {
        val modifierView = view.strippingModifiers(until = { it -> it == ComposeModifierRole.listItem }, perform = { it -> it as? ListItemModifierView })
        this.background = background ?: modifierView?.background
        this.separator = separator ?: modifierView?.separator
    }

    @Composable
    override fun shouldComposeListItem(): Boolean {
        return (view as? ListItemAdapting)?.shouldComposeListItem() == true
    }

    @Composable
    override fun ComposeListItem(context: ComposeContext, contentModifier: Modifier) {
        (view as? ListItemAdapting)?.ComposeListItem(context = context, contentModifier = contentModifier)
    }
}

