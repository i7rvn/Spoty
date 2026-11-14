package com.music.vivi.utils

import android.media.AudioManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.view.KeyEvent

/**
 * Quick Settings tile that toggles play/pause for whatever is currently
 * loaded in the app's MediaSession, without needing to open the app.
 *
 * Dispatches a standard media button event (KEYCODE_MEDIA_PLAY_PAUSE) through
 * the system AudioManager. Android routes this to the active MediaSession,
 * which Media3's MediaButtonReceiver (already declared in the manifest) picks
 * up and forwards to MusicService automatically — so this file never touches
 * MusicService's internals directly.
 */
class PlaybackTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        dispatchPlayPause()
        updateTile()
    }

    private fun dispatchPlayPause() {
        val audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager ?: return
        val eventTime = System.currentTimeMillis()
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0)
        )
        audioManager.dispatchMediaKeyEvent(
            KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0)
        )
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        tile.state = Tile.STATE_ACTIVE
        tile.label = "Spoty"
        tile.subtitle = "Play / Pause"
        tile.updateTile()
    }
}
