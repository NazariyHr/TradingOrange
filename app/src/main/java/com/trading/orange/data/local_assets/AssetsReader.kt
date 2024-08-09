package com.trading.orange.data.local_assets

import android.content.Context
import com.google.gson.Gson
import com.trading.orange.data.local_assets.dto.ArticlesDto
import java.io.BufferedReader

class AssetsReader(
    private val context: Context
) {
    fun parseArticlesFromFile(): ArticlesDto {
        val jsonStr = context
            .assets
            .open("articles.json")
            .bufferedReader()
            .use(BufferedReader::readText)
        return Gson().fromJson(jsonStr, ArticlesDto::class.java)
    }
}