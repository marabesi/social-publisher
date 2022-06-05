package integration

import org.junit.jupiter.api.Assertions.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import persistence.FileSystemRepository
import socialPosts.SocialPosts
import java.io.File

class FileSystemTest {
    private val filePath = "social-publisher.csv"
    private val filePathWithSubfolder = "data/social-publisher.csv"

    @AfterEach
    fun afterEach() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
    }

    @Test
    fun storePostsInAFile() {
        val post = SocialPosts(1, "another post")
        val repository = FileSystemRepository(filePath)

        assertTrue(repository.save(arrayListOf(post)))
    }

    @Test
    fun storePostsInAFileUnderAPath() {
        val post = SocialPosts(1, "my_another_post")
        val repository = FileSystemRepository(filePathWithSubfolder)

        assertTrue(repository.save(arrayListOf(post)))
    }

    @Test
    fun fetchPostFromCsv() {
        val post = SocialPosts(1, "fetch from csv")
        val repository = FileSystemRepository(filePath)

        repository.save(
            arrayListOf(post)
        )

        val storedPost: SocialPosts = repository.findAll().first()

        assertEquals(post.text, storedPost.text)
    }

    @Test
    fun fetchPostsFromCsv() {
        val repository = FileSystemRepository(filePath)

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

        assertEquals(1, repository.findAll()[0].id)
        assertEquals(2, repository.findAll()[1].id)
    }

    @Test
    fun fetchPostByIdFromCsv() {
        val repository = FileSystemRepository(filePath)
        val post = SocialPosts(text = "fetch from csv with id 1")

        repository.save(arrayListOf(post))

        val findById = repository.findById("1")
        assertEquals(post.text, findById?.text)
        assertEquals("1", findById?.id.toString())
    }
}