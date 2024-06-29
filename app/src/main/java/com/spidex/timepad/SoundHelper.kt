package com.spidex.timepad

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class SoundHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    fun playSound() {
        if (mediaPlayer?.isPlaying == true) return

        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/task_complete_audio")

        if (context.resources.openRawResourceFd(R.raw.task_complete_audio) != null) {
            mediaPlayer = MediaPlayer.create(context, soundUri)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else {
            Log.e("SoundHelper", "Sound file not found or invalid")
        }
    }
    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}