package com.example.kotlinapi.repository

import com.example.kotlinapi.data.FavoriteRecipeDao
import com.example.kotlinapi.model.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRecipeRepository(
    private val dao: FavoriteRecipeDao
) {

    fun getFavorites(): Flow<List<FavoriteRecipeEntity>> = dao.getAllFavorites()

    fun isFavorite(id: String): Flow<Boolean> = dao.isFavorite(id)

    suspend fun toggleFavorite(id: String, name: String, thumbnail: String?) {
        val current = dao.isFavorite(id)
        // on ne peut pas lire Flow directement ici, donc on fait simple :
        // l'appelant gère la logique en fonction de isFavorite().
        // Pour simplifier, on expose plutôt 2 fonctions :
    }

    suspend fun addFavorite(id: String, name: String, thumbnail: String?) {
        dao.insertFavorite(
            FavoriteRecipeEntity(id = id, name = name, thumbnailUrl = thumbnail)
        )
    }

    suspend fun removeFavorite(id: String, name: String, thumbnail: String?) {
        dao.deleteFavorite(
            FavoriteRecipeEntity(id = id, name = name, thumbnailUrl = thumbnail)
        )
    }
}
