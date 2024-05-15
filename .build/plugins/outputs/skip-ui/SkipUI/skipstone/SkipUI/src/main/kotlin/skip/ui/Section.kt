// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

// Erase generics to facilitate specialized constructor support.
class Section: View, LazyItemFactory {
    internal val header: ComposeBuilder?
    internal val footer: ComposeBuilder?
    internal val content: ComposeBuilder

    constructor(content: () -> View, header: () -> View, footer: () -> View) {
        this.header = ComposeBuilder.from(header)
        this.footer = ComposeBuilder.from(footer)
        this.content = ComposeBuilder.from(content)
    }

    constructor(content: () -> View, footer: () -> View, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.header = null
        this.footer = ComposeBuilder.from(footer)
        this.content = ComposeBuilder.from(content)
    }

    constructor(content: () -> View, header: () -> View) {
        this.header = ComposeBuilder.from(header)
        this.footer = null
        this.content = ComposeBuilder.from(content)
    }

    constructor(header: View, content: () -> View) {
        this.header = ComposeBuilder.from({ -> header })
        this.footer = null
        this.content = ComposeBuilder.from(content)
    }

    constructor(content: () -> View) {
        this.header = null
        this.footer = null
        this.content = ComposeBuilder.from(content)
    }

    constructor(titleKey: LocalizedStringKey, content: () -> View): this(content = content, header = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(titleKey).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    constructor(title: String, content: () -> View): this(content = content, header = { ->
        ComposeBuilder { composectx: ComposeContext ->
            Text(verbatim = title).Compose(composectx)
            ComposeResult.ok
        }
    }) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(titleKey: LocalizedStringKey, isExpanded: Binding<Boolean>, content: () -> View): this(titleKey, content = content) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(title: String, isExpanded: Binding<Boolean>, content: () -> View): this(title, content = content) {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(isExpanded: Binding<Boolean>, content: () -> View, header: () -> View): this(content = content, header = header) {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        content.Compose(context = context)
    }

    @Composable
    override fun appendLazyItemViews(to: MutableList<View>, appendingContext: ComposeContext): ComposeResult {
        val views = to
        views.add(LazySectionHeader(content = header ?: EmptyView()))
        content.Compose(context = appendingContext)
        views.add(LazySectionFooter(content = footer ?: EmptyView()))
        return ComposeResult.ok
    }

    override fun composeLazyItems(context: LazyItemFactoryContext) {
        // Not called because the section does not append itself as a list item view
    }

    companion object {
    }
}
