// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array
import skip.lib.Collection
import skip.lib.Sequence


internal typealias PlatformData = kotlin.ByteArray
typealias NSData = Data

interface DataProtocol {
    val platformData: kotlin.ByteArray
}

class Data: DataProtocol, Codable, KotlinConverting<kotlin.ByteArray>, MutableStruct {
    var platformValue: kotlin.ByteArray
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(platformValue: kotlin.ByteArray) {
        this.platformValue = platformValue
    }

    constructor(data: Data) {
        this.platformValue = data.platformValue
    }

    constructor(bytes: Array<UByte>, length: Int? = null) {
        this.platformValue = kotlin.ByteArray(size = length ?: bytes.count, init = { it -> bytes[it].toByte() })
    }

    constructor(base64Encoded: String, options: Data.Base64DecodingOptions = Data.Base64DecodingOptions.of()) {
        val data_0 = try { java.util.Base64.getDecoder().decode(base64Encoded) } catch (_: Throwable) { null }
        if (data_0 == null) {
            throw NullReturnException()
        }
        this.platformValue = data_0
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(base64Encoded: Data, options: Data.Base64DecodingOptions = Data.Base64DecodingOptions.of()) {
        val base64Data = base64Encoded
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    constructor() {
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    constructor(count: Int) {
        this.platformValue = kotlin.ByteArray(size = count)
    }

    constructor(capacity: Int, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        // No equivalent kotlin.ByteArray(capacity:), so allocate with zero
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(bytes: Any, count: Int) {
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(buffer: Any) {
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(repeating: UByte, count: Int) {
        val repeatedValue = repeating
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(bytesNoCopy: Any, count: Int, deallocator: Data.Deallocator) {
        val bytes = bytesNoCopy
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(elements: Sequence<UByte>) {
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    constructor(referencing: Data, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        val reference = referencing
        this.platformValue = kotlin.ByteArray(size = 0)
    }

    constructor(contentsOfFile: String) {
        val filePath = contentsOfFile
        this.platformValue = java.io.File(filePath).readBytes()
    }

    constructor(contentsOf: URL, options: Data.ReadingOptions = Data.ReadingOptions.of()) {
        val url = contentsOf
        this.platformValue = url.platformValue.readBytes()
    }

    constructor(checksum: Digest): this(checksum.bytes) {
    }

    constructor(from: Decoder) {
        val decoder = from
        var container = decoder.unkeyedContainer()
        var bytes: Array<UByte> = arrayOf()
        while (!container.isAtEnd) {
            bytes.append(container.decode(UByte::class))
        }
        this.platformValue = kotlin.ByteArray(size = bytes.count, init = { it -> bytes[it].toByte() })
    }

    override fun encode(to: Encoder) {
        val encoder = to
        var container = encoder.unkeyedContainer()
        for (b in this.bytes.sref()) {
            container.encode(b)
        }
    }

    override val platformData: kotlin.ByteArray
        get() = platformValue

    val description: String
        get() = platformValue.description

    val count: Int
        get() = platformValue.size

    val isEmpty: Boolean
        get() = count == 0

    val bytes: Array<UByte>
        get() {
            return Array(platformValue.map { it -> it.toUByte() })
        }

    // Platform declaration clash: The following declarations have the same JVM signature (<init>(Lskip/lib/Array;)V):
    //public init(_ bytes: [Int]) {
    //    self.platformValue = PlatformData(size: bytes.count, init: {
    //        bytes[$0].toByte()
    //    })
    //}

    val utf8String: String?
        get() = String(data = this, encoding = StringEncoding.utf8)

    fun base64EncodedString(): String = java.util.Base64.getEncoder().encodeToString(platformValue)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun base64EncodedData(options: Data.Base64EncodingOptions = Data.Base64EncodingOptions.of()): Data {
        fatalError()
    }

    fun sha256(): Data = Data(SHA256.hash(data = this).bytes)

    fun hex(): String = platformValue.hex()

    fun reserveCapacity(minimumCapacity: Int) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val regions: Collection<Data>
        get() {
            fatalError()
        }

    @Deprecated("withUnsafeBytes requires import SkipFFI")
    internal fun withUnsafeBytes(body: (Any) -> Any): Any {
        fatalError()
    }

    @Deprecated("withUnsafeMutableBytes requires import SkipFFI")
    internal fun withUnsafeMutableBytes(body: (Any) -> Any): Any {
        willmutate()
        try {
            fatalError()
        } finally {
            didmutate()
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun copyBytes(to: Any, count: Int) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun copyBytes(to: Any, from: IntRange) = Unit

    // public func copyBytes<DestinationType>(to buffer: UnsafeMutableBufferPointer<DestinationType>, from range: Range<Data.Index>? = nil) -> Int

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun append(bytes: Any, count: Int) = Unit

    fun append(other: Data) {
        willmutate()
        try {
            append(contentsOf = other)
        } finally {
            didmutate()
        }
    }

    fun append(contentsOf: Array<UByte>) {
        val bytes = contentsOf
        willmutate()
        try {
            this.platformValue += Data(bytes).platformValue
        } finally {
            didmutate()
        }
    }

    // This should be append(contentsOf: any Sequence<UInt8>), but Data does not yet conform to Sequence
    fun append(contentsOf: Data, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
        val data = contentsOf
        willmutate()
        try {
            this.platformValue += data.platformValue
        } finally {
            didmutate()
        }
    }

    // public mutating func append<SourceType>(_ buffer: UnsafeBufferPointer<SourceType>)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun resetBytes(in_: IntRange) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun replaceSubrange(subrange: IntRange, with: Data) = Unit

    // public mutating func replaceSubrange<SourceType>(_ subrange: Range<Data.Index>, with buffer: UnsafeBufferPointer<SourceType>)

    // public mutating func replaceSubrange<ByteCollection>(_ subrange: Range<Data.Index>, with newElements: ByteCollection) where ByteCollection : Collection, ByteCollection.Element == UInt8

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun replaceSubrange(subrange: IntRange, with: Any, count: Int) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun subdata(in_: IntRange): Data {
        val range = in_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun range(of: Data, options: Data.SearchOptions = Data.SearchOptions.of(), in_: IntRange? = null): IntRange? {
        val dataToFind = of
        val range = in_
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun advanced(by: Int): Data {
        val amount = by
        fatalError()
    }

    operator fun get(index: Int): UByte = UByte(platformValue.get(index))

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    operator fun get(bounds: IntRange): Data {
        fatalError()
    }

    fun write(to: URL, options: Data.WritingOptions = Data.WritingOptions.of()) {
        val url = to
        var opts: Array<java.nio.file.StandardOpenOption> = arrayOf()
        opts.append(java.nio.file.StandardOpenOption.CREATE)
        opts.append(java.nio.file.StandardOpenOption.WRITE)
        if (options.contains(Data.WritingOptions.atomic)) {
            opts.append(java.nio.file.StandardOpenOption.DSYNC)
        }
        java.nio.file.Files.write(url.toPath(), platformValue, *(opts.toList().toTypedArray()))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Data) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.platformValue.contentEquals(rhs.platformValue)
    }

    override fun hashCode(): Int {
        var hasher = Hasher()
        hash(into = InOut<Hasher>({ hasher }, { hasher = it }))
        return hasher.finalize()
    }
    fun hash(into: InOut<Hasher>) {
        val hasher = into
        hasher.value.combine(platformValue.hashCode())
    }

    sealed class Deallocator {
        class VirtualMemoryCase: Deallocator() {
        }
        class UnmapCase: Deallocator() {
        }
        class FreeCase: Deallocator() {
        }
        class NoneCase: Deallocator() {
        }
        class CustomCase(val associated0: (Any, Int) -> Unit): Deallocator() {
        }

        companion object {
            val virtualMemory: Deallocator = VirtualMemoryCase()
            val unmap: Deallocator = UnmapCase()
            val free: Deallocator = FreeCase()
            val none: Deallocator = NoneCase()
            fun custom(associated0: (Any, Int) -> Unit): Deallocator = CustomCase(associated0)
        }
    }

    class ReadingOptions: OptionSet<Data.ReadingOptions, Int>, Sendable, MutableStruct {
        override var rawValue: Int
        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): Data.ReadingOptions = ReadingOptions(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: Data.ReadingOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Data.ReadingOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Data.ReadingOptions(this as MutableStruct)

        private fun assignfrom(target: Data.ReadingOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val mappedIfSafe = ReadingOptions(rawValue = 1)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val uncached = ReadingOptions(rawValue = 2)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val alwaysMapped = ReadingOptions(rawValue = 4)

            fun of(vararg options: Data.ReadingOptions): Data.ReadingOptions {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return ReadingOptions(rawValue = value)
            }
        }
    }

    class WritingOptions: OptionSet<Data.WritingOptions, Int>, Sendable, MutableStruct {
        override var rawValue: Int
        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): Data.WritingOptions = WritingOptions(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: Data.WritingOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Data.WritingOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Data.WritingOptions(this as MutableStruct)

        private fun assignfrom(target: Data.WritingOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            val atomic = WritingOptions(rawValue = 1)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val withoutOverwriting = WritingOptions(rawValue = 2)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val noFileProtection = WritingOptions(rawValue = 4)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val completeFileProtection = WritingOptions(rawValue = 8)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val completeFileProtectionUnlessOpen = WritingOptions(rawValue = 16)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val completeFileProtectionUntilFirstUserAuthentication = WritingOptions(rawValue = 32)

            fun of(vararg options: Data.WritingOptions): Data.WritingOptions {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return WritingOptions(rawValue = value)
            }
        }
    }

    class SearchOptions: OptionSet<Data.SearchOptions, Int>, Sendable, MutableStruct {
        override var rawValue: Int
        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): Data.SearchOptions = SearchOptions(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: Data.SearchOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Data.SearchOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Data.SearchOptions(this as MutableStruct)

        private fun assignfrom(target: Data.SearchOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val backwards = SearchOptions(rawValue = 1)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val anchored = SearchOptions(rawValue = 2)

            fun of(vararg options: Data.SearchOptions): Data.SearchOptions {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return SearchOptions(rawValue = value)
            }
        }
    }

    class Base64EncodingOptions: OptionSet<Data.Base64EncodingOptions, Int>, Sendable, MutableStruct {
        override var rawValue: Int
        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): Data.Base64EncodingOptions = Base64EncodingOptions(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: Data.Base64EncodingOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Data.Base64EncodingOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Data.Base64EncodingOptions(this as MutableStruct)

        private fun assignfrom(target: Data.Base64EncodingOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val lineLength64Characters = Base64EncodingOptions(rawValue = 1)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val lineLength76Characters = Base64EncodingOptions(rawValue = 2)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val endLineWithCarriageReturn = Base64EncodingOptions(rawValue = 4)

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val endLineWithLineFeed = Base64EncodingOptions(rawValue = 8)

            fun of(vararg options: Data.Base64EncodingOptions): Data.Base64EncodingOptions {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return Base64EncodingOptions(rawValue = value)
            }
        }
    }

    class Base64DecodingOptions: OptionSet<Data.Base64DecodingOptions, Int>, Sendable, MutableStruct {
        override var rawValue: Int
        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): Data.Base64DecodingOptions = Base64DecodingOptions(rawValue = Int(rawvaluelong))
        override fun assignoptionset(target: Data.Base64DecodingOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Data.Base64DecodingOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = Data.Base64DecodingOptions(this as MutableStruct)

        private fun assignfrom(target: Data.Base64DecodingOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
            val ignoreUnknownCharacters = Base64DecodingOptions(rawValue = 1)

            fun of(vararg options: Data.Base64DecodingOptions): Data.Base64DecodingOptions {
                val value = options.fold(Int(0)) { result, option -> result or option.rawValue }
                return Base64DecodingOptions(rawValue = value)
            }
        }
    }

    override fun kotlin(nocopy: Boolean): kotlin.ByteArray = if (nocopy) platformValue else platformValue.copyOf()

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Data
        this.platformValue = copy.platformValue
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Data(this as MutableStruct)

    override fun toString(): String = description

    companion object: DecodableCompanion<Data> {
        override fun init(from: Decoder): Data = Data(from = from)
    }
}

