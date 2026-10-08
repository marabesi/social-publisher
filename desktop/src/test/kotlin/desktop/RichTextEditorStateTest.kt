package desktop

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RichTextEditorStateTest {
    @Test
    fun `starts with the given text`() {
        val state = RichTextEditorState("hello")

        assertEquals("hello", state.text())
    }

    @Test
    fun `applies bold to the selected range`() {
        val state = RichTextEditorState("hello")
        state.update(TextFieldValue("hello", selection = TextRange(0, 5)))

        state.toggleBold()

        val annotated = state.value.annotatedString
        val span = annotated.spanStyles.single()
        assertEquals(FontWeight.Bold, span.item.fontWeight)
        assertEquals(0, span.start)
        assertEquals(5, span.end)
    }

    @Test
    fun `toggles bold off when the selection is already bold`() {
        val state = RichTextEditorState("hello")
        state.update(TextFieldValue("hello", selection = TextRange(0, 5)))

        state.toggleBold()
        state.toggleBold()

        val annotated = state.value.annotatedString
        assertTrue(annotated.spanStyles.isEmpty())
    }

    @Test
    fun `applies italic and underline`() {
        val state = RichTextEditorState("hello")
        state.update(TextFieldValue("hello", selection = TextRange(0, 3)))

        state.toggleItalic()
        state.toggleUnderline()

        val annotated = state.value.annotatedString
        assertTrue(annotated.spanStyles.any { it.item.fontStyle == FontStyle.Italic })
        assertTrue(annotated.spanStyles.any { it.item.textDecoration == TextDecoration.Underline })
    }

    @Test
    fun `does nothing when there is no selection`() {
        val state = RichTextEditorState("hello")
        state.update(TextFieldValue("hello", selection = TextRange(2, 2)))

        state.toggleBold()

        val annotated = state.value.annotatedString
        assertTrue(annotated.spanStyles.isEmpty())
    }

    @Test
    fun `clears the editor`() {
        val state = RichTextEditorState("hello")

        state.clear()

        assertEquals("", state.text())
    }
}
