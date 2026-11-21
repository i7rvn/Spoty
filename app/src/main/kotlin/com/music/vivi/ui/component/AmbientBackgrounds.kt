package com.music.vivi.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

/**
 * Aurora Background Effects (#7) and Mesh Gradient Animations (#8).
 *
 * Both are slow, soft, blurred color blobs — the same underlying technique,
 * different motion profile. Kept intentionally subtle and slow per the
 * design brief ("avoid excessive animation", "avoid excessive gradients"):
 * this is meant to sit quietly behind content, not draw attention to itself.
 *
 * Reduced-motion note: callers should gate the animated variant behind the
 * system/accessibility "reduce motion" preference and fall back to
 * [MeshGradientBackground] with a fixed, non-animated progress value (e.g.
 * 0.5f) when motion should be minimized.
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    colors: List<Color>,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auroraShift"
    )

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .blur(80.dp)
        ) {
            drawAuroraBlobs(colors = colors, progress = shift)
        }
    }
}

@Composable
fun MeshGradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    progress: Float = 0.5f,
) {
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .blur(60.dp)
        ) {
            drawAuroraBlobs(colors = colors, progress = progress)
        }
    }
}

private fun DrawScope.drawAuroraBlobs(colors: List<Color>, progress: Float) {
    if (colors.isEmpty()) return
    val w = size.width
    val h = size.height
    colors.forEachIndexed { index, color ->
        val angle = (progress * 2f * Math.PI + index * (Math.PI / colors.size.coerceAtLeast(1))).toFloat()
        val cx = w * (0.5f + 0.3f * kotlin.math.cos(angle))
        val cy = h * (0.5f + 0.3f * kotlin.math.sin(angle))
        val radius = (w.coerceAtMost(h)) * 0.45f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.35f), Color.Transparent),
                center = Offset(cx, cy),
                radius = radius
            ),
            radius = radius,
            center = Offset(cx, cy)
        )
    }
}

/**
 * Ambient Glow (#9): a single soft radial glow, typically placed behind
 * album artwork or a hero element. Much more restrained than Aurora/Mesh —
 * one color, one source point, no motion by default.
 */
@Composable
fun AmbientGlow(
    modifier: Modifier = Modifier,
    color: Color,
    centerBias: Offset = Offset(0.5f, 0.35f),
) {
    Canvas(modifier = modifier.blur(70.dp)) {
        val center = Offset(size.width * centerBias.x, size.height * centerBias.y)
        val radius = size.minDimension * 0.6f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.4f), Color.Transparent),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}
