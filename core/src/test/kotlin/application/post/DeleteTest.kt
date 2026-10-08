package application.post

import MockedOutput
import adapters.outbound.inmemory.InMemoryPostRepository
import application.Messages
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteTest {
    private lateinit var postsRepository: InMemoryPostRepository
    private lateinit var delete: Delete

    @BeforeEach
    fun setUp() {
        postsRepository = InMemoryPostRepository()
        delete = Delete(postsRepository, MockedOutput())
    }

    @Test
    fun `should remove an existing post`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "to be removed")))

        val result = delete.invoke("1")

        assertEquals("Post 1 has been removed", result)
        assertEquals(0, postsRepository.findAll().size)
    }

    @Test
    fun `should show missing required fields when id is blank`() {
        assertEquals(Messages.MISSING_REQUIRED_FIELDS, delete.invoke(""))
    }

    @Test
    fun `should show missing required fields when post does not exist`() {
        assertEquals(Messages.MISSING_REQUIRED_FIELDS, delete.invoke("42"))
    }
}
