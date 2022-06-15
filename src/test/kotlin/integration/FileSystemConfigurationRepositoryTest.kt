package integration

import cli.MissingConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.FileSystemConfigurationRepository
import socialPosts.SocialConfiguration
import java.io.File
import kotlin.test.assertFailsWith

class FileSystemConfigurationRepositoryTest {

    @AfterEach
    fun afterEach() {
        File("data/global.json").delete()
    }

    @BeforeEach
    fun beforeEach() {
        File("data/global.json").delete()
    }

    @Test
    fun storeConfiguration() {
        val repository = FileSystemConfigurationRepository()

        val configuration = SocialConfiguration("tmp")
        Assertions.assertEquals("tmp", repository.save(configuration).fileName)
    }

    @Test
    fun ensuresFileOrPathExistsBeforeCreatingFile() {
        val repository = FileSystemConfigurationRepository()

        Assertions.assertEquals("whatever", repository.save(SocialConfiguration("whatever")).fileName)
    }

    @Test
    fun listConfiguration() {
        val repository = FileSystemConfigurationRepository()
        repository.save(SocialConfiguration("data"))

        val config = repository.find()
        Assertions.assertEquals(config.fileName, "data")
    }

    @Test
    fun listConfigurationWithoutAnyPreviousCreated() {
        val repository = FileSystemConfigurationRepository()
        assertFailsWith(
            exceptionClass = MissingConfiguration::class,
            block = {
                repository.find()
            }
        )
    }
}