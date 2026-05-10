package com.clementvexegon.instruxa.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────
//  INSTRUXA — Color Palette
//  Every color used in the app lives here.
//  Never hardcode colors in screens —
//  always reference from this file.
// ─────────────────────────────────────────


// 🌸 PRIMARY — Lilac / Futuristic
// Used for: main buttons, icons, highlights
val LilacPrimary = Color(0xFF8B5CF6)   // Main brand color
val LilacLight   = Color(0xFFA78BFA)   // Lighter version — hover, containers
val LilacDark    = Color(0xFF6D28D9)   // Darker version — pressed state


// 🔥 PHOENIX — Warm Brown-Red Identity
// Used for: Hire button, CTAs, important actions
val PhoenixRed   = Color(0xFF9F3A3A)   // Main phoenix color
val PhoenixSoft  = Color(0xFFC46A6A)   // Softer — secondary actions
val PhoenixDark  = Color(0xFF7A1F1F)   // Darker — pressed state


// 🧊 FIREBASE-STYLE NEUTRALS — Clean UI
// Used for: backgrounds, cards, borders
val CloudWhite      = Color(0xFFFFFFFF)   // Cards, surfaces
val SoftBackground  = Color(0xFFF9FAFB)   // App background (not pure white)
val MistGray        = Color(0xFFF3F4F6)   // Input fields, surface variants
val BorderGray      = Color(0xFFE5E7EB)   // Card borders, dividers


// 📝 TEXT SYSTEM
// Used for: all text across the app
val TextPrimary   = Color(0xFF111827)   // Main text — headings, names
val TextSecondary = Color(0xFF6B7280)   // Supporting text — subtitles
val TextMuted     = Color(0xFF9CA3AF)   // Placeholder, hint text


// ⚡ ACCENTS — Highlights & UI Richness
// Used for: special badges, links, info states
val ElectricBlue  = Color(0xFF3B82F6)   // Firebase-like action color
val AquaAccent    = Color(0xFF22D3EE)   // Futuristic pop — tags, online badge


// ✅ STATUS COLORS
// Used for: success messages, warnings, errors
val SuccessGreen  = Color(0xFF10B981)   // Payment success, available badge
val WarningAmber  = Color(0xFFF59E0B)   // Busy badge, pending state
val ErrorRed      = Color(0xFFEF4444)   // Errors, failed payment


// 🌫️ OVERLAY & SHADOW
// Used for: modal backgrounds, card depth
val ShadowLight   = Color(0x1A000000)   // Subtle card shadow
val OverlayDark   = Color(0x66000000)   // Dark overlay behind modals/dialogs