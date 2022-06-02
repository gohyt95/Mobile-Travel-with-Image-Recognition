package com.example.mobiletravel.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiletravel.baseclass.User
import com.example.mobiletravel.database.getDatabase
import com.example.mobiletravel.network.TravelApi
import com.example.mobiletravel.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository = UserRepository(getDatabase(application))

    var user: LiveData<User>

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error

    init {
        user = userRepository.user
        _error.value = false
    }

    fun onSignIn(email: String, password: String){
        TravelApi.retrofitService.signin(email, password).enqueue(
            object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    viewModelScope.launch {
                        userRepository.setUser(response.body()!!)
                        _error.value = false
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.i("Signin error", "Failure :" + t.message)
                    _error.value = true
                }

            }
        )
    }

    suspend fun onSignOut(){
        userRepository.signOut()
        user = userRepository.userDao.getUser()
    }

    fun onSignUp(user:User){
        TravelApi.retrofitService.signup(user).enqueue(
            object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    viewModelScope.launch {
                        userRepository.setUser(response.body()!!)
                        _error.value = false
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.i("Signup error", "Failure :" + t.message)
                    _error.value = true
                }

            }
        )
    }

    fun onUpdatePassword(input: User){
        TravelApi.retrofitService.changePassword(input).enqueue(
            object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    viewModelScope.launch {
                        userRepository.updatePassword(response.body()!!.email, response.body()!!.password)
                        _error.value = false
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.i("Update password error", "Failure :" + t.message)
                    _error.value = true
                }

            }
        )
    }

    fun onUpdateProfile(input: User){
        TravelApi.retrofitService.updateProfile(input).enqueue(
            object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    viewModelScope.launch {
                        userRepository.
                        updateProfile(
                            response.body()!!.email, response.body()!!.dob,
                            response.body()!!.phone, response.body()!!.name)
                        _error.value = false
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.i("Update profile error", "Failure :" + t.message)
                    _error.value = true
                }

            }
        )
    }
}