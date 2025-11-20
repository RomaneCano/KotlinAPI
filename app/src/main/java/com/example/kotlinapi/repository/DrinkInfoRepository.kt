package com.example.kotlinapi.repository

import com.example.kotlinapi.data.DrinkApiClient
import com.example.kotlinapi.model.Drink

class DrinkInfoRepository(
    private val api: DrinkApiClient = DrinkApiClient
) {
    suspend fun getDrink(ingredient: String): Drink? {
        return api.fetchDrinkForIngredient(ingredient)
    }
}
