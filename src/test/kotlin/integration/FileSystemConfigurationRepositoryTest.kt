package integration

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import persistence.FileSystemConfigurationRepository
import socialPosts.SocialConfiguration

class FileSystemConfigurationRepositoryTest {

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
}