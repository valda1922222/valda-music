package com.valda.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.valda.music.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var b: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnPlay.setOnClickListener {
            MainActivityRef.player?.toggle()
            updateUI()
        }
        b.btnNext.setOnClickListener {
            MainActivityRef.player?.next(); updateUI()
        }
        b.btnPrev.setOnClickListener {
            MainActivityRef.player?.prev(); updateUI()
        }
        b.btnClose.setOnClickListener { finish() }
        updateUI()
    }

    private fun updateUI() {
        val p = MainActivityRef.player ?: return
        val s = p.currentSong
        b.title.text = s?.title ?: "-"
        b.artist.text = s?.artist ?: "-"
        b.btnPlay.text = if (p.isPlaying) "⏸" else "▶"
    }
}
