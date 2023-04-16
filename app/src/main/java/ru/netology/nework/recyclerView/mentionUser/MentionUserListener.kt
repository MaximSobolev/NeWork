package ru.netology.nework.recyclerView.mentionUser

import ru.netology.nework.dto.User

interface MentionUserListener {
    fun chooseUser(user : User)
}