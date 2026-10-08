package integration

import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.StorePath
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class FileSystemPostRepositoryTest {
    private val filePath = "social-publisher.csv"
    private val filePathWithSubfolder = "data/social-publisher.csv"
    private val suffixStoreDirectory = "build/store-suffix"

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
            arrayListOf(post),
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
            ),
        )
        repository.save(
            arrayListOf(
                SocialPosts(text = "fetch from csv"),
            ),
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

    @Test
    fun deletePostByIdFromCsv() {
        val repository = FileSystemPostRepository(filePath)
        repository.save(
            arrayListOf(
                SocialPosts(text = "first post"),
                SocialPosts(text = "second post"),
            ),
        )

        val deleted = repository.deleteById("1")

        assertEquals("first post", deleted?.text)
        assertEquals(1, repository.findAll().size)
        assertEquals("second post", repository.findAll().first().text)
    }

    @Test
    fun updatePostFromCsv() {
        val repository = FileSystemPostRepository(filePath)
        repository.save(arrayListOf(SocialPosts(text = "original text")))

        assertTrue(repository.update(SocialPosts("1", "edited text")))

        assertEquals("edited text", repository.findById("1")?.text)
        assertEquals(1, repository.findAll().size)
    }

    @Test
    fun keepsIdsUniqueAfterDeletingAPost() {
        val repository = FileSystemPostRepository(filePath)
        repository.save(
            arrayListOf(
                SocialPosts(text = "first post"),
                SocialPosts(text = "second post"),
                SocialPosts(text = "third post"),
            ),
        )

        repository.deleteById("2")
        repository.save(arrayListOf(SocialPosts(text = "fourth post")))

        assertEquals("4", repository.findAll().last().id)
    }

    private fun cleanUp() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
        File(suffixStoreDirectory).deleteRecursively()
    }

    @Test
    fun usesTheConfigurationFileNameAsTheFileSuffix() {
        val configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(SocialConfiguration(fileName = "e2e-file"))
        val repository =
            FileSystemPostRepository(
                configurationRepository = configurationRepository,
                storePath = StorePath { suffixStoreDirectory },
            )

        repository.save(arrayListOf(SocialPosts(text = "suffixed post")))

        assertTrue(File("$suffixStoreDirectory/posts-e2e-file.csv").exists())
    }
}
