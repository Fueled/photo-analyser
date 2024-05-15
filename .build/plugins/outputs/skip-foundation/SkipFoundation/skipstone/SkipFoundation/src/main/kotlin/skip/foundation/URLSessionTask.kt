// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString

open class URLSessionTask {

    internal val session: URLSession
    internal val build: (Request.Builder) -> Unit
    internal val lock = NSRecursiveLock()
    internal val completionHandler: ((Data?, URLResponse?, Error?) -> Unit)?

    internal constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, build: (Request.Builder) -> Unit = { it ->  }, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null) {
        this.session = session
        this.originalRequest = request.sref()
        this.taskIdentifier = taskIdentifier
        this.build = build
        this.completionHandler = completionHandler
    }

    val taskIdentifier: Int
    val originalRequest: URLRequest?
    open var delegate: URLSessionTaskDelegate?
        get() {
            return lock.withLock { -> _delegate }.sref({ this.delegate = it })
        }
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            lock.withLock { -> _delegate = newValue }
        }
    private var _delegate: URLSessionTaskDelegate? = null
        get() = field.sref({ this._delegate = it })
        set(newValue) {
            field = newValue.sref()
        }

    open var countOfBytesClientExpectsToReceive = Long(-1)
    open var countOfBytesClientExpectsToSend = Long(-1)

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val progress: Any? = null /* Progress(totalUnitCount: -1) */

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open var earliestBeginDate: Date? = null
        get() = field.sref()
        set(newValue) {
            field = newValue.sref()
        }

    enum class State(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        running(0),
        suspended(1),
        canceling(2),
        completed(3);

        companion object {
        }
    }

    open val state: URLSessionTask.State
        get() {
            return lock.withLock { -> _state }
        }
    private var _state: URLSessionTask.State = URLSessionTask.State.suspended

    open val error: Error?
        get() {
            return lock.withLock { -> _error }
        }
    private var _error: Error? = null
        get() = field.sref({ this._error = it })
        set(newValue) {
            field = newValue.sref()
        }

    open val currentRequest: URLRequest?
        get() = originalRequest

    val countOfBytesReceived = Long(0)
    val countOfBytesSent = Long(0)
    val countOfBytesExpectedToSend = Long(0)
    val countOfBytesExpectedToReceive = Long(0)

    open var priority: Float
        get() {
            return lock.withLock { -> _priority }
        }
        set(newValue) {
            lock.withLock { -> _priority = newValue }
        }
    private var _priority: Float = URLSessionTask.defaultPriority

    open var taskDescription: String? = null

    open fun cancel() {
        var completionError: Error? = null
        lock.withLock l@{ ->
            if (_state != URLSessionTask.State.running && _state != URLSessionTask.State.suspended) {
                return@l
            }
            _state = URLSessionTask.State.canceling
            var info = dictionaryOf(Tuple2(NSLocalizedDescriptionKey, "${URLError.Code.cancelled}" as Any))
            originalRequest?.url.sref()?.let { url ->
                info[NSURLErrorFailingURLErrorKey] = url.sref()
                info[NSURLErrorFailingURLStringErrorKey] = url.absoluteString
            }
            try {
                close()
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
            }
            completionError = URLError(URLError.Code.cancelled, userInfo = info)
        }
        completion(data = null, response = null, error = completionError)
    }

    open fun suspend() {
        lock.withLock l@{ ->
            if (_state == URLSessionTask.State.canceling || _state == URLSessionTask.State.completed) {
                return@l
            }
            _suspendCount += 1
            _state = URLSessionTask.State.suspended
            if (_suspendCount != 1) {
                return@l
            }
            close()
        }
    }
    private var _suspendCount = 0

    open fun resume() {
        var completionError: Error? = null
        lock.withLock l@{ ->
            if (_state == URLSessionTask.State.canceling || _state == URLSessionTask.State.completed) {
                return@l
            }
            if (_suspendCount > 0) {
                _suspendCount -= 1
            }
            if (_suspendCount != 0) {
                return@l
            }
            _state = URLSessionTask.State.running
            val request_0 = originalRequest.sref()
            if (request_0 == null) {
                completionError = URLError(URLError.Code.badURL)
                return@l
            }
            val url_0 = request_0.url.sref()
            if (url_0 == null) {
                completionError = URLError(URLError.Code.badURL)
                return@l
            }
            try {
                open(request = request_0, with = url_0)
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
                completionError = error.sref()
            }
        }
        if (completionError != null) {
            completion(data = null, response = null, error = completionError)
        }
    }

    // MARK: - Internal

    /// Open the connection. Called with lock.
    internal open fun open(request: URLRequest, with: URL) = Unit

    /// Close the connection. Called with lock.
    internal open fun close() = Unit

    /// Send completion events.
    internal open fun completion(data: Data?, response: URLResponse?, error: Error?) {
        lock.withLock { ->
            _error = error
            if (_state != URLSessionTask.State.completed && _state != URLSessionTask.State.canceling) {
                _state = if (error == null) URLSessionTask.State.completed else URLSessionTask.State.canceling
            }
        }
        session.taskDidComplete(this)
        completionHandler?.let { completionHandler ->
            completionHandler(data, response, error)
        }
        withDelegates(task = delegate, session = session.delegate as? URLSessionTaskDelegate) { delegate -> delegate.urlSession(session, task = this, didCompleteWithError = error) }
    }

    internal open fun <D> withDelegates(task: D?, session: D?, operation: (D) -> Unit) {
        val taskDelegate = task
        val sessionDelegate = session
        if (taskDelegate == null && sessionDelegate == null) {
            return
        }
        this.session.delegateQueue.runBlock { ->
            if (taskDelegate != null) {
                operation(taskDelegate)
            }
            if (sessionDelegate != null) {
                operation(sessionDelegate)
            }
        }
    }

    companion object: CompanionClass() {
        override val defaultPriority = 0.5f
        override val lowPriority = 0.25f
        override val highPriority = 0.75f

        override fun State(rawValue: Int): URLSessionTask.State? {
            return when (rawValue) {
                0 -> State.running
                1 -> State.suspended
                2 -> State.canceling
                3 -> State.completed
                else -> null
            }
        }
    }
    open class CompanionClass {
        open val defaultPriority
            get() = URLSessionTask.defaultPriority
        open val lowPriority
            get() = URLSessionTask.lowPriority
        open val highPriority
            get() = URLSessionTask.highPriority
        open fun State(rawValue: Int): URLSessionTask.State? = URLSessionTask.State(rawValue = rawValue)
    }
}

open class _URLSessionDataTask: URLSessionTask {
    internal open var genericJob: Job? = null
        get() = field.sref({ this.genericJob = it })
        set(newValue) {
            field = newValue.sref()
        }
    internal open var httpCall: Call? = null
        get() = field.sref({ this.httpCall = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal open var isForResponse = false
    internal var genericConnection: java.net.URLConnection? = null
        get() = field.sref({ this.genericConnection = it })
        private set(newValue) {
            field = newValue.sref()
        }
    internal var httpResponse: Response? = null
        get() = field.sref({ this.httpResponse = it })
        private set(newValue) {
            field = newValue.sref()
        }

    override fun open(request: URLRequest, with: URL) {
        val url = with
        when (RequestType(request)) {
            RequestType.generic -> {
                val job = Job()
                genericJob = job
                GlobalScope.launch(job) { ->
                    try {
                        val (data, response, connection) = genericResponse(for_ = request, with = url, isForResponse = isForResponse)
                        genericConnection = connection
                        notifyDelegate(response = response)
                        if (data != null) {
                            notifyDelegate(data = data)
                        }
                        completion(data = data, response = response, error = null)
                    } catch (error: Throwable) {
                        @Suppress("NAME_SHADOWING") val error = error.aserror()
                        completion(data = null, response = null, error = error)
                    }
                }
            }
            RequestType.http -> {
                val (client, httpRequest) = httpRequest(for_ = request, with = url, configuration = session.configuration, build = build)
                httpCall = client.newCall(httpRequest)
                httpCall?.enqueue(HTTPCallback(task = this, url = url))
            }
        }
    }

    override fun close() {
        try {
            httpCall?.cancel()
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
        }
        httpCall = null
        try {
            genericJob?.cancel()
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
        }
        genericJob = null
    }

    private fun notifyDelegate(response: URLResponse) {
        (this as? URLSessionDataTask)?.let { dataTask ->
            withDelegates(task = delegate as? URLSessionDataDelegate, session = session.delegate as? URLSessionDataDelegate) { delegate ->
                delegate.urlSession(session, dataTask = dataTask, didReceive = response, completionHandler = { _ ->  })
            }
        }
    }

    private fun notifyDelegate(data: Data) {
        (this as? URLSessionDataTask)?.let { dataTask ->
            withDelegates(task = delegate as? URLSessionDataDelegate, session = session.delegate as? URLSessionDataDelegate) { delegate -> delegate.urlSession(session, dataTask = dataTask, didReceive = data) }
        }
    }

    private class HTTPCallback: Callback {
        private val task: _URLSessionDataTask
        private val url: URL

        internal constructor(task: _URLSessionDataTask, url: URL) {
            this.task = task
            this.url = url.sref()
        }

        override fun onFailure(call: Call, e: java.io.IOException): Unit = task.completion(data = null, response = null, error = ErrorException(e))

        override fun onResponse(call: Call, response: Response) {
            var deferaction_0: (() -> Unit)? = null
            try {
                deferaction_0 = {
                    if (response != task.httpResponse) {
                        try {
                            response.close()
                        } catch (error: Throwable) {
                            @Suppress("NAME_SHADOWING") val error = error.aserror()
                        }
                    }
                }
                try {
                    val urlResponse = httpURLResponse(from = response, with = url)
                    task.notifyDelegate(response = urlResponse)
                    val data: Data?
                    if (task.isForResponse) {
                        data = null
                        task.httpResponse = response
                    } else {
                        val matchtarget_0 = response.body?.bytes()
                        if (matchtarget_0 != null) {
                            val bytes = matchtarget_0
                            data = Data(platformValue = bytes)
                        } else {
                            data = Data()
                        }
                        task.notifyDelegate(data = data)
                    }
                    task.completion(data = data, response = urlResponse, error = null)
                } catch (error: Throwable) {
                    @Suppress("NAME_SHADOWING") val error = error.aserror()
                    task.httpResponse = null
                    task.completion(data = null, response = null, error = error)
                }
            } finally {
                deferaction_0?.invoke()
            }
        }
    }

    constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, build: (Request.Builder) -> Unit = { it ->  }, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): super(session, request, taskIdentifier, build, completionHandler) {
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass: URLSessionTask.CompanionClass() {
    }
}

open class URLSessionDataTask: _URLSessionDataTask {

    constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, build: (Request.Builder) -> Unit = { it ->  }, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): super(session, request, taskIdentifier, build, completionHandler) {
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass: _URLSessionDataTask.CompanionClass() {
    }
}

open class URLSessionUploadTask: _URLSessionDataTask {

    constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, build: (Request.Builder) -> Unit = { it ->  }, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): super(session, request, taskIdentifier, build, completionHandler) {
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass: _URLSessionDataTask.CompanionClass() {
    }
}

open class URLSessionDownloadTask: URLSessionTask {
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun cancel(byProducingResumeData: (Data?) -> Unit) {
        val completionHandler = byProducingResumeData
        fatalError()
    }

    constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, build: (Request.Builder) -> Unit = { it ->  }, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): super(session, request, taskIdentifier, build, completionHandler) {
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass: URLSessionTask.CompanionClass() {
    }
}

open class URLSessionWebSocketTask: URLSessionTask {
    enum class CloseCode(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        invalid(0),
        normalClosure(1000),
        goingAway(1001),
        protocolError(1002),
        unsupportedData(1003),
        noStatusReceived(1005),
        abnormalClosure(1006),
        invalidFramePayloadData(1007),
        policyViolation(1008),
        messageTooBig(1009),
        mandatoryExtensionMissing(1010),
        internalServerError(1011),
        tlsHandshakeFailure(1015);

        companion object {
        }
    }

    sealed class Message {
        class DataCase(val associated0: Data): Message() {
        }
        class StringCase(val associated0: String): Message() {
        }

        companion object {
            fun data(associated0: Data): Message = DataCase(associated0)
            fun string(associated0: String): Message = StringCase(associated0)
        }
    }

    private val listener: URLSessionWebSocketTask.Listener
    private var webSocket: WebSocket? = null
        get() = field.sref({ this.webSocket = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var url: URL? = null
        get() = field.sref({ this.url = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var channel: Channel<URLSessionWebSocketTask.Message>? = null
        get() = field.sref({ this.channel = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): super(session = session, request = request, taskIdentifier = taskIdentifier, completionHandler = completionHandler) {
        this.listener = Listener(task = this)
    }

    override fun open(request: URLRequest, with: URL) {
        val url = with
        val (client, httpRequest) = httpRequest(for_ = request, with = url, configuration = session.configuration, build = build)
        this.url = url
        webSocket = client.newWebSocket(httpRequest, listener)
        channel = Channel<URLSessionWebSocketTask.Message>(Channel.UNLIMITED)
    }

    override fun close() {
        webSocket?.close((_closeCode ?: URLSessionWebSocketTask.CloseCode.invalid).rawValue, _closeReason?.utf8String)
        webSocket = null
        channel?.close()
        channel = null
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open suspend fun sendPing(): Unit = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun sendPing(pongReceiveHandler: (Error?) -> Unit) = Unit

    override fun cancel(): Unit = cancel(with = URLSessionWebSocketTask.CloseCode.invalid, reason = null)

    open fun cancel(with: URLSessionWebSocketTask.CloseCode, reason: Data?) {
        val closeCode = with
        lock.withLock { ->
            _closeCode = closeCode
            _closeReason = reason
        }
        super.cancel()
    }

    open var maximumMessageSize: Int = 1 * 1024 * 1024

    open val closeCode: URLSessionWebSocketTask.CloseCode
        get() {
            return lock.withLock { -> _closeCode } ?: URLSessionWebSocketTask.CloseCode.invalid
        }
    private var _closeCode: URLSessionWebSocketTask.CloseCode? = null

    open val closeReason: Data?
        get() {
            return lock.withLock { -> _closeReason }
        }
    private var _closeReason: Data? = null
        get() = field.sref({ this._closeReason = it })
        set(newValue) {
            field = newValue.sref()
        }

    open suspend fun send(message: URLSessionWebSocketTask.Message): Unit = Async.run {
        val webSocket_0 = lock.withLock({ -> this.webSocket })
        if (webSocket_0 == null) {
            throw URLError(URLError.Code.cancelled)
        }
        when (message) {
            is URLSessionWebSocketTask.Message.DataCase -> {
                val data = message.associated0
                webSocket_0.send(data.platformValue.toByteString())
            }
            is URLSessionWebSocketTask.Message.StringCase -> {
                val string = message.associated0
                webSocket_0.send(string)
            }
        }
    }

    open suspend fun receive(): URLSessionWebSocketTask.Message = Async.run l@{
        val channel_0 = lock.withLock({ -> this.channel })
        if (channel_0 == null) {
            throw URLError(URLError.Code.cancelled)
        }
        return@l channel_0.receive()
    }

    private class Listener: WebSocketListener {
        internal val task: URLSessionWebSocketTask

        internal constructor(task: URLSessionWebSocketTask) {
            this.task = task
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket = webSocket, response = response)
            task.withDelegates(task = task.delegate as? URLSessionWebSocketDelegate, session = task.session.delegate as? URLSessionWebSocketDelegate) { delegate -> delegate.urlSession(task.session, webSocketTask = task, didOpenWithProtocol = response.protocol.toString()) }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket = webSocket, code = code, reason = reason)
            task.withDelegates(task = task.delegate as? URLSessionWebSocketDelegate, session = task.session.delegate as? URLSessionWebSocketDelegate) { delegate ->
                val closeCode = CloseCode(rawValue = code) ?: CloseCode.invalid
                val closeData = reason.utf8Data.sref()
                delegate.urlSession(task.session, webSocketTask = task, didCloseWith = closeCode, reason = closeData)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket = webSocket, t = t, response = response)
            val userInfo: Dictionary<String, Any> = dictionaryOf(
                Tuple2(NSUnderlyingErrorKey, t.aserror()),
                Tuple2(NSLocalizedDescriptionKey, t.toString())
            )
            val urlError = URLError(URLError.Code.unknown, userInfo = userInfo)
            var httpResponse: URLResponse? = null
            if (response != null) {
                task.url.sref()?.let { url ->
                    httpResponse = httpURLResponse(from = response, with = url)
                }
            }
            task.completion(data = null, response = httpResponse, error = urlError)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket = webSocket, text = text)
            task.channel?.trySend(Message.string(text))
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket = webSocket, bytes = bytes)
            task.channel?.trySend(Message.data(Data(platformValue = bytes.toByteArray())))
        }
    }

    companion object: CompanionClass() {

        override fun CloseCode(rawValue: Int): URLSessionWebSocketTask.CloseCode? {
            return when (rawValue) {
                0 -> CloseCode.invalid
                1000 -> CloseCode.normalClosure
                1001 -> CloseCode.goingAway
                1002 -> CloseCode.protocolError
                1003 -> CloseCode.unsupportedData
                1005 -> CloseCode.noStatusReceived
                1006 -> CloseCode.abnormalClosure
                1007 -> CloseCode.invalidFramePayloadData
                1008 -> CloseCode.policyViolation
                1009 -> CloseCode.messageTooBig
                1010 -> CloseCode.mandatoryExtensionMissing
                1011 -> CloseCode.internalServerError
                1015 -> CloseCode.tlsHandshakeFailure
                else -> null
            }
        }
    }
    open class CompanionClass: URLSessionTask.CompanionClass() {
        open fun CloseCode(rawValue: Int): URLSessionWebSocketTask.CloseCode? = URLSessionWebSocketTask.CloseCode(rawValue = rawValue)
    }
}

interface URLSessionWebSocketDelegate: URLSessionTaskDelegate {
    fun urlSession(session: URLSession, webSocketTask: URLSessionWebSocketTask, didOpenWithProtocol: String?) = Unit

    fun urlSession(session: URLSession, webSocketTask: URLSessionWebSocketTask, didCloseWith: URLSessionWebSocketTask.CloseCode, reason: Data?) = Unit
}

open class URLSessionStreamTask: URLSessionTask {
    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun readData(ofMinLength: Int, maxLength: Int, timeout: Double, completionHandler: (Data?, Boolean, Error?) -> Unit) {
        val minBytes = ofMinLength
        val maxBytes = maxLength
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun write(data: Data, timeout: Double, completionHandler: (Error?) -> Unit) {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun captureStreams() {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun closeWrite() {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun closeRead() {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun startSecureConnection() {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun stopSecureConnection() {
        fatalError()
    }

    constructor(session: URLSession, request: URLRequest, taskIdentifier: Int, build: (Request.Builder) -> Unit = { it ->  }, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): super(session, request, taskIdentifier, build, completionHandler) {
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass: URLSessionTask.CompanionClass() {
    }
}

val URLSessionDownloadTaskResumeData: String = "NSURLSessionDownloadTaskResumeData"

interface URLSessionTaskDelegate: URLSessionDelegate {
    fun urlSession(session: URLSession, didCreateTask: URLSessionTask) = Unit

    fun urlSession(session: URLSession, task: URLSessionTask, willPerformHTTPRedirection: HTTPURLResponse, newRequest: URLRequest, completionHandler: (URLRequest?) -> Unit) = Unit

    fun urlSession(session: URLSession, task: URLSessionTask, didReceive: URLAuthenticationChallenge, completionHandler: (URLSession.AuthChallengeDisposition, URLCredential?) -> Unit) = Unit

    //    public func urlSession(_ session: URLSession, task: URLSessionTask, needNewBodyStream completionHandler: @escaping (InputStream?) -> Void) {
    //    }

    fun urlSession(session: URLSession, task: URLSessionTask, didSendBodyData: Long, totalBytesSent: Long, totalBytesExpectedToSend: Long) = Unit

    fun urlSession(session: URLSession, task: URLSessionTask, didCompleteWithError: Error?) = Unit

    fun urlSession(session: URLSession, task: URLSessionTask, willBeginDelayedRequest: URLRequest, completionHandler: (URLSession.DelayedRequestDisposition, URLRequest?) -> Unit) = Unit

    fun urlSession(session: URLSession, task: URLSessionTask, didFinishCollecting: URLSessionTaskMetrics) = Unit

    fun urlSession(session: URLSession, task: URLSessionTask, didReceiveInformationalResponse: HTTPURLResponse) = Unit
}

interface URLSessionDataDelegate: URLSessionTaskDelegate {
    fun urlSession(session: URLSession, dataTask: URLSessionDataTask, didReceive: URLResponse, completionHandler: (URLSession.ResponseDisposition) -> Unit) = Unit

    fun urlSession(session: URLSession, dataTask: URLSessionDataTask, didBecome: URLSessionDownloadTask) = Unit

    fun urlSession(session: URLSession, dataTask: URLSessionDataTask, didBecome: URLSessionStreamTask) = Unit

    fun urlSession(session: URLSession, dataTask: URLSessionDataTask, didReceive: Data) = Unit

    fun urlSession(session: URLSession, dataTask: URLSessionDataTask, willCacheResponse: CachedURLResponse, completionHandler: (CachedURLResponse?) -> Unit) = Unit
}

interface URLSessionDownloadDelegate: URLSessionTaskDelegate {
    fun urlSession(session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo: URL)
    fun urlSession(session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData: Long, totalBytesWritten: Long, totalBytesExpectedToWrite: Long) = Unit

    fun urlSession(session: URLSession, downloadTask: URLSessionDownloadTask, didResumeAtOffset: Long, expectedTotalBytes: Long) = Unit
}

interface URLSessionStreamDelegate: URLSessionTaskDelegate {

    //    public func urlSession(_ session: URLSession, readClosedFor streamTask: URLSessionStreamTask) {
    //    }
    //
    //    public func urlSession(_ session: URLSession, writeClosedFor streamTask: URLSessionStreamTask) {
    //    }
    //
    //    public func urlSession(_ session: URLSession, betterRouteDiscoveredFor streamTask: URLSessionStreamTask) {
    //    }
    //
    //    public func urlSession(_ session: URLSession, streamTask: URLSessionStreamTask, didBecome inputStream: InputStream, outputStream: OutputStream) {
    //    }
}

// Stubs
class URLSessionTaskMetrics {

    companion object {
    }
}

private enum class RequestType {
    generic,
    http;
}

private fun RequestType(request: URLRequest): RequestType {
    val url_1 = request.url.sref()
    if (url_1 == null) {
        return RequestType.generic
    }
    when (url_1.scheme?.lowercased()) {
        "http", "https", "ws", "wss" -> return RequestType.http
        else -> return RequestType.generic
    }
}

private val httpClient: OkHttpClient = linvoke l@{ ->
    val builder = OkHttpClient.Builder()
        .callTimeout(Long(URLSessionConfiguration.default.timeoutIntervalForRequest * 1000), TimeUnit.MILLISECONDS)
        .readTimeout(Long(URLSessionConfiguration.default.timeoutIntervalForResource * 1000), TimeUnit.MILLISECONDS)
    try {
        builder.cache(Cache(java.io.File(ProcessInfo.processInfo.androidContext.cacheDir, "http_cache"), 5 * 1024 * 1024))
    } catch (error: Throwable) {
        @Suppress("NAME_SHADOWING") val error = error.aserror()
        // Can't access ProcessInfo in testing environments
    }
    return@l builder.build()
}

/// Use for HTTP requests.
private fun httpRequest(for_: URLRequest, with: URL, configuration: URLSessionConfiguration, build: (Request.Builder) -> Unit = { it ->  }): Tuple2<OkHttpClient, Request> {
    val request = for_
    val url = with
    val requestTimeout = if (request.timeoutInterval > 0.0) request.timeoutInterval else configuration.timeoutIntervalForRequest
    val resourceTimeout = configuration.timeoutIntervalForResource
    val client: OkHttpClient
    if (requestTimeout != URLSessionConfiguration.default.timeoutIntervalForRequest || resourceTimeout != URLSessionConfiguration.default.timeoutIntervalForResource) {
        client = httpClient.newBuilder()
            .callTimeout(Long(requestTimeout * 1000), TimeUnit.MILLISECONDS)
            .readTimeout(Long(resourceTimeout * 1000), TimeUnit.MILLISECONDS)
            .build()
    } else {
        client = httpClient.sref()
    }

    val builder = Request.Builder()
        .url(url.platformValue)
        .method(request.httpMethod ?: "GET", request.httpBody?.platformValue?.toRequestBody())
    (request.allHTTPHeaderFields?.kotlin(nocopy = true) as? Map<String, String>).sref()?.let { headerMap ->
        builder.headers(headerMap.toHeaders())
    }
    for (unusedi in 0..0) {
        when (request.cachePolicy) {
            URLRequest.CachePolicy.useProtocolCachePolicy -> break
            URLRequest.CachePolicy.returnCacheDataElseLoad -> builder.header("Cache-Control", "max-stale=31536000") // One year
            URLRequest.CachePolicy.returnCacheDataDontLoad -> builder.cacheControl(CacheControl.FORCE_CACHE)
            URLRequest.CachePolicy.reloadRevalidatingCacheData -> builder.header("Cache-Control", "no-cache, must-revalidate")
            URLRequest.CachePolicy.reloadIgnoringLocalCacheData -> builder.cacheControl(CacheControl.FORCE_NETWORK)
            URLRequest.CachePolicy.reloadIgnoringLocalAndRemoteCacheData -> builder.cacheControl(CacheControl.FORCE_NETWORK)
        }
    }

    build(builder)
    return Tuple2(client.sref(), builder.build())
}

private fun httpURLResponse(from: Response, with: URL): HTTPURLResponse {
    val response = from
    val url = with
    val statusCode = response.code.sref()
    val httpVersion = response.protocol.toString()
    val headerDictionary = Dictionary(response.headers.toMap(), nocopy = true)
    return HTTPURLResponse(url = url, statusCode = statusCode, httpVersion = httpVersion, headerFields = headerDictionary)
}

/// Use for non-HTTP requests.
private fun genericConnection(for_: URLRequest, with: URL): java.net.URLConnection {
    val request = for_
    val url = with
    // Calling openConnection does not actually connect
    val connection = url.platformValue.openConnection()
    when (request.cachePolicy) {
        URLRequest.CachePolicy.useProtocolCachePolicy -> connection.setUseCaches(true)
        URLRequest.CachePolicy.returnCacheDataElseLoad -> connection.setUseCaches(true)
        URLRequest.CachePolicy.returnCacheDataDontLoad -> connection.setUseCaches(true)
        URLRequest.CachePolicy.reloadRevalidatingCacheData -> connection.setUseCaches(true)
        URLRequest.CachePolicy.reloadIgnoringLocalCacheData -> connection.setUseCaches(false)
        URLRequest.CachePolicy.reloadIgnoringLocalAndRemoteCacheData -> connection.setUseCaches(false)
    }
    return connection.sref()
}

private suspend fun genericResponse(for_: URLRequest, with: URL, isForResponse: Boolean): Tuple3<Data?, URLResponse, java.net.URLConnection?> {
    val request = for_
    val url = with
    val job = Job()
    return withContext(job + Dispatchers.IO) l@{ ->
        val connection = genericConnection(for_ = request, with = url)
        val response = URLResponse(url = url, mimeType = null, expectedContentLength = -1, textEncodingName = null)
        if (isForResponse) {
            return@l Tuple3(null, response, connection.sref())
        }
        var inputStream: java.io.InputStream? = null
        return@l withTaskCancellationHandler(operation = { -> Async.run l@{
            inputStream = connection.getInputStream()
            val outputStream = java.io.ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            inputStream.sref()?.let { stableInputStream ->
                var bytesRead: Int
                while ((stableInputStream.read(buffer).also { it -> bytesRead = it } != -1)) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
            try {
                inputStream?.close()
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
            }
            val bytes = outputStream.toByteArray()
            return@l Tuple3(Data(platformValue = bytes), response, null)
        } }, onCancel = { ->
            try {
                inputStream?.close()
            } catch (error: Throwable) {
                @Suppress("NAME_SHADOWING") val error = error.aserror()
            }
            job.cancel()
        })
    }
}

