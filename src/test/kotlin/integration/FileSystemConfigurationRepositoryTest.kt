package integration

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.FileSystemConfigurationRepository
import java.io.File

class FileSystemConfigurationRepositoryTest {

    private val filePath = "configuration.json"
    private val filePathWithSubfolder = "data/configuration.json"

    @BeforeEach
    fun beforeEach() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
        File("whatever").deleteRecursively()
    }

    @AfterEach
    fun afterEach() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
        File("whatever").deleteRecursively()
    }

    @Test
    fun storeConfiguration() {
        val repository = FileSystemConfigurationRepository()

        Assertions.assertEquals("data/configuration.json", repository.save("data").path)
    }

    @Test
    fun ensuresFileOrPathExistsBeforeCreatingFile() {
        val repository = FileSystemConfigurationRepository()

        Assertions.assertEquals("whatever/configuration.json", repository.save("whatever").path)
    }

    @Test
    fun listConfiguration() {
        val repository = FileSystemConfigurationRepository()
        repository.save("data")

        val config = repository.find("data")
        Assertions.assertEquals(config.path, "data/configuration.json")
    }
}