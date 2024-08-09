package com.trading.orange.data.appwrite

import com.trading.orange.data.appwrite.AppWriteConstants.NEWS_IMAGES_BUCKET_NAME
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.services.Storage

class AppWriteStorage {
    companion object {
        private const val ENDPOINT = "https://cloud.appwrite.io/v1"
        private const val PROJECT_ID = "66b4af9f0035af13ee03"
        private const val SECRET_KEY =
            "8638506b884ea3f79b211652fc0548eaaa85762828f140c191536f57984c20c683e432d01982bf1e8705d3a5a321f9cdafad37837badda182abf7d9eff2d083fd257eb7522e2deb7ba5dde1b4a12c09718e187b6251d41208d3928f3025109b3dcc2bd18d589e7cf7713ff802d3948bdefa61b078e449eb5ef19880358dd1e0f"
    }

    private val client = Client()
        .setEndpoint(ENDPOINT)
        .setProject(PROJECT_ID)
        .setKey(SECRET_KEY)
        .setSelfSigned(true)

    private val storage = Storage(client)

    suspend fun getRandomImages(neededImagesCount: Int): List<ByteArray> {
        val bucketId = getNewsImagesBucketId() ?: return emptyList()

        val allImagesIdsList = storage.listFiles(bucketId).files.map { it.id }
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
            .map { fileId ->
                storage.getFileView(
                    bucketId = bucketId,
                    fileId = fileId
                )
            }
    }

    suspend fun getRandomImageFileIds(neededImagesCount: Int): List<String> {
        val bucketId = getNewsImagesBucketId() ?: return emptyList()

        val allImagesIdsList = storage.listFiles(bucketId).files.map { it.id }
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

    private suspend fun getNewsImagesBucketId(): String? {
        return storage
            .listBuckets(queries = listOf(Query.search("name", NEWS_IMAGES_BUCKET_NAME)))
            .buckets
            .firstOrNull()
            ?.id
    }
}