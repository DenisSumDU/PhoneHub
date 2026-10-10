package com.example.phonehub

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneDao {
    @Query("SELECT * FROM phones WHERE model LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY price DESC")
    fun searchPhones(query: String): Flow<List<PhoneEntity>>

    // Додано метод для отримання обраних телефонів
    @Query("SELECT * FROM phones WHERE isFavorite = 1")
    fun getFavoritePhones(): Flow<List<PhoneEntity>>

    @Query("SELECT COUNT(*) FROM phones")
    suspend fun getPhonesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phones: List<PhoneEntity>)

    @Query("UPDATE phones SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Suppress("unused")
    @Query("DELETE FROM phones")
    suspend fun clearAll()
}