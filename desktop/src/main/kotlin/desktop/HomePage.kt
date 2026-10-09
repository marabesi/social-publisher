package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

internal fun homeNavigationTag(page: AppPage): String = "home-navigate-${page.name}"

@Composable
fun homePage(
    onNavigate: (AppPage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("What do you want to do?", style = MaterialTheme.typography.titleLarge)
        AppPage.entries
            .filter { it != AppPage.HOME }
            .forEach { page ->
                Button(
                    onClick = { onNavigate(page) },
                    modifier = Modifier.fillMaxWidth().testTag(homeNavigationTag(page)),
                ) {
                    Icon(page.icon, contentDescription = page.title)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(page.title)
                }
            }
    }
}
