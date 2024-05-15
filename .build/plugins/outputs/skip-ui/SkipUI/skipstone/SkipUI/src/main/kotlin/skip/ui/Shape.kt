// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

interface Shape: View, Sendable {
    fun path(in_: CGRect): Path {
        val rect = in_
        return Path()
    }

    val layoutDirectionBehavior: LayoutDirectionBehavior
        get() = LayoutDirectionBehavior.mirrors

    fun sizeThatFits(proposal: ProposedViewSize): CGSize = proposal.replacingUnspecifiedDimensions()

    val modified: ModifiedShape
        get() = ModifiedShape(shape = this)

    val canOutsetForStroke: Boolean
        get() = false

    @Composable
    override fun ComposeContent(context: ComposeContext): Unit = fill().ComposeContent(context = context)

    fun asComposePath(size: Size, density: Density): androidx.compose.ui.graphics.Path {
        val px = with(density) { -> 1.dp.toPx() }
        val path = path(in_ = CGRect(x = 0.0, y = 0.0, width = Double(size.width / px), height = Double(size.height / px)))
        return path.asComposePath(density = density)
    }

    fun asComposeShape(density: Density): androidx.compose.ui.graphics.Shape {
        return GenericShape { size, _ -> this.addPath(asComposePath(size = size, density = density)) }
    }
    fun fill(content: ShapeStyle, style: FillStyle = FillStyle()): Shape {
        var modifiedShape = this.modified.sref()
        modifiedShape.fill = content
        return modifiedShape.sref()
    }

    fun fill(style: FillStyle = FillStyle()): Shape = fill(ForegroundStyle(), style = style)

    fun inset(by: Double): Shape {
        val amount = by
        var modifiedShape = this.modified.sref()
        modifiedShape.modifications.append(ShapeModification.inset(amount))
        return modifiedShape.sref()
    }

    override fun offset(offset: CGSize): Shape = this.offset(CGPoint(x = offset.width, y = offset.height))

    fun offset(offset: CGPoint): Shape {
        var modifiedShape = this.modified.sref()
        modifiedShape.modifications.append(ShapeModification.offset(offset))
        return modifiedShape.sref()
    }

    override fun offset(x: Double, y: Double): Shape = this.offset(CGPoint(x = x, y = y))

    fun rotation(angle: Angle, anchor: UnitPoint = UnitPoint.center): Shape {
        var modifiedShape = this.modified.sref()
        modifiedShape.modifications.append(ShapeModification.rotation(angle, anchor))
        return modifiedShape.sref()
    }

    fun scale(x: Double = 1.0, y: Double = 1.0, anchor: UnitPoint = UnitPoint.center): Shape {
        var modifiedShape = this.modified.sref()
        modifiedShape.modifications.append(ShapeModification.scale(CGPoint(x = x, y = y), anchor))
        return modifiedShape.sref()
    }

    fun scale(scale: Double, anchor: UnitPoint = UnitPoint.center): Shape = this.scale(x = scale, y = scale, anchor = anchor)

    fun stroke(content: ShapeStyle, style: StrokeStyle, antialiased: Boolean = true): Shape {
        var modifiedShape = this.modified.sref()
        modifiedShape.strokes.append(ShapeStroke(content, style, false))
        return modifiedShape.sref()
    }

    fun stroke(content: ShapeStyle, lineWidth: Double = 1.0, antialiased: Boolean = true): Shape = stroke(content, style = StrokeStyle(lineWidth = lineWidth), antialiased = antialiased)

    fun stroke(style: StrokeStyle): Shape = stroke(ForegroundStyle(), style = style)

    fun stroke(lineWidth: Double = 1.0): Shape = stroke(ForegroundStyle(), style = StrokeStyle(lineWidth = lineWidth))

    fun strokeBorder(content: ShapeStyle = ForegroundStyle.foreground, style: StrokeStyle, antialiased: Boolean = true): View {
        var modifiedShape = this.modified.sref()
        modifiedShape.strokes.append(ShapeStroke(content, style, true))
        return modifiedShape.sref()
    }

    fun strokeBorder(style: StrokeStyle, antialiased: Boolean = true): View = strokeBorder(ForegroundStyle(), style = style, antialiased = antialiased)

    fun strokeBorder(content: ShapeStyle = ForegroundStyle.foreground, lineWidth: Double = 1.0, antialiased: Boolean = true): View = strokeBorder(content, style = StrokeStyle(lineWidth = lineWidth), antialiased = antialiased)

    fun strokeBorder(lineWidth: Double = 1.0, antialiased: Boolean = true): View = strokeBorder(ForegroundStyle(), style = StrokeStyle(lineWidth = lineWidth), antialiased = antialiased)
}
interface ShapeCompanion {
}

/// Modifications to a shape.
internal sealed class ShapeModification {
    class OffsetCase(val associated0: CGPoint): ShapeModification() {
    }
    class InsetCase(val associated0: Double): ShapeModification() {
    }
    class ScaleCase(val associated0: CGPoint, val associated1: UnitPoint): ShapeModification() {
    }
    class RotationCase(val associated0: Angle, val associated1: UnitPoint): ShapeModification() {
    }

    companion object {
        fun offset(associated0: CGPoint): ShapeModification = OffsetCase(associated0)
        fun inset(associated0: Double): ShapeModification = InsetCase(associated0)
        fun scale(associated0: CGPoint, associated1: UnitPoint): ShapeModification = ScaleCase(associated0, associated1)
        fun rotation(associated0: Angle, associated1: UnitPoint): ShapeModification = RotationCase(associated0, associated1)
    }
}

/// Strokes on a shape.
internal class ShapeStroke {
    internal val stroke: ShapeStyle
    internal val style: StrokeStyle?
    internal val isInset: Boolean

    constructor(stroke: ShapeStyle, style: StrokeStyle? = null, isInset: Boolean) {
        this.stroke = stroke.sref()
        this.style = style.sref()
        this.isInset = isInset
    }
}

/// A shape that has been modified.
class ModifiedShape: Shape, MutableStruct {
    internal val shape: Shape
    internal var modifications: Array<ShapeModification> = arrayOf()
        get() = field.sref({ this.modifications = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    internal var fill: ShapeStyle? = null
        get() = field.sref({ this.fill = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    internal var strokes: Array<ShapeStroke> = arrayOf()
        get() = field.sref({ this.strokes = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    internal constructor(shape: Shape) {
        this.shape = shape.sref()
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        val modifier = context.modifier.fillSize()
        val density = LocalDensity.current.sref()

        val fillBrush: Brush?
        val matchtarget_0 = fill
        if (matchtarget_0 != null) {
            val fill = matchtarget_0
            fillBrush = (fill.asBrush(opacity = 1.0, animationContext = context) ?: Color.primary.asBrush(opacity = 1.0, animationContext = null)).sref()
        } else {
            fillBrush = null
        }
        var strokeBrushes: Array<Tuple3<Brush, DrawStyle, Float>> = arrayOf()
        for (stroke in strokes.sref()) {
            val brush = (stroke.stroke.asBrush(opacity = 1.0, animationContext = context) ?: Color.primary.asBrush(opacity = 1.0, animationContext = null)!!).sref()
            val drawStyle = (stroke.style?.asDrawStyle() ?: Stroke()).sref()
            var inset = 0.0f
            if (stroke.isInset) {
                stroke.style.sref()?.let { style ->
                    inset = with(density) { -> (style.lineWidth / 2.0).dp.toPx() }
                }
            }
            strokeBrushes.append(Tuple3(brush.sref(), drawStyle.sref(), inset))
        }

        Canvas(modifier = modifier) { ->
            val scope = this.sref()
            val path = asComposePath(size = scope.size, density = density)
            if (fillBrush != null) {
                scope.drawPath(path, fillBrush)
            }
            for (strokeBrush in strokeBrushes.sref()) {
                val strokeInset = strokeBrush.element2
                if (strokeInset == 0.0f) {
                    scope.drawPath(path, brush = strokeBrush.element0, style = strokeBrush.element1)
                } else {
                    scope.inset(strokeInset) { ->
                        val strokePath = asComposePath(size = scope.size, density = density)
                        scope.drawPath(strokePath, brush = strokeBrush.element0, style = strokeBrush.element1)
                    }
                }
            }
        }
    }

    override val modified: ModifiedShape
        get() = this

    override fun asComposePath(size: Size, density: Density): androidx.compose.ui.graphics.Path = asComposePath(size = size, density = density, strokeOutset = 0.0)

    /// If this shape can be expressed as a touchable area, return it.
    ///
    /// This only works for shapes that aren't stroked or that can be outset for their stroke.
    /// - Seealso: `canOutsetForStroke`
    internal fun asComposeTouchShape(density: Density): androidx.compose.ui.graphics.Shape? {
        var strokeOutset = 0.0
        for (stroke in strokes.sref()) {
            if (!stroke.isInset) {
                stroke.style.sref()?.let { style ->
                    strokeOutset = max(strokeOutset, style.lineWidth / 2.0)
                }
            }
        }
        if (strokeOutset <= 0.0) {
            return asComposeShape(density = density)
        }
        if (!shape.canOutsetForStroke) {
            return null
        }
        return GenericShape { size, _ -> this.addPath(asComposePath(size = size, density = density, strokeOutset = strokeOutset)) }
    }

    private fun asComposePath(size: Size, density: Density, strokeOutset: Double): androidx.compose.ui.graphics.Path {
        val path = shape.asComposePath(size = size, density = density)
        var scaledSize = size.sref()
        var totalOffset = Offset(0.0f, 0.0f)
        var modifications = this.modifications.sref()
        if (strokeOutset > 0.0) {
            modifications.append(ShapeModification.inset(-strokeOutset))
        }
        // TODO: Support scale and rotation anchors
        for (mod in modifications.sref()) {
            when (mod) {
                is ShapeModification.OffsetCase -> {
                    val offset = mod.associated0
                    val offsetX = with(density) { -> offset.x.dp.toPx() }
                    val offsetY = with(density) { -> offset.y.dp.toPx() }
                    path.translate(Offset(offsetX, offsetY))
                    totalOffset = Offset(totalOffset.x + offsetX, totalOffset.y + offsetY)
                }
                is ShapeModification.InsetCase -> {
                    val inset = mod.associated0
                    val px = with(density) { -> inset.dp.toPx() }
                    val scaleX = 1.0f - (px * 2 / scaledSize.width)
                    val scaleY = 1.0f - (px * 2 / scaledSize.height)
                    val matrix = Matrix()
                    matrix.scale(scaleX, scaleY, 1.0f)
                    path.transform(matrix)
                    // Android scales from the origin, so the transform will move our translation too. Put it back
                    val scaledOffsetX = totalOffset.x * Float(scaleX)
                    val scaledOffsetY = totalOffset.y * Float(scaleY)
                    path.translate(Offset(px - (scaledOffsetX - totalOffset.x), px - (scaledOffsetY - totalOffset.y)))
                    scaledSize = Size(scaledSize.width - px * 2, scaledSize.height - px * 2)
                    totalOffset = Offset(totalOffset.x + px, totalOffset.y + px)
                }
                is ShapeModification.ScaleCase -> {
                    val scale = mod.associated0
                    val matrix = Matrix()
                    matrix.scale(Float(scale.x), Float(scale.y), 1.0f)
                    path.transform(matrix)
                    // Android scales from the origin, so the transform will move our translation too. Put it back
                    val scaledWidth = scaledSize.width * Float(scale.x)
                    val scaledHeight = scaledSize.height * Float(scale.y)
                    val scaledOffsetX = totalOffset.x * Float(scale.x)
                    val scaledOffsetY = totalOffset.y * Float(scale.y)
                    val additionalOffsetX = (scaledSize.width - scaledWidth) / 2
                    val additionalOffsetY = (scaledSize.height - scaledHeight) / 2
                    path.translate(Offset(additionalOffsetX - (scaledOffsetX - totalOffset.x), additionalOffsetY - (scaledOffsetY - totalOffset.y)))
                    scaledSize = Size(scaledWidth, scaledHeight)
                    totalOffset = Offset(totalOffset.x + additionalOffsetX, totalOffset.y + additionalOffsetY)
                }
                is ShapeModification.RotationCase -> {
                    val angle = mod.associated0
                    val matrix = Matrix()
                    matrix.rotateZ(Float(angle.degrees))
                    path.transform(matrix)
                    // Android rotates around the origin rather than the center. Calculate the offset that this rotation
                    // causes to the center point and apply its inverse to get a rotation around the center. Note that we
                    // negate the y axis because mathmatical coordinate systems have the origin in the bottom left, not top
                    val radians = angle.radians
                    val centerX = scaledSize.width / 2 + totalOffset.x
                    val centerY = -scaledSize.height / 2 - totalOffset.y
                    val rotatedCenterX = centerX * cos(-radians) - centerY * sin(-radians)
                    val rotatedCenterY = centerX * sin(-radians) + centerY * cos(-radians)
                    val additionalOffsetX = Float(centerX - rotatedCenterX)
                    val additionalOffsetY = Float(-(centerY - rotatedCenterY))
                    path.translate(Offset(additionalOffsetX, additionalOffsetY))
                }
            }
        }
        return path.sref()
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as ModifiedShape
        this.shape = copy.shape
        this.modifications = copy.modifications
        this.fill = copy.fill
        this.strokes = copy.strokes
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = ModifiedShape(this as MutableStruct)

    companion object: ShapeCompanion {
    }
}

class Circle: Shape {
    constructor() {
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        val dim = min(rect.width, rect.height)
        val x = rect.minX + (rect.width - dim) / 2.0
        val y = rect.minY + (rect.height - dim) / 2.0
        return Path(ellipseIn = CGRect(x = x, y = y, width = dim, height = dim))
    }

    override val canOutsetForStroke: Boolean
        get() = true

    companion object: ShapeCompanion {

        val circle: Circle
            get() = Circle()
    }
}

class Rectangle: Shape {
    constructor() {
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        return Path(rect)
    }

    override val canOutsetForStroke: Boolean
        get() = true

    companion object: ShapeCompanion {

        val rect: Rectangle
            get() = Rectangle()
    }
}

class RoundedRectangle: Shape, MutableStruct {
    val cornerSize: CGSize
    val style: RoundedCornerStyle
    internal var fillStyle: ShapeStyle? = null
        get() = field.sref({ this.fillStyle = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(cornerSize: CGSize, style: RoundedCornerStyle = RoundedCornerStyle.continuous) {
        this.cornerSize = cornerSize.sref()
        this.style = style
    }

    constructor(cornerRadius: Double, style: RoundedCornerStyle = RoundedCornerStyle.continuous) {
        this.cornerSize = CGSize(width = cornerRadius, height = cornerRadius)
        this.style = style
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        return Path(roundedRect = rect, cornerSize = cornerSize, style = style)
    }

    override val canOutsetForStroke: Boolean
        get() = true

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as RoundedRectangle
        this.cornerSize = copy.cornerSize
        this.style = copy.style
        this.fillStyle = copy.fillStyle
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = RoundedRectangle(this as MutableStruct)

    companion object: ShapeCompanion {

        fun rect(cornerSize: CGSize, style: RoundedCornerStyle = RoundedCornerStyle.continuous): RoundedRectangle = RoundedRectangle(cornerSize = cornerSize, style = style)

        fun rect(cornerRadius: Double, style: RoundedCornerStyle = RoundedCornerStyle.continuous): RoundedRectangle = RoundedRectangle(cornerRadius = cornerRadius, style = style)
    }
}

class UnevenRoundedRectangle: Shape {
    val cornerRadii: RectangleCornerRadii
    val style: RoundedCornerStyle

    constructor(cornerRadii: RectangleCornerRadii, style: RoundedCornerStyle = RoundedCornerStyle.continuous) {
        this.cornerRadii = cornerRadii
        this.style = style
    }

    constructor(topLeadingRadius: Double = 0.0, bottomLeadingRadius: Double = 0.0, bottomTrailingRadius: Double = 0.0, topTrailingRadius: Double = 0.0, style: RoundedCornerStyle = RoundedCornerStyle.continuous) {
        this.cornerRadii = RectangleCornerRadii(topLeading = topLeadingRadius, bottomLeading = bottomLeadingRadius, bottomTrailing = bottomTrailingRadius, topTrailing = topTrailingRadius)
        this.style = style
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        return Path(roundedRect = rect, cornerRadii = cornerRadii, style = style)
    }

    override val canOutsetForStroke: Boolean
        get() = true

    companion object: ShapeCompanion {

        fun rect(cornerRadii: RectangleCornerRadii, style: RoundedCornerStyle = RoundedCornerStyle.continuous): UnevenRoundedRectangle = UnevenRoundedRectangle(cornerRadii = cornerRadii, style = style)

        fun rect(topLeadingRadius: Double = 0.0, bottomLeadingRadius: Double = 0.0, bottomTrailingRadius: Double = 0.0, topTrailingRadius: Double = 0.0, style: RoundedCornerStyle = RoundedCornerStyle.continuous): UnevenRoundedRectangle = UnevenRoundedRectangle(topLeadingRadius = topLeadingRadius, bottomLeadingRadius = bottomLeadingRadius, bottomTrailingRadius = bottomTrailingRadius, topTrailingRadius = topTrailingRadius, style = style)
    }
}

class Capsule: Shape {
    val style: RoundedCornerStyle

    constructor(style: RoundedCornerStyle = RoundedCornerStyle.continuous) {
        this.style = style
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        var path = Path()
        if (rect.width >= rect.height) {
            path.move(to = CGPoint(x = rect.minX + rect.height / 2.0, y = rect.minY))
            path.addLine(to = CGPoint(x = rect.maxX - rect.height / 2.0, y = rect.minY))
            path.addRelativeArc(center = CGPoint(x = rect.maxX - rect.height / 2.0, y = rect.midY), radius = rect.height / 2.0, startAngle = Angle(degrees = -90.0), delta = Angle(degrees = 180.0))
            path.addLine(to = CGPoint(x = rect.minX + rect.height / 2.0, y = rect.maxY))
            path.addRelativeArc(center = CGPoint(x = rect.minX + rect.height / 2.0, y = rect.midY), radius = rect.height / 2.0, startAngle = Angle(degrees = 90.0), delta = Angle(degrees = 180.0))
        } else {
            path.move(to = CGPoint(x = rect.minX, y = rect.minY + rect.width / 2.0))
            path.addRelativeArc(center = CGPoint(x = rect.midX, y = rect.minY + rect.width / 2.0), radius = rect.width / 2.0, startAngle = Angle(degrees = -180.0), delta = Angle(degrees = 180.0))
            path.addLine(to = CGPoint(x = rect.maxX, y = rect.maxY - rect.width / 2.0))
            path.addRelativeArc(center = CGPoint(x = rect.midX, y = rect.maxY - rect.width / 2.0), radius = rect.width / 2.0, startAngle = Angle(degrees = 0.0), delta = Angle(degrees = 180.0))
        }
        return path.sref()
    }

    override val canOutsetForStroke: Boolean
        get() = true

    companion object: ShapeCompanion {

        val capsule: Capsule
            get() = Capsule()

        fun capsule(style: RoundedCornerStyle): Capsule = Capsule(style = style)
    }
}

class Ellipse: Shape {
    constructor() {
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        return Path(ellipseIn = rect)
    }

    override val canOutsetForStroke: Boolean
        get() = true

    companion object: ShapeCompanion {

        val ellipse: Ellipse
            get() = Ellipse()
    }
}

class AnyShape: Shape, Sendable {
    private val shape: Shape

    constructor(shape: Shape) {
        this.shape = shape.sref()
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        return shape.path(in_ = rect)
    }

    @Composable
    override fun ComposeContent(context: ComposeContext) {
        shape.Compose(context = context)
    }

    override val modified: ModifiedShape
        get() = shape.modified

    override val canOutsetForStroke: Boolean
        get() = shape.canOutsetForStroke

    companion object: ShapeCompanion {
    }
}

class RectangleCornerRadii: Sendable {
    val topLeading: Double
    val bottomLeading: Double
    val bottomTrailing: Double
    val topTrailing: Double

    constructor(topLeading: Double = 0.0, bottomLeading: Double = 0.0, bottomTrailing: Double = 0.0, topTrailing: Double = 0.0) {
        this.topLeading = topLeading
        this.bottomLeading = bottomLeading
        this.bottomTrailing = bottomTrailing
        this.topTrailing = topTrailing
    }


    override fun equals(other: Any?): Boolean {
        if (other !is RectangleCornerRadii) return false
        return topLeading == other.topLeading && bottomLeading == other.bottomLeading && bottomTrailing == other.bottomTrailing && topTrailing == other.topTrailing
    }

    companion object {
    }
}


