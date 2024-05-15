// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org

package skip.foundation

import skip.lib.*


/// Skip `Logger` aliases to `SkipLogger` type and wraps `java.util.logging.Logger`
typealias Logger = SkipLogger
typealias LogMessage = String
typealias OSLogType = SkipLogger.LogType

/// Logger cover for versions before Logger was available (which coincides with Concurrency).
open class SkipLogger {
    internal val logName: String

    enum class LogType {
        default,
        info,
        debug,
        error,
        fault;

        companion object {
        }
    }

    constructor(subsystem: String, category: String) {
        this.logName = subsystem + "." + category
    }

    open fun isEnabled(type: SkipLogger.LogType): Boolean = true

    open fun log(level: SkipLogger.LogType, message: String) {
        when (level) {
            SkipLogger.LogType.default -> log(message)
            SkipLogger.LogType.info -> info(message)
            SkipLogger.LogType.debug -> debug(message)
            SkipLogger.LogType.error -> error(message)
            SkipLogger.LogType.fault -> fault(message)
            else -> log(message)
        }
    }

    open fun log(message: String) {
        try {
            android.util.Log.i(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.INFO, message)
        }
    }

    open fun trace(message: String) {
        try {
            android.util.Log.v(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.FINER, message)
        }
    }

    open fun debug(message: String) {
        try {
            android.util.Log.d(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.FINE, message)
        }
    }

    open fun info(message: String) {
        try {
            android.util.Log.i(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.INFO, message)
        }
    }

    open fun notice(message: String) {
        try {
            android.util.Log.i(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.CONFIG, message)
        }
    }

    open fun warning(message: String) {
        try {
            android.util.Log.w(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.WARNING, message)
        }
    }

    open fun error(message: String) {
        try {
            android.util.Log.e(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.SEVERE, message)
        }
    }

    open fun critical(message: String) {
        try {
            android.util.Log.wtf(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.SEVERE, message)
        }
    }

    open fun fault(message: String) {
        try {
            android.util.Log.wtf(logName, message)
        } catch (error: Throwable) {
            @Suppress("NAME_SHADOWING") val error = error.aserror()
            java.util.logging.Logger.getLogger(logName).log(java.util.logging.Level.SEVERE, message)
        }
    }

    companion object: CompanionClass() {
    }
    open class CompanionClass {
    }
}

