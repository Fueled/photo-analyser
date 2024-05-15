// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


open class PropertyListSerialization {

    enum class PropertyListFormat {
        openStep,
        xml,
        binary;

        companion object {
        }
    }

    class ReadOptions: RawRepresentable<UInt>, OptionSet<PropertyListSerialization.ReadOptions, UInt>, MutableStruct {
        override var rawValue: UInt

        constructor(rawValue: UInt) {
            this.rawValue = rawValue
        }

        override val rawvaluelong: ULong
            get() = ULong(rawValue)
        override fun makeoptionset(rawvaluelong: ULong): PropertyListSerialization.ReadOptions = ReadOptions(rawValue = UInt(rawvaluelong))
        override fun assignoptionset(target: PropertyListSerialization.ReadOptions) {
            willmutate()
            try {
                assignfrom(target)
            } finally {
                didmutate()
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as PropertyListSerialization.ReadOptions
            this.rawValue = copy.rawValue
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = PropertyListSerialization.ReadOptions(this as MutableStruct)

        private fun assignfrom(target: PropertyListSerialization.ReadOptions) {
            this.rawValue = target.rawValue
        }

        companion object {

            val mutableContainers = ReadOptions(rawValue = 1U)
            val mutableContainersAndLeaves = ReadOptions(rawValue = 2U)

            fun of(vararg options: PropertyListSerialization.ReadOptions): PropertyListSerialization.ReadOptions {
                val value = options.fold(UInt(0)) { result, option -> result or option.rawValue }
                return ReadOptions(rawValue = value)
            }
        }
    }

    companion object: CompanionClass() {
        override fun propertyList(from: Data, options: PropertyListSerialization.ReadOptions, format: Any?): Dictionary<String, String>? {
            var dict: Dictionary<String, String> = dictionaryOf()
            //let re = #"(?<!\\)"(.*?)(?<!\\)"\s*=\s*"(.*?)(?<!\\)";"# // Swift Regex error: "lookbehind is not currently supported"
            //let re = "^\"(.*)\"[ ]*=[ ]*\"(.*)\";\\s*$"
            val re = "^\"(.*)\"[ ]*=[ ]*\"(.*)\";\$" // needs https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex-option/-m-u-l-t-i-l-i-n-e.html

            val text = from.utf8String
            if (text == null) {
                // should this throw an error?
                return null
            }

            fun unescape(string: String): String {
                return string
                    .replacingOccurrences(of = "\\\"", with = "\"")
                    .replacingOccurrences(of = "\\n", with = "\n")
            }

            for (line in text.components(separatedBy = "\n")) {
                val exp = kotlin.text.Regex(re, RegexOption.MULTILINE) // https://www.baeldung.com/regular-expressions-java#Pattern
                for (match in exp.findAll(text).map({ it.groupValues })) {
                    if (match.size == 3) {
                        match[1].sref()?.let { key ->
                            match[2].sref()?.let { value ->
                                dict[unescape(key)] = unescape(value)
                            }
                        }
                    }
                }
            }
            return dict.sref()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun data(fromPropertyList: Any, format: PropertyListSerialization.PropertyListFormat, options: Int): Data {
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun writePropertyList(propertyList: Any, to: Any, format: PropertyListSerialization.PropertyListFormat, options: Int, error: Any): Int {
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun propertyList(with: Any, options: PropertyListSerialization.ReadOptions = PropertyListSerialization.ReadOptions.of(), format: Any?): Any {
            fatalError()
        }

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        fun propertyList(propertyList: Any, isValidFor: PropertyListSerialization.PropertyListFormat): Boolean {
            fatalError()
        }
    }
    open class CompanionClass {
        open fun propertyList(from: Data, options: PropertyListSerialization.ReadOptions = PropertyListSerialization.ReadOptions.of(), format: Any?): Dictionary<String, String>? = PropertyListSerialization.propertyList(from = from, options = options, format = format)
    }
}

