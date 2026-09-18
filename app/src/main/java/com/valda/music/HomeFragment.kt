package com.valda.music

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.valda.music.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), SongConsumer {

    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!
    private var adapter: SongAdapter? = null
    private var songs: List<Song> = emptyList()

    override fun onCreateView(
        i: LayoutInflater, c: ViewGroup?, s: Bundle?
    ): View {
        _b = FragmentHomeBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        adapter = SongAdapter(songs) { pos -> playFrom(pos) }
        b.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        b.rvSongs.adapter = adapter

        (activity as? MainActivity)?.let {
            if (it.allSongs.isNotEmpty()) onSongs(it.allSongs)
        }
    }

    override fun onSongs(songs: List<Song>) {
        this.songs = songs
        adapter?.submit(songs)
    }

    private fun playFrom(pos: Int) {
        val svc = MainActivityRef.player ?: return
        svc.setQueue(songs, pos)
        startActivity(Intent(requireContext(), PlayerActivity::class.java))
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
