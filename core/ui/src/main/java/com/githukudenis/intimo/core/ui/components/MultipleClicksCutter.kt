package com.githukudenis.intimo.core.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role

interface MultipleClicksCutter {
    fun processEvent(event: () -> Unit)

    companion object
}

fun MultipleClicksCutter.Companion.get(): MultipleClicksCutter = MultipleClicksCutterImpl()

class MultipleClicksCutterImpl : MultipleClicksCutter {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimestamp: Long = 0L

    override fun processEvent(event: () -> Unit) {
        if (now - lastEventTimestamp >= 300L) {
            event.invoke()
        }
        lastEventTimestamp = now
    }
}

fun Modifier.clickableOnce(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    val multipleClicksCutter = remember {
        MultipleClicksCutter.get()
    }
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = { multipleClicksCutter.processEvent { onClick() } },
        indication = LocalIndication.current,
        interactionSource = MutableInteractionSource()
    )
}