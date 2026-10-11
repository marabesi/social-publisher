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
import application.entities.BlueskyCredentials
import application.entities.GoogleDriveCredentials
import application.entities.LinkedInCredentials
import application.entities.NotionCredentials
import application.entities.SocialConfiguration
import application.entities.TwitterCredentials

private const val DEFAULT_STORAGE = "csv"
private const val DEFAULT_TIMEZONE = "UTC"

@Composable
fun configurationPage(store: SocialPublisherStore) {
    val errorReporter = LocalErrorReporter.current
    val loading = rememberLoadingState()
    val existing = remember { errorReporter.reporting(null) { store.configuration() } }
    var storage by remember { mutableStateOf(existing?.storage?.ifBlank { DEFAULT_STORAGE } ?: DEFAULT_STORAGE) }
    var timezone by remember { mutableStateOf(existing?.timezone?.ifBlank { DEFAULT_TIMEZONE } ?: DEFAULT_TIMEZONE) }
    var consumerKey by remember { mutableStateOf(existing?.twitter?.consumerKey.orEmpty()) }
    var consumerSecret by remember { mutableStateOf(existing?.twitter?.consumerSecret.orEmpty()) }
    var accessToken by remember { mutableStateOf(existing?.twitter?.accessToken.orEmpty()) }
    var accessTokenSecret by remember { mutableStateOf(existing?.twitter?.accessTokenSecret.orEmpty()) }
    var linkedinClientId by remember { mutableStateOf(existing?.linkedin?.clientId.orEmpty()) }
    var linkedinClientSecret by remember { mutableStateOf(existing?.linkedin?.clientSecret.orEmpty()) }
    var linkedinRedirectUri by remember { mutableStateOf(existing?.linkedin?.redirectUri.orEmpty()) }
    var linkedinAccessToken by remember { mutableStateOf(existing?.linkedin?.accessToken.orEmpty()) }
    var linkedinAuthorUrn by remember { mutableStateOf(existing?.linkedin?.authorUrn.orEmpty()) }
    var blueskyIdentifier by remember { mutableStateOf(existing?.bluesky?.identifier.orEmpty()) }
    var blueskyPassword by remember { mutableStateOf(existing?.bluesky?.password.orEmpty()) }
    var notionApiKey by remember { mutableStateOf(existing?.notion?.apiKey.orEmpty()) }
    var notionPageId by remember { mutableStateOf(existing?.notion?.pageId.orEmpty()) }
    var googleDriveAccessToken by remember { mutableStateOf(existing?.googleDrive?.accessToken.orEmpty()) }
    var googleDriveFolderId by remember { mutableStateOf(existing?.googleDrive?.folderId.orEmpty()) }
    var linkedinUrl by remember { mutableStateOf("") }
    var linkedinCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            pageTitle(title = "Configuration", loading = loading.isLoading)
            Button(
                enabled = !loading.isLoading,
                onClick = {
                    val configuration =
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
                            linkedin =
                                LinkedInCredentials(
                                    clientId = linkedinClientId,
                                    clientSecret = linkedinClientSecret,
                                    redirectUri = linkedinRedirectUri,
                                    accessToken = linkedinAccessToken,
                                    authorUrn = linkedinAuthorUrn,
                                ),
                            bluesky =
                                BlueskyCredentials(
                                    identifier = blueskyIdentifier,
                                    password = blueskyPassword,
                                ),
                            notion =
                                NotionCredentials(
                                    apiKey = notionApiKey,
                                    pageId = notionPageId,
                                ),
                            googleDrive =
                                GoogleDriveCredentials(
                                    accessToken = googleDriveAccessToken,
                                    folderId = googleDriveFolderId,
                                ),
                        )
                    loading.run(
                        block = { store.storeConfiguration(configuration) },
                        onSuccess = { message = it },
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
                supportingText = { Text("Use csv, notion or google_drive to upload the data files on every change.") },
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

            Text("LinkedIn credentials", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = linkedinClientId,
                onValueChange = { linkedinClientId = it },
                label = { Text("LinkedIn client id") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = linkedinClientSecret,
                onValueChange = { linkedinClientSecret = it },
                label = { Text("LinkedIn client secret") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = linkedinRedirectUri,
                onValueChange = { linkedinRedirectUri = it },
                label = { Text("LinkedIn redirect uri") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = linkedinAccessToken,
                onValueChange = { linkedinAccessToken = it },
                label = { Text("LinkedIn access token") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = linkedinAuthorUrn,
                onValueChange = { linkedinAuthorUrn = it },
                label = { Text("LinkedIn author urn") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Bluesky credentials", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = blueskyIdentifier,
                onValueChange = { blueskyIdentifier = it },
                label = { Text("Bluesky identifier") },
                supportingText = { Text("The handle or email used to log in to Bluesky.") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = blueskyPassword,
                onValueChange = { blueskyPassword = it },
                label = { Text("Bluesky password") },
                supportingText = { Text("Use an app password from Bluesky settings.") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Notion storage", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = notionApiKey,
                onValueChange = { notionApiKey = it },
                label = { Text("Notion API key") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = notionPageId,
                onValueChange = { notionPageId = it },
                label = { Text("Notion page id") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Google Drive storage", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = googleDriveAccessToken,
                onValueChange = { googleDriveAccessToken = it },
                label = { Text("Google Drive access token") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = googleDriveFolderId,
                onValueChange = { googleDriveFolderId = it },
                label = { Text("Google Drive folder id") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Connect LinkedIn", style = MaterialTheme.typography.titleMedium)
            Text(
                "Store the configuration first, then generate the URL, approve it in your browser and paste the code back.",
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = !loading.isLoading,
                    onClick = {
                        loading.run(
                            block = { store.linkedInAuthorizationUrl() },
                            onSuccess = { linkedinUrl = it },
                        )
                    },
                ) {
                    Text("Generate authorization URL")
                }
                Button(
                    enabled = linkedinUrl.startsWith("http"),
                    onClick = { openInBrowser(linkedinUrl) },
                ) {
                    Text("Open in browser")
                }
            }

            if (linkedinUrl.isNotBlank()) {
                OutlinedTextField(
                    value = linkedinUrl,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Authorization URL") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            OutlinedTextField(
                value = linkedinCode,
                onValueChange = { linkedinCode = it },
                label = { Text("Authorization code") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                enabled = !loading.isLoading,
                onClick = {
                    val code = linkedinCode
                    loading.run(
                        block = {
                            val response = store.connectLinkedIn(code)
                            response to store.configuration()?.linkedin
                        },
                        onSuccess = { (response, linkedin) ->
                            message = response
                            linkedinCode = ""
                            linkedinAccessToken = linkedin?.accessToken.orEmpty()
                            linkedinAuthorUrn = linkedin?.authorUrn.orEmpty()
                        },
                    )
                },
            ) {
                Text("Store token")
            }
        }
    }
}

private fun openInBrowser(url: String) {
    runCatching {
        if (java.awt.Desktop.isDesktopSupported()) {
            val desktop = java.awt.Desktop.getDesktop()
            desktop.browse(java.net.URI(url))
        }
    }
}

internal fun configurationFrom(
    fileName: String,
    storage: String,
    timezone: String,
    twitter: TwitterCredentials?,
    linkedin: LinkedInCredentials? = null,
    bluesky: BlueskyCredentials? = null,
    notion: NotionCredentials? = null,
    googleDrive: GoogleDriveCredentials? = null,
): SocialConfiguration {
    val credentials =
        twitter?.takeIf {
            it.consumerKey.isNotBlank() ||
                it.consumerSecret.isNotBlank() ||
                it.accessToken.isNotBlank() ||
                it.accessTokenSecret.isNotBlank()
        }

    val linkedinCredentials =
        linkedin?.takeIf {
            it.clientId.isNotBlank() ||
                it.clientSecret.isNotBlank() ||
                it.redirectUri.isNotBlank() ||
                it.accessToken.isNotBlank() ||
                it.authorUrn.isNotBlank()
        }

    val blueskyCredentials =
        bluesky?.takeIf {
            it.identifier.isNotBlank() ||
                it.password.isNotBlank()
        }

    val notionCredentials =
        notion?.takeIf {
            it.apiKey.isNotBlank() ||
                it.pageId.isNotBlank()
        }

    val googleDriveCredentials =
        googleDrive?.takeIf {
            it.accessToken.isNotBlank() ||
                it.folderId.isNotBlank()
        }

    return SocialConfiguration(
        fileName = fileName,
        storage = storage,
        twitter = credentials,
        linkedin = linkedinCredentials,
        bluesky = blueskyCredentials,
        notion = notionCredentials,
        googleDrive = googleDriveCredentials,
        timezone = timezone,
    )
}
