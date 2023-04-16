package integration

import adapters.outbound.csv.FileSystemConfigurationRepository
import application.entities.SocialConfiguration
import application.persistence.configuration.MissingConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        repository.save(SocialConfiguration("data", timezone = "UTC"))

        val config = repository.find()
        Assertions.assertEquals(config.fileName, "data")
        Assertions.assertEquals(config.timezone, "UTC")
    }

    @Test
    fun listConfigurationWithoutAnyPreviousCreated() {
        val repository = FileSystemConfigurationRepository()
        assertFailsWith(
            exceptionClass = MissingConfiguration::class,
            message = "There is no configuration stored",
            block = {
                repository.find()
            }
        )
    }
}
