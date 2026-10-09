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
import application.scheduler.order.Direction
import application.scheduler.order.PublishDateOrder
import java.time.LocalDate

internal const val SCHEDULE_POST_ID_TAG = "schedulePostId"
internal const val SCHEDULE_SOCIAL_MEDIA_TAG = "scheduleSocialMedia"
internal const val SCHEDULE_PUBLISH_DATE_TAG = "schedulePublishDate"
internal const val SCHEDULE_CREATE_TAG = "scheduleCreate"
internal const val SCHEDULE_RANDOM_DAY_TAG = "scheduleRandomDay"
internal const val SCHEDULE_RANDOM_SOCIAL_MEDIA_TAG = "scheduleRandomSocialMedia"
internal const val SCHEDULE_RANDOM_TAG = "scheduleRandom"
internal const val SCHEDULE_SEARCH_TEXT_TAG = "scheduleSearchText"
internal const val SCHEDULE_SEARCH_IDS_TAG = "scheduleSearchIds"
internal const val SCHEDULE_SEARCH_SOCIAL_MEDIA_TAG = "scheduleSearchSocialMedia"
internal const val SCHEDULE_SORT_TAG = "scheduleSort"
internal const val SCHEDULE_SORT_DEFAULT_LABEL = "Default order"
internal const val SCHEDULE_SORT_ASC_LABEL = "Date ascending"
internal const val SCHEDULE_SORT_DESC_LABEL = "Date descending"

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
    var randomDay by remember { mutableStateOf(LocalDate.now().toString()) }
    var randomSocialMedia by remember { mutableStateOf(SocialMedia.TWITTER) }
    var randomMessage by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var searchIds by remember { mutableStateOf("") }
    var searchSocialMedia by remember { mutableStateOf<SocialMedia?>(null) }
    var sortDirection by remember { mutableStateOf<Direction?>(null) }

    val filteredSchedules =
        ScheduleSearch
            .from(text = searchText, postIds = searchIds, socialMedia = searchSocialMedia)
            .filter(schedules)
    val visibleSchedules =
        sortDirection?.let { PublishDateOrder(it).apply(ArrayList(filteredSchedules)) } ?: filteredSchedules

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Schedules", style = MaterialTheme.typography.titleLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                scheduleSortSelect(
                    selected = sortDirection,
                    onSelect = { sortDirection = it },
                )
                Button(onClick = { schedules = errorReporter.reporting(schedules) { store.schedules() } }) {
                    Text("Refresh")
                }
            }
        }

        HorizontalDivider()

        Text("New schedule", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = postId,
                onValueChange = { postId = it },
                label = { Text("Post id") },
                singleLine = true,
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
                pickDescription = "Pick publish date",
                modifier = Modifier.weight(1f).testTag(SCHEDULE_PUBLISH_DATE_TAG),
            )
            Button(
                onClick = {
                    errorReporter.reporting {
                        message = store.schedule(postId, publishDate, socialMedia)
                        schedules = store.schedules()
                    }
                },
                modifier = Modifier.testTag(SCHEDULE_CREATE_TAG),
            ) {
                Text("Schedule")
            }
        }
        if (message.isNotBlank()) {
            Text(message, color = MaterialTheme.colorScheme.primary)
        }

        HorizontalDivider()

        Text("Random", style = MaterialTheme.typography.titleMedium)
        Text(
            "Picks a post not scheduled in that week, at least 30 minutes from now.",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dateTimePicker(
                label = "Day",
                value = randomDay,
                onValueChange = { randomDay = it },
                pickDescription = "Pick random day",
                modifier = Modifier.weight(1f).testTag(SCHEDULE_RANDOM_DAY_TAG),
            )
            socialMediaSelect(
                selected = randomSocialMedia,
                onSelect = { selected -> selected?.let { randomSocialMedia = it } },
                testTag = SCHEDULE_RANDOM_SOCIAL_MEDIA_TAG,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = {
                    errorReporter.reporting {
                        randomMessage = store.randomSchedule(randomDay, randomSocialMedia)
                        schedules = store.schedules()
                    }
                },
                modifier = Modifier.testTag(SCHEDULE_RANDOM_TAG),
            ) {
                Text("Random")
            }
        }
        if (randomMessage.isNotBlank()) {
            Text(randomMessage, color = MaterialTheme.colorScheme.primary)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun scheduleSortSelect(
    selected: Direction?,
    onSelect: (Direction?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val label =
        when (selected) {
            null -> SCHEDULE_SORT_DEFAULT_LABEL
            Direction.ASC -> SCHEDULE_SORT_ASC_LABEL
            Direction.DESC -> SCHEDULE_SORT_DESC_LABEL
        }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.width(200.dp).testTag(SCHEDULE_SORT_TAG),
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sort by date") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            sortOption(SCHEDULE_SORT_DEFAULT_LABEL, "$SCHEDULE_SORT_TAG-DEFAULT") {
                onSelect(null)
                expanded = false
            }
            sortOption(SCHEDULE_SORT_ASC_LABEL, "$SCHEDULE_SORT_TAG-ASC") {
                onSelect(Direction.ASC)
                expanded = false
            }
            sortOption(SCHEDULE_SORT_DESC_LABEL, "$SCHEDULE_SORT_TAG-DESC") {
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
