// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import skip.lib.*

import skip.model.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

class SubscriptionView<PublisherType, Content>: View where Content: View {
    val content: Content
    val publisher: PublisherType
    val action: (Any) -> Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(content: Content, publisher: PublisherType, action: (Any) -> Unit) {
        this.content = content.sref()
        this.publisher = publisher.sref()
        this.action = action
    }


    companion object {
    }
}
