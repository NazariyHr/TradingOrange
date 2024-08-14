package com.trading.orange.data.local_assets

import android.content.Context
import com.google.gson.Gson
import com.trading.orange.data.local_assets.dto.ArticlesDto
import java.io.BufferedReader
import java.io.FileNotFoundException

class AssetsReader(
    private val context: Context
) {
    fun parseArticlesFromFile(): ArticlesDto {
        val currLocale = context.resources.configuration.locales.get(0).language
        val fileName = when (currLocale) {
            "es" -> "articles_es.json"
            "pt" -> "articles_pt.json"
            "ru", "uk", "kk", "be" -> "articles_ru.json"
            else -> "articles.json"
        }
        val jsonStr: String = try {
            context
                .assets
                .open(fileName)
                .bufferedReader()
                .use(BufferedReader::readText)
        } catch (e: FileNotFoundException) {
            context
                .assets
                .open("articles.json")
                .bufferedReader()
                .use(BufferedReader::readText)
        }

        return Gson().fromJson(jsonStr, ArticlesDto::class.java)
    }
}