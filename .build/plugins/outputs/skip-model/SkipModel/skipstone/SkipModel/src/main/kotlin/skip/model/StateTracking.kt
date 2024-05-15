// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.model

import skip.lib.*

import android.os.Looper
import androidx.compose.runtime.Composable

/// Participate in observable state tracking.
///
/// This protocol is implemented by our observation property wrappers.
interface StateTracker {
    fun trackState()
}

/// Manage observable state tracking.
class StateTracking {


    companion object {
        private var bodyDepth = 0
        private val trackers: MutableList<StateTracker> = mutableListOf()

        /// Register a state tracker to call when state tracking should begin.
        ///
        /// If a body is executing, delays state tracking until the body completes or a new body begins executing.
        /// This is meant to avoid infinite recomposition in scenarios like the following:
        ///
        /// - Parent view `P` creates child view `V`
        /// - On construction, `V` creates observable `@StateObject` `O`
        /// - Either `O` or `V` both read and update one of `O`'s observable properties in their constructors
        ///
        /// If `O`'s properites were immediately backed by `MutableState`, that sequence would cause the state
        /// to be both read and updated in the context of `P`, causing `P` to recompose and recreate `V`, which
        /// would recreate `O` and cause the cycle to repeat.
        ///
        /// We also considered tracking view construction rather than body execution. But it's possible that `P` creates
        /// and mutates `O` before passing it to `V`, or that `V` does so in a factory function, so view construction
        /// may be too limited.
        fun register(tracker: StateTracker) {
            if (isMainThread && bodyDepth > 0) {
                trackers.add(tracker)
            } else {
                tracker.trackState()
            }
        }

        /// Push a body execution.
        fun pushBody() {
            if (isMainThread) {
                bodyDepth += 1
                activateTrackers()
            }
        }

        /// Pop a body execution.
        fun popBody() {
            if (isMainThread && bodyDepth > 0) {
                bodyDepth -= 1
                activateTrackers()
            }
        }
        private fun activateTrackers() {
            if (trackers.isEmpty()) {
                return
            }
            val trackersArray = trackers.toTypedArray()
            trackers.clear()
            for (tracker in trackersArray.sref()) {
                tracker.trackState()
            }
        }

        private val isMainThread: Boolean
            get() {
                // Looper not mocked for Roboelectric and will cause test exceptions, so use `try`
                return try { (Looper.myLooper() == Looper.getMainLooper()) } catch (_: Throwable) { null } ?: false
            }
    }
}
