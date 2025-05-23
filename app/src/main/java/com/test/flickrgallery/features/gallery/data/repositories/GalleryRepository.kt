package com.test.flickrgallery.features.gallery.data.repositories

import com.test.flickrgallery.features.gallery.data.models.GetPhotosResponse

interface GalleryRepository {

    suspend fun getGalleryPhotos(page: Int, query: String): Result<GetPhotosResponse?>
}