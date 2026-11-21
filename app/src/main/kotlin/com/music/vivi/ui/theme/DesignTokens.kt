package com.music.vivi.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spoty Design Tokens — Batch 1 (Module A).
 *
 * A single, shared source of truth for spacing, corner radius, border
 * thickness and elevation, so later batches (Home, Player, Library...)
 * pull from here instead of hardcoding their own values. Nothing here
 * changes how any existing screen looks by itself — screens only change
 * once they're migrated to use these tokens in their own batch.
 */

/** Customizable Spacing System (#20). A small, consistent scale. */
object SpotySpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
}

/** Custom Corner Radius (#13) — a user-selectable preset, not a fixed value. */
enum class SpotyCornerStyle(val radius: Dp) {
    SHARP(0.dp),
    SOFT(8.dp),
    ROUNDED(16.dp),
    PILL(28.dp);

    fun shape() = RoundedCornerShape(radius)
}

/** Custom Border Thickness (#14). */
enum class SpotyBorderThickness(val width: Dp) {
    NONE(0.dp),
    THIN(0.5.dp),
    MEDIUM(1.dp),
    THICK(2.dp)
}

/** Multi-level Shadows (#15) — elevation presets rather than one fixed dp value. */
enum class SpotyShadowLevel(val elevation: Dp) {
    FLAT(0.dp),
    SUBTLE(2.dp),
    ELEVATED(6.dp),
    DRAMATIC(16.dp)
}

/**
 * Adjustable Blur Intensity (#12) and Adjustable Transparency (#11) share one
 * small value class so glass-style components (Batch 1) take a single,
 * consistent "how glassy" input instead of two unrelated raw floats.
 */
data class SpotyGlassIntensity(
    val blurRadius: Dp,
    val surfaceAlpha: Float,
) {
    companion object {
        val SUBTLE = SpotyGlassIntensity(blurRadius = 12.dp, surfaceAlpha = 0.85f)
        val MEDIUM = SpotyGlassIntensity(blurRadius = 24.dp, surfaceAlpha = 0.65f)
        val STRONG = SpotyGlassIntensity(blurRadius = 40.dp, surfaceAlpha = 0.45f)

        /** Builds a custom level from the raw 0f..1f sliders used in Design Studio settings. */
        fun from(blurFraction: Float, transparencyFraction: Float): SpotyGlassIntensity {
            val clampedBlur = blurFraction.coerceIn(0f, 1f)
            val clampedAlpha = transparencyFraction.coerceIn(0f, 1f)
            return SpotyGlassIntensity(
                blurRadius = (clampedBlur * 48f).dp,
                surfaceAlpha = 1f - (clampedAlpha * 0.7f)
            )
        }
    }
}
