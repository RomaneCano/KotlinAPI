package com.example.kotlinapi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapi.model.Drink
import com.example.kotlinapi.repository.DrinkInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

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

    fun load(rawIngredient: String) {
        _state.value = DrinkInfoUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val drink = repo.getDrink(rawIngredient)

                _state.value = if (drink != null) {
                    DrinkInfoUiState(
                        isLoading = false,
                        drink = drink,
                        error = null
                    )
                } else {
                    DrinkInfoUiState(
                        isLoading = false,
                        drink = null,
                        error = "Aucune boisson trouvée pour cet ingrédient."
                    )
                }

            } catch (e: Exception) {
                // on log l’erreur pour toi dans Logcat, mais on n’affiche pas le message moche à l’utilisateur
                Log.e("DrinkInfoViewModel", "Erreur lors du chargement de la boisson", e)

                _state.value = DrinkInfoUiState(
                    isLoading = false,
                    drink = null,
                    // ❗ message propre côté UI
                    error = "Erreur lors du chargement de la boisson."
                )
            }
        }
    }
}
