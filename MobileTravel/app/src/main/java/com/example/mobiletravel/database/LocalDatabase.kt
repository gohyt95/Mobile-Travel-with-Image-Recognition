package com.example.mobiletravel.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobiletravel.baseclass.User

@Dao
interface UserDao{
    @Query("select * from user")
    fun getUser(): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("delete from user")
    fun deleteUser()

    @Query("update user set password=(:password) where email=(:email)")
    fun updatePassword(email: String, password: String)

    @Query("update user set phone=(:phone), dob=(:dob), name=(:name) where email=(:email)")
    fun updateProfile(email: String, dob:String, phone:String, name:String)
}

@Database(entities = [User::class], version = 2)
abstract class LocalDB: RoomDatabase(){
    abstract val userDao: UserDao
}

private lateinit var INSTANCE: LocalDB

fun getDatabase(context: Context): LocalDB {
    synchronized(LocalDB::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            LocalDB::class.java,
            "userCredential").fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}