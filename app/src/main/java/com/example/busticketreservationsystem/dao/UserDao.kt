package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Query("UPDATE user_table SET username=:userName, emailId=:emailId, mobileNumber=:mobileNumber, password=:password, age=:age, gender=:gender WHERE userId LIKE :userId")
    fun updateUserData(userId: Int, userName: String, emailId: String, mobileNumber: String, password: String, age: Int, gender: String)

    @Query("SELECT count(*) FROM user_table WHERE mobileNumber=:mobileNumber")
    fun getAccountCount(mobileNumber: String): Int

    @Query("SELECT count(*) FROM user_table WHERE emailId=:email")
    fun getEmailCount(email: String): Int

    @Query("SELECT * FROM user_table WHERE mobileNumber LIKE :mobileNumber")
    fun getUserAccount(mobileNumber: String): User

    @Query("SELECT * FROM user_table WHERE userId LIKE :userId")
    fun getUserAccountByUserID(userId: Int): User

    @Query("SELECT password FROM user_table WHERE mobileNumber LIKE :mobileNumber")
    fun getPassword(mobileNumber: String): String

    @Query("UPDATE user_table SET password=:password WHERE mobileNumber LIKE :mobileNumber")
    fun updateUserPassword(password: String, mobileNumber: String)

}