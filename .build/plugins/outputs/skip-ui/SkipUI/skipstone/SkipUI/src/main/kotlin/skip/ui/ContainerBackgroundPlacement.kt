// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

class ContainerBackgroundPlacement: Sendable {
    override fun equals(other: Any?): Boolean = other is ContainerBackgroundPlacement

    override fun hashCode(): Int = "ContainerBackgroundPlacement".hashCode()

    companion object {
    }
}
