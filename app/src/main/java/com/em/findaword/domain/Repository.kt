package com.em.findaword.domain

import android.content.Context
import com.em.findaword.core.Resource

interface Repository {
    suspend fun getAllWords(context: Context): Resource<List<String>>
}