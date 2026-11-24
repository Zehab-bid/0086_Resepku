package com.resepku.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ===== Skema warna light mode (hanya light, sesuai spesifikasi) =====
private val ResepkuColorScheme = lightColorScheme(
    primary          = OrangeMain,
    onPrimary        = androidx.compose.ui.graphics.Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,

    secondary        = CreamMain,
    onSecondary      = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = CreamLight,
    onSecondaryContainer = CreamDark,

    background       = BackgroundWarm,
    onBackground     = androidx.compose.ui.graphics.Color(0xFF1C1B1F),

    surface          = SurfaceWarm,
    onSurface        = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant   = CreamLight,

    error            = ErrorRed,
    onError          = androidx.compose.ui.graphics.Color.White,
    errorContainer   = ErrorRedLight,
)

/**
 * ResepkuTheme — tema aplikasi ResepKu.
 * Hanya light mode (darkTheme diabaikan), tanpa dynamic color.
 * Ini mempermudah konsistensi UI dan debugging.
 */
@Composable
fun ResepkuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ResepkuColorScheme,
        typography  = ResepkuTypography,
        content     = content
    )
}
