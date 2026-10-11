package desktop

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.configureSwingGlobalsForCompose
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Taskbar
import java.awt.Toolkit
import java.awt.event.InputEvent

const val APP_NAME = "Social Publisher"

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    System.setProperty("apple.awt.application.name", APP_NAME)
    configureSwingGlobalsForCompose()
    setApplicationIcon()
    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
        val appIcon = remember { loadAppIcon() }
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = APP_NAME,
            icon = appIcon,
        ) {
            MenuBar {
                Menu(text = APP_NAME, mnemonic = 'S') {
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

/** Sets the icon shown in the macOS Dock/Alt-Tab switcher and Windows taskbar. */
private fun setApplicationIcon() {
    val icon = loadAwtAppIcon() ?: return
    if (!Taskbar.isTaskbarSupported()) return
    val taskbar = Taskbar.getTaskbar()
    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
        taskbar.setIconImage(icon)
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
