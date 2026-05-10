package com.clementvexegon.instruxa.ui.screens.profile

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_BOOKING
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────
//  INSTRUXA — Artist Profile Screen
//
//  The most important screen in the app.
//  When a user taps an artist card on Home,
//  this is what they see.
//
//  Features:
//  • Animated cinematic hero header
//  • Live pulsing availability indicator
//  • Stats that COUNT UP on entry (gigs, rating)
//  • Interactive portfolio grid with tap glow
//  • Skill chips that bounce in one by one
//  • Animated waveform showing artist is "live"
//  • Particle burst on Hire button press
//  • Favorite heart toggle with spring bounce
//  • Floating music notes around the avatar
//  • Animated equalizer in bio section
// ─────────────────────────────────────────

// Dummy artist data — replace with Firestore later
data class ArtistData(
    val name: String,
    val role: String,
    val location: String,
    val rating: Float,
    val reviews: Int,
    val gigs: Int,
    val experience: String,
    val price: Int,
    val bio: String,
    val skills: List<String>,
    val isAvailable: Boolean,
    val portfolioEmojis: List<String>
)

val dummyArtist = ArtistData(
    name         = "Kevin Omondi",
    role         = "Guitarist · Pianist",
    location     = "Nairobi, Kenya",
    rating       = 4.9f,
    reviews      = 47,
    gigs         = 120,
    experience   = "3 yrs",
    price        = 800,
    bio          = "Passionate guitarist with 3 years of professional " +
            "experience. I specialize in Afrobeats, Gospel and " +
            "Contemporary genres. Available for events, recording " +
            "sessions and private lessons.",
    skills       = listOf(
        "Acoustic Guitar", "Electric Guitar", "Piano",
        "Afrobeats", "Gospel", "Recording", "Live Events", "Lessons"
    ),
    isAvailable  = true,
    portfolioEmojis = listOf("🎸","🎹","🎤","🎶","🎵","🎷","🥁","🎻","🎺","🎧","🎼","🎙️")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArtistProfileScreen(navController: NavController) {

    val scrollState = rememberScrollState()

    // ── Entry animations ──────────────────
    val headerAlpha   = remember { Animatable(0f) }
    val contentSlide  = remember { Animatable(60f) }
    val contentAlpha  = remember { Animatable(0f) }
    val avatarScale   = remember { Animatable(0f) }

    // Counting stats
    var displayGigs    by remember { mutableIntStateOf(0) }
    var displayReviews by remember { mutableIntStateOf(0) }
    var displayRating  by remember { mutableFloatStateOf(0f) }

    // Interactive state
    var isFavorited      by remember { mutableStateOf(false) }
    var favoriteScale    by remember { mutableFloatStateOf(1f) }
    var selectedPortfolio by remember { mutableIntStateOf(-1) }
    var hirePressed      by remember { mutableStateOf(false) }
    var hireBurst        by remember { mutableFloatStateOf(0f) }

    // Chip animation — each chip bounces in
    val chipAlphas = remember { List(dummyArtist.skills.size) { Animatable(0f) } }
    val chipScales = remember { List(dummyArtist.skills.size) { Animatable(0.6f) } }

    LaunchedEffect(Unit) {
        // Hero fades in
        headerAlpha.animateTo(1f, tween(500))

        // Avatar bounces in
        avatarScale.animateTo(
            1f, spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow
            )
        )

        // Content slides up
        launch {
            contentSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
            contentAlpha.animateTo(1f, tween(400))
        }

        // Stats count up
        launch {
            delay(400)
            val steps = 30
            for (i in 1..steps) {
                displayGigs    = (dummyArtist.gigs * i / steps)
                displayReviews = (dummyArtist.reviews * i / steps)
                displayRating  = dummyArtist.rating * i / steps
                delay(18)
            }
            displayGigs    = dummyArtist.gigs
            displayReviews = dummyArtist.reviews
            displayRating  = dummyArtist.rating
        }

        // Chips bounce in one by one
        launch {
            delay(600)
            chipAlphas.forEachIndexed { i, anim ->
                launch {
                    delay(i * 60L)
                    anim.animateTo(1f, tween(250))
                    chipScales[i].animateTo(
                        1f, spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness    = Spring.StiffnessMedium
                        )
                    )
                }
            }
        }
    }

    // Hire burst animation
    LaunchedEffect(hirePressed) {
        if (hirePressed) {
            hireBurst = 0f
            repeat(20) {
                hireBurst = it / 20f
                delay(16)
            }
            delay(200)
            navController.navigate(ROUT_BOOKING)
        }
    }

    // ── Infinite background animations ────
    val inf = rememberInfiniteTransition(label = "profile_bg")

    val ring1 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "r1"
    )
    val ring2 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2500, delayMillis = 833, easing = LinearEasing), RepeatMode.Restart),
        label = "r2"
    )
    val ring3 by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2500, delayMillis = 1666, easing = LinearEasing), RepeatMode.Restart),
        label = "r3"
    )
    val noteFloat by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "note"
    )
    val glowPulse by inf.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val waveAnim by inf.animateFloat(
        0f, (2f * PI).toFloat(),
        infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )
    val availPulse by inf.animateFloat(
        0.3f, 1f,
        infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "avail"
    )

    val favoriteScaleAnim by animateFloatAsState(
        targetValue = favoriteScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fav"
    )
    val hireScaleAnim by animateFloatAsState(
        targetValue = if (hirePressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "hire"
    )

    // ─────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080612))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            // ════════════════════════════
            //  HERO HEADER
            // ════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .alpha(headerAlpha.value)
            ) {

                // Hero background canvas
                Canvas(modifier = Modifier.fillMaxSize()) {

                    // Deep gradient bg
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A0838),
                                Color(0xFF0D0520),
                                Color(0xFF080612)
                            )
                        )
                    )

                    // Side glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(alpha = glowPulse * 0.35f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.15f, size.height * 0.3f),
                            radius = size.minDimension * 0.6f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF9F3A3A).copy(alpha = glowPulse * 0.25f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.85f, size.height * 0.5f),
                            radius = size.minDimension * 0.45f
                        )
                    )

                    // Grid dots
                    val sp = 36.dp.toPx()
                    val cols = (size.width / sp).toInt() + 2
                    val rows = (size.height / sp).toInt() + 2
                    for (c in 0..cols) for (r in 0..rows) {
                        drawCircle(
                            color = Color(0xFF6D28D9).copy(alpha = 0.1f),
                            radius = 1.2.dp.toPx(),
                            center = Offset(c * sp, r * sp)
                        )
                    }

                    // Pulsing rings from avatar center
                    val avatarCenter = Offset(size.width / 2f, size.height * 0.58f)
                    listOf(ring1, ring2, ring3).forEach { p ->
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = (1f - p) * 0.4f),
                            radius = p * 110.dp.toPx() + 52.dp.toPx(),
                            center = avatarCenter,
                            style = Stroke(1.2.dp.toPx())
                        )
                    }

                    // Floating music notes around avatar
                    val noteAngles = listOf(0f, 72f, 144f, 216f, 288f)
                    noteAngles.forEachIndexed { i, angle ->
                        val prog  = (noteFloat + i * 0.2f) % 1f
                        val rad   = (angle * PI / 180f).toFloat()
                        val dist  = 62.dp.toPx() + prog * 45.dp.toPx()
                        val x     = avatarCenter.x + cos(rad) * dist
                        val y     = avatarCenter.y + sin(rad) * dist - prog * 20.dp.toPx()
                        val alpha = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.8f
                        val r     = (2.5f + (i % 2) * 1.5f).dp.toPx()
                        drawCircle(
                            color = Color(0xFF22D3EE).copy(alpha = alpha),
                            radius = r,
                            center = Offset(x, y)
                        )
                    }
                }

                // Top bar — back + share + favorite
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Share button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Favorite toggle with spring bounce
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFavorited)
                                        Color(0xFF9F3A3A).copy(alpha = 0.3f)
                                    else
                                        Color.White.copy(alpha = 0.1f)
                                )
                                .clickable {
                                    isFavorited   = !isFavorited
                                    favoriteScale = 1.4f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorited)
                                    Icons.Default.Favorite
                                else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorited)
                                    Color(0xFFC46A6A)
                                else
                                    Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .scale(favoriteScaleAnim)
                            )
                        }
                    }
                }

                // Avatar + name area
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with glow
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(avatarScale.value),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer glow ring
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF8B5CF6).copy(alpha = glowPulse * 0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }

                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF9F3A3A),
                                            Color(0xFF6D28D9)
                                        )
                                    )
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "KO",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Available indicator dot
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color(0xFF10B981).copy(alpha = availPulse * 0.4f),
                                    radius = size.minDimension * 0.7f
                                )
                                drawCircle(
                                    color = Color(0xFF10B981),
                                    radius = size.minDimension * 0.4f
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dummyArtist.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = dummyArtist.role,
                            fontSize = 13.sp,
                            color = Color(0xFF8B5CF6)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = dummyArtist.location,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // ════════════════════════════
            //  BODY CONTENT
            // ════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = contentSlide.value.dp)
                    .alpha(contentAlpha.value)
                    .padding(horizontal = 20.dp)
            ) {

                // ── Stats row ─────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF130A28))
                        .border(
                            1.dp,
                            Color(0xFF8B5CF6).copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Rating
                    StatItem(
                        value = "%.1f".format(displayRating),
                        label = "Rating",
                        color = Color(0xFFF59E0B)
                    )
                    StatDivider()
                    // Reviews
                    StatItem(
                        value = "$displayReviews",
                        label = "Reviews",
                        color = Color(0xFF22D3EE)
                    )
                    StatDivider()
                    // Gigs
                    StatItem(
                        value = "$displayGigs+",
                        label = "Gigs",
                        color = Color(0xFF10B981)
                    )
                    StatDivider()
                    // Experience
                    StatItem(
                        value = dummyArtist.experience,
                        label = "Experience",
                        color = Color(0xFF8B5CF6)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Bio section ───────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF130A28))
                        .border(
                            1.dp,
                            Color(0xFF8B5CF6).copy(alpha = 0.15f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "About",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Live waveform when available
                        if (dummyArtist.isAvailable) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .height(18.dp)
                                        .width(42.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val count = 6
                                        val w = size.width / (count * 2f - 1)
                                        for (i in 0 until count) {
                                            val h = (0.3f + 0.7f * abs(
                                                sin(waveAnim + i * 0.8f)
                                            )) * size.height
                                            drawRoundRect(
                                                color = Color(0xFF10B981).copy(alpha = 0.85f),
                                                topLeft = Offset(
                                                    i * w * 2f,
                                                    size.height - h
                                                ),
                                                size = Size(w * 0.75f, h),
                                                cornerRadius = CornerRadius(4f)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Available",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dummyArtist.bio,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Skills chips ──────────
                Text(
                    text = "Skills",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    dummyArtist.skills.forEachIndexed { i, skill ->
                        Box(
                            modifier = Modifier
                                .alpha(chipAlphas[i].value)
                                .scale(chipScales[i].value)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when (i % 4) {
                                        0 -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                        1 -> Color(0xFF9F3A3A).copy(alpha = 0.2f)
                                        2 -> Color(0xFF22D3EE).copy(alpha = 0.15f)
                                        else -> Color(0xFF10B981).copy(alpha = 0.15f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    when (i % 4) {
                                        0 -> Color(0xFF8B5CF6).copy(alpha = 0.4f)
                                        1 -> Color(0xFF9F3A3A).copy(alpha = 0.4f)
                                        2 -> Color(0xFF22D3EE).copy(alpha = 0.3f)
                                        else -> Color(0xFF10B981).copy(alpha = 0.3f)
                                    },
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = skill,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = when (i % 4) {
                                    0 -> Color(0xFFA78BFA)
                                    1 -> Color(0xFFC46A6A)
                                    2 -> Color(0xFF22D3EE)
                                    else -> Color(0xFF10B981)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Portfolio grid ─────────
                Text(
                    text = "Portfolio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 4-column emoji grid with tap glow
                val cols = 4
                val rows = (dummyArtist.portfolioEmojis.size + cols - 1) / cols
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in 0 until rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (col in 0 until cols) {
                                val index = row * cols + col
                                if (index < dummyArtist.portfolioEmojis.size) {
                                    val isSelected = selectedPortfolio == index
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(72.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isSelected)
                                                    Brush.linearGradient(
                                                        listOf(
                                                            Color(0xFF8B5CF6).copy(alpha = 0.4f),
                                                            Color(0xFF9F3A3A).copy(alpha = 0.4f)
                                                        )
                                                    )
                                                else
                                                    Brush.linearGradient(
                                                        listOf(
                                                            Color(0xFF1A0E35),
                                                            Color(0xFF110A22)
                                                        )
                                                    )
                                            )
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected)
                                                    Color(0xFF8B5CF6).copy(alpha = 0.8f)
                                                else
                                                    Color(0xFF8B5CF6).copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable {
                                                selectedPortfolio =
                                                    if (selectedPortfolio == index) -1
                                                    else index
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dummyArtist.portfolioEmojis[index],
                                            fontSize = 28.sp
                                        )

                                        // Play overlay on selected
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Color(0xFF8B5CF6).copy(alpha = 0.25f)
                                                    ),
                                                contentAlignment = Alignment.TopEnd
                                            ) {
                                                Icon(
                                                    Icons.Default.PlayArrow,
                                                    contentDescription = "Play",
                                                    tint = Color.White.copy(alpha = 0.9f),
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Reviews preview ────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF130A28))
                        .border(
                            1.dp,
                            Color(0xFF8B5CF6).copy(alpha = 0.15f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reviews",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%.1f".format(dummyArtist.rating),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = " (${dummyArtist.reviews})",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sample review cards
                    listOf(
                        Triple("Brian M.", "🎸", "Kevin was amazing! Super professional and talented."),
                        Triple("Aisha K.", "🎹", "Best pianist I've ever hired. Will definitely book again!")
                    ).forEach { (name, emoji, text) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E1040)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = text,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── Price + Hire Button ────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Session rate",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "KSh ${dummyArtist.price}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "/hr",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.padding(bottom = 4.dp, start = 3.dp)
                            )
                        }
                    }

                    // HIRE BUTTON with particle burst
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .width(160.dp)
                            .scale(hireScaleAnim)
                    ) {
                        // Burst canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (hirePressed && hireBurst > 0f) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val burstCount = 8
                                for (i in 0 until burstCount) {
                                    val angle = (i * 360f / burstCount) * PI.toFloat() / 180f
                                    val dist  = hireBurst * 60.dp.toPx()
                                    val bx    = cx + cos(angle) * dist
                                    val by    = cy + sin(angle) * dist
                                    val alpha = (1f - hireBurst) * 0.9f
                                    drawCircle(
                                        color = if (i % 2 == 0)
                                            Color(0xFF8B5CF6).copy(alpha = alpha)
                                        else
                                            Color(0xFFF59E0B).copy(alpha = alpha),
                                        radius = (4f - hireBurst * 3f).coerceAtLeast(0.5f).dp.toPx(),
                                        center = Offset(bx, by)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF9F3A3A),
                                            Color(0xFF8B5CF6)
                                        )
                                    )
                                )
                                .clickable { hirePressed = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hire Kevin  →",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// ── Reusable Stat Item ────────────────────
@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
    }
}

// ── Thin vertical divider ─────────────────
@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.08f))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtistProfileScreenPreview() {
    InstruxaTheme {
        ArtistProfileScreen(navController = rememberNavController())
    }
}