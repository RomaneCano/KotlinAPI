package com.example.kotlinapi.data

import android.util.Log
import com.example.kotlinapi.model.NutritionInfo
import com.example.kotlinapi.model.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONObject
import kotlin.random.Random

object RecipeApiClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(this@RecipeApiClient.json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 15000
        }
    }

    // ---------------------------------------------------------
    //                FETCH LISTE DE PRODUITS
    // ---------------------------------------------------------
    suspend fun fetchRecipes(query: String?): List<Recipe> {
        val q = query.orEmpty()

        return try {
            val responseText = client.get(
                "https://fr.openfoodfacts.org/cgi/search.pl"
            ) {
                parameter("search_terms", q)
                parameter("search_simple", 1)
                parameter("action", "process")
                parameter("json", 1)
                parameter("page_size", 20)
            }.bodyAsText()

            val root = JSONObject(responseText)
            val productsArray = root.optJSONArray("products") ?: return emptyList()

            (0 until productsArray.length()).mapNotNull { idx ->
                val obj = productsArray.optJSONObject(idx) ?: return@mapNotNull null

                val code = obj.optString("code", "")
                if (code.isBlank()) return@mapNotNull null

                val name = obj.optString("product_name", "Produit $code")
                val categories = obj.optString("categories", null)
                val countries = obj.optString("countries", null)
                val thumb = obj.optString("image_front_small_url", null)

                val ingredientsText = obj.optString(
                    "ingredients_text_fr",
                    obj.optString("ingredients_text", "")
                )

                val ingredients = ingredientsText
                    .split(",", ";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                // 🟩 Nutri-score dans la liste aussi
                val nutriScore = obj.optString("nutriscore_grade", null)
                    ?.takeIf { it.isNotBlank() }

                Recipe(
                    id = code,
                    name = name,
                    thumbnailUrl = thumb,
                    category = categories,
                    area = countries,
                    instructions = "Produit alimentaire (OpenFoodFacts)",
                    ingredients = ingredients,
                    nutriScore = nutriScore,
                    nutrition = null // on ne charge le détail nutritionnel que dans fetchRecipeDetail
                )
            }

        } catch (e: Exception) {
            Log.e("RecipeApiClient", "Erreur lors de l'appel OpenFoodFacts", e)
            emptyList()
        }
    }

    // ---------------------------------------------------------
    //                FETCH DETAIL D’UN PRODUIT
    // ---------------------------------------------------------
    suspend fun fetchRecipeDetail(id: String): Recipe? {
        if (id.isBlank()) return null

        return try {
            val responseText = client.get(
                "https://fr.openfoodfacts.org/api/v2/product/$id.json"
            ).bodyAsText()

            val root = JSONObject(responseText)
            val product = root.optJSONObject("product") ?: return null

            val code = product.optString("code", id)
            val name = product.optString("product_name", "Produit $code")
            val categories = product.optString("categories", null)
            val countries = product.optString("countries", null)
            val thumb = product.optString("image_front_small_url", null)

            val ingredientsText = product.optString(
                "ingredients_text_fr",
                product.optString("ingredients_text", "")
            )

            val ingredients = ingredientsText
                .split(",", ";")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            // 🟩 Nutri-score
            val nutriScore = product.optString("nutriscore_grade", null)
                ?.takeIf { it.isNotBlank() }

            // 🍽️ Nutriments sur 100g
            val nutrimentsObj = product.optJSONObject("nutriments")

            val energyKcal100g = nutrimentsObj?.optDouble("energy-kcal_100g", Double.NaN)
                ?.let { if (it.isNaN()) null else it }

            val fat100g = nutrimentsObj?.optDouble("fat_100g", Double.NaN)
                ?.let { if (it.isNaN()) null else it }

            val saturatedFat100g = nutrimentsObj?.optDouble("saturated-fat_100g", Double.NaN)
                ?.let { if (it.isNaN()) null else it }

            val sugars100g = nutrimentsObj?.optDouble("sugars_100g", Double.NaN)
                ?.let { if (it.isNaN()) null else it }

            val salt100g = nutrimentsObj?.optDouble("salt_100g", Double.NaN)
                ?.let { if (it.isNaN()) null else it }

            val nutrition = if (
                energyKcal100g != null ||
                fat100g != null ||
                saturatedFat100g != null ||
                sugars100g != null ||
                salt100g != null
            ) {
                NutritionInfo(
                    energyKcal100g = energyKcal100g,
                    fat100g = fat100g,
                    saturatedFat100g = saturatedFat100g,
                    sugars100g = sugars100g,
                    salt100g = salt100g
                )
            } else {
                null
            }

            Recipe(
                id = code,
                name = name,
                thumbnailUrl = thumb,
                category = categories,
                area = countries,
                instructions = "Produit alimentaire (OpenFoodFacts)",
                ingredients = ingredients,
                nutriScore = nutriScore,
                nutrition = nutrition
            )

        } catch (e: Exception) {
            Log.e("RecipeApiClient", "Erreur détail produit", e)
            null
        }
    }

    // ---------------------------------------------------------
    //                PRODUIT ALÉATOIRE
    // ---------------------------------------------------------
    suspend fun fetchRandomRecipe(): Recipe? {
        return try {
            val list = fetchRecipes("a")  // lettre large
            if (list.isNotEmpty()) list[Random.nextInt(list.size)]
            else null
        } catch (e: Exception) {
            Log.e("RecipeApiClient", "Erreur random", e)
            null
        }
    }
}
