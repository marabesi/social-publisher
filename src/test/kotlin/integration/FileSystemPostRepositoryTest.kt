package integration

import org.junit.jupiter.api.Assertions.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import adapters.outbound.csv.FileSystemPostRepository
import application.entities.SocialPosts
import java.io.File

class FileSystemPostRepositoryTest {
    private val filePath = "social-publisher.csv"
    private val filePathWithSubfolder = "data/social-publisher.csv"

    @BeforeEach
    fun beforeEach() {
        cleanUp()
    }

    @AfterEach
    fun afterEach() {
        cleanUp()
    }

    @Test
    fun storePostsInAFile() {
        val post = SocialPosts("1", "another post")
        val repository = FileSystemPostRepository(filePath)

        assertTrue(repository.save(arrayListOf(post)))
    }

    @Test
    fun storePostsInAFileUnderAPath() {
        val post = SocialPosts("1", "my_another_post")
        val repository = FileSystemPostRepository(filePathWithSubfolder)

        assertTrue(repository.save(arrayListOf(post)))
    }

    @Test
    fun fetchEmptyPosts() {
        val repository = FileSystemPostRepository(filePath)

        val storedPost: ArrayList<SocialPosts> = repository.findAll()

        assertEquals(0, storedPost.size)
    }

    @Test
    fun fetchPostFromCsv() {
        val post = SocialPosts("1", "fetch from csv")
        val repository = FileSystemPostRepository(filePath)

        repository.save(
            arrayListOf(post)
        )

        val storedPost: SocialPosts = repository.findAll().first()

        assertEquals(post.text, storedPost.text)
    }

    @Test
    fun fetchPostsFromCsv() {
        val repository = FileSystemPostRepository(filePath)

        repository.save(
            arrayListOf(
                SocialPosts(text = "fetch from csv"),
            )
        )
        repository.save(
            arrayListOf(
                SocialPosts(text = "fetch from csv"),
            )
        )

        assertEquals("1", repository.findAll()[0].id)
        assertEquals("2", repository.findAll()[1].id)
    }

    @Test
    fun fetchPostByIdFromCsv() {
        val repository = FileSystemPostRepository(filePath)
        val post = SocialPosts(text = "fetch from csv with id 1")

        repository.save(arrayListOf(post))

        val findById = repository.findById("1")
        assertEquals(post.text, findById?.text)
        assertEquals("1", findById?.id.toString())
    }

    private fun cleanUp() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
    }
}
