package com.example.kotlinapi

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlinapi.presentation.screen.DetailScreen
import com.example.kotlinapi.presentation.screen.DrinkInfoScreen
import com.example.kotlinapi.presentation.screen.FavoritesScreen
import com.example.kotlinapi.presentation.screen.HomeScreen
import com.example.kotlinapi.ui.theme.KotlinAPITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinAPITheme {
                AppScaffold()
            }
        }
    }
}

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    IHM(navController)
}

@Composable
fun IHM(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // --- HOME ---
        composable("home") {
            HomeScreen(
                onRecipeSelected = { id, name ->
                    val encoded = Uri.encode(name)
                    navController.navigate("detail/$id/$encoded")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                }
            )
        }

        // --- DETAIL ---
        composable(
            route = "detail/{id}/{name}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            val encodedName = backStack.arguments?.getString("name") ?: ""
            val name = Uri.decode(encodedName)

            DetailScreen(
                id = id,
                name = name,
                navController = navController
            )
        }

        // --- FAVORITES ---
        composable("favorites") {
            FavoritesScreen(navController = navController)
        }

        // --- DRINK INFO ---
        composable(
            route = "drinkInfo/{ingredient}",
            arguments = listOf(
                navArgument("ingredient") { type = NavType.StringType }
            )
        ) { backStack ->
            val ingredient = backStack.arguments?.getString("ingredient") ?: ""

            DrinkInfoScreen(
                ingredient = ingredient,
                navController = navController
            )
        }
    }
}
