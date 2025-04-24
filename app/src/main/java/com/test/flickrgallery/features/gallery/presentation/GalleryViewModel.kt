package com.test.flickrgallery.features.gallery.presentation

import androidx.lifecycle.ViewModel
import com.test.flickrgallery.features.gallery.domain.models.GalleryPhotos
import com.test.flickrgallery.features.gallery.domain.models.Photo
import com.test.flickrgallery.features.gallery.domain.usecases.GetRecentPhotosUseCase
import com.test.flickrgallery.features.gallery.utlities.GalleryPrefs
import com.test.flickrgallery.features.gallery.utlities.RecentPhotosPollingManager
import com.test.flickrgallery.features.gallery.utlities.SharedPhotosStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
    private val getRecentPhotosUseCase: GetRecentPhotosUseCase,
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

    private fun setMenuUiState(pollingEnabled: Boolean, searchTerm: String) {
        intent {
            reduce {
                state.copy(
                    autoPollEnabled = pollingEnabled,
                    query = searchTerm
                )
            }
        }
    }

    fun sendAction(action: Action) {
        when (action) {
            is Action.PhotoClicked -> {
                intent {
                    postSideEffect(SideEffect.NavToPhotoSelectedScreen(action.photo))
                }
            }

            is Action.SearchQueryChanged -> {
                intent { reduce { state.copy(query = action.query) } }
                _searchQuery.value = action.query
            }

            Action.ClearSearchClicked -> {
                intent { reduce { state.copy(query = "") } }
                _searchQuery.value = ""
            }

            Action.TogglePollingClicked -> {
                if (_searchQuery.value == "") {
                    intent {
                        postSideEffect(SideEffect.ShowToast("Cant start auto polling without search query"))
                    }
                } else {
                    galleryPrefs.getBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED).let { enabled ->
                        galleryPrefs.putBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED, !enabled)
                        recentPhotosPollingManager.enablePolling(!enabled)
                        intent {
                            reduce { state.copy(autoPollEnabled = !enabled) }
                            postSideEffect(SideEffect.ShowToast("Auto polling enabled: ${!enabled}"))
                        }
                    }
                }
            }

            is Action.Scrolled -> {
                intent {
                    val lastVisible = action.lastVisibleItemPosition
                    val currentPhotos =
                        (state.uiState as? UiStates.Data)?.galleryPhotos?.photos ?: emptyList()
                    val thresholdReached = lastVisible >= currentPhotos.size - 5

                    if (!thresholdReached || isLoading || !hasMoreData) return@intent

                    isLoading = true
                    val query = _searchQuery.value
                    getRecentPhotosUseCase(currentPage, query)
                        .onSuccess {
                            updatePhotos(currentPhotos, it)
                        }.onFailure {
                            isLoading = false
                            postSideEffect(
                                SideEffect.ShowToast(
                                    it.message ?: "Something went wrong"
                                )
                            )
                        }
                }
            }
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
                .flatMapLatest { query ->
                    flow {
                        if (query.isEmpty()) {
                            galleryPrefs.putBoolean(GalleryPrefs.K_AUTO_POLL_ENABLED, false)
                            recentPhotosPollingManager.enablePolling(false)
                        }

                        reduce {
                            state.copy(
                                autoPollEnabled = if (query.isEmpty())
                                    false
                                else state.autoPollEnabled
                            )
                        }
                        val result = getRecentPhotosUseCase(1, query)
                        emit(result)
                    }
                }
                .collect {
                    it.onSuccess { res ->
                        currentPage++
                        hasMoreData = res.photos.size >= res.perpage
                        galleryPrefs.putString(GalleryPrefs.K_LAST_SEARCH_TERM, _searchQuery.value)
                        res.photos.firstOrNull()?.id?.let { id ->
                            galleryPrefs.putString(GalleryPrefs.K_LAST_PHOTO_ID, id)
                        }
                        reduce { state.copy(uiState = UiStates.Data(res)) }
                        postSideEffect(SideEffect.ScrollToTop)
                    }.onFailure {
                        postSideEffect(SideEffect.ShowToast(it.message ?: "Something went wrong"))
                    }
                }
        }
    }

    private fun collectNewPhotosFromWorker() {
        intent {
            sharedPhotosStream.newGalleryPhotos.collect { res ->
                if (res != null) {
                    updatePhotos(
                        currentPhotos = (state.uiState as? UiStates.Data)
                            ?.galleryPhotos
                            ?.photos ?: emptyList(),
                        res = res
                    )
                }
            }
        }
    }

    private fun updatePhotos(currentPhotos: List<Photo>, res: GalleryPhotos?) {
        intent {
            val updatedPhotos = currentPhotos + res!!.photos
            val updatedGalleryPhotos = res.copy(photos = updatedPhotos)
            hasMoreData = res.photos.size >= res.perpage
            isLoading = false
            res.photos.firstOrNull()?.id?.let { id ->
                galleryPrefs.putString(GalleryPrefs.K_LAST_PHOTO_ID, id)
            }
            reduce { state.copy(uiState = UiStates.Data(updatedGalleryPhotos)) }
            if (currentPage == 1) {
                postSideEffect(SideEffect.ScrollToTop)
            }
            currentPage++
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
        val autoPollEnabled: Boolean = false
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
        data class Scrolled(val lastVisibleItemPosition: Int) : Action()
    }
}