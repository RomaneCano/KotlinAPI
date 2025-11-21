package com.example.kotlinapi.data

import android.util.Log
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
import org.json.JSONArray
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

            // ---- PARSING JSON SANS parseToJsonElement ----
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

                val ingredientsText = obj.optString("ingredients_text_fr",
                    obj.optString("ingredients_text", "")
                )

                val ingredients = ingredientsText
                    .split(",", ";")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                Recipe(
                    id = code,
                    name = name,
                    thumbnailUrl = thumb,
                    category = categories,
                    area = countries,
                    instructions = "Produit alimentaire (OpenFoodFacts)",
                    ingredients = ingredients
                )
            }

        } catch (e: Exception) {
            Log.e("RecipeApiClient", "Erreur lors de l'appel OpenFoodFacts", e)
            emptyList()  // plus de fallback
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

            Recipe(
                id = code,
                name = name,
                thumbnailUrl = thumb,
                category = categories,
                area = countries,
                instructions = "Produit alimentaire (OpenFoodFacts)",
                ingredients = ingredients
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
