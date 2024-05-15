// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array

// This code is adapted from https://github.com/apple/swift-corelibs-foundation/blob/main/Tests/Foundation/Tests which has the following license:

//===----------------------------------------------------------------------===//
//
// This source file is part of the Swift.org open source project
//
// Copyright (c) 2014 - 2021 Apple Inc. and the Swift project authors
// Licensed under Apache License v2.0 with Runtime Library Exception
//
// See https://swift.org/LICENSE.txt for license information
// See https://swift.org/CONTRIBUTORS.txt for the list of Swift project authors
//
//===----------------------------------------------------------------------===//


typealias NSErrorDomain = String

val NSCocoaErrorDomain: String = "NSCocoaErrorDomain"
val NSPOSIXErrorDomain: String = "NSPOSIXErrorDomain"
val NSOSStatusErrorDomain: String = "NSOSStatusErrorDomain"
val NSMachErrorDomain: String = "NSMachErrorDomain"

val NSUnderlyingErrorKey: String = "NSUnderlyingError"
val NSLocalizedDescriptionKey: String = "NSLocalizedDescription"
val NSLocalizedFailureReasonErrorKey: String = "NSLocalizedFailureReason"
val NSLocalizedRecoverySuggestionErrorKey: String = "NSLocalizedRecoverySuggestion"
val NSLocalizedRecoveryOptionsErrorKey: String = "NSLocalizedRecoveryOptions"
val NSRecoveryAttempterErrorKey: String = "NSRecoveryAttempter"
val NSHelpAnchorErrorKey: String = "NSHelpAnchor"
val NSDebugDescriptionErrorKey = "NSDebugDescription"
val NSStringEncodingErrorKey: String = "NSStringEncodingErrorKey"
val NSURLErrorKey: String = "NSURL"
val NSFilePathErrorKey: String = "NSFilePathErrorKey"

open class NSError: Exception, Error {
    // ErrorType forbids this being internal
    open var _domain: String
    open var _code: Int

    private var _userInfo: Dictionary<String, Any>? = null
        get() = field.sref({ this._userInfo = it })
        set(newValue) {
            field = newValue.sref()
        }

    constructor(domain: String, code: Int, userInfo: Dictionary<String, Any>? = null): super() {
        val dict = userInfo
        _domain = domain
        _code = code
        _userInfo = dict
    }

    open val domain: String
        get() = _domain

    open val code: Int
        get() = _code

    open val userInfo: Dictionary<String, Any>
        get() {
            val matchtarget_0 = _userInfo
            if (matchtarget_0 != null) {
                val info = matchtarget_0
                return info
            } else {
                return Dictionary<String, Any>()
            }
        }

    override val localizedDescription: String
        get() {
            val matchtarget_1 = userInfo[NSLocalizedDescriptionKey] as? String
            if (matchtarget_1 != null) {
                val localizedDescription = matchtarget_1
                return localizedDescription
            } else {
                // placeholder values
                return "The operation could not be completed." + " " + (this.localizedFailureReason ?: "(${domain} error ${code}.)")
            }
        }

    open val localizedFailureReason: String?
        get() {
            val matchtarget_2 = userInfo[NSLocalizedFailureReasonErrorKey] as? String
            if (matchtarget_2 != null) {
                val localizedFailureReason = matchtarget_2
                return localizedFailureReason
            } else {
                when (domain) {
                    NSPOSIXErrorDomain -> {
                        //return String(cString: strerror(Int32(code)), encoding: .ascii)
                        return "POSIX error ${code}"
                    }
                    NSCocoaErrorDomain -> return CocoaError.errorMessages[CocoaError.Code(rawValue = code)]
                    NSURLErrorDomain -> return null
                    else -> return null
                }
            }
        }

    open val localizedRecoverySuggestion: String?
        get() = userInfo[NSLocalizedRecoverySuggestionErrorKey] as? String

    open val localizedRecoveryOptions: Array<String>?
        get() = userInfo[NSLocalizedRecoveryOptionsErrorKey] as? Array<String>

    open val recoveryAttempter: Any?
        get() = userInfo[NSRecoveryAttempterErrorKey]

    open val helpAnchor: String?
        get() = userInfo[NSHelpAnchorErrorKey] as? String

    open val description: String
        get() = "Error Domain=${domain} Code=${code} \"${localizedFailureReason ?: "(null)"}\""

    override fun toString(): String = description

    companion object: CompanionClass() {
        override var userInfoProviders = Dictionary<String, (Error, String) -> Any?>()
            get() = field.sref({ this.userInfoProviders = it })
            set(newValue) {
                field = newValue.sref()
            }

        override fun setUserInfoValueProvider(forDomain: String, provider: ((Error, String) -> Any?)?) {
            val errorDomain = forDomain
            NSError.userInfoProviders[errorDomain] = provider
        }

        override fun userInfoValueProvider(forDomain: String): ((Error, String) -> Any?)? {
            val errorDomain = forDomain
            return NSError.userInfoProviders[errorDomain]
        }
    }
    open class CompanionClass {
        internal open var userInfoProviders
            get() = NSError.userInfoProviders
            set(newValue) {
                NSError.userInfoProviders = newValue
            }
        open fun setUserInfoValueProvider(forDomain: String, provider: ((Error, String) -> Any?)?) = NSError.setUserInfoValueProvider(forDomain = forDomain, provider = provider)
        open fun userInfoValueProvider(forDomain: String): ((Error, String) -> Any?)? = NSError.userInfoValueProvider(forDomain = forDomain)
    }
}

interface LocalizedError: Error {
    val errorDescription: String?
        get() = null
    val failureReason: String?
        get() = null
    val recoverySuggestion: String?
        get() = null
    val helpAnchor: String?
        get() = null
}

internal open class _NSErrorRecoveryAttempter {
    internal open fun attemptRecovery(fromError: Error, optionIndex: Int): Boolean {
        val error = fromError
        val recoveryOptionIndex = optionIndex
        val recoverableError = (error as RecoverableError).sref()
        return recoverableError.attemptRecovery(optionIndex = recoveryOptionIndex)
    }
}

interface RecoverableError: Error {
    val recoveryOptions: Array<String>
    fun attemptRecovery(optionIndex: Int, resultHandler: (Boolean) -> Unit) {
        val recoveryOptionIndex = optionIndex
        val handler = resultHandler
        handler(attemptRecovery(optionIndex = recoveryOptionIndex))
    }
    fun attemptRecovery(optionIndex: Int): Boolean
}

interface CustomNSError: Error {
    val errorCode: Int
        get() = 0 // no equivalent for Swift._getDefaultErrorCode()

    val errorUserInfo: Dictionary<String, Any>
        get() = dictionaryOf()
}

class CocoaError: Exception, Error {
    val _nsError: NSError

    constructor(_nsError: NSError): super() {
        val error = _nsError
        precondition(error.domain == NSCocoaErrorDomain)
        this._nsError = error
    }

    val code: CocoaError.Code
        get() = Code(rawValue = _nsError.code)

    class Code: RawRepresentable<Int> {
        override val rawValue: Int

        constructor(rawValue: Int) {
            this.rawValue = rawValue
        }

        override fun equals(other: Any?): Boolean {
            if (other !is CocoaError.Code) return false
            return rawValue == other.rawValue
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, rawValue)
            return result
        }

        companion object {

            val fileNoSuchFile: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4)
            val fileLocking: CocoaError.Code
                get() = CocoaError.Code(rawValue = 255)
            val fileReadUnknown: CocoaError.Code
                get() = CocoaError.Code(rawValue = 256)
            val fileReadNoPermission: CocoaError.Code
                get() = CocoaError.Code(rawValue = 257)
            val fileReadInvalidFileName: CocoaError.Code
                get() = CocoaError.Code(rawValue = 258)
            val fileReadCorruptFile: CocoaError.Code
                get() = CocoaError.Code(rawValue = 259)
            val fileReadNoSuchFile: CocoaError.Code
                get() = CocoaError.Code(rawValue = 260)
            val fileReadInapplicableStringEncoding: CocoaError.Code
                get() = CocoaError.Code(rawValue = 261)
            val fileReadUnsupportedScheme: CocoaError.Code
                get() = CocoaError.Code(rawValue = 262)
            val fileReadTooLarge: CocoaError.Code
                get() = CocoaError.Code(rawValue = 263)
            val fileReadUnknownStringEncoding: CocoaError.Code
                get() = CocoaError.Code(rawValue = 264)
            val fileWriteUnknown: CocoaError.Code
                get() = CocoaError.Code(rawValue = 512)
            val fileWriteNoPermission: CocoaError.Code
                get() = CocoaError.Code(rawValue = 513)
            val fileWriteInvalidFileName: CocoaError.Code
                get() = CocoaError.Code(rawValue = 514)
            val fileWriteFileExists: CocoaError.Code
                get() = CocoaError.Code(rawValue = 516)
            val fileWriteInapplicableStringEncoding: CocoaError.Code
                get() = CocoaError.Code(rawValue = 517)
            val fileWriteUnsupportedScheme: CocoaError.Code
                get() = CocoaError.Code(rawValue = 518)
            val fileWriteOutOfSpace: CocoaError.Code
                get() = CocoaError.Code(rawValue = 640)
            val fileWriteVolumeReadOnly: CocoaError.Code
                get() = CocoaError.Code(rawValue = 642)
            val fileManagerUnmountUnknown: CocoaError.Code
                get() = CocoaError.Code(rawValue = 768)
            val fileManagerUnmountBusy: CocoaError.Code
                get() = CocoaError.Code(rawValue = 769)
            val keyValueValidation: CocoaError.Code
                get() = CocoaError.Code(rawValue = 1024)
            val formatting: CocoaError.Code
                get() = CocoaError.Code(rawValue = 2048)
            val userCancelled: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3072)
            val featureUnsupported: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3328)
            val executableNotLoadable: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3584)
            val executableArchitectureMismatch: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3585)
            val executableRuntimeMismatch: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3586)
            val executableLoad: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3587)
            val executableLink: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3588)
            val propertyListReadCorrupt: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3840)
            val propertyListReadUnknownVersion: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3841)
            val propertyListReadStream: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3842)
            val propertyListWriteStream: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3851)
            val propertyListWriteInvalid: CocoaError.Code
                get() = CocoaError.Code(rawValue = 3852)
            val xpcConnectionInterrupted: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4097)
            val xpcConnectionInvalid: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4099)
            val xpcConnectionReplyInvalid: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4101)
            val ubiquitousFileUnavailable: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4353)
            val ubiquitousFileNotUploadedDueToQuota: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4354)
            val ubiquitousFileUbiquityServerNotAvailable: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4355)
            val userActivityHandoffFailed: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4608)
            val userActivityConnectionUnavailable: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4609)
            val userActivityRemoteApplicationTimedOut: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4610)
            val userActivityHandoffUserInfoTooLarge: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4611)
            val coderReadCorrupt: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4864)
            val coderValueNotFound: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4865)
            val coderInvalidValue: CocoaError.Code
                get() = CocoaError.Code(rawValue = 4866)
        }
    }

    private val _nsUserInfo: Dictionary<String, Any>
        get() = _nsError.userInfo

    val filePath: String?
        get() = _nsUserInfo[NSFilePathErrorKey] as? String

    val underlying: Error?
        get() = _nsUserInfo[NSUnderlyingErrorKey] as? Error

    val url: URL?
        get() = _nsUserInfo[NSURLErrorKey] as? URL

    val isCoderError: Boolean
        get() = code.rawValue >= 4864 && code.rawValue <= 4991

    val isExecutableError: Boolean
        get() = code.rawValue >= 3584 && code.rawValue <= 3839

    val isFileError: Boolean
        get() = code.rawValue >= 0 && code.rawValue <= 1023

    val isFormattingError: Boolean
        get() = code.rawValue >= 2048 && code.rawValue <= 2559

    val isPropertyListError: Boolean
        get() = code.rawValue >= 3840 && code.rawValue <= 4095

    val isUbiquitousFileError: Boolean
        get() = code.rawValue >= 4352 && code.rawValue <= 4607

    val isUserActivityError: Boolean
        get() = code.rawValue >= 4608 && code.rawValue <= 4863

    val isValidationError: Boolean
        get() = code.rawValue >= 1024 && code.rawValue <= 2047

    val isXPCConnectionError: Boolean
        get() = code.rawValue >= 4096 && code.rawValue <= 4224

    companion object {

        val _nsErrorDomain: String
            get() = NSCocoaErrorDomain

        internal val errorMessages = dictionaryOf(
            Tuple2(Code(rawValue = 4), "The file doesn’t exist."),
            Tuple2(Code(rawValue = 255), "The file couldn’t be locked."),
            Tuple2(Code(rawValue = 257), "You don’t have permission."),
            Tuple2(Code(rawValue = 258), "The file name is invalid."),
            Tuple2(Code(rawValue = 259), "The file isn’t in the correct format."),
            Tuple2(Code(rawValue = 260), "The file doesn’t exist."),
            Tuple2(Code(rawValue = 261), "The specified text encoding isn’t applicable."),
            Tuple2(Code(rawValue = 262), "The specified URL type isn’t supported."),
            Tuple2(Code(rawValue = 263), "The item is too large."),
            Tuple2(Code(rawValue = 264), "The text encoding of the contents couldn’t be determined."),
            Tuple2(Code(rawValue = 513), "You don’t have permission."),
            Tuple2(Code(rawValue = 514), "The file name is invalid."),
            Tuple2(Code(rawValue = 516), "A file with the same name already exists."),
            Tuple2(Code(rawValue = 517), "The specified text encoding isn’t applicable."),
            Tuple2(Code(rawValue = 518), "The specified URL type isn’t supported."),
            Tuple2(Code(rawValue = 640), "There isn’t enough space."),
            Tuple2(Code(rawValue = 642), "The volume is read only."),
            Tuple2(Code(rawValue = 1024), "The value is invalid."),
            Tuple2(Code(rawValue = 2048), "The value is invalid."),
            Tuple2(Code(rawValue = 3072), "The operation was cancelled."),
            Tuple2(Code(rawValue = 3328), "The requested operation is not supported."),
            Tuple2(Code(rawValue = 3840), "The data is not in the correct format."),
            Tuple2(Code(rawValue = 3841), "The data is in a format that this application doesn’t understand."),
            Tuple2(Code(rawValue = 3842), "An error occurred in the source of the data."),
            Tuple2(Code(rawValue = 3851), "An error occurred in the destination for the data."),
            Tuple2(Code(rawValue = 3852), "An error occurred in the content of the data."),
            Tuple2(Code(rawValue = 4353), "The file is not available on iCloud yet."),
            Tuple2(Code(rawValue = 4354), "There isn’t enough space in your account."),
            Tuple2(Code(rawValue = 4355), "The iCloud servers might be unreachable or your settings might be incorrect."),
            Tuple2(Code(rawValue = 4864), "The data isn’t in the correct format."),
            Tuple2(Code(rawValue = 4865), "The data is missing."),
            Tuple2(Code(rawValue = 4866), "The data isn’t in the correct format.")
        )

        fun error(code: CocoaError.Code, userInfo: Dictionary<AnyHashable, Any>? = null, url: URL? = null): Error {
            var info: Dictionary<String, Any> = (userInfo as? Dictionary<String, Any> ?: dictionaryOf()).sref()
            if (url != null) {
                info[NSURLErrorKey] = url.sref()
            }
            return NSError(domain = NSCocoaErrorDomain, code = code.rawValue, userInfo = info)
        }

        val fileNoSuchFile: CocoaError.Code
            get() = CocoaError.Code.fileNoSuchFile
        val fileLocking: CocoaError.Code
            get() = CocoaError.Code.fileLocking
        val fileReadUnknown: CocoaError.Code
            get() = CocoaError.Code.fileReadUnknown
        val fileReadNoPermission: CocoaError.Code
            get() = CocoaError.Code.fileReadNoPermission
        val fileReadInvalidFileName: CocoaError.Code
            get() = CocoaError.Code.fileReadInvalidFileName
        val fileReadCorruptFile: CocoaError.Code
            get() = CocoaError.Code.fileReadCorruptFile
        val fileReadNoSuchFile: CocoaError.Code
            get() = CocoaError.Code.fileReadNoSuchFile
        val fileReadInapplicableStringEncoding: CocoaError.Code
            get() = CocoaError.Code.fileReadInapplicableStringEncoding
        val fileReadUnsupportedScheme: CocoaError.Code
            get() = CocoaError.Code.fileReadUnsupportedScheme
        val fileReadTooLarge: CocoaError.Code
            get() = CocoaError.Code.fileReadTooLarge
        val fileReadUnknownStringEncoding: CocoaError.Code
            get() = CocoaError.Code.fileReadUnknownStringEncoding
        val fileWriteUnknown: CocoaError.Code
            get() = CocoaError.Code.fileWriteUnknown
        val fileWriteNoPermission: CocoaError.Code
            get() = CocoaError.Code.fileWriteNoPermission
        val fileWriteInvalidFileName: CocoaError.Code
            get() = CocoaError.Code.fileWriteInvalidFileName
        val fileWriteFileExists: CocoaError.Code
            get() = CocoaError.Code.fileWriteFileExists
        val fileWriteInapplicableStringEncoding: CocoaError.Code
            get() = CocoaError.Code.fileWriteInapplicableStringEncoding
        val fileWriteUnsupportedScheme: CocoaError.Code
            get() = CocoaError.Code.fileWriteUnsupportedScheme
        val fileWriteOutOfSpace: CocoaError.Code
            get() = CocoaError.Code.fileWriteOutOfSpace
        val fileWriteVolumeReadOnly: CocoaError.Code
            get() = CocoaError.Code.fileWriteVolumeReadOnly
        val fileManagerUnmountUnknown: CocoaError.Code
            get() = CocoaError.Code.fileManagerUnmountUnknown
        val fileManagerUnmountBusy: CocoaError.Code
            get() = CocoaError.Code.fileManagerUnmountBusy
        val keyValueValidation: CocoaError.Code
            get() = CocoaError.Code.keyValueValidation
        val formatting: CocoaError.Code
            get() = CocoaError.Code.formatting
        val userCancelled: CocoaError.Code
            get() = CocoaError.Code.userCancelled
        val featureUnsupported: CocoaError.Code
            get() = CocoaError.Code.featureUnsupported
        val executableNotLoadable: CocoaError.Code
            get() = CocoaError.Code.executableNotLoadable
        val executableArchitectureMismatch: CocoaError.Code
            get() = CocoaError.Code.executableArchitectureMismatch
        val executableRuntimeMismatch: CocoaError.Code
            get() = CocoaError.Code.executableRuntimeMismatch
        val executableLoad: CocoaError.Code
            get() = CocoaError.Code.executableLoad
        val executableLink: CocoaError.Code
            get() = CocoaError.Code.executableLink
        val propertyListReadCorrupt: CocoaError.Code
            get() = CocoaError.Code.propertyListReadCorrupt
        val propertyListReadUnknownVersion: CocoaError.Code
            get() = CocoaError.Code.propertyListReadUnknownVersion
        val propertyListReadStream: CocoaError.Code
            get() = CocoaError.Code.propertyListReadStream
        val propertyListWriteStream: CocoaError.Code
            get() = CocoaError.Code.propertyListWriteStream
        val propertyListWriteInvalid: CocoaError.Code
            get() = CocoaError.Code.propertyListWriteInvalid
        val xpcConnectionInterrupted: CocoaError.Code
            get() = CocoaError.Code.xpcConnectionInterrupted
        val xpcConnectionInvalid: CocoaError.Code
            get() = CocoaError.Code.xpcConnectionInvalid
        val xpcConnectionReplyInvalid: CocoaError.Code
            get() = CocoaError.Code.xpcConnectionReplyInvalid
        val ubiquitousFileUnavailable: CocoaError.Code
            get() = CocoaError.Code.ubiquitousFileUnavailable
        val ubiquitousFileNotUploadedDueToQuota: CocoaError.Code
            get() = CocoaError.Code.ubiquitousFileNotUploadedDueToQuota
        val ubiquitousFileUbiquityServerNotAvailable: CocoaError.Code
            get() = CocoaError.Code.ubiquitousFileUbiquityServerNotAvailable
        val userActivityHandoffFailed: CocoaError.Code
            get() = CocoaError.Code.userActivityHandoffFailed
        val userActivityConnectionUnavailable: CocoaError.Code
            get() = CocoaError.Code.userActivityConnectionUnavailable
        val userActivityRemoteApplicationTimedOut: CocoaError.Code
            get() = CocoaError.Code.userActivityRemoteApplicationTimedOut
        val userActivityHandoffUserInfoTooLarge: CocoaError.Code
            get() = CocoaError.Code.userActivityHandoffUserInfoTooLarge
        val coderReadCorrupt: CocoaError.Code
            get() = CocoaError.Code.coderReadCorrupt
        val coderValueNotFound: CocoaError.Code
            get() = CocoaError.Code.coderValueNotFound
        val coderInvalidValue: CocoaError.Code
            get() = CocoaError.Code.coderInvalidValue
    }
}

class URLError: Exception, Error {
    val _nsError: NSError

    constructor(_nsError: NSError): super() {
        val error = _nsError
        precondition(error.domain == NSURLErrorDomain)
        this._nsError = error
    }

    constructor(code: URLError.Code, userInfo: Dictionary<String, Any> = dictionaryOf()): this(_nsError = NSError(domain = NSURLErrorDomain, code = code.rawValue, userInfo = userInfo)) {
    }

    val code: URLError.Code
        get() = Code(rawValue = _nsError.code)!!

    enum class Code(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        unknown(-1),
        cancelled(-999),
        badURL(-1000),
        timedOut(-1001),
        unsupportedURL(-1002),
        cannotFindHost(-1003),
        cannotConnectToHost(-1004),
        networkConnectionLost(-1005),
        dnsLookupFailed(-1006),
        httpTooManyRedirects(-1007),
        resourceUnavailable(-1008),
        notConnectedToInternet(-1009),
        redirectToNonExistentLocation(-1010),
        badServerResponse(-1011),
        userCancelledAuthentication(-1012),
        userAuthenticationRequired(-1013),
        zeroByteResource(-1014),
        cannotDecodeRawData(-1015),
        cannotDecodeContentData(-1016),
        cannotParseResponse(-1017),
        appTransportSecurityRequiresSecureConnection(-1022),
        fileDoesNotExist(-1100),
        fileIsDirectory(-1101),
        noPermissionsToReadFile(-1102),
        dataLengthExceedsMaximum(-1103),
        secureConnectionFailed(-1200),
        serverCertificateHasBadDate(-1201),
        serverCertificateUntrusted(-1202),
        serverCertificateHasUnknownRoot(-1203),
        serverCertificateNotYetValid(-1204),
        clientCertificateRejected(-1205),
        clientCertificateRequired(-1206),
        cannotLoadFromNetwork(-2000),
        cannotCreateFile(-3000),
        cannotOpenFile(-3001),
        cannotCloseFile(-3002),
        cannotWriteToFile(-3003),
        cannotRemoveFile(-3004),
        cannotMoveFile(-3005),
        downloadDecodingFailedMidStream(-3006),
        downloadDecodingFailedToComplete(-3007),
        internationalRoamingOff(-1018),
        callIsActive(-1019),
        dataNotAllowed(-1020),
        requestBodyStreamExhausted(-1021),
        backgroundSessionRequiresSharedContainer(-995),
        backgroundSessionInUseByAnotherProcess(-996),
        backgroundSessionWasDisconnected(-997);

        companion object {
        }
    }

    private val _nsUserInfo: Dictionary<String, Any>
        get() = _nsError.userInfo

    val failingURL: URL?
        get() = _nsUserInfo[NSURLErrorFailingURLErrorKey] as? URL

    val failureURLString: String?
        get() = _nsUserInfo[NSURLErrorFailingURLStringErrorKey] as? String

    companion object {

        val _nsErrorDomain: String
            get() = NSURLErrorDomain

        val unknown: URLError.Code
            get() = URLError.Code.unknown
        val cancelled: URLError.Code
            get() = URLError.Code.cancelled
        val badURL: URLError.Code
            get() = URLError.Code.badURL
        val timedOut: URLError.Code
            get() = URLError.Code.timedOut
        val unsupportedURL: URLError.Code
            get() = URLError.Code.unsupportedURL
        val cannotFindHost: URLError.Code
            get() = URLError.Code.cannotFindHost
        val cannotConnectToHost: URLError.Code
            get() = URLError.Code.cannotConnectToHost
        val networkConnectionLost: URLError.Code
            get() = URLError.Code.networkConnectionLost
        val dnsLookupFailed: URLError.Code
            get() = URLError.Code.dnsLookupFailed
        val httpTooManyRedirects: URLError.Code
            get() = URLError.Code.httpTooManyRedirects
        val resourceUnavailable: URLError.Code
            get() = URLError.Code.resourceUnavailable
        val notConnectedToInternet: URLError.Code
            get() = URLError.Code.notConnectedToInternet
        val redirectToNonExistentLocation: URLError.Code
            get() = URLError.Code.redirectToNonExistentLocation
        val badServerResponse: URLError.Code
            get() = URLError.Code.badServerResponse
        val userCancelledAuthentication: URLError.Code
            get() = URLError.Code.userCancelledAuthentication
        val userAuthenticationRequired: URLError.Code
            get() = URLError.Code.userAuthenticationRequired
        val zeroByteResource: URLError.Code
            get() = URLError.Code.zeroByteResource
        val cannotDecodeRawData: URLError.Code
            get() = URLError.Code.cannotDecodeRawData
        val cannotDecodeContentData: URLError.Code
            get() = URLError.Code.cannotDecodeContentData
        val cannotParseResponse: URLError.Code
            get() = URLError.Code.cannotParseResponse
        val fileDoesNotExist: URLError.Code
            get() = URLError.Code.fileDoesNotExist
        val fileIsDirectory: URLError.Code
            get() = URLError.Code.fileIsDirectory
        val noPermissionsToReadFile: URLError.Code
            get() = URLError.Code.noPermissionsToReadFile
        val secureConnectionFailed: URLError.Code
            get() = URLError.Code.secureConnectionFailed
        val serverCertificateHasBadDate: URLError.Code
            get() = URLError.Code.serverCertificateHasBadDate
        val serverCertificateUntrusted: URLError.Code
            get() = URLError.Code.serverCertificateUntrusted
        val serverCertificateHasUnknownRoot: URLError.Code
            get() = URLError.Code.serverCertificateHasUnknownRoot
        val serverCertificateNotYetValid: URLError.Code
            get() = URLError.Code.serverCertificateNotYetValid
        val clientCertificateRejected: URLError.Code
            get() = URLError.Code.clientCertificateRejected
        val clientCertificateRequired: URLError.Code
            get() = URLError.Code.clientCertificateRequired
        val cannotLoadFromNetwork: URLError.Code
            get() = URLError.Code.cannotLoadFromNetwork
        val cannotCreateFile: URLError.Code
            get() = URLError.Code.cannotCreateFile
        val cannotOpenFile: URLError.Code
            get() = URLError.Code.cannotOpenFile
        val cannotCloseFile: URLError.Code
            get() = URLError.Code.cannotCloseFile
        val cannotWriteToFile: URLError.Code
            get() = URLError.Code.cannotWriteToFile
        val cannotRemoveFile: URLError.Code
            get() = URLError.Code.cannotRemoveFile
        val cannotMoveFile: URLError.Code
            get() = URLError.Code.cannotMoveFile
        val downloadDecodingFailedMidStream: URLError.Code
            get() = URLError.Code.downloadDecodingFailedMidStream
        val downloadDecodingFailedToComplete: URLError.Code
            get() = URLError.Code.downloadDecodingFailedToComplete
        val internationalRoamingOff: URLError.Code
            get() = URLError.Code.internationalRoamingOff
        val callIsActive: URLError.Code
            get() = URLError.Code.callIsActive
        val dataNotAllowed: URLError.Code
            get() = URLError.Code.dataNotAllowed
        val requestBodyStreamExhausted: URLError.Code
            get() = URLError.Code.requestBodyStreamExhausted
        val backgroundSessionRequiresSharedContainer: URLError.Code
            get() = URLError.Code.backgroundSessionRequiresSharedContainer
        val backgroundSessionInUseByAnotherProcess: URLError.Code
            get() = URLError.Code.backgroundSessionInUseByAnotherProcess
        val backgroundSessionWasDisconnected: URLError.Code
            get() = URLError.Code.backgroundSessionWasDisconnected

        fun Code(rawValue: Int): URLError.Code? {
            return when (rawValue) {
                -1 -> Code.unknown
                -999 -> Code.cancelled
                -1000 -> Code.badURL
                -1001 -> Code.timedOut
                -1002 -> Code.unsupportedURL
                -1003 -> Code.cannotFindHost
                -1004 -> Code.cannotConnectToHost
                -1005 -> Code.networkConnectionLost
                -1006 -> Code.dnsLookupFailed
                -1007 -> Code.httpTooManyRedirects
                -1008 -> Code.resourceUnavailable
                -1009 -> Code.notConnectedToInternet
                -1010 -> Code.redirectToNonExistentLocation
                -1011 -> Code.badServerResponse
                -1012 -> Code.userCancelledAuthentication
                -1013 -> Code.userAuthenticationRequired
                -1014 -> Code.zeroByteResource
                -1015 -> Code.cannotDecodeRawData
                -1016 -> Code.cannotDecodeContentData
                -1017 -> Code.cannotParseResponse
                -1022 -> Code.appTransportSecurityRequiresSecureConnection
                -1100 -> Code.fileDoesNotExist
                -1101 -> Code.fileIsDirectory
                -1102 -> Code.noPermissionsToReadFile
                -1103 -> Code.dataLengthExceedsMaximum
                -1200 -> Code.secureConnectionFailed
                -1201 -> Code.serverCertificateHasBadDate
                -1202 -> Code.serverCertificateUntrusted
                -1203 -> Code.serverCertificateHasUnknownRoot
                -1204 -> Code.serverCertificateNotYetValid
                -1205 -> Code.clientCertificateRejected
                -1206 -> Code.clientCertificateRequired
                -2000 -> Code.cannotLoadFromNetwork
                -3000 -> Code.cannotCreateFile
                -3001 -> Code.cannotOpenFile
                -3002 -> Code.cannotCloseFile
                -3003 -> Code.cannotWriteToFile
                -3004 -> Code.cannotRemoveFile
                -3005 -> Code.cannotMoveFile
                -3006 -> Code.downloadDecodingFailedMidStream
                -3007 -> Code.downloadDecodingFailedToComplete
                -1018 -> Code.internationalRoamingOff
                -1019 -> Code.callIsActive
                -1020 -> Code.dataNotAllowed
                -1021 -> Code.requestBodyStreamExhausted
                -995 -> Code.backgroundSessionRequiresSharedContainer
                -996 -> Code.backgroundSessionInUseByAnotherProcess
                -997 -> Code.backgroundSessionWasDisconnected
                else -> null
            }
        }
    }
}

typealias POSIXErrorCode = POSIXError.Code

class POSIXError: Exception, Error {
    val _nsError: NSError

    constructor(_nsError: NSError): super() {
        val error = _nsError
        precondition(error.domain == NSPOSIXErrorDomain)
        this._nsError = error
    }

    //public typealias Code = POSIXErrorCode // FIXME: Kotlin does not support typealias declarations within functions and types. Consider moving this to a top level declaration

    enum class Code(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        EPERM(1),
        ENOENT(2),
        ESRCH(3),
        EINTR(4),
        EIO(5),
        ENXIO(6),
        E2BIG(7),
        ENOEXEC(8),
        EBADF(9),
        ECHILD(10),
        EDEADLK(11),
        ENOMEM(12),
        EACCES(13),
        EFAULT(14),
        ENOTBLK(15),
        EBUSY(16),
        EEXIST(17),
        EXDEV(18),
        ENODEV(19),
        ENOTDIR(20),
        EISDIR(21),
        EINVAL(22),
        ENFILE(23),
        EMFILE(24),
        ENOTTY(25),
        ETXTBSY(26),
        EFBIG(27),
        ENOSPC(28),
        ESPIPE(29),
        EROFS(30),
        EMLINK(31),
        EPIPE(32),
        EDOM(33),
        ERANGE(34),
        EAGAIN(35),
        //public static var EWOULDBLOCK: POSIXErrorCode { get }
        EINPROGRESS(36),
        EALREADY(37),
        ENOTSOCK(38),
        EDESTADDRREQ(39),
        EMSGSIZE(40),
        EPROTOTYPE(41),
        ENOPROTOOPT(42),
        EPROTONOSUPPORT(43),
        ESOCKTNOSUPPORT(44),
        ENOTSUP(45),
        EPFNOSUPPORT(46),
        EAFNOSUPPORT(47),
        EADDRINUSE(48),
        EADDRNOTAVAIL(49),
        ENETDOWN(50),
        ENETUNREACH(51),
        ENETRESET(52),
        ECONNABORTED(53),
        ECONNRESET(54),
        ENOBUFS(55),
        EISCONN(56),
        ENOTCONN(57),
        ESHUTDOWN(58),
        ETOOMANYREFS(59),
        ETIMEDOUT(60),
        ECONNREFUSED(61),
        ELOOP(62),
        ENAMETOOLONG(63),
        EHOSTDOWN(64),
        EHOSTUNREACH(65),
        ENOTEMPTY(66),
        EPROCLIM(67),
        EUSERS(68),
        EDQUOT(69),
        ESTALE(70),
        EREMOTE(71),
        EBADRPC(72),
        ERPCMISMATCH(73),
        EPROGUNAVAIL(74),
        EPROGMISMATCH(75),
        EPROCUNAVAIL(76),
        ENOLCK(77),
        ENOSYS(78),
        EFTYPE(79),
        EAUTH(80),
        ENEEDAUTH(81),
        EPWROFF(82),
        EDEVERR(83),
        EOVERFLOW(84),
        EBADEXEC(85),
        EBADARCH(86),
        ESHLIBVERS(87),
        EBADMACHO(88),
        ECANCELED(89),
        EIDRM(90),
        ENOMSG(91),
        EILSEQ(92),
        ENOATTR(93),
        EBADMSG(94),
        EMULTIHOP(95),
        ENODATA(96),
        ENOLINK(97),
        ENOSR(98),
        ENOSTR(99),
        EPROTO(100),
        ETIME(101),
        ENOPOLICY(103),
        ENOTRECOVERABLE(104),
        EOWNERDEAD(105),
        EQFULL(106);
        //public static var ELAST: POSIXErrorCode { get }

        companion object {
        }
    }

    companion object {

        val _nsErrorDomain: String
            get() = NSPOSIXErrorDomain

        val EPERM: POSIXError.Code
            get() = POSIXError.Code.EPERM
        val ENOENT: POSIXError.Code
            get() = POSIXError.Code.ENOENT
        val ESRCH: POSIXError.Code
            get() = POSIXError.Code.ESRCH
        val EINTR: POSIXError.Code
            get() = POSIXError.Code.EINTR
        val EIO: POSIXError.Code
            get() = POSIXError.Code.EIO
        val ENXIO: POSIXError.Code
            get() = POSIXError.Code.ENXIO
        val E2BIG: POSIXError.Code
            get() = POSIXError.Code.E2BIG
        val ENOEXEC: POSIXError.Code
            get() = POSIXError.Code.ENOEXEC
        val EBADF: POSIXError.Code
            get() = POSIXError.Code.EBADF
        val ECHILD: POSIXError.Code
            get() = POSIXError.Code.ECHILD
        val EDEADLK: POSIXError.Code
            get() = POSIXError.Code.EDEADLK
        val ENOMEM: POSIXError.Code
            get() = POSIXError.Code.ENOMEM
        val EACCES: POSIXError.Code
            get() = POSIXError.Code.EACCES
        val EFAULT: POSIXError.Code
            get() = POSIXError.Code.EFAULT
        val EBUSY: POSIXError.Code
            get() = POSIXError.Code.EBUSY
        val EEXIST: POSIXError.Code
            get() = POSIXError.Code.EEXIST
        val EXDEV: POSIXError.Code
            get() = POSIXError.Code.EXDEV
        val ENODEV: POSIXError.Code
            get() = POSIXError.Code.ENODEV
        val ENOTDIR: POSIXError.Code
            get() = POSIXError.Code.ENOTDIR
        val EISDIR: POSIXError.Code
            get() = POSIXError.Code.EISDIR
        val EINVAL: POSIXError.Code
            get() = POSIXError.Code.EINVAL
        val ENFILE: POSIXError.Code
            get() = POSIXError.Code.ENFILE
        val EMFILE: POSIXError.Code
            get() = POSIXError.Code.EMFILE
        val ENOTTY: POSIXError.Code
            get() = POSIXError.Code.ENOTTY
        val EFBIG: POSIXError.Code
            get() = POSIXError.Code.EFBIG
        val ENOSPC: POSIXError.Code
            get() = POSIXError.Code.ENOSPC
        val ESPIPE: POSIXError.Code
            get() = POSIXError.Code.ESPIPE
        val EROFS: POSIXError.Code
            get() = POSIXError.Code.EROFS
        val EMLINK: POSIXError.Code
            get() = POSIXError.Code.EMLINK
        val EPIPE: POSIXError.Code
            get() = POSIXError.Code.EPIPE
        val EDOM: POSIXError.Code
            get() = POSIXError.Code.EDOM
        val ERANGE: POSIXError.Code
            get() = POSIXError.Code.ERANGE
        val EAGAIN: POSIXError.Code
            get() = POSIXError.Code.EAGAIN
        val ENAMETOOLONG: POSIXError.Code
            get() = POSIXError.Code.ENAMETOOLONG
        val ENOTEMPTY: POSIXError.Code
            get() = POSIXError.Code.ENOTEMPTY
        val ENOLCK: POSIXError.Code
            get() = POSIXError.Code.ENOLCK
        val ENOSYS: POSIXError.Code
            get() = POSIXError.Code.ENOSYS
        val ECANCELED: POSIXError.Code
            get() = POSIXError.Code.ECANCELED
        val EILSEQ: POSIXError.Code
            get() = POSIXError.Code.EILSEQ

        fun Code(rawValue: Int): POSIXError.Code? {
            return when (rawValue) {
                1 -> Code.EPERM
                2 -> Code.ENOENT
                3 -> Code.ESRCH
                4 -> Code.EINTR
                5 -> Code.EIO
                6 -> Code.ENXIO
                7 -> Code.E2BIG
                8 -> Code.ENOEXEC
                9 -> Code.EBADF
                10 -> Code.ECHILD
                11 -> Code.EDEADLK
                12 -> Code.ENOMEM
                13 -> Code.EACCES
                14 -> Code.EFAULT
                15 -> Code.ENOTBLK
                16 -> Code.EBUSY
                17 -> Code.EEXIST
                18 -> Code.EXDEV
                19 -> Code.ENODEV
                20 -> Code.ENOTDIR
                21 -> Code.EISDIR
                22 -> Code.EINVAL
                23 -> Code.ENFILE
                24 -> Code.EMFILE
                25 -> Code.ENOTTY
                26 -> Code.ETXTBSY
                27 -> Code.EFBIG
                28 -> Code.ENOSPC
                29 -> Code.ESPIPE
                30 -> Code.EROFS
                31 -> Code.EMLINK
                32 -> Code.EPIPE
                33 -> Code.EDOM
                34 -> Code.ERANGE
                35 -> Code.EAGAIN
                36 -> Code.EINPROGRESS
                37 -> Code.EALREADY
                38 -> Code.ENOTSOCK
                39 -> Code.EDESTADDRREQ
                40 -> Code.EMSGSIZE
                41 -> Code.EPROTOTYPE
                42 -> Code.ENOPROTOOPT
                43 -> Code.EPROTONOSUPPORT
                44 -> Code.ESOCKTNOSUPPORT
                45 -> Code.ENOTSUP
                46 -> Code.EPFNOSUPPORT
                47 -> Code.EAFNOSUPPORT
                48 -> Code.EADDRINUSE
                49 -> Code.EADDRNOTAVAIL
                50 -> Code.ENETDOWN
                51 -> Code.ENETUNREACH
                52 -> Code.ENETRESET
                53 -> Code.ECONNABORTED
                54 -> Code.ECONNRESET
                55 -> Code.ENOBUFS
                56 -> Code.EISCONN
                57 -> Code.ENOTCONN
                58 -> Code.ESHUTDOWN
                59 -> Code.ETOOMANYREFS
                60 -> Code.ETIMEDOUT
                61 -> Code.ECONNREFUSED
                62 -> Code.ELOOP
                63 -> Code.ENAMETOOLONG
                64 -> Code.EHOSTDOWN
                65 -> Code.EHOSTUNREACH
                66 -> Code.ENOTEMPTY
                67 -> Code.EPROCLIM
                68 -> Code.EUSERS
                69 -> Code.EDQUOT
                70 -> Code.ESTALE
                71 -> Code.EREMOTE
                72 -> Code.EBADRPC
                73 -> Code.ERPCMISMATCH
                74 -> Code.EPROGUNAVAIL
                75 -> Code.EPROGMISMATCH
                76 -> Code.EPROCUNAVAIL
                77 -> Code.ENOLCK
                78 -> Code.ENOSYS
                79 -> Code.EFTYPE
                80 -> Code.EAUTH
                81 -> Code.ENEEDAUTH
                82 -> Code.EPWROFF
                83 -> Code.EDEVERR
                84 -> Code.EOVERFLOW
                85 -> Code.EBADEXEC
                86 -> Code.EBADARCH
                87 -> Code.ESHLIBVERS
                88 -> Code.EBADMACHO
                89 -> Code.ECANCELED
                90 -> Code.EIDRM
                91 -> Code.ENOMSG
                92 -> Code.EILSEQ
                93 -> Code.ENOATTR
                94 -> Code.EBADMSG
                95 -> Code.EMULTIHOP
                96 -> Code.ENODATA
                97 -> Code.ENOLINK
                98 -> Code.ENOSR
                99 -> Code.ENOSTR
                100 -> Code.EPROTO
                101 -> Code.ETIME
                103 -> Code.ENOPOLICY
                104 -> Code.ENOTRECOVERABLE
                105 -> Code.EOWNERDEAD
                106 -> Code.EQFULL
                else -> null
            }
        }
    }
}

internal sealed class UnknownNSError: Exception(), Error {
    class MissingErrorCase: UnknownNSError() {
        override fun equals(other: Any?): Boolean = other is MissingErrorCase
        override fun hashCode(): Int = "MissingErrorCase".hashCode()
    }

    companion object {
        val missingError: UnknownNSError
            get() = MissingErrorCase()
    }
}

val NSURLErrorDomain: String = "NSURLErrorDomain"
val NSURLErrorFailingURLErrorKey: String = "NSErrorFailingURLKey"
val NSURLErrorFailingURLStringErrorKey: String = "NSErrorFailingURLStringKey"
val NSURLErrorFailingURLPeerTrustErrorKey: String = "NSURLErrorFailingURLPeerTrustErrorKey"
val NSURLErrorBackgroundTaskCancelledReasonKey: String = "NSURLErrorBackgroundTaskCancelledReasonKey"
val NSURLErrorCancelledReasonUserForceQuitApplication: Int
    get() = 0
val NSURLErrorCancelledReasonBackgroundUpdatesDisabled: Int
    get() = 1
val NSURLErrorCancelledReasonInsufficientSystemResources: Int
    get() = 2

val NSURLErrorUnknown: Int
    get() = -1
val NSURLErrorCancelled: Int
    get() = -999
val NSURLErrorBadURL: Int
    get() = -1000
val NSURLErrorTimedOut: Int
    get() = -1001
val NSURLErrorUnsupportedURL: Int
    get() = -1002
val NSURLErrorCannotFindHost: Int
    get() = -1003
val NSURLErrorCannotConnectToHost: Int
    get() = -1004
val NSURLErrorNetworkConnectionLost: Int
    get() = -1005
val NSURLErrorDNSLookupFailed: Int
    get() = -1006
val NSURLErrorHTTPTooManyRedirects: Int
    get() = -1007
val NSURLErrorResourceUnavailable: Int
    get() = -1008
val NSURLErrorNotConnectedToInternet: Int
    get() = -1009
val NSURLErrorRedirectToNonExistentLocation: Int
    get() = -1010
val NSURLErrorBadServerResponse: Int
    get() = -1011
val NSURLErrorUserCancelledAuthentication: Int
    get() = -1012
val NSURLErrorUserAuthenticationRequired: Int
    get() = -1013
val NSURLErrorZeroByteResource: Int
    get() = -1014
val NSURLErrorCannotDecodeRawData: Int
    get() = -1015
val NSURLErrorCannotDecodeContentData: Int
    get() = -1016
val NSURLErrorCannotParseResponse: Int
    get() = -1017
val NSURLErrorAppTransportSecurityRequiresSecureConnection: Int
    get() = -1022
val NSURLErrorFileDoesNotExist: Int
    get() = -1100
val NSURLErrorFileIsDirectory: Int
    get() = -1101
val NSURLErrorNoPermissionsToReadFile: Int
    get() = -1102
val NSURLErrorDataLengthExceedsMaximum: Int
    get() = -1103

val NSURLErrorSecureConnectionFailed: Int
    get() = -1201
val NSURLErrorServerCertificateHasBadDate: Int
    get() = -1202
val NSURLErrorServerCertificateUntrusted: Int
    get() = -1203
val NSURLErrorServerCertificateHasUnknownRoot: Int
    get() = -1204
val NSURLErrorServerCertificateNotYetValid: Int
    get() = -1205
val NSURLErrorClientCertificateRejected: Int
    get() = -1206
val NSURLErrorClientCertificateRequired: Int
    get() = -1207
val NSURLErrorCannotLoadFromNetwork: Int
    get() = -2000

val NSURLErrorCannotCreateFile: Int
    get() = -3000
val NSURLErrorCannotOpenFile: Int
    get() = -3001
val NSURLErrorCannotCloseFile: Int
    get() = -3002
val NSURLErrorCannotWriteToFile: Int
    get() = -3003
val NSURLErrorCannotRemoveFile: Int
    get() = -3004
val NSURLErrorCannotMoveFile: Int
    get() = -3005
val NSURLErrorDownloadDecodingFailedMidStream: Int
    get() = -3006
val NSURLErrorDownloadDecodingFailedToComplete: Int
    get() = -3007

val NSURLErrorInternationalRoamingOff: Int
    get() = -1018
val NSURLErrorCallIsActive: Int
    get() = -1019
val NSURLErrorDataNotAllowed: Int
    get() = -1020
val NSURLErrorRequestBodyStreamExhausted: Int
    get() = -1021

val NSURLErrorBackgroundSessionRequiresSharedContainer: Int
    get() = -995
val NSURLErrorBackgroundSessionInUseByAnotherProcess: Int
    get() = -996
val NSURLErrorBackgroundSessionWasDisconnected: Int
    get() = -997

