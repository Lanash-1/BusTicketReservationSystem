package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.Chat

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertChat(chat: Chat)

    @Query("select * from chat_table where userId like :userId")
    fun getChatOfAUser(userId: Int): List<Chat>

    @Query("select distinct userId from chat_table")
    fun getUserList(): List<Int>

}