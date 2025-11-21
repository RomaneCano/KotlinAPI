package com.example.kotlinapi.data

import com.example.kotlinapi.model.Drink
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.text.Normalizer
import kotlin.math.abs

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

    // Liste officielle d'ingrédients TheCocktailDB (extraits)
    private val officialIngredients = setOf(
        "Vodka","Gin","Rum","Light rum","Dark rum","Tequila","Scotch","Whiskey",
        "Brandy","Cognac","Amaretto","Kahlua","Coffee liqueur","Baileys irish cream",
        "Triple sec","Southern Comfort","Galliano","Campari","Vermouth","Sweet Vermouth",
        "Dry Vermouth","Beer","Champagne",

        "Orange juice","Pineapple juice","Lemon juice","Lime juice",
        "Tomato juice","Apple juice","Cranberry juice","Grapefruit juice",

        "Milk","Cream","Coconut milk","Coconut cream",

        "Sugar","Brown sugar","Honey","Simple syrup","Grenadine","Maple syrup",
        "Mint syrup",

        "Ginger","Cinnamon","Nutmeg","Mint","Basil",

        "Strawberries","Raspberries","Blackberries","Blueberries","Cherry","Banana",
        "Peach","Pear","Apple","Pineapple","Orange","Lemon","Lime","Pomegranate",
        "Water","Soda water","Tonic water","Cola","Ginger ale","Ginger beer"
    )

    private val directMap = mapOf(
        // FR → ingrédients officiels exacts
        "fraise" to "Strawberries",
        "fraises" to "Strawberries",
        "framboise" to "Raspberries",
        "framboises" to "Raspberries",
        "myrtille" to "Blueberries",
        "myrtilles" to "Blueberries",
        "mure" to "Blackberries",
        "mûre" to "Blackberries",
        "mures" to "Blackberries",
        "cerise" to "Cherry",
        "cerises" to "Cherry",
        "banane" to "Banana",
        "bananes" to "Banana",
        "pomme" to "Apple",
        "pommes" to "Apple",
        "poire" to "Pear",
        "poires" to "Pear",
        "peche" to "Peach",
        "pêche" to "Peach",

        "citron" to "Lemon",
        "citrons" to "Lemon",
        "citron vert" to "Lime",
        "lime" to "Lime",

        "orange" to "Orange",
        "ananas" to "Pineapple",

        "lait" to "Milk",
        "creme" to "Cream",
        "crème" to "Cream",
        "lait de coco" to "Coconut milk",
        "noix de coco" to "Coconut",

        "eau" to "Water",
        "eau gazeuse" to "Soda water",
        "tonic" to "Tonic water",
        "cola" to "Cola",
        "ginger ale" to "Ginger ale",
        "ginger beer" to "Ginger beer",

        "sucre" to "Sugar",
        "sucre roux" to "Brown sugar",
        "cassonade" to "Brown sugar",
        "sirop de sucre" to "Simple syrup",
        "grenadine" to "Grenadine",

        "cacao" to "Chocolate",
        "chocolat" to "Chocolate",
        "cafe" to "Coffee",
        "café" to "Coffee",

        "gingembre" to "Ginger",
        "cannelle" to "Cinnamon",
        "muscade" to "Nutmeg",

        "vodka" to "Vodka",
        "gin" to "Gin",
        "rhum" to "Rum",
        "tequila" to "Tequila",
        "whisky" to "Scotch",
        "whiskey" to "Scotch",
        "cognac" to "Cognac",
        "amaretto" to "Amaretto",
        "kahlua" to "Kahlua",
        "campari" to "Campari"
    )

    private fun mapToKnownIngredient(input: String): String {
        val lower = input.lowercase().trim()

        directMap[lower]?.let { return it }

        officialIngredients.firstOrNull { it.lowercase().contains(lower) }
            ?.let { return it }

        return when {
            "fruit" in lower -> "Orange juice"
            "legume" in lower -> "Tomato juice"
            "farine" in lower || "blé" in lower || "cereal" in lower -> "Milk"
            "pate" in lower -> "Milk"
            else -> "Water"
        }
    }

    private fun sanitizeIngredient(input: String): String {
        val clean = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .substringBefore(",")
            .substringBefore("(")
            .trim()

        return mapToKnownIngredient(clean)
    }

    suspend fun fetchDrinkForIngredient(rawIngredient: String): Drink? {

        val sanitized = sanitizeIngredient(rawIngredient)

        val responseText = try {
            client.get("https://www.thecocktaildb.com/api/json/v1/1/filter.php") {
                parameter("i", sanitized)
            }.bodyAsText()
        } catch (_: Exception) {
            return null
        }

        return try {
            val root = JSONObject(responseText)
            val drinksAny = root.opt("drinks") ?: return null

            if (drinksAny is String) return null

            if (drinksAny is JSONArray && drinksAny.length() > 0) {

                // 🎯 Sélection NON alphabétique
                // -> cocktail choisi via hashcode (stable mais varié)
                val size = drinksAny.length()
                val index = abs(sanitized.hashCode()) % size
                val obj = drinksAny.getJSONObject(index)

                Drink(
                    name = obj.optString("strDrink", sanitized),
                    instructions = "Boisson contenant : $sanitized",
                    thumbnailUrl = obj.optString("strDrinkThumb", null)
                )
            } else null

        } catch (_: Exception) {
            null
        }
    }
}
