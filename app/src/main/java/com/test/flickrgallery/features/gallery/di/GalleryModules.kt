package com.test.flickrgallery.features.gallery.di

import android.content.Context
import com.test.flickrgallery.features.gallery.data.datasources.GalleryApiService
import com.test.flickrgallery.features.gallery.data.datasources.GalleryRemoteDataSource
import com.test.flickrgallery.features.gallery.data.repositories.GalleryRepository
import com.test.flickrgallery.features.gallery.data.repositories.GalleryRepositoryImpl
import com.test.flickrgallery.features.gallery.utlities.GalleryPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GalleryModules {

    @Provides
    @Singleton
    fun provideGalleryPrefs(@ApplicationContext context: Context): GalleryPrefs =
        GalleryPrefs(context)

    @Provides
    @Singleton
    fun provideGalleryApiService(retrofit: Retrofit): GalleryApiService {
        return retrofit.create(GalleryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGalleryRepository(
        remoteDataSource: GalleryRemoteDataSource,
        dispatcher: CoroutineDispatcher
    ): GalleryRepository {
//        return MockGalleryRepositoryImpl()
        return GalleryRepositoryImpl(
            dispatcher,
            remoteDataSource,
        )
    }
}