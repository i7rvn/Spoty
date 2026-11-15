package com.music.vivi.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.music.vivi.LocalDatabase
import com.music.vivi.LocalPlayerAwareWindowInsets
import com.music.vivi.R
import kotlinx.coroutines.launch

/**
 * Finds songs in the library that look like duplicates (same normalized title
 * + same primary artist, ignoring "(feat. ...)" noise) and lets the person
 * remove the extra copies with one tap. Only affects library membership
 * (same soft-remove the rest of the app already uses) — never touches
 * playback, downloads or any other business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateSongsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()
    val songs by database.allSongs().collectAsState(initial = emptyList())

    val duplicateGroups = remember(songs) {
        songs
            .filter { it.song.inLibrary != null }
            .groupBy { song ->
                val normalizedTitle = song.title
                    .trim()
                    .lowercase()
                    .replace(Regex("\\(feat[^)]*\\)"), "")
                    .replace(Regex("\\[feat[^]]*\\]"), "")
                    .trim()
                val primaryArtist = song.artists.firstOrNull()?.name?.trim()?.lowercase().orEmpty()
                "$normalizedTitle|$primaryArtist"
            }
            .values
            .filter { it.size > 1 }
            .toList()
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
    ) {
        if (duplicateGroups.isEmpty()) {
            Text(
                text = stringResource(R.string.no_duplicate_songs_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(duplicateGroups) { group ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = group.first().title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = group.first().artists.joinToString { it.name },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            group.forEach { song ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.duplicate_song_duration,
                                            song.song.duration / 60,
                                            song.song.duration % 60
                                        ),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                database.query {
                                                    inLibrary(song.song.id, null)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.close),
                                            contentDescription = stringResource(R.string.remove_from_library)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.duplicate_songs)) },
        navigationIcon = {
            IconButton(onClick = navController::navigateUp) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
