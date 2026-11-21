package com.music.vivi.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.music.vivi.LocalPlayerAwareWindowInsets
import com.music.vivi.R
import com.music.vivi.constants.DesignAmbientGlowKey
import com.music.vivi.constants.DesignBlurIntensityKey
import com.music.vivi.constants.DesignBorderThicknessKey
import com.music.vivi.constants.DesignCornerStyleKey
import com.music.vivi.constants.DesignGrainTextureKey
import com.music.vivi.constants.DesignShadowLevelKey
import com.music.vivi.constants.DesignTransparencyKey
import com.music.vivi.ui.component.AmbientGlow
import com.music.vivi.ui.component.GrainOverlay
import com.music.vivi.ui.component.LiquidGlassSurface
import com.music.vivi.ui.theme.DefaultThemeColor
import com.music.vivi.ui.theme.SpotyBorderThickness
import com.music.vivi.ui.theme.SpotyCornerStyle
import com.music.vivi.ui.theme.SpotyGlassIntensity
import com.music.vivi.ui.theme.SpotyShadowLevel
import com.music.vivi.utils.rememberEnumPreference
import com.music.vivi.utils.rememberPreference

/**
 * Design Studio (Batch 1): the settings surface for the Module A tokens.
 * Everything here is additive — it does not touch how any existing screen
 * currently renders. A later batch migrates specific screens to read these
 * tokens; this screen is where the person sets their preference and sees it
 * previewed live first.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignStudioSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var cornerStyle by rememberEnumPreference(DesignCornerStyleKey, SpotyCornerStyle.ROUNDED)
    var borderThickness by rememberEnumPreference(DesignBorderThicknessKey, SpotyBorderThickness.THIN)
    var shadowLevel by rememberEnumPreference(DesignShadowLevelKey, SpotyShadowLevel.SUBTLE)
    val (blurFraction, onBlurFractionChange) = rememberPreference(DesignBlurIntensityKey, defaultValue = 0.5f)
    val (transparencyFraction, onTransparencyFractionChange) = rememberPreference(DesignTransparencyKey, defaultValue = 0.5f)
    val (grainTexture, onGrainTextureChange) = rememberPreference(DesignGrainTextureKey, defaultValue = false)
    val (ambientGlow, onAmbientGlowChange) = rememberPreference(DesignAmbientGlowKey, defaultValue = true)

    val intensity = SpotyGlassIntensity.from(blurFraction, transparencyFraction)

    Column(
        modifier = Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Live preview card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .padding(bottom = 24.dp)
        ) {
            if (ambientGlow) {
                AmbientGlow(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    color = DefaultThemeColor
                )
            }
            LiquidGlassSurface(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
                shape = cornerStyle.shape(),
                borderThickness = borderThickness,
                intensity = intensity,
                tint = DefaultThemeColor,
            ) {
                if (grainTexture) {
                    GrainOverlay(modifier = Modifier.fillMaxWidth().aspectRatio(1.6f))
                }
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.design_studio_preview_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.design_studio_preview_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.design_studio_corner_style),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 20.dp)) {
            SpotyCornerStyle.entries.forEach { option ->
                FilterChip(
                    selected = cornerStyle == option,
                    onClick = { cornerStyle = option },
                    label = { Text(option.name) }
                )
            }
        }

        Text(
            text = stringResource(R.string.design_studio_border_thickness),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 20.dp)) {
            SpotyBorderThickness.entries.forEach { option ->
                FilterChip(
                    selected = borderThickness == option,
                    onClick = { borderThickness = option },
                    label = { Text(option.name) }
                )
            }
        }

        Text(
            text = stringResource(R.string.design_studio_shadow_level),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 20.dp)) {
            SpotyShadowLevel.entries.forEach { option ->
                FilterChip(
                    selected = shadowLevel == option,
                    onClick = { shadowLevel = option },
                    label = { Text(option.name) }
                )
            }
        }

        Text(
            text = stringResource(R.string.design_studio_blur_intensity),
            style = MaterialTheme.typography.titleSmall
        )
        Slider(
            value = blurFraction,
            onValueChange = onBlurFractionChange,
            valueRange = 0f..1f,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.design_studio_transparency),
            style = MaterialTheme.typography.titleSmall
        )
        Slider(
            value = transparencyFraction,
            onValueChange = onTransparencyFractionChange,
            valueRange = 0f..1f,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.design_studio_grain_texture), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = grainTexture, onCheckedChange = onGrainTextureChange)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.design_studio_ambient_glow), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = ambientGlow, onCheckedChange = onAmbientGlowChange)
        }

        Button(
            onClick = {
                cornerStyle = SpotyCornerStyle.ROUNDED
                borderThickness = SpotyBorderThickness.THIN
                shadowLevel = SpotyShadowLevel.SUBTLE
                onBlurFractionChange(0.5f)
                onTransparencyFractionChange(0.5f)
                onGrainTextureChange(false)
                onAmbientGlowChange(true)
            },
            colors = ButtonDefaults.textButtonColors(),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.design_studio_reset))
        }
    }

    TopAppBar(
        title = { Text(stringResource(R.string.design_studio)) },
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
