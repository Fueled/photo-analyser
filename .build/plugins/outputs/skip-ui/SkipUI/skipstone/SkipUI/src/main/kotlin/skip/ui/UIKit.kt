// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.ui.text.input.KeyboardType

enum class UIKeyboardType(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
    default(0),
    asciiCapable(1),
    numbersAndPunctuation(2),
    URL(3),
    numberPad(4),
    phonePad(5),
    namePhonePad(6),
    emailAddress(7),
    decimalPad(8),
    twitter(9),
    webSearch(10),
    asciiCapableNumberPad(11),
    alphabet(12);

    internal fun asComposeKeyboardType(): KeyboardType {
        when (this) {
            UIKeyboardType.default -> return KeyboardType.Text.sref()
            UIKeyboardType.asciiCapable -> return KeyboardType.Ascii.sref()
            UIKeyboardType.numbersAndPunctuation -> return KeyboardType.Text.sref()
            UIKeyboardType.URL -> return KeyboardType.Uri.sref()
            UIKeyboardType.numberPad -> return KeyboardType.Number.sref()
            UIKeyboardType.phonePad -> return KeyboardType.Phone.sref()
            UIKeyboardType.namePhonePad -> return KeyboardType.Text.sref()
            UIKeyboardType.emailAddress -> return KeyboardType.Email.sref()
            UIKeyboardType.decimalPad -> return KeyboardType.Decimal.sref()
            UIKeyboardType.twitter -> return KeyboardType.Text.sref()
            UIKeyboardType.webSearch -> return KeyboardType.Text.sref()
            UIKeyboardType.asciiCapableNumberPad -> return KeyboardType.Text.sref()
            UIKeyboardType.alphabet -> return KeyboardType.Text.sref()
        }
    }

    companion object {
    }
}

fun UIKeyboardType(rawValue: Int): UIKeyboardType? {
    return when (rawValue) {
        0 -> UIKeyboardType.default
        1 -> UIKeyboardType.asciiCapable
        2 -> UIKeyboardType.numbersAndPunctuation
        3 -> UIKeyboardType.URL
        4 -> UIKeyboardType.numberPad
        5 -> UIKeyboardType.phonePad
        6 -> UIKeyboardType.namePhonePad
        7 -> UIKeyboardType.emailAddress
        8 -> UIKeyboardType.decimalPad
        9 -> UIKeyboardType.twitter
        10 -> UIKeyboardType.webSearch
        11 -> UIKeyboardType.asciiCapableNumberPad
        12 -> UIKeyboardType.alphabet
        else -> null
    }
}

class UITextContentType: RawRepresentable<Int> {
    override val rawValue: Int

    constructor(rawValue: Int) {
        this.rawValue = rawValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is UITextContentType) return false
        return rawValue == other.rawValue
    }

    companion object {

        val name = UITextContentType(rawValue = 0) // Not allowed as a Kotlin enum case name
        val namePrefix = UITextContentType(rawValue = 1)
        val givenName = UITextContentType(rawValue = 2)
        val middleName = UITextContentType(rawValue = 3)
        val familyName = UITextContentType(rawValue = 4)
        val nameSuffix = UITextContentType(rawValue = 5)
        val nickname = UITextContentType(rawValue = 6)
        val jobTitle = UITextContentType(rawValue = 7)
        val organizationName = UITextContentType(rawValue = 8)
        val location = UITextContentType(rawValue = 9)
        val fullStreetAddress = UITextContentType(rawValue = 10)
        val streetAddressLine1 = UITextContentType(rawValue = 11)
        val streetAddressLine2 = UITextContentType(rawValue = 12)
        val addressCity = UITextContentType(rawValue = 13)
        val addressState = UITextContentType(rawValue = 14)
        val addressCityAndState = UITextContentType(rawValue = 15)
        val sublocality = UITextContentType(rawValue = 16)
        val countryName = UITextContentType(rawValue = 17)
        val postalCode = UITextContentType(rawValue = 18)
        val telephoneNumber = UITextContentType(rawValue = 19)
        val emailAddress = UITextContentType(rawValue = 20)
        val URL = UITextContentType(rawValue = 21)
        val creditCardNumber = UITextContentType(rawValue = 22)
        val username = UITextContentType(rawValue = 23)
        val password = UITextContentType(rawValue = 24)
        val newPassword = UITextContentType(rawValue = 25)
        val oneTimeCode = UITextContentType(rawValue = 26)
        val shipmentTrackingNumber = UITextContentType(rawValue = 27)
        val flightNumber = UITextContentType(rawValue = 28)
        val dateTime = UITextContentType(rawValue = 29)
        val birthdate = UITextContentType(rawValue = 30)
        val birthdateDay = UITextContentType(rawValue = 31)
        val birthdateMonth = UITextContentType(rawValue = 32)
        val birthdateYear = UITextContentType(rawValue = 33)
        val creditCardSecurityCode = UITextContentType(rawValue = 34)
        val creditCardName = UITextContentType(rawValue = 35)
        val creditCardGivenName = UITextContentType(rawValue = 36)
        val creditCardMiddleName = UITextContentType(rawValue = 37)
        val creditCardFamilyName = UITextContentType(rawValue = 38)
        val creditCardExpiration = UITextContentType(rawValue = 39)
        val creditCardExpirationMonth = UITextContentType(rawValue = 40)
        val creditCardExpirationYear = UITextContentType(rawValue = 41)
        val creditCardType = UITextContentType(rawValue = 42)
    }
}

