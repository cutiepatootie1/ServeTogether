package com.main.servetogether.data.source

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

// <T> means "This class works with any Type"
class JsonStore<T>(
    private val context: Context,
    private val fileName: String
) {
    val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun getFile(): File = File(context.filesDir, fileName)

    // We use 'inline' and 'reified' so Kotlin knows how to serialize T at runtime
    inline fun <reified T> save(data: T) {
        val jsonString = json.encodeToString(data)
        getFile().writeText(jsonString)
    }

    inline fun <reified T> load(): T? {
        val file = getFile()
        if (!file.exists()) return null

        return try {
            val jsonString = file.readText()
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clear() {
        if (getFile().exists()) getFile().delete()
    }
}