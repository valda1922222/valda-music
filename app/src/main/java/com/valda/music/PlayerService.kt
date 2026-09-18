package com.valda.music

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class PlayerService : Service() {

    private var mp: MediaPlayer? = null
    private val binder = LocalBinder()
    var currentSong: Song? = null
        private set
    var isPlaying: Boolean = false
        private set

    private val playlist = mutableListOf<Song>()
    private var index = -1

    inner class LocalBinder : Binder() {
        fun get(): PlayerService = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    fun setQueue(songs: List<Song>, startIndex: Int) {
        playlist.clear()
        playlist.addAll(songs)
        index = startIndex
        playCurrent()
    }

    fun playCurrent() {
        val song = playlist.getOrNull(index) ?: return
        this.currentSong = song
        mp?.release()
        val player = MediaPlayer()
        player.setDataSource(applicationContext, android.net.Uri.parse(song.uri))
        player.setOnPreparedListener {
            it.start()
            this.isPlaying = true
            showNotification()
        }
        player.setOnCompletionListener { next() }
        player.prepareAsync()
        mp = player
    }

    fun toggle() {
        val p = mp ?: return
        if (p.isPlaying) {
            p.pause()
            this.isPlaying = false
        } else {
            p.start()
            this.isPlaying = true
        }
        showNotification()
    }

    fun next() {
        if (playlist.isEmpty()) return
        index = (index + 1) % playlist.size
        playCurrent()
    }

    fun prev() {
        if (playlist.isEmpty()) return
        index = if (index - 1 < 0) playlist.size - 1 else index - 1
        playCurrent()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                "valda_music", "Music",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    private fun showNotification() {
        val song = currentSong ?: return
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val n = NotificationCompat.Builder(this, "valda_music")
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pi)
            .setOngoing(isPlaying)
            .build()
        startForeground(1, n)
    }
}
