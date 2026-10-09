package desktop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.configureSwingGlobalsForCompose
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Toolkit
import java.awt.event.InputEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    configureSwingGlobalsForCompose()
    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Social Publisher",
        ) {
            MenuBar {
                Menu(text = "Social Publisher", mnemonic = 'S') {
                    Item(
                        text = "Quit",
                        shortcut = menuShortcut(Key.Q),
                        onClick = ::exitApplication,
                    )
                }
            }
            socialPublisherApp()
        }
    }
}

private fun menuShortcut(key: Key): KeyShortcut {
    val mask = Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
    return KeyShortcut(
        key = key,
        ctrl = mask and InputEvent.CTRL_DOWN_MASK != 0,
        meta = mask and InputEvent.META_DOWN_MASK != 0,
    )
}
