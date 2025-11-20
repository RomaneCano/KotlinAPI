package com.example.kotlinapi.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kotlinapi.presentation.FavoriteRecipeViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    vm: FavoriteRecipeViewModel = viewModel()
) {
    val favorites by vm.favorites.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes produits favoris") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (favorites.isEmpty()) {
                Text("Aucun produit pour l'instant.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites, key = { it.id }) { fav ->
                        Surface(
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("detail/${fav.id}/${fav.name}")
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                AsyncImage(
                                    model = fav.thumbnailUrl,
                                    contentDescription = fav.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(end = 12.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    fav.name,
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
