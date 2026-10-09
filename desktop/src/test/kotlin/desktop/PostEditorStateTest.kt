package desktop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PostEditorStateTest {
    @Test
    fun `starts with the given text`() {
        val state = PostEditorState("hello")

        assertEquals("hello", state.text)
    }

    @Test
    fun `updates the text`() {
        val state = PostEditorState("hello")

        state.update("updated")

        assertEquals("updated", state.text)
    }

    @Test
    fun `clears the editor`() {
        val state = PostEditorState("hello")

        state.clear()

        assertEquals("", state.text)
    }
}
