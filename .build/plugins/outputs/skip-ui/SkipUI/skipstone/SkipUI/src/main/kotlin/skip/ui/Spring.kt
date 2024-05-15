// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.foundation.*
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec

class Spring: Sendable {
    private val animationSpec: AnimationSpec<Any>

    /// Convert this spring to a Compose animation spec.
    fun asAnimationSpec(): AnimationSpec<Any> = animationSpec.sref()

    constructor(duration: Double = 0.5, bounce: Double = 0.0, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        animationSpec = TweenSpec(durationMillis = Int(duration * 1000.0), easing = EaseInOutBack)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val duration: Double
        get() = 0.0

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val bounce: Double
        get() = 0.0

    constructor(response: Double, dampingRatio: Double) {
        animationSpec = TweenSpec(durationMillis = Int(response * 1000.0), easing = EaseInOutBack)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val response: Double
        get() = 0.0

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val dampingRatio: Double
        get() = 0.0

    constructor(mass: Double = 1.0, stiffness: Double, damping: Double, allowOverDamping: Boolean = false) {
        val dampingRatio = damping / (2.0 * sqrt(mass * stiffness))
        animationSpec = SpringSpec(dampingRatio = Float(dampingRatio), stiffness = Float(stiffness))
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val mass: Double
        get() = 0.0

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val stiffness: Double
        get() = 0.0

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val damping: Double
        get() = 0.0

    constructor(settlingDuration: Double, dampingRatio: Double, epsilon: Double = 0.001) {
        animationSpec = TweenSpec(durationMillis = Int(settlingDuration * 1000.0), easing = EaseInOutBack)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val settlingDuration: Double
        get() = 0.0

    override fun equals(other: Any?): Boolean {
        if (other !is Spring) return false
        return animationSpec == other.animationSpec
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, animationSpec)
        return result
    }

    companion object {

        val smooth: Spring
            get() = smooth(duration = 0.5, extraBounce = 0.0)

        fun smooth(duration: Double = 0.5, extraBounce: Double = 0.0): Spring = Spring(duration = duration, bounce = extraBounce)

        val snappy: Spring
            get() = snappy(duration = 0.5, extraBounce = 0.0)

        fun snappy(duration: Double = 0.5, extraBounce: Double = 0.0): Spring = Spring(duration = duration, bounce = extraBounce)

        val bouncy: Spring
            get() = bouncy(duration = 0.5, extraBounce = 0.0)

        fun bouncy(duration: Double = 0.5, extraBounce: Double = 0.0): Spring = Spring(duration = duration, bounce = extraBounce)
    }
}

enum class SpringLoadingBehavior: Sendable {
    automatic,
    enabled,
    disabled;

    companion object {
    }
}

