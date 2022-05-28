package unit

import Post
import Scheduler
import persistence.InMemoryRepository
import picocli.CommandLine
import kotlin.test.assertEquals

class SchedulerTest {

    fun `should schedule a post to be posted in one day from now`() {
        val cmd = CommandLine(Scheduler(InMemoryRepository()))

        cmd.execute("-t", "2022")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
        """.trimIndent(), result)
    }
}