package de.marcreichelt.covid4wear

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationData.TYPE_RANGED_VALUE
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat


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

            val numberText = DecimalFormat.getPercentInstance()
                .apply { maximumFractionDigits = 1 }
                .format(newestData.fullVaccination)
            Log.d(tag, numberText)

            val complicationData: ComplicationData? =
                when (dataType) {
                    TYPE_RANGED_VALUE -> ComplicationData.Builder(TYPE_RANGED_VALUE)
                        .setShortText(ComplicationText.plainText(numberText))
                        .setMinValue(0f)
                        .setValue(newestData.fullVaccination * 100)
                        .setMaxValue(100f)
                        .setIcon(Icon.createWithResource(baseContext, R.drawable.ic_needle_full))
                        .build()
                    else -> {
                        Log.w(tag, "Unexpected complication type $dataType")
                        null
                    }
                }

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
        val fullVaccinations = 0.17f
        val numberText = DecimalFormat.getPercentInstance()
            .apply { maximumFractionDigits = 1 }
            .format(fullVaccinations)
        Log.d(tag, numberText)

        val complicationData: ComplicationData? =
            when (dataType) {
                TYPE_RANGED_VALUE -> ComplicationData.Builder(TYPE_RANGED_VALUE)
                    .setShortText(ComplicationText.plainText(numberText))
                    .setMinValue(0f)
                    .setValue(fullVaccinations * 100)
                    .setMaxValue(100f)
                    .setIcon(Icon.createWithResource(baseContext, R.drawable.ic_needle_full))
                    .build()
                else -> {
                    Log.w(tag, "Unexpected complication type $dataType")
                    null
                }
            }

        if (complicationData != null) {
            complicationManager.updateComplicationData(complicationId, complicationData)
        } else {
            // If no data is sent, we still need to inform the ComplicationManager, so
            // the update job can finish and the wake lock isn't held any longer.
            complicationManager.noUpdateRequired(complicationId)
        }
    }

}
