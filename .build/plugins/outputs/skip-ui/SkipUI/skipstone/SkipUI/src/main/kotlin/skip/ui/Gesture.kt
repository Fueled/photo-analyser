// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import skip.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

interface Gesture<V> {
    val modified: ModifiedGesture<V>
        get() = ModifiedGesture(gesture = this)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun exclusively(before: Gesture<*>): Gesture<V> {
        val other = before
        return this.sref()
    }

    // Skip can't distinguish between this and the other onEnded variant
    //    @available(*, unavailable)
    //    public func onEnded(_ action: @escaping () -> Void) -> any Gesture<V> {
    //        return self
    //    }

    fun onEnded(action: (V) -> Unit): Gesture<V> {
        var gesture = this.modified.sref()
        gesture.onEnded.append(action)
        return gesture.sref()
    }

    // Skip can't distinguish between this and the other onChanged variant
    //    @available(*, unavailable)
    //    public func onChanged(_ action: @escaping () -> Void) -> any Gesture<V> {
    //        return self
    //    }

    fun onChanged(action: (V) -> Unit): Gesture<V> {
        var gesture = this.modified.sref()
        gesture.onChanged.append(action)
        return gesture.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun map(body: () -> Any): Gesture<V> = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun map(body: (Any) -> Any): Gesture<V> = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun sequenced(before: Gesture<*>): Gesture<V> {
        val other = before
        return this.sref()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun simultaneously(with: Gesture<*>): Gesture<V> {
        val other = with
        return this.sref()
    }

}

class DragGesture: Gesture<DragGesture.Value>, MutableStruct {

    class Value: Sendable, MutableStruct {
        var time: Date
            get() = field.sref({ this.time = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var location: CGPoint
            get() = field.sref({ this.location = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var startLocation: CGPoint
            get() = field.sref({ this.startLocation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var translation: CGSize
            get() = field.sref({ this.translation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var velocity: CGSize
            get() = field.sref({ this.velocity = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var predictedEndLocation: CGPoint
            get() = field.sref({ this.predictedEndLocation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var predictedEndTranslation: CGSize
            get() = field.sref({ this.predictedEndTranslation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(time: Date, location: CGPoint, startLocation: CGPoint, translation: CGSize, velocity: CGSize, predictedEndLocation: CGPoint, predictedEndTranslation: CGSize) {
            this.time = time
            this.location = location
            this.startLocation = startLocation
            this.translation = translation
            this.velocity = velocity
            this.predictedEndLocation = predictedEndLocation
            this.predictedEndTranslation = predictedEndTranslation
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = DragGesture.Value(time, location, startLocation, translation, velocity, predictedEndLocation, predictedEndTranslation)

        override fun equals(other: Any?): Boolean {
            if (other !is DragGesture.Value) return false
            return time == other.time && location == other.location && startLocation == other.startLocation && translation == other.translation && velocity == other.velocity && predictedEndLocation == other.predictedEndLocation && predictedEndTranslation == other.predictedEndTranslation
        }

        companion object {
        }
    }

    var minimumDistance: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var coordinateSpace: CoordinateSpaceProtocol
        get() = field.sref({ this.coordinateSpace = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(minimumDistance: Double = 10.0, coordinateSpace: CoordinateSpaceProtocol = LocalCoordinateSpace.local) {
        this.minimumDistance = minimumDistance
        this.coordinateSpace = coordinateSpace
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as DragGesture
        this.minimumDistance = copy.minimumDistance
        this.coordinateSpace = copy.coordinateSpace
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = DragGesture(this as MutableStruct)

    companion object {
    }
}

class TapGesture: Gesture<Unit>, MutableStruct {

    var count: Int
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(count: Int = 1) {
        this.count = count
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as TapGesture
        this.count = copy.count
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = TapGesture(this as MutableStruct)

    companion object {
    }
}

class LongPressGesture: Gesture<Boolean>, MutableStruct {

    var minimumDuration: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var maximumDistance: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(minimumDuration: Double = 0.5, maximumDistance: Double = 10.0) {
        this.minimumDuration = minimumDuration
        this.maximumDistance = maximumDistance
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as LongPressGesture
        this.minimumDuration = copy.minimumDuration
        this.maximumDistance = copy.maximumDistance
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = LongPressGesture(this as MutableStruct)

    companion object {
    }
}

class MagnifyGesture: Gesture<MagnifyGesture.Value>, MutableStruct {

    class Value: Sendable, MutableStruct {
        var time: Date
            get() = field.sref({ this.time = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var magnification: Double
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var velocity: Double
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }
        var startAnchor: UnitPoint
            get() = field.sref({ this.startAnchor = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var startLocation: CGPoint
            get() = field.sref({ this.startLocation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(time: Date, magnification: Double, velocity: Double, startAnchor: UnitPoint, startLocation: CGPoint) {
            this.time = time
            this.magnification = magnification
            this.velocity = velocity
            this.startAnchor = startAnchor
            this.startLocation = startLocation
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = MagnifyGesture.Value(time, magnification, velocity, startAnchor, startLocation)

        override fun equals(other: Any?): Boolean {
            if (other !is MagnifyGesture.Value) return false
            return time == other.time && magnification == other.magnification && velocity == other.velocity && startAnchor == other.startAnchor && startLocation == other.startLocation
        }

        companion object {
        }
    }

    var minimumScaleDelta: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(minimumScaleDelta: Double = 0.01) {
        this.minimumScaleDelta = minimumScaleDelta
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as MagnifyGesture
        this.minimumScaleDelta = copy.minimumScaleDelta
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = MagnifyGesture(this as MutableStruct)

    companion object {
    }
}

class RotateGesture: Gesture<RotateGesture.Value>, MutableStruct {

    class Value: Sendable, MutableStruct {
        var time: Date
            get() = field.sref({ this.time = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var rotation: Angle
            get() = field.sref({ this.rotation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var velocity: Angle
            get() = field.sref({ this.velocity = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var startAnchor: UnitPoint
            get() = field.sref({ this.startAnchor = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        var startLocation: CGPoint
            get() = field.sref({ this.startLocation = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(time: Date, rotation: Angle, velocity: Angle, startAnchor: UnitPoint, startLocation: CGPoint) {
            this.time = time
            this.rotation = rotation
            this.velocity = velocity
            this.startAnchor = startAnchor
            this.startLocation = startLocation
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = RotateGesture.Value(time, rotation, velocity, startAnchor, startLocation)

        override fun equals(other: Any?): Boolean {
            if (other !is RotateGesture.Value) return false
            return time == other.time && rotation == other.rotation && velocity == other.velocity && startAnchor == other.startAnchor && startLocation == other.startLocation
        }

        companion object {
        }
    }

    var minimumAngleDelta: Angle
        get() = field.sref({ this.minimumAngleDelta = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(minimumAngleDelta: Angle = Angle.degrees(1.0)) {
        this.minimumAngleDelta = minimumAngleDelta
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as RotateGesture
        this.minimumAngleDelta = copy.minimumAngleDelta
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = RotateGesture(this as MutableStruct)

    companion object {
    }
}

class SpatialEventGesture: Gesture<Unit> {

    val coordinateSpace: CoordinateSpaceProtocol
    val action: (Any) -> Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(coordinateSpace: CoordinateSpaceProtocol = LocalCoordinateSpace.local, action: (Any) -> Unit) {
        this.coordinateSpace = coordinateSpace.sref()
        this.action = action
    }

    companion object {
    }
}

class SpatialTapGesture: Gesture<SpatialTapGesture.Value>, MutableStruct {

    class Value: Sendable, MutableStruct {
        var location: CGPoint
            get() = field.sref({ this.location = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }

        constructor(location: CGPoint) {
            this.location = location
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = SpatialTapGesture.Value(location)

        override fun equals(other: Any?): Boolean {
            if (other !is SpatialTapGesture.Value) return false
            return location == other.location
        }

        companion object {
        }
    }

    var count: Int
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var coordinateSpace: CoordinateSpaceProtocol
        get() = field.sref({ this.coordinateSpace = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(count: Int = 1, coordinateSpace: CoordinateSpaceProtocol = LocalCoordinateSpace.local) {
        this.count = count
        this.coordinateSpace = coordinateSpace
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as SpatialTapGesture
        this.count = copy.count
        this.coordinateSpace = copy.coordinateSpace
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = SpatialTapGesture(this as MutableStruct)

    companion object {
    }
}

class GestureMask: OptionSet<GestureMask, Int>, Sendable, MutableStruct {
    override var rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override val rawvaluelong: ULong
        get() = ULong(rawValue)
    override fun makeoptionset(rawvaluelong: ULong): GestureMask = GestureMask(rawValue = Int(rawvaluelong))
    override fun assignoptionset(target: GestureMask) {
        willmutate()
        try {
            assignfrom(target)
        } finally {
            didmutate()
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as GestureMask
        this.rawValue = copy.rawValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = GestureMask(this as MutableStruct)

    private fun assignfrom(target: GestureMask) {
        this.rawValue = target.rawValue
    }

    companion object {

        val none = GestureMask(rawValue = 1)
        val gesture = GestureMask(rawValue = 2)
        val subviews = GestureMask(rawValue = 4)
        val all = GestureMask(rawValue = 7)

        fun of(vararg options: GestureMask): GestureMask {
            val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
            return GestureMask(rawValue = value)
        }
    }
}

/// A gesture that has been modified with callbacks, etc.
class ModifiedGesture<V>: Gesture<V>, MutableStruct {
    internal val gesture: Gesture<V>
    internal var onChanged: Array<(V) -> Unit> = arrayOf()
        get() = field.sref({ this.onChanged = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    internal var onEnded: Array<(V) -> Unit> = arrayOf()
        get() = field.sref({ this.onEnded = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    internal var onEndedWithLocation: ((CGPoint) -> Unit)? = null
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    internal constructor(gesture: Gesture<V>) {
        this.gesture = gesture.sref()
    }

    internal val isTapGesture: Boolean
        get() {
            return (gesture as? TapGesture)?.count == 1
        }

    internal fun onTap(at: CGPoint) {
        val point = at
        onEndedWithLocation?.invoke(point)
        onEnded.forEach { it -> it(Unit as V) }
    }

    internal val isDoubleTapGesture: Boolean
        get() {
            return (gesture as? TapGesture)?.count == 2
        }

    internal fun onDoubleTap(at: CGPoint) {
        val point = at
        onEndedWithLocation?.invoke(point)
        onEnded.forEach { it -> it(Unit as V) }
    }

    internal val isLongPressGesture: Boolean
        get() = gesture is LongPressGesture

    internal fun onLongPressChange() {
        onChanged.forEach { it -> it(true as V) }
    }

    internal fun onLongPressEnd() {
        onEnded.forEach { it -> it(true as V) }
    }

    internal val isDragGesture: Boolean
        get() = gesture is DragGesture

    internal fun onDragChange(location: CGPoint, translation: CGSize) {
        val value = dragValue(location = location, translation = translation)
        onChanged.forEach { it -> it(value as V) }
    }

    internal fun onDragEnd(location: CGPoint, translation: CGSize) {
        val value = dragValue(location = location, translation = translation)
        onEnded.forEach { it -> it(value as V) }
    }

    private fun dragValue(location: CGPoint, translation: CGSize): DragGesture.Value = DragGesture.Value(time = Date(), location = location, startLocation = CGPoint(x = location.x - translation.width, y = location.y - translation.height), translation = translation, velocity = CGSize.zero, predictedEndLocation = location, predictedEndTranslation = translation)

    override val modified: ModifiedGesture<V>
        get() = this

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ModifiedGesture<V>
        this.gesture = copy.gesture
        this.onChanged = copy.onChanged
        this.onEnded = copy.onEnded
        this.onEndedWithLocation = copy.onEndedWithLocation
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ModifiedGesture<V>(this as MutableStruct)

    companion object {
    }
}

/// Modifier view that collects and executes gestures.
internal class GestureModifierView: ComposeModifierView {
    internal var gestures: Array<ModifiedGesture<Any>>
        get() = field.sref({ this.gestures = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal constructor(view: View, gesture: Gesture<Any>): super(view = view, role = ComposeModifierRole.gesture) {
        gestures = arrayOf(gesture.modified)

        // Compose wants you to collect all e.g. tap gestures into a single pointerInput modifier, so we collect all our gestures
        view.strippingModifiers(until = { it -> it == ComposeModifierRole.gesture }, perform = { it -> it as? GestureModifierView })?.let { wrappedGestureView ->
            gestures += wrappedGestureView.gestures
            wrappedGestureView.gestures = arrayOf()
        }
        this.action = l@{ it ->
            it.value.modifier = addGestures(to = it.value.modifier)
            return@l ComposeResult.ok
        }
    }

    @Composable
    private fun addGestures(to: Modifier): Modifier {
        val modifier = to
        if (gestures.isEmpty) {
            return modifier
        }
        if (!EnvironmentValues.shared.isEnabled) {
            return modifier
        }

        val density = LocalDensity.current.sref()
        var ret = modifier

        // If the gesture is placed directly on a shape, we attempt to constrain hits to the shape
        view.strippingModifiers(until = { it -> it != ComposeModifierRole.accessibility }, perform = { it -> it as? ModifiedShape })?.let { shape ->
            shape.asComposeTouchShape(density = density)?.let { touchShape ->
                ret = ret.clip(touchShape)
            }
        }

        val tapGestures = rememberUpdatedState(gestures.filter { it -> it.isTapGesture })
        val doubleTapGestures = rememberUpdatedState(gestures.filter { it -> it.isDoubleTapGesture })
        val longPressGestures = rememberUpdatedState(gestures.filter { it -> it.isLongPressGesture })
        if (!tapGestures.value.isEmpty || !doubleTapGestures.value.isEmpty || !longPressGestures.value.isEmpty) {
            ret = ret.pointerInput(true) { ->
                val onDoubleTap: ((Offset) -> Unit)?
                if (!doubleTapGestures.value.isEmpty) {
                    onDoubleTap = { offsetPx ->
                        val x = with(density) { -> offsetPx.x.toDp() }
                        val y = with(density) { -> offsetPx.y.toDp() }
                        val point = CGPoint(x = Double(x.value), y = Double(y.value))
                        doubleTapGestures.value.forEach { it -> it.onDoubleTap(at = point) }
                    }
                } else {
                    onDoubleTap = null
                }
                val onLongPress: ((Offset) -> Unit)?
                if (!longPressGestures.value.isEmpty) {
                    onLongPress = { _ ->
                        longPressGestures.value.forEach { it -> it.onLongPressEnd() }
                    }
                } else {
                    onLongPress = null
                }
                detectTapGestures(onDoubleTap = onDoubleTap, onLongPress = onLongPress, onPress = { _ ->
                    longPressGestures.value.forEach { it -> it.onLongPressChange() }
                }, onTap = { offsetPx ->
                    if (!tapGestures.value.isEmpty) {
                        val x = with(density) { -> offsetPx.x.toDp() }
                        val y = with(density) { -> offsetPx.y.toDp() }
                        val point = CGPoint(x = Double(x.value), y = Double(y.value))
                        tapGestures.value.forEach { it -> it.onTap(at = point) }
                    }
                })
            }
        }

        val dragGestures = rememberUpdatedState(gestures.filter { it -> it.isDragGesture })
        if (!dragGestures.value.isEmpty) {
            val dragOffsetX = remember { -> mutableStateOf(0.0f) }
            val dragOffsetY = remember { -> mutableStateOf(0.0f) }
            val dragPositionX = remember { -> mutableStateOf(0.0f) }
            val dragPositionY = remember { -> mutableStateOf(0.0f) }
            ret = ret.pointerInput(true) { ->
                detectDragGestures(onDrag = { change, offsetPx ->
                    val offsetX = with(density) { -> offsetPx.x.toDp() }
                    val offsetY = with(density) { -> offsetPx.y.toDp() }
                    dragOffsetX.value += offsetX.value
                    dragOffsetY.value += offsetY.value
                    val translation = CGSize(width = Double(dragOffsetX.value), height = Double(dragOffsetY.value))

                    dragPositionX.value = (with(density) { -> change.position.x.toDp() }).value
                    dragPositionY.value = (with(density) { -> change.position.y.toDp() }).value
                    val location = CGPoint(x = Double(dragPositionX.value), y = Double(dragPositionY.value))

                    dragGestures.value.forEach { it -> it.onDragChange(location = location, translation = translation) }
                }, onDragEnd = { ->
                    val translation = CGSize(width = Double(dragOffsetX.value), height = Double(dragOffsetY.value))
                    val location = CGPoint(x = Double(dragPositionX.value), y = Double(dragPositionY.value))
                    dragOffsetX.value = 0.0f
                    dragOffsetY.value = 0.0f
                    dragGestures.value.forEach { it -> it.onDragEnd(location = location, translation = translation) }
                }, onDragCancel = { ->
                    val translation = CGSize(width = Double(dragOffsetX.value), height = Double(dragOffsetY.value))
                    val location = CGPoint(x = Double(dragPositionX.value), y = Double(dragPositionY.value))
                    dragOffsetX.value = 0.0f
                    dragOffsetY.value = 0.0f
                    dragGestures.value.forEach { it -> it.onDragEnd(location = location, translation = translation) }
                })
            }
        }
        return ret
    }
}

