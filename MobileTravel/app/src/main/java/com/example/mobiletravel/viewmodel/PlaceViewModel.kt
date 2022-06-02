package com.example.mobiletravel.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobiletravel.network.TravelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.mobiletravel.baseclass.Place

class PlaceViewModel:ViewModel() {

    private val _selectedPlace = MutableLiveData<Place?>()
    val selectedPlace: LiveData<Place?>
        get() = _selectedPlace

    private val _homeAttraction =  MutableLiveData<List<Place>>()
    val homeAttraction: LiveData<List<Place>>
        get() = _homeAttraction

    private val _searchResult =  MutableLiveData<List<Place>>()
    val searchResult: LiveData<List<Place>>
        get() = _searchResult

    private val _nearbyPlaces = MutableLiveData<List<Place>>()
    val nearbyPlaces: LiveData<List<Place>>
        get() = _nearbyPlaces

    private val _bookmarkPlaces = MutableLiveData<List<Place>?>()
    val bookmarkPlaces: MutableLiveData<List<Place>?>
        get() = _bookmarkPlaces

    fun getHomeAttraction(email: String?){

        TravelApi.retrofitService.getHomeAttraction(email).enqueue(
            object : Callback<List<Place>> {
                override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                    _homeAttraction.value = response.body()
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    Log.i("home error", "Failure :" + t.message)

                }

            }
        )

    }

    fun getSearchResult(searchQuery: String,email: String?){
        TravelApi.retrofitService.getSearchResult(searchQuery,email).enqueue(
            object : Callback<List<Place>> {
                override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                    _searchResult.value = response.body()
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    Log.i("search error", "Failure :" + t.message)

                }

            }
        )
    }

    fun getImageResult(searchQuery: String,email: String?){
        TravelApi.retrofitService.getImageResult(searchQuery,email).enqueue(
            object : Callback<Place> {
                override fun onResponse(call: Call<Place>, response: Response<Place>) {
                    _selectedPlace.value = response.body()
                }

                override fun onFailure(call: Call<Place>, t: Throwable) {
                    Log.i("Image result error", "Failure :" + t.message)

                }

            }
        )
    }

    fun getNearbyPlace(latitude: Double, longitude: Double, cat: Int,email: String?){
        TravelApi.retrofitService.getNearbyPlaces(latitude, longitude, cat, email).enqueue(
            object : Callback<List<Place>> {
                override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                    _nearbyPlaces.value = response.body()
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    Log.i("nearby error", "Failure :" + t.message)

                }

            }
        )
    }

    fun getAllNearbyPlaces(latitude: Double, longitude: Double, email: String?){
        TravelApi.retrofitService.getAllNearbyPlaces(latitude, longitude, email).enqueue(
            object : Callback<List<Place>> {
                override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                    _nearbyPlaces.value = response.body()
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    Log.i("map error", "Failure :" + t.message)

                }

            }
        )
    }

    fun getSaved(email: String){
        TravelApi.retrofitService.getSaved(email).enqueue(
            object : Callback<List<Place>>{
                override fun onResponse(call: Call<List<Place>>, response: Response<List<Place>>) {
                    if(response.body().isNullOrEmpty()){
                        _bookmarkPlaces.value = listOf()
                        Log.i("bookmark error", "it is null")
                    }
                    else
                        _bookmarkPlaces.value = response.body()
                }

                override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                    _bookmarkPlaces.value = listOf()
                    Log.i("Get bookmark error", "Failure :" + t.message)
                }

            }
        )
    }

    fun addBookmark(pid:Int,email:String){
        TravelApi.retrofitService.addBookmark(pid, email).enqueue(
            object : Callback<Int>{
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    _selectedPlace.value!!.bookmark = response.body()
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.i("Add bookmark error", "Failure :" + t.message)
                }

            }
        )
    }

    fun removeBookmark(pid:Int,email:String){
        TravelApi.retrofitService.removeBookmark(pid, email).enqueue(
            object : Callback<Int>{
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    _selectedPlace.value!!.bookmark = response.body()
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.i("Remove bookmark error", "Failure :" + t.message)
                }

            }
        )
    }

    fun submitRating(pid:Int, email:String, star: Int){
        TravelApi.retrofitService.submitRating(pid,email, star).enqueue(
            object : Callback<Int>{
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    _selectedPlace.value!!.rating = response.body()
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.i("Submit rating error", "Failure :" + t.message)
                }

            }
        )
    }

    fun updateRating(pid:Int, email:String, star: Int){
        TravelApi.retrofitService.updateRating(pid,email, star).enqueue(
            object : Callback<Int>{
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    _selectedPlace.value!!.rating = response.body()
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.i("update rating error", "Failure :" + t.message)
                }

            }
        )
    }

    fun clearOnSignOut(){
        _bookmarkPlaces.value = null
    }

    fun setSelectedPlace(data: Place){
        _selectedPlace.value = data
    }

    fun clearSelectedPlace(){
        _selectedPlace.value = null
    }
}