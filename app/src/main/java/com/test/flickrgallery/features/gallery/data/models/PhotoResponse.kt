package com.test.flickrgallery.features.gallery.data.models

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("url_s")
    val url: String? = null,
    @SerializedName("height_s")
    val height: Int? = null,
    @SerializedName("width_s")
    val width: Int? = null,
)
