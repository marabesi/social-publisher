package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import application.entities.ScheduledItem
import application.entities.SocialMedia

internal const val SCHEDULE_POST_ID_TAG = "schedulePostId"
internal const val SCHEDULE_SOCIAL_MEDIA_TAG = "scheduleSocialMedia"

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "MaxLineLength")
@Composable
fun schedulesPage(store: SocialPublisherStore) {
    val errorReporter = LocalErrorReporter.current
    var schedules by remember {
        mutableStateOf(errorReporter.reporting(arrayListOf<ScheduledItem>()) { store.schedules() })
    }
    var postId by remember { mutableStateOf("") }
    var publishDate by remember { mutableStateOf("") }
    var socialMedia by remember { mutableStateOf(SocialMedia.TWITTER) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Schedules", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = postId,
                onValueChange = { postId = it },
                label = { Text("Post id") },
                modifier = Modifier.width(120.dp).testTag(SCHEDULE_POST_ID_TAG),
            )
            socialMediaSelect(
                selected = socialMedia,
                onSelect = { socialMedia = it },
                modifier = Modifier.weight(1f),
            )
            dateTimePicker(
                label = "Publish date",
                value = publishDate,
                onValueChange = { publishDate = it },
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    errorReporter.reporting {
                        message = store.schedule(postId, publishDate, socialMedia)
                        schedules = store.schedules()
                    }
                },
            ) {
                Text("Schedule")
            }
            Button(onClick = { schedules = errorReporter.reporting(schedules) { store.schedules() } }) {
                Text("Refresh")
            }
        }

        if (message.isNotBlank()) {
            Text(message)
        }

        if (schedules.isEmpty()) {
            Text("No schedules yet")
        } else {
            LazyColumn {
                items(schedules) { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.id}. Post ${item.post.id} on ${item.publishDate} (${item.socialMedia.displayName})",
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(
                            onClick = {
                                errorReporter.reporting {
                                    store.deleteSchedule(item.id ?: "")
                                    schedules = store.schedules()
                                }
                            },
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun socialMediaSelect(
    selected: SocialMedia,
    onSelect: (SocialMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.testTag(SCHEDULE_SOCIAL_MEDIA_TAG),
    ) {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Social media") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SocialMedia.entries.forEach { media ->
                DropdownMenuItem(
                    text = { Text(media.displayName) },
                    onClick = {
                        onSelect(media)
                        expanded = false
                    },
                    modifier = Modifier.testTag("$SCHEDULE_SOCIAL_MEDIA_TAG-${media.name}"),
                )
            }
        }
    }
}
