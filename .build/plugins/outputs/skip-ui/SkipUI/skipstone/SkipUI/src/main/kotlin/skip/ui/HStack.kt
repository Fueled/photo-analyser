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
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class HStack: View {
    internal val alignment: VerticalAlignment
    internal val spacing: Double?
    internal val content: ComposeBuilder

    constructor(alignment: VerticalAlignment = VerticalAlignment.center, spacing: Double? = null, content: () -> View) {
        this.alignment = alignment
        this.spacing = spacing
        this.content = ComposeBuilder.from(content)
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val rowAlignment = alignment.asComposeAlignment()
        val rowArrangement = Arrangement.spacedBy((spacing ?: 8.0).dp, alignment = androidx.compose.ui.Alignment.CenterHorizontally)

        val views = content.collectViews(context = context)
        val idMap: (View) -> Any? = { it ->
            TagModifierView.strip(from = it, role = ComposeModifierRole.id)?.value
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
            val contentContext = context.content()
            ComposeContainer(axis = Axis.horizontal, modifier = context.modifier) { modifier ->
                Row(modifier = modifier, horizontalArrangement = rowArrangement, verticalAlignment = rowAlignment) { ->
                    val fillWidthModifier = Modifier.weight(1.0f) // Only available in Row context
                    EnvironmentValues.shared.setValues({ it -> it.set_fillWidthModifier(fillWidthModifier) }, in_ = { ->
                        views.forEach { it -> it.Compose(context = contentContext) }
                    })
                }
            }
        } else {
            ComposeContainer(axis = Axis.horizontal, modifier = context.modifier) { modifier ->
                AnimatedContent(modifier = modifier, targetState = views, transitionSpec = { ->
                    EnterTransition.None togetherWith ExitTransition.None
                }, contentKey = { it -> it.map(idMap) }, content = { state ->
                    val animation = Animation.current(isAnimating = transition.isRunning)
                    if (animation == null) {
                        rememberedNewIds.clear()
                    }
                    Row(horizontalArrangement = rowArrangement, verticalAlignment = rowAlignment) { ->
                        val fillWidthModifier = Modifier.weight(1.0f) // Only available in Row context
                        EnvironmentValues.shared.setValues({ it -> it.set_fillWidthModifier(fillWidthModifier) }, in_ = { ->
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
                                view.Compose(context = context.content(modifier = modifier))
                            }
                        })
                    }
                }, label = "HStack")
            }
        }
    }

    companion object {
    }
}

