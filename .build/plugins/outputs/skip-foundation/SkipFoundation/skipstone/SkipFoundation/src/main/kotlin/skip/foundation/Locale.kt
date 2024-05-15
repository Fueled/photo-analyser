// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


internal typealias NSLocale = Locale

class Locale: KotlinConverting<java.util.Locale> {
    internal val platformValue: java.util.Locale

    constructor(platformValue: java.util.Locale) {
        this.platformValue = platformValue.sref()
    }

    constructor(identifier: String) {
        // Returns a locale for the specified IETF BCP 47 language tag string.
        this.platformValue = java.util.Locale.forLanguageTag(identifier.replace("_", "-"))
    }

    /// Construct an identifier that conforms to the expected Foundation identifiers
    val identifier: String
        get() {
            // Returns a string representation of this Locale object, consisting of language, country, variant, script, and extensions as below: language + "_" + country + "_" + (variant + "_#" | "#") + script + "-" + extensions Language is always lower case, country is always upper case, script is always title case, and extensions are always lower case.
            //return platformValue.toString()

            // To represent a Locale as a String for interchange purposes, use toLanguageTag().
            return platformValue.toLanguageTag().replace("-", "_")
        }

    override fun equals(other: Any?): Boolean {
        if (other !is Locale) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.platformValue == rhs.platformValue
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

    /// Returns an array of tags to search for a locale identifier, from most specific to least specific
    internal val localeSearchTags: Array<String>
        get() {
            // for an identifier like "fr_FR", seek "fr-FR.lproj" and "fr.lproj"
            // for an identifier like "zh_Hant", seek "zh-Hant.lproj" and "zh.lproj"
            // for an identifier like "fr_CA_QC", seek "fr-QC-CA.lproj" and "fr-CA.lproj" and "fr.lproj"
            var identifiers = arrayOf(this.canonicalIdentifier)
            val languageCode = this.languageCode ?: ""
            val matchtarget_0 = this.regionCode
            if (matchtarget_0 != null) {
                val regionCode = matchtarget_0
                if (!regionCode.isEmpty) {
                    val matchtarget_1 = this.variantCode
                    if (matchtarget_1 != null) {
                        val variantCode = matchtarget_1
                        if (!variantCode.isEmpty) {
                            identifiers.append(languageCode + "-" + variantCode + "-" + regionCode)
                            identifiers.append(languageCode + "-" + variantCode)
                            identifiers.append(languageCode + "-" + regionCode)
                        } else {
                            identifiers.append(languageCode + "-" + regionCode)
                        }
                    } else {
                        identifiers.append(languageCode + "-" + regionCode)
                    }
                } else {
                    this.variantCode?.let { variantCode ->
                        if (!variantCode.isEmpty) {
                            identifiers.append(languageCode + "-" + variantCode)
                        }
                    }
                }
            } else {
                this.variantCode?.let { variantCode ->
                    if (!variantCode.isEmpty) {
                        identifiers.append(languageCode + "-" + variantCode)
                    }
                }
            }
            // special case: un-specified "zh" should fall back to zh-Hans
            if (languageCode == "zh") {
                identifiers.append("zh-Hans")
            }
            identifiers.append(languageCode)

            return identifiers
        }

    /// The identifier that matches the default ID used for xcstrings dictionary keys
    val canonicalIdentifier: String
        get() {
            return platformValue.toLanguageTag()

            val languageCode = languageCode ?: "en"
            val matchtarget_2 = regionCode
            if (matchtarget_2 != null) {
                val regionCode = matchtarget_2
                if (!regionCode.isEmpty) {
                    val matchtarget_3 = variantCode
                    if (matchtarget_3 != null) {
                        val variantCode = matchtarget_3
                        if (!variantCode.isEmpty) {
                            return languageCode + "-" + variantCode + "-" + regionCode
                        } else {
                            return languageCode + "-" + regionCode
                        }
                    } else {
                        return languageCode + "-" + regionCode
                    }
                } else {
                    val matchtarget_4 = variantCode
                    if (matchtarget_4 != null) {
                        val variantCode = matchtarget_4
                        if (!variantCode.isEmpty) {
                            return languageCode + "-" + variantCode
                        } else {
                            return languageCode
                        }
                    } else {
                        return languageCode
                    }
                }
            } else {
                val matchtarget_4 = variantCode
                if (matchtarget_4 != null) {
                    val variantCode = matchtarget_4
                    if (!variantCode.isEmpty) {
                        return languageCode + "-" + variantCode
                    } else {
                        return languageCode
                    }
                } else {
                    return languageCode
                }
            }
        }

    val languageCode: String?
        get() = platformValue.getLanguage()

    val variantCode: String?
        get() = platformValue.getVariant()

    val regionCode: String?
        get() = platformValue.getCountry()

    val scriptCode: String?
        get() = platformValue.getScript()

    fun localizedString(forIdentifier: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null): String? {
        val targetIdentifier = forIdentifier
        return Locale(identifier = targetIdentifier).platformValue.getDisplayName(platformValue)
    }

    fun localizedString(forLanguageCode: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null, @Suppress("UNUSED_PARAMETER") unusedp_1: Nothing? = null): String? {
        // malformed languages like "en-AU" throw an exception in Java, but no in Cocoa; so we ignore exceptions and fallback to attempting to create the Locale directly
        val locale = try { java.util.Locale.Builder().setLanguage(forLanguageCode).build() } catch (_: Throwable) { null }
        return (locale ?: Locale(identifier = forLanguageCode).platformValue).getDisplayLanguage(platformValue)
    }

    fun localizedString(forRegionCode: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null, @Suppress("UNUSED_PARAMETER") unusedp_1: Nothing? = null, @Suppress("UNUSED_PARAMETER") unusedp_2: Nothing? = null): String? {
        val locale = try { java.util.Locale.Builder().setRegion(forRegionCode).build() } catch (_: Throwable) { null }
        return (locale ?: Locale(identifier = forRegionCode).platformValue).getDisplayCountry(platformValue)
    }

    fun localizedString(forScriptCode: String): String? {
        val locale = try { java.util.Locale.Builder().setScript(forScriptCode).build() } catch (_: Throwable) { null }
        return (locale ?: Locale(identifier = forScriptCode).platformValue).getDisplayScript(platformValue)
    }

    val currencySymbol: String?
        get() {
            return java.text.NumberFormat.getCurrencyInstance(platformValue).currency?.symbol
        }

    fun localize(key: String, value: String?, bundle: Bundle?, tableName: String?): String? {
        return bundle?.localizedBundle(locale = this)?.localizedString(forKey = key, value = value, table = tableName)
    }

    override fun kotlin(nocopy: Boolean): java.util.Locale = (if (nocopy) platformValue else platformValue.clone() as java.util.Locale).sref()

    companion object {

        val availableIdentifiers: Array<String>
            get() {
                return Array(java.util.Locale.getAvailableLocales().map({ it -> it.toString() }))
            }

        val current: Locale
            get() = Locale(platformValue = java.util.Locale.getDefault())

        val system: Locale
            get() = Locale(platformValue = java.util.Locale.getDefault()) // FIXME: not the same as .system: “Use the system locale when you don’t want any localizations”
    }
}

