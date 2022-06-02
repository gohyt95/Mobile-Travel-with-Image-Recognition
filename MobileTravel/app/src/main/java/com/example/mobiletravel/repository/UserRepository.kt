package com.example.mobiletravel.repository

import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.database.LocalDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val database: LocalDB) {

    val userDao = database.userDao

    val user = userDao.getUser()

    suspend fun setUser(user: User){
        withContext(Dispatchers.IO){
            database.userDao.insertUser(user)
        }
    }

    suspend fun signOut(){
        withContext(Dispatchers.IO) {
            database.userDao.deleteUser()
        }
    }

    suspend fun updatePassword(email: String, password: String){
        withContext(Dispatchers.IO){
            database.userDao.updatePassword(email, password)
        }
    }

    suspend fun updateProfile(email: String, dob:String, phone:String, name:String){
        withContext(Dispatchers.IO){
            database.userDao.updateProfile(email, dob, phone, name)
        }
    }
}