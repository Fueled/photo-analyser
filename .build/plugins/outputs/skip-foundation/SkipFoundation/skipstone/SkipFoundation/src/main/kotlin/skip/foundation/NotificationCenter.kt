// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


import android.os.Looper
import java.util.LinkedHashMap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class NotificationCenter {
    private var registries: MutableMap<String?, NotificationCenter.Registry> = mutableMapOf()
        get() = field.sref({ this.registries = it })
        set(newValue) {
            field = newValue.sref()
        }

    open fun addObserver(forName: Notification.Name?, object_: Any?, queue: OperationQueue?, using: (Notification) -> Unit): Any {
        val name = forName
        val block = using
        val observer = Observer(forObject = object_, queue = queue, block = block)
        val registry: NotificationCenter.Registry
        synchronized(this) { ->
            var r = registries[name?.rawValue].sref()
            if (r == null) {
                r = Registry(name = name?.rawValue)
                registries[name?.rawValue] = r.sref()
            }
            registry = r!!
        }
        return registry.register(observer)
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun addObserver(observer: Any, selector: Any, name: Notification.Name?, object_: Any?) = Unit

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    open fun removeObserver(observer: Any, name: Notification.Name?, object_: Any?) = Unit

    open fun removeObserver(observer: Any) {
        val observerId_0 = observer as? NotificationCenter.ObserverId
        if (observerId_0 == null) {
            return
        }
        val registry: NotificationCenter.Registry?
        synchronized(this) { -> registry = registries[observerId_0.name] }
        registry?.unregister(observerId_0)
    }

    open fun post(notification: Notification) {
        val allRegistry: NotificationCenter.Registry?
        val registry: NotificationCenter.Registry?
        synchronized(this) { ->
            allRegistry = registries[null]
            registry = registries[notification.name.rawValue]
        }
        allRegistry?.post(notification)
        registry?.post(notification)
    }

    open fun post(name: Notification.Name, object_: Any?, userInfo: Dictionary<AnyHashable, Any>? = null): Unit = post(Notification(name = name, object_ = object_, userInfo = userInfo))

    open fun notifications(named: Notification.Name, object_: Any? = null): NotificationCenter.Notifications {
        val (stream, continuation) = AsyncStream.makeStream(of = Notification::class)
        val token = addObserver(forName = named, object_ = object_, queue = null) { notification -> continuation.yield(notification) }
        return Notifications(center = this, stream = stream, token = token)
    }

    class Notifications: AsyncSequence<Notification> {

        internal val center: NotificationCenter
        internal val stream: AsyncStream<Notification>
        internal val token: Any

        internal constructor(center: NotificationCenter, stream: AsyncStream<Notification>, token: Any) {
            this.center = center
            this.stream = stream
            this.token = token.sref()
        }

        fun finalize(): Unit = center.removeObserver(token)

        override fun makeAsyncIterator(): NotificationCenter.Notifications.Iterator = Iterator(notifications = this)

        class Iterator: AsyncIteratorProtocol<Notification> {
            // Keep a reference to the owning Notifications to prevent it from GC during iteration, because it unregisters on GC
            private val notifications: NotificationCenter.Notifications
            private val iterator: AsyncStream.Iterator<Notification>

            internal constructor(notifications: NotificationCenter.Notifications) {
                this.notifications = notifications
                this.iterator = notifications.stream.makeAsyncIterator()
            }

            override suspend fun next(): Notification? = Async.run l@{
                return@l iterator.next()
            }

            companion object {
            }
        }

        companion object {
        }
    }

    private class Observer {
        internal val forObject: Any?
        internal val queue: OperationQueue?
        internal val block: (Notification) -> Unit

        constructor(forObject: Any? = null, queue: OperationQueue? = null, block: (Notification) -> Unit) {
            this.forObject = forObject.sref()
            this.queue = queue
            this.block = block
        }
    }

    private class ObserverId {
        internal val name: String?
        internal val id: Int

        constructor(name: String? = null, id: Int) {
            this.name = name
            this.id = id
        }

        override fun equals(other: Any?): Boolean {
            if (other !is NotificationCenter.ObserverId) return false
            return name == other.name && id == other.id
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, name)
            result = Hasher.combine(result, id)
            return result
        }
    }

    private class Registry {
        internal val name: String?
        internal val observers: LinkedHashMap<Int, NotificationCenter.Observer> = LinkedHashMap<Int, NotificationCenter.Observer>()
        internal var nextId = 0

        internal constructor(name: String?) {
            this.name = name
        }

        internal fun register(observer: NotificationCenter.Observer): NotificationCenter.ObserverId {
            val id: Int
            synchronized(this) { ->
                id = nextId
                nextId += 1
                observers[id] = observer
            }
            return ObserverId(name = name, id = id)
        }

        internal fun unregister(observerId: NotificationCenter.ObserverId) {
            synchronized(this) { -> observers.remove(observerId.id) }
        }

        @OptIn(DelicateCoroutinesApi::class)
        internal fun post(notification: Notification) {
            val matches: List<NotificationCenter.Observer>
            synchronized(this) { ->
                matches = observers.values.mapNotNull l@{ observer ->
                    if (observer.forObject != null && observer.forObject != notification.object_) {
                        return@l null
                    }
                    return@l observer
                }
            }
            for (match in matches.sref()) {
                if (match.queue == OperationQueue.main && Looper.myLooper() != Looper.getMainLooper()) {
                    GlobalScope.launch { ->
                        withContext(Dispatchers.Main) { -> match.block(notification) }
                    }
                } else {
                    match.block(notification)
                }
            }
        }
    }

    companion object: CompanionClass() {

        override val default: NotificationCenter = NotificationCenter()
    }
    open class CompanionClass {
        open val default: NotificationCenter
            get() = NotificationCenter.default
    }
}

class Notification: MutableStruct {
    var name: Notification.Name
        set(newValue) {
            willmutate()
            field = newValue
            didmutate()
        }
    var object_: Any? = null
        get() = field.sref({ this.object_ = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }
    var userInfo: Dictionary<AnyHashable, Any>? = null
        get() = field.sref({ this.userInfo = it })
        set(newValue) {
            @Suppress("NAME_SHADOWING") val newValue = newValue.sref()
            willmutate()
            field = newValue
            didmutate()
        }

    constructor(name: Notification.Name, object_: Any? = null, userInfo: Dictionary<AnyHashable, Any>? = null) {
        this.name = name
        this.object_ = object_
        this.userInfo = userInfo
    }

    class Name: RawRepresentable<String> {
        override val rawValue: String

        constructor(rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp_0: Nothing? = null) {
            this.rawValue = rawValue
        }

        constructor(value: String) {
            this.rawValue = value
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Notification.Name) return false
            return rawValue == other.rawValue
        }

        override fun hashCode(): Int {
            var result = 1
            result = Hasher.combine(result, rawValue)
            return result
        }

        companion object {
        }
    }

    private constructor(copy: MutableStruct) {
        @Suppress("NAME_SHADOWING", "UNCHECKED_CAST") val copy = copy as Notification
        this.name = copy.name
        this.object_ = copy.object_
        this.userInfo = copy.userInfo
    }

    override var supdate: ((Any) -> Unit)? = null
    override var smutatingcount = 0
    override fun scopy(): MutableStruct = Notification(this as MutableStruct)

    override fun equals(other: Any?): Boolean {
        if (other !is Notification) return false
        return name == other.name && object_ == other.object_ && userInfo == other.userInfo
    }

    override fun hashCode(): Int {
        var result = 1
        result = Hasher.combine(result, name)
        result = Hasher.combine(result, object_)
        result = Hasher.combine(result, userInfo)
        return result
    }

    companion object {
    }
}

