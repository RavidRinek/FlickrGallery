package com.test.flickrgallery

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.test.flickrgallery.features.gallery.utlities.RecentPhotosPollingWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FlickrGalleryApp : Application(), Configuration.Provider {

    @Inject
    lateinit var recentPhotosPollingWorkerFactory: RecentPhotosPollingWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(recentPhotosPollingWorkerFactory)
            .build()
}