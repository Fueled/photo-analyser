// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*
import skip.lib.Array

import kotlinx.coroutines.channels.Channel
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class URLSession {

    val configuration: URLSessionConfiguration
    var delegate: URLSessionDelegate? = null
        get() = field.sref({ this.delegate = it })
        private set(newValue) {
            field = newValue.sref()
        }
    var delegateQueue: OperationQueue
        private set

    private val identifier: Int
    private val lock = NSRecursiveLock()
    private var nextTaskIdentifier = 0
    private var tasks: Dictionary<Int, URLSessionTask> = dictionaryOf()
        get() = field.sref({ this.tasks = it })
        set(newValue) {
            field = newValue.sref()
        }
    private var invalidateOnCompletion = false

    constructor(configuration: URLSessionConfiguration, delegate: URLSessionDelegate? = null, delegateQueue: OperationQueue? = null): this(configuration = configuration, delegate = delegate, delegateQueue = delegateQueue, isShared = false) {
    }

    private constructor(configuration: URLSessionConfiguration, delegate: URLSessionDelegate?, delegateQueue: OperationQueue?, isShared: Boolean) {
        this.configuration = configuration
        this.delegate = delegate
        this.delegateQueue = delegateQueue ?: OperationQueue()
        var identifier = -1
        if (!isShared) {
            Companion.sessionsLock.withLock { ->
                identifier = Companion.nextSessionIdentifier
                Companion.nextSessionIdentifier += 1
                Companion.sessions[identifier] = this
            }
        }
        this.identifier = identifier
    }

    var sessionDescription: String? = null

    suspend fun data(for_: URLRequest, delegate: URLSessionTaskDelegate? = null): Tuple2<Data, URLResponse> {
        val request = for_
        return runDataTask(delegate = delegate, factory = l@{ completionHandler -> return@l dataTask(with = request, completionHandler = completionHandler) })
    }

    private suspend fun runDataTask(delegate: URLSessionTaskDelegate?, factory: ((Data?, URLResponse?, Error?) -> Unit) -> URLSessionTask): Tuple2<Data, URLResponse> = Async.run l@{
        val channel = Channel<Tuple3<Data?, URLResponse?, Error?>>(1)
        val task = factory { data, response, error -> channel.trySend(Tuple3(data.sref(), response, error.sref())) }
        task.delegate = delegate
        task.resume()
        val (data, response, error) = channel.receive()
        channel.close()
        if (error != null) {
            throw error as Throwable
        } else if ((data != null) && (response != null)) {
            return@l Tuple2(data.sref(), response)
        } else {
            throw URLError(URLError.Code.unknown)
        }
    }

    suspend fun data(from: URL, delegate: URLSessionTaskDelegate? = null): Tuple2<Data, URLResponse> {
        val url = from
        return this.data(for_ = URLRequest(url = url), delegate = delegate)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    suspend fun download(for_: URLRequest, delegate: URLSessionTaskDelegate? = null): Tuple2<URL, URLResponse> = Async.run {
        val request = for_
        // NOTE: Partial implementation was here prior to 4/7/2024. See git history to revive
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    suspend fun download(from: URL, delegate: URLSessionTaskDelegate? = null): Tuple2<URL, URLResponse> = Async.run {
        val url = from
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal fun download(resumeFrom: Data, delegate: URLSessionTaskDelegate? = null): Tuple2<URL, URLResponse> {
        fatalError()
    }

    suspend fun upload(for_: URLRequest, fromFile: URL, delegate: URLSessionTaskDelegate? = null): Tuple2<Data, URLResponse> {
        val request = for_
        val fileURL = fromFile
        return runDataTask(delegate = delegate, factory = l@{ completionHandler -> return@l uploadTask(with = request, fromFile = fromFile, completionHandler = completionHandler) })
    }

    suspend fun upload(for_: URLRequest, from: Data, delegate: URLSessionTaskDelegate? = null): Tuple2<Data, URLResponse> {
        val request = for_
        val bodyData = from
        return runDataTask(delegate = delegate, factory = l@{ completionHandler -> return@l uploadTask(with = request, from = bodyData, completionHandler = completionHandler) })
    }

    suspend fun bytes(for_: URLRequest, delegate: URLSessionTaskDelegate? = null): Tuple2<URLSession.AsyncBytes, URLResponse> {
        val request = for_
        val channel = Channel<Tuple2<URLResponse?, Error?>>(1)
        val task = dataTask(with = request) { _, response, error -> channel.trySend(Tuple2(response, error.sref())) }
        task.delegate = delegate
        task.isForResponse = true
        task.resume()
        val (response, error) = channel.receive()
        channel.close()
        if (error != null) {
            throw error as Throwable
        } else {
            if (response != null) {
                val matchtarget_0 = task.genericConnection
                if (matchtarget_0 != null) {
                    val genericConnection = matchtarget_0
                    val inputStream = genericConnection.getInputStream()
                    val asyncBytes = AsyncBytes(inputStream = inputStream, onClose = { ->
                        try {
                            inputStream?.close()
                        } catch (error: Throwable) {
                            @Suppress("NAME_SHADOWING") val error = error.aserror()
                        }
                    })
                    return Tuple2(asyncBytes.sref(), response)
                } else {
                    if (response != null) {
                        val matchtarget_1 = task.httpResponse
                        if (matchtarget_1 != null) {
                            val httpResponse = matchtarget_1
                            val inputStream = httpResponse.body?.byteStream()
                            val asyncBytes = URLSession.AsyncBytes(inputStream = inputStream, onClose = { ->
                                try {
                                    httpResponse.close()
                                } catch (error: Throwable) {
                                    @Suppress("NAME_SHADOWING") val error = error.aserror()
                                }
                            })
                            return Tuple2(asyncBytes.sref(), response)
                        } else {
                            throw URLError(URLError.Code.unknown)
                        }
                    } else {
                        throw URLError(URLError.Code.unknown)
                    }
                }
            } else {
                if (response != null) {
                    val matchtarget_1 = task.httpResponse
                    if (matchtarget_1 != null) {
                        val httpResponse = matchtarget_1
                        val inputStream = httpResponse.body?.byteStream()
                        val asyncBytes = URLSession.AsyncBytes(inputStream = inputStream, onClose = { ->
                            try {
                                httpResponse.close()
                            } catch (error: Throwable) {
                                @Suppress("NAME_SHADOWING") val error = error.aserror()
                            }
                        })
                        return Tuple2(asyncBytes.sref(), response)
                    } else {
                        throw URLError(URLError.Code.unknown)
                    }
                } else {
                    throw URLError(URLError.Code.unknown)
                }
            }
        }
    }

    suspend fun bytes(from: URL, delegate: URLSessionTaskDelegate? = null): Tuple2<URLSession.AsyncBytes, URLResponse> {
        val url = from
        return bytes(for_ = URLRequest(url = url), delegate = delegate)
    }

    class AsyncBytes: AsyncSequence<UByte>, MutableStruct {

        internal var inputStream: java.io.InputStream? = null
            get() = field.sref({ this.inputStream = it })
            set(newValue) {
                @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
                willmutate()
                field = newValue
                didmutate()
            }
        internal var onClose: (() -> Unit)? = null
            set(newValue) {
                willmutate()
                field = newValue
                didmutate()
            }

        internal constructor(inputStream: java.io.InputStream?, onClose: (() -> Unit)? = null) {
            this.inputStream = inputStream
            this.onClose = onClose
        }

        fun finalize() {
            onClose?.let { onClose ->
                onClose()
            }
        }

        override fun makeAsyncIterator(): URLSession.AsyncBytes.Iterator = Iterator(bytes = this)

        internal fun close() {
            onClose?.let { onClose ->
                onClose()
                this.inputStream = null
                this.onClose = null
            }
        }

        class Iterator: AsyncIteratorProtocol<UByte> {
            private val bytes: URLSession.AsyncBytes

            internal constructor(bytes: URLSession.AsyncBytes) {
                this.bytes = bytes.sref()
            }

            override suspend fun next(): UByte? = Async.run l@{
                val byte_0 = try { bytes.inputStream?.read() } catch (_: Throwable) { null }
                if ((byte_0 == null) || (byte_0 == -1)) {
                    bytes.close()
                    return@l null
                }
                return@l UByte(byte_0)
            }

            companion object {
            }
        }

        private constructor(copy: MutableStruct) {
            @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as URLSession.AsyncBytes
            this.inputStream = copy.inputStream
            this.onClose = copy.onClose
        }

        override var supdate: ((Any) -> Unit)? = null
        override var smutatingcount = 0
        override fun scopy(): MutableStruct = URLSession.AsyncBytes(this as MutableStruct)

        companion object {
        }
    }

    fun dataTask(with: URL, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): URLSessionDataTask {
        val url = with
        return dataTask(with = URLRequest(url = url), completionHandler = completionHandler)
    }

    fun dataTask(with: URLRequest, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): URLSessionDataTask {
        val request = with
        val task = lock.withLock l@{ ->
            val identifier = nextTaskIdentifier
            nextTaskIdentifier += 1
            val task = URLSessionDataTask(session = this, request = request, taskIdentifier = identifier, completionHandler = completionHandler)
            tasks[task.taskIdentifier] = task
            return@l task
        }
        taskDidCreate(task)
        return task
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun downloadTask(with: URL): URLSessionDownloadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun downloadTask(with: URL, completionHandler: (URL?, URLResponse?, Error?) -> Unit): URLSessionDownloadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun downloadTask(with: URLRequest): URLSessionDownloadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun downloadTask(with: URLRequest, completionHandler: (URL?, URLResponse?, Error?) -> Unit): URLSessionDownloadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun downloadTask(withResumeData: Data): URLSessionDownloadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun downloadTask(withResumeData: Data, completionHandler: (URL?, URLResponse?, Error?) -> Unit): URLSessionDownloadTask {
        fatalError()
    }

    fun uploadTask(with: URLRequest, from: Data?, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)? = null): URLSessionUploadTask {
        val request = with
        val bodyData = from
        val task = lock.withLock l@{ ->
            val identifier = nextTaskIdentifier
            nextTaskIdentifier += 1
            val task = URLSessionUploadTask(session = this, request = request, taskIdentifier = identifier, build = { it ->
                if (bodyData != null) {
                    it.post(bodyData.platformValue.toRequestBody())
                }
            }, completionHandler = completionHandler)
            tasks[task.taskIdentifier] = task
            return@l task
        }
        taskDidCreate(task)
        return task
    }

    fun uploadTask(with: URLRequest, fromFile: URL, completionHandler: ((Data?, URLResponse?, Error?) -> Unit)?): URLSessionUploadTask {
        val request = with
        val url = fromFile
        val file = java.io.File(url.platformValue.toURI())
        val task = lock.withLock l@{ ->
            val identifier = nextTaskIdentifier
            nextTaskIdentifier += 1
            val task = URLSessionUploadTask(session = this, request = request, taskIdentifier = identifier, build = { it -> it.post(file.asRequestBody()) }, completionHandler = completionHandler)
            tasks[task.taskIdentifier] = task
            return@l task
        }
        taskDidCreate(task)
        return task
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun uploadTask(withStreamedRequest: URLRequest): URLSessionUploadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal fun uploadTask(withResumeData: Data): URLSessionUploadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    internal fun uploadTask(withResumeData: Data, completionHandler: (Data?, URLResponse?, Error?) -> Unit): URLSessionUploadTask {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun streamTask(withHostName: String, port: Int): URLSessionStreamTask {
        fatalError()
    }

    fun webSocketTask(with: URL): URLSessionWebSocketTask {
        val url = with
        return webSocketTask(with = URLRequest(url = url))
    }

    fun webSocketTask(with: URLRequest): URLSessionWebSocketTask {
        val request = with
        val task = lock.withLock l@{ ->
            val identifier = nextTaskIdentifier
            nextTaskIdentifier += 1
            val task = URLSessionWebSocketTask(session = this, request = request, taskIdentifier = identifier)
            tasks[task.taskIdentifier] = task
            return@l task
        }
        taskDidCreate(task)
        return task
    }

    fun webSocketTask(with: URL, protocols: Array<String>): URLSessionWebSocketTask {
        val url = with
        var request = URLRequest(url = url)
        request.setValue(protocols.joined(separator = ", "), forHTTPHeaderField = "Sec-WebSocket-Protocol")
        return webSocketTask(with = request)
    }

    /// We call this after task creation to inform our delegate.
    private fun taskDidCreate(task: URLSessionTask) {
        (delegate as? URLSessionTaskDelegate).sref()?.let { taskDelegate ->
            taskDelegate.urlSession(this, didCreateTask = task)
        }
    }

    /// Called by tasks to remove them from the session.
    internal fun taskDidComplete(task: URLSessionTask) {
        lock.withLock { ->
            tasks[task.taskIdentifier] = null
            if (tasks.isEmpty && invalidateOnCompletion) {
                invalidate()
            }
        }
    }

    /// Invalidate this session. Called with lock.
    private fun invalidate() {
        Companion.sessionsLock.withLock { -> Companion.sessions[identifier] = null }
        delegate.sref()?.let { delegate ->
            delegateQueue.runBlock { -> delegate.urlSession(this, didBecomeInvalidWithError = null) }
        }
    }

    fun finishTasksAndInvalidate() {
        if (identifier < 0) {
            return
        }
        lock.withLock { ->
            if (tasks.isEmpty) {
                invalidate()
            } else {
                invalidateOnCompletion = true
            }
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun flush(completionHandler: () -> Unit) = Unit

    fun getTasksWithCompletionHandler(handler: (Array<URLSessionDataTask>, Array<URLSessionUploadTask>, Array<URLSessionDownloadTask>) -> Unit) {
        val (dataTasks, uploadTasks, downloadTasks) = tasksByType.sref()
        handler(dataTasks, uploadTasks, downloadTasks)
    }

    suspend fun tasks(): Tuple3<Array<URLSessionDataTask>, Array<URLSessionUploadTask>, Array<URLSessionDownloadTask>> = Async.run l@{
        return@l tasksByType
    }

    private val tasksByType: Tuple3<Array<URLSessionDataTask>, Array<URLSessionUploadTask>, Array<URLSessionDownloadTask>>
        get() {
            return lock.withLock l@{ ->
                var dataTasks: Array<URLSessionDataTask> = arrayOf()
                var uploadTasks: Array<URLSessionUploadTask> = arrayOf()
                var downloadTasks: Array<URLSessionDownloadTask> = arrayOf()
                for (task in tasks.values.sref()) {
                    val matchtarget_2 = task as? URLSessionDataTask
                    if (matchtarget_2 != null) {
                        val dataTask = matchtarget_2
                        dataTasks.append(dataTask)
                    } else {
                        val matchtarget_3 = task as? URLSessionUploadTask
                        if (matchtarget_3 != null) {
                            val uploadTask = matchtarget_3
                            uploadTasks.append(uploadTask)
                        } else {
                            (task as? URLSessionDownloadTask)?.let { downloadTask ->
                                downloadTasks.append(downloadTask)
                            }
                        }
                    }
                }
                return@l Tuple3(dataTasks.sref(), uploadTasks.sref(), downloadTasks.sref())
            }
        }

    fun getAllTasks(handler: (Array<URLSessionTask>) -> Unit) {
        val allTasks = lock.withLock { -> Array(tasks.values) }
        handler(allTasks)
    }

    suspend fun allTasks(): Array<URLSessionTask> = Async.run l@{
        return@l lock.withLock { -> Array(tasks.values) }
    }

    fun invalidateAndCancel() {
        if (identifier < 0) {
            return
        }
        lock.withLock { ->
            if (tasks.isEmpty) {
                invalidate()
            } else {
                invalidateOnCompletion = true
                for (task in tasks.values.sref()) {
                    try {
                        task.cancel()
                    } catch (error: Throwable) {
                        @Suppress("NAME_SHADOWING") val error = error.aserror()
                    }
                }
            }
        }
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun reset(completionHandler: () -> Unit) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dataTaskPublisher(for_: URLRequest): Any {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun dataTaskPublisher(for_: URL): Any {
        fatalError()
    }

    enum class DelayedRequestDisposition(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        continueLoading(0),
        useNewRequest(1),
        cancel(2);

        companion object {
        }
    }

    enum class AuthChallengeDisposition(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        useCredential(0),
        performDefaultHandling(1),
        cancelAuthenticationChallenge(2),
        rejectProtectionSpace(3);

        companion object {
        }
    }

    enum class ResponseDisposition(override val rawValue: Int, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<Int> {
        cancel(0),
        allow(1),
        becomeDownload(2),
        becomeStream(3);

        companion object {
        }
    }

    companion object {
        val shared = URLSession(configuration = URLSessionConfiguration.default, delegate = null, delegateQueue = null, isShared = true)

        private var sessions: Dictionary<Int, URLSession> = dictionaryOf()
            get() = field.sref({ this.sessions = it })
            set(newValue) {
                field = newValue.sref()
            }
        private var nextSessionIdentifier = 0
        private val sessionsLock = NSRecursiveLock()

        fun DelayedRequestDisposition(rawValue: Int): URLSession.DelayedRequestDisposition? {
            return when (rawValue) {
                0 -> DelayedRequestDisposition.continueLoading
                1 -> DelayedRequestDisposition.useNewRequest
                2 -> DelayedRequestDisposition.cancel
                else -> null
            }
        }

        fun AuthChallengeDisposition(rawValue: Int): URLSession.AuthChallengeDisposition? {
            return when (rawValue) {
                0 -> AuthChallengeDisposition.useCredential
                1 -> AuthChallengeDisposition.performDefaultHandling
                2 -> AuthChallengeDisposition.cancelAuthenticationChallenge
                3 -> AuthChallengeDisposition.rejectProtectionSpace
                else -> null
            }
        }

        fun ResponseDisposition(rawValue: Int): URLSession.ResponseDisposition? {
            return when (rawValue) {
                0 -> ResponseDisposition.cancel
                1 -> ResponseDisposition.allow
                2 -> ResponseDisposition.becomeDownload
                3 -> ResponseDisposition.becomeStream
                else -> null
            }
        }
    }
}

interface URLSessionDelegate {
    fun urlSession(session: URLSession, didBecomeInvalidWithError: Error?) = Unit

    fun urlSession(session: URLSession, didReceive: URLAuthenticationChallenge, completionHandler: (URLSession.AuthChallengeDisposition, URLCredential?) -> Unit) = Unit
}

// Stub
class URLAuthenticationChallenge {

    companion object {
    }
}
class URLCredential {

    companion object {
    }
}
class CachedURLResponse {

    companion object {
    }
}

