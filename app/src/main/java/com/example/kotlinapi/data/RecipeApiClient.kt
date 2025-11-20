package com.example.kotlinapi.data

import com.example.kotlinapi.model.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.random.Random
import android.util.Log

object RecipeApiClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(this@RecipeApiClient.json)
        }
    }

    // ---------- Données de secours (fallback local) ----------
    // On les garde, mais on ne les utilise PLUS par défaut.
    private val fallbackRecipes = listOf(
        Recipe(
            id = "F001",
            name = "Pâtes à la bolognaise",
            thumbnailUrl = null,
            category = "Plat préparé",
            area = "France",
            instructions = "Exemple local : recette de pâtes à la bolognaise.",
            ingredients = listOf("Pâtes", "Viande hachée", "Tomates", "Oignons")
        ),
        Recipe(
            id = "F002",
            name = "Salade composée",
            thumbnailUrl = null,
            category = "Salade",
            area = "France",
            instructions = "Exemple local : salade avec légumes variés.",
            ingredients = listOf("Laitue", "Tomates", "Maïs", "Oeufs")
        ),
        Recipe(
            id = "F003",
            name = "Yaourt nature",
            thumbnailUrl = null,
            category = "Produit laitier",
            area = "France",
            instructions = "Exemple local : yaourt nature sans sucre.",
            ingredients = listOf("Lait", "Ferments lactiques")
        )
    )

    // ---------- Appels API OpenFoodFacts ----------

    /**
     * Recherche de produits alimentaires via OpenFoodFacts.
     */
    suspend fun fetchRecipes(query: String?): List<Recipe> {
        val q = query.orEmpty()
        return try {
            val responseText = client.get(
                // tu peux tester aussi avec world.openfoodfacts.org si tu veux
                "https://fr.openfoodfacts.org/cgi/search.pl"
            ) {
                parameter("search_terms", q)
                parameter("search_simple", 1)
                parameter("action", "process")
                parameter("json", 1)
                parameter("page_size", 20)
            }.bodyAsText()

            val root = json.parseToJsonElement(responseText).jsonObject
            val productsArray = root["products"]?.jsonArray ?: return emptyList()

            productsArray.mapNotNull { element ->
                val obj = element.jsonObject

                val code = obj["code"]?.jsonPrimitive?.contentOrNull
                if (code.isNullOrBlank()) return@mapNotNull null

                val name = obj["product_name"]?.jsonPrimitive?.contentOrNull
                val categories = obj["categories"]?.jsonPrimitive?.contentOrNull
                val countries = obj["countries"]?.jsonPrimitive?.contentOrNull
                val thumb = obj["image_front_small_url"]?.jsonPrimitive?.contentOrNull
                val ingredientsText =
                    obj["ingredients_text"]?.jsonPrimitive?.contentOrNull
                        ?: obj["ingredients_text_fr"]?.jsonPrimitive?.contentOrNull

                val ingredients = ingredientsText
                    ?.split(",", ";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?: emptyList()

                Recipe(
                    id = code,
                    name = name ?: "Produit $code",
                    thumbnailUrl = thumb,
                    category = categories,
                    area = countries,
                    instructions = "Produit alimentaire (données OpenFoodFacts).",
                    ingredients = ingredients
                )
            }
        } catch (e: Exception) {
                Log.e("RecipeApiClient", "Erreur lors de l'appel OpenFoodFacts", e)
                fallbackRecipes
        }
    }

    /**
     * Détail d'un produit par code-barres.
     */
    suspend fun fetchRecipeDetail(id: String): Recipe? {
        if (id.isBlank()) return null

        return try {
            val responseText = client.get(
                "https://fr.openfoodfacts.org/api/v2/product/$id.json"
            ).bodyAsText()

            val root = json.parseToJsonElement(responseText).jsonObject
            val product = root["product"]?.jsonObject ?: return null

            val code = product["code"]?.jsonPrimitive?.contentOrNull ?: id
            val name = product["product_name"]?.jsonPrimitive?.contentOrNull
            val categories = product["categories"]?.jsonPrimitive?.contentOrNull
            val countries = product["countries"]?.jsonPrimitive?.contentOrNull
            val thumb = product["image_front_small_url"]?.jsonPrimitive?.contentOrNull
            val ingredientsText =
                product["ingredients_text"]?.jsonPrimitive?.contentOrNull
                    ?: product["ingredients_text_fr"]?.jsonPrimitive?.contentOrNull

            val ingredients = ingredientsText
                ?.split(",", ";")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()

            Recipe(
                id = code,
                name = name ?: "Produit $code",
                thumbnailUrl = thumb,
                category = categories,
                area = countries,
                instructions = "Produit alimentaire (données OpenFoodFacts).",
                ingredients = ingredients
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Produit aléatoire : on pioche dans la liste des recettes récupérée.
     */
    suspend fun fetchRandomRecipe(): Recipe? {
        return try {
            val list = fetchRecipes("a") // on utilise une lettre "large"
            if (list.isNotEmpty()) {
                list[Random.nextInt(list.size)]
            } else {
                // si l'API ne renvoie rien, on pioche dans le fallback
                fallbackRecipes[Random.nextInt(fallbackRecipes.size)]
            }
        } catch (e: Exception) {
            fallbackRecipes[Random.nextInt(fallbackRecipes.size)]
        }
    }
}
