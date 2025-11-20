package com.example.kotlinapi.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapi.data.AppDatabase
import com.example.kotlinapi.repository.FavoriteRecipeRepository
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoriteRecipeViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val dao = AppDatabase.getInstance(app).favoriteRecipeDao()
    private val repo = FavoriteRecipeRepository(dao)

    val favorites = repo.getFavorites()

    fun isFavorite(id: String) = repo.isFavorite(id)

    fun addFavorite(id: String, name: String, thumbnail: String?) {
        viewModelScope.launch {
            repo.addFavorite(id, name, thumbnail)
        }
    }

    fun removeFavorite(id: String, name: String, thumbnail: String?) {
        viewModelScope.launch {
            repo.removeFavorite(id, name, thumbnail)
        }
    }
}
