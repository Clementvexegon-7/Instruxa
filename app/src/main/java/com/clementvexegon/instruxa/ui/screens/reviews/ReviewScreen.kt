package com.clementvexegon.instruxa.ui.screens.reviews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
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
import com.clementvexegon.instruxa.navigation.ROUT_HOME
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────
//  INSTRUXA — Review Screen
//
//  After the session, the client rates Kevin.
//  This screen is the soul of artist growth.
//
//  Features:
//  • Cinematic dark animated background
//  • Session summary card at top
//  • 5 interactive stars — each one:
//      - Scales up with spring bounce on tap
//      - Fires a PARTICLE BURST in amber/gold
//      - Stays lit (filled) up to tapped index
//  • Dynamic rating label that transforms:
//      1★ → "Terrible 😤"
//      2★ → "Below Average 😞"
//      3★ → "It was OK 😊"
//      4★ → "Great Session! 🔥"
//      5★ → "LEGENDARY! ⚡"
//  • Quick tag chips — toggle on/off:
//      Professional, Talented, Punctual,
//      Creative, Patient, Affordable
//  • Multi-line comment field with counter
//  • "Would you recommend Kevin?" toggle
//  • Submit button with gradient glow
//  • Full-screen CELEBRATION on submit:
//      - Confetti explosions
//      - Animated rings expanding outward
//      - "Thank you" card with impact copy
//      - Back to home button
// ─────────────────────────────────────────

val reviewTags = listOf(
    "Professional 💼", "Talented 🎸",
    "Punctual ⏰",     "Creative 🎨",
    "Patient 😌",      "Affordable 💰",
    "Great Energy ⚡",  "Would Book Again 🔁"
)

val ratingLabels = mapOf(
    0 to ("Tap a star to rate" to "⭐"),
    1 to ("Terrible" to "😤"),
    2 to ("Below Average" to "😞"),
    3 to ("It was OK" to "😊"),
    4 to ("Great Session!" to "🔥"),
    5 to ("LEGENDARY!" to "⚡")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    // ── Review state ──────────────────────
    var selectedStars   by remember { mutableIntStateOf(0) }
    var selectedTags    by remember { mutableStateOf(setOf<String>()) }
    var comment         by remember { mutableStateOf("") }
    var recommends      by remember { mutableStateOf(true) }
    var isSubmitted     by remember { mutableStateOf(false) }

    // Per-star burst state
    val starBursts      = remember { List(5) { mutableStateOf(0f) } }
    val starScales      = remember { List(5) { Animatable(1f) } }

    // Entry animations
    val contentAlpha    = remember { Animatable(0f) }
    val contentSlide    = remember { Animatable(60f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(600))
        contentSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    // ── Infinite background animations ────
    val inf = rememberInfiniteTransition(label = "review_bg")
    val particleAnim by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "p"
    )
    val glowPulse by inf.animateFloat(
        0.3f, 0.9f,
        infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "g"
    )
    val ring1 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "r1"
    )
    val ring2 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3000, delayMillis = 1000, easing = LinearEasing), RepeatMode.Restart),
        label = "r2"
    )

    val ratingInfo = ratingLabels[selectedStars] ?: ("Tap a star to rate" to "⭐")

    // ─────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        // ── Background Canvas ─────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Amber glow center (stars theme)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF3D2800).copy(alpha = glowPulse * 0.7f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height * 0.4f),
                    radius = size.minDimension * 0.7f
                )
            )
            // Lilac glow bottom
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF2D1060).copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.85f),
                    radius = size.minDimension * 0.55f
                )
            )
            // Grid dots
            val sp = 46.dp.toPx()
            for (c in 0..(size.width / sp).toInt() + 2)
                for (r in 0..(size.height / sp).toInt() + 2)
                    drawCircle(
                        Color(0xFF6D28D9).copy(alpha = 0.07f),
                        1.1.dp.toPx(),
                        Offset(c * sp, r * sp)
                    )
            // Particles
            val pSeeds = listOf(
                Offset(0.1f,0.2f), Offset(0.88f,0.14f),
                Offset(0.05f,0.7f),Offset(0.92f,0.65f),
                Offset(0.5f,0.05f),Offset(0.35f,0.9f),
                Offset(0.68f,0.82f),Offset(0.78f,0.38f)
            )
            pSeeds.forEachIndexed { i, pos ->
                val prog = (particleAnim + i * (1f / pSeeds.size)) % 1f
                val x    = pos.x * size.width + sin(prog * 2f * PI.toFloat() + i.toFloat()) * 16.dp.toPx()
                val y    = size.height * (1f - prog)
                val a    = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.55f
                drawCircle(
                    color  = if (i % 2 == 0) Color(0xFFF59E0B).copy(alpha = a)
                    else             Color(0xFF8B5CF6).copy(alpha = a * 0.7f),
                    radius = (1.5f + i % 3).dp.toPx(),
                    center = Offset(x, y)
                )
            }
            // Rings around star area center
            val rc = Offset(size.width / 2f, size.height * 0.42f)
            listOf(ring1, ring2).forEach { p ->
                drawCircle(
                    color  = Color(0xFFF59E0B).copy(alpha = (1f - p) * 0.22f),
                    radius = p * 100.dp.toPx() + 50.dp.toPx(),
                    center = rc,
                    style  = Stroke(1.dp.toPx())
                )
            }
        }

        // ── Star burst particles overlay ──
        Canvas(modifier = Modifier.fillMaxSize()) {
            starBursts.forEachIndexed { i, burstState ->
                val prog  = burstState.value
                if (prog > 0f) {
                    val starX = size.width / 2f + (i - 2) * 56.dp.toPx()
                    val starY = size.height * 0.44f
                    for (j in 0 until 10) {
                        val angle = (j * 36f) * PI.toFloat() / 180f
                        val dist  = prog * 55.dp.toPx()
                        val x     = starX + cos(angle) * dist
                        val y     = starY + sin(angle) * dist
                        val a     = (1f - prog).coerceAtLeast(0f)
                        drawCircle(
                            color  = if (j % 2 == 0) Color(0xFFF59E0B).copy(alpha = a)
                            else             Color(0xFFFFDC5E).copy(alpha = a),
                            radius = (4f - prog * 3.5f).coerceAtLeast(0.4f).dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        // ── Main Content ──────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .offset(y = contentSlide.value.dp)
                .alpha(contentAlpha.value)
        ) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Rate Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("How was Kevin?", fontSize = 12.sp, color = Color(0xFFF59E0B))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("1 session", fontSize = 11.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

                // Session recap card
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF1E1000), Color(0xFF1A0A1A))))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(46.dp).clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A), Color(0xFF6D28D9)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("KO", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Kevin Omondi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Guitarist · Session Complete", fontSize = 11.sp, color = Color(0xFFF59E0B))
                        }
                    }
                    Text("✓ Done", fontSize = 13.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(36.dp))

                // "How was your session?" heading
                Text(
                    "How was your\nsession with Kevin?",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Your honest feedback helps Kevin grow\nand helps other users choose wisely.",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(36.dp))

                // ── 5 INTERACTIVE STARS ───────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        val isFilled   = i <= selectedStars
                        val starScale by animateFloatAsState(
                            targetValue = starScales[i - 1].value,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy),
                            label = "ss$i"
                        )
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(starScale)
                                .clickable {
                                    selectedStars = i
                                    scope.launch {
                                        // Spring bounce
                                        starScales[i - 1].snapTo(1.5f)
                                        starScales[i - 1].animateTo(
                                            1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                                        )
                                        // Burst
                                        val burst = starBursts[i - 1]
                                        burst.value = 0f
                                        for (step in 0..20) {
                                            burst.value = step / 20f
                                            delay(15)
                                        }
                                        burst.value = 0f
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (isFilled) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // ── Dynamic rating label ──────
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(ratingInfo.second, fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            ratingInfo.first,
                            fontSize = if (selectedStars == 5) 22.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (selectedStars) {
                                0    -> Color.White.copy(alpha = 0.3f)
                                1, 2 -> Color(0xFFEF4444)
                                3    -> Color(0xFFF59E0B)
                                4    -> Color(0xFF10B981)
                                5    -> Color(0xFF22D3EE)
                                else -> Color.White
                            }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── Quick tag chips ───────────
                Text("What stood out?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    reviewTags.forEach { tag ->
                        val isSel = tag in selectedTags
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSel)
                                        Brush.horizontalGradient(listOf(Color(0xFFF59E0B).copy(alpha = 0.3f), Color(0xFF8B5CF6).copy(alpha = 0.2f)))
                                    else
                                        Brush.horizontalGradient(listOf(Color(0xFF1A0E35), Color(0xFF110A22)))
                                )
                                .border(
                                    if (isSel) 1.5.dp else 1.dp,
                                    if (isSel) Color(0xFFF59E0B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedTags = if (isSel) selectedTags - tag else selectedTags + tag
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                tag,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.45f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ── Comment field ─────────────
                Text("Leave a comment", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { if (it.length <= 300) comment = it },
                    placeholder = {
                        Text(
                            "Tell others about your experience with Kevin...",
                            color = Color.White.copy(alpha = 0.2f),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor        = Color.White,
                        unfocusedTextColor      = Color.White,
                        focusedBorderColor      = Color(0xFFF59E0B),
                        unfocusedBorderColor    = Color.White.copy(alpha = 0.1f),
                        cursorColor             = Color(0xFFF59E0B),
                        focusedContainerColor   = Color(0xFF130A28),
                        unfocusedContainerColor = Color(0xFF130A28)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        "${comment.length}/300",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── Recommend toggle ──────────
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF130A28))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .clickable { recommends = !recommends }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Would you recommend Kevin?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            if (recommends) "Yes, I'd recommend him to others 👍"
                            else            "Not this time 👎",
                            fontSize = 12.sp,
                            color = if (recommends) Color(0xFF10B981) else Color(0xFFEF4444).copy(alpha = 0.8f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (recommends) Color(0xFF10B981).copy(alpha = 0.2f)
                                else            Color(0xFFEF4444).copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (recommends) "👍" else "👎", fontSize = 20.sp)
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ── Submit button ─────────────
                Box(
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (selectedStars > 0)
                                Brush.horizontalGradient(listOf(Color(0xFFB45309), Color(0xFFF59E0B), Color(0xFF8B5CF6).copy(alpha = 0.8f)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF1A0E35), Color(0xFF110A22)))
                        )
                        .border(
                            1.dp,
                            if (selectedStars > 0) Color(0xFFF59E0B).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable(enabled = selectedStars > 0) { isSubmitted = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (selectedStars > 0) "Submit Review  ⭐" else "Tap a star first",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedStars > 0) Color.White else Color.White.copy(alpha = 0.3f),
                        letterSpacing = 0.3.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "Your review helps Kevin get more gigs\nand helps the Instruxa community grow.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.22f),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(50.dp))
            }
        }

        // ════════════════════════════════
        //  CELEBRATION OVERLAY
        // ════════════════════════════════
        AnimatedVisibility(
            visible = isSubmitted,
            enter   = fadeIn(tween(500)) + scaleIn(spring(Spring.DampingRatioMediumBouncy)),
            exit    = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color(0xFF080612).copy(alpha = 0.97f)),
                contentAlignment = Alignment.Center
            ) {
                // Confetti + rings
                val celebInf = rememberInfiniteTransition(label = "celeb")
                val celebAnim by celebInf.animateFloat(
                    0f, 1f,
                    infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
                    label = "cel"
                )
                val ringAnim by celebInf.animateFloat(
                    0f, 1f,
                    infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
                    label = "ra"
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Celebration rings
                    val cc = Offset(size.width / 2f, size.height * 0.42f)
                    for (k in 0 until 3) {
                        val rp = (ringAnim + k * 0.33f) % 1f
                        drawCircle(
                            color  = Color(0xFFF59E0B).copy(alpha = (1f - rp) * 0.4f),
                            radius = rp * size.minDimension * 0.5f + 50.dp.toPx(),
                            center = cc,
                            style  = Stroke(1.5.dp.toPx())
                        )
                    }
                    // Confetti
                    val confColors = listOf(Color(0xFFF59E0B),Color(0xFF8B5CF6),Color(0xFF10B981),Color(0xFF22D3EE),Color(0xFFC46A6A))
                    for (i in 0 until 40) {
                        val prog = (celebAnim + i * (1f / 40f)) % 1f
                        val x    = (i * 37f + sin(prog * PI.toFloat() * 2f + i.toFloat()) * 22f) % size.width
                        val y    = prog * size.height
                        val a    = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.9f
                        drawCircle(
                            color  = confColors[i % confColors.size].copy(alpha = a),
                            radius = (2f + i % 4).dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 28.dp)
                ) {
                    Spacer(Modifier.height(60.dp))

                    // Star trophy
                    Box(
                        modifier = Modifier.size(110.dp).clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFFB45309), Color(0xFF92400E)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⭐", fontSize = 52.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Review Submitted!",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "You rated Kevin $selectedStars star${if (selectedStars != 1) "s" else ""}.\nHe'll be notified. This means a lot to him.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    // Impact card
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1A0E00))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚡", fontSize = 28.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Your Review Has Impact", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Artists like Kevin depend on reviews to get\ndiscovered and land more gigs. You just\nhelped him grow his career.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    // Stars display
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(selectedStars) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(28.dp))
                        }
                        repeat(5 - selectedStars) {
                            Icon(Icons.Outlined.StarOutline, null, tint = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // Back home button
                    Box(
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFB45309), Color(0xFF8B5CF6)))
                            )
                            .clickable {
                                navController.navigate(ROUT_HOME) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Back to Home  →", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewScreenPreview() {
    InstruxaTheme { ReviewScreen(navController = rememberNavController()) }
}