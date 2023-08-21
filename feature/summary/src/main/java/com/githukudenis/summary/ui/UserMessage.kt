package com.githukudenis.summary.ui

data class UserMessage(
    val id: Long = 0,
    val message: String? = null,
    val messageType: MessageType = MessageType.INFO
)

sealed class MessageType {
    data object INFO: MessageType()
    data class ERROR(val dismissable: Boolean = true): MessageType()
}