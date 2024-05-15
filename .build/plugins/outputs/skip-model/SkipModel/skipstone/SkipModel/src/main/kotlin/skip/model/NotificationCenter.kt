// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.model

import skip.lib.*


import skip.foundation.*

fun NotificationCenter.publisher(for_: Notification.Name, object_: Any? = null): Publisher<Notification, Never> {
    val name = for_
    val publisher = NotificationCenterPublisher(center = this)
    publisher.observer = addObserver(forName = name, object_ = object_, queue = null) { it -> publisher.send(it) }
    return publisher
}

private class NotificationCenterPublisher: Publisher<Notification, Never> {

    private val center: NotificationCenter
    private val helper: SubjectHelper<Notification, Never> = SubjectHelper<Notification, Never>()
    internal var observer: Any? = null
        get() = field.sref({ this.observer = it })
        set(newValue) {
            field = newValue.sref()
        }

    internal constructor(center: NotificationCenter) {
        this.center = center
    }

    fun finalize() {
        observer.sref()?.let { observer ->
            center.removeObserver(observer)
        }
    }

    override fun sink(receiveValue: (Notification) -> Unit): AnyCancellable {
        val internalCancellable = helper.sink(receiveValue)
        val referencingCancellable = ReferencingCancellable(publisher = this, cancellable = internalCancellable)
        return AnyCancellable(referencingCancellable)
    }

    internal fun send(notification: Notification): Unit = helper.send(notification)
}

/// Cancellable that references the producing publisher.
///
/// The publisher will deregister from the notification center only when it finalizes after all these references are gone.
private class ReferencingCancellable: Cancellable {
    private var publisher: NotificationCenterPublisher? = null
    private val cancellable: Cancellable

    internal constructor(publisher: NotificationCenterPublisher?, cancellable: Cancellable) {
        this.publisher = publisher
        this.cancellable = cancellable.sref()
    }

    override fun cancel() {
        publisher = null
        cancellable.cancel()
    }
}

