package com.example.phonehub

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhoneApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService by lazy {
        retrofit.create(PhoneApiService::class.java)
    }

    @Suppress("unused")
    val repository by lazy {
        PhoneRepository(database.phoneDao(), apiService)
    }
}