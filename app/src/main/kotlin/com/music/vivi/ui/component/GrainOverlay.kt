package com.music.vivi.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.random.Random

/**
 * Optional Grain Texture (#16).
 *
 * A static (non-animated, so it costs nothing per-frame beyond the first
 * draw) sparse noise overlay that can sit on top of a gradient or glass
 * surface to avoid the flat, overly-clean "AI gradient" look called out in
 * the design brief. Off by default — opt-in per the [DesignGrainTextureKey]
 * preference, applied by whichever screen chooses to use it in its own batch.
 *
 * Deliberately sparse (alpha 0.03–0.06, low dot count) — grain should be felt,
 * not seen.
 */
@Composable
fun GrainOverlay(
    modifier: Modifier = Modifier,
    dotCount: Int = 400,
    alpha: Float = 0.05f,
) {
    // Fixed seed so the pattern doesn't flicker/change across recompositions.
    val points = remember(dotCount) {
        val random = Random(42)
        List(dotCount) { Offset(random.nextFloat(), random.nextFloat()) }
    }

    Canvas(modifier = modifier) {
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                color = Color.White.copy(alpha = alpha).toArgb()
                isAntiAlias = false
            }
            points.forEach { point ->
                canvas.nativeCanvas.drawPoint(
                    point.x * size.width,
                    point.y * size.height,
                    paint
                )
            }
        }
    }
}
