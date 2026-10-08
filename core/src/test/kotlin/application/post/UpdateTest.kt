package application.post

import MockedOutput
import adapters.outbound.inmemory.InMemoryPostRepository
import application.Messages
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateTest {
    private lateinit var postsRepository: InMemoryPostRepository
    private lateinit var update: Update

    @BeforeEach
    fun setUp() {
        postsRepository = InMemoryPostRepository()
        update = Update(postsRepository, MockedOutput())
    }

    @Test
    fun `should update an existing post`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "hello desktop")))

        val result = update.invoke("1", "edited text")

        assertEquals("Post has been updated", result)
        assertEquals("edited text", postsRepository.findById("1")?.text)
    }

    @Test
    fun `should show missing required fields when text is blank`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "hello desktop")))

        assertEquals(Messages.MISSING_REQUIRED_FIELDS, update.invoke("1", ""))
    }

    @Test
    fun `should show missing required fields when id is blank`() {
        assertEquals(Messages.MISSING_REQUIRED_FIELDS, update.invoke("", "edited text"))
    }

    @Test
    fun `should show message when post does not exist`() {
        assertEquals("Couldn't find post with id 42", update.invoke("42", "edited text"))
    }
}
