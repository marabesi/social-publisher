package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PosterState(
    val runPoster: () -> String,
    private val clock: () -> Instant = { Instant.now() },
) {
    var enabled by mutableStateOf(false)
        private set
    var cadenceMinutes by mutableStateOf(1)
        private set
    var lastRunAt by mutableStateOf<Instant?>(null)
        private set
    var lastOutput by mutableStateOf("")
        private set

    fun updateEnabled(value: Boolean) {
        enabled = value
        if (!value) {
            lastRunAt = null
        }
    }

    fun setCadence(minutes: Int) {
        cadenceMinutes = minutes.coerceAtLeast(1)
    }

    fun nextRun(): Instant? =
        if (enabled) {
            (lastRunAt ?: clock()).plus(cadenceMinutes.toLong(), ChronoUnit.MINUTES)
        } else {
            null
        }

    fun runNow(): String {
        val output = runPoster()
        lastRunAt = clock()
        lastOutput = output
        return output
    }
}

@Composable
fun posterPage(state: PosterState) {
    val errorReporter = LocalErrorReporter.current
    var cadenceMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Poster", style = MaterialTheme.typography.titleLarge)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Switch(
                checked = state.enabled,
                onCheckedChange = state::updateEnabled,
                modifier = Modifier.testTag("poster-enabled"),
            )
            Text(if (state.enabled) "Enabled" else "Disabled")
        }

        val nextRun = state.nextRun()
        Text(
            text =
                when {
                    !state.enabled -> "The poster is disabled."
                    nextRun == null -> "Next run: as soon as possible"
                    else -> "Next run: ${formatInstant(nextRun)}"
                },
        )

        Text("Run every")

        Box {
            OutlinedButton(onClick = { cadenceMenuOpen = true }) {
                Text(cadenceLabel(state.cadenceMinutes))
            }
            DropdownMenu(expanded = cadenceMenuOpen, onDismissRequest = { cadenceMenuOpen = false }) {
                POSTER_CADENCES.forEach { minutes ->
                    DropdownMenuItem(
                        text = { Text(cadenceLabel(minutes)) },
                        onClick = {
                            state.setCadence(minutes)
                            cadenceMenuOpen = false
                        },
                    )
                }
            }
        }

        Button(onClick = { errorReporter.reporting { state.runNow() } }) {
            Text("Run now")
        }

        if (state.lastOutput.isNotBlank()) {
            Text(state.lastOutput)
        }
    }
}

private val POSTER_CADENCES = listOf(1, 5, 10, 30, 60)

private fun cadenceLabel(minutes: Int): String =
    if (minutes == 1) {
        "Every minute"
    } else {
        "Every $minutes minutes"
    }

private fun formatInstant(instant: Instant): String =
    DateTimeFormatter
        .ofPattern("dd MMM yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(instant)
