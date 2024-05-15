// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density

class GeometryProxy {
    internal val globalFramePx: Rect
    internal val density: Density

    val size: CGSize
        get() {
            return with(density) { -> CGSize(width = Double(globalFramePx.width.toDp().value), height = Double(globalFramePx.height.toDp().value)) }
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    operator fun <T> get(anchor: Any): T {
        fatalError()
    }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    val safeAreaInsets: EdgeInsets
        get() {
            fatalError()
        }

    @Deprecated("This API is not yet available in Skip. Consider placing it within a #if !SKIP block. You can file an issue against the owning library at https://github.com/skiptools, or see the library README for information on adding support", level = DeprecationLevel.ERROR)
    fun bounds(of: NamedCoordinateSpace): CGRect? {
        val coordinateSpace = of
        fatalError()
    }

    fun frame(in_: CoordinateSpaceProtocol): CGRect {
        val coordinateSpace = in_
        if (coordinateSpace.coordinateSpace.isGlobal) {
            return with(density) { -> CGRect(x = Double(globalFramePx.left.toDp().value), y = Double(globalFramePx.top.toDp().value), width = Double(globalFramePx.width.toDp().value), height = Double(globalFramePx.height.toDp().value)) }
        } else {
            return CGRect(origin = CGPoint.zero, size = size)
        }
    }

    internal constructor(globalFramePx: Rect, density: Density) {
        this.globalFramePx = globalFramePx.sref()
        this.density = density.sref()
    }

    companion object {
    }
}
