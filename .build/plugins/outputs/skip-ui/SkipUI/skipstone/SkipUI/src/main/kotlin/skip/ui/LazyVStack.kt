// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class LazyVStack: View {
    internal val alignment: HorizontalAlignment
    internal val spacing: Double?
    internal val content: ComposeBuilder

    constructor(alignment: HorizontalAlignment = HorizontalAlignment.center, spacing: Double? = null, pinnedViews: PinnedScrollableViews = PinnedScrollableViews.of(), content: () -> View) {
        this.alignment = alignment
        this.spacing = spacing
        this.content = ComposeBuilder.from(content)
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val columnAlignment = alignment.asComposeAlignment()
        val columnArrangement = Arrangement.spacedBy((spacing ?: 8.0).dp, alignment = androidx.compose.ui.Alignment.CenterVertically)
        val isScrollEnabled = EnvironmentValues.shared._scrollAxes.contains(Axis.Set.vertical)

        // Collect all top-level views to compose. The LazyColumn itself is not a composable context, so we have to execute
        // our content's Compose function to collect its views before entering the LazyColumn body, then use LazyColumn's
        // LazyListScope functions to compose individual items
        val collectingComposer = LazyItemCollectingComposer()
        val viewsCollector = context.content(composer = collectingComposer)
        content.Compose(context = viewsCollector)

        val itemContext = context.content()
        val factoryContext = LazyItemFactoryContext()
        ComposeContainer(axis = Axis.vertical, modifier = context.modifier, fillWidth = true, fillHeight = true) { modifier ->
            LazyColumn(modifier = modifier, verticalArrangement = columnArrangement, horizontalAlignment = columnAlignment, userScrollEnabled = isScrollEnabled) { ->
                factoryContext.initialize(startItemIndex = 0, item = { view ->
                    item { -> view.Compose(context = itemContext) }
                }, indexedItems = { range, identifier, _, _, _, factory ->
                    val count = (range.endExclusive - range.start).sref()
                    val key: ((Int) -> String)? = if (identifier == null) null else { it -> composeBundleString(for_ = identifier!!(it)) }
                    items(count = count, key = key) { index -> factory(index + range.start).Compose(context = itemContext) }
                }, objectItems = { objects, identifier, _, _, _, factory ->
                    val key: (Int) -> String = { it -> composeBundleString(for_ = identifier(objects[it])) }
                    items(count = objects.count, key = key) { index -> factory(objects[index]).Compose(context = itemContext) }
                }, objectBindingItems = { objectsBinding, identifier, _, _, _, _, factory ->
                    val key: (Int) -> String = { it -> composeBundleString(for_ = identifier(objectsBinding.wrappedValue[it])) }
                    items(count = objectsBinding.wrappedValue.count, key = key) { index -> factory(objectsBinding, index).Compose(context = itemContext) }
                }, sectionHeader = { view ->
                    item { -> view.Compose(context = itemContext) }
                }, sectionFooter = { view ->
                    item { -> view.Compose(context = itemContext) }
                })
                for (view in collectingComposer.views.sref()) {
                    val matchtarget_0 = view as? LazyItemFactory
                    if (matchtarget_0 != null) {
                        val factory = matchtarget_0
                        factory.composeLazyItems(context = factoryContext)
                    } else {
                        factoryContext.item(view)
                    }
                }
            }
        }
    }

    companion object {
    }
}
