// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.model

import skip.lib.*


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/// We model properties of `@Observable` types as if they had this synthetic `@Observed` property wrapper.
/// Like `Published`, it uses `MutableState` to tie into Compose's observation system.
class Observed<Value>: StateTracker {
    constructor(wrappedValue: Value) {
        _wrappedValue = wrappedValue
        StateTracking.register(this)
    }

    var wrappedValue: Value
        get() {
            val matchtarget_0 = projectedValue
            if (matchtarget_0 != null) {
                val projectedValue = matchtarget_0
                return projectedValue.value.sref({ this.wrappedValue = it })
            } else {
                return _wrappedValue.sref({ this.wrappedValue = it })
            }
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            projectedValue.sref()?.let { projectedValue ->
                projectedValue.value = newValue
            }
            _wrappedValue = newValue
        }
    private var _wrappedValue: Value
        get() = field.sref({ this._wrappedValue = it })
        set(newValue) {
            field = newValue.sref()
        }

    var projectedValue: MutableState<Value>? = null
        get() = field.sref({ this.projectedValue = it })
        set(newValue) {
            field = newValue.sref()
        }

    override fun trackState() {
        // Once we create our internal MutableState, reads and writes will be tracked by Compose
        if (projectedValue == null) {
            projectedValue = mutableStateOf(_wrappedValue)
        }
    }

    companion object {
    }
}


