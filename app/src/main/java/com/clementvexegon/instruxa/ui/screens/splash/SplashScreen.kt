package com.clementvexegon.instruxa.ui.screens.splash

import android.graphics.Color
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_ONBOARDING
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlinx.coroutines.delay
import java.nio.file.Files.size
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────
//  INSTRUXA — Splash Screen (Cinematic)
//
//  Fully animated — no images needed.
//  Uses Canvas drawing + Compose animations.
//
//  What you see:
//  • Deep space dark background
//  • Grid of glowing dots (like a circuit board)
//  • Floating colored particles drifting upward
//  • 3 pulsing rings expanding from the logo
//  • Logo bounces in with a spring animation
//  • Glow pulses behind the logo
//  • App name + tagline fade in
//  • Animated equalizer bars at the bottom
// ─────────────────────────────────────────

@Composable
fun SplashScreen(navController: NavController) {

    // ── One-time entry animations ─────────
    val logoScale    = remember { android.graphics.drawable.Animatable(0f) }
    val nameAlpha    = remember { android.graphics.drawable.Animatable(0f) }
    val taglineAlpha = remember { android.graphics.drawable.Animatable(0f) }

    // ── Continuous looping animations ─────
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bg")

    // 3 rings staggered by 700ms each
    val ring1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = LinearEasing), RepeatMode.Restart
        ), label = "ring1"
    )
    val ring2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, delayMillis = 700, easing = LinearEasing), RepeatMode.Restart
        ), label = "ring2"
    )
    val ring3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, delayMillis = 1400, easing = LinearEasing), RepeatMode.Restart
        ), label = "ring3"
    )

    // Particles drifting upward
    val particleAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(5000, easing = LinearEasing), RepeatMode.Restart
        ), label = "particles"
    )

    // Logo glow breathing
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "glow"
    )

    // Equalizer wave
    val waveAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing), RepeatMode.Restart
        ), label = "wave"
    )

    // Slow color shift for background glow
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing), RepeatMode.Reverse
        ), label = "colorShift"
    )

    // ── Sequence: animate then navigate ───
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        nameAlpha.animateTo(1f, tween(600))
        taglineAlpha.animateTo(1f, tween(500))
        delay(2200)
        navController.navigate(ROUT_ONBOARDING) {
            popUpTo(0) { inclusive = true }
        }
    }

    // Fixed particle positions (normalized 0..1)
    val particles = remember {
        listOf(
            Offset(0.12f, 0.18f), Offset(0.82f, 0.12f),
            Offset(0.22f, 0.72f), Offset(0.72f, 0.78f),
            Offset(0.50f, 0.08f), Offset(0.91f, 0.42f),
            Offset(0.08f, 0.52f), Offset(0.62f, 0.28f),
            Offset(0.38f, 0.88f), Offset(0.88f, 0.62f),
            Offset(0.44f, 0.44f), Offset(0.18f, 0.38f),
            Offset(0.55f, 0.92f), Offset(0.76f, 0.22f),
            Offset(0.33f, 0.55f)
        )
    }

    // ── Root container ────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Very deep dark base — almost black with purple tint
            .background(Color(0xFF080612)),
        contentAlignment = Alignment.Center
    ) {

        // ── Full screen Canvas ────────────
        // Everything drawn here is purely animated code
        Canvas(modifier = Modifier.fillMaxSize()) {

            // Animated radial background glow
            // Shifts between purple and indigo
            val glowColor1 = Color(
                red = 0.18f + colorShift * 0.05f,
                green = 0.08f,
                blue = 0.38f + colorShift * 0.1f,
                alpha = 0.85f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor1, Color.Transparent),
                    center = center,
                    radius = size.minDimension * 0.75f
                )
            )

            // Secondary glow — shifted offset for depth
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A0A3A).copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(center.x * 0.6f, center.y * 0.4f),
                    radius = size.minDimension * 0.5f
                )
            )

            // Grid dots — like a circuit/hologram pattern
            val spacing = 38.dp.toPx()
            val cols = (size.width / spacing).toInt() + 2
            val rows = (size.height / spacing).toInt() + 2
            for (col in 0..cols) {
                for (row in 0..rows) {
                    val x = col * spacing
                    val y = row * spacing
                    val dist = sqrt(
                        (x - center.x) * (x - center.x) +
                                (y - center.y) * (y - center.y)
                    )
                    val maxDist = size.minDimension * 0.65f
                    val a = (1f - (dist / maxDist).coerceIn(0f, 1f)) * 0.35f
                    if (a > 0.02f) {
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = a),
                            radius = 1.8.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }

            // Floating colored particles
            particles.forEachIndexed { i, pos ->
                val offset = (particleAnim + i.toFloat() * (1f / particles.size.toFloat())) % 1f
                val x = pos.x * size.width + sin(
                    offset * 2f * PI.toFloat() + i.toFloat() * 1.3f
                ) * 18.dp.toPx()
                val y = size.height * (1f - offset)
                val a = (sin(offset * PI.toFloat()) as Float).coerceIn(0f, 1f) * 0.85f
                val r = (1.8f + (i % 4).toFloat() * 1.2f).dp.toPx()

                // 3 different colors cycling through particles
                val particleColor = when (i % 3) {
                    0 -> Color(0xFF8B5CF6).copy(alpha = a)       // Lilac
                    1 -> Color(0xFF22D3EE).copy(alpha = a * 0.75f) // Aqua
                    else -> Color(0xFFC46A6A).copy(alpha = a * 0.6f) // Phoenix soft
                }
                drawCircle(color = particleColor, radius = r, center = Offset(x, y))
            }

            // 3 pulsing rings — staggered so they cascade outward
            listOf(ring1, ring2, ring3).forEach { progress ->
                val radius = size.minDimension * 0.18f +
                        progress * size.minDimension * 0.38f
                val alpha = (1f - progress) * 0.45f
                val strokeW = (2.5f - progress * 2f).coerceAtLeast(0.3f).dp.toPx()
                drawCircle(
                    color = Color(0xFF8B5CF6).copy(alpha = alpha),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeW)
                )
            }

            // Thin diagonal lines — adds depth/dimension
            val lineColor = Color(0xFF6D28D9).copy(alpha = 0.12f)
            val lineStroke = 0.5.dp.toPx()
            for (i in 0..8) {
                val startX = size.width * i / 8f
                drawLine(
                    color = lineColor,
                    start = Offset(startX, 0f),
                    end = Offset(startX + size.height * 0.3f, size.height),
                    strokeWidth = lineStroke
                )
            }
        }

        // ── Foreground content ────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {

            // ── Logo with glow ────────────
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(logoScale.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow (drawn on canvas)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(alpha = glowPulse * 0.55f),
                                Color(0xFF6D28D9).copy(alpha = glowPulse * 0.2f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension * 0.65f
                    )
                }

                // Logo circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF9F3A3A), // PhoenixRed
                                    Color(0xFF6D28D9)  // LilacDark
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "IX",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── App Name ──────────────────
            Text(
                text = "Instruxa",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-1.5).sp,
                modifier = Modifier.alpha(nameAlpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Tagline ───────────────────
            Text(
                text = "HIRE THE SOUND YOU NEED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.45f),
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )

            Spacer(modifier = Modifier.height(52.dp))

            // ── Animated Equalizer bars ───
            // Simulates an audio waveform
            Box(
                modifier = Modifier
                    .alpha(taglineAlpha.value)
                    .height(28.dp)
                    .width(72.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = 7
                    val totalWidth = size.width
                    val gap = totalWidth / (barCount * 2f - 1)
                    val barW = gap * 0.85f

                    for (i in 0 until barCount) {
                        val barH = (0.25f + 0.75f *
                                abs(sin(waveAnim + i * 0.9f))) * size.height
                        val x = i * gap * 2f
                        val barColor = when {
                            i < 2 || i > 4 -> Color(0xFF8B5CF6).copy(alpha = 0.7f)
                            else -> Color(0xFF22D3EE).copy(alpha = 0.85f)
                        }
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, size.height - barH),
                            size = Size(barW, barH),
                            cornerRadius = CornerRadius(barW / 2f)
                        )
                    }
                    // "by Clement Vexegon" should be a Text composable outside Canvas or handled differently.
                    // For now, I'll remove the misplaced Text call inside Canvas.
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    InstruxaTheme {
        SplashScreen(navController = rememberNavController())
    }
}