package com.example.kotlinapi.model

data class Recipe(
    val id: String,
    val name: String,
    val thumbnailUrl: String?,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val ingredients: List<String>
)
