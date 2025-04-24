package com.test.flickrgallery.features.gallery.data.repositories

import com.test.flickrgallery.features.gallery.data.datasources.GalleryRemoteDataSource
import com.test.flickrgallery.features.gallery.data.models.GetPhotosResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val apiDataSource: GalleryRemoteDataSource,
) : GalleryRepository {

    override suspend fun getRecentPhotos(page: Int, query: String): Result<GetPhotosResponse?> =
        withContext(dispatcher) {
            return@withContext apiDataSource.getRecentPhotos(
                page = page,
                query = query
            )
        }
}