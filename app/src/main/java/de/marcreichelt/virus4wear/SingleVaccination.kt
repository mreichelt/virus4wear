package de.marcreichelt.virus4wear

import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationManager
import kotlinx.coroutines.runBlocking

class SingleVaccinationComplicationProviderService : CovidComplicationService() {

    override fun onComplicationUpdate(
        complicationId: Int,
        dataType: Int,
        complicationManager: ComplicationManager
    ) {
        runBlocking {
            val data = getLastVaccinationDataFromCache()
            val complicationData = complicationDataForPercent(
                dataType,
                data.firstVaccination,
                baseContext,
                Icon.createWithResource(baseContext, R.drawable.ic_needle_single)
            )
            complicationManager.updateComplicationData(complicationId, complicationData)
        }
    }

}
