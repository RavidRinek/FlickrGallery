package com.test.flickrgallery.features.gallery.domain.usecases

import com.test.flickrgallery.features.gallery.data.repositories.GalleryRepository
import com.test.flickrgallery.features.gallery.domain.models.GalleryPhotos
import com.test.flickrgallery.features.gallery.domain.models.toDomain
import javax.inject.Inject

class GetGalleryPhotosUseCase @Inject constructor(
    private val galleryRepository: GalleryRepository,
) {
    suspend operator fun invoke(
        page: Int,
        query: String
    ): Result<GalleryPhotos> {
        val res = galleryRepository.getGalleryPhotos(
            page = page,
            query = query
        ).onSuccess {
            if (it == null || it.stat != "ok" || it.galleryPhotosResponse == null) {
                return Result.failure(Throwable("Something Went Wrong.."))
            }/* else if (it.galleryPhotosResponse.photos.isNullOrEmpty()) {
                return Result.failure(Throwable("No Photos Found"))
            }*/
        }
        return res.map { it!!.galleryPhotosResponse!!.toDomain() }
    }
}