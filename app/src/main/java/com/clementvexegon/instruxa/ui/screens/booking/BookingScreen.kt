package com.clementvexegon.instruxa.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_HOME
import com.clementvexegon.instruxa.navigation.ROUT_REVIEWS
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────
//  INSTRUXA — Booking Screen
//
//  The transaction heart of the app.
//  This screen handles the full booking flow:
//
//  1. Session type selector (4 animated cards)
//  2. Date carousel (10 glowing date tiles)
//  3. Time slot grid (6 slots, some unavailable)
//  4. Duration selector with LIVE price calc
//  5. Special notes field
//  6. Price breakdown card
//  7. M-Pesa payment section
//  8. Confirm button with particle BURST
//  9. Full-screen success overlay + receipt
//     with falling confetti animation
// ─────────────────────────────────────────

// ── Session types ─────────────────────────
data class SessionType(
    val emoji: String,
    val name: String,
    val desc: String,
    val color: Color
)

val sessionTypes = listOf(
    SessionType("🎤", "Live Event",  "Concerts, weddings",    Color(0xFF8B5CF6)),
    SessionType("🎙️", "Recording",  "Studio sessions",       Color(0xFF9F3A3A)),
    SessionType("📚", "Lesson",     "1-on-1 teaching",       Color(0xFF22D3EE)),
    SessionType("💻", "Online",     "Virtual anywhere",      Color(0xFF10B981))
)

val timeSlots        = listOf("9:00 AM","11:00 AM","1:00 PM","3:00 PM","5:00 PM","7:00 PM")
val unavailableSlots = setOf("7:00 PM")

val dateItems = listOf(
    "Mon" to 5,  "Tue" to 6,  "Wed" to 7,  "Thu" to 8,
    "Fri" to 9,  "Sat" to 10, "Sun" to 11, "Mon" to 12,
    "Tue" to 13, "Wed" to 14
)

// ─────────────────────────────────────────

@Composable
fun BookingScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    // ── Form state ────────────────────────
    var selectedSession  by remember { mutableIntStateOf(0) }
    var selectedDate     by remember { mutableIntStateOf(1) }
    var selectedTime     by remember { mutableStateOf("11:00 AM") }
    var selectedDuration by remember { mutableIntStateOf(2) }
    var mpesaPhone       by remember { mutableStateOf("") }
    var notes            by remember { mutableStateOf("") }
    var isConfirmed      by remember { mutableStateOf(false) }
    var burstActive      by remember { mutableStateOf(false) }
    var burstProgress    by remember { mutableFloatStateOf(0f) }

    val pricePerHour = 800
    val serviceFee   = 50
    val totalPrice   = pricePerHour * selectedDuration + serviceFee

    // ── Entry animations ──────────────────
    val contentAlpha = remember { Animatable(0f) }
    val contentSlide = remember { Animatable(60f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(600))
        contentSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    // ── Burst animation ───────────────────
    LaunchedEffect(burstActive) {
        if (burstActive) {
            for (i in 0..25) { burstProgress = i / 25f; delay(16) }
            delay(300)
            isConfirmed = true
            burstActive = false
        }
    }

    // ── Infinite background animations ────
    val inf = rememberInfiniteTransition(label = "booking")
    val particleAnim by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "p"
    )
    val glowPulse by inf.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "g"
    )
    val ring1 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "r1"
    )
    val ring2 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2800, delayMillis = 930, easing = LinearEasing), RepeatMode.Restart),
        label = "r2"
    )
    val waveAnim by inf.animateFloat(
        0f, (2f * PI).toFloat(),
        infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "w"
    )

    val particles = remember {
        listOf(
            Offset(0.08f, 0.25f), Offset(0.92f, 0.18f),
            Offset(0.05f, 0.72f), Offset(0.95f, 0.65f),
            Offset(0.5f,  0.08f), Offset(0.28f, 0.88f),
            Offset(0.72f, 0.82f), Offset(0.18f, 0.48f),
            Offset(0.82f, 0.38f)
        )
    }

    // ─────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        // ── Animated Background ───────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF2D1060).copy(alpha = glowPulse * 0.65f), Color.Transparent),
                    center = Offset(size.width * 0.3f, size.height * 0.2f),
                    radius = size.minDimension * 0.75f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF3A1010).copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.82f),
                    radius = size.minDimension * 0.5f
                )
            )
            // Grid
            val sp = 44.dp.toPx()
            for (c in 0..(size.width / sp).toInt() + 2)
                for (r in 0..(size.height / sp).toInt() + 2)
                    drawCircle(
                        Color(0xFF6D28D9).copy(alpha = 0.08f),
                        1.2.dp.toPx(),
                        Offset(c * sp, r * sp)
                    )
            // Particles
            particles.forEachIndexed { i, pos ->
                val prog = (particleAnim + i * (1f / particles.size)) % 1f
                val x    = pos.x * size.width + sin(prog * 2f * PI.toFloat() + i.toFloat()) * 18.dp.toPx()
                val y    = size.height * (1f - prog)
                val a    = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.6f
                drawCircle(
                    color  = when (i % 3) {
                        0    -> Color(0xFF8B5CF6).copy(alpha = a)
                        1    -> Color(0xFF22D3EE).copy(alpha = a * 0.7f)
                        else -> Color(0xFFC46A6A).copy(alpha = a * 0.5f)
                    },
                    radius = (1.5f + i % 3).dp.toPx(),
                    center = Offset(x, y)
                )
            }
            // Rings (top center area)
            val rc = Offset(size.width / 2f, 160.dp.toPx())
            listOf(ring1, ring2).forEach { p ->
                drawCircle(
                    color  = Color(0xFF8B5CF6).copy(alpha = (1f - p) * 0.28f),
                    radius = p * 90.dp.toPx() + 42.dp.toPx(),
                    center = rc,
                    style  = Stroke(1.dp.toPx())
                )
            }
        }

        // Burst particles overlay
        if (burstActive) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val bx = size.width / 2f
                val by = size.height * 0.88f
                for (i in 0 until 14) {
                    val angle = (i * (360f / 14f)) * PI.toFloat() / 180f
                    val dist  = burstProgress * 130.dp.toPx()
                    val x     = bx + cos(angle) * dist
                    val y     = by + sin(angle) * dist
                    val a     = (1f - burstProgress).coerceAtLeast(0f)
                    drawCircle(
                        color  = if (i % 2 == 0) Color(0xFF8B5CF6).copy(alpha = a)
                        else             Color(0xFFF59E0B).copy(alpha = a),
                        radius = (5f - burstProgress * 4.5f).coerceAtLeast(0.3f).dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }

        // ── Main Scroll Content ───────────
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
                    .padding(start = 16.dp, end = 16.dp, top = 52.dp, bottom = 8.dp),
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
                    Text("Book Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Kevin Omondi", fontSize = 12.sp, color = Color(0xFF8B5CF6))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("Step 1/2", fontSize = 11.sp, color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
                }
            }

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Select" to true, "Review" to false, "Pay" to false).forEach { (label, active) ->
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                                .background(if (active) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.1f))
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(label, fontSize = 10.sp, color = if (active) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.3f))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

                // Artist reminder card
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF1A0838), Color(0xFF1A0A1A)))
                        )
                        .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(18.dp))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Spacer(Modifier.width(5.dp))
                                Text("Available Now", fontSize = 11.sp, color = Color(0xFF10B981))
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("KSh 800", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("per hour", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // SESSION TYPE
                BkSectionLabel("Session Type")
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sessionTypes.forEachIndexed { i, type ->
                        val isSel = selectedSession == i
                        Box(
                            modifier = Modifier
                                .size(width = 112.dp, height = 92.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (isSel) Brush.verticalGradient(
                                        listOf(type.color.copy(alpha = 0.35f), type.color.copy(alpha = 0.12f))
                                    ) else Brush.verticalGradient(
                                        listOf(Color(0xFF1A0E35), Color(0xFF110A22))
                                    )
                                )
                                .border(
                                    if (isSel) 1.5.dp else 1.dp,
                                    if (isSel) type.color else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedSession = i }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(type.emoji, fontSize = 28.sp)
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    type.name, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color.White.copy(alpha = 0.45f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // DATE CAROUSEL
                BkSectionLabel("Select Date — May 2026")
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    dateItems.forEachIndexed { i, (day, date) ->
                        val isSel = selectedDate == i
                        Box(
                            modifier = Modifier
                                .size(width = 58.dp, height = 82.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSel) Brush.verticalGradient(
                                        listOf(Color(0xFF8B5CF6).copy(alpha = 0.4f), Color(0xFF6D28D9).copy(alpha = 0.25f))
                                    ) else Brush.verticalGradient(
                                        listOf(Color(0xFF1A0E35), Color(0xFF110A22))
                                    )
                                )
                                .border(
                                    if (isSel) 1.5.dp else 1.dp,
                                    if (isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedDate = i },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    day, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                    color = if (isSel) Color(0xFFA78BFA) else Color.White.copy(alpha = 0.35f)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "$date", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color.White.copy(alpha = 0.65f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier.size(5.dp).clip(CircleShape)
                                        .background(
                                            if (isSel) Color(0xFF8B5CF6)
                                            else Color.Transparent
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // TIME SLOTS
                BkSectionLabel("Available Time Slots")
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                if (index < timeSlots.size) {
                                    val slot        = timeSlots[index]
                                    val isSel       = selectedTime == slot
                                    val isUnavail   = slot in unavailableSlots
                                    Box(
                                        modifier = Modifier
                                            .weight(1f).height(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                when {
                                                    isUnavail -> Brush.horizontalGradient(listOf(Color(0xFF160D20), Color(0xFF120A1C)))
                                                    isSel     -> Brush.horizontalGradient(listOf(Color(0xFF8B5CF6).copy(alpha = 0.4f), Color(0xFF6D28D9).copy(alpha = 0.3f)))
                                                    else      -> Brush.horizontalGradient(listOf(Color(0xFF1A0E35), Color(0xFF110A22)))
                                                }
                                            )
                                            .border(
                                                if (isSel) 1.5.dp else 1.dp,
                                                when {
                                                    isUnavail -> Color.White.copy(alpha = 0.05f)
                                                    isSel     -> Color(0xFF8B5CF6)
                                                    else      -> Color.White.copy(alpha = 0.1f)
                                                },
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable(enabled = !isUnavail) { selectedTime = slot },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                slot, fontSize = 12.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isUnavail -> Color.White.copy(alpha = 0.18f)
                                                    isSel     -> Color.White
                                                    else      -> Color.White.copy(alpha = 0.6f)
                                                }
                                            )
                                            if (isUnavail) Text("Taken", fontSize = 9.sp, color = Color.White.copy(alpha = 0.18f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // DURATION
                BkSectionLabel("Duration")
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(1, 2, 3, 4).forEach { hrs ->
                        val isSel = selectedDuration == hrs
                        Box(
                            modifier = Modifier
                                .weight(1f).height(64.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSel) Brush.verticalGradient(
                                        listOf(Color(0xFF9F3A3A).copy(alpha = 0.35f), Color(0xFF7A1F1F).copy(alpha = 0.2f))
                                    ) else Brush.verticalGradient(
                                        listOf(Color(0xFF1A0E35), Color(0xFF110A22))
                                    )
                                )
                                .border(
                                    if (isSel) 1.5.dp else 1.dp,
                                    if (isSel) Color(0xFFC46A6A) else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedDuration = hrs },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "$hrs hr${if (hrs > 1) "s" else ""}",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                                Text(
                                    "KSh ${pricePerHour * hrs}",
                                    fontSize = 10.sp,
                                    color = if (isSel) Color(0xFFC46A6A) else Color.White.copy(alpha = 0.28f)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // NOTES
                BkSectionLabel("Special Notes  (optional)")
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = {
                        Text("e.g. Acoustic guitar for outdoor wedding...",
                            color = Color.White.copy(alpha = 0.22f), fontSize = 13.sp)
                    },
                    modifier = Modifier.fillMaxWidth().height(88.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor       = Color.White,
                        unfocusedTextColor     = Color.White,
                        focusedBorderColor     = Color(0xFF8B5CF6),
                        unfocusedBorderColor   = Color.White.copy(alpha = 0.1f),
                        cursorColor            = Color(0xFF8B5CF6),
                        focusedContainerColor  = Color(0xFF130A28),
                        unfocusedContainerColor= Color(0xFF130A28)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(24.dp))

                // PRICE BREAKDOWN
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF1A0838), Color(0xFF110A22))))
                        .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Text("Price Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(16.dp))

                    BkPriceRow(
                        "KSh $pricePerHour × $selectedDuration hr${if (selectedDuration > 1) "s" else ""}",
                        "KSh ${pricePerHour * selectedDuration}"
                    )
                    Spacer(Modifier.height(8.dp))
                    BkPriceRow("Platform fee", "KSh $serviceFee")
                    Spacer(Modifier.height(8.dp))
                    BkPriceRow("Session type", sessionTypes[selectedSession].name)

                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.08f)))
                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            "KSh $totalPrice",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // M-PESA SECTION
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF081A10), Color(0xFF040C08))))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Pay via M-Pesa", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Safaricom · Instant & Secure", fontSize = 11.sp, color = Color(0xFF10B981))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = mpesaPhone,
                        onValueChange = { mpesaPhone = it },
                        label = { Text("M-Pesa Phone Number", color = Color.White.copy(alpha = 0.35f)) },
                        placeholder = { Text("e.g. 0712 345 678", color = Color.White.copy(alpha = 0.18f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor        = Color.White,
                            unfocusedTextColor      = Color.White,
                            focusedBorderColor      = Color(0xFF10B981),
                            unfocusedBorderColor    = Color(0xFF10B981).copy(alpha = 0.3f),
                            cursorColor             = Color(0xFF10B981),
                            focusedLabelColor       = Color(0xFF10B981),
                            focusedContainerColor   = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.08f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔒", fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Payment is held securely by Instruxa.\nReleased to Kevin only after your session.",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981).copy(alpha = 0.75f),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Equalizer above confirm
                Box(modifier = Modifier.fillMaxWidth().height(22.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val count = 22
                        val bw    = size.width / (count * 2f)
                        for (i in 0 until count) {
                            val h = (0.25f + 0.75f * abs(sin(waveAnim + i * 0.55f))) * size.height
                            drawRoundRect(
                                color       = Color(0xFF8B5CF6).copy(alpha = 0.4f),
                                topLeft     = Offset(i * bw * 2f, size.height - h),
                                size        = Size(bw * 0.65f, h),
                                cornerRadius= CornerRadius(bw / 2f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // CONFIRM BUTTON
                Box(
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF9F3A3A), Color(0xFF8B5CF6), Color(0xFF22D3EE).copy(alpha = 0.75f))
                            )
                        )
                        .clickable {
                            if (!burstActive && !isConfirmed) {
                                scope.launch { burstActive = true }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Confirm & Pay  KSh $totalPrice",
                            fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = 0.3.sp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("→", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    "By confirming you agree to Instruxa's booking policy",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.22f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(50.dp))
            }
        }

        // ════════════════════════════════
        //  SUCCESS OVERLAY
        // ════════════════════════════════
        AnimatedVisibility(
            visible = isConfirmed,
            enter   = fadeIn(tween(500)) + scaleIn(spring(Spring.DampingRatioMediumBouncy)),
            exit    = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color(0xFF080612).copy(alpha = 0.97f)),
                contentAlignment = Alignment.Center
            ) {
                // Falling confetti
                val confInf = rememberInfiniteTransition(label = "conf")
                val confAnim by confInf.animateFloat(
                    0f, 1f,
                    infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
                    label = "ca"
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val confColors = listOf(Color(0xFF8B5CF6),Color(0xFFF59E0B),Color(0xFF10B981),Color(0xFF22D3EE),Color(0xFFC46A6A))
                    for (i in 0 until 35) {
                        val prog = (confAnim + i * (1f / 35f)) % 1f
                        val x    = (i * 41f + sin(prog * PI.toFloat() * 2f) * 20f) % size.width
                        val y    = prog * size.height
                        val a    = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.85f
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

                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF059669), Color(0xFF065F46)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(52.dp))
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("Booking\nConfirmed!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center, lineHeight = 40.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Kevin has been notified.\nM-Pesa push sent to your phone.",
                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center, lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    // Receipt
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF130A28))
                            .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                            .padding(18.dp)
                    ) {
                        Text("Booking Receipt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(12.dp))
                        BkReceiptRow("Artist",   "Kevin Omondi")
                        BkReceiptRow("Date",     "${dateItems[selectedDate].first}, May ${dateItems[selectedDate].second}")
                        BkReceiptRow("Time",     selectedTime)
                        BkReceiptRow("Duration", "$selectedDuration hr${if (selectedDuration > 1) "s" else ""}")
                        BkReceiptRow("Type",     sessionTypes[selectedSession].name)
                        Spacer(Modifier.height(10.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.07f)))
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Total Paid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("KSh $totalPrice", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // Rate session button
                    Box(
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF9F3A3A), Color(0xFF8B5CF6))))
                            .clickable {
                                navController.navigate(ROUT_REVIEWS) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Rate Your Session  ⭐", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "Back to Home",
                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier.clickable {
                            navController.navigate(ROUT_HOME) { popUpTo(0) { inclusive = true } }
                        }
                    )
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

// ── Helper Composables ────────────────────

@Composable
fun BkSectionLabel(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f), letterSpacing = 0.2.sp)
}

@Composable
fun BkPriceRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.45f))
        Text(value, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun BkReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.38f))
        Text(value, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BookingScreenPreview() {
    InstruxaTheme { BookingScreen(navController = rememberNavController()) }
}