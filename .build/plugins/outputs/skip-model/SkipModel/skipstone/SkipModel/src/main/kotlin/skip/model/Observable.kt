// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.model

import skip.lib.*


/// Kotlin representation of `Observation.Observable`.
interface Observable {
}

/// Kotlin representation of `Combine.ObservableObject`.
interface ObservableObject {
    val objectWillChange: ObservableObjectPublisher
}

