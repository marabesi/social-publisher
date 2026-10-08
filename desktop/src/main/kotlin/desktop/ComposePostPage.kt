package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun composePostPage(store: SocialPublisherStore) {
    var postText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Compose a new post", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = postText,
            onValueChange = { postText = it },
            label = { Text("Post text") },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                message = store.createPost(postText)
                postText = ""
            },
        ) {
            Text("Create post")
        }

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}
