package com.example.kotlinapi.data

import androidx.room.*
import com.example.kotlinapi.model.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {

    @Query("SELECT * FROM favorite_recipes ORDER BY name ASC")
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(recipe: FavoriteRecipeEntity)

    @Delete
    suspend fun deleteFavorite(recipe: FavoriteRecipeEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>
}
