package com.test.flickrgallery.features.gallery.utlities

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.test.flickrgallery.features.gallery.domain.usecases.GetRecentPhotosUseCase
import com.test.flickrgallery.utilities.NotificationsBuilder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.UnknownHostException

@HiltWorker
class RecentPhotosPollingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    @Assisted private val galleryPrefs: GalleryPrefs,
    @Assisted private val getRecentPhotosUseCase: GetRecentPhotosUseCase,
    @Assisted private val sharedPhotosStream: SharedPhotosStream,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val res = getRecentPhotosUseCase(
                page = 1,
                query = galleryPrefs.getString(GalleryPrefs.K_LAST_SEARCH_TERM)
            )

            res.onSuccess {
                val latestPhotoId = it.photos.firstOrNull()?.id
                val lastPhotoId = galleryPrefs.getString(GalleryPrefs.K_LAST_PHOTO_ID, null)
                if (latestPhotoId != null && latestPhotoId != lastPhotoId) {
                    sharedPhotosStream.updatePhotos(it)
                    galleryPrefs.putStringAsync(GalleryPrefs.K_LAST_PHOTO_ID, latestPhotoId)
                    NotificationsBuilder.showNewPhotosNotification(
                        context = context,
                        channelId = "flickr_updates",
                        channelName = "Flickr Pictures Updates",
                        contentTitle = "New Flickr Pictures!",
                        contentText = "You have new pictures in Flickr client."
                    )
                }
            }

            if (res.isSuccess) {
                Log.d(TAG, "Success!")
                Result.success()
            } else {
                Log.d(TAG, "Retrying..")
                Result.retry()
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Log.d(TAG, "Retrying..")
                Result.retry()
            } else {
                Log.d(TAG, "Error")
                Result.failure(Data.Builder().putString("error", e.message).build())
            }
        }
    }

    companion object {
        const val TAG = "RecentPhotosPollingWorker"
    }
}