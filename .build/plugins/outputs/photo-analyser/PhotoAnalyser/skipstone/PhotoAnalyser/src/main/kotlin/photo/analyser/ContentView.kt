package photo.analyser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import skip.lib.*

import skip.ui.*
import skip.foundation.*
import skip.model.*

class ContentView: View {
    internal var tab: Tab
        get() = _tab.wrappedValue
        set(newValue) {
            _tab.wrappedValue = newValue
        }
    internal var _tab: skip.ui.AppStorage<Tab> = skip.ui.AppStorage(Tab.welcome, "tab", serializer = { it.rawValue }, deserializer = { if (it is String) Tab(rawValue = it) else null })
    internal var name: String
        get() = _name.wrappedValue
        set(newValue) {
            _name.wrappedValue = newValue
        }
    internal var _name: skip.ui.AppStorage<String> = skip.ui.AppStorage("Skipper", "name")
    internal var appearance: String
        get() = _appearance.wrappedValue
        set(newValue) {
            _appearance.wrappedValue = newValue
        }
    internal var _appearance: skip.ui.State<String> = skip.ui.State("")
    internal var isBeating: Boolean
        get() = _isBeating.wrappedValue
        set(newValue) {
            _isBeating.wrappedValue = newValue
        }
    internal var _isBeating: skip.ui.State<Boolean> = skip.ui.State(false)

    constructor() {
    }

    override fun body(): View {
        return ComposeBuilder { composectx: ComposeContext ->
            TabView(selection = _tab.projectedValue) { ->
                ComposeBuilder { composectx: ComposeContext ->
                    VStack(spacing = 0.0) { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            Text({
                                val str = LocalizedStringKey.StringInterpolation(literalCapacity = 0, interpolationCount = 0)
                                str.appendLiteral("Hello ")
                                str.appendInterpolation(name)
                                str.appendLiteral("!")
                                LocalizedStringKey(stringInterpolation = str)
                            }())
                                .padding().Compose(composectx)
                            Image(systemName = "heart.fill")
                                .foregroundStyle(Color.red)
                                .scaleEffect(if (isBeating) 1.5 else 1.0)
                                .animation(Animation.easeInOut(duration = 1.0).repeatForever(), value = isBeating)
                                .onAppear { -> isBeating = true }.Compose(composectx)
                            ComposeResult.ok
                        }
                    }
                    .font(Font.largeTitle)
                    .tabItem { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            Label(LocalizedStringKey(stringLiteral = "Welcome"), systemImage = "heart.fill").Compose(composectx)
                            ComposeResult.ok
                        }
                    }
                    .tag(Tab.welcome).Compose(composectx)

                    NavigationStack { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            List { ->
                                ComposeBuilder { composectx: ComposeContext ->
                                    ForEach(1 until 1_000) { i ->
                                        ComposeBuilder { composectx: ComposeContext ->
                                            NavigationLink({
                                                val str = LocalizedStringKey.StringInterpolation(literalCapacity = 0, interpolationCount = 0)
                                                str.appendLiteral("Item ")
                                                str.appendInterpolation(i)
                                                LocalizedStringKey(stringInterpolation = str)
                                            }(), value = i).Compose(composectx)
                                            ComposeResult.ok
                                        }
                                    }.Compose(composectx)
                                    ComposeResult.ok
                                }
                            }
                            .navigationTitle(LocalizedStringKey(stringLiteral = "Home"))
                            .navigationDestination(for_ = Int::class) { i ->
                                ComposeBuilder { composectx: ComposeContext ->
                                    Text({
                                        val str = LocalizedStringKey.StringInterpolation(literalCapacity = 0, interpolationCount = 0)
                                        str.appendLiteral("Item ")
                                        str.appendInterpolation(i)
                                        LocalizedStringKey(stringInterpolation = str)
                                    }())
                                        .font(Font.title)
                                        .navigationTitle({
                                            val str = LocalizedStringKey.StringInterpolation(literalCapacity = 0, interpolationCount = 0)
                                            str.appendLiteral("Screen ")
                                            str.appendInterpolation(i)
                                            LocalizedStringKey(stringInterpolation = str)
                                        }()).Compose(composectx)
                                    ComposeResult.ok
                                }
                            }.Compose(composectx)
                            ComposeResult.ok
                        }
                    }
                    .tabItem { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            Label(LocalizedStringKey(stringLiteral = "Home"), systemImage = "house.fill").Compose(composectx)
                            ComposeResult.ok
                        }
                    }
                    .tag(Tab.home).Compose(composectx)

                    NavigationStack { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            Form { ->
                                ComposeBuilder { composectx: ComposeContext ->
                                    TextField(LocalizedStringKey(stringLiteral = "Name"), text = _name.projectedValue).Compose(composectx)
                                    Picker(LocalizedStringKey(stringLiteral = "Appearance"), selection = Binding({ _appearance.wrappedValue }, { it -> _appearance.wrappedValue = it })) { ->
                                        ComposeBuilder { composectx: ComposeContext ->
                                            Text(LocalizedStringKey(stringLiteral = "System")).tag("").Compose(composectx)
                                            Text(LocalizedStringKey(stringLiteral = "Light")).tag("light").Compose(composectx)
                                            Text(LocalizedStringKey(stringLiteral = "Dark")).tag("dark").Compose(composectx)
                                            ComposeResult.ok
                                        }
                                    }.Compose(composectx)
                                    HStack { ->
                                        ComposeBuilder { composectx: ComposeContext ->
                                            ComposeView { ctx -> androidx.compose.material3.Text("💚", modifier = ctx.modifier) }.Compose(composectx)
                                            Text({
                                                val str = LocalizedStringKey.StringInterpolation(literalCapacity = 0, interpolationCount = 0)
                                                str.appendLiteral("Powered by ")
                                                str.appendInterpolation(if (androidSDK != null) "Jetpack Compose" else "SwiftUI")
                                                LocalizedStringKey(stringInterpolation = str)
                                            }()).Compose(composectx)
                                            ComposeResult.ok
                                        }
                                    }
                                    .foregroundStyle(Color.gray).Compose(composectx)
                                    ComposeResult.ok
                                }
                            }
                            .navigationTitle(LocalizedStringKey(stringLiteral = "Settings")).Compose(composectx)
                            ComposeResult.ok
                        }
                    }
                    .tabItem { ->
                        ComposeBuilder { composectx: ComposeContext ->
                            Label(LocalizedStringKey(stringLiteral = "Settings"), systemImage = "gearshape.fill").Compose(composectx)
                            ComposeResult.ok
                        }
                    }
                    .tag(Tab.settings).Compose(composectx)
                    ComposeResult.ok
                }
            }
            .preferredColorScheme(if (appearance == "dark") ColorScheme.dark else if (appearance == "light") ColorScheme.light else null).Compose(composectx)
        }
    }

    @Composable
    @Suppress("UNCHECKED_CAST")
    override fun ComposeContent(composectx: ComposeContext) {
        val rememberedappearance by rememberSaveable(stateSaver = composectx.stateSaver as Saver<skip.ui.State<String>, Any>) { mutableStateOf(_appearance) }
        _appearance = rememberedappearance

        val rememberedisBeating by rememberSaveable(stateSaver = composectx.stateSaver as Saver<skip.ui.State<Boolean>, Any>) { mutableStateOf(_isBeating) }
        _isBeating = rememberedisBeating

        val rememberedtab by rememberSaveable(stateSaver = composectx.stateSaver as Saver<skip.ui.AppStorage<Tab>, Any>) { mutableStateOf(_tab) }
        _tab = rememberedtab

        val rememberedname by rememberSaveable(stateSaver = composectx.stateSaver as Saver<skip.ui.AppStorage<String>, Any>) { mutableStateOf(_name) }
        _name = rememberedname

        super.ComposeContent(composectx)
    }

    companion object {
    }
}

internal enum class Tab(override val rawValue: String, @Suppress("UNUSED_PARAMETER") unusedp: Nothing? = null): RawRepresentable<String> {
    welcome("welcome"),
    home("home"),
    settings("settings");
}

internal fun Tab(rawValue: String): Tab? {
    return when (rawValue) {
        "welcome" -> Tab.welcome
        "home" -> Tab.home
        "settings" -> Tab.settings
        else -> null
    }
}

// #Preview omitted
