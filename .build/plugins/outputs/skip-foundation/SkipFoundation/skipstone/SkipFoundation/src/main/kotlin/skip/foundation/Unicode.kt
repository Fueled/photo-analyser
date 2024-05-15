// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


// This code is adapted from https://github.com/apple/swift-corelibs-foundation/blob/main/Tests/Foundation/Tests which has the following license:

//===----------------------------------------------------------------------===//
//
// This source file is part of the Swift.org open source project
//
// Copyright (c) 2014 - 2021 Apple Inc. and the Swift project authors
// Licensed under Apache License v2.0 with Runtime Library Exception
//
// See https://swift.org/LICENSE.txt for license information
// See https://swift.org/CONTRIBUTORS.txt for the list of Swift project authors
//
//===----------------------------------------------------------------------===//

typealias UnicodeScalarValue = UInt

@Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
fun String(scalar: Unicode.Scalar): String {
    return fatalError("SKIP TODO")
}

class Unicode {
    class ASCII {

        companion object {
        }
    }

    class UTF16: Sendable {
        //case _swift3Buffer(Unicode.UTF16.ForwardParser)

        companion object {
        }
    }

    class UTF32: Sendable {
        //case _swift3Codec

        companion object {
        }
    }

    class UTF8: Sendable {
        //case struct(Unicode.UTF8.ForwardParser)

        companion object {
        }
    }

    sealed class ParseResult<out T> {
        class ValidCase<T>(val associated0: T): ParseResult<T>() {
        }
        class EmptyInputCase: ParseResult<Nothing>() {
        }
        class ErrorCase(val associated0: Int): ParseResult<Nothing>() {
            val length = associated0
        }

        companion object {
            fun <T> valid(associated0: T): ParseResult<T> = ValidCase(associated0)
            val emptyInput: ParseResult<Nothing> = EmptyInputCase()
            fun error(length: Int): ParseResult<Nothing> = ErrorCase(length)
        }
    }

    class Scalar: RawRepresentable<UInt>, Comparable<Unicode.Scalar>, Sendable {
        override val rawValue: UInt

        constructor(rawValue: UInt, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
            this.rawValue = rawValue
        }

        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override fun compareTo(other: Unicode.Scalar): Int {
            if (this == other) return 0
            fun islessthan(lhs: Unicode.Scalar, rhs: Unicode.Scalar): Boolean {
                return lhs.rawValue < rhs.rawValue
            }
            return if (islessthan(this, other)) -1 else 1
        }


        override fun equals(other: Any?): Boolean {
            if (other !is Unicode.Scalar) return false
            return rawValue == other.rawValue
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, rawValue)
            return result
        }

        companion object {
        }
    }

    companion object {
    }
}

