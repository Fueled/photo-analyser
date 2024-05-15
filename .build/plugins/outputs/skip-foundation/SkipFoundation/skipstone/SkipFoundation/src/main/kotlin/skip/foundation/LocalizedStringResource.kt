// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


class LocalizedStringResource {
    val key: String
    val defaultValue: String? // TODO: String.LocalizationValue
    val table: String?
    var locale: Locale? = null
    var bundle: LocalizedStringResource.BundleDescription? = null
    var comment: String? = null

    constructor(key: String, defaultValue: String? = null, table: String? = null, locale: Locale? = null, bundle: LocalizedStringResource.BundleDescription? = null, comment: String? = null) {
        this.key = key
        this.defaultValue = defaultValue
        this.table = table
        this.locale = locale
        this.bundle = bundle
        this.comment = comment
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LocalizedStringResource) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.key == rhs.key && lhs.defaultValue == rhs.defaultValue && lhs.table == rhs.table && lhs.locale == rhs.locale && lhs.bundle == rhs.bundle && lhs.comment == rhs.comment
    }

    override fun hashCode(): Int {
        var hasher = Hasher()
        hash(into = InOut<Hasher>({ hasher }, { hasher = it }))
        return hasher.finalize()
    }
    fun hash(into: InOut<Hasher>) {
        val hasher = into
        hasher.value.combine(key.hashCode())
        if (defaultValue != null) {
            hasher.value.combine(defaultValue.hashCode())
        }
        if (table != null) {
            hasher.value.combine(table.hashCode())
        }
        locale?.let { locale ->
            hasher.value.combine(locale.hashCode())
        }
        bundle?.let { bundle ->
            hasher.value.combine(bundle.hashCode())
        }
        comment?.let { comment ->
            hasher.value.combine(comment.hashCode())
        }
    }

    sealed class BundleDescription {
        class MainCase: BundleDescription() {
        }
        class ForClassCase(val associated0: AnyClass): BundleDescription() {
            override fun equals(other: Any?): Boolean {
                if (other !is ForClassCase) return false
                return associated0 == other.associated0
            }
            override fun hashCode(): Int {
                var result = 1
                result = Hasher.combine(result, associated0)
                return result
            }
        }
        class AtURLCase(val associated0: URL): BundleDescription() {
            override fun equals(other: Any?): Boolean {
                if (other !is AtURLCase) return false
                return associated0 == other.associated0
            }
            override fun hashCode(): Int {
                var result = 1
                result = Hasher.combine(result, associated0)
                return result
            }
        }

        val description: String
            get() {
                when (this) {
                    is LocalizedStringResource.BundleDescription.MainCase -> return "bundle: main"
                    is LocalizedStringResource.BundleDescription.ForClassCase -> {
                        val c = this.associated0
                        return "bundle: ${c}"
                    }
                    is LocalizedStringResource.BundleDescription.AtURLCase -> {
                        val url = this.associated0
                        return "bundle: ${url}"
                    }
                }
            }

        override fun toString(): String = description

        companion object {
            val main: BundleDescription = MainCase()
            fun forClass(associated0: AnyClass): BundleDescription = ForClassCase(associated0)
            fun atURL(associated0: URL): BundleDescription = AtURLCase(associated0)
        }
    }

    companion object {
    }
}

