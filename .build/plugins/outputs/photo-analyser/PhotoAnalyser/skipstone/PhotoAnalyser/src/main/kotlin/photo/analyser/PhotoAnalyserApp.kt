package photo.analyser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import skip.lib.*

import skip.foundation.*
import skip.ui.*
import skip.model.*

internal val logger: SkipLogger = SkipLogger(subsystem = "com.fueled.PhotoAnalyser", category = "PhotoAnalyser")

/// The Android SDK number we are running against, or `nil` if not running on Android
internal val androidSDK = ProcessInfo.processInfo.environment["android.os.Build.VERSION.SDK_INT"].optionalflatMap({ it -> Int(it) })

/// The shared top-level view for the app, loaded from the platform-specific App delegates below.
///
/// The default implementation merely loads the `ContentView` for the app and logs a message.
class RootView: View {
    constructor() {
    }

    override fun body(): View {
        return ComposeBuilder { composectx: ComposeContext ->
            ContentView()
                .task { -> MainActor.run {
                    logger.log("Welcome to Skip on ${if (androidSDK != null) "Android" else "Darwin"}!")
                    logger.warning("Skip app logs are viewable in the Xcode console for iOS; Android logs can be viewed in Studio or using adb logcat")
                } }.Compose(composectx)
        }
    }

    companion object {
    }
}

