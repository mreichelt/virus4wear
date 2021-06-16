package de.marcreichelt.virus4wear

import java.text.DecimalFormat

@JvmInline
value class Percent(val value: Float) {

    fun toHumanReadableString(): String = DecimalFormat.getPercentInstance()
        .apply { maximumFractionDigits = 1 }
        .format(value)

}
