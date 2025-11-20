package com.example.kotlinapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapi.model.Recipe
import com.example.kotlinapi.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null
)

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val error: String? = null
)

class RecipeViewModel(
    private val repo: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _listState = MutableStateFlow(RecipeListUiState())
    val listState: StateFlow<RecipeListUiState> = _listState

    private val _detailState = MutableStateFlow(RecipeDetailUiState())
    val detailState: StateFlow<RecipeDetailUiState> = _detailState

    init {
        loadRecipes(null)
    }

    fun loadRecipes(query: String?) {
        _listState.value = RecipeListUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val recipes = repo.getRecipes(query)
                _listState.value = RecipeListUiState(
                    isLoading = false,
                    recipes = recipes,
                    error = null
                )
            } catch (e: Exception) {
                _listState.value = RecipeListUiState(
                    isLoading = false,
                    recipes = emptyList(),
                    error = e.message ?: "Erreur lors du chargement des recettes."
                )
            }
        }
    }

    fun loadRandomRecipe() {
        _listState.value = RecipeListUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val recipe = repo.getRandomRecipe()
                _listState.value = RecipeListUiState(
                    isLoading = false,
                    recipes = recipe?.let { listOf(it) } ?: emptyList(),
                    error = null
                )
            } catch (e: Exception) {
                _listState.value = RecipeListUiState(
                    isLoading = false,
                    recipes = emptyList(),
                    error = e.message ?: "Erreur lors du chargement aléatoire."
                )
            }
        }
    }

    fun loadRecipeDetail(id: String) {
        _detailState.value = RecipeDetailUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val recipe = repo.getRecipeDetail(id)
                _detailState.value = RecipeDetailUiState(
                    isLoading = false,
                    recipe = recipe,
                    error = null
                )
            } catch (e: Exception) {
                _detailState.value = RecipeDetailUiState(
                    isLoading = false,
                    recipe = null,
                    error = e.message ?: "Erreur lors du chargement de la recette."
                )


            }
        }
    }

}
