// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.ui

import skip.lib.*

import androidx.compose.runtime.Composable

/// Used by the transpiler in place of SkipLib's standard `linvoke` when dealing with Composable code.
@Composable
fun <R> linvokeComposable(l: @Composable () -> R): R = l()
