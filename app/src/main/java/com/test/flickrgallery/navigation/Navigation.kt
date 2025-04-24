package com.test.flickrgallery.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.test.flickrgallery.features.gallery.presentation.PhotoSelectedScreen
import com.test.flickrgallery.features.gallery.presentation.PhotosGridScreen
import kotlinx.serialization.json.Json

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = PHOTOS_GRID_SCREEN) {
        composable<PHOTOS_GRID_SCREEN> {
            PhotosGridScreen(
                navToPhotoSelectedScreen = {
                    navController.navigate(route = PHOTO_SELECTED_SCREEN(Json.encodeToString(it)))
                }
            )
        }

        composable<PHOTO_SELECTED_SCREEN> { entry ->
            PhotoSelectedScreen(photo = Json.decodeFromString(entry.toRoute<PHOTO_SELECTED_SCREEN>().photo))
        }
    }
}