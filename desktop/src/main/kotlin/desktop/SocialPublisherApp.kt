package desktop

import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import application.Output

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming", "LongMethod")
@Composable
fun socialPublisherApp() {
    val store =
        remember {
            val configurationRepository = FileSystemConfigurationRepository()
            val postsRepository = FileSystemPostRepository(configurationRepository = configurationRepository)
            val schedulerRepository =
                FileSystemSchedulerRepository(
                    postsRepository = postsRepository,
                    configurationRepository = configurationRepository,
                )
            SocialPublisherStore(
                postsRepository = postsRepository,
                schedulerRepository = schedulerRepository,
                configurationRepository = configurationRepository,
                output = restOutput(),
            )
        }

    var posts by remember { mutableStateOf(store.posts()) }
    var schedules by remember { mutableStateOf(store.schedules()) }
    var message by remember { mutableStateOf("") }
    var postText by remember { mutableStateOf("") }
    var postId by remember { mutableStateOf("") }
    var publishDate by remember { mutableStateOf("") }
    var configurationJson by remember { mutableStateOf("") }

    fun refresh() {
        posts = store.posts()
        schedules = store.schedules()
    }

    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Social Publisher") }) },
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (message.isNotBlank()) {
                    Text(message)
                }

                Text("Configuration", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = configurationJson,
                    onValueChange = { configurationJson = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("JSON configuration") },
                    maxLines = 3,
                )
                Button(
                    onClick = {
                        message = store.storeConfiguration(configurationJson)
                    },
                ) {
                    Text("Store configuration")
                }

                Text("Posts", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = postText,
                    onValueChange = { postText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Post text") },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            message = store.createPost(postText)
                            postText = ""
                            refresh()
                        },
                    ) {
                        Text("Create post")
                    }
                    TextButton(onClick = ::refresh) {
                        Text("Refresh")
                    }
                }
                posts.forEach {
                    Text("${it.id}. ${it.text}")
                }

                Text("Schedules", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = postId,
                        onValueChange = { postId = it },
                        label = { Text("Post id") },
                    )
                    OutlinedTextField(
                        value = publishDate,
                        onValueChange = { publishDate = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Publish date") },
                    )
                }
                Button(
                    onClick = {
                        message = store.schedule(postId, publishDate)
                        refresh()
                    },
                ) {
                    Text("Schedule")
                }
                schedules.forEach { item ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${item.id}. Post ${item.post.id} on ${item.publishDate}")
                        TextButton(
                            onClick = {
                                store.deleteSchedule(item.id ?: "")
                                message = "Schedule ${item.id} has been removed"
                                refresh()
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

private fun restOutput(): Output =
    object : Output {
        override fun write(arguments: String): String = arguments
    }
