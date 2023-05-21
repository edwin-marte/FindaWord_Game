package com.em.findaword.presentation

import android.content.Context
import android.media.MediaPlayer
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.em.findaword.R
import com.em.findaword.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val repository: Repository): ViewModel() {
    private var volumeOff = false
    var tap: MediaPlayer = MediaPlayer()
    var success: MediaPlayer = MediaPlayer()
    var failure: MediaPlayer = MediaPlayer()

    var words = listOf<String>()
    var currentWord= ""
    var currentSubString = ""

    fun fetchAllWords(context: Context) = liveData(Dispatchers.IO) {
        emit(repository.getAllWords(context))
    }

    private fun selectRandomWord(words: List<String>): String {
        return words.asSequence().shuffled().distinct().take(1).first()
    }

    fun selectRandomSubStr(): String {
        val word = selectRandomWord(words)
        var maxLength = 1

        if (word.length > 2) maxLength = 2
        if (word.length > 3) maxLength = 3

        var middleAvailable = false

        val length = word.length
        if ((((length / 2)-1) + maxLength) <= length-1) middleAvailable = true

        var last = 1
        if (middleAvailable) last = 2
        val position = (0..last).random()

        var result = ""
        val param = (1..maxLength).random()

        when(position) {
            0 -> result = word.take(param)
            1 -> result = word.takeLast(param)
            2 -> result = word.takeMiddle(((length / 2)-1), param)
        }

        currentWord = word
        currentSubString = result

        return result
    }

    fun checkWordExistence(word: String): Boolean {
        if(words.isNotEmpty() && words.contains(word)) return true
        return false
    }

    fun setupMediaPlayers(context: Context) {
        tap = MediaPlayer.create(context, R.raw.tap_sound)
        success = MediaPlayer.create(context, R.raw.success)
        failure = MediaPlayer.create(context, R.raw.failure)
    }

    private fun unmuteAll() {
        tap.setVolume(1f, 1f)
        success.setVolume(1f, 1f)
        failure.setVolume(1f, 1f)
    }

    private fun muteAll() {
        tap.setVolume(0f, 0f)
        success.setVolume(0f, 0f)
        failure.setVolume(0f, 0f)
    }

    fun setupVolume(button: ImageButton) {
        if (volumeOff) {
            button.setImageResource(R.drawable.volume_off)
            muteAll()
        } else {
            button.setImageResource(R.drawable.volume)
            unmuteAll()
        }
    }

    fun manageVolume(button: ImageButton) {
        volumeOff = if (volumeOff) {
            button.setImageResource(R.drawable.volume)
            unmuteAll()
            false
        } else {
            button.setImageResource(R.drawable.volume_off)
            muteAll()
            true
        }
    }

    private fun String.takeMiddle(start: Int, max: Int): String =  this.substring(start, start+max)
}