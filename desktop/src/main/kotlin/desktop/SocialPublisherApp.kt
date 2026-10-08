package desktop

import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import application.Output
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun socialPublisherApp() {
    desktopAppShell(store = rememberDesktopStore())
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionNaming", "LongMethod")
@Composable
fun desktopAppShell(store: SocialPublisherStore) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedPage by remember { mutableStateOf(AppPage.POSTS) }

    MaterialTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    AppPage.entries.forEach { page ->
                        NavigationDrawerItem(
                            label = { Text(page.title) },
                            selected = selectedPage == page,
                            onClick = {
                                selectedPage = page
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(page.icon, contentDescription = page.title) },
                        )
                    }
                }
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selectedPage.title) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                    )
                },
            ) { padding ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                ) {
                    when (selectedPage) {
                        AppPage.POSTS -> postsPage(store)
                        AppPage.SCHEDULES -> schedulesPage(store)
                        AppPage.COMPOSE -> composePostPage(store)
                        AppPage.CONFIGURATION -> configurationPage(store)
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberDesktopStore(): SocialPublisherStore =
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

private fun restOutput(): Output =
    object : Output {
        override fun write(arguments: String): String = arguments
    }
