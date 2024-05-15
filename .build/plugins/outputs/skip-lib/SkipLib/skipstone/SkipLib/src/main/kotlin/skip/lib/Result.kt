// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.lib


/// Kotlin representation of `Swift.Result`.
sealed class Result<out Success, out Failure> where Failure: Error {
    class SuccessCase<Success>(val associated0: Success): Result<Success, Nothing>() {
    }
    class FailureCase<Failure>(val associated0: Failure): Result<Nothing, Failure>() where Failure: Error {
    }

    fun get(): Success {
        when (this) {
            is Result.SuccessCase -> {
                val success = this.associated0
                return success.sref()
            }
            is Result.FailureCase -> {
                val failure = this.associated0
                throw failure as Throwable
            }
        }
    }

    companion object {
        fun <Success> success(associated0: Success): Result<Success, Nothing> = SuccessCase(associated0)
        fun <Failure> failure(associated0: Failure): Result<Nothing, Failure> where Failure: Error = FailureCase(associated0)
    }
}

fun <Success, Failure> Result(catching: () -> Success): Result<Success, Failure> where Failure: Error {
    val body = catching
    try {
        return Result.success(body())
    } catch (error: Throwable) {
        @Suppress("NAME_SHADOWING") val error = error.aserror()
        return Result.failure(error as Failure)
    }
}

