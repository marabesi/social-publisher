package application.search

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextSearchTest {
    @Test
    fun `an empty query matches everything`() {
        assertTrue(TextSearch.matches("", "anything at all"))
        assertTrue(TextSearch.matches("   ", "anything at all"))
    }

    @Test
    fun `matches a word case insensitively`() {
        assertTrue(TextSearch.matches("AWS", "deploying to AWS today"))
        assertTrue(TextSearch.matches("aws", "deploying to AWS today"))
    }

    @Test
    fun `matches the start of a word`() {
        assertTrue(TextSearch.matches("desk", "hello desktop"))
        assertTrue(TextSearch.matches("release", "release notes"))
    }

    @Test
    fun `does not match when the term only appears inside another word`() {
        assertFalse(TextSearch.matches("aws", "these are the laws"))
        assertFalse(TextSearch.matches("aws", "he draws well"))
        assertFalse(TextSearch.matches("aws", "the claws came out"))
    }

    @Test
    fun `does not match characters scattered across the text`() {
        assertFalse(TextSearch.matches("aws", "a quiet walk outside"))
    }

    @Test
    fun `matches a term after a punctuation boundary`() {
        assertTrue(TextSearch.matches("notes", "release-notes"))
        assertTrue(TextSearch.matches("aws", "cloud (AWS)"))
    }

    @Test
    fun `requires every token to match`() {
        assertTrue(TextSearch.matches("hello desk", "hello desktop"))
        assertFalse(TextSearch.matches("hello missing", "hello desktop"))
    }
}
