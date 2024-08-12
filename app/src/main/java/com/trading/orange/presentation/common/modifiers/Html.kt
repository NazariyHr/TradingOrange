package com.trading.orange.presentation.common.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.AnnotatedString
import com.mohamedrejeb.richeditor.model.rememberRichTextState

@Composable
fun String.toHtmlString(): AnnotatedString {
    val state = rememberRichTextState()

    LaunchedEffect(this) {
        state.setHtml(this@toHtmlString)
    }

    return state.annotatedString
}