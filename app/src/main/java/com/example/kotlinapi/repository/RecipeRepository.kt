package com.example.kotlinapi.repository

import com.example.kotlinapi.data.RecipeApiClient
import com.example.kotlinapi.model.Recipe

class RecipeRepository(
    private val api: RecipeApiClient = RecipeApiClient
) {

    suspend fun getRecipes(query: String?): List<Recipe> {
        return api.fetchRecipes(query)
    }

    suspend fun getRandomRecipe(): Recipe? {
        return api.fetchRandomRecipe()
    }

    suspend fun getRecipeDetail(id: String): Recipe? {
        return api.fetchRecipeDetail(id)
    }
}
