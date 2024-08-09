package com.trading.orange.data

import com.trading.orange.data.appwrite.AppWriteStorage
import com.trading.orange.data.local_assets.AssetsReader
import com.trading.orange.data.local_assets.dto.toQuickReadArticle
import com.trading.orange.data.local_assets.dto.toStrategyArticle
import com.trading.orange.domain.model.ImageProvider
import com.trading.orange.domain.model.QuickReadArticle
import com.trading.orange.domain.model.StrategyArticle
import com.trading.orange.domain.repository.ArticlesRepository

class ArticlesRepositoryImpl(
    private val assetsReader: AssetsReader,
    private val appWriteStorage: AppWriteStorage
) : ArticlesRepository {

    private val cachedImages = mutableMapOf<String, ByteArray?>()
    private val cachedStrategies = mutableListOf<StrategyArticle>()
    private val cachedQuickReads = mutableListOf<QuickReadArticle>()

    override suspend fun getStrategies(): List<StrategyArticle> {
        return assetsReader
            .parseArticlesFromFile()
            .strategies
            .mapIndexed { index, strategy ->
                strategy.toStrategyArticle(
                    id = index,
                    imageDataProvider = object : ImageProvider() {
                        override suspend fun provideImage(
                            widthPx: Long?,
                            heightPx: Long?
                        ): ByteArray? {
                            if (cachedImages.containsKey(strategy.imageFileName)) cachedImages[strategy.imageFileName]
                            return cachedImages[strategy.imageFileName]
                                ?: if (widthPx != null && heightPx != null) {
                                    appWriteStorage.getImagePreviewByFileName(
                                        fileName = strategy.imageFileName,
                                        widthPx = widthPx,
                                        heightPx = heightPx
                                    )
                                        .also {
                                            cachedImages[strategy.imageFileName] = it
                                        }
                                } else {
                                    appWriteStorage.getImageContentByFileName(
                                        strategy.imageFileName
                                    )
                                        .also {
                                            cachedImages[strategy.imageFileName] = it
                                        }
                                }
                        }
                    }
                )
            }
            .also {
                cachedStrategies.clear()
                cachedStrategies.addAll(it)
            }
    }

    override suspend fun getQuickReads(): List<QuickReadArticle> {
        return assetsReader
            .parseArticlesFromFile()
            .quickReads
            .mapIndexed { index, quickRead ->
                quickRead.toQuickReadArticle(
                    id = index,
                    imageDataProvider = object : ImageProvider() {
                        override suspend fun provideImage(
                            widthPx: Long?,
                            heightPx: Long?
                        ): ByteArray? {
                            if (cachedImages.containsKey(quickRead.imageFileName)) cachedImages[quickRead.imageFileName]
                            return cachedImages[quickRead.imageFileName]
                                ?: if (widthPx != null && heightPx != null) {
                                    appWriteStorage.getImagePreviewByFileName(
                                        fileName = quickRead.imageFileName,
                                        widthPx = widthPx,
                                        heightPx = heightPx
                                    )
                                        .also {
                                            cachedImages[quickRead.imageFileName] = it
                                        }
                                } else {
                                    appWriteStorage.getImageContentByFileName(
                                        quickRead.imageFileName
                                    )
                                        .also {
                                            cachedImages[quickRead.imageFileName] = it
                                        }
                                }

                        }
                    }
                )
            }
            .also {
                cachedQuickReads.clear()
                cachedQuickReads.addAll(it)
            }
    }
}