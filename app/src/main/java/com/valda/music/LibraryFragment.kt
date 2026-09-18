package com.valda.music

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.valda.music.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment(), SongConsumer {
    private var _b: FragmentLibraryBinding? = null
    private val b get() = _b!!
    private var adapter: SongAdapter? = null
    private var songs: List<Song> = emptyList()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentLibraryBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        adapter = SongAdapter(songs) { pos ->
            val svc = MainActivityRef.player ?: return@SongAdapter
            svc.setQueue(songs, pos)
            startActivity(Intent(requireContext(), PlayerActivity::class.java))
        }
        b.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        b.rvSongs.adapter = adapter
        (activity as? MainActivity)?.let {
            if (it.allSongs.isNotEmpty()) onSongs(it.allSongs)
        }
    }

    override fun onSongs(songs: List<Song>) {
        this.songs = songs; adapter?.submit(songs)
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
