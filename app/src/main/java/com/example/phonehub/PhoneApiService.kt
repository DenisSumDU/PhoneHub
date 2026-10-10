package com.example.phonehub

import retrofit2.http.GET

data class PhoneDto(
    val id: String,
    val brand: String,
    val model: String,
    val price: Double,
    val ramGb: Int,
    val storageGb: Int,
    val batteryCapacity: Int,
    val imageUrl: String
)

interface PhoneApiService {
    @GET("v1/phones.json")
    suspend fun fetchPhones(): List<PhoneDto>
}