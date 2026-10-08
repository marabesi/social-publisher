package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import application.entities.SocialPosts

@Composable
fun composePostPage(
    store: SocialPublisherStore,
    postToEdit: SocialPosts? = null,
    onFinish: () -> Unit = {},
) {
    val editingPost = postToEdit
    val isEditing = editingPost != null
    val editorState = rememberRichTextEditorState(editingPost?.text ?: "")
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = if (isEditing) "Edit post" else "Compose a new post",
            style = MaterialTheme.typography.titleLarge,
        )

        richTextEditor(state = editorState, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (isEditing) {
                        message = store.updatePost(editingPost.id ?: "", editorState.text())
                        onFinish()
                    } else {
                        message = store.createPost(editorState.text())
                        editorState.clear()
                    }
                },
            ) {
                Text(if (isEditing) "Save" else "Create post")
            }

            if (isEditing) {
                TextButton(onClick = onFinish) {
                    Text("Cancel")
                }
            }
        }

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}
