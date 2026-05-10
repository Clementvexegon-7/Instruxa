package com.clementvexegon.instruxa.ui.screens.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_LOGIN
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────
//  INSTRUXA — Onboarding Screen (Cinematic)
//
//  3 slides with unique Canvas animations:
//
//  Slide 1 — Sonar/radar animation with
//            music notes floating outward
//
//  Slide 2 — Artist card with animated
//            stars filling up one by one
//
//  Slide 3 — Animated M-Pesa payment
//            flow with checkmark draw
//
//  Background matches Splash dark theme
// ─────────────────────────────────────────

data class OnboardingSlide(
    val title: String,
    val description: String,
    val slideIndex: Int
)

val onboardingSlides = listOf(
    OnboardingSlide(
        title = "Find Instrumentalists\nNear You",
        description = "Search for guitarists, pianists, DJs and more — " +
                "available in your area right now.",
        slideIndex = 0
    ),
    OnboardingSlide(
        title = "View Profiles &\nPortfolios",
        description = "See each artist's skills, ratings and full portfolio " +
                "before you decide to hire.",
        slideIndex = 1
    ),
    OnboardingSlide(
        title = "Hire, Pay &\nRate Easily",
        description = "Book instantly, pay securely via M-Pesa, " +
                "and rate your experience after.",
        slideIndex = 2
    )
)

// ── Slide 0 Illustration ──────────────────
// Sonar pulse with floating music notes
@Composable
fun Slide0Illustration() {
    val infinite = rememberInfiniteTransition(label = "s0")

    val pulse by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse"
    )
    val pulse2 by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1800, delayMillis = 600, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse2"
    )
    val pulse3 by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1800, delayMillis = 1200, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse3"
    )
    val noteFloat by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "note"
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Sonar rings
        listOf(pulse, pulse2, pulse3).forEach { p ->
            val r = p * size.minDimension * 0.48f
            val a = (1f - p) * 0.6f
            drawCircle(
                color = Color(0xFF8B5CF6).copy(alpha = a),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Center dot
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF9F3A3A), Color(0xFF6D28D9)),
                center = Offset(cx, cy),
                radius = 28.dp.toPx()
            ),
            radius = 28.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Music note symbol in center
        val noteSize = 18.dp.toPx()
        val noteX = cx - noteSize / 2f
        val noteY = cy - noteSize / 2f
        // Simple stem + head representation
        drawLine(
            color = Color.White,
            start = Offset(noteX + noteSize * 0.6f, noteY),
            end = Offset(noteX + noteSize * 0.6f, noteY + noteSize * 0.7f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color.White,
            radius = 5.dp.toPx(),
            center = Offset(noteX + noteSize * 0.35f, noteY + noteSize * 0.75f)
        )

        // Floating particles around
        val angles = listOf(30f, 90f, 150f, 210f, 270f, 330f)
        angles.forEachIndexed { i, angle ->
            val progress = (noteFloat + i * (1f / angles.size)) % 1f
            val rad = (angle * PI / 180f).toFloat()
            val dist = progress * size.minDimension * 0.45f
            val x = cx + cos(rad) * dist
            val y = cy + sin(rad) * dist
            val a = sin(progress * PI.toFloat()).coerceIn(0f, 1f) * 0.9f
            drawCircle(
                color = Color(0xFF22D3EE).copy(alpha = a),
                radius = 3.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

// ── Slide 1 Illustration ──────────────────
// Animated profile card with star rating
@Composable
fun Slide1Illustration() {
    val infinite = rememberInfiniteTransition(label = "s1")

    // Stars fill sequentially
    val starAnim by infinite.animateFloat(
        0f, 5f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "stars"
    )
    val cardPulse by infinite.animateFloat(
        0.95f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "card"
    )
    val avatarGlow by infinite.animateFloat(
        0.3f, 0.9f,
        infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "aglow"
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val cardW = size.width * 0.88f * cardPulse
        val cardH = size.height * 0.75f
        val cardX = (size.width - cardW) / 2f
        val cardY = (size.height - cardH) / 2f

        // Card shadow
        drawRoundRect(
            color = Color(0xFF6D28D9).copy(alpha = 0.25f),
            topLeft = Offset(cardX + 6f, cardY + 8f),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(20.dp.toPx())
        )

        // Card background
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E1040), Color(0xFF120A2A)),
                startY = cardY, endY = cardY + cardH
            ),
            topLeft = Offset(cardX, cardY),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(20.dp.toPx())
        )

        // Card border
        drawRoundRect(
            color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
            topLeft = Offset(cardX, cardY),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(20.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        // Avatar glow
        val avCx = size.width / 2f
        val avCy = cardY + 50.dp.toPx()
        drawCircle(
            color = Color(0xFF8B5CF6).copy(alpha = avatarGlow * 0.4f),
            radius = 36.dp.toPx(),
            center = Offset(avCx, avCy)
        )

        // Avatar circle
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF9F3A3A), Color(0xFF6D28D9)),
                start = Offset(avCx - 20f, avCy - 20f),
                end = Offset(avCx + 20f, avCy + 20f)
            ),
            radius = 28.dp.toPx(),
            center = Offset(avCx, avCy)
        )

        // Avatar initials area — person silhouette hint
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = 10.dp.toPx(),
            center = Offset(avCx, avCy - 6.dp.toPx())
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = 16.dp.toPx(),
            center = Offset(avCx, avCy + 20.dp.toPx())
        )

        // Animated star rating
        val starY = cardY + 110.dp.toPx()
        val starSpacing = 22.dp.toPx()
        val startX = size.width / 2f - starSpacing * 2f

        for (i in 0 until 5) {
            val filled = starAnim > i.toFloat()
            val partial = if (starAnim > i.toFloat()) 1f else (starAnim - i.toFloat()).coerceIn(0f, 1f)
            val starColor = if (filled || partial > 0f)
                Color(0xFFF59E0B).copy(alpha = 0.3f + partial * 0.7f)
            else
                Color(0xFF8B5CF6).copy(alpha = 0.2f)

            drawCircle(
                color = starColor,
                radius = 7.dp.toPx(),
                center = Offset(startX + i.toFloat() * starSpacing, starY)
            )
        }

        // "Available" badge
        val badgeY = cardY + cardH - 32.dp.toPx()
        drawRoundRect(
            color = Color(0xFF10B981).copy(alpha = 0.2f),
            topLeft = Offset(size.width / 2f - 40.dp.toPx(), badgeY),
            size = Size(80.dp.toPx(), 20.dp.toPx()),
            cornerRadius = CornerRadius(10.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF10B981),
            radius = 4.dp.toPx(),
            center = Offset(size.width / 2f - 22.dp.toPx(), badgeY + 10.dp.toPx())
        )
    }
}

// ── Slide 2 Illustration ──────────────────
// Animated payment/checkmark flow
@Composable
fun Slide2Illustration() {
    val infinite = rememberInfiniteTransition(label = "s2")

    val checkProgress by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "check"
    )
    val ring by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring"
    )
    val moneyFloat by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "money"
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Outer ring pulse
        drawCircle(
            color = Color(0xFF10B981).copy(alpha = (1f - ring) * 0.5f),
            radius = ring * size.minDimension * 0.48f,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Success circle
        val circleAlpha = if (checkProgress > 0.3f) 1f else checkProgress / 0.3f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF059669).copy(alpha = circleAlpha),
                    Color(0xFF065F46).copy(alpha = circleAlpha)
                ),
                center = Offset(cx, cy),
                radius = 46.dp.toPx()
            ),
            radius = 46.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Animated checkmark drawing itself
        if (checkProgress > 0.3f) {
            val drawProgress = ((checkProgress - 0.3f) / 0.7f).coerceIn(0f, 1f)
            val checkPath = Path().apply {
                // Checkmark: left down, then up-right
                val startX = cx - 18.dp.toPx()
                val midX   = cx - 5.dp.toPx()
                val endX   = cx + 22.dp.toPx()
                val midY   = cy + 8.dp.toPx()
                val startY = cy - 2.dp.toPx()
                val endY   = cy - 16.dp.toPx()
                moveTo(startX, startY)
                lineTo(midX, midY)
                lineTo(endX, endY)
            }

            // Draw only the portion based on progress
            drawPath(
                path = checkPath,
                color = Color.White.copy(alpha = drawProgress),
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    miter = 2f
                )
            )
        }

        // Floating coin/payment particles
        listOf(0f, 0.33f, 0.66f).forEachIndexed { i, offset ->
            val prog = (moneyFloat + offset) % 1f
            val angle = (60f + i * 120f) * PI.toFloat() / 180f
            val dist = prog * size.minDimension * 0.42f
            val x = cx + cos(angle) * dist
            val y = cy + sin(angle) * dist - prog * 20.dp.toPx()
            val a = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.9f
            drawCircle(
                color = Color(0xFFF59E0B).copy(alpha = a),
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            // "M" for M-Pesa hint
            drawLine(
                color = Color.White.copy(alpha = a),
                start = Offset(x - 3.dp.toPx(), y + 3.dp.toPx()),
                end = Offset(x, y - 3.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = a),
                start = Offset(x, y - 3.dp.toPx()),
                end = Offset(x + 3.dp.toPx(), y + 3.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

@Composable
fun OnboardingScreen(navController: NavController) {

    var currentSlide by remember { mutableIntStateOf(0) }
    val slide = onboardingSlides[currentSlide]
    val isLastSlide = currentSlide == onboardingSlides.size - 1

    // Fade in content on slide change
    val contentAlpha = remember { Animatable(1f) }
    LaunchedEffect(currentSlide) {
        contentAlpha.snapTo(0f)
        contentAlpha.animateTo(1f, tween(400))
    }

    // Background particles
    val infinite = rememberInfiniteTransition(label = "ob_bg")
    val bgParticle by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "bgp"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080612))
    ) {

        // ── Background Canvas ─────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Soft background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E0A3A).copy(alpha = 0.8f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height * 0.35f),
                    radius = size.minDimension * 0.7f
                )
            )

            // Background floating dots
            val spacing = 50.dp.toPx()
            val cols = (size.width / spacing).toInt() + 1
            val rows = (size.height / spacing).toInt() + 1
            for (col in 0..cols) {
                for (row in 0..rows) {
                    drawCircle(
                        color = Color(0xFF6D28D9).copy(alpha = 0.12f),
                        radius = 1.2.dp.toPx(),
                        center = Offset(col * spacing, row * spacing)
                    )
                }
            }

            // A few larger drifting particles
            val pPositions = listOf(
                Offset(0.15f, 0.6f), Offset(0.85f, 0.3f),
                Offset(0.7f, 0.8f),  Offset(0.3f, 0.15f)
            )
            pPositions.forEachIndexed { i, p ->
                val prog = (bgParticle + i.toFloat() * 0.25f) % 1f
                val y = size.height * p.y * (1f - prog * 0.3f)
                val a = (sin(prog * PI.toFloat()) as Float).coerceIn(0f, 1f) * 0.4f
                drawCircle(
                    color = Color(0xFF8B5CF6).copy(alpha = a),
                    radius = 3.dp.toPx(),
                    center = Offset(size.width * p.x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(52.dp))

            // ── Skip button ───────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Instruxa mini logo top left
                Text(
                    text = "INSTRUXA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5CF6).copy(alpha = 0.7f),
                    letterSpacing = 2.sp
                )

                if (!isLastSlide) {
                    TextButton(onClick = {
                        navController.navigate(ROUT_LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text(
                            text = "Skip",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Illustration area ─────────
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .alpha(contentAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                when (currentSlide) {
                    0 -> Slide0Illustration()
                    1 -> Slide1Illustration()
                    2 -> Slide2Illustration()
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // ── Slide text ────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha.value)
            ) {
                Text(
                    text = slide.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = slide.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Dot indicators ────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                onboardingSlides.forEachIndexed { index, _ ->
                    val dotWidth by animateDpAsState(
                        targetValue = if (index == currentSlide) 24.dp else 7.dp,
                        animationSpec = tween(300),
                        label = "dot"
                    )
                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(
                                if (index == currentSlide)
                                    Color(0xFF8B5CF6)
                                else
                                    Color.White.copy(alpha = 0.2f)
                            )
                    )
                    if (index < onboardingSlides.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Next / Get Started button ──
            Button(
                onClick = {
                    if (isLastSlide) {
                        navController.navigate(ROUT_LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        currentSlide++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastSlide)
                        Color(0xFF9F3A3A) // PhoenixRed
                    else
                        Color(0xFF8B5CF6) // LilacPrimary
                )
            ) {
                Text(
                    text = if (isLastSlide) "Get Started  →" else "Next  →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(44.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    InstruxaTheme {
        OnboardingScreen(navController = rememberNavController())
    }
}