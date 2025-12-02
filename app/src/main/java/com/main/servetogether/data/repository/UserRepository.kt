package com.main.servetogether.data.repository

import com.main.servetogether.data.models.UserData
import com.main.servetogether.data.source.LocalFileStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val storage : LocalFileStorage) {
    suspend fun saveUser(user : UserData) = withContext(Dispatchers.IO){
        storage.save(user)
    }

    suspend fun getUser(): UserData? = withContext(Dispatchers.IO){
        storage.load()
    }

}