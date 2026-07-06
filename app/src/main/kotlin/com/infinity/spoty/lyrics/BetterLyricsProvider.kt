/**
 * Spoty Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.infinity.spoty.lyrics

import android.content.Context
import com.infinity.spoty.betterlyrics.BetterLyrics
import com.infinity.spoty.constants.EnableBetterLyricsKey
import com.infinity.spoty.utils.dataStore
import com.infinity.spoty.utils.get

object BetterLyricsProvider : LyricsProvider {
    override val name = "BetterLyrics"

    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableBetterLyricsKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = BetterLyrics.getLyrics(title, artist, duration, album)

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
        callback: (String) -> Unit,
    ) {
        BetterLyrics.getAllLyrics(title, artist, duration, album, callback)
    }
}
