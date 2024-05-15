// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

enum class BlendMode: Sendable {
    normal,
    multiply,
    screen,
    overlay,
    darken,
    lighten,
    colorDodge,
    colorBurn,
    softLight,
    hardLight,
    difference,
    exclusion,
    hue,
    saturation,
    color,
    luminosity,
    sourceAtop,
    destinationOver,
    destinationOut,
    plusDarker,
    plusLighter;

    companion object {
    }
}
