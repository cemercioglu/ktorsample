package com.cem.ktorsample.data.db.dao

import com.cem.ktorsample.data.model.MessageModel
import com.cem.ktorsample.data.model.UserModel
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable("users") {

    val email = varchar("email", 70).index()
    val firstName = varchar("firstName", 50)
    val lastName = varchar("lastName", 50)


}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var email by Users.email
    var firstName by Users.firstName
    var lastName by Users.lastName
}




object UsersDao {
    fun allUsers(): ArrayList<UserModel> {
        val users: ArrayList<UserModel> = arrayListOf()
        transaction {
            Users.selectAll().map {
                users.add(
                    UserModel(
                        it[Users.id].value,
                        it[Users.email],
                        it[Users.firstName],
                        it[Users.lastName],
                    )
                )
            }
        }
        return users
    }

    fun insertUser(user: UserModel): UserModel? {
        var userCem: User? = null
        transaction {
            val existsUser = Users.select { Users.email eq user.email }
            if (existsUser.count() == 0.toLong()) {
                userCem = User.new {
                    email = user.email
                    firstName = user.firstName
                    lastName = user.lastName
                }
            }
        }
        return if (userCem == null) {
            null
        } else {
            userCem!!.id.value.let { user.copy(id = it) }
        }
    }
    fun getUserById(id: Int): UserModel? {
        var userModel: UserModel? = null
        transaction {
            val select = Users.select { Users.id eq id }
                .map {
                    UserModel(
                        it[Users.id].value,
                        it[Users.email],
                        it[Users.firstName],
                        it[Users.lastName],
                    )
                }
            if (select.isNotEmpty())
                userModel = select.first()
        }
        return userModel
    }

}

