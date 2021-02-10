package com.example.cepnokotlin.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cepnokotlin.R

private lateinit var mpSplash: MediaPlayer
private var totalTime: Int = 0

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        executarIntro()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun executarIntro() {
        mpSplash = MediaPlayer.create(this, R.raw.konvict)
        mpSplash.isLooping = false
        mpSplash.setVolume(0.5f, 0.5f)
        totalTime = mpSplash.duration
        mpSplash.start()
    }
}