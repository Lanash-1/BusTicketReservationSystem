package com.example.busticketreservationsystem.data.dao

import androidx.room.*
import com.example.busticketreservationsystem.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Query("UPDATE user_table SET username=:userName, emailId=:emailId, mobileNumber=:mobileNumber, password=:password, dob=:age, gender=:gender WHERE userId LIKE :userId")
    fun updateUserData(userId: Int, userName: String, emailId: String, mobileNumber: String, password: String, age: String, gender: String)

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

    @Delete
    fun deleteUserAccount(user: User)

    @Query("select count(*) from user_table")
    fun getUserCount(): Int

}