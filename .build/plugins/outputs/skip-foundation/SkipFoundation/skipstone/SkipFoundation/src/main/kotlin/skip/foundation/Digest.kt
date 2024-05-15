// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Sequence


typealias MessageDigest = java.security.MessageDigest

interface Digest: Sequence<UByte> {
    val bytes: kotlin.ByteArray
}

interface HashFunction {
    fun update(data: DataProtocol)
    fun finalize(): Digest
}

interface NamedHashFunction<Digest>: HashFunction {
    val digest: java.security.MessageDigest
    val digestName: String // Kotlin does not support static members in protocols
}

class SHA256: NamedHashFunction<SHA256Digest> {
    override val digest: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA-256")
    override val digestName = "SHA256"

    override fun update(data: DataProtocol): Unit = digest.update(data.platformData)

    override fun finalize(): SHA256Digest = SHA256Digest(bytes = digest.digest())

    companion object {

        fun hash(data: Data): SHA256Digest = SHA256Digest(bytes = SHA256().digest.digest(data.platformValue))
    }
}

class SHA256Digest: Digest {
    override val bytes: kotlin.ByteArray

    val description: String
        get() = "SHA256 digest: " + bytes.hex()

    override val iterable: kotlin.collections.Iterable<UByte>
        get() = BytesIterable(bytes = bytes)

    constructor(bytes: kotlin.ByteArray) {
        this.bytes = bytes
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SHA256Digest) return false
        return bytes == other.bytes
    }

    companion object {
    }
}

class SHA384: NamedHashFunction<SHA384Digest> {
    override val digest: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA-384")
    override val digestName = "SHA384"

    override fun update(data: DataProtocol): Unit = digest.update(data.platformData)

    override fun finalize(): SHA384Digest = SHA384Digest(bytes = digest.digest())

    companion object {

        fun hash(data: Data): SHA384Digest = SHA384Digest(bytes = SHA384().digest.digest(data.platformValue))
    }
}

class SHA384Digest: Digest {
    override val bytes: kotlin.ByteArray

    val description: String
        get() = "SHA384 digest: " + bytes.hex()

    override val iterable: kotlin.collections.Iterable<UByte>
        get() = BytesIterable(bytes = bytes)

    constructor(bytes: kotlin.ByteArray) {
        this.bytes = bytes
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SHA384Digest) return false
        return bytes == other.bytes
    }

    companion object {
    }
}

class SHA512: NamedHashFunction<SHA512Digest> {
    override val digest: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA-512")
    override val digestName = "SHA"

    override fun update(data: DataProtocol): Unit = digest.update(data.platformData)

    override fun finalize(): SHA512Digest = SHA512Digest(bytes = digest.digest())

    companion object {

        fun hash(data: Data): SHA512Digest = SHA512Digest(bytes = SHA512().digest.digest(data.platformValue))
    }
}

class SHA512Digest: Digest {
    override val bytes: kotlin.ByteArray

    val description: String
        get() = "SHA512 digest: " + bytes.hex()

    override val iterable: kotlin.collections.Iterable<UByte>
        get() = BytesIterable(bytes = bytes)

    constructor(bytes: kotlin.ByteArray) {
        this.bytes = bytes
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SHA512Digest) return false
        return bytes == other.bytes
    }

    companion object {
    }
}

class Insecure {
    class MD5: NamedHashFunction<Insecure.MD5Digest> {
        override val digest: java.security.MessageDigest = java.security.MessageDigest.getInstance("MD5")
        override val digestName = "MD5"

        override fun update(data: DataProtocol): Unit = digest.update(data.platformData)

        override fun finalize(): Insecure.MD5Digest = MD5Digest(bytes = digest.digest())

        companion object {

            fun hash(data: Data): Insecure.MD5Digest = MD5Digest(bytes = MD5().digest.digest(data.platformValue))
        }
    }

    class MD5Digest: Digest {
        override val bytes: kotlin.ByteArray

        val description: String
            get() = "MD5 digest: " + bytes.hex()

        override val iterable: kotlin.collections.Iterable<UByte>
            get() = BytesIterable(bytes = bytes)

        constructor(bytes: kotlin.ByteArray) {
            this.bytes = bytes
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Insecure.MD5Digest) return false
            return bytes == other.bytes
        }

        companion object {
        }
    }

    class SHA1: NamedHashFunction<Insecure.SHA1Digest> {
        override val digest: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA1")
        override val digestName = "SHA1"

        override fun update(data: DataProtocol): Unit = digest.update(data.platformData)

        override fun finalize(): Insecure.SHA1Digest = SHA1Digest(bytes = digest.digest())

        companion object {

            fun hash(data: Data): Insecure.SHA1Digest = SHA1Digest(bytes = SHA1().digest.digest(data.platformValue))
        }
    }

    class SHA1Digest: Digest {
        override val bytes: kotlin.ByteArray

        val description: String
            get() = "SHA1 digest: " + bytes.hex()

        override val iterable: kotlin.collections.Iterable<UByte>
            get() = BytesIterable(bytes = bytes)

        constructor(bytes: kotlin.ByteArray) {
            this.bytes = bytes
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Insecure.SHA1Digest) return false
            return bytes == other.bytes
        }

        companion object {
        }
    }

    companion object {
    }
}

// Implemented as a simple Data wrapper.
class SymmetricKey {
    val data: Data

    constructor(data: Data) {
        this.data = data.sref()
    }

    companion object {
    }
}

open class HMACMD5: DigestFunction() {

    companion object: CompanionClass() {
        override fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray {
            val message = for_
            val secret = using
            return DigestFunction.authenticationCode(for_ = message, using = secret, algorithm = "MD5")
        }
    }
    open class CompanionClass: DigestFunction.CompanionClass() {
        open fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray = HMACMD5.authenticationCode(for_ = for_, using = using)
    }
}

open class HMACSHA1: DigestFunction() {

    companion object: CompanionClass() {
        override fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray {
            val message = for_
            val secret = using
            return DigestFunction.authenticationCode(for_ = message, using = secret, algorithm = "SHA1")
        }
    }
    open class CompanionClass: DigestFunction.CompanionClass() {
        open fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray = HMACSHA1.authenticationCode(for_ = for_, using = using)
    }
}

open class HMACSHA256: DigestFunction() {

    companion object: CompanionClass() {
        override fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray {
            val message = for_
            val secret = using
            return DigestFunction.authenticationCode(for_ = message, using = secret, algorithm = "SHA256")
        }
    }
    open class CompanionClass: DigestFunction.CompanionClass() {
        open fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray = HMACSHA256.authenticationCode(for_ = for_, using = using)
    }
}

open class HMACSHA384: DigestFunction() {

    companion object: CompanionClass() {
        override fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray {
            val message = for_
            val secret = using
            return DigestFunction.authenticationCode(for_ = message, using = secret, algorithm = "SHA384")
        }
    }
    open class CompanionClass: DigestFunction.CompanionClass() {
        open fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray = HMACSHA384.authenticationCode(for_ = for_, using = using)
    }
}

open class HMACSHA512: DigestFunction() {

    companion object: CompanionClass() {
        override fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray {
            val message = for_
            val secret = using
            return DigestFunction.authenticationCode(for_ = message, using = secret, algorithm = "SHA512")
        }
    }
    open class CompanionClass: DigestFunction.CompanionClass() {
        open fun authenticationCode(for_: Data, using: SymmetricKey): kotlin.ByteArray = HMACSHA512.authenticationCode(for_ = for_, using = using)
    }
}

open class DigestFunction {

    companion object: CompanionClass() {
        override fun authenticationCode(for_: Data, using: SymmetricKey, algorithm: String): kotlin.ByteArray {
            val message = for_
            val secret = using
            val hashName = algorithm
            val secretKeySpec = javax.crypto.spec.SecretKeySpec(secret.data.platformValue, "Hmac${hashName}")
            val mac = javax.crypto.Mac.getInstance("Hmac${hashName}")
            // Skip removes .init because it assumes you want a constructor, so we need to put it back in
            mac.init(secretKeySpec)
            val signature = mac.doFinal(message.platformValue)
            return signature
        }
    }
    open class CompanionClass {
        internal open fun authenticationCode(for_: Data, using: SymmetricKey, algorithm: String): kotlin.ByteArray = DigestFunction.authenticationCode(for_ = for_, using = using, algorithm = algorithm)
    }
}

fun kotlin.ByteArray.hex(): String {
    return joinToString("") { it -> java.lang.Byte.toUnsignedInt(it).toString(radix = 16).padStart(2, "0".get(0)) }
}

internal class BytesIterable: kotlin.collections.Iterable<UByte> {
    internal val bytes: kotlin.ByteArray

    override fun iterator(): kotlin.collections.Iterator<UByte> = Iterator(iterator = bytes.iterator())

    internal class Iterator: kotlin.collections.Iterator<UByte> {
        internal val iterator: kotlin.collections.Iterator<Byte>

        override fun hasNext(): Boolean = iterator.hasNext()

        override fun next(): UByte = UByte(iterator.next())

        constructor(iterator: kotlin.collections.Iterator<Byte>) {
            this.iterator = iterator.sref()
        }
    }

    constructor(bytes: kotlin.ByteArray) {
        this.bytes = bytes
    }
}

