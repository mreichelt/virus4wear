package de.marcreichelt.covid4wear

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(val data: VaccinationData) : MainUiState
    object Error : MainUiState
}

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val tag = javaClass.simpleName

    private val _uiState: MutableLiveData<MainUiState> = MutableLiveData(MainUiState.Loading)
    val uiState: LiveData<MainUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                val freshData = downloadVaccinationData()
                _uiState.value = MainUiState.Success(freshData)

                app.vaccinationDataCacheStore.write(freshData)

                // fresh data is cached now, so it's a good moment to update all complications
                updateAllComplications(app)
            } catch (e: IOException) {
                Log.e(tag, "error downloading vaccination data", e)
                _uiState.value = MainUiState.Error
            }
        }
    }
}

fun VaccinationDataCache.toModel(): VaccinationData {
    return VaccinationData(
        firstVaccination = Percent(firstVaccination),
        fullVaccination = Percent(fullVaccination),
    )
}