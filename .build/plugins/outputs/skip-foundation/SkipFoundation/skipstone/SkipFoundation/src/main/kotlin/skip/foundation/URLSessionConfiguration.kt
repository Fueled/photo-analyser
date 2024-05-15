// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array


open class URLSessionConfiguration {

    open var identifier: String? = null
    open var requestCachePolicy: URLRequest.CachePolicy = URLRequest.CachePolicy.useProtocolCachePolicy
    open var timeoutIntervalForRequest: Double = 60.0
    open var timeoutIntervalForResource: Double = 604800.0
    open var networkServiceType: URLRequest.NetworkServiceType = URLRequest.NetworkServiceType.default
    open var allowsCellularAccess: Boolean = true
    open var allowsExpensiveNetworkAccess: Boolean = true
    open var allowsConstrainedNetworkAccess: Boolean = true
    open var requiresDNSSECValidation: Boolean = true
    open var waitsForConnectivity: Boolean = false
    open var isDiscretionary: Boolean = false
    open var sharedContainerIdentifier: String? = null
    open var sessionSendsLaunchEvents: Boolean = false
    open var connectionProxyDictionary: Dictionary<AnyHashable, Any>? = null
        get() = field.sref({ this.connectionProxyDictionary = it })
        set(newValue) {
            field = newValue.sref()
        }
    open var httpShouldUsePipelining: Boolean = false
    open var httpShouldSetCookies: Boolean = true
    open var httpAdditionalHeaders: Dictionary<AnyHashable, Any>? = null
        get() = field.sref({ this.httpAdditionalHeaders = it })
        set(newValue) {
            field = newValue.sref()
        }
    open var httpMaximumConnectionsPerHost: Int = 6
    open var shouldUseExtendedBackgroundIdleMode: Boolean = false
    open var protocolClasses: Array<AnyClass>? = null
        get() = field.sref({ this.protocolClasses = it })
        set(newValue) {
            field = newValue.sref()
        }

    constructor() {
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var httpCookieAcceptPolicy: Any
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var httpCookieStorage: Any?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var urlCredentialStorage: Any?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var urlCache: Any?
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var tlsMinimumSupportedProtocol: Any
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var tlsMaximumSupportedProtocol: Any
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var tlsMinimumSupportedProtocolVersion: Any
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var tlsMaximumSupportedProtocolVersion: Any
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var multipathServiceType: Any
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var proxyConfigurations: Array<Any>
        get() {
            fatalError()
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
        }

    companion object: CompanionClass() {
        override val default: URLSessionConfiguration
            get() = _default
        private val _default = URLSessionConfiguration()

        override val ephemeral: URLSessionConfiguration
            get() = _ephemeral
        // TODO: ephemeral config
        private val _ephemeral = URLSessionConfiguration()

        @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
        open fun background(withIdentifier: String): URLSessionConfiguration {
            fatalError()
        }
    }
    open class CompanionClass {
        open val default: URLSessionConfiguration
            get() = URLSessionConfiguration.default
        open val ephemeral: URLSessionConfiguration
            get() = URLSessionConfiguration.ephemeral
    }
}

