package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

class ErrorReporter(
    private val onError: (Throwable) -> Unit = {},
) {
    fun report(error: Throwable) {
        onError(error)
    }
}

val LocalErrorReporter = staticCompositionLocalOf { ErrorReporter() }

@Suppress("TooGenericExceptionCaught")
inline fun ErrorReporter.reporting(block: () -> Unit) {
    try {
        block()
    } catch (error: Throwable) {
        report(error)
    }
}

@Suppress("TooGenericExceptionCaught")
inline fun <T> ErrorReporter.reporting(
    fallback: T,
    block: () -> T,
): T =
    try {
        block()
    } catch (error: Throwable) {
        report(error)
        fallback
    }

fun errorMessage(error: Throwable): String = error.message?.takeIf { it.isNotBlank() } ?: error::class.simpleName ?: "Unexpected error"

fun errorCause(error: Throwable): String? = error.cause?.let(::errorMessage)

@Composable
fun errorDialog(
    error: Throwable,
    onDismiss: () -> Unit,
) {
    val cause = errorCause(error)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        },
        title = { Text("Something went wrong") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(errorMessage(error))
                if (cause != null) {
                    Text("Cause: $cause", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
    )
}
