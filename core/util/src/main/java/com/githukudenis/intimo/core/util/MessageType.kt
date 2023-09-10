package com.githukudenis.intimo.core.util

sealed class MessageType {
    data object INFO: MessageType()
    data class ERROR(val dismissable: Boolean = true): MessageType()
}