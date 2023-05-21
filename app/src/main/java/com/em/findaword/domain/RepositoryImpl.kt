package com.em.findaword.domain

import android.content.Context
import com.em.findaword.core.Resource
import com.em.findaword.data.local.LocalClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {
    override suspend fun getAllWords(context: Context): Resource<List<String>> {
        val localClient = LocalClient(context)
        val list: List<String> =
            Gson().fromJson(localClient.jsonData, object : TypeToken<List<String>>() {}.type)
        return Resource.Success(list)
    }
}