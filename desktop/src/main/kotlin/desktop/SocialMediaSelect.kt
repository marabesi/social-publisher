package desktop

import androidx.compose.foundation.layout.fillMaxWidth
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
import application.entities.SocialMedia

const val SOCIAL_MEDIA_SELECT_TAG = "socialMediaSelect"
const val ALL_SOCIAL_MEDIA_LABEL = "All networks"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun socialMediaSelect(
    selected: SocialMedia?,
    onSelect: (SocialMedia?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Social media",
    testTag: String = SOCIAL_MEDIA_SELECT_TAG,
    includeAllOption: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.testTag(testTag),
    ) {
        OutlinedTextField(
            value = selected?.displayName ?: ALL_SOCIAL_MEDIA_LABEL,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (includeAllOption) {
                socialMediaOption(
                    label = ALL_SOCIAL_MEDIA_LABEL,
                    testTag = "$testTag-ALL",
                    onClick = {
                        onSelect(null)
                        expanded = false
                    },
                )
            }
            SocialMedia.entries.forEach { media ->
                socialMediaOption(
                    label = media.displayName,
                    testTag = "$testTag-${media.name}",
                    onClick = {
                        onSelect(media)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun socialMediaOption(
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
