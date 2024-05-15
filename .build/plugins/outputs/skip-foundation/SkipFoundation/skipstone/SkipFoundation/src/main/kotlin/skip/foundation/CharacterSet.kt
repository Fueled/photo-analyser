// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array
import skip.lib.Set


class CharacterSet : SetAlgebra<CharacterSet, Unicode.Scalar>, MutableStruct {
    internal var platformValue: Set<UInt>
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    internal constructor(platformValue: Set<UInt>) {
        this.platformValue = platformValue
    }

    constructor() {
        this.platformValue = Set<UInt>()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(charactersIn: IntRange) {
        val range = charactersIn
        this.platformValue = SkipCrash("TODO: CharacterSet")
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(charactersIn: ClosedRange<Unicode.Scalar>) {
        val range = charactersIn
        this.platformValue = SkipCrash("TODO: CharacterSet")
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(charactersIn: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        val string = charactersIn
        this.platformValue = SkipCrash("TODO: CharacterSet")
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(bitmapRepresentation: Data) {
        val data = bitmapRepresentation
        this.platformValue = SkipCrash("TODO: CharacterSet")
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(contentsOfFile: String) {
        val file = contentsOfFile
        this.platformValue = SkipCrash("SKIP TODO: CharacterSet")
    }

    val description: String
        get() = platformValue.description

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val bitmapRepresentation: Data
        get() {
            return fatalError("SKIP TODO: CharacterSet")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val inverted: CharacterSet
        get() {
            return fatalError("SKIP TODO: CharacterSet")
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun hasMember(inPlane: UByte): Boolean {
        val plane = inPlane
        return fatalError("SKIP TODO: CharacterSet")
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun insert(charactersIn: IntRange) {
        val range = charactersIn
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun insert(charactersIn: ClosedRange<Unicode.Scalar>) {
        val range = charactersIn
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun remove(charactersIn: IntRange) {
        val range = charactersIn
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun remove(charactersIn: ClosedRange<Unicode.Scalar>) {
        val range = charactersIn
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun insert(charactersIn: String) {
        val string = charactersIn
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun remove(charactersIn: String) {
        val string = charactersIn
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun invert() {
        willmutate()
        try {
            fatalError("SKIP TODO: CharacterSet")
        } finally {
            didmutate()
        }
    }

    override fun insert(character: Unicode.Scalar): Tuple2<Boolean, Unicode.Scalar> {
        willmutate()
        try {
            val (inserted, _) = platformValue.insert(character.rawValue)
            return Tuple2(inserted, character)
        } finally {
            didmutate()
        }
    }

    override fun update(with: Unicode.Scalar): Unicode.Scalar? {
        val character = with
        willmutate()
        try {
            return if (platformValue.update(with = character.rawValue) == null) null else character
        } finally {
            didmutate()
        }
    }

    override fun remove(character: Unicode.Scalar): Unicode.Scalar? {
        willmutate()
        try {
            return if (platformValue.remove(character.rawValue) == null) null else character
        } finally {
            didmutate()
        }
    }

    override fun contains(member: Unicode.Scalar): Boolean = platformValue.contains(member.rawValue)

    override fun union(other: CharacterSet): CharacterSet = CharacterSet(platformValue = this.platformValue.union(other.platformValue))

    override fun formUnion(other: CharacterSet) {
        willmutate()
        try {
            platformValue.formUnion(other.platformValue)
        } finally {
            didmutate()
        }
    }

    override fun intersection(other: CharacterSet): CharacterSet = CharacterSet(platformValue = this.platformValue.intersection(other.platformValue))

    override fun formIntersection(other: CharacterSet) {
        willmutate()
        try {
            platformValue.formIntersection(other.platformValue)
        } finally {
            didmutate()
        }
    }

    override fun subtracting(other: CharacterSet): CharacterSet = CharacterSet(platformValue = this.platformValue.subtracting(other.platformValue))

    override fun subtract(other: CharacterSet) {
        willmutate()
        try {
            platformValue.subtract(other.platformValue)
        } finally {
            didmutate()
        }
    }

    override fun symmetricDifference(other: CharacterSet): CharacterSet = CharacterSet(platformValue = this.platformValue.symmetricDifference(other.platformValue))

    override fun formSymmetricDifference(other: CharacterSet) {
        willmutate()
        try {
            platformValue.formSymmetricDifference(other.platformValue)
        } finally {
            didmutate()
        }
    }

    override fun isSuperset(of: CharacterSet): Boolean {
        val other = of
        return platformValue.isSuperset(of = other.platformValue)
    }

    override fun isSubset(of: CharacterSet): Boolean {
        val other = of
        return platformValue.isSubset(of = other.platformValue)
    }

    override fun isDisjoint(with: CharacterSet): Boolean {
        val other = with
        return platformValue.isDisjoint(with = other.platformValue)
    }

    override fun isStrictSubset(of: CharacterSet): Boolean {
        val other = of
        return platformValue.isStrictSuperset(of = other.platformValue)
    }

    override fun isStrictSuperset(of: CharacterSet): Boolean {
        val other = of
        return platformValue.isStrictSuperset(of = other.platformValue)
    }

    override val isEmpty: Boolean
        get() = platformValue.isEmpty

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as CharacterSet
        this.platformValue = copy.platformValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = CharacterSet(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is CharacterSet) return false
        return platformValue == other.platformValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, platformValue)
        return result
    }

    companion object {

        private fun toPlatformValue(value: Array<String>): Set<UInt> {
            var set = LinkedHashSet<UInt>()
            for (str in value.sref()) {
                for (c in str.toCharArray()) {
                    set.add(UInt(c.code))
                }
            }
            return Set(collection = set, nocopy = true)
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val controlCharacters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        // Also see SkipLib.Character.isWhitespace, .whitespacesAndNewlines
        val whitespaces: CharacterSet = CharacterSet(platformValue = toPlatformValue(arrayOf(" ", "\t", "\u2029", "\u3000")))

        // Also see SkipLib.Character.isWhitespace, SkipLib.Character.isNewline, .whitespaces, .newlines
        val whitespacesAndNewlines = CharacterSet(platformValue = toPlatformValue(arrayOf(" ", "\t", "\u2029", "\u3000", "\n", "\r", "\u000B", "\u000C", "\u0085", "\u2028", "\u2029")))

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val decimalDigits: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val letters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val lowercaseLetters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val uppercaseLetters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val nonBaseCharacters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val alphanumerics: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val decomposables: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val illegalCharacters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val punctuationCharacters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val capitalizedLetters: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val symbols: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        // Also see SkipLib.Character.isNewline, .whitespacesAndNewlines
        val newlines: CharacterSet = CharacterSet(platformValue = toPlatformValue(arrayOf("\n", "\r", "\u000B", "\u000C", "\u0085", "\u2028", "\u2029")))

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val urlUserAllowed: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        val urlPasswordAllowed: CharacterSet
            get() {
                return fatalError("SKIP TODO: CharacterSet")
            }

        val urlHostAllowed: CharacterSet
            get() = urlPathAllowed

        val urlPathAllowed = CharacterSet(platformValue = toPlatformValue(arrayOf("-", ".", "_", "~", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")))

        val urlQueryAllowed = CharacterSet(platformValue = toPlatformValue(arrayOf("/", "?", "&", "=", "+", "-", ".", "_", "~", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")))

        val urlFragmentAllowed: CharacterSet
            get() = urlQueryAllowed
    }
}

