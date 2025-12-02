package com.main.servetogether.data.source

import android.content.Context
import com.main.servetogether.data.models.UserData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LocalFileStorage (private val context : Context){
    private val fileName = "user_data.json"

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true}

    private fun getFile(): File = File (context.filesDir, fileName)

    fun save(data: UserData) {
        val jsonString = json.encodeToString(data)
        getFile().writeText(jsonString)
    }

    fun load(): UserData? {
        val file = getFile()
        if (!file.exists()) return null

        return try {
            val jsonString = file.readText()
            json.decodeFromString<UserData>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clear() {
        val file = getFile()
        if (file.exists()) file.delete()
    }
}