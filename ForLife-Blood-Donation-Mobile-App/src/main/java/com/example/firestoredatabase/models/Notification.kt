package com.example.firestoredatabase.models

import java.util.*

object MessageType{
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Notification{
    val time: Date
    val senderId: String
    val recipientId: String
    val senderName: String
    val type: String
}