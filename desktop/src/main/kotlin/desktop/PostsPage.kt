package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import application.entities.SocialPosts

private const val ID_COLUMN_WEIGHT = 0.12f
private const val TEXT_COLUMN_WEIGHT = 0.58f
private const val ACTIONS_COLUMN_WEIGHT = 0.3f

@Composable
fun postsPage(
    store: SocialPublisherStore,
    onEdit: (SocialPosts) -> Unit = {},
) {
    var posts by remember { mutableStateOf(store.posts().toList()) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Posts", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { posts = store.posts().toList() }) {
                Text("Refresh")
            }
        }

        if (message.isNotBlank()) {
            Text(message)
        }

        if (posts.isEmpty()) {
            Text("No posts yet")
        } else {
            postsTable(
                posts = posts,
                onEdit = onEdit,
                onRemove = {
                    message = store.deletePost(it.id ?: "")
                    posts = store.posts().toList()
                },
            )
        }
    }
}

@Composable
private fun postsTable(
    posts: List<SocialPosts>,
    onEdit: (SocialPosts) -> Unit,
    onRemove: (SocialPosts) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            tableHeader("Id", ID_COLUMN_WEIGHT)
            tableHeader("Text", TEXT_COLUMN_WEIGHT)
            tableHeader("Actions", ACTIONS_COLUMN_WEIGHT)
        }
        HorizontalDivider()
        LazyColumn {
            items(posts) { post ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = post.id.orEmpty(),
                        modifier = Modifier.weight(ID_COLUMN_WEIGHT),
                    )
                    Text(
                        text = post.text,
                        modifier = Modifier.weight(TEXT_COLUMN_WEIGHT),
                    )
                    Row(
                        modifier = Modifier.weight(ACTIONS_COLUMN_WEIGHT),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        TextButton(onClick = { onEdit(post) }) {
                            Text("Edit")
                        }
                        TextButton(onClick = { onRemove(post) }) {
                            Text("Remove")
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RowScope.tableHeader(
    text: String,
    weight: Float,
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
    )
}
