package com.valda.music

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.valda.music.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var player: PlayerService? = null
    var allSongs: List<Song> = emptyList()

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(n: ComponentName?, s: IBinder?) {
            player = (s as PlayerService.LocalBinder).get()
            MainActivityRef.player = player
        }
        override fun onServiceDisconnected(n: ComponentName?) { player = null }
    }

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) loadSongs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val perm = if (Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, perm)
            == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        } else permLauncher.launch(perm)

        binding.bottomNav.setOnItemSelectedListener { item ->
            val frag: Fragment = when (item.itemId) {
                R.id.nav_home    -> HomeFragment()
                R.id.nav_search  -> SearchFragment()
                R.id.nav_library -> LibraryFragment()
                else -> HomeFragment()
            }
            switchFrag(frag)
            true
        }
        binding.bottomNav.selectedItemId = R.id.nav_home

        binding.miniPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
    }

    private fun switchFrag(f: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, f).commit()
    }

    private fun loadSongs() {
        allSongs = SongRepository.loadLocalSongs(this)
        supportFragmentManager.fragments.forEach {
            (it as? SongConsumer)?.onSongs(allSongs)
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, PlayerService::class.java),
            conn, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(conn)
    }
}

interface SongConsumer {
    fun onSongs(songs: List<Song>)
}

object MainActivityRef {
    var player: PlayerService? = null
}
