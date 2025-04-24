package com.test.flickrgallery.features.gallery.data.models

import com.google.gson.annotations.SerializedName

data class GetPhotosResponse(
    @SerializedName("photos")
    val galleryPhotosResponse: GalleryPhotosResponse? = null,
    @SerializedName("stat")
    val stat: String? = null
)