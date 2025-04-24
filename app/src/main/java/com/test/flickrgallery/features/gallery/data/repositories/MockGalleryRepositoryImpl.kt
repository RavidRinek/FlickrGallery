package com.test.flickrgallery.features.gallery.data.repositories

import com.test.flickrgallery.features.gallery.data.models.GalleryPhotosResponse
import com.test.flickrgallery.features.gallery.data.models.GetPhotosResponse
import com.test.flickrgallery.features.gallery.data.models.PhotoResponse

class MockGalleryRepositoryImpl : GalleryRepository {

    override suspend fun getRecentPhotos(page: Int, query: String): Result<GetPhotosResponse?> {
//        return Result.failure(Throwable("XXX"))
        return Result.success(
            GetPhotosResponse(
                galleryPhotosResponse = GalleryPhotosResponse(
                    page = 1,
                    pages = 1,
                    perpage = 1,
                    total = "1",
                    photos = listOf(
                        PhotoResponse(
                            id = "1",
                            title = "car",
                            url = "https://live.staticflickr.com/65535/",
                            height = 1,
                            width = 1
                        ),
                        PhotoResponse(
                            id = "2",
                            title = "car",
                            url = "https://live.staticflickr.com/65535/",
                            height = 1,
                            width = 1
                        ),
                        PhotoResponse(
                            id = "3",
                            title = "car",
                            url = "https://live.staticflickr.com/65535/",
                            height = 1,
                            width = 1
                        ),
                        PhotoResponse(
                            id = "4",
                            title = "car",
                            url = "https://live.staticflickr.com/65535/",
                            height = 1,
                            width = 1
                        ),
                        PhotoResponse(
                            id = "5",
                            title = "car",
                            url = "https://live.staticflickr.com/65535/",
                            height = 1,
                            width = 1
                        )
                    )
                ),
                stat = "ok"
            )
        )
    }
}