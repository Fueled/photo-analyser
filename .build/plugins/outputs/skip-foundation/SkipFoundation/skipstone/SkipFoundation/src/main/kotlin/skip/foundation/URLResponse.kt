// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


open class URLResponse {
    open var url: URL? = null
        get() = field.sref({ this.url = it })
        internal set(newValue) {
            field = newValue.sref()
        }
    open var mimeType: String? = null
        internal set
    open var expectedContentLength: Long = -1
        internal set
    open var textEncodingName: String? = null
        internal set

    constructor() {
    }

    constructor(url: URL, mimeType: String?, expectedContentLength: Int, textEncodingName: String?) {
        this.url = url
        this.mimeType = mimeType
        this.expectedContentLength = Long(expectedContentLength)
        this.textEncodingName = textEncodingName
    }

    open val suggestedFilename: String?
        get() {
            // A filename specified using the content disposition header.
            // The last path component of the URL.
            // The host of the URL.
            // If the host of URL can't be converted to a valid filename, the filename “unknown” is used.
            this.url?.lastPathComponent?.let { component ->
                if (!component.isEmpty) {
                    return component
                }
            }
            // not expected by the test cases
            //if let host = self.url?.host {
            //    return host
            //}
            return "Unknown"
        }

    open fun copy(): Any {
        val matchtarget_0 = this.url
        if (matchtarget_0 != null) {
            val url = matchtarget_0
            return URLResponse(url = url, mimeType = this.mimeType, expectedContentLength = Int(this.expectedContentLength), textEncodingName = this.textEncodingName)
        } else {
            return URLResponse()
        }
    }

    open fun isEqual(other: Any?): Boolean {
        val other_0 = other as? URLResponse
        if (other_0 == null) {
            return false
        }
        return this.url == other_0.url && this.mimeType == other_0.mimeType && this.expectedContentLength == other_0.expectedContentLength && this.textEncodingName == other_0.textEncodingName
    }

    override fun equals(other: Any?): Boolean {
        if (other !is URLResponse) {
            return false
        }
        val lhs = this
        val rhs = other
        return lhs.isEqual(rhs)
    }

    override fun hashCode(): Int {
        var hasher = Hasher()
        hash(into = InOut<Hasher>({ hasher }, { hasher = it }))
        return hasher.finalize()
    }
    open fun hash(into: InOut<Hasher>) {
        val hasher = into
        hasher.value.combine(url)
        hasher.value.combine(mimeType)
        hasher.value.combine(expectedContentLength)
        hasher.value.combine(textEncodingName)
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

