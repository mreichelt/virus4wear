package de.marcreichelt.covid4wear

import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SingleVaccinationComplicationProviderService : ComplicationProviderService() {

    private val tag = javaClass.simpleName

    override fun onComplicationActivated(
        complicationId: Int,
        dataType: Int,
        manager: ComplicationManager
    ) {
        Log.d(tag, "onComplicationActivated() id: $complicationId")
        GlobalScope.launch {
            val newestData = downloadVaccinationData()
            vaccinationDataCacheStore.write(newestData)
            val complicationData = complicationDataForPercent(
                dataType,
                newestData.firstVaccination,
                baseContext,
                Icon.createWithResource(baseContext, R.drawable.ic_needle_single)
            )
            manager.updateComplicationData(complicationId, complicationData)
        }
    }

    override fun onComplicationDeactivated(complicationId: Int) {
        Log.d(tag, "onComplicationDeactivated() id: $complicationId")
    }

    override fun onComplicationUpdate(
        complicationId: Int,
        dataType: Int,
        complicationManager: ComplicationManager
    ) {
        Log.d(tag, "onComplicationUpdate() id: $complicationId")

        runBlocking {
            val cachedData = vaccinationDataCacheStore.data.first().toModel()
            val complicationData = complicationDataForPercent(
                dataType,
                cachedData.firstVaccination,
                baseContext,
                Icon.createWithResource(baseContext, R.drawable.ic_needle_single)
            )
            complicationManager.updateComplicationData(complicationId, complicationData)
        }
    }

}
