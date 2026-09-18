package com.valda.music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valda.music.databinding.ItemSongBinding

class SongAdapter(
    private var data: List<Song>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.VH>() {

    inner class VH(val b: ItemSongBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        val b = ItemSongBinding.inflate(
            LayoutInflater.from(p.context), p, false)
        return VH(b)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = data[pos]
        h.b.title.text = s.title
        h.b.artist.text = s.artist
        h.b.root.setOnClickListener { onClick(pos) }
    }

    override fun getItemCount() = data.size

    fun submit(list: List<Song>) {
        data = list
        notifyDataSetChanged()
    }
}
