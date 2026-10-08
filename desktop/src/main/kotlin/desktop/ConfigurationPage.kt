package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import application.entities.SocialConfiguration
import application.entities.TwitterCredentials

private const val DEFAULT_STORAGE = "csv"
private const val DEFAULT_TIMEZONE = "UTC"

@Composable
fun configurationPage(store: SocialPublisherStore) {
    val existing = remember { store.configuration() }
    var storage by remember { mutableStateOf(existing?.storage?.ifBlank { DEFAULT_STORAGE } ?: DEFAULT_STORAGE) }
    var timezone by remember { mutableStateOf(existing?.timezone?.ifBlank { DEFAULT_TIMEZONE } ?: DEFAULT_TIMEZONE) }
    var consumerKey by remember { mutableStateOf(existing?.twitter?.consumerKey.orEmpty()) }
    var consumerSecret by remember { mutableStateOf(existing?.twitter?.consumerSecret.orEmpty()) }
    var accessToken by remember { mutableStateOf(existing?.twitter?.accessToken.orEmpty()) }
    var accessTokenSecret by remember { mutableStateOf(existing?.twitter?.accessTokenSecret.orEmpty()) }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Configuration", style = MaterialTheme.typography.titleLarge)
            Button(
                onClick = {
                    message =
                        store.storeConfiguration(
                            configurationFrom(
                                fileName = existing?.fileName.orEmpty(),
                                storage = storage,
                                timezone = timezone,
                                twitter =
                                    TwitterCredentials(
                                        consumerKey = consumerKey,
                                        consumerSecret = consumerSecret,
                                        accessToken = accessToken,
                                        accessTokenSecret = accessTokenSecret,
                                    ),
                            ),
                        )
                },
            ) {
                Text("Store configuration")
            }
        }

        if (message.isNotBlank()) {
            Text(message)
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Edit the stored configuration. The file name is managed automatically and cannot be changed here.",
                style = MaterialTheme.typography.bodyMedium,
            )

            OutlinedTextField(
                value = storage,
                onValueChange = { storage = it },
                label = { Text("Storage") },
                supportingText = { Text("Only csv is currently supported.") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = timezone,
                onValueChange = { timezone = it },
                label = { Text("Timezone") },
                supportingText = { Text("For example UTC or Europe/Madrid.") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("X (Twitter) credentials", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = consumerKey,
                onValueChange = { consumerKey = it },
                label = { Text("Consumer key") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = consumerSecret,
                onValueChange = { consumerSecret = it },
                label = { Text("Consumer secret") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = accessToken,
                onValueChange = { accessToken = it },
                label = { Text("Access token") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = accessTokenSecret,
                onValueChange = { accessTokenSecret = it },
                label = { Text("Access token secret") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

internal fun configurationFrom(
    fileName: String,
    storage: String,
    timezone: String,
    twitter: TwitterCredentials?,
): SocialConfiguration {
    val credentials =
        twitter?.takeIf {
            it.consumerKey.isNotBlank() ||
                it.consumerSecret.isNotBlank() ||
                it.accessToken.isNotBlank() ||
                it.accessTokenSecret.isNotBlank()
        }

    return SocialConfiguration(
        fileName = fileName,
        storage = storage,
        twitter = credentials,
        timezone = timezone,
    )
}
