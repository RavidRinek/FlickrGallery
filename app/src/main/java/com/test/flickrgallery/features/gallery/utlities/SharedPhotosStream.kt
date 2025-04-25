package com.test.flickrgallery.features.gallery.utlities

import com.test.flickrgallery.features.gallery.domain.models.GalleryPhotos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPhotosStream @Inject constructor() {
    private val _newGalleryPhotos = MutableStateFlow<GalleryPhotos?>(null)
    val newGalleryPhotos: StateFlow<GalleryPhotos?> = _newGalleryPhotos

    fun updatePhotos(photos: GalleryPhotos?) {
        _newGalleryPhotos.value = photos
    }
}