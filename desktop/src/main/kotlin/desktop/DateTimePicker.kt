package desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dateTimePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()
    val timeState = rememberTimePickerState(is24Hour = true)

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable { dialogOpen = true },
        trailingIcon = {
            IconButton(onClick = { dialogOpen = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
            }
        },
    )

    if (dialogOpen) {
        DatePickerDialog(
            onDismissRequest = { dialogOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            onValueChange(toIsoInstant(millis, timeState.hour, timeState.minute))
                        }
                        dialogOpen = false
                    },
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogOpen = false }) {
                    Text("Cancel")
                }
            },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DatePicker(state = dateState, modifier = Modifier.fillMaxWidth())
                TimePicker(state = timeState)
            }
        }
    }
}

internal fun toIsoInstant(
    selectedDateMillis: Long,
    hour: Int,
    minute: Int,
): String {
    val date = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneOffset.UTC).toLocalDate()
    return LocalDateTime.of(date, LocalTime.of(hour, minute)).toInstant(ZoneOffset.UTC).toString()
}
