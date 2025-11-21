package com.example.kotlinapi.model

data class NutritionInfo(
    val energyKcal100g: Double? = null,
    val fat100g: Double? = null,
    val saturatedFat100g: Double? = null,
    val sugars100g: Double? = null,
    val salt100g: Double? = null
)

data class Recipe(
    val id: String,
    val name: String,
    val thumbnailUrl: String?,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val ingredients: List<String>,

    // 🆕 Analyse nutritionnelle
    val nutriScore: String? = null,          // "a", "b", "c", "d", "e"
    val nutrition: NutritionInfo? = null     // kcal, lipides, sucres, sel...
)
