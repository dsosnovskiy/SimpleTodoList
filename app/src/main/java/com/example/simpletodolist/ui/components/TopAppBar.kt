package com.example.simpletodolist.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletodolist.R
import com.example.simpletodolist.ui.theme.topBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    isSelectionModeEnabled: Boolean,
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onSelectAll: () -> Unit,
) {
    if (isSelectionModeEnabled) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = if (selectedCount > 0) "Selected: ${selectedCount}" else "Select objects",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            colors = TopAppBarColors(
                containerColor = topBarColor,
                scrolledContainerColor = topBarColor,
                navigationIconContentColor = topBarColor,
                titleContentColor = Color.White,
                actionIconContentColor = topBarColor
            ),
            navigationIcon = {
                IconButton(onClick = { onClearSelection() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSelectAll() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_select_all),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            modifier = Modifier
                .padding(bottom = 5.dp)
                .clip(RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)),
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "My Todos",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            colors = TopAppBarColors(
                containerColor = topBarColor,
                scrolledContainerColor = topBarColor,
                navigationIconContentColor = topBarColor,
                titleContentColor = Color.White,
                actionIconContentColor = topBarColor
            ),
            modifier = Modifier
                .padding(bottom = 5.dp)
                .clip(RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)),
        )
    }
}