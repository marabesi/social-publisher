package desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadingState internal constructor(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val errorReporter: ErrorReporter,
) {
    var isLoading by mutableStateOf(false)
        private set

    @Suppress("TooGenericExceptionCaught")
    fun <T> run(
        block: () -> T,
        onSuccess: (T) -> Unit = {},
    ) {
        if (isLoading) return
        isLoading = true
        scope.launch {
            try {
                val outcome =
                    withContext(dispatcher) {
                        try {
                            Result.success(block())
                        } catch (cancellation: CancellationException) {
                            throw cancellation
                        } catch (error: Throwable) {
                            Result.failure(error)
                        }
                    }
                outcome.fold(
                    onSuccess = onSuccess,
                    onFailure = errorReporter::report,
                )
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun rememberLoadingState(dispatcher: CoroutineDispatcher = Dispatchers.IO): LoadingState {
    val scope = rememberCoroutineScope()
    val errorReporter = LocalErrorReporter.current
    return remember(scope, dispatcher, errorReporter) {
        LoadingState(scope = scope, dispatcher = dispatcher, errorReporter = errorReporter)
    }
}
