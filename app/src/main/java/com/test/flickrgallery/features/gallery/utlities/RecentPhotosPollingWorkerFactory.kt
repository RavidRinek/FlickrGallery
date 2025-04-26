package com.test.flickrgallery.features.gallery.utlities

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.test.flickrgallery.features.gallery.domain.usecases.GetGalleryPhotosUseCase
import javax.inject.Inject

class RecentPhotosPollingWorkerFactory @Inject constructor(
    private val prefs: GalleryPrefs,
    private val getGalleryPhotosUseCase: GetGalleryPhotosUseCase,
    private val sharedPhotoStream: SharedPhotosStream
): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = RecentPhotosPollingWorker(
        appContext,
        workerParameters,
        prefs,
        getGalleryPhotosUseCase,
        sharedPhotoStream
    )
}

