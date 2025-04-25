package com.test.flickrgallery.features.gallery.presentation

import TopBarWithSearchMenu
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.test.flickrgallery.features.gallery.domain.models.Photo
import com.test.flickrgallery.features.gallery.presentation.components.PhotoGridLazyColumn
import com.test.flickrgallery.features.utilities.EmptyScreenState
import com.test.flickrgallery.features.utilities.LoadingScreen
import com.test.flickrgallery.features.utilities.LoadingStateItemsColumn
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PhotosGridScreen(
    navToPhotoSelectedScreen: (Photo) -> Unit,
) {

    val viewModel: GalleryViewModel = hiltViewModel()
    val state = viewModel.collectAsState().value
    val context = LocalContext.current
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

//    var query by remember { mutableStateOf("") }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is GalleryViewModel.SideEffect.ShowToast -> {
                Toast.makeText(
                    context,
                    sideEffect.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is GalleryViewModel.SideEffect.ScrollToTop -> {
                coroutineScope.launch {
                    gridState.scrollToItem(0)
                }
            }

            is GalleryViewModel.SideEffect.NavToPhotoSelectedScreen -> {
                navToPhotoSelectedScreen.invoke(sideEffect.photo)
            }
        }
    }

    Column {
        TopBarWithSearchMenu(
            searchText = state.query,
            autoPollEnabled = state.autoPollEnabled,
            onSearchChange = {
                viewModel.sendAction(
                    GalleryViewModel
                        .Action
                        .SearchQueryChanged(it)
                )
            },
            onClearSearch = {
                viewModel.sendAction(
                    GalleryViewModel
                        .Action
                        .ClearSearchClicked
                )
            },
            onToggleChanged = {
                viewModel.sendAction(
                    GalleryViewModel
                        .Action
                        .TogglePollingClicked
                )
            }
        )

        when (val uiState = state.uiState) {
            GalleryViewModel.UiStates.Error -> {
                EmptyScreenState("Couldnt load data")
            }

            GalleryViewModel.UiStates.Loading -> {
                LoadingStateItemsColumn()
            }

            is GalleryViewModel.UiStates.Data -> {
                Box {
                    PhotoGridLazyColumn(
                        photos = uiState.galleryPhotos.photos,
                        gridState = gridState,
                        onPhotoClick = { photo ->
                            viewModel.sendAction(
                                GalleryViewModel
                                    .Action
                                    .PhotoClicked(photo)
                            )
                        },
                        onScrolledToBottom = { lastVisible ->
                            viewModel.sendAction(
                                GalleryViewModel
                                    .Action
                                    .OnScrolling(lastVisible)
                            )
                        }
                    )
                    if (state.appendingLoading) {
                        LoadingScreen()
                    }
                }
            }
        }
    }
}