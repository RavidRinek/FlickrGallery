package com.test.flickrgallery.features.gallery.domain.models

import com.test.flickrgallery.features.gallery.data.models.PhotoResponse
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: String,
    val title: String,
    val url: String,
    val height: Int,
    val width: Int
)

fun PhotoResponse.toDomain(): Photo =
    Photo(
        id = id ?: "",
        title = title ?: "",
        url = url ?: "",
        height = height ?: 0,
        width = width ?: 0
    )