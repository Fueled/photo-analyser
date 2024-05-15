// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


open class Formatter {
    open fun string(for_: Any?): String? {
        val obj = for_
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun attributedString(for_: Any, withDefaultAttributes: Dictionary<AnyHashable, Any>? = null): Any? {
        val obj = for_
        val attrs = withDefaultAttributes
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun editingString(for_: Any): String? {
        val obj = for_
        return null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun getObjectValue(obj: Any?, for_: String, errorDescription: Any?): Boolean {
        val string = for_
        val error = errorDescription
        return false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun isPartialStringValid(partialString: String, newEditingString: Any?, errorDescription: Any?): Boolean {
        val newString = newEditingString
        val error = errorDescription
        return false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun isPartialStringValid(partialStringPtr: Any, proposedSelectedRange: Any?, originalString: String, originalSelectedRange: Any, errorDescription: Any?): Boolean {
        val proposedSelRangePtr = proposedSelectedRange
        val origString = originalString
        val origSelRange = originalSelectedRange
        val error = errorDescription
        return false
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open val formattingContext: Any
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun getObjectValue(obj: Any?, for_: String, range: Any?, unusedp: Nothing? = null) = Unit

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

