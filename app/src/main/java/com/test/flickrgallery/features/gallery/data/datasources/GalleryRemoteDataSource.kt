package com.test.flickrgallery.features.gallery.data.datasources

import com.test.flickrgallery.features.gallery.data.models.GetPhotosResponse
import javax.inject.Inject

//this should extend some kind of 'BaseApiDataSource' to handle errors and etc..
class GalleryRemoteDataSource @Inject constructor(
    private val api: GalleryApiService,
) {

    suspend fun getRecentPhotos(
        page: Int,
        query: String
    ): Result<GetPhotosResponse?> {
        return try {
            val response = api.getRecentPhotos(
                method = if (query.isEmpty())
                    METHOD_GET_RECENT_PHOTOS else METHOD_SEARCH_PHOTOS,
                text = query,
                page = page
            )
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val METHOD_GET_RECENT_PHOTOS = "flickr.photos.getRecent"
        private const val METHOD_SEARCH_PHOTOS = "flickr.photos.search"
    }
}