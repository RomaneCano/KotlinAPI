package com.example.kotlinapi.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                .padding(16.dp)
                .fillMaxSize()
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
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = recipe.thumbnailUrl,
                            contentDescription = recipe.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = {
                                    if (isFav) {
                                        favVm.removeFavorite(recipe.id, recipe.name, recipe.thumbnailUrl)
                                    } else {
                                        favVm.addFavorite(recipe.id, recipe.name, recipe.thumbnailUrl)
                                    }
                                }
                            ) {
                                Text(if (isFav) "- favoris" else "+ favoris")
                            }
                        }

                        recipe.category?.let {
                            Text("Catégorie : $it")
                        }
                        recipe.area?.let {
                            Text("Origine : $it")
                        }

                        Text(
                            text = "Ingrédients :",
                            style = MaterialTheme.typography.titleMedium
                        )
                        recipe.ingredients.forEach { ing ->
                            Text("- $ing")
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Instructions :",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(recipe.instructions ?: "Instructions non disponibles.")

                        Spacer(Modifier.height(16.dp))

                        val mainIngredient = recipe.ingredients.firstOrNull()
                        if (mainIngredient != null) {
                            Button(
                                onClick = {
                                    navController.navigate("drinkInfo/$mainIngredient")
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Boisson recommandée")
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
