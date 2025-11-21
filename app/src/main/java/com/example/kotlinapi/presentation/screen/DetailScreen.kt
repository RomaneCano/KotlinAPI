package com.example.kotlinapi.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kotlinapi.presentation.FavoriteRecipeViewModel
import com.example.kotlinapi.presentation.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: String,
    name: String,
    navController: NavHostController
) {
    val recipeVm: RecipeViewModel = viewModel()
    val favVm: FavoriteRecipeViewModel = viewModel()
    val detailState by recipeVm.detailState.collectAsState()

    LaunchedEffect(id) {
        recipeVm.loadRecipeDetail(id)
    }

    val isFav by favVm.isFavorite(id).collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF8E1), // jaune très clair
                            Color(0xFFFFECB3)  // jaune-orangé
                        )
                    )
                )
                .padding(16.dp)
        ) {
            when {
                detailState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                detailState.error != null -> {
                    Text(
                        text = "Erreur : ${detailState.error}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                detailState.recipe != null -> {
                    val recipe = detailState.recipe!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = recipe.thumbnailUrl,
                            contentDescription = recipe.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )

                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Ingrédient principal pour la boisson recommandée
                        val mainIngredient = recipe.ingredients.firstOrNull() ?: recipe.name

                        // 🔸 Ligne de boutons (orange/jaune)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("drinkInfo/$mainIngredient")
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFB300), // orange soutenu
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Boisson recommandée")
                            }

                            Button(
                                onClick = {
                                    if (isFav) {
                                        favVm.removeFavorite(
                                            recipe.id,
                                            recipe.name,
                                            recipe.thumbnailUrl
                                        )
                                    } else {
                                        favVm.addFavorite(
                                            recipe.id,
                                            recipe.name,
                                            recipe.thumbnailUrl
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFav) Color(0xFFFFD54F) else Color(0xFFFFF176),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(if (isFav) "Retirer des favoris" else "Ajouter aux favoris")
                            }
                        }

                        recipe.category?.let {
                            Text("Catégorie : $it")
                        }
                        recipe.area?.let {
                            Text("Origine : $it")
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Ingrédients :",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        recipe.ingredients.forEach { ing ->
                            Text("- $ing")
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Instructions :",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(recipe.instructions ?: "Instructions non disponibles.")

                        // 🧾 Analyse nutritionnelle
                        val nutrition = recipe.nutrition
                        val nutriScore = recipe.nutriScore

                        if (nutrition != null || nutriScore != null) {
                            Spacer(Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFFDE7) // jaune très pâle
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Analyse nutritionnelle (pour 100 g)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    // Nutri-score
                                    nutriScore?.let { ns ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Nutri-score : ")

                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 4.dp)
                                                    .background(
                                                        color = nutriScoreColor(ns),
                                                        shape = MaterialTheme.shapes.small
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = ns.uppercase(),
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }

                                    // Valeurs nutritionnelles
                                    nutrition?.let { n ->
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            n.energyKcal100g?.let {
                                                Text("• Énergie : ${it.toInt()} kcal")
                                            }
                                            n.fat100g?.let {
                                                Text("• Lipides : ${"%.1f".format(it)} g")
                                            }
                                            n.saturatedFat100g?.let {
                                                Text("• Dont acides gras saturés : ${"%.1f".format(it)} g")
                                            }
                                            n.sugars100g?.let {
                                                Text("• Sucres : ${"%.1f".format(it)} g")
                                            }
                                            n.salt100g?.let {
                                                Text("• Sel : ${"%.2f".format(it)} g")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = "Aucune donnée pour ce produit.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

private fun nutriScoreColor(letter: String): Color {
    return when (letter.lowercase()) {
        "a" -> Color(0xFF2E7D32) // vert
        "b" -> Color(0xFF388E3C)
        "c" -> Color(0xFFFBC02D) // jaune
        "d" -> Color(0xFFF57C00) // orange
        "e" -> Color(0xFFD32F2F) // rouge
        else -> Color(0xFF9E9E9E) // gris
    }
}
