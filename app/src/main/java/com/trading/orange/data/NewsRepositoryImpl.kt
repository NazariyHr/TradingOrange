package com.trading.orange.data

import com.trading.orange.data.appwrite.AppWriteStorage
import com.trading.orange.data.server.ServerApi.Companion.searchParameters
import com.trading.orange.data.server.ServerDataManager
import com.trading.orange.data.server.dto.toNewsArticle
import com.trading.orange.domain.model.ImageProvider
import com.trading.orange.domain.model.NewsArticle
import com.trading.orange.domain.repository.NewsRepository
import retrofit2.HttpException

class NewsRepositoryImpl(
    private val serverDataManager: ServerDataManager,
    private val appWriteStorage: AppWriteStorage
) : NewsRepository {

    private val cachedImages = mutableMapOf<String, ByteArray?>()
    private val cachedImageIds = mutableListOf<String>()

    override suspend fun getNews(): List<NewsArticle> {
        var searchParameterToTry = searchParameters.first()
        var searchParameterIndex = 0

        while (true) {
            try {
                val newsResult = serverDataManager.getNews(searchParameterToTry)
                val randomImageIds = if (cachedImageIds.size >= newsResult.count()) {
                    cachedImageIds
                } else {
                    appWriteStorage.getRandomImageFileIds(newsResult.count())
                        .also {
                            cachedImageIds.clear()
                            cachedImageIds.addAll(it)
                        }
                }

                return newsResult
                    .mapIndexed { index, newsArticleDto ->
                        if (randomImageIds.size - 1 >= index) {
                            newsArticleDto.toNewsArticle(
                                imageDataProvider = object : ImageProvider() {
                                    override suspend fun provideImage(): ByteArray? {
                                        val fileId = randomImageIds[index]
                                        if (cachedImages.containsKey(fileId)) cachedImages[fileId]
                                        return cachedImages[fileId]
                                            ?: appWriteStorage.getImageContent(
                                                fileId
                                            )
                                                .also {
                                                    cachedImages[fileId] = it
                                                }
                                    }
                                }
                            )
                        } else {
                            newsArticleDto.toNewsArticle(
                                imageDataProvider = object : ImageProvider() {
                                    override suspend fun provideImage(): ByteArray? {
                                        val fileId = randomImageIds.lastOrNull() ?: return null
                                        if (cachedImages.containsKey(fileId)) cachedImages[fileId]
                                        return cachedImages[fileId]
                                            ?: appWriteStorage.getImageContent(
                                                fileId
                                            )
                                                .also {
                                                    cachedImages[fileId] = it
                                                }
                                    }
                                }
                            )
                        }
                    }
            } catch (e: HttpException) {
                e.printStackTrace()
                if (searchParameterIndex < searchParameters.count() - 1) {
                    searchParameterIndex++
                    searchParameterToTry = searchParameters[searchParameterIndex]
                } else {
                    return emptyList()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                return emptyList()
            }
        }
    }
}