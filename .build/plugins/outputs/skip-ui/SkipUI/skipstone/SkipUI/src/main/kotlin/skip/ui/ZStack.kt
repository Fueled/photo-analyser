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
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

class ZStack: View {
    internal val alignment: Alignment
    internal val content: ComposeBuilder

    constructor(alignment: Alignment = Alignment.center, content: () -> View) {
        this.alignment = alignment.sref()
        this.content = ComposeBuilder.from(content)
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val views = content.collectViews(context = context)
        val idMap: (View) -> Any? = { it ->
            TagModifierView.strip(from = it.sref(), role = ComposeModifierRole.id)?.value
        }
        val ids = views.compactMap(transform = idMap)
        val rememberedIds = remember { -> mutableSetOf<Any>() }
        val newIds = ids.filter { it -> !rememberedIds.contains(it) }
        val rememberedNewIds = remember { -> mutableSetOf<Any>() }

        rememberedNewIds.addAll(newIds)
        rememberedIds.clear()
        rememberedIds.addAll(ids)

        if (ids.count < views.count) {
            rememberedNewIds.clear()
            val contentContext = context.content()
            ComposeContainer(eraseAxis = true, modifier = context.modifier) { modifier ->
                Box(modifier = modifier, contentAlignment = alignment.asComposeAlignment()) { ->
                    EnvironmentValues.shared.setValues({ it ->
                        // The ComposeContainer uses the presence of these modifiers to influence container expansion behavior
                        it.set_fillWidthModifier(Modifier)
                        it.set_fillHeightModifier(Modifier)
                    }, in_ = { ->
                        views.forEach { it -> it.Compose(context = contentContext) }
                    })
                }
            }
        } else {
            ComposeContainer(eraseAxis = true, modifier = context.modifier) { modifier ->
                AnimatedContent(modifier = modifier, targetState = views, transitionSpec = { ->
                    EnterTransition.None togetherWith ExitTransition.None
                }, contentKey = { it -> it.map(idMap) }, content = { state ->
                    val animation = Animation.current(isAnimating = transition.isRunning)
                    if (animation == null) {
                        rememberedNewIds.clear()
                    }
                    Box(contentAlignment = alignment.asComposeAlignment()) { ->
                        EnvironmentValues.shared.setValues({ it ->
                            // The ComposeContainer uses the presence of these modifiers to influence container expansion behavior
                            it.set_fillWidthModifier(Modifier)
                            it.set_fillHeightModifier(Modifier)
                        }, in_ = { ->
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
                }, label = "ZStack")
            }
        }
    }

    companion object {
    }
}

