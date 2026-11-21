package com.music.vivi.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Light Reflection Effects (#17).
 *
 * A single diagonal highlight that sweeps across a surface once every few
 * seconds — meant for ONE hero element per screen at most (e.g. a featured
 * album card), not every card. Used sparingly, this reads as "polished
 * material"; used everywhere, it becomes visual noise, which the design
 * brief explicitly warns against.
 *
 * Respects reduced motion: pass [enabled] = false (wire this to the
 * system/accessibility reduce-motion setting) to render nothing extra.
 */
@Composable
fun LightReflectionEffect(
    modifier: Modifier = Modifier,
    shape: Shape,
    enabled: Boolean = true,
) {
    if (!enabled) return

    val infiniteTransition = rememberInfiniteTransition(label = "lightReflection")
    val sweep by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepPosition"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                val diagonal = size.width + size.height
                val sweepPx = sweep * diagonal
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.14f),
                            Color.Transparent,
                        ),
                        start = Offset(sweepPx - size.height * 0.3f, 0f),
                        end = Offset(sweepPx + size.height * 0.3f, size.height)
                    )
                )
            }
    )
}
