package com.cem.ktorsample.data.db.dao

import com.cem.ktorsample.data.model.MessageModel
import com.cem.ktorsample.data.model.NewMessageModel
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


object Messages : LongIdTable("messages") {

    val text = varchar("text", 255)
    var fromUser = reference("from_user", Users)
    var toUser = reference("to_user", Users)
    val insertDate = datetime("insert_date").nullable()
    val readDate = datetime("read_date").nullable()

}

class Message(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Message>(Messages)

    var text by Messages.text
    var fromUser by User referencedOn Messages.fromUser
    var toUser by User referencedOn Messages.toUser
    var insertDate by Messages.insertDate
    val readDate by Messages.readDate
}

fun allMessages(): ArrayList<MessageModel> {
    val messages: ArrayList<MessageModel> = arrayListOf()
    transaction {

        val fromUser = Users.alias("u1")
        val toUser = Users.alias("u2")
        Messages
            .join(fromUser,
                JoinType.INNER,
                additionalConstraint = { Messages.fromUser eq fromUser[Users.id] })
            .join(toUser,
                JoinType.INNER,
                additionalConstraint = { Messages.toUser eq toUser[Users.id] })
            .slice(
                Messages.id,
                Messages.text,
                Messages.insertDate,
                Messages.readDate,
                fromUser[Users.email],
                toUser[Users.email],
            )
            .selectAll().forEach {
                messages.add(
                    MessageModel(
                        it[Messages.id].value,
                        it[Messages.text],
                        it[fromUser[Users.email]],
                        it[toUser[Users.email]],
                        it[Messages.insertDate].toString(),
                        it[Messages.readDate].toString(),
                    )
                )
            }

    }
    return messages
}

fun getMessage(id: Long): MessageModel? {
    var messageUser: MessageModel? = null
    transaction {
        val select = Messages.select { Messages.id eq id }
            .map {
                MessageModel(
                    messageId = it[Messages.id].value,
                    it[Messages.text],
                    "",
                    "",
                    it[Messages.insertDate].toString(),
                    it[Messages.readDate].toString()
                )
            }
        if (select.isNotEmpty())
            messageUser = select.first()

    }
    return messageUser

}

fun insertMessage(newMessageModel: NewMessageModel): MessageModel? {
    var messageModel: MessageModel? = null
    transaction {
        val fromUserQuery = Users.select { Users.id eq newMessageModel.fromUserId }
        val toUserQuery = Users.select { Users.id eq newMessageModel.toUserId }

        if (fromUserQuery.count() > 0 && toUserQuery.count() > 0) {

            val from = User[newMessageModel.fromUserId]
            val to = User[newMessageModel.toUserId]
            val messageCem = Message.new {
                text = newMessageModel.messageText
                fromUser = from
                toUser = to
                insertDate = LocalDateTime.now()

            }
            messageModel = MessageModel(
                messageCem.id.value,
                messageCem.text,
                messageCem.fromUser.email,
                messageCem.toUser.email,
                messageCem.insertDate.toString(),
                messageCem.readDate.toString(),
            )
        }

    }

    return messageModel
}