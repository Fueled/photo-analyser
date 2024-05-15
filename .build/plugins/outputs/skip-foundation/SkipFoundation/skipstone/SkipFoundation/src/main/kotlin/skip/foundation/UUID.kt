// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


typealias NSUUID = UUID

class UUID: Comparable<UUID>, Codable, KotlinConverting<java.util.UUID>, MutableStruct {
    internal var platformValue: java.util.UUID
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(uuidString: String) {
        val uuid_0 = try { java.util.UUID.fromString(uuidString) } catch (_: Throwable) { null }
        if (uuid_0 == null) {
            throw NullReturnException()
        }
        this.platformValue = uuid_0
    }

    constructor(platformValue: java.util.UUID) {
        this.platformValue = platformValue
    }

    constructor() {
        this.platformValue = java.util.UUID.randomUUID()
    }

    constructor(from: Decoder) {
        val decoder = from
        var container = decoder.singleValueContainer()
        this.platformValue = java.util.UUID.fromString(container.decode(String::class))
    }

    override fun encode(to: Encoder) {
        val encoder = to
        var container = encoder.singleValueContainer()
        container.encode(this.uuidString)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val uuid: Any
        get() {
            fatalError()
        }

    val uuidString: String
        get() {
            // java.util.UUID is lowercase, Foundation.UUID is uppercase
            return platformValue.toString().uppercase()
        }

    val description: String
        get() = uuidString

    override fun compareTo(other: UUID): Int {
        if (this == other) return 0
        fun islessthan(lhs: UUID, rhs: UUID): Boolean {
            return lhs.platformValue < rhs.platformValue
        }
        return if (islessthan(this, other)) -1 else 1
    }

    override fun kotlin(nocopy: Boolean): java.util.UUID = platformValue.sref()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as UUID
        this.platformValue = copy.platformValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = UUID(this as MutableStruct)

    override fun toString(): String = description

    override fun equals(other: Any?): Boolean {
        if (other !is UUID) return false
        return platformValue == other.platformValue
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, platformValue)
        return result
    }

    companion object: DecodableCompanion<UUID> {

        fun fromString(uuidString: String): UUID? {
            // Java throws an exception for bad UUID, but Foundation expects it to return nil
            // return try? UUID(platformValue: PlatformUUID.fromString(uuidString)) // mistranspiles to: (PlatformUUID.companionObjectInstance as java.util.UUID.Companion).fromString(uuidString))
            return try { UUID(platformValue = java.util.UUID.fromString(uuidString)) } catch (_: Throwable) { null }
        }

        override fun init(from: Decoder): UUID = UUID(from = from)
    }
}

public fun UUID(mostSigBits: Long, leastSigBits: Long): UUID { return UUID(java.util.UUID(mostSigBits, leastSigBits)) }


