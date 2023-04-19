package com.example.busticketreservationsystem.cleanarchitecture.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.cleanarchitecture.data.local.entity.Chat

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertChat(chat: Chat)

    @Query("select * from chat_table where userId like :userId")
    fun getChatOfAUser(userId: Int): List<Chat>

    @Query("select distinct userId from chat_table")
    fun getUserList(): List<Int>

    @Query("select * from chat_table")
    fun getAllChat(): List<Chat>

    @Query("select timestamp from chat_table where userId like :userId")
    fun getTimeStampOfUserChat(userId: Int): List<String>

    @Query("select * from chat_table where messageId like :messageId")
    fun getSingleMessage(messageId: Int): Chat


}