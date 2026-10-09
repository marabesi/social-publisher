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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts
import application.post.PostSearch

private const val ID_COLUMN_WEIGHT = 0.12f
private const val TEXT_COLUMN_WEIGHT = 0.58f
private const val ACTIONS_COLUMN_WEIGHT = 0.3f

internal const val POSTS_SEARCH_TEXT_TAG = "postsSearchText"
internal const val POSTS_SEARCH_IDS_TAG = "postsSearchIds"
internal const val POSTS_SOCIAL_MEDIA_TAG = "postsSocialMedia"

@Suppress("LongMethod")
@Composable
fun postsPage(
    store: SocialPublisherStore,
    onEdit: (SocialPosts) -> Unit = {},
) {
    val errorReporter = LocalErrorReporter.current
    var posts by remember {
        mutableStateOf(errorReporter.reporting(emptyList<SocialPosts>()) { store.posts().toList() })
    }
    var schedules by remember {
        mutableStateOf(errorReporter.reporting(emptyList<ScheduledItem>()) { store.schedules().toList() })
    }
    var textQuery by remember { mutableStateOf("") }
    var idsQuery by remember { mutableStateOf("") }
    var socialMedia by remember { mutableStateOf<SocialMedia?>(null) }
    var message by remember { mutableStateOf("") }

    val filters = PostSearch.from(text = textQuery, ids = idsQuery, socialMedia = socialMedia)
    val visiblePosts = filters.filter(posts, schedules)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Posts", style = MaterialTheme.typography.titleLarge)
            Button(
                onClick = {
                    posts = errorReporter.reporting(posts) { store.posts().toList() }
                    schedules = errorReporter.reporting(schedules) { store.schedules().toList() }
                },
            ) {
                Text("Refresh")
            }
        }

        postsSearch(
            textQuery = textQuery,
            onTextChange = { textQuery = it },
            idsQuery = idsQuery,
            onIdsChange = { idsQuery = it },
            socialMedia = socialMedia,
            onSocialMediaChange = { socialMedia = it },
        )

        if (message.isNotBlank()) {
            Text(message)
        }

        when {
            posts.isEmpty() -> Text("No posts yet")
            visiblePosts.isEmpty() -> Text("No posts match your search")
            else ->
                postsTable(
                    posts = visiblePosts,
                    onEdit = onEdit,
                    onRemove = {
                        errorReporter.reporting {
                            message = store.deletePost(it.id ?: "")
                            posts = store.posts().toList()
                            schedules = store.schedules().toList()
                        }
                    },
                )
        }
    }
}

@Composable
private fun postsSearch(
    textQuery: String,
    onTextChange: (String) -> Unit,
    idsQuery: String,
    onIdsChange: (String) -> Unit,
    socialMedia: SocialMedia?,
    onSocialMediaChange: (SocialMedia?) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = textQuery,
            onValueChange = onTextChange,
            label = { Text("Search text") },
            singleLine = true,
            modifier = Modifier.weight(1f).testTag(POSTS_SEARCH_TEXT_TAG),
        )
        OutlinedTextField(
            value = idsQuery,
            onValueChange = onIdsChange,
            label = { Text("Post ids (comma separated)") },
            singleLine = true,
            modifier = Modifier.weight(1f).testTag(POSTS_SEARCH_IDS_TAG),
        )
        socialMediaSelect(
            selected = socialMedia,
            onSelect = onSocialMediaChange,
            label = "Social network",
            testTag = POSTS_SOCIAL_MEDIA_TAG,
            includeAllOption = true,
            modifier = Modifier.weight(1f),
        )
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
