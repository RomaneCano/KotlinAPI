package com.example.kotlinapi.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    // On observe l'état exposé par le ViewModel
    val state by vm.state.collectAsState()

    // À chaque nouvel "ingredient", on (re)charge la boisson correspondante
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
                .padding(16.dp)
                .fillMaxSize()
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            style = MaterialTheme.typography.titleLarge
                        )

                        if (!drink.instructions.isNullOrBlank()) {
                            Text(
                                text = drink.instructions!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "Pas d'instructions détaillées.",
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
