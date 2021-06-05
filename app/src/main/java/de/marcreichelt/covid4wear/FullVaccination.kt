package de.marcreichelt.covid4wear

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FullVaccinationComplicationTapReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("received tap")
    }
}

class FullVaccinationComplicationProviderService : ComplicationProviderService() {

    private val tag = javaClass.simpleName

    override fun onComplicationActivated(
        complicationId: Int,
        dataType: Int,
        manager: ComplicationManager
    ) {
        Log.d(tag, "onComplicationActivated() id: $complicationId")

        GlobalScope.launch {
            val newestData = downloadVaccinationData()
            val complicationData = complicationDataForPercent(
                dataType,
                newestData.fullVaccination,
                Icon.createWithResource(baseContext, R.drawable.ic_needle_full)
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

        val thisProvider = ComponentName(this, javaClass)

//        val preferences = getSharedPreferences(
//            CovidComplicationTapReceiver.COMPLICATION_PROVIDER_PREFERENCES_FILE_KEY,
//            0
//        )

        // TODO: get from preferences

        val complicationData: ComplicationData? = null

        if (complicationData != null) {
            complicationManager.updateComplicationData(complicationId, complicationData)
        } else {
            // If no data is sent, we still need to inform the ComplicationManager, so
            // the update job can finish and the wake lock isn't held any longer.
            complicationManager.noUpdateRequired(complicationId)
        }
    }

}
