package de.marcreichelt.virus4wear

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import de.marcreichelt.virus4wear.MainUiState.*
import de.marcreichelt.virus4wear.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)

        fun showData(data: VaccinationData) {
            views.covidVaccinationsSingle.text = data.firstVaccination.toHumanReadableString()
            views.covidVaccinationsFull.text = data.fullVaccination.toHumanReadableString()
        }

        val model: MainViewModel by viewModels()
        model.uiState.observe(this, { uiState ->
            when (uiState) {
                Loading -> {
                    views.content.visibility = View.GONE
                    views.loading.showWithAnimation()
                }
                is Success -> {
                    showData(uiState.data)
                    views.loading.hideWithAnimation()
                    views.content.showWithAnimation()
                }
                Error -> {
                    views.loading.hideWithAnimation()
                    views.errorDataNotLoaded.showWithAnimation()
                }
            }.apply { /* exhaustive */ }
        })
    }

}

private fun View.showWithAnimation() {
    visibility = View.VISIBLE
    alpha = 0f
    animate().apply { startDelay = 300 }.alpha(1f)
}