package desktop

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import application.order.Direction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun sortSelect(
    optionLabel: String,
    selected: Direction,
    ascendingLabel: String,
    descendingLabel: String,
    testTag: String,
    onSelect: (Direction) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val label =
        when (selected) {
            Direction.ASC -> ascendingLabel
            Direction.DESC -> descendingLabel
        }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.width(200.dp).testTag(testTag),
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text(optionLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            sortOption(ascendingLabel, "$testTag-ASC") {
                onSelect(Direction.ASC)
                expanded = false
            }
            sortOption(descendingLabel, "$testTag-DESC") {
                onSelect(Direction.DESC)
                expanded = false
            }
        }
    }
}

@Composable
private fun sortOption(
    label: String,
    testTag: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        modifier = Modifier.testTag(testTag),
    )
}
