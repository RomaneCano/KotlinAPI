package com.example.kotlinapi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val thumbnailUrl: String?
)
