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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinapi.presentation.RecipeViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeSelected: (String, String) -> Unit,
    onFavoritesClick: () -> Unit,
    vm: RecipeViewModel = viewModel()
) {
    val state by vm.listState.collectAsState()
    var query by remember { mutableStateOf(TextFieldValue("")) }

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
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // pour ne pas être caché par le bouton
                        ) {
                            items(state.recipes, key = { it.id }) { recipe ->
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
                                        Column {
                                            Text(
                                                recipe.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 🔥 Nouveau bouton "Découvrir un produit" en bas, orange, bien visible
            Button(
                onClick = { vm.loadRandomRecipe() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726), // orange soutenu
                    contentColor = Color.White
                )
            ) {
                Text("Découvrir un produit", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
