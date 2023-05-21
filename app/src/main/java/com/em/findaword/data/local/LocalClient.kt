package com.em.findaword.data.local

import android.content.Context
import com.em.findaword.R

class LocalClient(context: Context) {
    val jsonData = context.resources.openRawResource(
        R.raw.eng_words
    ).bufferedReader().use { it.readText() }
}