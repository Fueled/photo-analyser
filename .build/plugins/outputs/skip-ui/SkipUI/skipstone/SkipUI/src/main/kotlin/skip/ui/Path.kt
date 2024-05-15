// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*
import skip.lib.Array

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

class Path: Shape, MutableStruct {
    private val path: androidx.compose.ui.graphics.Path

    constructor(path: androidx.compose.ui.graphics.Path = androidx.compose.ui.graphics.Path()) {
        this.path = path.sref()
    }

    // Custom copy constructor to copy the path
    constructor(copy: MutableStruct): this() {
        path.addPath((copy as Path).path)
    }

    constructor(rect: CGRect): this() {
        addRect(rect)
    }

    constructor(roundedRect: CGRect, cornerSize: CGSize, style: RoundedCornerStyle = RoundedCornerStyle.continuous): this() {
        val rect = roundedRect
        addRoundedRect(in_ = rect, cornerSize = cornerSize, style = style)
    }

    constructor(roundedRect: CGRect, cornerRadius: Double, style: RoundedCornerStyle = RoundedCornerStyle.continuous): this() {
        val rect = roundedRect
        addRoundedRect(in_ = rect, cornerSize = CGSize(width = cornerRadius, height = cornerRadius), style = style)
    }

    constructor(roundedRect: CGRect, cornerRadii: RectangleCornerRadii, style: RoundedCornerStyle = RoundedCornerStyle.continuous): this() {
        val rect = roundedRect
        addRoundedRect(in_ = rect, cornerRadii = cornerRadii, style = style)
    }

    constructor(ellipseIn: CGRect, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): this() {
        val rect = ellipseIn
        addEllipse(in_ = rect)
    }

    constructor(callback: (InOut<Path>) -> Unit): this() {
        callback(InOut({ this }, { }))
    }

    override fun path(in_: CGRect): Path {
        val rect = in_
        return this.sref()
    }

    fun asComposePath(density: Density): androidx.compose.ui.graphics.Path {
        val px = with(density) { -> 1.dp.toPx() }
        val scaledPath = androidx.compose.ui.graphics.Path()
        scaledPath.addPath(path)
        val matrix = Matrix()
        matrix.scale(px, px, 1.0f)
        scaledPath.transform(matrix)
        return scaledPath.sref()
    }

    val isEmpty: Boolean
        get() = path.isEmpty

    val boundingRect: CGRect
        get() {
            val bounds = path.getBounds()
            return CGRect(x = Double(bounds.left), y = Double(bounds.top), width = Double(bounds.width), height = Double(bounds.height))
        }

    fun contains(p: CGPoint, eoFill: Boolean = false): Boolean = boundingRect.contains(p)

    sealed class Element: Sendable {
        class MoveCase(val associated0: CGPoint): Element() {
            val to = associated0

            override fun equals(other: Any?): Boolean {
                if (other !is MoveCase) return false
                return associated0 == other.associated0
            }
        }
        class LineCase(val associated0: CGPoint): Element() {
            val to = associated0

            override fun equals(other: Any?): Boolean {
                if (other !is LineCase) return false
                return associated0 == other.associated0
            }
        }
        class QuadCurveCase(val associated0: CGPoint, val associated1: CGPoint): Element() {
            val to = associated0
            val control = associated1

            override fun equals(other: Any?): Boolean {
                if (other !is QuadCurveCase) return false
                return associated0 == other.associated0 && associated1 == other.associated1
            }
        }
        class CurveCase(val associated0: CGPoint, val associated1: CGPoint, val associated2: CGPoint): Element() {
            val to = associated0
            val control1 = associated1
            val control2 = associated2

            override fun equals(other: Any?): Boolean {
                if (other !is CurveCase) return false
                return associated0 == other.associated0 && associated1 == other.associated1 && associated2 == other.associated2
            }
        }
        class CloseSubpathCase: Element() {
        }

        companion object {
            fun move(to: CGPoint): Element = MoveCase(to)
            fun line(to: CGPoint): Element = LineCase(to)
            fun quadCurve(to: CGPoint, control: CGPoint): Element = QuadCurveCase(to, control)
            fun curve(to: CGPoint, control1: CGPoint, control2: CGPoint): Element = CurveCase(to, control1, control2)
            val closeSubpath: Element = CloseSubpathCase()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun forEach(body: (Path.Element) -> Unit) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun strokedPath(style: StrokeStyle): Path = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun trimmedPath(from: Double, to: Double): Path = this.sref()

    fun move(to: CGPoint) {
        val end = to
        willmutate()
        try {
            path.moveTo(Float(end.x), Float(end.y))
        } finally {
            didmutate()
        }
    }

    fun addLine(to: CGPoint) {
        val end = to
        willmutate()
        try {
            path.lineTo(Float(end.x), Float(end.y))
        } finally {
            didmutate()
        }
    }

    fun addQuadCurve(to: CGPoint, control: CGPoint) {
        val end = to
        willmutate()
        try {
            path.quadraticBezierTo(Float(control.x), Float(control.y), Float(end.x), Float(end.y))
        } finally {
            didmutate()
        }
    }

    fun addCurve(to: CGPoint, control1: CGPoint, control2: CGPoint) {
        val end = to
        willmutate()
        try {
            path.cubicTo(Float(control1.x), Float(control1.y), Float(control2.x), Float(control2.y), Float(end.x), Float(end.y))
        } finally {
            didmutate()
        }
    }

    fun closeSubpath() {
        willmutate()
        try {
            path.close()
        } finally {
            didmutate()
        }
    }

    fun addRect(rect: CGRect, transform: CGAffineTransform = CGAffineTransform.identity) {
        willmutate()
        try {
            if (transform.isIdentity) {
                path.addRect(Rect(Float(rect.minX), Float(rect.minY), Float(rect.maxX), Float(rect.maxY)))
            } else {
                path.addPath(Path(rect).applying(transform).path)
            }
        } finally {
            didmutate()
        }
    }

    fun addRoundedRect(in_: CGRect, cornerSize: CGSize, style: RoundedCornerStyle = RoundedCornerStyle.continuous, transform: CGAffineTransform = CGAffineTransform.identity) {
        val rect = in_
        willmutate()
        try {
            if (transform.isIdentity) {
                path.addRoundRect(RoundRect(Float(rect.minX), Float(rect.minY), Float(rect.maxX), Float(rect.maxY), Float(cornerSize.width), Float(cornerSize.height)))
            } else {
                path.addPath(Path(roundedRect = rect, cornerSize = cornerSize, style = style).applying(transform).path)
            }
        } finally {
            didmutate()
        }
    }

    fun addRoundedRect(in_: CGRect, cornerRadii: RectangleCornerRadii, style: RoundedCornerStyle = RoundedCornerStyle.continuous, transform: CGAffineTransform = CGAffineTransform.identity) {
        val rect = in_
        willmutate()
        try {
            if (transform.isIdentity) {
                path.addRoundRect(RoundRect(Rect(Float(rect.minX), Float(rect.minY), Float(rect.maxX), Float(rect.maxY)), CornerRadius(Float(cornerRadii.topLeading), Float(cornerRadii.topLeading)), CornerRadius(Float(cornerRadii.topTrailing), Float(cornerRadii.topTrailing)), CornerRadius(Float(cornerRadii.bottomTrailing), Float(cornerRadii.bottomTrailing)), CornerRadius(Float(cornerRadii.bottomLeading), Float(cornerRadii.bottomLeading))))
            } else {
                path.addPath(Path(roundedRect = rect, cornerRadii = cornerRadii, style = style).applying(transform).path)
            }
        } finally {
            didmutate()
        }
    }

    fun addEllipse(in_: CGRect, transform: CGAffineTransform = CGAffineTransform.identity) {
        val rect = in_
        willmutate()
        try {
            if (transform.isIdentity) {
                path.addOval(Rect(Float(rect.minX), Float(rect.minY), Float(rect.maxX), Float(rect.maxY)))
            } else {
                path.addPath(Path(ellipseIn = rect).applying(transform).path)
            }
        } finally {
            didmutate()
        }
    }

    fun addRects(rects: Array<CGRect>, transform: CGAffineTransform = CGAffineTransform.identity) {
        willmutate()
        try {
            rects.forEach { it -> addRect(it, transform = transform) }
        } finally {
            didmutate()
        }
    }

    fun addLines(lines: Array<CGPoint>) {
        willmutate()
        try {
            lines.first.sref()?.let { first ->
                move(to = first)
            }
            for (i in 1 until lines.count) {
                addLine(to = lines[i])
            }
        } finally {
            didmutate()
        }
    }

    fun addRelativeArc(center: CGPoint, radius: Double, startAngle: Angle, delta: Angle, transform: CGAffineTransform = CGAffineTransform.identity) {
        willmutate()
        try {
            if (transform.isIdentity) {
                path.arcTo(Rect(Float(center.x - radius), Float(center.y - radius), Float(center.x + radius), Float(center.y + radius)), Float(startAngle.degrees), Float(delta.degrees), forceMoveTo = false)
            } else {
                var arcPath = Path()
                arcPath.addRelativeArc(center = center, radius = radius, startAngle = startAngle, delta = delta)
                path.addPath(arcPath.applying(transform).path)
            }
        } finally {
            didmutate()
        }
    }

    fun addArc(center: CGPoint, radius: Double, startAngle: Angle, endAngle: Angle, clockwise: Boolean, transform: CGAffineTransform = CGAffineTransform.identity) {
        willmutate()
        try {
            val deltar = if (clockwise) startAngle.radians - endAngle.radians else endAngle.radians - startAngle.radians
            addRelativeArc(center = center, radius = radius, startAngle = startAngle, delta = Angle(radians = deltar))
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun addArc(tangent1End: CGPoint, tangent2End: CGPoint, radius: Double, transform: CGAffineTransform = CGAffineTransform.identity) = Unit

    fun addPath(other: Path, transform: CGAffineTransform = CGAffineTransform.identity) {
        willmutate()
        try {
            path.addPath(other.applying(transform).path)
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val currentPoint: CGPoint?
        get() = null

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun normalized(eoFill: Boolean = true): Path = this.sref()

    fun intersection(other: Path, eoFill: Boolean = false): Path = Path(path = androidx.compose.ui.graphics.Path.combine(PathOperation.Intersect, path, other.path))

    fun union(other: Path, eoFill: Boolean = false): Path = Path(path = androidx.compose.ui.graphics.Path.combine(PathOperation.Union, path, other.path))

    fun subtracting(other: Path, eoFill: Boolean = false): Path = Path(path = androidx.compose.ui.graphics.Path.combine(PathOperation.Difference, path, other.path))

    fun symmetricDifference(other: Path, eoFill: Boolean = false): Path = Path(path = androidx.compose.ui.graphics.Path.combine(PathOperation.Xor, path, other.path))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun lineIntersection(other: Path, eoFill: Boolean = false): Path = this.sref()

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun lineSubtraction(other: Path, eoFill: Boolean = false): Path = this.sref()

    fun applying(transform: CGAffineTransform): Path {
        if (transform.isIdentity) {
            return this.sref()
        }
        val transformedPath = androidx.compose.ui.graphics.Path()
        transformedPath.addPath(path)
        transformedPath.transform(transform.asMatrix())
        return Path(path = transformedPath)
    }

    fun offsetBy(dx: Double, dy: Double): Path {
        val translatedPath = androidx.compose.ui.graphics.Path()
        translatedPath.addPath(path, Offset(Float(dx), Float(dy)))
        return Path(path = translatedPath)
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Path(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is Path) return false
        return path == other.path
    }

    companion object: ShapeCompanion {
    }
}


internal fun CGAffineTransform.asMatrix(): Matrix = Matrix(floatArrayOf(Float(a), Float(b), 0.0f, 0.0f, Float(c), Float(d), 0.0f, 0.0f, Float(tx), Float(ty), 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f))

