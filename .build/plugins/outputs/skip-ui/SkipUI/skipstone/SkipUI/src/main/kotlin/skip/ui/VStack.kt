// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class VStack: View {
    internal val alignment: HorizontalAlignment
    internal val spacing: Double?
    internal val content: ComposeBuilder

    constructor(alignment: HorizontalAlignment = HorizontalAlignment.center, spacing: Double? = null, content: () -> View) {
        this.alignment = alignment
        this.spacing = spacing
        this.content = ComposeBuilder.from(content)
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val columnAlignment = alignment.asComposeAlignment()
        val composer: VStackComposer?
        val columnArrangement: Arrangement.Vertical
        if (spacing != null) {
            composer = null
            columnArrangement = Arrangement.spacedBy(spacing.dp, alignment = androidx.compose.ui.Alignment.CenterVertically)
        } else {
            composer = VStackComposer()
            columnArrangement = Arrangement.spacedBy(0.dp, alignment = androidx.compose.ui.Alignment.CenterVertically)
        }

        val views = content.collectViews(context = context)
        val idMap: (View) -> Any? = { it ->
            TagModifierView.strip(from = it.sref(), role = ComposeModifierRole.id)?.value
        }
        val ids = views.compactMap(idMap)
        val rememberedIds = remember { -> mutableSetOf<Any>() }
        val newIds = ids.filter { it -> !rememberedIds.contains(it) }
        val rememberedNewIds = remember { -> mutableSetOf<Any>() }

        rememberedNewIds.addAll(newIds)
        rememberedIds.clear()
        rememberedIds.addAll(ids)

        if (ids.count < views.count) {
            rememberedNewIds.clear()
            val contentContext = context.content(composer = composer)
            ComposeContainer(axis = Axis.vertical, modifier = context.modifier) { modifier ->
                Column(modifier = modifier, verticalArrangement = columnArrangement, horizontalAlignment = columnAlignment) { ->
                    val fillHeightModifier = Modifier.weight(1.0f) // Only available in Column context
                    EnvironmentValues.shared.setValues({ it -> it.set_fillHeightModifier(fillHeightModifier) }, in_ = { ->
                        composer?.willCompose()
                        views.forEach { it -> it.Compose(context = contentContext) }
                        composer?.didCompose(result = ComposeResult.ok)
                    })
                }
            }
        } else {
            ComposeContainer(axis = Axis.horizontal, modifier = context.modifier) { modifier ->
                AnimatedContent(modifier = modifier, targetState = views, transitionSpec = { ->
                    EnterTransition.None togetherWith ExitTransition.None
                }, contentKey = { it -> it.map(idMap) }, content = { state ->
                    val animation = Animation.current(isAnimating = this.transition.isRunning)
                    if (animation == null) {
                        rememberedNewIds.clear()
                    }
                    Column(verticalArrangement = columnArrangement, horizontalAlignment = columnAlignment) { ->
                        val fillHeightModifier = Modifier.weight(1.0f) // Only available in Column context
                        EnvironmentValues.shared.setValues({ it -> it.set_fillHeightModifier(fillHeightModifier) }, in_ = { ->
                            composer?.willCompose()
                            for (view in state.sref()) {
                                val id = idMap(view)
                                var modifier: Modifier = Modifier
                                if ((animation != null) && (newIds.contains(id) || rememberedNewIds.contains(id) || !ids.contains(id))) {
                                    val transition = TransitionModifierView.transition(for_ = view) ?: OpacityTransition.shared
                                    val spec = animation.asAnimationSpec()
                                    val enter = transition.asEnterTransition(spec = spec)
                                    val exit = transition.asExitTransition(spec = spec)
                                    modifier = modifier.animateEnterExit(enter = enter, exit = exit)
                                }
                                val contentContext = context.content(modifier = modifier, composer = composer)
                                view.Compose(context = contentContext)
                            }
                            composer?.didCompose(result = ComposeResult.ok)
                        })
                    }
                }, label = "VStack")
            }
        }
    }

    companion object {
    }
}

internal class VStackComposer: RenderingComposer {

    private var lastViewWasText: Boolean? = null

    override fun willCompose() {
        lastViewWasText = null
    }

    @Composable
    override fun Compose(view: View, context: (Boolean) -> ComposeContext) {
        if (view.isSwiftUIEmptyView) {
            return
        }
        // If the Text has spacing modifiers, no longer special case its spacing
        val isText = view.strippingModifiers(until = { it -> it == ComposeModifierRole.spacing }) { it -> it is Text }
        var contentContext = context(false)
        lastViewWasText?.let { lastViewWasText ->
            val spacing = if (lastViewWasText && isText) Companion.textSpacing else Companion.defaultSpacing
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(spacing.dp))
        }
        view.ComposeContent(context = contentContext)
        lastViewWasText = isText
    }

    internal constructor(compose: @Composable (View, (Boolean) -> ComposeContext) -> Unit): super(compose) {
    }

    internal constructor(): super() {
    }

    companion object: RenderingComposer.CompanionClass() {
        private val defaultSpacing = 8.0
        // SwiftUI spaces adaptively based on font, etc, but this is at least closer to SwiftUI than our defaultSpacing
        private val textSpacing = 3.0
    }
}

