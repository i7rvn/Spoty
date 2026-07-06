/**
 * Spoty Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.infinity.spoty.ui.screens

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.infinity.spoty.constants.DarkModeKey
import com.infinity.spoty.constants.PureBlackKey
import com.infinity.spoty.ui.screens.artist.ArtistAlbumsScreen
import com.infinity.spoty.ui.screens.artist.ArtistItemsScreen
import com.infinity.spoty.ui.screens.artist.ArtistScreen
import com.infinity.spoty.ui.screens.artist.ArtistSongsScreen
import com.infinity.spoty.ui.screens.equalizer.EqScreen
import com.infinity.spoty.ui.screens.library.LibraryScreen
import com.infinity.spoty.ui.screens.playlist.AutoPlaylistScreen
import com.infinity.spoty.ui.screens.playlist.CachePlaylistScreen
import com.infinity.spoty.ui.screens.playlist.LocalPlaylistScreen
import com.infinity.spoty.ui.screens.playlist.OnlinePlaylistScreen
import com.infinity.spoty.ui.screens.playlist.TopPlaylistScreen
import com.infinity.spoty.ui.screens.search.OnlineSearchResult
import com.infinity.spoty.ui.screens.search.SearchScreen
import com.infinity.spoty.ui.screens.settings.AboutScreen
import com.infinity.spoty.ui.screens.settings.AppearanceSettings
import com.infinity.spoty.ui.screens.settings.CanvasSelection
import com.infinity.spoty.ui.screens.settings.FontSelectionScreen
import com.infinity.spoty.ui.screens.settings.BackupAndRestore
import com.infinity.spoty.ui.screens.settings.AutoBackupSettings
import com.infinity.spoty.ui.screens.settings.SpotifyScreen
import com.infinity.spoty.viewmodels.SpotifyImportViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.infinity.spoty.ui.screens.settings.ContentSettings
import com.infinity.spoty.ui.screens.settings.DarkMode
import com.infinity.spoty.ui.screens.settings.DiscordLoginScreen
import com.infinity.spoty.ui.screens.settings.PlayerSettings
import com.infinity.spoty.ui.screens.settings.JioSettings
import com.infinity.spoty.ui.screens.settings.CipherSettings
import com.infinity.spoty.ui.screens.settings.PrivacySettings
import com.infinity.spoty.ui.screens.settings.RomanizationSettings
import com.infinity.spoty.ui.screens.settings.SettingsScreen
import com.infinity.spoty.ui.screens.settings.AccountSettingsScreen
import com.infinity.spoty.ui.screens.settings.StorageSettings
import com.infinity.spoty.ui.screens.settings.ThemeScreen
import com.infinity.spoty.ui.screens.settings.AiSettings
import com.infinity.spoty.ui.screens.settings.integrations.DiscordSettings
import com.infinity.spoty.ui.screens.settings.integrations.IntegrationScreen
import com.infinity.spoty.ui.screens.settings.integrations.LastFMSettings
import com.infinity.spoty.ui.screens.settings.integrations.ListenTogetherSettings
import com.infinity.spoty.ui.screens.recognition.RecognitionScreen
import com.infinity.spoty.ui.screens.recognition.RecognitionHistoryScreen
import com.infinity.spoty.ui.screens.settings.UpdateSettings
import com.infinity.spoty.ui.screens.settings.NotificationPermission
import com.infinity.spoty.ui.screens.wrapped.WrappedScreen
import com.infinity.spoty.updater_lib.updater.UpdateScreen
import com.infinity.spoty.utils.rememberEnumPreference
import com.infinity.spoty.utils.rememberPreference
import com.infinity.spoty.updater_lib.changelog.ChangelogScreen
import com.infinity.spoty.updater_lib.commitscreen.CommitScreen
import com.infinity.spoty.ui.screens.equalizer.axion.AxionEqScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.navigationBuilder(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    activity: Activity,
    snackbarHostState: SnackbarHostState
) {
    composable(Screens.Home.route) {
        HomeScreen(navController = navController, snackbarHostState = snackbarHostState)
    }

    composable(Screens.Search.route) {
        val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
        val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }
        val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
            pureBlackEnabled && useDarkTheme
        }
        SearchScreen(
            navController = navController,
            pureBlack = pureBlack
        )
    }

    composable(Screens.Library.route) {
        LibraryScreen(navController)
    }

    composable(Screens.ListenTogether.route) {
        ListenTogetherScreen(navController, showTopBar = false)
    }

    composable(
        route = "listen_together_from_topbar",
    ) {
        ListenTogetherScreen(navController, showTopBar = true)
    }

    composable("listen_together/chat") {
        CommentTogetherScreen(navController)
    }

    composable("history") {
        HistoryScreen(navController)
    }

    composable("stats") {
        StatsScreen(navController)
    }

    composable("mood_and_genres") {
        MoodAndGenresScreen(navController, scrollBehavior)
    }

    composable("account") {
        AccountScreen(navController, scrollBehavior)
    }

    composable("new_release") {
        NewReleaseScreen(navController, scrollBehavior)
    }

    composable("charts_screen") {
        ChartsScreen(navController)
    }

    composable(
        route = "browse/{browseId}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
            }
        )
    ) {
        BrowseScreen(
            navController,
            scrollBehavior,
            it.arguments?.getString("browseId")
        )
    }

    composable(
        route = "search/{query}",
        arguments = listOf(
            navArgument("query") {
                type = NavType.StringType
            },
        ),
        enterTransition = {
            fadeIn(tween(250))
        },
        exitTransition = {
            if (targetState.destination.route?.startsWith("search/") == true) {
                fadeOut(tween(200))
            } else {
                fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
            }
        },
        popEnterTransition = {
            if (initialState.destination.route?.startsWith("search/") == true) {
                fadeIn(tween(250))
            } else {
                fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
            }
        },
        popExitTransition = {
            fadeOut(tween(200))
        },
    ) {
        OnlineSearchResult(navController)
    }

    composable(
        route = "album/{albumId}",
        arguments = listOf(
            navArgument("albumId") {
                type = NavType.StringType
            },
        ),
    ) {
        AlbumScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/songs",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
        ),
    ) {
        ArtistSongsScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/albums",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            }
        )
    ) {
        ArtistAlbumsScreen(navController, scrollBehavior)
    }

    composable(
        route = "artist/{artistId}/items?browseId={browseId}?params={params}",
        arguments = listOf(
            navArgument("artistId") {
                type = NavType.StringType
            },
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        ArtistItemsScreen(navController, scrollBehavior)
    }

    composable(
        route = "online_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        OnlinePlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "local_playlist/{playlistId}",
        arguments = listOf(
            navArgument("playlistId") {
                type = NavType.StringType
            },
        ),
    ) {
        LocalPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "auto_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        AutoPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "cache_playlist/{playlist}",
        arguments = listOf(
            navArgument("playlist") {
                type = NavType.StringType
            },
        ),
    ) {
        CachePlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "top_playlist/{top}",
        arguments = listOf(
            navArgument("top") {
                type = NavType.StringType
            },
        ),
    ) {
        TopPlaylistScreen(navController, scrollBehavior)
    }

    composable(
        route = "youtube_browse/{browseId}?params={params}",
        arguments = listOf(
            navArgument("browseId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("params") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        YouTubeBrowseScreen(navController)
    }

    composable("settings") {
        SettingsScreen(navController, scrollBehavior)
    }

    composable("settings/update") {
       UpdateSettings(navController, scrollBehavior)
    }

    composable("settings/update/notification_permission") {
        NotificationPermission(navController, scrollBehavior)
    }

    composable("settings/account") {
        AccountSettingsScreen(navController, scrollBehavior)
    }

    composable("settings/appearance") {
        AppearanceSettings(navController, scrollBehavior, activity, snackbarHostState)
    }

    composable("settings/appearance/theme") {
        ThemeScreen(navController)
    }

    composable("settings/appearance/canvas") {
        CanvasSelection(navController, scrollBehavior)
    }

    composable("settings/appearance/font") {
        FontSelectionScreen(navController, scrollBehavior)
    }

    composable("settings/content") {
        ContentSettings(navController, scrollBehavior)
    }

    composable("settings/content/romanization") {
        RomanizationSettings(navController, scrollBehavior)
    }

    composable("settings/ai") {
        AiSettings(navController, scrollBehavior)
    }
    
    composable("settings/player") {
        PlayerSettings(navController, scrollBehavior)
    }

    composable("settings/player/jio") {
        JioSettings(navController, scrollBehavior)
    }

    composable("settings/player/cipher") {
        CipherSettings(navController, scrollBehavior)
    }

    composable("settings/storage") {
        StorageSettings(navController, scrollBehavior)
    }

    composable("settings/equalizer") {
        AxionEqScreen(onBackClick = { navController.navigateUp() })
    }

    composable("settings/privacy") {
        PrivacySettings(navController, scrollBehavior)
    }

    composable("settings/backup_restore") {
        BackupAndRestore(navController, scrollBehavior)
    }

    composable("settings/backup_restore/autobackup") {
        AutoBackupSettings(navController, scrollBehavior)
    }

    composable("settings/spotify") {
        SpotifyScreen(navController, scrollBehavior)
    }



    composable("settings/integrations") {
        IntegrationScreen(navController, scrollBehavior)
    }

    composable("settings/integrations/discord") {
        DiscordSettings(navController, scrollBehavior, snackbarHostState)
    }

    composable("settings/integrations/lastfm") {
        LastFMSettings(navController, scrollBehavior)
    }

    composable(route = "settings/integrations/listen_together") {
        ListenTogetherSettings(navController, scrollBehavior)
    }

    composable("settings/discord/login") {
        DiscordLoginScreen(navController)
    }

    composable("settings/about") {
        AboutScreen(navController, scrollBehavior)
    }

    composable("update") {
        UpdateScreen(navController)
    }

    composable("login") {
        LoginScreen(navController)
    }

    composable("wrapped") {
        WrappedScreen(navController)
    }

    dialog("equalizer") {
        EqScreen(navController = navController)
    }

    composable("recognition") {
        RecognitionScreen(navController)
    }

    composable("recognition_history") {
        RecognitionHistoryScreen(navController)
    }
    composable("settings/changelog") {
        ChangelogScreen(navController,scrollBehavior)
    }
    composable("settings/commits") {
        CommitScreen(navController, scrollBehavior)
    }
}
