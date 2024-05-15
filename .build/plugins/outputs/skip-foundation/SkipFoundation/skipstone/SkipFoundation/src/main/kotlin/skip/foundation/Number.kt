// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


typealias Decimal = java.math.BigDecimal
typealias NSDecimalNumber = java.math.BigDecimal
typealias NSNumber = java.lang.Number

val java.lang.Number.doubleValue: Double
    get() = doubleValue()
val java.lang.Number.intValue: Int
    get() = intValue()
val java.lang.Number.longValue: Long
    get() = longValue()
val java.lang.Number.int64Value: Long
    get() = longValue()
val java.lang.Number.int32Value: Int
    get() = intValue()
val java.lang.Number.int16Value: Short
    get() = shortValue()
val java.lang.Number.int8Value: Byte
    get() = byteValue()

// Initializing an NSNumber with a numeric value just returns the instance itself
fun NSNumber(value: Byte): java.lang.Number = value as java.lang.Number
fun NSNumber(value: Short): java.lang.Number = value as java.lang.Number
fun NSNumber(value: Int): java.lang.Number = value as java.lang.Number
fun NSNumber(value: Long): java.lang.Number = value as java.lang.Number
fun NSNumber(value: UByte): java.lang.Number = value as java.lang.Number
fun NSNumber(value: UShort): java.lang.Number = value as java.lang.Number
fun NSNumber(value: UInt): java.lang.Number = value as java.lang.Number
fun NSNumber(value: ULong): java.lang.Number = value as java.lang.Number
fun NSNumber(value: Float): java.lang.Number = value as java.lang.Number
fun NSNumber(value: Double): java.lang.Number = value as java.lang.Number

// NSNumber also accepts unlabeled values. Add an additional unused argument to satisfy the Kotlin compiler that they are different functions.
fun NSNumber(v: Byte, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: Short, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: Int, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: Long, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: UByte, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: UShort, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: UInt, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: ULong, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: Float, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number
fun NSNumber(v: Double, unusedp: Unit? = null): java.lang.Number = v as java.lang.Number

