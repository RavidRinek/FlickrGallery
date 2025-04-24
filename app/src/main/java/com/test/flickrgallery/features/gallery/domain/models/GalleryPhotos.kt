package com.test.flickrgallery.features.gallery.domain.models

import com.test.flickrgallery.features.gallery.data.models.GalleryPhotosResponse

data class GalleryPhotos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: String,
    val photos: List<Photo>,
)

fun GalleryPhotosResponse.toDomain(): GalleryPhotos =
    GalleryPhotos(
        page = page ?: 0,
        pages = pages ?: 0,
        perpage = perpage ?: 0,
        total = total ?: "",
        photos = (photos ?: listOf()).map { it.toDomain() },
    )