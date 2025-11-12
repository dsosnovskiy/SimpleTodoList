package com.example.simpletodolist.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.wear.compose.material3.Text
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.example.simpletodolist.utils.toFormattedDateTimeFromMillis
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long?) -> Unit,
    initialTimeMillis: Long?
) {
    val initialDateTime = remember {
        if (initialTimeMillis != null && initialTimeMillis > 0) {
            Instant.ofEpochMilli(initialTimeMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } else {
            LocalDateTime.now()
        }
    }

    var selectedDateTime by remember {
        mutableStateOf(initialDateTime)
    }

    val reminderDateText = remember(selectedDateTime) {
        selectedDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
            .toFormattedDateTimeFromMillis()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Reminder",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = reminderDateText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )

                WheelDateTimePicker(
                    modifier = Modifier
                        .padding(vertical = 20.dp),
                    startDateTime = selectedDateTime,
                    minDateTime = LocalDateTime.now(),
                ) { snappedDateTime ->
                    selectedDateTime = snappedDateTime
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier
                            .width(130.dp),
                        onClick = {
                            onDateTimeSelected(null)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(
                        modifier = Modifier
                            .width(130.dp),
                        onClick = {
                            val selectedMillis = selectedDateTime
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()

                            onDateTimeSelected(selectedMillis)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        )
                    ) {
                        Text(
                            text = "OK",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview()
@Composable
fun DateTimePickerDialogPreview() {
    DateTimePickerDialog(
        onDismiss = { },
        onDateTimeSelected = { _ ->},
        initialTimeMillis = null
    )
}