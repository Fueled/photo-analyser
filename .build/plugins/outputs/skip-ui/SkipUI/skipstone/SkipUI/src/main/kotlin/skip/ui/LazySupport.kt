// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import skip.foundation.*
import androidx.compose.runtime.Composable

/// Adopted by views that generate lazy items.
internal interface LazyItemFactory {
    /// Append views and view factories representing lazy items to the given mutable list.
    ///
    /// - Parameter appendingContext: Pass this context to the `Compose` function of a `ComposableView` to append all its child views.
    /// - Returns A `ComposeResult` to force the calling container to be fully re-evaluated on state change. Otherwise if only this
    ///   function were called again, it could continue appending to the given mutable list.
    @Composable
    fun appendLazyItemViews(to: MutableList<View>, appendingContext: ComposeContext): ComposeResult

    /// Use the given context to compose individual lazy items and ranges of items.
    fun composeLazyItems(context: LazyItemFactoryContext)
}

/// Allows `LazyItemFactory` instances to define the lazy content.
class LazyItemFactoryContext {
    internal var item: (View) -> Unit = { _ ->  }
        private set
    internal var indexedItems: (IntRange, ((Any) -> AnyHashable)?, ((IntSet) -> Unit)?, ((IntSet, Int) -> Unit)?, (Int) -> View) -> Unit = { _, _, _, _, _ ->  }
        private set
    internal var objectItems: (RandomAccessCollection<Any>, (Any) -> AnyHashable, ((IntSet) -> Unit)?, ((IntSet, Int) -> Unit)?, (Any) -> View) -> Unit = { _, _, _, _, _ ->  }
        private set
    internal var objectBindingItems: (Binding<RandomAccessCollection<Any>>, (Any) -> AnyHashable, EditActions, ((IntSet) -> Unit)?, ((IntSet, Int) -> Unit)?, (Binding<RandomAccessCollection<Any>>, Int) -> View) -> Unit = { _, _, _, _, _, _ ->  }
        private set
    internal var sectionHeader: (View) -> Unit = { _ ->  }
        private set
    internal var sectionFooter: (View) -> Unit = { _ ->  }
        private set
    private var startItemIndex = 0

    /// Initialize the content factories.
    internal fun initialize(startItemIndex: Int, item: (View) -> Unit, indexedItems: (IntRange, ((Any) -> AnyHashable)?, Int, ((IntSet) -> Unit)?, ((IntSet, Int) -> Unit)?, (Int) -> View) -> Unit, objectItems: (RandomAccessCollection<Any>, (Any) -> AnyHashable, Int, ((IntSet) -> Unit)?, ((IntSet, Int) -> Unit)?, (Any) -> View) -> Unit, objectBindingItems: (Binding<RandomAccessCollection<Any>>, (Any) -> AnyHashable, Int, EditActions, ((IntSet) -> Unit)?, ((IntSet, Int) -> Unit)?, (Binding<RandomAccessCollection<Any>>, Int) -> View) -> Unit, sectionHeader: (View) -> Unit, sectionFooter: (View) -> Unit) {
        this.startItemIndex = startItemIndex

        content.removeAll()
        this.item = { view ->
            // If this is an item after a section, add a header before it
            if (content.last is LazyItemFactoryContext.Content.SectionFooterCase) {
                this.sectionHeader(EmptyView())
            }
            item(view)
            content.append(LazyItemFactoryContext.Content.items(1, null))
        }
        this.indexedItems = { range, identifier, onDelete, onMove, factory ->
            if (content.last is LazyItemFactoryContext.Content.SectionFooterCase) {
                this.sectionHeader(EmptyView())
            }
            indexedItems(range, identifier, count, onDelete, onMove, factory)
            content.append(LazyItemFactoryContext.Content.items(range.endExclusive - range.start, onMove))
        }
        this.objectItems = { objects, identifier, onDelete, onMove, factory ->
            if (content.last is LazyItemFactoryContext.Content.SectionFooterCase) {
                this.sectionHeader(EmptyView())
            }
            objectItems(objects, identifier, count, onDelete, onMove, factory)
            content.append(LazyItemFactoryContext.Content.objectItems(objects, onMove))
        }
        this.objectBindingItems = { binding, identifier, editActions, onDelete, onMove, factory ->
            if (content.last is LazyItemFactoryContext.Content.SectionFooterCase) {
                this.sectionHeader(EmptyView())
            }
            objectBindingItems(binding, identifier, count, editActions, onDelete, onMove, factory)
            content.append(LazyItemFactoryContext.Content.objectBindingItems(binding, onMove))
        }
        this.sectionHeader = { view ->
            // If this is a header after an item, add a section footer before it
            for (unusedi in 0..0) {
                when (content.last) {
                    is LazyItemFactoryContext.Content.SectionFooterCase, null -> break
                    else -> this.sectionFooter(EmptyView())
                }
            }
            sectionHeader(view)
            content.append(LazyItemFactoryContext.Content.sectionHeader)
        }
        this.sectionFooter = { view ->
            sectionFooter(view)
            content.append(LazyItemFactoryContext.Content.sectionFooter)
        }
    }

    /// The current number of content items.
    internal val count: Int
        get() {
            var itemCount = 0
            for (content in this.content.sref()) {
                when (content) {
                    is LazyItemFactoryContext.Content.ItemsCase -> {
                        val count = content.associated0
                        itemCount += count
                    }
                    is LazyItemFactoryContext.Content.ObjectItemsCase -> {
                        val objects = content.associated0
                        itemCount += objects.count
                    }
                    is LazyItemFactoryContext.Content.ObjectBindingItemsCase -> {
                        val binding = content.associated0
                        itemCount += binding.wrappedValue.count
                    }
                    is LazyItemFactoryContext.Content.SectionHeaderCase, is LazyItemFactoryContext.Content.SectionFooterCase -> itemCount += 1
                }
            }
            return itemCount
        }

    private var moving: Tuple2<Int, Int>? = null
    private var moveTrigger = 0

    /// Re-map indexes for any in-progress operations.
    internal fun remapIndex(index: Int, from: Int): Int {
        val offset = from
        val moving_0 = moving
        if (moving_0 == null) {
            return index
        }
        // While a move is in progress we have to make the list appear reordered even though we don't change
        // the underlying data until the user ends the drag
        val offsetIndex = index + offset + startItemIndex
        if (offsetIndex == moving_0.toIndex) {
            return moving_0.fromIndex - offset - startItemIndex
        }
        if (moving_0.fromIndex < moving_0.toIndex && offsetIndex >= moving_0.fromIndex && offsetIndex < moving_0.toIndex) {
            return index + 1
        } else if (moving_0.fromIndex > moving_0.toIndex && offsetIndex > moving_0.toIndex && offsetIndex <= moving_0.fromIndex) {
            return index - 1
        } else {
            return index
        }
    }

    /// Commit the current active move operation, if any.
    internal fun commitMove() {
        val moving_1 = moving
        if (moving_1 == null) {
            return
        }
        val fromIndex = moving_1.fromIndex
        val toIndex = moving_1.toIndex
        this.moving = null
        performMove(fromIndex = fromIndex, toIndex = toIndex)
    }

    /// Call this function during an active move operation with the current move progress.
    internal fun move(from: Int, to: Int, trigger: (Int) -> Unit) {
        val fromIndex = from
        val toIndex = to
        if (moving == null) {
            if (fromIndex != toIndex) {
                moving = Tuple2(fromIndex, toIndex)
                trigger(++moveTrigger) // Trigger recompose to see change
            }
        } else {
            // Keep the original fromIndex, not the in-progress one. The framework assumes we move one position at a time
            if (moving!!.fromIndex == toIndex) {
                moving = null
            } else {
                moving = Tuple2(moving!!.fromIndex, toIndex)
            }
            trigger(++moveTrigger) // Trigger recompose to see change
        }
    }

    private fun performMove(fromIndex: Int, toIndex: Int) {
        var itemIndex = startItemIndex
        for (content in this.content.sref()) {
            when (content) {
                is LazyItemFactoryContext.Content.ItemsCase -> {
                    val count = content.associated0
                    val onMove = content.associated1
                    if (performMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = count, onMove = onMove)) {
                        return
                    }
                }
                is LazyItemFactoryContext.Content.ObjectItemsCase -> {
                    val objects = content.associated0
                    val onMove = content.associated1
                    if (performMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = objects.count, onMove = onMove)) {
                        return
                    }
                }
                is LazyItemFactoryContext.Content.ObjectBindingItemsCase -> {
                    val binding = content.associated0
                    val onMove = content.associated1
                    if (performMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = binding.wrappedValue.count, onMove = onMove, customMove = { ->
                        (binding.wrappedValue as? RangeReplaceableCollection<Any>)?.remove(at = fromIndex - itemIndex)?.let { element ->
                            (binding.wrappedValue as? RangeReplaceableCollection<Any>)?.insert(element, at = toIndex - itemIndex)
                        }
                    })) {
                        return
                    }
                }
                is LazyItemFactoryContext.Content.SectionHeaderCase, is LazyItemFactoryContext.Content.SectionFooterCase -> {
                    if (performMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = 1, onMove = null)) {
                        return
                    }
                }
            }
        }
    }

    private fun performMove(fromIndex: Int, toIndex: Int, itemIndex: InOut<Int>, count: Int, onMove: ((IntSet, Int) -> Unit)?, customMove: (() -> Unit)? = null): Boolean {
        if (min(fromIndex, toIndex) < itemIndex.value || max(fromIndex, toIndex) >= itemIndex.value + count) {
            itemIndex.value += count
            return false
        }
        if (onMove != null) {
            val indexSet = IntSet(integer = fromIndex - itemIndex.value)
            onMove(indexSet, if (fromIndex < toIndex) toIndex - itemIndex.value + 1 else toIndex - itemIndex.value)
        } else if (customMove != null) {
            customMove()
        }
        return true
    }

    /// Whether a given move would be permitted.
    internal fun canMove(from: Int, to: Int): Boolean {
        val fromIndex = from
        val toIndex = to
        if (fromIndex == toIndex) {
            return true
        }
        var itemIndex = startItemIndex
        for (content in this.content.sref()) {
            when (content) {
                is LazyItemFactoryContext.Content.ItemsCase -> {
                    val count = content.associated0
                    canMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = count)?.let { ret ->
                        return ret
                    }
                }
                is LazyItemFactoryContext.Content.ObjectItemsCase -> {
                    val objects = content.associated0
                    canMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = objects.count)?.let { ret ->
                        return ret
                    }
                }
                is LazyItemFactoryContext.Content.ObjectBindingItemsCase -> {
                    val binding = content.associated0
                    canMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = binding.wrappedValue.count)?.let { ret ->
                        return ret
                    }
                }
                is LazyItemFactoryContext.Content.SectionHeaderCase, is LazyItemFactoryContext.Content.SectionFooterCase -> {
                    canMove(fromIndex = fromIndex, toIndex = toIndex, itemIndex = InOut({ itemIndex }, { itemIndex = it }), count = 1)?.let { ret ->
                        return ret
                    }
                }
            }
        }
        return false
    }

    private fun canMove(fromIndex: Int, toIndex: Int, itemIndex: InOut<Int>, count: Int): Boolean? {
        if (fromIndex >= itemIndex.value && fromIndex < itemIndex.value + count) {
            return toIndex >= itemIndex.value && toIndex < itemIndex.value + count
        } else {
            itemIndex.value += count
            return null
        }
    }

    private sealed class Content {
        class ItemsCase(val associated0: Int, val associated1: ((IntSet, Int) -> Unit)?): Content() {
        }
        class ObjectItemsCase(val associated0: RandomAccessCollection<Any>, val associated1: ((IntSet, Int) -> Unit)?): Content() {
        }
        class ObjectBindingItemsCase(val associated0: Binding<RandomAccessCollection<Any>>, val associated1: ((IntSet, Int) -> Unit)?): Content() {
        }
        class SectionHeaderCase: Content() {
        }
        class SectionFooterCase: Content() {
        }

        companion object {
            fun items(associated0: Int, associated1: ((IntSet, Int) -> Unit)?): Content = ItemsCase(associated0, associated1)
            fun objectItems(associated0: RandomAccessCollection<Any>, associated1: ((IntSet, Int) -> Unit)?): Content = ObjectItemsCase(associated0, associated1)
            fun objectBindingItems(associated0: Binding<RandomAccessCollection<Any>>, associated1: ((IntSet, Int) -> Unit)?): Content = ObjectBindingItemsCase(associated0, associated1)
            val sectionHeader: Content = SectionHeaderCase()
            val sectionFooter: Content = SectionFooterCase()
        }
    }
    private var content: Array<LazyItemFactoryContext.Content> = arrayOf()
        get() = field.sref({ this.content = it })
        set(newValue) {
            field = newValue.sref()
        }

    companion object {
    }
}

internal class LazyItemCollectingComposer: SideEffectComposer {
    internal val views: MutableList<View> = mutableListOf() // Use MutableList to avoid copies

    @Composable
    override fun Compose(view: View, context: (Boolean) -> ComposeContext): ComposeResult {
        val matchtarget_0 = view as? LazyItemFactory
        if (matchtarget_0 != null) {
            val factory = matchtarget_0
            factory.appendLazyItemViews(to = views, appendingContext = context(true))
        } else {
            views.add(view)
        }
        return ComposeResult.ok
    }

    internal constructor(compose: @Composable (View, (Boolean) -> ComposeContext) -> ComposeResult): super(compose) {
    }

    internal constructor(): super() {
    }

    companion object: SideEffectComposer.CompanionClass() {
    }
}

/// Add to lazy items to render a section header.
internal class LazySectionHeader: View, LazyItemFactory {
    internal val content: View

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        content.Compose(context = context)
    }

    @Composable
    override fun appendLazyItemViews(to: MutableList<View>, appendingContext: ComposeContext): ComposeResult {
        val views = to
        views.add(this)
        return ComposeResult.ok
    }

    override fun composeLazyItems(context: LazyItemFactoryContext): Unit = context.sectionHeader(content)

    constructor(content: View) {
        this.content = content.sref()
    }
}

/// Add to lazy items to render a section footer.
internal class LazySectionFooter: View, LazyItemFactory {
    internal val content: View

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        content.Compose(context = context)
    }

    @Composable
    override fun appendLazyItemViews(to: MutableList<View>, appendingContext: ComposeContext): ComposeResult {
        val views = to
        views.add(this)
        return ComposeResult.ok
    }

    override fun composeLazyItems(context: LazyItemFactoryContext): Unit = context.sectionFooter(content)

    constructor(content: View) {
        this.content = content.sref()
    }
}

