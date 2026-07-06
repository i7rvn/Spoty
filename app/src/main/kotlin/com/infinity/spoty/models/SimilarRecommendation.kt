/**
 * Spoty Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.infinity.spoty.models

import com.music.innertube.models.YTItem
import com.infinity.spoty.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
