/**
 * Spoty Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.infinity.spoty.lyrics

import android.content.Context
import com.music.musixmatch.Musixmatch
import com.infinity.spoty.constants.EnableMusixmatchKey
import com.infinity.spoty.utils.dataStore
import com.infinity.spoty.utils.get

object MusixmatchLyricsProvider : LyricsProvider {
    override val name = "Musixmatch"

    override fun isEnabled(context: Context): Boolean =
        context.dataStore[EnableMusixmatchKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> =
        Musixmatch.getLyrics(title, artist, duration, album)
}
