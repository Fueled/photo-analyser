// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib


import kotlin.math.pow
import kotlin.math.IEEErem

enum class FloatingPointRoundingRule {
    toNearestOrAwayFromZero,
    toNearestOrEven,
    up,
    down,
    towardZero,
    awayFromZero;

    companion object {
    }
}

fun acosf(x: Float): Float = kotlin.math.acos(x)

fun acos(x: Double): Double = kotlin.math.acos(x)

fun acosl(x: Double): Double = kotlin.math.acos(x)

fun asinf(x: Float): Float = kotlin.math.asin(x)

fun asin(x: Double): Double = kotlin.math.asin(x)

fun asinl(x: Double): Double = kotlin.math.asin(x)

fun atanf(x: Float): Float = kotlin.math.atan(x)

fun atan(x: Double): Double = kotlin.math.atan(x)

fun atanl(x: Double): Double = kotlin.math.atan(x)

fun atan2f(x: Float, y: Float): Float = kotlin.math.atan2(x, y)

fun atan2(x: Double, y: Double): Double = kotlin.math.atan2(x, y)

fun atan2l(x: Double, y: Double): Double = kotlin.math.atan2(x, y)

fun cosf(x: Float): Float = kotlin.math.cos(x)

fun cos(x: Double): Double = kotlin.math.cos(x)

fun cosl(x: Double): Double = kotlin.math.cos(x)

fun sinf(x: Float): Float = kotlin.math.sin(x)

fun sin(x: Double): Double = kotlin.math.sin(x)

fun sinl(x: Double): Double = kotlin.math.sin(x)

fun tanf(x: Float): Float = kotlin.math.tan(x)

fun tan(x: Double): Double = kotlin.math.tan(x)

fun tanl(x: Double): Double = kotlin.math.tan(x)

fun acoshf(x: Float): Float = kotlin.math.acosh(x)

fun acosh(x: Double): Double = kotlin.math.acosh(x)

fun acoshl(x: Double): Double = kotlin.math.acosh(x)

fun asinhf(x: Float): Float = kotlin.math.asinh(x)

fun asinh(x: Double): Double = kotlin.math.asinh(x)

fun asinhl(x: Double): Double = kotlin.math.asinh(x)

fun atanhf(x: Float): Float = kotlin.math.atanh(x)

fun atanh(x: Double): Double = kotlin.math.atanh(x)

fun atanhl(x: Double): Double = kotlin.math.atanh(x)

fun coshf(x: Float): Float = kotlin.math.cosh(x)

fun cosh(x: Double): Double = kotlin.math.cosh(x)

fun coshl(x: Double): Double = kotlin.math.cosh(x)

fun sinhf(x: Float): Float = kotlin.math.sinh(x)

fun sinh(x: Double): Double = kotlin.math.sinh(x)

fun sinhl(x: Double): Double = kotlin.math.sinh(x)

fun tanhf(x: Float): Float = kotlin.math.tanh(x)

fun tanh(x: Double): Double = kotlin.math.tanh(x)

fun tanhl(x: Double): Double = kotlin.math.tanh(x)

fun expf(x: Float): Float = kotlin.math.exp(x)

fun exp(x: Double): Double = kotlin.math.exp(x)

fun expl(x: Double): Double = kotlin.math.exp(x)

fun exp2f(x: Float): Float = powf(2.0f, x)

fun exp2(x: Double): Double = pow(2.0, x)

fun exp2l(x: Double): Double = pow(2.0, x)

fun expm1f(x: Float): Float = kotlin.math.expm1(x)

fun expm1(x: Double): Double = kotlin.math.expm1(x)

fun expm1l(x: Double): Double = kotlin.math.expm1(x)

fun logf(x: Float): Float = kotlin.math.log(x, kotlin.math.E.toFloat())

fun log(x: Double): Double = kotlin.math.log(x, kotlin.math.E)

fun logl(x: Double): Double = kotlin.math.log(x, kotlin.math.E)

fun log10f(x: Float): Float = kotlin.math.log10(x)

fun log10(x: Double): Double = kotlin.math.log10(x)

fun log10l(x: Double): Double = kotlin.math.log10(x)

fun log2f(x: Float): Float = kotlin.math.log2(x)

fun log2(x: Double): Double = kotlin.math.log2(x)

fun log2l(x: Double): Double = kotlin.math.log2(x)

fun log1pf(x: Float): Float = kotlin.math.ln(1.0f + x)

fun log1p(x: Double): Double = kotlin.math.ln(1.0 + x)

fun log1pl(x: Double): Double = kotlin.math.ln(1.0 + x)

fun logbf(x: Float): Float = (kotlin.math.ln(1.0f + x) / kotlin.math.ln(2.0f)).sref()

fun logb(x: Double): Double = (kotlin.math.ln(1.0 + x) / kotlin.math.ln(2.0)).sref()

fun logbl(x: Double): Double = (kotlin.math.ln(1.0 + x) / kotlin.math.ln(2.0)).sref()

fun abs(x: Double): Double = kotlin.math.abs(x)

fun abs(x: Int): Int = kotlin.math.abs(x)

fun abs(x: Long): Long = kotlin.math.abs(x)

fun fabsf(x: Float): Float = kotlin.math.abs(x)

fun fabs(x: Double): Double = kotlin.math.abs(x)

fun fabsl(x: Double): Double = kotlin.math.abs(x)

fun cbrtf(x: Float): Float = kotlin.math.cbrt(x)

fun cbrt(x: Double): Double = kotlin.math.cbrt(x)

fun cbrtl(x: Double): Double = kotlin.math.cbrt(x)

fun hypotf(x: Float, y: Float): Float = kotlin.math.hypot(x, y)

fun hypot(x: Double, y: Double): Double = kotlin.math.hypot(x, y)

fun hypotl(x: Double, y: Double): Double = kotlin.math.hypot(x, y)

fun powf(x: Float, y: Float): Float = x.pow(y)

fun pow(x: Double, y: Double): Double = x.pow(y)

fun powl(x: Double, y: Double): Double = x.pow(y)

fun sqrtf(x: Float): Float = kotlin.math.sqrt(x)

fun sqrt(x: Double): Double = kotlin.math.sqrt(x)

fun sqrtl(x: Double): Double = kotlin.math.sqrt(x)

fun ceilf(x: Float): Float = kotlin.math.ceil(x)

fun ceil(x: Double): Double = kotlin.math.ceil(x)

fun ceill(x: Double): Double = kotlin.math.ceil(x)

fun floorf(x: Float): Float = kotlin.math.floor(x)

fun floor(x: Double): Double = kotlin.math.floor(x)

fun floorl(x: Double): Double = kotlin.math.floor(x)

fun roundf(x: Float): Float = kotlin.math.round(x)

fun round(x: Double): Double = kotlin.math.round(x)

fun roundl(x: Double): Double = kotlin.math.round(x)

fun fmodf(x: Float, y: Float): Float = x % y

fun fmod(x: Double, y: Double): Double = x % y

fun fmodl(x: Double, y: Double): Double = x % y

fun remainderf(x: Float, y: Float): Float = x.IEEErem(y)

fun remainder(x: Double, y: Double): Double = x.IEEErem(y)

fun remainderl(x: Double, y: Double): Double = x.IEEErem(y)

fun fmaxf(x: Float, y: Float): Float = max(x, y)

fun fmax(x: Double, y: Double): Double = max(x, y)

fun fmaxl(x: Double, y: Double): Double = max(x, y)

fun fminf(x: Float, y: Float): Float = min(x, y)

fun fmin(x: Double, y: Double): Double = fmin(x, y)

fun fminl(x: Double, y: Double): Double = fmin(x, y)

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lroundf(x: Float): Int {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lround(x: Double): Int {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lroundl(x: Double): Int {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun truncf(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun trunc(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun truncl(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nearbyintf(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nearbyint(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nearbyintl(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun rintf(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun rint(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun rintl(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lrintf(x: Float): Int {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lrint(x: Double): Int {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lrintl(x: Double): Int {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun erff(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun erf(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun erfl(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun erfcf(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun erfc(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun erfcl(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lgammaf(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lgamma(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun lgammal(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun tgammaf(x: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun tgamma(x: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun tgammal(x: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun modff(x: Float, y: Any): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun modf(x: Double, y: Any): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun modfl(x: Double, y: Any): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun ldexpf(x: Float, y: Int): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun ldexp(x: Double, y: Int): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun ldexpl(x: Double, y: Int): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun frexpf(x: Float, y: Any): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun frexp(x: Double, y: Any): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun frexpl(x: Double, y: Any): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun ilogbf(x: Float): Int {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun ilogb(x: Double): Int {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun ilogbl(x: Double): Int {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun scalbnf(x: Float, y: Int): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun scalbn(x: Double, y: Int): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun scalbnl(x: Double, y: Int): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun scalblnf(x: Float, y: Int): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun scalbln(x: Double, y: Int): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun scalblnl(x: Double, y: Int): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun remquof(x: Float, y: Float, z: Any): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun remquo(x: Double, y: Double, z: Any): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun remquol(x: Double, y: Double, z: Any): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun copysignf(x: Float, y: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun copysign(x: Double, y: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun copysignl(x: Double, y: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nanf(x: Any): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nan(x: Any): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nanl(x: Any): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nextafterf(x: Float, y: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nextafter(x: Double, y: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nextafterl(x: Double, y: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nexttowardf(x: Float, y: Double): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nexttoward(x: Double, y: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun nexttowardl(x: Double, y: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun fdimf(x: Float, y: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun fdim(x: Double, y: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun fdiml(x: Double, y: Double): Double {
    fatalError()
}

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun fmaf(x: Float, y: Float, z: Float): Float {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun fma(x: Double, y: Double, z: Double): Double {
    fatalError()
}
@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun fmal(x: Double, y: Double, z: Double): Double {
    fatalError()
}

