package desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

class RichTextEditorState(
    initialText: String,
) {
    var value by mutableStateOf(TextFieldValue(initialText))
        internal set

    fun update(newValue: TextFieldValue) {
        value = newValue
    }

    fun toggleBold() = toggle(SpanStyle(fontWeight = FontWeight.Bold))

    fun toggleItalic() = toggle(SpanStyle(fontStyle = FontStyle.Italic))

    fun toggleUnderline() = toggle(SpanStyle(textDecoration = TextDecoration.Underline))

    fun text(): String = value.text

    fun clear() {
        value = TextFieldValue()
    }

    private fun toggle(style: SpanStyle) {
        val start = value.selection.min
        val end = value.selection.max
        if (start == end) {
            return
        }

        val current = value.annotatedString
        val spanStyles = current.spanStyles.toMutableList()
        val isApplied = spanStyles.any { it.item == style && it.start <= start && it.end >= end }

        if (isApplied) {
            spanStyles.removeAll { it.item == style && it.start >= start && it.end <= end }
        } else {
            spanStyles.add(AnnotatedString.Range(style, start, end))
        }

        value =
            value.copy(
                annotatedString = AnnotatedString(current.text, spanStyles),
                selection = TextRange(start, end),
            )
    }
}

@Composable
fun rememberRichTextEditorState(initialText: String): RichTextEditorState = remember(initialText) { RichTextEditorState(initialText) }

@Composable
fun richTextEditor(
    state: RichTextEditorState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = state::toggleBold) {
                Text("Bold", fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = state::toggleItalic) {
                Text("Italic", fontStyle = FontStyle.Italic)
            }
            TextButton(onClick = state::toggleUnderline) {
                Text("Underline", textDecoration = TextDecoration.Underline)
            }
        }

        OutlinedTextField(
            value = state.value,
            onValueChange = state::update,
            label = { Text("Post text") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
        )
    }
}
