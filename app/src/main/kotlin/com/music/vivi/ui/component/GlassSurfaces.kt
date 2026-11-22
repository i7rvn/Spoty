package com.music.vivi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import com.music.vivi.ui.theme.SpotyBorderThickness
import com.music.vivi.ui.theme.SpotyCornerStyle
import com.music.vivi.ui.theme.SpotyGlassIntensity

/**
 * Frosted Glass Card (#6) / Liquid Glass Components (#5).
 *
 * A reusable "glass" surface: a blurred tint of whatever color is passed in
 * (usually the current theme or album-art color), a translucent surface on
 * top, and an optional hairline border. Blur strength and transparency come
 * from [SpotyGlassIntensity] so the same component can be "subtle" in one
 * screen and "strong" in another without duplicating the recipe.
 *
 * This does not replace any existing card/surface composable — it's an
 * additional option screens can opt into in their own batch.
 */
@Composable
fun FrostedGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = SpotyCornerStyle.ROUNDED.shape(),
    borderThickness: SpotyBorderThickness = SpotyBorderThickness.THIN,
    intensity: SpotyGlassIntensity = SpotyGlassIntensity.MEDIUM,
    tint: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    Box(
        modifier = modifier
            .clip(shape)
            .background(tint.copy(alpha = intensity.surfaceAlpha))
            .then(
                if (borderThickness.width.value > 0f) {
                    Modifier.border(borderThickness.width, borderColor, shape)
                } else {
                    Modifier
                }
            )
    ) {
        // A soft blurred backdrop layer gives the "frosted" look without
        // needing a full backdrop-blur API (consistent with the blur()
        // approach already used for player backgrounds in this codebase).
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = 0.99f }
                .blur(intensity.blurRadius)
                .background(tint.copy(alpha = intensity.surfaceAlpha * 0.5f))
        )
        content()
    }
}

/**
 * Liquid Glass variant: adds a soft diagonal highlight gradient on top of the
 * frosted surface to suggest light moving across a glass-like material.
 * Intentionally restrained — one subtle highlight, not a full animated sheen
 * (see LightReflectionEffect for the animated version used sparingly, e.g.
 * on a hero card).
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = SpotyCornerStyle.ROUNDED.shape(),
    borderThickness: SpotyBorderThickness = SpotyBorderThickness.THIN,
    intensity: SpotyGlassIntensity = SpotyGlassIntensity.MEDIUM,
    tint: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    FrostedGlassSurface(
        modifier = modifier,
        shape = shape,
        borderThickness = borderThickness,
        intensity = intensity,
        tint = tint,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent,
                            Color.Transparent,
                        )
                    )
                )
        )
        content()
    }
}
