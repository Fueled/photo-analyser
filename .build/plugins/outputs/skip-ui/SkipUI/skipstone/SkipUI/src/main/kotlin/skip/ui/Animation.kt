// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import skip.foundation.*
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.async

internal val defaultAnimationDuration = 0.35

fun <Result> withAnimation(animation: Animation? = Animation.default, body: () -> Result): Result = Animation.withAnimation(animation, body)

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun <Result> withAnimation(animation: Animation? = Animation.default, completionCriteria: AnimationCompletionCriteria = AnimationCompletionCriteria.logicallyComplete, body: () -> Result, completion: () -> Unit): Result {
    fatalError()
}

internal class AnimationHolder {
    internal var animation: Animation? = null
        get() = field.sref({ this.animation = it })
        set(newValue) {
            field = newValue.sref()
        }
}

class Animation: Sendable {

    private val spec: AnimationSpec<Any>

    /// Convert this animation to a Compose animation spec.
    fun asAnimationSpec(): AnimationSpec<Any> = spec.sref()

    /// Whether this is an infinite animation.
    val isInfinite: Boolean
        get() = spec is InfiniteRepeatableSpec<*>

    internal constructor(spec: AnimationSpec<Any>, delay: Double = 0.0, speed: Double = 1.0) {
        this.spec = spec.sref()
    }

    fun logicallyComplete(after: Double): Animation {
        val duration = after
        return this.sref()
    }

    fun delay(delay: Double): Animation {
        val matchtarget_0 = spec as? TweenSpec<Any>
        if (matchtarget_0 != null) {
            val tweenSpec = matchtarget_0
            return Animation(spec = TweenSpec(tweenSpec.durationMillis, Int(delay * 1000.0), tweenSpec.easing))
        } else {
            val matchtarget_1 = spec as? RepeatableSpec<Any>
            if (matchtarget_1 != null) {
                val repeatableSpec = matchtarget_1
                return Animation(spec = RepeatableSpec(repeatableSpec.iterations, repeatableSpec.animation, repeatableSpec.repeatMode, StartOffset(Int(delay * 1000.0), StartOffsetType.Delay)))
            } else {
                val matchtarget_2 = spec as? InfiniteRepeatableSpec<Any>
                if (matchtarget_2 != null) {
                    val repeatableSpec = matchtarget_2
                    return Animation(spec = InfiniteRepeatableSpec(repeatableSpec.animation, repeatableSpec.repeatMode, StartOffset(Int(delay * 1000.0), StartOffsetType.Delay)))
                } else {
                    return this.sref() // Cannot delay
                }
            }
        }
    }

    fun speed(speed: Double): Animation {
        val matchtarget_3 = spec as? TweenSpec<Any>
        if (matchtarget_3 != null) {
            val tweenSpec = matchtarget_3
            return Animation(spec = TweenSpec(Int(tweenSpec.durationMillis / speed), tweenSpec.delay, tweenSpec.easing))
        } else {
            val matchtarget_4 = spec as? RepeatableSpec<Any>
            if (matchtarget_4 != null) {
                val repeatableSpec = matchtarget_4
                val matchtarget_5 = repeatableSpec.animation as? TweenSpec<Any>
                if (matchtarget_5 != null) {
                    val tweenSpec = matchtarget_5
                    val speedSpec = TweenSpec<Any>(Int(tweenSpec.durationMillis / speed), tweenSpec.delay, tweenSpec.easing)
                    return Animation(spec = RepeatableSpec(repeatableSpec.iterations, speedSpec, repeatableSpec.repeatMode, repeatableSpec.initialStartOffset))
                } else {
                    val matchtarget_6 = spec as? InfiniteRepeatableSpec<Any>
                    if (matchtarget_6 != null) {
                        val repeatableSpec = matchtarget_6
                        val matchtarget_7 = repeatableSpec.animation as? TweenSpec<Any>
                        if (matchtarget_7 != null) {
                            val tweenSpec = matchtarget_7
                            val speedSpec = TweenSpec<Any>(Int(tweenSpec.durationMillis / speed), tweenSpec.delay, tweenSpec.easing)
                            return Animation(spec = InfiniteRepeatableSpec(speedSpec, repeatableSpec.repeatMode, repeatableSpec.initialStartOffset))
                        } else {
                            return this.sref() // Cannot delay
                        }
                    } else {
                        return this.sref() // Cannot delay
                    }
                }
            } else {
                val matchtarget_6 = spec as? InfiniteRepeatableSpec<Any>
                if (matchtarget_6 != null) {
                    val repeatableSpec = matchtarget_6
                    val matchtarget_7 = repeatableSpec.animation as? TweenSpec<Any>
                    if (matchtarget_7 != null) {
                        val tweenSpec = matchtarget_7
                        val speedSpec = TweenSpec<Any>(Int(tweenSpec.durationMillis / speed), tweenSpec.delay, tweenSpec.easing)
                        return Animation(spec = InfiniteRepeatableSpec(speedSpec, repeatableSpec.repeatMode, repeatableSpec.initialStartOffset))
                    } else {
                        return this.sref() // Cannot delay
                    }
                } else {
                    return this.sref() // Cannot delay
                }
            }
        }
    }

    fun repeatCount(repeatCount: Int, autoreverses: Boolean = true): Animation {
        val matchtarget_8 = spec as? DurationBasedAnimationSpec<Any>
        if (matchtarget_8 != null) {
            val durationBasedSpec = matchtarget_8
            return Animation(spec = RepeatableSpec(iterations = repeatCount, animation = durationBasedSpec, repeatMode = if (autoreverses) RepeatMode.Reverse else RepeatMode.Restart))
        } else {
            return this.sref()
        }
    }

    fun repeatForever(autoreverses: Boolean = true): Animation {
        val matchtarget_9 = spec as? DurationBasedAnimationSpec<Any>
        if (matchtarget_9 != null) {
            val durationBasedSpec = matchtarget_9
            return Animation(spec = InfiniteRepeatableSpec(animation = durationBasedSpec, repeatMode = if (autoreverses) RepeatMode.Reverse else RepeatMode.Restart))
        } else {
            return this.sref()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Animation) return false
        return spec == other.spec
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, spec)
        return result
    }

    companion object {
        /// The current active animation, whether from the environment via `animation` or from `withAnimation`.
        @Composable
        internal fun current(isAnimating: Boolean): Animation? {
            val environmentAnimation = EnvironmentValues.shared._animation.sref()
            val animation = (environmentAnimation ?: _withAnimation).sref()

            // Update our remembered animation value if there is a new animation or the animation is complete
            val rememberedAnimationHolder = remember { -> AnimationHolder() }
            val rememberedAnimation = rememberedAnimationHolder.animation.sref()
            if (animation != null) {
                rememberedAnimationHolder.animation = animation
            } else if (!isAnimating) {
                rememberedAnimationHolder.animation = null
            }
            if (animation != null) {
                return animation.sref()
            }
            // No current animation, but if we're still animating a previous animation, use it
            return (if (isAnimating) rememberedAnimation else null).sref()
        }

        /// Internal implementation of global `withAnimation` SwiftUI function.
        internal fun <Result> withAnimation(animation: Animation? = Animation.default, body: () -> Result): Result {
            var deferaction_0: (() -> Unit)? = null
            try {
                // SwiftUI's withAnimation works as if by snapshotting the view tree at the beginning of the block,
                // snapshotting again at the end fo the block, and animating the difference with the given animation.
                // We don't have the ability to snapshot. Instead, we run the given body, which should trigger a
                // recompose. We set a global animation instance that animatable properties check via `current()`
                // so that the recompose will begin animations. We then wait for the next frame and unset the global.
                // Note that we cannot properly handle `withAnimation` nesting with different animations; instead
                // the last set animation wins
                var isNested = false
                synchronized(withAnimationLock) { ->
                    isNested = _withAnimation != null
                    _withAnimation = animation
                }
                deferaction_0 = {
                    if (!isNested) {
                        GlobalScope.async(Dispatchers.Main) { ->
                            awaitFrame()
                            synchronized(withAnimationLock) { -> _withAnimation = null }
                        }
                    }
                }
                return body()
            } finally {
                deferaction_0?.invoke()
            }
        }

        private var _withAnimation: Animation? = null
            get() = field.sref({ this._withAnimation = it })
            set(newValue) {
                field = newValue.sref()
            }
        private val withAnimationLock: java.lang.Object = java.lang.Object()

        fun spring(duration: Double = 0.5, bounce: Double = 0.0, blendDuration: Double = 0.0, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): Animation = Animation(spec = Spring(duration = duration, bounce = bounce).asAnimationSpec())

        fun spring(response: Double = 0.5, dampingFraction: Double = 0.825, blendDuration: Double = 0.0): Animation = Animation(spec = Spring(response = response, dampingRatio = dampingFraction).asAnimationSpec())

        val spring: Animation
            get() = spring(response = 0.5, dampingFraction = 0.825)

        fun interactiveSpring(response: Double = 0.15, dampingFraction: Double = 0.86, blendDuration: Double = 0.25): Animation = spring(response = response, dampingFraction = dampingFraction, blendDuration = blendDuration)

        val interactiveSpring: Animation
            get() = interactiveSpring(response = 0.15, dampingFraction = 0.86)

        fun interactiveSpring(duration: Double = 0.15, extraBounce: Double = 0.0, blendDuration: Double = 0.25, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): Animation = spring(duration = duration, bounce = extraBounce, blendDuration = blendDuration)

        val smooth: Animation
            get() = smooth(duration = 0.5, extraBounce = 0.0)

        fun smooth(duration: Double = 0.5, extraBounce: Double = 0.0): Animation = Animation(spec = Spring.smooth(duration = duration, extraBounce = extraBounce).asAnimationSpec())

        val snappy: Animation
            get() = snappy(duration = 0.5, extraBounce = 0.0)

        fun snappy(duration: Double = 0.5, extraBounce: Double = 0.0): Animation = Animation(spec = Spring.snappy(duration = duration, extraBounce = extraBounce).asAnimationSpec())

        val bouncy: Animation
            get() = bouncy(duration = 0.5, extraBounce = 0.0)

        fun bouncy(duration: Double = 0.5, extraBounce: Double = 0.0): Animation = Animation(spec = Spring.bouncy(duration = duration, extraBounce = extraBounce).asAnimationSpec())

        fun spring(spring: Spring, blendDuration: Double = 0.0): Animation = Animation(spec = spring.asAnimationSpec())

        fun interpolatingSpring(spring: Spring, initialVelocity: Double = 0.0): Animation = Animation(spec = spring.asAnimationSpec())

        fun timingCurve(curve: UnitCurve, duration: Double): Animation = Animation(spec = TweenSpec(durationMillis = Int(duration * 1000.0), easing = curve.asEasing()))

        val default: Animation
            get() {
                // WARNING: Android can't repeat non-duration-based animations, so changing the default to a spring would
                // prevent default repeatable animations
                return timingCurve(UnitCurve.easeInOut, duration = defaultAnimationDuration)
            }

        fun easeInOut(duration: Double): Animation = timingCurve(UnitCurve.easeInOut, duration = duration)

        val easeInOut: Animation
            get() = easeInOut(duration = defaultAnimationDuration)

        fun easeIn(duration: Double): Animation = timingCurve(UnitCurve.easeIn, duration = duration)

        val easeIn: Animation
            get() = easeIn(duration = defaultAnimationDuration)

        fun easeOut(duration: Double): Animation = timingCurve(UnitCurve.easeOut, duration = duration)

        val easeOut: Animation
            get() = easeOut(duration = defaultAnimationDuration)

        fun linear(duration: Double): Animation = timingCurve(UnitCurve.linear, duration = duration)

        val linear: Animation
            get() = linear(duration = defaultAnimationDuration)

        fun timingCurve(p1x: Double, p1y: Double, p2x: Double, p2y: Double, duration: Double = 0.35): Animation {
            val p1 = UnitPoint(x = p1x, y = p1y)
            val p2 = UnitPoint(x = p2x, y = p2y)
            return timingCurve(UnitCurve(startControlPoint = p1, endControlPoint = p2), duration = duration)
        }

        fun interpolatingSpring(mass: Double = 1.0, stiffness: Double, damping: Double, initialVelocity: Double = 0.0): Animation = Animation(spec = Spring(mass = mass, stiffness = stiffness, damping = damping).asAnimationSpec())

        fun interpolatingSpring(duration: Double = 0.5, bounce: Double = 0.0, initialVelocity: Double = 0.0): Animation = Animation(spec = Spring(duration = duration, bounce = bounce).asAnimationSpec())

        val interpolatingSpring: Animation
            get() = interpolatingSpring(duration = 0.5, bounce = 0.0, initialVelocity = 0.0)
    }
}

enum class AnimationCompletionCriteria: Sendable {
    logicallyComplete,
    removed;

    companion object {
    }
}

@Composable
internal fun <T, VectorT> toAnimatable(value: T, converter: TwoWayConverter<T, VectorT>, context: ComposeContext): Animatable<T, VectorT> where T: Any, VectorT: AnimationVector {
    // In order to reset infinite animations after exiting and coming back to a composition, we have to remember its initial
    // value, because the powering state value will be at its target when we return to the composition
    val resetValue = rememberSaveable(stateSaver = context.stateSaver as Saver<T?, Any>) { -> mutableStateOf<T?>(null) }
    val animatable = remember { -> Animatable(resetValue.value ?: value, converter) }
    val isAnimating = animatable.isRunning || animatable.value != animatable.targetValue
    if (isAnimating || animatable.value != value) {
        val animation = Animation.current(isAnimating = isAnimating)
        LaunchedEffect(value, animation) { ->
            if (animation != null) {
                if (animation.isInfinite) {
                    resetValue.value = animatable.value // Remember infinite animation start value
                } else {
                    resetValue.value = null
                }
                animatable.animateTo(value, animationSpec = animation.asAnimationSpec() as AnimationSpec<T>)
            } else {
                resetValue.value = null
                animatable.snapTo(value)
            }
        }
    }
    return animatable.sref()
}

/// Return an animatable version of this value.
@Composable
internal fun Float.asAnimatable(context: ComposeContext): Animatable<Float, AnimationVector1D> {
    return toAnimatable(value = this, converter = TwoWayConverter({ it -> AnimationVector1D(it) }, { it -> it.value }), context = context)
}

/// Return an animatable version of this value.
@Composable
internal fun Tuple2<Float, Float>.asAnimatable(context: ComposeContext): Animatable<Tuple2<Float, Float>, AnimationVector2D> {
    return toAnimatable(value = this, converter = TwoWayConverter({ it -> AnimationVector2D(it.element0, it.element1) }, { it -> Tuple2(it.v1, it.v2) }), context = context)
}

/// Return an animatable version of this value.
@Composable
internal fun androidx.compose.ui.graphics.Color.asAnimatable(context: ComposeContext): Animatable<androidx.compose.ui.graphics.Color, AnimationVector4D> {
    return toAnimatable(value = this, converter = TwoWayConverter({ it -> AnimationVector4D(it.red, it.green, it.blue, it.alpha) }, { it -> androidx.compose.ui.graphics.Color(max(0f, min(1f, it.v1)), max(0f, min(1f, it.v2)), max(0f, min(1f, it.v3)), max(0f, min(1f, it.v4))) }), context = context)
}

/// Return an animatable version of this value.
@Composable
internal fun androidx.compose.ui.text.TextStyle.asAnimatable(context: ComposeContext): Animatable<androidx.compose.ui.text.TextStyle, AnimationVector1D> {
    val value = this
    return toAnimatable(value = value, converter = TwoWayConverter({ it -> AnimationVector1D(it.fontSize.value) }, { it -> value.copy(fontSize = TextUnit(it.value, TextUnitType.Sp)) }), context = context)
}


