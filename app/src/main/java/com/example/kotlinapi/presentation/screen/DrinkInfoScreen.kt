package com.example.kotlinapi.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kotlinapi.presentation.DrinkInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkInfoScreen(
    ingredient: String,
    navController: NavHostController,
    vm: DrinkInfoViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(ingredient) {
        vm.load(ingredient)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Boisson pour $ingredient") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
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
                            Color(0xFFFFF8E1),
                            Color(0xFFFFECB3)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = "Erreur : ${state.error}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.drink != null -> {
                    val drink = state.drink!!

                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFFDF5)
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = drink.thumbnailUrl,
                                contentDescription = drink.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )

                            Text(
                                text = drink.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFF57C00) // bel orange
                            )

                            Text(
                                text = drink.instructions ?: "Pas d'instructions détaillées.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        text = "Aucune boisson trouvée pour cet ingrédient.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
