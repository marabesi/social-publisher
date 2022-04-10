package integration

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import persistence.FileSystemRepository
import socialPosts.SocialPosts
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val post = SocialPosts("another post")
        val repository = FileSystemRepository(filePath)

        assertTrue(repository.save(arrayListOf(post)))
    }

    @Test
    fun storePostsInAFileUnderAPath() {
        val post = SocialPosts("my_another_post")
        val repository = FileSystemRepository(filePathWithSubfolder)

        assertTrue(repository.save(arrayListOf(post)))
    }

    @Test
    fun fetchPostFromCsv() {
        val post = SocialPosts("fetch from csv")
        val repository = FileSystemRepository(filePath)

        repository.save(
            arrayListOf(post)
        )

        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun fetchPostsFromCsv() {
        val repository = FileSystemRepository(filePath)

        repository.save(
            arrayListOf(
                SocialPosts("fetch from csv"),
                SocialPosts("another post")
            )
        )


        assertEquals(2, repository.findAll().size)
    }
}