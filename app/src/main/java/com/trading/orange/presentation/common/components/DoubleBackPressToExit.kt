package com.trading.orange.presentation.common.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoubleBackPressToExit(enabled: Boolean = true) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isBackPressed = remember { mutableStateOf(false) }
    BackHandler(enabled && !isBackPressed.value) {
        isBackPressed.value = true
        Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        scope.launch {
            delay(2000L)
            isBackPressed.value = false
        }
    }
}