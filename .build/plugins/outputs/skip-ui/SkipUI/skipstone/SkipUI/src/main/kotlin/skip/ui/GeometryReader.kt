// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

class GeometryReader: View {
    val content: (GeometryProxy) -> View

    constructor(content: (GeometryProxy) -> View) {
        this.content = content
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val rememberedGlobalFramePx = remember { -> mutableStateOf<Rect?>(null) }
        Box(modifier = context.modifier.fillSize(expandContainer = false).onGloballyPositioned { it -> rememberedGlobalFramePx.value = it.boundsInRoot() }) { ->
            rememberedGlobalFramePx.value.sref()?.let { globalFramePx ->
                val proxy = GeometryProxy(globalFramePx = globalFramePx, density = LocalDensity.current)
                content(proxy).Compose(context.content())
            }
        }
    }

    companion object {
    }
}
