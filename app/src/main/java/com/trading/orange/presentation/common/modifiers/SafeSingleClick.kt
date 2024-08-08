package com.trading.orange.presentation.common.modifiers

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role

private class SafeClickHandler {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    fun handleClickEvent(clickEvent: () -> Unit) {
        if (now - lastEventTimeMs >= 300L) {
            clickEvent.invoke()
        }
        lastEventTimeMs = now
    }
}

fun Modifier.safeSingleClick(
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "safeSingleClick"
        properties["interactionSource"] = interactionSource
        properties["indication"] = indication
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    val clickHandler = remember { SafeClickHandler() }
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { clickHandler.handleClickEvent { onClick() } },
        role = role
    )
}

fun Modifier.safeSingleClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "safeSingleClick"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    val localIndication = LocalIndication.current
    val interactionSource = if (localIndication is IndicationNodeFactory) {
        // We can fast path here as it will be created inside clickable lazily
        null
    } else {
        // We need an interaction source to pass between the indication modifier and clickable, so
        // by creating here we avoid another composed down the line
        remember { MutableInteractionSource() }
    }
    val clickHandler = remember { SafeClickHandler() }
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = localIndication,
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { clickHandler.handleClickEvent { onClick() } },
        role = role
    )
}