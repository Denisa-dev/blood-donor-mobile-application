package com.example.firestoredatabase.models

import java.util.*

data class TextMessage(val text: String,
                       override val time: Date,
                       override val senderId: String,
                       override val recipientId: String,
                       override val senderName: String,
                       override val type: String = MessageType.TEXT)
    : Notification {
    constructor(): this("", Date(0), "", "", "")
}