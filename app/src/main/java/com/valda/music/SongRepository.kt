package com.valda.music

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object SongRepository {

    fun loadLocalSongs(context: Context): List<Song> {
        val list = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        cursor?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val tCol  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val aCol  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val alCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val dCol  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                ).toString()
                list += Song(
                    id = id,
                    title = c.getString(tCol) ?: "Unknown",
                    artist = c.getString(aCol) ?: "Unknown",
                    album = c.getString(alCol) ?: "Unknown",
                    durationMs = c.getLong(dCol),
                    uri = uri,
                    albumArtUri = null
                )
            }
        }
        return list
    }
}
