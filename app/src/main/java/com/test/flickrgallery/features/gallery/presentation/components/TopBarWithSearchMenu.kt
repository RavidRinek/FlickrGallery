import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.test.flickrgallery.R

@Composable
fun TopBarWithSearchMenu(
    searchText: String,
    autoPollEnabled: Boolean,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleChanged: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(searchText, TextRange(searchText.length)))
    }

    LaunchedEffect(searchText) {
        if (searchText != textFieldValue.text) {
            textFieldValue = TextFieldValue(searchText, TextRange(searchText.length))
        }
    }

    Surface(
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onSearchChange(it.text)
                },
                placeholder = { Text("Search Flickr...") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(x = (-4).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Clear Search") },
                        onClick = {
                            menuExpanded = false
                            onClearSearch()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("${if (autoPollEnabled) "Stop" else "Start"} polling")
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleChanged()
                        }
                    )
                }
            }
        }
    }
}