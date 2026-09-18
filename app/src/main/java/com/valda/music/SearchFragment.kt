package com.valda.music

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.valda.music.databinding.FragmentSearchBinding

class SearchFragment : Fragment(), SongConsumer {

    private var _b: FragmentSearchBinding? = null
    private val b get() = _b!!
    private var adapter: SongAdapter? = null
    private var songs: List<Song> = emptyList()

    override fun onCreateView(
        i: LayoutInflater, c: ViewGroup?, s: Bundle?
    ): View {
        _b = FragmentSearchBinding.inflate(i, c, false)
        return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        adapter = SongAdapter(emptyList()) { pos ->
            val filtered = filter(b.etSearch.text.toString())
            val svc = MainActivityRef.player ?: return@SongAdapter
            svc.setQueue(filtered, pos)
            startActivity(Intent(requireContext(), PlayerActivity::class.java))
        }
        b.rvResults.layoutManager = LinearLayoutManager(requireContext())
        b.rvResults.adapter = adapter

        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter?.submit(filter(s?.toString() ?: ""))
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })
    }

    private fun filter(q: String): List<Song> {
        if (q.isBlank()) return songs
        val s = q.lowercase()
        return songs.filter {
            it.title.lowercase().contains(s) ||
            it.artist.lowercase().contains(s)
        }
    }

    override fun onSongs(songs: List<Song>) {
        this.songs = songs
        adapter?.submit(filter(b.etSearch.text.toString()))
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
