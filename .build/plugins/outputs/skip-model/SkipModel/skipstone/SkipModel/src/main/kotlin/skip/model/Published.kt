// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.model

import skip.lib.*


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/// We model `@Published` properties as Kotlin `MutableState` so that Compose will track its values.
class Published<Value>: StateTracker {
    private val subject: PropertySubject<Value, Never>
    private var state: MutableState<Value>? = null
        get() = field.sref({ this.state = it })
        set(newValue) {
            field = newValue.sref()
        }

    constructor(wrappedValue: Value) {
        subject = PropertySubject(initialValue = wrappedValue)
        StateTracking.register(this)
    }

    var wrappedValue: Value
        get() {
            val matchtarget_0 = state
            if (matchtarget_0 != null) {
                val state = matchtarget_0
                return state.value.sref({ this.wrappedValue = it })
            } else {
                return subject.current.sref({ this.wrappedValue = it })
            }
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            subject.send(newValue)
            state?.value = newValue
        }

    val projectedValue: Publisher<Value, Never>
        get() = subject

    override fun trackState() {
        // Once we create our internal MutableState, reads and writes will be tracked by Compose
        if (state == null) {
            state = mutableStateOf(subject.current)
        }
    }

    companion object {
    }
}

/// Property publishers immediately send the current value.
private open class PropertySubject<Output, Failure>: Subject<Output, Failure> {
    private val helper: SubjectHelper<Output, Failure> = SubjectHelper<Output, Failure>()

    internal constructor(initialValue: Output) {
        this.current = initialValue
    }

    internal var current: Output
        get() = field.sref({ this.current = it })
        private set(newValue) {
            field = newValue.sref()
        }

    override fun sink(receiveValue: (Output) -> Unit): AnyCancellable {
        val cancellable = helper.sink(receiveValue)
        current.sref()?.let { current ->
            receiveValue(current)
        }
        return cancellable
    }

    override fun send(value: Output) {
        helper.send(value)
        current = value
    }
}

