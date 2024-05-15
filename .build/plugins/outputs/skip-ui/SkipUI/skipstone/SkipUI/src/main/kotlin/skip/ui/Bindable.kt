// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

// Model Bindable as a class rather than struct to avoid copy overhead on mutation
class Bindable<Value> {
    constructor(wrappedValue: Value) {
        this.wrappedValue = wrappedValue
    }

    constructor(wrappedValue: Value, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.wrappedValue = wrappedValue
    }

    var wrappedValue: Value
        get() = field.sref({ this.wrappedValue = it })
        set(newValue) {
            field = newValue.sref()
        }

    val projectedValue: Bindable<Value>
        get() = Bindable(wrappedValue = wrappedValue)

    companion object {
    }
}
