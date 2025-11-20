package com.example.kotlinapi.data

import com.example.kotlinapi.model.Drink
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object DrinkApiClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    @Serializable
    private data class DrinkListDto(
        val drinks: List<DrinkDto>? = null
    )

    @Serializable
    private data class DrinkDto(
        @SerialName("strDrink") val strDrink: String,
        @SerialName("strInstructions") val strInstructions: String? = null,
        @SerialName("strDrinkThumb") val strDrinkThumb: String? = null
    )

    private fun DrinkDto.toDrink(): Drink =
        Drink(
            name = strDrink,
            instructions = strInstructions,
            thumbnailUrl = strDrinkThumb
        )

    // Boisson recommandée pour un ingrédient
    suspend fun fetchDrinkForIngredient(ingredient: String): Drink? {
        // TheCocktailDB : https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=Gin
        val url =
            "https://www.thecocktaildb.com/api/json/v1/1/filter.php?i=${ingredient.trim()}"
        val dto = client.get(url).body<DrinkListDto>()
        val first = dto.drinks?.firstOrNull() ?: return null

        // Pour obtenir plus de détails, il faudrait un second appel par ID,
        // mais on garde simple : on utilise juste le nom + image.
        return Drink(
            name = first.strDrink,
            instructions = "Boisson recommandée à base de $ingredient.",
            thumbnailUrl = first.strDrinkThumb
        )
    }
}
