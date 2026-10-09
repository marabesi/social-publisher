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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import application.scheduler.ScheduleSearch

internal const val SCHEDULE_POST_ID_TAG = "schedulePostId"
internal const val SCHEDULE_SOCIAL_MEDIA_TAG = "scheduleSocialMedia"
internal const val SCHEDULE_SEARCH_TEXT_TAG = "scheduleSearchText"
internal const val SCHEDULE_SEARCH_IDS_TAG = "scheduleSearchIds"
internal const val SCHEDULE_SEARCH_SOCIAL_MEDIA_TAG = "scheduleSearchSocialMedia"

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
    var searchText by remember { mutableStateOf("") }
    var searchIds by remember { mutableStateOf("") }
    var searchSocialMedia by remember { mutableStateOf<SocialMedia?>(null) }

    val visibleSchedules =
        ScheduleSearch
            .from(text = searchText, postIds = searchIds, socialMedia = searchSocialMedia)
            .filter(schedules)

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
                onSelect = { selected -> selected?.let { socialMedia = it } },
                testTag = SCHEDULE_SOCIAL_MEDIA_TAG,
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

        HorizontalDivider()

        Text("Search", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search text") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag(SCHEDULE_SEARCH_TEXT_TAG),
            )
            OutlinedTextField(
                value = searchIds,
                onValueChange = { searchIds = it },
                label = { Text("Post ids (comma separated)") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag(SCHEDULE_SEARCH_IDS_TAG),
            )
            socialMediaSelect(
                selected = searchSocialMedia,
                onSelect = { searchSocialMedia = it },
                label = "Social network",
                testTag = SCHEDULE_SEARCH_SOCIAL_MEDIA_TAG,
                includeAllOption = true,
                modifier = Modifier.weight(1f),
            )
        }

        when {
            schedules.isEmpty() -> Text("No schedules yet")
            visibleSchedules.isEmpty() -> Text("No schedules match your search")
            else ->
                LazyColumn {
                    items(visibleSchedules) { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text =
                                    "${item.id}. Post ${item.post.id} on ${item.publishDate} " +
                                        "(${item.socialMedia.displayName})",
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
