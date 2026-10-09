package desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

internal const val DATE_TIME_DIALOG_TAG = "dateTimeDialog"
internal const val DATE_PICKER_TAG = "datePicker"
internal const val TIME_PICKER_TAG = "timePicker"

private val dialogPadding = 16.dp
private val dialogSpacing = 16.dp
private val timePickerWidth = 200.dp

// BasicAlertDialog clamps its content to DialogMaxWidth (560.dp); the surface has to stay
// within that bound or the right edge (including the Ok button) is clipped out of the window.
private val dialogMaxWidth = 560.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dateTimePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    pickDescription: String = "Pick date",
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
                Icon(Icons.Default.DateRange, contentDescription = pickDescription)
            }
        },
    )

    if (dialogOpen) {
        dateTimePickerDialog(
            dateState = dateState,
            timeState = timeState,
            onDismiss = { dialogOpen = false },
            onConfirm = {
                dateState.selectedDateMillis?.let { millis ->
                    onValueChange(toIsoInstant(millis, timeState.hour, timeState.minute))
                }
                dialogOpen = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dateTimePickerDialog(
    dateState: DatePickerState,
    timeState: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.widthIn(max = dialogMaxWidth).testTag(DATE_TIME_DIALOG_TAG),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = dialogPadding, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(dialogSpacing),
                    verticalAlignment = Alignment.Top,
                ) {
                    DatePicker(
                        state = dateState,
                        modifier = Modifier.weight(1f).testTag(DATE_PICKER_TAG),
                    )
                    TimePicker(
                        state = timeState,
                        modifier = Modifier.width(timePickerWidth).testTag(TIME_PICKER_TAG),
                        layoutType = TimePickerLayoutType.Vertical,
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = onConfirm) {
                        Text("Ok")
                    }
                }
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
