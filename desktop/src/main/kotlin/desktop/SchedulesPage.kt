package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp

@Suppress("LongMethod")
@Composable
fun schedulesPage(store: SocialPublisherStore) {
    var schedules by remember { mutableStateOf(store.schedules()) }
    var postId by remember { mutableStateOf("") }
    var publishDate by remember { mutableStateOf("") }
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
                modifier = Modifier.width(120.dp),
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
                    message = store.schedule(postId, publishDate)
                    schedules = store.schedules()
                },
            ) {
                Text("Schedule")
            }
            Button(onClick = { schedules = store.schedules() }) {
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
                            text = "${item.id}. Post ${item.post.id} on ${item.publishDate}",
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(
                            onClick = {
                                store.deleteSchedule(item.id ?: "")
                                schedules = store.schedules()
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
