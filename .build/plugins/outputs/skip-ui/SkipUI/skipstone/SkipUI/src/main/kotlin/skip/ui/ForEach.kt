// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array
import skip.lib.MutableCollection

import skip.foundation.*
import androidx.compose.runtime.Composable

class ForEach: View, LazyItemFactory {
    internal val identifier: ((Any) -> AnyHashable)?
    internal val indexRange: IntRange?
    internal val indexedContent: ((Int) -> View)?
    internal val objects: RandomAccessCollection<Any>?
    internal val objectContent: ((Any) -> View)?
    internal val objectsBinding: Binding<RandomAccessCollection<Any>>?
    internal val objectsBindingContent: ((Binding<RandomAccessCollection<Any>>, Int) -> View)?
    internal val editActions: EditActions
    internal var onDeleteAction: ((IntSet) -> Unit)? = null
    internal var onMoveAction: ((IntSet, Int) -> Unit)? = null

    internal constructor(identifier: ((Any) -> AnyHashable)? = null, indexRange: IntRange? = null, indexedContent: ((Int) -> View)? = null, objects: RandomAccessCollection<Any>? = null, objectContent: ((Any) -> View)? = null, objectsBinding: Binding<RandomAccessCollection<Any>>? = null, objectsBindingContent: ((Binding<RandomAccessCollection<Any>>, Int) -> View)? = null, editActions: EditActions = EditActions.of()) {
        this.identifier = identifier
        this.indexRange = indexRange
        this.indexedContent = indexedContent
        this.objects = objects.sref()
        this.objectContent = objectContent
        this.objectsBinding = objectsBinding.sref()
        this.objectsBindingContent = objectsBindingContent
        this.editActions = editActions.sref()
    }

    fun onDelete(perform: ((IntSet) -> Unit)?): ForEach {
        val action = perform
        onDeleteAction = action
        return this
    }

    fun onMove(perform: ((IntSet, Int) -> Unit)?): ForEach {
        val action = perform
        onMoveAction = action
        return this
    }

    @Composable
    override fun Compose(context: ComposeContext): ComposeResult {
        // We typically want to be transparent and act as though our loop were unrolled. The exception is when we need
        // to act as a lazy item factory
        if (context.composer is LazyItemCollectingComposer) {
            return super.Compose(context = context)
        } else {
            ComposeContent(context = context)
            return ComposeResult.ok
        }
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val isTagging = EnvironmentValues.shared._placement.contains(ViewPlacement.tagged)
        if (indexRange != null) {
            for (index in indexRange) {
                var views = collectViews(from = indexedContent!!(index), context = context)
                if (isTagging) {
                    views = taggedViews(for_ = views, defaultTag = index, context = context)
                }
                views.forEach { it -> it.Compose(context = context) }
            }
        } else if (objects != null) {
            for (object_ in objects.sref()) {
                var views = collectViews(from = objectContent!!(object_), context = context)
                if (isTagging) {
                    identifier?.let { identifier ->
                        views = taggedViews(for_ = views, defaultTag = identifier(object_), context = context)
                    }
                }
                views.forEach { it -> it.Compose(context = context) }
            }
        } else if (objectsBinding != null) {
            val objects = objectsBinding.wrappedValue.sref()
            for (i in 0 until objects.count) {
                var views = collectViews(from = objectsBindingContent!!(objectsBinding, i), context = context)
                if (isTagging) {
                    identifier?.let { identifier ->
                        views = taggedViews(for_ = views, defaultTag = identifier(objects[i]), context = context)
                    }
                }
                views.forEach { it -> it.Compose(context = context) }
            }
        }
    }

    @Composable
    private fun taggedViews(for_: Array<View>, defaultTag: Any, context: ComposeContext): Array<View> {
        val views = for_
        return views.map l@{ view ->
            val matchtarget_0 = TagModifierView.strip(from = view, role = ComposeModifierRole.tag)
            if (matchtarget_0 != null) {
                val taggedView = matchtarget_0
                return@l taggedView
            } else {
                return@l TagModifierView(view = view, value = defaultTag, role = ComposeModifierRole.tag)
            }
        }
    }

    @Composable
    override fun appendLazyItemViews(to: MutableList<View>, appendingContext: ComposeContext): ComposeResult {
        val views = to
        // ForEach views might contain nested lazy item factories such as Sections or other ForEach instances. They also
        // might contain more than one view per iteration, which isn't supported by Compose lazy processing. We execute
        // our content closure for the first item in the ForEach and examine its content to see if it should be unrolled
        // If it should, we perform the full ForEach to append all items. If not, we append ourselves instead so that we
        // can take advantage of Compose's ability to specify ranges of items
        var isFirstView = true
        if (indexRange != null) {
            for (index in indexRange) {
                val contentViews = collectViews(from = indexedContent!!(index), context = appendingContext)
                if (!isUnrollRequired(contentViews = contentViews, isFirstView = isFirstView, context = appendingContext)) {
                    views.add(this)
                    return ComposeResult.ok
                } else {
                    isFirstView = false
                }
                contentViews.forEach { it -> it.Compose(appendingContext) }
            }
        } else if (objects != null) {
            for (object_ in objects.sref()) {
                val contentViews = collectViews(from = objectContent!!(object_), context = appendingContext)
                if (!isUnrollRequired(contentViews = contentViews, isFirstView = isFirstView, context = appendingContext)) {
                    views.add(this)
                    return ComposeResult.ok
                } else {
                    isFirstView = false
                }
                contentViews.forEach { it -> it.Compose(appendingContext) }
            }
        } else if (objectsBinding != null) {
            for (i in 0 until objectsBinding.wrappedValue.count) {
                val contentViews = collectViews(from = objectsBindingContent!!(objectsBinding, i), context = appendingContext)
                if (!isUnrollRequired(contentViews = contentViews, isFirstView = isFirstView, context = appendingContext)) {
                    views.add(this)
                    return ComposeResult.ok
                } else {
                    isFirstView = false
                }
                contentViews.forEach { it -> it.Compose(appendingContext) }
            }
        }
        return ComposeResult.ok
    }

    @Composable
    private fun isUnrollRequired(contentViews: Array<View>, isFirstView: Boolean, context: ComposeContext): Boolean {
        if (!isFirstView) {
            return true
        }
        // We have to unroll if the ForEach body contains multiple views. We also unroll if this is
        // e.g. a ForEach of Sections which each append lazy items
        return contentViews.count > 1 || contentViews.first is LazyItemFactory
    }

    override fun composeLazyItems(context: LazyItemFactoryContext) {
        if (indexRange != null) {
            context.indexedItems(indexRange, identifier, onDeleteAction, onMoveAction, indexedContent!!)
        } else if (objects != null) {
            context.objectItems(objects, identifier!!, onDeleteAction, onMoveAction, objectContent!!)
        } else if (objectsBinding != null) {
            context.objectBindingItems(objectsBinding, identifier!!, editActions, onDeleteAction, onMoveAction, objectsBindingContent!!)
        }
    }

    @Composable
    private fun collectViews(from: View, context: ComposeContext): Array<View> {
        val view = from
        return ((view as? ComposeBuilder)?.collectViews(context = context) ?: arrayOf(view)).sref()
    }

    companion object {
    }
}


// Kotlin does not support generic constructor parameters, so we have to model many ForEach constructors as functions

//extension ForEach where ID == Data.Element.ID, Content : AccessibilityRotorContent, Data.Element : Identifiable {
//    public init(_ data: Data, @AccessibilityRotorContentBuilder content: @escaping (Data.Element) -> Content) { fatalError() }
//}
fun <D> ForEach(data: RandomAccessCollection<D>, content: (D) -> View): ForEach {
    return ForEach(identifier = { it -> (it as Identifiable<Hashable>).id }, objects = data as RandomAccessCollection<Any>, objectContent = { it -> content(it as D) })
}

//extension ForEach where Content : AccessibilityRotorContent {
//    public init(_ data: Data, id: KeyPath<Data.Element, ID>, @AccessibilityRotorContentBuilder content: @escaping (Data.Element) -> Content) { fatalError() }
//}
fun <D> ForEach(data: RandomAccessCollection<D>, id: (D) -> AnyHashable, content: (D) -> View): ForEach {
    return ForEach(identifier = { it -> id(it as D) }, objects = data as RandomAccessCollection<Any>, objectContent = { it -> content(it as D) })
}
fun ForEach(data: IntRange, id: ((Int) -> AnyHashable)? = null, content: (Int) -> View): ForEach {
    return ForEach(identifier = if (id == null) null else { it -> id!!(it as Int) }, indexRange = data, indexedContent = content)
}

//extension ForEach {
//  public init<C, R>(_ data: Binding<C>, editActions: EditActions /* <C> */, @ViewBuilder content: @escaping (Binding<C.Element>) -> R) where Data == IndexedIdentifierCollection<C, ID>, ID == C.Element.ID, Content == EditableCollectionContent<R, C>, C : MutableCollection, C : RandomAccessCollection, R : View, C.Element : Identifiable, C.Index : Hashable
//}
fun <C, E> ForEach(data: Binding<C>, editActions: EditActions = EditActions.of(), content: (Binding<E>) -> View): ForEach where C: RandomAccessCollection<E> {
    return ForEach(identifier = { it -> (it as Identifiable<Hashable>).id }, objectsBinding = data as Binding<RandomAccessCollection<Any>>, objectsBindingContent = l@{ data, index ->
        val binding = Binding<E>(get = { -> data.wrappedValue[index] as E }, set = { it -> (data.wrappedValue as skip.lib.MutableCollection<E>)[index] = it.sref() })
        return@l content(binding)
    }, editActions = editActions)
}

//extension ForEach {
//    public init<C, R>(_ data: Binding<C>, id: KeyPath<C.Element, ID>, editActions: EditActions /* <C> */, @ViewBuilder content: @escaping (Binding<C.Element>) -> R) where Data == IndexedIdentifierCollection<C, ID>, Content == EditableCollectionContent<R, C>, C : MutableCollection, C : RandomAccessCollection, R : View, C.Index : Hashable { fatalError() }
//}
fun <C, E> ForEach(data: Binding<C>, id: (E) -> AnyHashable, editActions: EditActions = EditActions.of(), content: (Binding<E>) -> View): ForEach where C: RandomAccessCollection<E> {
    return ForEach(identifier = { it -> id(it as E) }, objectsBinding = data as Binding<RandomAccessCollection<Any>>, objectsBindingContent = l@{ data, index ->
        val binding = Binding<E>(get = { -> data.wrappedValue[index] as E }, set = { it -> (data.wrappedValue as skip.lib.MutableCollection<E>)[index] = it.sref() })
        return@l content(binding)
    }, editActions = editActions)
}

