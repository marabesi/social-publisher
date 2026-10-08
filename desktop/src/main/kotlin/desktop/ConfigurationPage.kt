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
fun configurationPage(store: SocialPublisherStore) {
    var configurationJson by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Configuration", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = configurationJson,
            onValueChange = { configurationJson = it },
            label = { Text("JSON configuration") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
        )

        Button(onClick = { message = store.storeConfiguration(configurationJson) }) {
            Text("Store configuration")
        }

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}
