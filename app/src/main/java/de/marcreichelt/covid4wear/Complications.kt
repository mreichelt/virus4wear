package de.marcreichelt.covid4wear

import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationData.TYPE_RANGED_VALUE
import android.support.wearable.complications.ComplicationData.TYPE_SHORT_TEXT
import android.support.wearable.complications.ComplicationText
import android.util.Log
import java.text.DecimalFormat

@JvmInline
value class Percent(val value: Float) {

    fun toHumanReadableString(): String = DecimalFormat.getPercentInstance()
        .apply { maximumFractionDigits = 1 }
        .format(value)

}

// Icon.createWithResource(context, R.drawable.ic_needle_full)


fun complicationDataForPercent(
    complicationType: Int,
    percent: Percent,
    icon: Icon?,
): ComplicationData? {
    return when (complicationType) {
        TYPE_RANGED_VALUE -> ComplicationData.Builder(TYPE_RANGED_VALUE)
            .setShortText(ComplicationText.plainText(percent.toHumanReadableString()))
            .setMinValue(0f)
            .setValue(percent.value * 100)
            .setMaxValue(100f)
            .setIcon(icon)
            .build()
        TYPE_SHORT_TEXT -> ComplicationData.Builder(TYPE_SHORT_TEXT)
            .setShortText(ComplicationText.plainText(percent.toHumanReadableString()))
            .setIcon(icon)
            .build()
        else -> {
            Log.w("Complications", "Unexpected complication type $complicationType")
            null
        }
    }
}
