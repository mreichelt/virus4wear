package de.marcreichelt.covid4wear

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET

interface VaccinationApi {

    // https://impfdashboard.de/static/data/germany_vaccinations_timeseries_v2.tsv
    @GET("static/data/germany_vaccinations_timeseries_v2.tsv")
    suspend fun vaccinationsTsv(): ResponseBody

}

suspend fun downloadVaccinationData(): VaccinationData {
    val retrofit = Retrofit.Builder().baseUrl("https://impfdashboard.de").build()
    val api = retrofit.create(VaccinationApi::class.java)
    val tsv = api.vaccinationsTsv()
    return parseVaccinations(tsv.charStream().readLines())
}

fun parseVaccinations(tsvLines: List<String>): VaccinationData {
    val headers = tsvLines.first().split('\t')
    val newestData = tsvLines.last().split('\t')
    val firstVaccinationIndex = headers.indexOf("impf_quote_erst")
    val fullVaccinationIndex = headers.indexOf("impf_quote_voll")
    require(firstVaccinationIndex != -1 && fullVaccinationIndex != -1)

    val firstVaccination = Percent(newestData[firstVaccinationIndex].toFloat())
    val fullVaccination = Percent(newestData[fullVaccinationIndex].toFloat())

    return VaccinationData(firstVaccination, fullVaccination)
}

data class VaccinationData(val firstVaccination: Percent, val fullVaccination: Percent)
