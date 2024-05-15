// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib

typealias CGFloat = Double

class CGPoint: MutableStruct {
    var x: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var y: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(x: Double = 0.0, y: Double = 0.0) {
        this.x = x
        this.y = y
    }

    fun applying(transform: CGAffineTransform): CGPoint {
        val px = transform.a * x + transform.c * y + transform.tx
        val py = transform.b * x + transform.d * y + transform.ty
        return CGPoint(x = px, y = py)
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as CGPoint
        this.x = copy.x
        this.y = copy.y
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = CGPoint(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is CGPoint) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, x)
        result = Hasher.combine(result, y)
        return result
    }

    companion object {
        val zero = CGPoint()
    }
}

class CGSize: MutableStruct {
    var width: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var height: Double
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(width: Double = 0.0, height: Double = 0.0) {
        this.width = width
        this.height = height
    }

    fun applying(transform: CGAffineTransform): CGSize {
        val swidth = transform.a * width + transform.c * height
        val sheight = transform.b * width + transform.d * height
        return CGSize(width = swidth, height = sheight)
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as CGSize
        this.width = copy.width
        this.height = copy.height
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = CGSize(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is CGSize) return false
        return width == other.width && height == other.height
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, width)
        result = Hasher.combine(result, height)
        return result
    }

    companion object {
        val zero = CGSize()
    }
}

class CGRect: MutableStruct {
    var origin: CGPoint
        get() = field.sref({ this.origin = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    var size: CGSize
        get() = field.sref({ this.size = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(origin: CGPoint = CGPoint.zero, size: CGSize = CGSize.zero) {
        this.origin = origin
        this.size = size
    }

    constructor(x: Double, y: Double, width: Double, height: Double): this(origin = CGPoint(x = x, y = y), size = CGSize(width = width, height = height)) {
    }

    constructor(x: Int, y: Int, width: Int, height: Int): this(origin = CGPoint(x = Double(x), y = Double(y)), size = CGSize(width = Double(width), height = Double(height))) {
    }

    var height: Double
        get() = size.height
        set(newValue) {
            size.height = newValue
        }

    var width: Double
        get() = size.width
        set(newValue) {
            size.width = newValue
        }

    val minX: Double
        get() = if (width >= 0.0) origin.x else origin.x + width

    val midX: Double
        get() = (minX + maxX) / 2.0

    val maxX: Double
        get() = if (width >= 0.0) origin.x + width else origin.x

    val minY: Double
        get() = if (height >= 0.0) origin.y else origin.y + height

    val midY: Double
        get() = (minY + maxY) / 2.0

    val maxY: Double
        get() = if (height >= 0.0) origin.y + height else origin.y

    val standardized: CGRect
        get() = CGRect(x = minX, y = minY, width = abs(width), height = abs(height))

    val integral: CGRect
        get() {
            if (isInfinite || isNull) {
                return this
            }
            val rect = standardized.sref()
            return CGRect(x = floor(rect.minX), y = floor(rect.minY), width = ceil(rect.width), height = ceil(rect.height))
        }

    fun applying(transform: CGAffineTransform): CGRect {
        if (isInfinite || isNull) {
            return this.sref()
        }
        return CGRect(origin = origin.applying(transform), size = size.applying(transform))
    }

    fun insetBy(dx: Double, dy: Double): CGRect {
        if (isInfinite || isNull) {
            return this.sref()
        }
        val rect = standardized.sref()
        return CGRect(x = rect.minX + dx, y = rect.minY + dy, width = rect.width - dx * 2.0, height = rect.height - dy * 2.0)
    }

    fun offsetBy(dx: Double, dy: Double): CGRect {
        if (isInfinite || isNull) {
            return this.sref()
        }
        val rect = standardized.sref()
        return CGRect(x = rect.minX + dx, y = rect.minY + dy, width = rect.width, height = rect.height)
    }

    fun union(other: CGRect): CGRect {
        if (other.isEmpty) {
            return this.sref()
        } else if (isEmpty) {
            return other.sref()
        }
        if (other.isInfinite || isInfinite) {
            return CGRect.infinite.sref()
        }
        val rect1 = standardized.sref()
        val rect2 = other.standardized.sref()
        val minX = min(rect1.minX, rect2.minX)
        val maxX = max(rect1.maxX, rect2.maxX)
        val minY = min(rect1.minY, rect2.minY)
        val maxY = max(rect1.maxY, rect2.maxY)
        return CGRect(x = minX, y = minY, width = maxX - minX, height = maxY - minY)
    }

    fun intersection(other: CGRect): CGRect {
        if (other.isEmpty || isEmpty) {
            return CGRect.null_.sref()
        }
        if (other.isInfinite) {
            return this.sref()
        } else if (isInfinite) {
            return other.sref()
        }
        val rect1 = standardized.sref()
        val rect2 = other.standardized.sref()
        val minX = max(rect1.minX, rect2.minX)
        val maxX = min(rect1.maxX, rect2.maxX)
        val minY = max(rect1.minY, rect2.minY)
        val maxY = min(rect1.maxY, rect2.maxY)
        return CGRect(x = minX, y = minY, width = max(0.0, maxX - minX), height = max(0.0, maxY - minY))
    }

    fun intersects(other: CGRect): Boolean = !intersection(other).isEmpty

    fun contains(point: CGPoint): Boolean {
        val rect = standardized.sref()
        return point.x >= rect.minX && point.x <= rect.maxX && point.y >= rect.minY && point.y <= rect.maxY
    }

    fun contains(rect: CGRect): Boolean = intersection(rect) == rect

    val isEmpty: Boolean
        get() = width == 0.0 && height == 0.0

    val isInfinite: Boolean
        get() = this == CGRect.infinite

    val isNull: Boolean
        get() = this == CGRect.null_

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as CGRect
        this.origin = copy.origin
        this.size = copy.size
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = CGRect(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is CGRect) return false
        return origin == other.origin && size == other.size
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, origin)
        result = Hasher.combine(result, size)
        return result
    }

    companion object {
        val zero = CGRect()
        val null_ = CGRect(x = Double.infinity, y = Double.infinity, width = 0.0, height = 0.0)
        val infinite = CGRect(x = -Double.MAX_VALUE / 2.0, y = -Double.MAX_VALUE / 2.0, width = Double.MAX_VALUE, height = Double.MAX_VALUE)
    }
}

class CGAffineTransform: Codable, MutableStruct {
    var a = 1.0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var b = 0.0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var c = 0.0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var d = 1.0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var tx = 0.0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var ty = 0.0
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor() {
    }

    constructor(a: Double, b: Double, c: Double, d: Double, tx: Double, ty: Double) {
        this.a = a
        this.b = b
        this.c = c
        this.d = d
        this.tx = tx
        this.ty = ty
    }

    constructor(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float) {
        this.a = Double(a)
        this.b = Double(b)
        this.c = Double(c)
        this.d = Double(d)
        this.tx = Double(tx)
        this.ty = Double(ty)
    }

    constructor(rotationAngle: Double) {
        val sinus = sin(rotationAngle)
        val cosinus = cos(rotationAngle)
        a = cosinus
        b = sinus
        c = -sinus
        d = cosinus
    }

    constructor(scaleX: Double, y: Double, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        a = scaleX
        d = y
    }

    constructor(translationX: Double, y: Double) {
        tx = translationX
        ty = y
    }

    val isIdentity: Boolean
        get() = this == Companion.identity

    fun concatenating(transform: CGAffineTransform): CGAffineTransform {
        var result = CGAffineTransform()
        result.a = transform.a * a + transform.b * c
        result.b = transform.a * b + transform.b * d
        result.c = transform.c * a + transform.d * c
        result.d = transform.c * b + transform.d * d
        result.tx = transform.tx * a + transform.ty * c + tx
        result.ty = transform.tx * b + transform.ty * d + ty
        return result.sref()
    }

    fun inverted(): CGAffineTransform {
        val determinant = a * d - c * b
        if (determinant == 0.0) {
            return this.sref()
        }

        var result = CGAffineTransform()
        result.a = d / determinant
        result.b = -b / determinant
        result.c = -c / determinant
        result.d = a / determinant
        result.tx = (-d * tx + c * ty) / determinant
        result.ty = (b * tx - a * ty) / determinant
        return result.sref()
    }

    fun rotated(by: Double): CGAffineTransform {
        val angle = by
        return concatenating(CGAffineTransform(rotationAngle = angle))
    }

    fun scaledBy(x: Double, y: Double): CGAffineTransform = concatenating(CGAffineTransform(scaleX = x, y = y))

    fun translatedBy(x: Double, y: Double): CGAffineTransform = concatenating(CGAffineTransform(translationX = x, y = y))

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as CGAffineTransform
        this.a = copy.a
        this.b = copy.b
        this.c = copy.c
        this.d = copy.d
        this.tx = copy.tx
        this.ty = copy.ty
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = CGAffineTransform(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is CGAffineTransform) return false
        return a == other.a && b == other.b && c == other.c && d == other.d && tx == other.tx && ty == other.ty
    }

    private enum class CodingKeys(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): CodingKey, RawRepresentable<String> {
        a("a"),
        b("b"),
        c("c"),
        d("d"),
        tx("tx"),
        ty("ty");
    }

    override fun encode(to: Encoder) {
        val container = to.container(keyedBy = CodingKeys::class)
        container.encode(a, forKey = CodingKeys.a)
        container.encode(b, forKey = CodingKeys.b)
        container.encode(c, forKey = CodingKeys.c)
        container.encode(d, forKey = CodingKeys.d)
        container.encode(tx, forKey = CodingKeys.tx)
        container.encode(ty, forKey = CodingKeys.ty)
    }

    constructor(from: Decoder) {
        val container = from.container(keyedBy = CodingKeys::class)
        this.a = container.decode(Double::class, forKey = CodingKeys.a)
        this.b = container.decode(Double::class, forKey = CodingKeys.b)
        this.c = container.decode(Double::class, forKey = CodingKeys.c)
        this.d = container.decode(Double::class, forKey = CodingKeys.d)
        this.tx = container.decode(Double::class, forKey = CodingKeys.tx)
        this.ty = container.decode(Double::class, forKey = CodingKeys.ty)
    }

    companion object: DecodableCompanion<CGAffineTransform> {

        val identity = CGAffineTransform()

        override fun init(from: Decoder): CGAffineTransform = CGAffineTransform(from = from)

        private fun CodingKeys(rawValue: String): CodingKeys? {
            return when (rawValue) {
                "a" -> CodingKeys.a
                "b" -> CodingKeys.b
                "c" -> CodingKeys.c
                "d" -> CodingKeys.d
                "tx" -> CodingKeys.tx
                "ty" -> CodingKeys.ty
                else -> null
            }
        }
    }
}
