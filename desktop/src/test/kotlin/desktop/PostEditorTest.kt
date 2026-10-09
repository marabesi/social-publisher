package desktop

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class PostEditorTest {
    @Test
    fun `shows the character count while typing`() =
        runComposeUiTest {
            val state = PostEditorState("")
            setContent { postEditor(state) }

            onNodeWithText("0 / 280").assertIsDisplayed()

            onNode(hasSetTextAction()).performTextReplacement("hello world")

            onNodeWithText("11 / 280").assertIsDisplayed()
        }

    @Test
    fun `reports a count above the limit`() =
        runComposeUiTest {
            val state = PostEditorState("")
            setContent { postEditor(state, charLimit = 5) }

            onNode(hasSetTextAction()).performTextReplacement("too long")

            onNodeWithText("8 / 5").assertIsDisplayed()
        }
}
