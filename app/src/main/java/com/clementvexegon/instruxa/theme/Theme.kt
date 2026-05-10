package com.clementvexegon.instruxa.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─────────────────────────────────────────
//  INSTRUXA — Light Color Scheme
//  Used when the phone is in light mode.
//  Primary   = LilacPrimary (buttons, icons)
//  Secondary = PhoenixRed (Hire button, CTA)
//  Background = SoftBackground (screen bg)
//  Surface   = CloudWhite (cards)
// ─────────────────────────────────────────

private val InstruxaLightColors = lightColorScheme(

    // Main brand color — lilac
    primary          = LilacPrimary,
    onPrimary        = CloudWhite,
    primaryContainer = LilacLight,
    onPrimaryContainer = LilacDark,

    // Secondary brand — phoenix red (Hire buttons)
    secondary        = PhoenixRed,
    onSecondary      = CloudWhite,
    secondaryContainer = PhoenixSoft,
    onSecondaryContainer = PhoenixDark,

    // App background — very light gray like Firebase
    background       = SoftBackground,
    onBackground     = TextPrimary,

    // Cards and surfaces — pure white
    surface          = CloudWhite,
    onSurface        = TextPrimary,
    surfaceVariant   = MistGray,
    onSurfaceVariant = TextSecondary,

    // Borders
    outline          = BorderGray,
    outlineVariant   = MistGray,

    // Status colors
    error            = ErrorRed,
    onError          = CloudWhite,
)

// ─────────────────────────────────────────
//  INSTRUXA — Dark Color Scheme
//  Used when the phone is in dark mode.
//  Keeps the Instruxa identity but inverted
//  for readability in the dark.
// ─────────────────────────────────────────

private val InstruxaDarkColors = darkColorScheme(

    // Lighter lilac so it's visible on dark bg
    primary          = LilacLight,
    onPrimary        = LilacDark,
    primaryContainer = LilacDark,
    onPrimaryContainer = LilacLight,

    // Phoenix soft so it doesn't blind in dark
    secondary        = PhoenixSoft,
    onSecondary      = PhoenixDark,
    secondaryContainer = PhoenixDark,
    onSecondaryContainer = PhoenixSoft,

    // Dark background for the app
    background       = TextPrimary,
    onBackground     = CloudWhite,

    // Dark cards
    surface          = TextSecondary,
    onSurface        = CloudWhite,
    surfaceVariant   = TextSecondary,
    onSurfaceVariant = TextMuted,

    // Borders visible on dark
    outline          = TextMuted,

    // Status
    error            = ErrorRed,
    onError          = CloudWhite,
)

// ─────────────────────────────────────────
//  INSTRUXA — Main Theme Wrapper
//  Wrap every screen with this in MainActivity
//  It applies: colors + typography + status bar
// ─────────────────────────────────────────

@Composable
fun InstruxaTheme(
    // Automatically follows the phone's dark/light setting
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Pick the right color scheme based on mode
    val colorScheme = if (darkTheme) InstruxaDarkColors else InstruxaLightColors

    // Update the status bar color to match the app
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar matches the app background
            window.statusBarColor = colorScheme.background.toArgb()
            // Dark icons on light bg, light icons on dark bg
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Apply everything globally
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = InstruxaTypography,
        content     = content
    )
}