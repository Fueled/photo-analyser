// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import kotlin.reflect.KClass
import skip.lib.*
import skip.lib.Array

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlin.reflect.full.companionObjectInstance

interface PreferenceKey<Value> {
}

/// Added to `PreferenceKey` companion objects.
interface PreferenceKeyCompanion<Value> {
    val defaultValue: Value
    fun reduce(value: InOut<Value>, nextValue: () -> Value)
}

/// Internal analog to `EnvironmentValues` for preferences.
///
/// Uses environment `CompositionLocals` internally.
///
/// - Seealso: `EnvironmentValues`
internal class PreferenceValues {

    /// Return a preference for the given `PreferenceKey` type.
    @Composable internal fun preference(key: KClass<*>): Preference<*>? {
        return EnvironmentValues.shared.compositionLocals[key]?.current as? Preference<*>
    }

    /// Collect the values of the given preferences while composing the given content.
    @Composable internal fun collectPreferences(preferences: Array<Preference<*>>, in_: @Composable () -> Unit) {
        val content = in_
        val provided = preferences.map { preference ->
            var compositionLocal = EnvironmentValues.shared.compositionLocals[preference.key].sref()
            if (compositionLocal == null) {
                compositionLocal = compositionLocalOf { -> Unit }
                EnvironmentValues.shared.compositionLocals[preference.key] = compositionLocal.sref()
            }
            val element = compositionLocal!! provides preference
            element
        }
        val kprovided = (provided.kotlin(nocopy = true) as MutableList<ProvidedValue<*>>).toTypedArray()

        preferences.forEach { it -> it.beginCollecting() }
        CompositionLocalProvider(*kprovided) { -> content() }
        preferences.forEach { it -> it.endCollecting() }
    }

    /// Update the value of the given preference, as if by calling .preference(key:value:).
    @Composable
    internal fun reducePreference(key: KClass<*>, value: Any) {
        val pvalue = remember { -> mutableStateOf<Any?>(null) }
        val preference = PreferenceValues.shared.preference(key = key)
        preference?.reduce(savedValue = pvalue.value, newValue = value)
        pvalue.value = value
    }

    /// Return a preference that can be used to sync the given preference across an asynchronously-composed boundary,
    /// such as a `NavHost`.
    @Composable internal fun syncingPreference(key: KClass<*>, recompose: () -> Unit): Preference<*>? {
        val preference_0 = PreferenceValues.shared.preference(key = key)
        if (preference_0 == null) {
            return null
        }

        // Remember the last collecting value for the preference. We only sync preferences that were collecting
        // during this synchronous compose phase. We might have subsequent local recompositions, though
        val collectingValue = remember { -> mutableStateOf<Any?>(null) }
        // Remember our last local value for the preference, and update the preference to that value during
        // the synchronous compose phase
        val localValue = remember { -> mutableStateOf<Any?>(null) }

        if (preference_0.isCollecting) {
            collectingValue.value = preference_0.value
            localValue.value.sref()?.let { lvalue ->
                reducePreference(key = key, value = lvalue)
            }
        } else if (collectingValue.value == null) {
            return null
        }

        // Create a local preference to use in the asynchronously-composed content. When the local preference
        // updates at the end of the composition, force the preference to recompose if our asynchronous
        // composition changed it
        val preferenceRecompose = rememberUpdatedState(preference_0.recompose)
        val syncing = Preference(key = key, initialValue = collectingValue.value!!, update = { value ->
            LaunchedEffect(value, localValue.value) { ->
                val lvalue = localValue.value.sref()
                if (value != lvalue) {
                    localValue.value = value
                    // Force a recompose on the original preference if our computed value has changed
                    if (lvalue != null || value != collectingValue.value) {
                        preferenceRecompose.value()
                    }
                }
            }
        }, recompose = recompose)
        return syncing
    }

    companion object {
        internal val shared = PreferenceValues()
    }
}

/// Used internally by our preferences system to collect preferences and recompose on change.
internal class Preference<Value> {
    internal val key: KClass<*>
    internal val update: @Composable (Value) -> Unit
    internal val recompose: () -> Unit
    private val initialValue: Value
    private var collectedValue: Value? = null
        get() = field.sref({ this.collectedValue = it })
        set(newValue) {
            field = newValue.sref()
        }

    /// Create a preference for the given `PreferenceKey` type.
    ///
    /// - Parameter update: Block to call to change the value of this preference.
    /// - Parameter recompose: Block to force a recompose of the relevant content, collecting the new value via `collectPreferences`
    internal constructor(key: KClass<*>, initialValue: Value? = null, update: @Composable (Value) -> Unit, recompose: () -> Unit) {
        this.key = key
        this.update = update
        this.recompose = recompose
        this.initialValue = (initialValue ?: (key.companionObjectInstance as PreferenceKeyCompanion<Value>).defaultValue).sref()
    }

    /// The current preference value.
    internal val value: Value
        get() = collectedValue ?: initialValue

    /// Reduce the current value and the given values.
    internal fun reduce(savedValue: Any?, newValue: Any) {
        if (isCollecting) {
            var value = this.value.sref()
            (key.companionObjectInstance as PreferenceKeyCompanion<Value>).reduce(value = InOut({ value }, { value = it }), nextValue = { -> newValue as Value })
            collectedValue = value
        } else if (savedValue != newValue) {
            recompose()
        }
    }

    /// Whether we're currently collecting this preference.
    internal var isCollecting = false
        private set

    /// Begin collecting the current value.
    ///
    /// Call this before composing content.
    internal fun beginCollecting() {
        isCollecting = true
        collectedValue = null
    }

    /// End collecting the current value.
    ///
    /// Call this after composing content.
    @Composable
    internal fun endCollecting() {
        isCollecting = false
        update(value)
    }
}

