package com.example.simpletodolist.ui.components
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.utils.toFormattedDateTimeFromMillis

private const val MAX_CHAR_LIMIT = 256

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TextDialog(
    currentText: String,
    onTextChanged: (String) -> Unit,
    onSave: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialTimeMillis: Long? = null
) {
    val charCount = currentText.length

    var selectedReminderTime by remember {
        mutableStateOf(initialTimeMillis)
    }

    var showDateTimePicker by remember {
        mutableStateOf(false)
    }

    val reminderText = remember(selectedReminderTime) {
        if (selectedReminderTime != null) {
            selectedReminderTime!!.toFormattedDateTimeFromMillis()
        } else {
            "Reminder"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(13.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Column {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            value = currentText,
                            onValueChange = {
                                if (it.length <= MAX_CHAR_LIMIT) {
                                    onTextChanged(it)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp
                            ),
                            maxLines = 5,
                            placeholder = {
                                Text(
                                    text = "Enter the title of the task"
                                )
                            }
                        )
                        Text(
                            text = "$charCount/$MAX_CHAR_LIMIT",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 27.dp)
                        .padding(
                            top = 20.dp,
                            bottom = 20.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .clip(RoundedCornerShape(7.dp))
                            .clickable(
                                onClick = {
                                    showDateTimePicker = true
                                },
                                enabled = currentText.isNotBlank()
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp).offset(y = (-1).dp),
                                tint = if (charCount > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            Text(
                                text = reminderText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (charCount > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }

                    TextButton(
                        onClick = { onSave(selectedReminderTime) },
                        enabled = currentText.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Done",
                            color = if (charCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showDateTimePicker) {
        DateTimePickerDialog(
            onDismiss = { showDateTimePicker = false },
            onDateTimeSelected = { millis ->
                selectedReminderTime = millis
            },
            initialTimeMillis = selectedReminderTime
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTodoDialogWithTextPreview() {
    SimpleTodoListTheme {
        TextDialog(
            currentText = "",
            onTextChanged = {},
            onSave = {},
            onDismiss = {},
        )
    }
}