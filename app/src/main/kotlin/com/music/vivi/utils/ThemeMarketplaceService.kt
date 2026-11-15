package com.music.vivi.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

@Serializable
data class CommunityTheme(
    val name: String,
    val author: String,
    val colorHex: String,
)

/**
 * Themes are just a name + author + hex color — no server, no accounts.
 * A small curated list ships inside the app so the marketplace has content
 * from the very first launch with zero setup. On top of that, it tries to
 * fetch extra community-submitted themes from a plain JSON file in the
 * project's own public GitHub repo; if that file doesn't exist yet or the
 * device is offline, it silently falls back to the built-in list only —
 * the feature never depends on anything paid or unreliable.
 *
 * To add more themes later without an app update: edit
 * themes/community-themes.json in the repo with entries like
 * {"name": "...", "author": "...", "colorHex": "#RRGGBB"}.
 */
object ThemeMarketplaceService {

    private const val REMOTE_THEMES_URL =
        "https://raw.githubusercontent.com/i7rvn/Spoty/main/themes/community-themes.json"

    val builtInThemes = listOf(
        CommunityTheme("Spoty Violet", "Spoty", "#8A5CFF"),
        CommunityTheme("Midnight Teal", "Spoty", "#14B8A6"),
        CommunityTheme("Sunset Coral", "Spoty", "#FF6B6B"),
        CommunityTheme("Forest", "Spoty", "#2F9E44"),
        CommunityTheme("Royal Blue", "Spoty", "#3B5BDB"),
        CommunityTheme("Hot Pink", "Spoty", "#E64980"),
        CommunityTheme("Amber", "Spoty", "#F59F00"),
        CommunityTheme("Slate", "Spoty", "#495057"),
    )

    private val client by lazy {
        HttpClient(OkHttp) {
            engine { preconfigured = OkHttpClient() }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
    }

    /** Built-in themes plus any reachable remote ones, de-duplicated by name. */
    suspend fun fetchAll(): List<CommunityTheme> {
        val remote = try {
            client.get(REMOTE_THEMES_URL).body<List<CommunityTheme>>()
        } catch (_: Exception) {
            emptyList()
        }
        return (builtInThemes + remote).distinctBy { it.name.lowercase() }
    }
}
