package com.clementvexegon.instruxa.ui.screens.auth.register

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_HOME
import com.clementvexegon.instruxa.navigation.ROUT_LOGIN
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────
//  INSTRUXA — Register Screen
//
//  Features:
//  • Animated DNA-helix style background
//  • Artist / Client toggle with instrument
//    field that appears only for Artists
//  • Password strength indicator bar
//  • All fields validated before submit
//  • Spring bounce card entry
//  • Gradient submit button
//  • Instrument category chips for Artists
// ─────────────────────────────────────────

// Instrument options artists can choose from
val instrumentOptions = listOf(
    "🎸 Guitar", "🎹 Piano", "🥁 Drums",
    "🎻 Violin", "🎺 Trumpet", "🎷 Saxophone",
    "🎙️ Vocals", "🎚️ DJ / Producer", "🪗 Accordion"
)

// Password strength levels
fun passwordStrength(password: String): Pair<Float, Color> {
    return when {
        password.length < 4                            -> 0.15f to Color(0xFFEF4444)
        password.length < 7                            -> 0.40f to Color(0xFFF59E0B)
        password.length < 10                           -> 0.70f to Color(0xFF8B5CF6)
        password.any { it.isUpperCase() } &&
                password.any { it.isDigit() }                  -> 1.00f to Color(0xFF10B981)
        else                                           -> 0.85f to Color(0xFF22D3EE)
    }
}

@Composable
fun RegisterScreen(navController: NavController) {

    // ── Form state ────────────────────────
    var fullName         by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var showPassword     by remember { mutableStateOf(false) }
    var showConfirm      by remember { mutableStateOf(false) }
    var isArtist         by remember { mutableStateOf(false) }
    var selectedInstrument by remember { mutableStateOf("") }
    var isLoading        by remember { mutableStateOf(false) }

    // Errors
    var nameError     by remember { mutableStateOf("") }
    var emailError    by remember { mutableStateOf("") }
    var passError     by remember { mutableStateOf("") }
    var confirmError  by remember { mutableStateOf("") }

    // ── Entry animations ──────────────────
    val cardAlpha = remember { Animatable(0f) }
    val cardSlide = remember { Animatable(100f) }
    val logoScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            1f, spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessMedium
            )
        )
        delay(100)
        cardAlpha.animateTo(1f, tween(500))
        cardSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    // ── Infinite background animations ────
    val inf = rememberInfiniteTransition(label = "reg_bg")

    val helixAnim by inf.animateFloat(
        0f, (2f * PI).toFloat(),
        infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "helix"
    )
    val particleAnim by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "p"
    )
    val glowPulse by inf.animateFloat(
        0.3f, 0.9f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val waveAnim by inf.animateFloat(
        0f, (2f * PI).toFloat(),
        infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )

    val toggleOffset by animateFloatAsState(
        targetValue  = if (isArtist) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "toggle"
    )

    val btnScale by animateFloatAsState(
        targetValue  = if (isLoading) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label        = "btn"
    )

    val (strengthVal, strengthColor) = passwordStrength(password)
    val strengthAnim by animateFloatAsState(
        targetValue  = if (password.isEmpty()) 0f else strengthVal,
        animationSpec = tween(400),
        label        = "strength"
    )

    val particles = remember {
        listOf(
            Offset(0.08f, 0.25f), Offset(0.92f, 0.18f),
            Offset(0.18f, 0.75f), Offset(0.82f, 0.72f),
            Offset(0.48f, 0.05f), Offset(0.55f, 0.95f),
            Offset(0.72f, 0.38f), Offset(0.28f, 0.48f),
            Offset(0.88f, 0.55f), Offset(0.12f, 0.62f)
        )
    }

    // ── Root ──────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080612))
    ) {

        // ── Animated background ───────────
        Canvas(modifier = Modifier.fillMaxSize()) {

            // Main purple glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2D1060).copy(alpha = glowPulse * 0.65f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.15f),
                    radius = size.minDimension * 0.7f
                )
            )

            // Phoenix accent glow bottom left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3A0A0A).copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.9f),
                    radius = size.minDimension * 0.4f
                )
            )

            // Aqua accent glow top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A2A2A).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = size.minDimension * 0.35f
                )
            )

            // Grid dots
            val spacing = 44.dp.toPx()
            val cols = (size.width / spacing).toInt() + 2
            val rows = (size.height / spacing).toInt() + 2
            for (col in 0..cols) {
                for (row in 0..rows) {
                    drawCircle(
                        color = Color(0xFF6D28D9).copy(alpha = 0.09f),
                        radius = 1.4.dp.toPx(),
                        center = Offset(col * spacing, row * spacing)
                    )
                }
            }

            // DNA helix on the right side
            val helixX   = size.width * 0.92f
            val helixH   = size.height
            val amplitude = 18.dp.toPx()
            val period    = 80.dp.toPx()
            var prevY1 = 0f
            var prevY2 = 0f
            val helixSteps = 60
            for (step in 0..helixSteps) {
                val t  = step.toFloat() / helixSteps
                val y  = helixH * t
                val x1 = helixX + amplitude * sin(helixAnim + t * 2f * PI.toFloat() * (helixH / period))
                val x2 = helixX - amplitude * sin(helixAnim + t * 2f * PI.toFloat() * (helixH / period))
                val a  = 0.25f + 0.15f * sin(t * 4f * PI.toFloat())

                if (step > 0) {
                    drawLine(
                        color  = Color(0xFF8B5CF6).copy(alpha = a),
                        start  = Offset(prevY1.let { helixX + amplitude * sin(helixAnim + (t - 1f/helixSteps) * 2f * PI.toFloat() * (helixH / period)) }, y - helixH / helixSteps),
                        end    = Offset(x1, y),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    drawLine(
                        color  = Color(0xFF22D3EE).copy(alpha = a * 0.7f),
                        start  = Offset(helixX - amplitude * sin(helixAnim + (t - 1f/helixSteps) * 2f * PI.toFloat() * (helixH / period)), y - helixH / helixSteps),
                        end    = Offset(x2, y),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }

                // Connecting rungs every few steps
                if (step % 5 == 0) {
                    drawLine(
                        color       = Color(0xFF8B5CF6).copy(alpha = a * 0.5f),
                        start       = Offset(x1, y),
                        end         = Offset(x2, y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    drawCircle(
                        color  = Color(0xFF8B5CF6).copy(alpha = a),
                        radius = 2.dp.toPx(),
                        center = Offset(x1, y)
                    )
                    drawCircle(
                        color  = Color(0xFF22D3EE).copy(alpha = a * 0.7f),
                        radius = 2.dp.toPx(),
                        center = Offset(x2, y)
                    )
                }
            }

            // Floating particles
            particles.forEachIndexed { i, pos ->
                val progress = (particleAnim + i * (1f / particles.size)) % 1f
                val x = pos.x * size.width * 0.85f +
                        sin(progress * 2f * PI.toFloat() + i * 1.3f) * 18.dp.toPx()
                val y = size.height * (1f - progress)
                val a = sin(progress * PI.toFloat()).coerceIn(0f, 1f) * 0.65f
                val c = when (i % 3) {
                    0    -> Color(0xFF8B5CF6).copy(alpha = a)
                    1    -> Color(0xFF22D3EE).copy(alpha = a * 0.75f)
                    else -> Color(0xFFC46A6A).copy(alpha = a * 0.6f)
                }
                drawCircle(
                    color  = c,
                    radius = (1.5f + (i % 3)).dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // ── Scrollable content ────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(52.dp))

            // ── Logo ──────────────────────
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(logoScale.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(alpha = glowPulse * 0.5f),
                                Color.Transparent
                            )
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF9F3A3A), Color(0xFF6D28D9))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("IX", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Text(
                "Join the Instruxa community",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Artist / Client Toggle ────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF130A28))
                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .alpha(cardAlpha.value)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                            )
                        )
                        .align(if (isArtist) Alignment.CenterEnd else Alignment.CenterStart)
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { isArtist = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎧", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Client",
                                fontSize = 14.sp,
                                fontWeight = if (!isArtist) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isArtist) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { isArtist = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎸", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Artist",
                                fontSize = 14.sp,
                                fontWeight = if (isArtist) FontWeight.Bold else FontWeight.Normal,
                                color = if (isArtist) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Register Card ─────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardSlide.value.dp)
                    .alpha(cardAlpha.value)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1A0E35), Color(0xFF110A22))
                        )
                    )
                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.18f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {

                Text(
                    if (isArtist) "Artist Registration" else "Client Registration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    if (isArtist)
                        "Set up your profile to start getting gigs"
                    else
                        "Sign up to start hiring instrumentalists",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 3.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Full Name
                OutlinedTextField(
                    value         = fullName,
                    onValueChange = { fullName = it; nameError = "" },
                    label         = { Text("Full Name", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon   = {
                        Icon(Icons.Default.Person, null, tint = Color(0xFF8B5CF6))
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    isError       = nameError.isNotEmpty(),
                    supportingText = {
                        if (nameError.isNotEmpty())
                            Text(nameError, color = Color(0xFFEF4444), fontSize = 12.sp)
                    },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White,
                        focusedBorderColor   = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        cursorColor          = Color(0xFF8B5CF6),
                        focusedLabelColor    = Color(0xFF8B5CF6),
                        errorBorderColor     = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; emailError = "" },
                    label         = { Text("Email address", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon   = {
                        Icon(Icons.Default.Email, null, tint = Color(0xFF8B5CF6))
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine    = true,
                    isError       = emailError.isNotEmpty(),
                    supportingText = {
                        if (emailError.isNotEmpty())
                            Text(emailError, color = Color(0xFFEF4444), fontSize = 12.sp)
                    },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White,
                        focusedBorderColor   = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        cursorColor          = Color(0xFF8B5CF6),
                        focusedLabelColor    = Color(0xFF8B5CF6),
                        errorBorderColor     = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it; passError = "" },
                    label         = { Text("Password", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon   = {
                        Icon(Icons.Default.Lock, null, tint = Color(0xFF8B5CF6))
                    },
                    trailingIcon  = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                null,
                                tint = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine    = true,
                    isError       = passError.isNotEmpty(),
                    supportingText = {
                        if (passError.isNotEmpty())
                            Text(passError, color = Color(0xFFEF4444), fontSize = 12.sp)
                    },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White,
                        focusedBorderColor   = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        cursorColor          = Color(0xFF8B5CF6),
                        focusedLabelColor    = Color(0xFF8B5CF6),
                        errorBorderColor     = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                // ── Password strength bar ──
                if (password.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val strengthLabel = when {
                        strengthVal <= 0.15f -> "Weak"
                        strengthVal <= 0.40f -> "Fair"
                        strengthVal <= 0.70f -> "Good"
                        strengthVal <= 0.85f -> "Strong"
                        else                 -> "Very Strong"
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(strengthAnim)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(strengthColor)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            strengthLabel,
                            fontSize = 11.sp,
                            color    = strengthColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                OutlinedTextField(
                    value         = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmError = "" },
                    label         = { Text("Confirm Password", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon   = {
                        Icon(Icons.Default.Lock, null, tint = Color(0xFF8B5CF6))
                    },
                    trailingIcon  = {
                        IconButton(onClick = { showConfirm = !showConfirm }) {
                            Icon(
                                if (showConfirm) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                null,
                                tint = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    },
                    visualTransformation = if (showConfirm) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    modifier      = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine    = true,
                    isError       = confirmError.isNotEmpty(),
                    supportingText = {
                        if (confirmError.isNotEmpty())
                            Text(confirmError, color = Color(0xFFEF4444), fontSize = 12.sp)
                    },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White,
                        focusedBorderColor   = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                        cursorColor          = Color(0xFF8B5CF6),
                        focusedLabelColor    = Color(0xFF8B5CF6),
                        errorBorderColor     = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                // ── Artist instrument chips ─
                if (isArtist) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            tint     = Color(0xFF8B5CF6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Your Instrument",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Chip grid
                    val rows = instrumentOptions.chunked(3)
                    rows.forEach { rowItems ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { instrument ->
                                val selected = selectedInstrument == instrument
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (selected)
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF9F3A3A), Color(0xFF8B5CF6))
                                                )
                                            else
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(0xFF1A0E35),
                                                        Color(0xFF1A0E35)
                                                    )
                                                )
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) Color(0xFF8B5CF6)
                                            else Color.White.copy(alpha = 0.1f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedInstrument = instrument }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        instrument,
                                        fontSize   = 11.sp,
                                        color      = if (selected) Color.White
                                        else Color.White.copy(alpha = 0.5f),
                                        textAlign  = TextAlign.Center,
                                        fontWeight = if (selected) FontWeight.Bold
                                        else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Register Button ───────
                Button(
                    onClick = {
                        var valid = true
                        if (fullName.isBlank()) { nameError = "Name is required"; valid = false }
                        if (email.isBlank() || !email.contains("@")) { emailError = "Valid email required"; valid = false }
                        if (password.length < 6) { passError = "At least 6 characters"; valid = false }
                        if (confirmPassword != password) { confirmError = "Passwords don't match"; valid = false }
                        if (isArtist && selectedInstrument.isEmpty()) { valid = false }
                        if (valid) isLoading = true
                        // TODO: connect Firebase Auth here
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .scale(btnScale),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF6D28D9), Color(0xFF9F3A3A))
                                ),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(24.dp),
                                color       = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                "Create Account  →",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Equalizer divider ─────────
            Box(
                modifier = Modifier
                    .alpha(cardAlpha.value)
                    .height(20.dp)
                    .width(100.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = 9
                    val gap      = size.width / (barCount * 2f - 1)
                    val barW     = gap * 0.7f
                    for (i in 0 until barCount) {
                        val bh = (0.3f + 0.7f * abs(sin(waveAnim + i * 0.7f))) * size.height
                        drawRoundRect(
                            color      = Color(0xFF8B5CF6).copy(alpha = 0.45f),
                            topLeft    = Offset(i * gap * 2f, size.height - bh),
                            size       = Size(barW, bh),
                            cornerRadius = CornerRadius(barW / 2f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Login link ────────────────
            Row(
                modifier = Modifier.alpha(cardAlpha.value),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account?  ",
                    fontSize = 14.sp,
                    color    = Color.White.copy(alpha = 0.4f)
                )
                Text(
                    "Sign In",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF8B5CF6),
                    modifier   = Modifier.clickable { navController.navigate(ROUT_LOGIN) }
                )
            }

            Spacer(modifier = Modifier.height(44.dp))
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1500)
            isLoading = false
            navController.navigate(ROUT_HOME) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    InstruxaTheme {
        RegisterScreen(navController = rememberNavController())
    }
}