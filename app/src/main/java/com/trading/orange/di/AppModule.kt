package com.trading.orange.di

import com.trading.orange.data.NewsRepositoryImpl
import com.trading.orange.data.appwrite.AppWriteStorage
import com.trading.orange.data.server.ServerApi
import com.trading.orange.data.server.ServerDataManager
import com.trading.orange.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideServerApi(): ServerApi {
        return Retrofit.Builder()
            .baseUrl(ServerApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    fun provideAppWriteDataBase(): AppWriteStorage {
        return AppWriteStorage()
    }

    @Provides
    @Singleton
    fun provideServerDataManager(
        serverApi: ServerApi
    ): ServerDataManager {
        return ServerDataManager(serverApi)
    }


    @Provides
    @Singleton
    fun provideNewsRepository(
        serverDataManager: ServerDataManager,
        appWriteStorage: AppWriteStorage
    ): NewsRepository {
        return NewsRepositoryImpl(serverDataManager, appWriteStorage)
    }
}