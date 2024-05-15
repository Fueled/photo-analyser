// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


fun OSAllocatedUnfairLock(): OSAllocatedUnfairLock<*> = OSAllocatedUnfairLock(uncheckedState = Unit)

class OSAllocatedUnfairLock<State>: Sendable, MutableStruct {
    private var state: State
        get() = field.sref({ this.state = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    private val lock: java.util.concurrent.locks.Lock

    constructor(initialState: State, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        this.lock = java.util.concurrent.locks.ReentrantLock()
        this.state = initialState
    }

    constructor(uncheckedState: State) {
        val initialState = uncheckedState
        this.lock = java.util.concurrent.locks.ReentrantLock()
        this.state = initialState
    }

    fun lock(): Unit = lock.lock()

    fun unlock(): Unit = lock.unlock()

    fun lockIfAvailable(): Boolean = lock.tryLock()

    fun <R> withLockUnchecked(body: (InOut<State>) -> R): R = withLock(body)

    fun <R> withLockUnchecked(body: () -> R): R = withLock(body)

    fun <R> withLock(body: (InOut<State>) -> R): R {
        var deferaction_0: (() -> Unit)? = null
        try {
            lock.lock()
            deferaction_0 = {
                lock.unlock()
            }
            return body(InOut({ state }, { state = it }))
        } finally {
            deferaction_0?.invoke()
        }
    }

    fun <R> withLock(body: () -> R): R {
        var deferaction_1: (() -> Unit)? = null
        try {
            lock.lock()
            deferaction_1 = {
                lock.unlock()
            }
            return body()
        } finally {
            deferaction_1?.invoke()
        }
    }

    fun <R> withLockIfAvailableUnchecked(body: (InOut<State>) -> R): R? = withLockIfAvailable(body)

    fun <R> withLockIfAvailableUnchecked(body: () -> R): R? = withLockIfAvailable(body)

    fun <R> withLockIfAvailable(body: (InOut<State>) -> R): R? {
        var deferaction_2: (() -> Unit)? = null
        try {
            if (!lock.tryLock()) {
                return null
            }
            deferaction_2 = {
                lock.unlock()
            }
            return body(InOut({ state }, { state = it }))
        } finally {
            deferaction_2?.invoke()
        }
    }

    fun <R> withLockIfAvailable(body: () -> R): R? {
        var deferaction_3: (() -> Unit)? = null
        try {
            if (!lock.tryLock()) {
                return null
            }
            deferaction_3 = {
                lock.unlock()
            }
            return body()
        } finally {
            deferaction_3?.invoke()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun precondition(condition: Any) = Unit

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as OSAllocatedUnfairLock<State>
        this.state = copy.state
        this.lock = copy.lock
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = OSAllocatedUnfairLock<State>(this as MutableStruct)

    companion object {
    }
}

