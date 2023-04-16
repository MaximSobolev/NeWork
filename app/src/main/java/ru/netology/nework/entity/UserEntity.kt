package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.User

@Entity
data class UserEntity (
    @PrimaryKey
    val id : Int,
    val login : String,
    val name : String,
    val avatar : String?
    ) {
    fun toDto() : User = User(id, login, name, avatar)

    companion object {
        fun fromDto(user: User) : UserEntity = UserEntity(user.id, user.login, user.name, user.avatar)
    }
}