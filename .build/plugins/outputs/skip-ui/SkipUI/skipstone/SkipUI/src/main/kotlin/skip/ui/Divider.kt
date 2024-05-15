// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Divider: View {
    constructor() {
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val dividerColor = androidx.compose.ui.graphics.Color.LightGray.sref()
        val modifier: Modifier
        when (EnvironmentValues.shared._layoutAxis) {
            Axis.horizontal -> {
                // If in a horizontal container, create a vertical divider
                modifier = Modifier.width(1.dp).then(context.modifier.fillHeight())
            }
            Axis.vertical, null -> modifier = context.modifier
        }
        androidx.compose.material3.Divider(modifier = modifier, color = dividerColor)
    }

    companion object {
    }
}
