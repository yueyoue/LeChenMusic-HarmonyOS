package com.lechen.music.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1AA34A),
    secondary = Color(0xFF1ED760),
    background = Color(0xFF121212),
    surface = Color(0xFF181818),
    surfaceVariant = Color(0xFF282828),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB3B3B3),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1AA34A),
    secondary = Color(0xFF1ED760),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFEEEEEE),
    onBackground = Color(0xFF121212),
    onSurface = Color(0xFF121212),
    onSurfaceVariant = Color(0xFF666666),
)

@Composable
fun LeChenMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
