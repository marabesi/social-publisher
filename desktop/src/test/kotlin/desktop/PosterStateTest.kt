package desktop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class PosterStateTest {
    @Test
    fun `is disabled by default and reports no next run`() {
        val state = PosterState(runPoster = { "run" })

        assertFalse(state.enabled)
        assertNull(state.nextRun())
    }

    @Test
    fun `computes the first next run from the clock when enabled`() {
        val fixed = Instant.parse("2026-10-02T09:00:00Z")
        val state = PosterState(runPoster = { "run" }, clock = { fixed })

        state.updateEnabled(true)

        assertEquals("2026-10-02T09:01:00Z", state.nextRun().toString())
    }

    @Test
    fun `computes the next run from the last run plus the cadence`() {
        val fixed = Instant.parse("2026-10-02T09:00:00Z")
        val state = PosterState(runPoster = { "run" }, clock = { fixed })

        state.updateEnabled(true)
        state.runNow()

        assertEquals("2026-10-02T09:01:00Z", state.nextRun().toString())
    }

    @Test
    fun `runs the poster and records the output`() {
        val state = PosterState(runPoster = { "Post 1 sent to twitter" })
        state.updateEnabled(true)

        val output = state.runNow()

        assertEquals("Post 1 sent to twitter", output)
        assertEquals("Post 1 sent to twitter", state.lastOutput)
        assertTrue(state.lastRunAt != null)
    }

    @Test
    fun `changes the cadence to every five minutes`() {
        val state = PosterState(runPoster = { "run" })

        state.setCadence(5)

        assertEquals(5, state.cadenceMinutes)
    }

    @Test
    fun `clears the last run when disabled`() {
        val state = PosterState(runPoster = { "run" })
        state.updateEnabled(true)
        state.runNow()

        state.updateEnabled(false)

        assertNull(state.nextRun())
        assertNull(state.lastRunAt)
    }
}
