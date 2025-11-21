package com.example.kotlinapi.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kotlinapi.presentation.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeSelected: (String, String) -> Unit,
    onFavoritesClick: () -> Unit,
    vm: RecipeViewModel = viewModel()
) {
    val state by vm.listState.collectAsState()
    var query by remember { mutableStateOf(TextFieldValue("")) }

    // 🆕 Filtre Nutri-score : null = tous, sinon "a".."e"
    var selectedNutriScore by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produits alimentaires") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Star, contentDescription = "Mes produits favoris")
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        vm.loadRecipes(it.text.ifBlank { null })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rechercher un produit") }
                )

                // 🆕 Rangée de chips Nutri-score
                NutriScoreFilterRow(
                    selected = selectedNutriScore,
                    onSelectedChange = { selectedNutriScore = it }
                )

                when {
                    state.isLoading -> {
                        Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }

                    state.error != null -> {
                        Text("Erreur : ${state.error}")
                    }

                    else -> {
                        // 🧮 On applique le filtre Nutri-score côté UI
                        val filteredRecipes = state.recipes.filter { recipe ->
                            selectedNutriScore == null ||
                                    recipe.nutriScore?.equals(
                                        selectedNutriScore,
                                        ignoreCase = true
                                    ) == true
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 100.dp) // pour le gros bouton
                        ) {
                            items(filteredRecipes, key = { it.id }) { recipe ->
                                Surface(
                                    tonalElevation = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onRecipeSelected(recipe.id, recipe.name)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = recipe.thumbnailUrl,
                                            contentDescription = recipe.name,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .padding(end = 12.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                recipe.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )

                                            // petit rappel du Nutri-score si dispo
                                            recipe.nutriScore?.let { ns ->
                                                AssistChip(
                                                    onClick = { /* rien, juste info */ },
                                                    label = {
                                                        Text("Nutri-score : ${ns.uppercase()}")
                                                    },
                                                    colors = AssistChipDefaults.assistChipColors(
                                                        containerColor = nutriScoreColor(ns)
                                                            .copy(alpha = 0.2f),
                                                        labelColor = Color.Black
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 🔥 Gros bouton "Découvrir un produit" en bas
            Button(
                onClick = { vm.loadRandomRecipe() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(68.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726), // orange soutenu
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Découvrir un produit",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun NutriScoreFilterRow(
    selected: String?,
    onSelectedChange: (String?) -> Unit
) {
    val options = listOf<String?>(null, "a", "b", "c", "d", "e")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { value ->
            val isSelected = selected == value
            val label = value?.uppercase() ?: "Tous"

            FilterChip(
                selected = isSelected,
                onClick = {
                    onSelectedChange(
                        if (isSelected) null else value
                    )
                },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFB300),
                    selectedLabelColor = Color.Black
                )
            )
        }
    }
}

// réutilisée ici aussi
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
