package com.clapped.ontherocks.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun OnTheRocksTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = AppBackground,
            onBackground = AppText,
            surface = AppSurface,
            onSurface = AppText,
            onSurfaceVariant = AppMuted,
            primary = AppGold,
            onPrimary = AppBackground
        ),
        content = content
    )
}
