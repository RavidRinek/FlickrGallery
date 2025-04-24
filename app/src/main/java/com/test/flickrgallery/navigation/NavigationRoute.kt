package com.test.flickrgallery.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
object PHOTOS_GRID_SCREEN

@Serializable
data class PHOTO_SELECTED_SCREEN(
    @SerialName("photo") val photo: String
)