package com.example.busticketreservationsystem.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.busticketreservationsystem.cleanarchitecture.data.local.dao.ChatDao
import com.example.busticketreservationsystem.cleanarchitecture.data.local.entity.Chat
import com.example.busticketreservationsystem.data.entity.*
import com.example.busticketreservationsystem.data.dao.*


@Database(entities = [
            Bookings::class,
            Bus::class,
            BusAmenities::class,
            Partners::class,
            PassengerInformation::class,
            RecentlyViewed::class,
            Reviews::class,
            SeatInformation::class,
            User::class,
            Chat::class,
            BusLayout::class
                     ], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun bookingsDao(): BookingsDao
    abstract fun busAmenitiesDao(): BusAmenitiesDao
    abstract fun busDao(): BusDao
    abstract fun partnersDao(): PartnersDao
    abstract fun passengerInformationDao(): PassengerInformationDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun reviewsDao(): ReviewsDao
    abstract fun seatInformationDao(): SeatInformationDao
    abstract fun userDao(): UserDao
    abstract fun busLayoutDao(): BusLayoutDao
    abstract fun chatDao(): ChatDao

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}