package com.test.flickrgallery.features.gallery.data.models

import com.google.gson.annotations.SerializedName

data class GalleryPhotosResponse(
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("pages")
    val pages: Int? = null,
    @SerializedName("perpage")
    val perpage: Int? = null,
    @SerializedName("total")
    val total: String? = null,
    @SerializedName("photo")
    val photos: List<PhotoResponse>? = null
)
