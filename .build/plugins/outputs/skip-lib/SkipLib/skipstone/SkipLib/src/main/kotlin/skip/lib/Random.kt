// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib

interface RandomNumberGenerator {
    fun next(): ULong
}

class SystemRandomNumberGenerator: RawRepresentable<java.security.SecureRandom>, RandomNumberGenerator {
    override val rawValue: java.security.SecureRandom

    constructor(rawValue: java.security.SecureRandom = java.security.SecureRandom()) {
        this.rawValue = rawValue.sref()
    }

    override fun next(): ULong = rawValue.nextLong().toULong()

    companion object {
    }
}

/// A seeded random number generator that is not cryptographically secure.
/// Provided for use in randomized testing, etc.
class PseudoRandomNumberGenerator: RawRepresentable<java.util.Random>, RandomNumberGenerator {
    override val rawValue: java.util.Random

    constructor(rawValue: java.util.Random = java.util.Random()) {
        this.rawValue = rawValue.sref()
    }

    override fun next(): ULong = rawValue.nextLong().toULong()

    companion object {

        fun seeded(seed: Long): PseudoRandomNumberGenerator = PseudoRandomNumberGenerator(rawValue = java.util.Random(seed))
    }
}
