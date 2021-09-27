package com.cem.ktorsample.data.model

data class MessageModel(
    val messageId: Long,
    val messageText: String,
    val fromUserEmail: String,
    val toUserEmail: String,
    val messageDateTime: String,
    val messageReadDateTime: String
)

data class NewMessageModel(
    val messageText: String,
    val fromUserId: Int,
    val toUserId: Int
)
