package com.test.flickrgallery.features.gallery.presentation

import androidx.lifecycle.ViewModel
import com.test.flickrgallery.features.gallery.domain.models.GalleryPhotos
import com.test.flickrgallery.features.gallery.domain.models.Photo
import com.test.flickrgallery.features.gallery.domain.usecases.GetGalleryPhotosUseCase
import com.test.flickrgallery.features.gallery.utlities.GalleryPrefs
import com.test.flickrgallery.features.gallery.utlities.RecentPhotosPollingManager
import com.test.flickrgallery.features.gallery.utlities.SharedPhotosStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val recentPhotosPollingManager: RecentPhotosPollingManager,
    private val galleryPrefs: GalleryPrefs,
    private val getGalleryPhotosUseCase: GetGalleryPhotosUseCase,
    private val sharedPhotosStream: SharedPhotosStream
) : ViewModel(), ContainerHost<GalleryViewModel.State, GalleryViewModel.SideEffect> {

    override val container: Container<State, SideEffect> = container(State())
    private val _searchQuery = MutableStateFlow("")
    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true

    init {
        val pollingEnabled = galleryPrefs.getBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED)
        val searchTerm = galleryPrefs.getString(GalleryPrefs.K_LAST_SEARCH_TERM)
        _searchQuery.value = searchTerm
        observeSearchQueryChanges()
        collectNewPhotosFromWorker()
        setMenuUiState(pollingEnabled, searchTerm)
    }

    fun sendAction(action: Action) {
        when (action) {
            is Action.PhotoClicked -> {
                intent {
                    postSideEffect(SideEffect.NavToPhotoSelectedScreen(action.photo))
                }
            }

            is Action.SearchQueryChanged -> {
                updateQuery(action.query)
            }

            Action.ClearSearchClicked -> {
                if (_searchQuery.value.isBlank()) {
                    intent {
                        postSideEffect(SideEffect.ShowToast("Search query already cleared"))
                    }
                } else {
                    updateQuery("")
                }
            }

            Action.TogglePollingClicked -> {
                togglePolling()
            }

            is Action.OnScrolling -> {
                onScrolled(action.lastVisibleItemPosition)
            }
        }
    }

    private fun updateQuery(query: String) {
        intent {
            reduce { state.copy(query = query) }
            _searchQuery.value = query
        }
    }

    private fun togglePolling() {
        intent {
            if (_searchQuery.value.isBlank()) {
                postSideEffect(SideEffect.ShowToast("Cant start auto polling without search query"))
            } else {
                val enabled = galleryPrefs.getBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED)
                galleryPrefs.putBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED, !enabled)
                recentPhotosPollingManager.enablePolling(!enabled)
                reduce { state.copy(autoPollEnabled = !enabled) }
                postSideEffect(SideEffect.ShowToast("Auto polling enabled: ${!enabled}"))
            }
        }
    }

    private fun onScrolled(lastVisible: Int) {
        intent {
            val currentPhotos =
                (state.uiState as? UiStates.Data)?.galleryPhotos?.photos ?: return@intent
            val thresholdReached = lastVisible >= currentPhotos.size - 5

            if (!thresholdReached || isLoading || !hasMoreData) return@intent

            isLoading = true
            reduce { state.copy(appendingLoading = true) }
            getGalleryPhotosUseCase(currentPage, _searchQuery.value)
                .onSuccess {
                    val updatedPhotos = currentPhotos + it.photos
                    val updatedGalleryPhotos = it.copy(photos = updatedPhotos)
                    updatePhotos(updatedGalleryPhotos, it.photos.size)
                }
                .onFailure {
                    isLoading = false
                    reduce { state.copy(appendingLoading = false) }
                    postSideEffect(SideEffect.ShowToast(it.message ?: "Something went wrong"))
                }
        }
    }

    private fun updatePhotos(updatedGalleryPhotos: GalleryPhotos, newPhotosSize: Int) {
        intent {
            hasMoreData = newPhotosSize >= updatedGalleryPhotos.perpage
            isLoading = false
            updatedGalleryPhotos.photos.firstOrNull()?.id?.let { id ->
                galleryPrefs.putString(GalleryPrefs.K_LAST_PHOTO_ID, id)
            }
            reduce {
                state.copy(
                    uiState = UiStates.Data(updatedGalleryPhotos),
                    appendingLoading = false
                )
            }
            if (currentPage == 1) {
                postSideEffect(SideEffect.ScrollToTop)
            }
            currentPage++
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQueryChanges() {
        intent {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .onEach {
                    currentPage = 1
                    hasMoreData = true
                    isLoading = false
                }
//                .filter { it.length >= 2 || it.isEmpty() }
                .flatMapLatest { query -> //flatMapLatest cancels any ongoing API request if a new one comes in
                    flow {
                        if (state.autoPollEnabled && state.uiState is UiStates.Data) {
                            galleryPrefs.putBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED, false)
                            recentPhotosPollingManager.enablePolling(false)
                            postSideEffect(SideEffect.ShowToast("Auto polling enabled: false"))
                        }
                        reduce {
                            state.copy(
                                autoPollEnabled = false,
                                appendingLoading = true
                            )
                        }
                        val result = getGalleryPhotosUseCase(1, query)
                        emit(result)
                    }
                }
                .collect {
                    it.onSuccess { res ->
                        galleryPrefs.putString(GalleryPrefs.K_LAST_SEARCH_TERM, _searchQuery.value)
                        updatePhotos(res, res.photos.size)
                        if (res.photos.isEmpty()) {
                            postSideEffect(SideEffect.ShowToast("No matching photos were found."))
                        }
                    }.onFailure {
                        reduce { state.copy(appendingLoading = false) }
                        postSideEffect(SideEffect.ShowToast(it.message ?: "Something went wrong"))
                    }
                }
        }
    }

    private fun collectNewPhotosFromWorker() {
        intent {
            sharedPhotosStream.newGalleryPhotos.collect { res ->
                res?.let { updatePhotos(it, it.photos.size) }
            }
        }
    }

    private fun setMenuUiState(pollingEnabled: Boolean, searchTerm: String) {
        intent {
            delay(500)
            reduce {
                state.copy(
                    autoPollEnabled = pollingEnabled,
                    query = searchTerm
                )
            }
        }
    }

    sealed class UiStates {
        data object Loading : UiStates()
        data object Error : UiStates()
        data class Data(val galleryPhotos: GalleryPhotos) : UiStates()
    }

    data class State(
        val uiState: UiStates = UiStates.Loading,
        val query: String = "",
        val autoPollEnabled: Boolean = false,
        val appendingLoading: Boolean = false,
    )

    sealed class SideEffect {
        data class ShowToast(val message: String) : SideEffect()
        data class NavToPhotoSelectedScreen(val photo: Photo) : SideEffect()
        data object ScrollToTop : SideEffect()
    }

    sealed class Action {
        data class PhotoClicked(val photo: Photo) : Action()
        data class SearchQueryChanged(val query: String) : Action()
        data object ClearSearchClicked : Action()
        data object TogglePollingClicked : Action()
        data class OnScrolling(val lastVisibleItemPosition: Int) : Action()
    }
}