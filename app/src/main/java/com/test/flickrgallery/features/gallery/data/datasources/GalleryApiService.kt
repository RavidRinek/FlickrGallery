package com.test.flickrgallery.features.gallery.data.datasources

import com.test.flickrgallery.features.gallery.data.models.GetPhotosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GalleryApiService {

    @GET(GET_PHOTOS)
    suspend fun getRecentPhotos(
        @Query("method") method: String,
        @Query("text") text: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = "aabca25d8cd75f676d3a74a72dcebf21",
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("extras") extras: String = "url_s",
    ): Response<GetPhotosResponse>

    companion object {
        private const val GET_PHOTOS = "services/rest/"
    }
}