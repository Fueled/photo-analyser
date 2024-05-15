// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Set

import skip.foundation.*

interface Scene {

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <D, R> backgroundTask(task: BackgroundTask<D, R>, action: suspend (D) -> R): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun commands(content: () -> Any): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun commandsRemoved(): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun commandsReplaced(content: () -> Any): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defaultAppStorage(store: UserDefaults): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defaultSize(size: CGSize): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun defaultSize(width: Double, height: Double): Scene = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun handlesExternalEvents(matching: Set<String>): Scene {
        val conditions = matching
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> onChange(of: V, perform: (V) -> Unit): Scene {
        val value = of
        val action = perform
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> onChange(of: V, initial: Boolean = false, action: (V, V) -> Unit): Scene {
        val value = of
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun <V> onChange(of: V, initial: Boolean = false, action: () -> Unit): Scene {
        val value = of
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun windowResizability(resizability: WindowResizability): Scene = this.sref()
}

class ScenePadding: Sendable {

    override fun equals(other: Any?): Boolean = other is ScenePadding

    companion object {
        val minimum = ScenePadding()
    }
}

sealed class ScenePhase(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Comparable<ScenePhase>, Sendable, RawRepresentable<Int> {
    class BackgroundCase: ScenePhase(0) {
    }
    class InactiveCase: ScenePhase(1) {
    }
    class ActiveCase: ScenePhase(2) {
    }

    override fun compareTo(other: ScenePhase): Int {
        if (this == other) return 0
        fun islessthan(a: ScenePhase, b: ScenePhase): Boolean {
            return a.rawValue < b.rawValue
        }
        return if (islessthan(this, other)) -1 else 1
    }

    companion object {
        val background: ScenePhase = BackgroundCase()
        val inactive: ScenePhase = InactiveCase()
        val active: ScenePhase = ActiveCase()
    }
}

fun ScenePhase(rawValue: Int): ScenePhase? {
    return when (rawValue) {
        0 -> ScenePhase.background
        1 -> ScenePhase.inactive
        2 -> ScenePhase.active
        else -> null
    }
}
