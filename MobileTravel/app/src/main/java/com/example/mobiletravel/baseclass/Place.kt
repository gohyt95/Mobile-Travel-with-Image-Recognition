package com.example.mobiletravel.baseclass

data class Place (
    val pid: Int,
    val name: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val placeid: String?,
    val phone: String?,
    val openHour: String?,
    val closeHour: String?,
    val category: Int,
    val avgRating: String?,
    val ratingCount: Int,
    val email: String?,
    val imageLink: String?,
    var rating: Int?,
    var bookmark: Int?
    ) {
}