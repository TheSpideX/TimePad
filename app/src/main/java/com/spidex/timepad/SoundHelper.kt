package com.spidex.timepad

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class SoundHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    fun playSound(totalHourSpend : Long) {
        if (mediaPlayer?.isPlaying == true) return

        val hour = (totalHourSpend)/(1000 * 60 * 60)
        val soundUri = when {
            hour in 0.. 14 -> Uri.parse("android.resource://${context.packageName}/raw/audio1")
            hour in 15..28 -> Uri.parse("android.resource://${context.packageName}/raw/audio2")
            hour in 29..42 -> Uri.parse("android.resource://${context.packageName}/raw/audio3")
            hour in 43..56 -> Uri.parse("android.resource://${context.packageName}/raw/audio4")
            hour in 57..70 -> Uri.parse("android.resource://${context.packageName}/raw/audio5")
            else -> Uri.parse("android.resource://${context.packageName}/raw/audio6")
        }


        if (context.resources.openRawResourceFd(when(hour){
                in 0.. 14 -> R.raw.audio1
                in 15..28 ->R.raw.audio2
                in 29..42 -> R.raw.audio3
                in 43..56 -> R.raw.audio4
                in 57..70 -> R.raw.audio5
                else -> R.raw.audio6
        }) != null) {
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