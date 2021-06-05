package de.marcreichelt.covid4wear

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.marcreichelt.covid4wear.MainUiState.*
import de.marcreichelt.covid4wear.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState: MutableLiveData<MainUiState> = MutableLiveData(Loading)
    val uiState: LiveData<MainUiState> = _uiState

    init {
        viewModelScope.launch {
            // TODO: show cached data
            val data = downloadVaccinationData()
            _uiState.value = Success(data)
            // TODO: also trigger update of complications
        }
    }
}

sealed interface MainUiState {
    object Loading : MainUiState

    //    data class LoadingButGotFromCache(val data: VaccinationData) : MainUiState
    data class Success(val data: VaccinationData) : MainUiState
    object Error : MainUiState
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)

        val model: MainViewModel by viewModels()
        model.uiState.observe(this, { uiState ->
            when (uiState) {
                Loading -> {
                    views.content.visibility = View.GONE
                    views.loading.showWithAnimation()
                }
                is Success -> {
                    views.covidVaccinationsSingle.text =
                        uiState.data.firstVaccination.toHumanReadableString()
                    views.covidVaccinationsFull.text =
                        uiState.data.fullVaccination.toHumanReadableString()
                    views.loading.hideWithAnimation()
                    views.content.showWithAnimation()
                }
                Error -> {
                    views.loading.hideWithAnimation()
                    views.errorDataNotLoaded.showWithAnimation()
                }
            }
        })
    }

}

private fun View.showWithAnimation() {
    visibility = View.VISIBLE
    alpha = 0f
    animate().apply { startDelay = 300 }.alpha(1f)
}