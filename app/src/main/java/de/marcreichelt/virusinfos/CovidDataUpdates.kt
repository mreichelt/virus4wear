package de.marcreichelt.virusinfos

import android.content.Context
import android.util.Log
import androidx.work.*
import java.io.IOException
import java.util.concurrent.TimeUnit.HOURS

fun triggerDataUpdateNow(context: Context) {
    val updateRequest: WorkRequest = OneTimeWorkRequestBuilder<UpdateCovidDataWorker>().build()
    WorkManager.getInstance(context).enqueue(updateRequest)
}

private const val updateCovidDataWorkName = "update_covid_data"

fun schedulePeriodicCovidDataUpdates(context: Context) {
    val constraints = Constraints.Builder()
        // only load covid data when we have a network connection - otherwise what's the point? ;)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        // if the smartwatch runs out of energy we can't display the data anyway, so we're nice
        .setRequiresBatteryNotLow(true)
        .build()

    val updateRequest: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<UpdateCovidDataWorker>(6, HOURS)
            // do not run instantly
            .setInitialDelay(6, HOURS)
            .setConstraints(constraints)
            .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        updateCovidDataWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        updateRequest
    )
}

fun cancelPeriodicCovidDataUpdates(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork(updateCovidDataWorkName)
}

class UpdateCovidDataWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val tag = javaClass.simpleName

    override suspend fun doWork(): Result {
        return try {
            applicationContext.complicationDataCache.write(downloadVaccinationData())
            updateAllComplications(applicationContext)
            Result.success()
        } catch (e: IOException) {
            Log.w(tag, "error downloading ", e)
            Result.failure()
        }
    }

}
