// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


open class NumberFormatter: Formatter {
    internal open var platformValue: java.text.DecimalFormat
        get() = field.sref({ this.platformValue = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal constructor(platformValue: java.text.DecimalFormat): super() {
        this.platformValue = platformValue
    }

    constructor(): super() {
        this.platformValue = java.text.DecimalFormat.getIntegerInstance() as java.text.DecimalFormat
        this.groupingSize = 0
    }

    private var _numberStyle: NumberFormatter.Style = NumberFormatter.Style.none

    open val description: String
        get() = platformValue.description

    open var numberStyle: NumberFormatter.Style
        get() = _numberStyle
        set(newValue) {
            var fmt: java.text.DecimalFormat = this.platformValue.sref()
            when (newValue) {
                NumberFormatter.Style.none -> {
                    val matchtarget_0 = _locale?.platformValue
                    if (matchtarget_0 != null) {
                        val loc = matchtarget_0
                        fmt = (java.text.DecimalFormat.getIntegerInstance(loc) as java.text.DecimalFormat).sref()
                    } else {
                        fmt = (java.text.DecimalFormat.getIntegerInstance() as java.text.DecimalFormat).sref()
                    }
                }
                NumberFormatter.Style.decimal -> {
                    val matchtarget_1 = _locale?.platformValue
                    if (matchtarget_1 != null) {
                        val loc = matchtarget_1
                        fmt = (java.text.DecimalFormat.getNumberInstance(loc) as java.text.DecimalFormat).sref()
                    } else {
                        fmt = (java.text.DecimalFormat.getNumberInstance() as java.text.DecimalFormat).sref()
                    }
                }
                NumberFormatter.Style.currency -> {
                    val matchtarget_2 = _locale?.platformValue
                    if (matchtarget_2 != null) {
                        val loc = matchtarget_2
                        fmt = (java.text.DecimalFormat.getCurrencyInstance(loc) as java.text.DecimalFormat).sref()
                    } else {
                        fmt = (java.text.DecimalFormat.getCurrencyInstance() as java.text.DecimalFormat).sref()
                    }
                }
                NumberFormatter.Style.percent -> {
                    val matchtarget_3 = _locale?.platformValue
                    if (matchtarget_3 != null) {
                        val loc = matchtarget_3
                        fmt = (java.text.DecimalFormat.getPercentInstance(loc) as java.text.DecimalFormat).sref()
                    } else {
                        fmt = (java.text.DecimalFormat.getPercentInstance() as java.text.DecimalFormat).sref()
                    }
                }
                else -> {
                    fatalError("SkipNumberFormatter: unsupported style ${newValue}")
                }
            }

            val symbols = this.platformValue.decimalFormatSymbols.sref()
            val matchtarget_4 = _locale?.platformValue
            if (matchtarget_4 != null) {
                val loc = matchtarget_4
                this.platformValue.applyLocalizedPattern(fmt.toLocalizedPattern())
                symbols.currency = java.util.Currency.getInstance(loc)
                //symbols.currencySymbol = symbols.currency.getSymbol(loc) // also needed or else the sumbol is not applied
            } else {
                this.platformValue.applyPattern(fmt.toPattern())
            }
            this.platformValue.decimalFormatSymbols = symbols
        }

    private var _locale: Locale? = Locale.current

    open var locale: Locale?
        get() = _locale
        set(newValue) {
            this._locale = newValue
            newValue?.let { loc ->
                applySymbol { it -> it.currency = java.util.Currency.getInstance(loc.platformValue) }
            }
        }


    open var groupingSize: Int
        get() = platformValue.getGroupingSize()
        set(newValue) {
            platformValue.setGroupingSize(newValue)
        }

    open var generatesDecimalNumbers: Boolean
        get() = platformValue.isParseBigDecimal()
        set(newValue) {
            platformValue.setParseBigDecimal(newValue)
        }

    open var alwaysShowsDecimalSeparator: Boolean
        get() = platformValue.isDecimalSeparatorAlwaysShown()
        set(newValue) {
            platformValue.setDecimalSeparatorAlwaysShown(newValue)
        }

    open var usesGroupingSeparator: Boolean
        get() = platformValue.isGroupingUsed()
        set(newValue) {
            platformValue.setGroupingUsed(newValue)
        }

    open var multiplier: java.lang.Number?
        get() = platformValue.multiplier as java.lang.Number
        set(newValue) {
            newValue?.let { value ->
                platformValue.multiplier = value.intValue
            }
        }

    open var groupingSeparator: String?
        get() = platformValue.decimalFormatSymbols.groupingSeparator.toString()
        set(newValue) {
            newValue?.first?.let { groupingSeparator ->
                applySymbol { it -> it.groupingSeparator = groupingSeparator }
            }
        }

    open var percentSymbol: String?
        get() = platformValue.decimalFormatSymbols.percent.toString()
        set(newValue) {
            newValue?.first?.let { percentSymbol ->
                applySymbol { it -> it.percent = percentSymbol }
            }
        }

    open var currencySymbol: String?
        get() = platformValue.decimalFormatSymbols.currencySymbol
        set(newValue) {
            applySymbol { it -> it.currencySymbol = newValue }
        }

    open var zeroSymbol: String?
        get() {
            return platformValue.decimalFormatSymbols.zeroDigit?.toString()
        }
        set(newValue) {
            newValue?.first?.let { zeroSymbolChar ->
                applySymbol { it -> it.zeroDigit = zeroSymbolChar }
            }
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var plusSign: String? = null // no plusSign in DecimalFormatSymbols

    open var minusSign: String?
        get() {
            return platformValue.decimalFormatSymbols.minusSign?.toString()
        }
        set(newValue) {
            newValue?.first?.let { minusSignChar ->
                applySymbol { it -> it.minusSign = minusSignChar }
            }
        }

    open var exponentSymbol: String?
        get() = platformValue.decimalFormatSymbols.exponentSeparator
        set(newValue) {
            applySymbol { it -> it.exponentSeparator = newValue }
        }

    open var negativeInfinitySymbol: String
        get() {
            // Note: java.text.DecimalFormatSymbols has only a single `infinity` compares to `positiveInfinitySymbol` and `negativeInfinitySymbol`
            return platformValue.decimalFormatSymbols.infinity
        }
        set(newValue) {
            applySymbol { it -> it.infinity = newValue }
        }

    open var positiveInfinitySymbol: String
        get() {
            // Note: java.text.DecimalFormatSymbols has only a single `infinity` compares to `positiveInfinitySymbol` and `negativeInfinitySymbol`
            return platformValue.decimalFormatSymbols.infinity
        }
        set(newValue) {
            applySymbol { it -> it.infinity = newValue }
        }

    open var internationalCurrencySymbol: String?
        get() = platformValue.decimalFormatSymbols.internationalCurrencySymbol
        set(newValue) {
            applySymbol { it -> it.internationalCurrencySymbol = newValue }
        }


    open var decimalSeparator: String?
        get() {
            return platformValue.decimalFormatSymbols.decimalSeparator?.toString()
        }
        set(newValue) {
            newValue?.first?.let { decimalSeparatorChar ->
                applySymbol { it -> it.decimalSeparator = decimalSeparatorChar }
            }
        }

    open var currencyCode: String?
        get() = platformValue.decimalFormatSymbols.internationalCurrencySymbol
        set(newValue) {
            applySymbol { it -> it.internationalCurrencySymbol = newValue }
        }

    open var currencyDecimalSeparator: String?
        get() {
            return platformValue.decimalFormatSymbols.monetaryDecimalSeparator?.toString()
        }
        set(newValue) {
            newValue?.first?.let { currencyDecimalSeparatorChar ->
                applySymbol { it -> it.monetaryDecimalSeparator = currencyDecimalSeparatorChar }
            }
        }

    open var notANumberSymbol: String?
        get() = platformValue.decimalFormatSymbols.getNaN()
        set(newValue) {
            applySymbol { it -> it.setNaN(newValue) }
        }

    open var positiveSuffix: String?
        get() = platformValue.positiveSuffix
        set(newValue) {
            platformValue.positiveSuffix = newValue
        }

    open var negativeSuffix: String?
        get() = platformValue.negativeSuffix
        set(newValue) {
            platformValue.negativeSuffix = newValue
        }

    open var positivePrefix: String?
        get() = platformValue.positivePrefix
        set(newValue) {
            platformValue.positivePrefix = newValue
        }

    open var negativePrefix: String?
        get() = platformValue.negativePrefix
        set(newValue) {
            platformValue.negativePrefix = newValue
        }

    open var maximumFractionDigits: Int
        get() = platformValue.maximumFractionDigits
        set(newValue) {
            platformValue.maximumFractionDigits = newValue
        }

    open var minimumFractionDigits: Int
        get() = platformValue.minimumFractionDigits
        set(newValue) {
            platformValue.minimumFractionDigits = newValue
        }

    open var maximumIntegerDigits: Int
        get() = platformValue.maximumIntegerDigits
        set(newValue) {
            platformValue.maximumIntegerDigits = newValue
        }

    open var minimumIntegerDigits: Int
        get() = platformValue.minimumIntegerDigits
        set(newValue) {
            platformValue.minimumIntegerDigits = newValue
        }

    open fun string(from: java.lang.Number): String? {
        val number = from
        return platformValue.format(number)
    }

    open fun string(from: Int): String? {
        val number = from
        return string(from = number as java.lang.Number)
    }
    open fun string(from: Double): String? {
        val number = from
        return string(from = number as java.lang.Number)
    }

    /// Sets the DecimalFormatSymbols with the given block; needed since `getDecimalFormatSymbols` returns a copy, so it must be re-set manually.
    private fun applySymbol(block: (java.text.DecimalFormatSymbols) -> Unit) {
        val dfs = platformValue.getDecimalFormatSymbols()
        block(dfs)
        platformValue.setDecimalFormatSymbols(dfs)
    }

    override fun string(for_: Any?): String? {
        val object_ = for_
        val matchtarget_5 = object_ as? java.lang.Number
        if (matchtarget_5 != null) {
            val number = matchtarget_5
            return string(from = number)
        } else {
            val matchtarget_6 = object_ as? Boolean
            if (matchtarget_6 != null) {
                val bool = matchtarget_6
                // this is the expected NSNumber behavior checked in test_stringFor
                return string(from = if (bool == true) 1 else 0)
            } else {
                return null
            }
        }
    }

    open fun number(from: String): java.lang.Number? {
        val string = from
        return platformValue.parse(string) as? java.lang.Number
    }

    enum class Style(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<Int> {
        none(0),
        decimal(1),
        currency(2),
        percent(3),
        scientific(4),
        spellOut(5),
        // case ordinal = 6 // FIXME: Kotlin error: 47:9 Conflicting declarations: public final val ordinal: Int, enum entry ordinal
        currencyISOCode(8),
        currencyPlural(9),
        currencyAccounting(10);

        companion object {
        }
    }

    enum class PadPosition(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<Int> {
        beforePrefix(0),
        afterPrefix(1),
        beforeSuffix(2),
        afterSuffix(3);

        companion object {
        }
    }

    enum class RoundingMode(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): Sendable, RawRepresentable<Int> {
        ceiling(0),
        floor(1),
        down(2),
        up(3),
        halfEven(4),
        halfDown(5),
        halfUp(6);

        companion object {
        }
    }

    companion object: CompanionClass() {

        override fun Style(rawValue: Int): NumberFormatter.Style? {
            return when (rawValue) {
                0 -> Style.none
                1 -> Style.decimal
                2 -> Style.currency
                3 -> Style.percent
                4 -> Style.scientific
                5 -> Style.spellOut
                8 -> Style.currencyISOCode
                9 -> Style.currencyPlural
                10 -> Style.currencyAccounting
                else -> null
            }
        }

        override fun PadPosition(rawValue: Int): NumberFormatter.PadPosition? {
            return when (rawValue) {
                0 -> PadPosition.beforePrefix
                1 -> PadPosition.afterPrefix
                2 -> PadPosition.beforeSuffix
                3 -> PadPosition.afterSuffix
                else -> null
            }
        }

        override fun RoundingMode(rawValue: Int): NumberFormatter.RoundingMode? {
            return when (rawValue) {
                0 -> RoundingMode.ceiling
                1 -> RoundingMode.floor
                2 -> RoundingMode.down
                3 -> RoundingMode.up
                4 -> RoundingMode.halfEven
                5 -> RoundingMode.halfDown
                6 -> RoundingMode.halfUp
                else -> null
            }
        }
    }
    open class CompanionClass: Formatter.CompanionClass() {
        open fun Style(rawValue: Int): NumberFormatter.Style? = NumberFormatter.Style(rawValue = rawValue)
        open fun PadPosition(rawValue: Int): NumberFormatter.PadPosition? = NumberFormatter.PadPosition(rawValue = rawValue)
        open fun RoundingMode(rawValue: Int): NumberFormatter.RoundingMode? = NumberFormatter.RoundingMode(rawValue = rawValue)
    }
}

