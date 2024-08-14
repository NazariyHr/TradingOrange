package com.trading.orange.di

import android.content.Context
import androidx.room.Room
import com.trading.orange.data.ArticlesRepositoryImpl
import com.trading.orange.data.NewsRepositoryImpl
import com.trading.orange.data.UserBalanceRepositoryImpl
import com.trading.orange.data.appwrite.AppWriteStorage
import com.trading.orange.data.local_assets.AssetsReader
import com.trading.orange.data.local_db.RatesDatabase
import com.trading.orange.data.rates.BetResultsManager
import com.trading.orange.data.rates.CoefficientSimulator
import com.trading.orange.data.rates.PrepareBetAmountManager
import com.trading.orange.data.rates.RatesRepositoryImpl
import com.trading.orange.data.rates.SignalsManager
import com.trading.orange.data.server.ServerApi
import com.trading.orange.data.server.ServerDataManager
import com.trading.orange.domain.repository.ArticlesRepository
import com.trading.orange.domain.repository.NewsRepository
import com.trading.orange.domain.repository.RatesRepository
import com.trading.orange.domain.repository.UserBalanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideAssetsReader(
        @ApplicationContext context: Context
    ): AssetsReader {
        return AssetsReader(context)
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

    @Provides
    fun provideArticlesRepository(
        assetsReader: AssetsReader,
        appWriteStorage: AppWriteStorage
    ): ArticlesRepository {
        return ArticlesRepositoryImpl(assetsReader, appWriteStorage)
    }


    @Provides
    @Singleton
    fun provideUserBalanceRepository(
        @ApplicationContext context: Context
    ): UserBalanceRepository {
        return UserBalanceRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideRatesDatabase(
        @ApplicationContext context: Context
    ): RatesDatabase {
        return Room.databaseBuilder(
            context,
            RatesDatabase::class.java,
            "exchange_rates.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRatesRepository(
        @ApplicationContext context: Context,
        userRepository: UserBalanceRepository,
        coefficientSimulator: CoefficientSimulator,
        serverDataManager: ServerDataManager,
        ratesDatabase: RatesDatabase,
        betResultsManager: BetResultsManager,
        prepareBetAmountManager: PrepareBetAmountManager,
        signalsManager: SignalsManager
    ): RatesRepository {
        return RatesRepositoryImpl(
            context,
            userRepository,
            coefficientSimulator,
            serverDataManager,
            ratesDatabase,
            betResultsManager,
            prepareBetAmountManager,
            signalsManager
        )
    }

    @Provides
    fun providePrepareBetAmountManager(
        @ApplicationContext context: Context
    ): PrepareBetAmountManager {
        return PrepareBetAmountManager(
            context
        )
    }
}