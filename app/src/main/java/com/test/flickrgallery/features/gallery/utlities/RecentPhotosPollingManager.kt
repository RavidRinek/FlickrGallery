package com.test.flickrgallery.features.gallery.utlities

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val RECENT_PHOTOS_POLLING_WORK_NAME = "recent_photos_polling"

@Singleton
class RecentPhotosPollingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun enablePolling(enable: Boolean){
        if (enable) {
            startPolling()
        } else {
            stopPolling()
        }
    }

    private fun startPolling() {
        /*val request = OneTimeWorkRequestBuilder<RecentPhotosPollingWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(request)*/

        val request =
            PeriodicWorkRequestBuilder<RecentPhotosPollingWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RECENT_PHOTOS_POLLING_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun stopPolling() {
        WorkManager.getInstance(context).cancelUniqueWork(RECENT_PHOTOS_POLLING_WORK_NAME)
    }
}