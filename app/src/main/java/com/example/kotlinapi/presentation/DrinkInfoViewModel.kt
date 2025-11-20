package com.example.kotlinapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapi.model.Drink
import com.example.kotlinapi.repository.DrinkInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DrinkInfoUiState(
    val isLoading: Boolean = false,
    val drink: Drink? = null,
    val error: String? = null
)

class DrinkInfoViewModel(
    private val repo: DrinkInfoRepository = DrinkInfoRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(DrinkInfoUiState())
    val state: StateFlow<DrinkInfoUiState> = _state

    fun load(ingredient: String) {
        _state.value = DrinkInfoUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val drink = repo.getDrink(ingredient)
                _state.value = DrinkInfoUiState(
                    isLoading = false,
                    drink = drink,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = DrinkInfoUiState(
                    isLoading = false,
                    drink = null,
                    error = e.message ?: "Erreur lors du chargement de la boisson."
                )
            }
        }
    }
}
