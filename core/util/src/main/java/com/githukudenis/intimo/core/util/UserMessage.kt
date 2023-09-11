package com.githukudenis.intimo.core.util

data class UserMessage(
    val id: Long = 0,
    val message: String? = null,
    val messageType: MessageType = MessageType.INFO
)