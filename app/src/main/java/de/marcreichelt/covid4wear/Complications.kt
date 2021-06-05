package de.marcreichelt.covid4wear

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationData.TYPE_RANGED_VALUE
import android.support.wearable.complications.ComplicationData.TYPE_SHORT_TEXT
import android.support.wearable.complications.ComplicationText
import android.support.wearable.complications.ProviderUpdateRequester
import android.util.Log

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
