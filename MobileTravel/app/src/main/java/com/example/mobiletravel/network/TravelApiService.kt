package com.example.mobiletravel.network

import com.example.mobiletravel.baseclass.Place
import com.example.mobiletravel.baseclass.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

//private const val BASE_URL = "http://192.168.68.107/travel/"
private const val BASE_URL = "https://travelimgreg.000webhostapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TravelApiService {
    @GET("home.php")
    fun getHomeAttraction(
        @Query(value="email") email: String?):
            Call<List<Place>>

    @GET("search.php")
    fun getSearchResult(
        @Query(value="keyword", encoded=true) keyword: String,
        @Query(value="email") email: String?):
            Call<List<Place>>

    @GET("keySearch.php")
    fun getImageResult(
        @Query(value="keyword", encoded=true) keyword: String,
        @Query(value="email") email: String?):
            Call<Place>

    @GET("nearby.php")
    fun getNearbyPlaces(
        @Query(value="lat") latitude: Double,
        @Query(value="long") longitude: Double,
        @Query(value="cat") cat: Int,
        @Query(value="email") email: String?):
            Call<List<Place>>

    @GET("allNearby.php")
    fun getAllNearbyPlaces(
        @Query(value="lat") latitude: Double,
        @Query(value="long") longitude: Double,
        @Query(value="email") email: String?):
            Call<List<Place>>

    @GET("signin.php")
    fun signin(
        @Query(value="email") email: String,
        @Query(value="password") password: String):
            Call<User>

    @POST("signup.php")
    fun signup(
        @Body user:User):
            Call<User>

    @PUT("changePw.php")
    fun changePassword(
        @Body user:User):
            Call<User>

    @PUT("updateProfile.php")
    fun updateProfile(
        @Body user:User):
            Call<User>

    @GET("bookmark.php")
    fun getSaved(
        @Query(value="email") email: String):
            Call<List<Place>>

    @FormUrlEncoded
    @POST("addBookmark.php")
    fun addBookmark(
        @Field("pid") pid:Int,
        @Field("email") email:String):
            Call<Int>

    @FormUrlEncoded
    @POST("removeBookmark.php")
    fun removeBookmark(
        @Field("pid") pid:Int,
        @Field("email") email:String):
            Call<Int>

    @FormUrlEncoded
    @POST("submitRating.php")
    fun submitRating(
        @Field("pid") pid:Int,
        @Field("email") email:String,
        @Field("star") star: Int):
            Call<Int>

    @FormUrlEncoded
    @POST("updateRating.php")
    fun updateRating(
        @Field("pid") pid:Int,
        @Field("email") email:String,
        @Field("star") star: Int):
            Call<Int>

}

object TravelApi {
    val retrofitService : TravelApiService by lazy {
        retrofit.create(TravelApiService::class.java) }
}

