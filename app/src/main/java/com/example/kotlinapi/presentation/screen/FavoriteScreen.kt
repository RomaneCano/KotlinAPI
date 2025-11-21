package com.example.kotlinapi.presentation.screen

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kotlinapi.presentation.FavoriteRecipeViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
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
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF8E1),
                            Color(0xFFFFECB3)
                        )
                    )
                )
                .padding(16.dp)
        ) {

            if (favorites.isEmpty()) {
                // État vide
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFDF5)
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Aucun produit pour l'instant.",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFF57C00)
                        )
                        Text(
                            "Ajoutez des favoris depuis les fiches produits.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

            } else {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // 🔸 CAROUSEL EN HAUT
                    val pagerState = rememberPagerState(pageCount = { favorites.size })

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) { page ->
                        val fav = favorites[page]

                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFFDF5)
                            ),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = fav.thumbnailUrl,
                                    contentDescription = fav.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = fav.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFF57C00)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val encodedName = Uri.encode(fav.name)
                                            navController.navigate("detail/${fav.id}/$encodedName")
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFB300),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text("Voir le produit")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            vm.removeFavorite(fav.id, fav.name, fav.thumbnailUrl)
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFD32F2F)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Supprimer")
                                    }
                                }
                            }
                        }
                    }

                    // 🔹 Indicateurs du carousel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(favorites.size) { index ->
                            val selected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(if (selected) 10.dp else 8.dp)
                                    .background(
                                        color = if (selected) Color(0xFFF57C00) else Color(0xFFFFCC80),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )
                        }
                    }

                    // 🔹 LISTE DES FAVORIS EN DESSOUS
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(favorites, key = { it.id }) { fav ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val encodedName = Uri.encode(fav.name)
                                        navController.navigate("detail/${fav.id}/$encodedName")
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFFDF5)
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = fav.thumbnailUrl,
                                        contentDescription = fav.name,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .padding(end = 10.dp),
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = fav.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF5D4037),
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            vm.removeFavorite(fav.id, fav.name, fav.thumbnailUrl)
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = Color(0xFFD32F2F)
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
}
