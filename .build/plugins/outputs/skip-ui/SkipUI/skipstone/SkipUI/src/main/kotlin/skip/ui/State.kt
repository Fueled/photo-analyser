// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.model.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// Model State as a class rather than struct to mutate by reference and void copy overhead
class State<Value>: StateTracker {
    constructor(initialValue: Value, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        _wrappedValue = initialValue
        StateTracking.register(this)
    }

    constructor(wrappedValue: Value): this(initialValue = wrappedValue) {
    }

    var wrappedValue: Value
        get() {
            _wrappedValueState.sref()?.let { _wrappedValueState ->
                return _wrappedValueState.value.sref({ this.wrappedValue = it })
            }
            return _wrappedValue.sref({ this.wrappedValue = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            _wrappedValue = newValue
            _wrappedValueState?.value = _wrappedValue
        }
    private var _wrappedValue: Value
        get() = field.sref({ this._wrappedValue = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var _wrappedValueState: MutableState<Value>? = null
        get() = field.sref({ this._wrappedValueState = it })
        set(newValue) {
            field = newValue.sref()
        }

    val projectedValue: Binding<Value>
        get() {
            return Binding(get = { -> this.wrappedValue }, set = { it -> this.wrappedValue = it })
        }

    override fun trackState() {
        _wrappedValueState = mutableStateOf(_wrappedValue)
    }

    companion object {
    }
}

// extension State where Value : ExpressibleByNilLiteral {
// public init() {
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun State() {
    fatalError()
}
// }
