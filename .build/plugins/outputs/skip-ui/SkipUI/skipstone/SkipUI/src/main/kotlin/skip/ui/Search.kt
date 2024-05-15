// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class SearchFieldPlacement: RawRepresentable<Int>, Sendable {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    enum class NavigationBarDrawerDisplayMode: Sendable {
        automatic,
        always;

        companion object {
        }
    }

    companion object {

        val automatic = SearchFieldPlacement(rawValue = 0)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val toolbar = SearchFieldPlacement(rawValue = 1)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val sidebar = SearchFieldPlacement(rawValue = 2)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val navigationBarDrawer = SearchFieldPlacement(rawValue = 3)

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun navigationBarDrawer(displayMode: SearchFieldPlacement.NavigationBarDrawerDisplayMode): SearchFieldPlacement = SearchFieldPlacement(rawValue = 4)
    }
}

internal val searchFieldHeight = 56.0

/// Renders a search field.
@ExperimentalMaterial3Api
@Composable
internal fun SearchField(state: SearchableState, context: ComposeContext) {
    val colors = TextField.colors(context = context)
    val disabledTextColor = TextField.textColor(enabled = false, context = context)
    val prompt = state.prompt ?: Text(verbatim = stringResource(android.R.string.search_go))
    val focusManager = LocalFocusManager.current.sref()
    val focusRequester = remember { -> FocusRequester() }
    val contentContext = context.content()
    val keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    val submitState = OnSubmitState(triggers = SubmitTriggers.search) { ->
        if (state.text.wrappedValue.isEmpty == false) {
            focusManager.clearFocus()
            state.submitState?.let { searchableSubmitState ->
                searchableSubmitState.onSubmit(trigger = SubmitTriggers.search)
            }
        }
    }
    val keyboardActions = KeyboardActions(submitState)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = context.modifier) { ->
        val isFocused = remember { -> mutableStateOf(false) }
        OutlinedTextField(value = state.text.wrappedValue, onValueChange = { it -> state.text.wrappedValue = it }, modifier = Modifier.weight(1.0f).focusRequester(focusRequester).onFocusChanged { it ->
            if (it.isFocused) {
                state.isSearching.value = true
            }
        }, placeholder = { -> TextField.Placeholder(prompt = prompt, context = contentContext) }, leadingIcon = { -> Icon(imageVector = Icons.Outlined.Search, tint = disabledTextColor, contentDescription = null) }, trailingIcon = { ->
            if (state.text.wrappedValue.isEmpty == false) {
                Icon(imageVector = Icons.Outlined.Clear, tint = disabledTextColor, contentDescription = "Clear", modifier = Modifier.clickable { ->
                    state.text.wrappedValue = ""
                    focusRequester.requestFocus()
                })
            }
        }, keyboardOptions = keyboardOptions, keyboardActions = keyboardActions, singleLine = true, colors = colors)
        AnimatedVisibility(visible = state.isSearching.value == true) { ->
            ComposeTextButton(label = Text(verbatim = stringResource(android.R.string.cancel)), context = contentContext) { ->
                state.text.wrappedValue = ""
                focusManager.clearFocus()
                state.isSearching.value = false
            }
        }
    }
}

/// Searchable state placed in the environment.
internal class SearchableState {
    internal val text: Binding<String>
    internal val prompt: Text?
    internal val submitState: OnSubmitState?
    internal val isSearching: MutableState<Boolean>
    internal val isOnNavigationStack: Boolean

    constructor(text: Binding<String>, prompt: Text? = null, submitState: OnSubmitState? = null, isSearching: MutableState<Boolean>, isOnNavigationStack: Boolean) {
        this.text = text.sref()
        this.prompt = prompt
        this.submitState = submitState
        this.isSearching = isSearching.sref()
        this.isOnNavigationStack = isOnNavigationStack
    }
}

/// Used by the `NavigationStack` to scroll the search field with screen content.
internal class SearchFieldScrollConnection: NestedScrollConnection {
    internal val heightPx: Float
    internal var offsetPx: MutableState<Float>
        get() = field.sref({ this.offsetPx = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal constructor(heightPx: Float, offsetPx: MutableState<Float>) {
        this.heightPx = heightPx
        this.offsetPx = offsetPx
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (available.y > 0.0f) {
            return Offset.Zero.sref()
        }
        // Consume content scrolling downward until the search field is pushed up under the nav bar
        val previousOffset = offsetPx.value.sref()
        offsetPx.value = min(0.0f, max(Float(-heightPx), offsetPx.value + available.y))
        return Offset(x = 0.0f, y = offsetPx.value - previousOffset)
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        if (available.y <= 0.0f) {
            return Offset.Zero.sref()
        }
        // Consume scrolling to top until the search field is fully expanded
        val previousOffset = offsetPx.value.sref()
        offsetPx.value = min(0.0f, max(Float(-heightPx), offsetPx.value + available.y))
        return Offset(x = 0.0f, y = offsetPx.value - previousOffset)
    }
}

