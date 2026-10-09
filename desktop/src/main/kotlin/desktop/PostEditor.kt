package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import application.post.PostLimits

class PostEditorState(
    initialText: String,
) {
    var text by mutableStateOf(initialText)
        internal set

    fun update(value: String) {
        text = value
    }

    fun clear() {
        text = ""
    }
}

@Composable
fun rememberPostEditorState(initialText: String): PostEditorState = remember(initialText) { PostEditorState(initialText) }

@Composable
fun postEditor(
    state: PostEditorState,
    modifier: Modifier = Modifier,
    charLimit: Int = PostLimits.MAX_CHARACTERS,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = state.text,
            onValueChange = state::update,
            label = { Text("Post text") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
        )
        Text(
            text = "${state.text.length} / $charLimit",
            style = MaterialTheme.typography.bodySmall,
            color =
                if (state.text.length > charLimit) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            modifier = Modifier.align(Alignment.End),
        )
    }
}
