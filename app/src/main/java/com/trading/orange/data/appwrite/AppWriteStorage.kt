package com.trading.orange.data.appwrite

import com.trading.orange.data.appwrite.AppWriteConstants.ARTICLES_IMAGES_BUCKET_NAME
import com.trading.orange.data.appwrite.AppWriteConstants.NEWS_IMAGES_BUCKET_NAME
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.services.Storage

class AppWriteStorage {
    companion object {
        private const val ENDPOINT = "https://cloud.appwrite.io/v1"
        private const val PROJECT_ID = "66bdd7ca00045b0e473f"
        private const val SECRET_KEY =
            "66e7a7e2bd73c85f00cd236eee57d93c52010d5165df24b16f3600677617aa05f8141d677c296488df8dd1591cb5fbe5c39441bbd664dd481f7bf4c6380b9afd5c8632c7536328332219656b920c31d17666d25fe063ab149f10c4421b3f30c525765d6e85e061b1e4f903c08d8ce80090445e8e81d5dbc091a13c3a498610a4"
    }

    private val client = Client()
        .setEndpoint(ENDPOINT)
        .setProject(PROJECT_ID)
        .setKey(SECRET_KEY)
        .setSelfSigned(true)

    private val storage = Storage(client)

    suspend fun getRandomImageFileIds(neededImagesCount: Int): List<String> {
        val bucketId = getNewsImagesBucketId() ?: return emptyList()

        val allImagesIdsList = storage.listFiles(bucketId).files.map {
            it.id
        }
        val imagesCountToReturn =
            if (allImagesIdsList.count() < neededImagesCount) allImagesIdsList.count() else neededImagesCount

        val filesIdsToFetch = mutableListOf<String>()
        repeat(imagesCountToReturn) {
            allImagesIdsList
                .filterNot { filesIdsToFetch.contains(it) }
                .let {
                    filesIdsToFetch.add(it.random())
                }
        }

        return filesIdsToFetch
    }

    suspend fun getImageContent(fileId: String): ByteArray? {
        val bucketId = getNewsImagesBucketId() ?: return null
        return storage.getFileView(
            bucketId = bucketId,
            fileId = fileId
        )
    }

    suspend fun getImagePreview(
        fileId: String,
        widthPx: Long,
        heightPx: Long
    ): ByteArray? {
        val bucketId = getNewsImagesBucketId() ?: return null
        return storage.getFilePreview(
            bucketId = bucketId,
            fileId = fileId,
            width = widthPx,
            height = heightPx
        )
    }

    suspend fun getImageContentByFileName(fileName: String): ByteArray? {
        val bucketId = getArticlesImagesBucketId() ?: return null

        val fileId = storage
            .listFiles(bucketId = bucketId)
            .files
            .firstOrNull { it.name == fileName }
            ?.id ?: return null

        return storage.getFileView(
            bucketId = bucketId,
            fileId = fileId
        )
    }

    suspend fun getImagePreviewByFileName(
        fileName: String,
        widthPx: Long,
        heightPx: Long
    ): ByteArray? {
        val bucketId = getArticlesImagesBucketId() ?: return null

        val fileId = storage
            .listFiles(bucketId = bucketId)
            .files
            .firstOrNull { it.name == fileName }
            ?.id ?: return null

        return storage.getFilePreview(
            bucketId = bucketId,
            fileId = fileId,
            width = widthPx,
            height = heightPx
        )
    }

    private suspend fun getNewsImagesBucketId(): String? {
        return storage
            .listBuckets(queries = listOf(Query.search("name", NEWS_IMAGES_BUCKET_NAME)))
            .buckets
            .firstOrNull()
            ?.id
    }

    private suspend fun getArticlesImagesBucketId(): String? {
        return storage
            .listBuckets(queries = listOf(Query.search("name", ARTICLES_IMAGES_BUCKET_NAME)))
            .buckets
            .firstOrNull()
            ?.id
    }
}