package de.marcreichelt.virus4wear

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.*
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationData.TYPE_RANGED_VALUE
import android.support.wearable.complications.ComplicationData.TYPE_SHORT_TEXT
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private fun mainActivityIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java)
    return PendingIntent.getActivity(context, 1, intent, 0)
}

fun complicationDataForPercent(
    complicationType: Int,
    percent: Percent,
    context: Context,
    icon: Icon?,
): ComplicationData? {
    return when (complicationType) {
        TYPE_RANGED_VALUE -> ComplicationData.Builder(TYPE_RANGED_VALUE)
            .setShortText(ComplicationText.plainText(percent.toHumanReadableString()))
            .setMinValue(0f)
            .setValue(percent.value * 100)
            .setMaxValue(100f)
            .setTapAction(mainActivityIntent(context))
            .setIcon(icon)
            .build()
        TYPE_SHORT_TEXT -> ComplicationData.Builder(TYPE_SHORT_TEXT)
            .setShortText(ComplicationText.plainText(percent.toHumanReadableString()))
            .setIcon(icon)
            .setTapAction(mainActivityIntent(context))
            .build()
        else -> {
            Log.w("Complications", "Unexpected complication type $complicationType")
            null
        }
    }
}

val allComplicationProviders = listOf(
    SingleVaccinationComplicationProviderService::class.java,
    FullVaccinationComplicationProviderService::class.java,
)

fun updateAllComplications(context: Context) {
    allComplicationProviders.forEach { clazz ->
        val component = ComponentName(context, clazz)
        ProviderUpdateRequester(context, component).requestUpdateAll()
    }
}

abstract class CovidComplicationService : ComplicationProviderService() {

    private val tag = javaClass.simpleName

    override fun onComplicationActivated(
        complicationId: Int,
        dataType: Int,
        manager: ComplicationManager
    ) {
        runBlocking {
            complicationDataCache.addComplicationId(complicationId)
            triggerDataUpdateNow(baseContext)
            schedulePeriodicCovidDataUpdates(baseContext)
        }
    }

    override fun onComplicationDeactivated(complicationId: Int) {
        runBlocking {
            val updatedIds = complicationDataCache.removeComplicationId(complicationId)
            if (updatedIds.isEmpty()) {
                Log.d(tag, "last complication deactivated, cancelling periodic data updates")
                cancelPeriodicCovidDataUpdates(baseContext)
            }
        }
    }

    suspend fun getLastVaccinationDataFromCache() =
        complicationDataCache.data.first().toModel()

}
