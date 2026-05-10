package com.clementvexegon.instruxa.ui.screens.auth.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_HOME
import com.clementvexegon.instruxa.navigation.ROUT_REGISTER
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import kotlin.math.*

@Composable
fun LoginScreen(navController: NavController) {

    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var showPassword  by remember { mutableStateOf(false) }
    var isArtist      by remember { mutableStateOf(false) }
    var isLoading     by remember { mutableStateOf(false) }
    var emailError    by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var firebaseError by remember { mutableStateOf("") }

    val cardAlpha = remember { Animatable(0f) }
    val cardSlide = remember { Animatable(80f) }
    val logoScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        kotlinx.coroutines.delay(150)
        cardAlpha.animateTo(1f, tween(500))
        cardSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    val inf = rememberInfiniteTransition(label = "login")
    val particleAnim by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart), label = "p")
    val glowPulse    by inf.animateFloat(0.4f, 1f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "g")
    val waveAnim     by inf.animateFloat(0f, (2f * PI).toFloat(), infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), label = "w")
    val ring1        by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), label = "r1")
    val ring2        by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, delayMillis = 1000, easing = LinearEasing), RepeatMode.Restart), label = "r2")
    val btnScale     by animateFloatAsState(if (isLoading) 0.96f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "btn")

    val particles = remember {
        listOf(
            Offset(0.1f,0.3f), Offset(0.9f,0.2f), Offset(0.2f,0.8f),
            Offset(0.8f,0.7f), Offset(0.5f,0.1f), Offset(0.6f,0.9f),
            Offset(0.15f,0.55f), Offset(0.75f,0.45f), Offset(0.4f,0.2f),
            Offset(0.88f,0.85f), Offset(0.35f,0.7f), Offset(0.65f,0.15f)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush = Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha = glowPulse * 0.7f), Color.Transparent), Offset(size.width * 0.5f, size.height * 0.2f), size.minDimension * 0.7f))
            drawCircle(brush = Brush.radialGradient(listOf(Color(0xFF3A0A0A).copy(alpha = 0.5f), Color.Transparent), Offset(size.width * 0.8f, size.height * 0.85f), size.minDimension * 0.45f))
            val sp = 42.dp.toPx()
            for (c in 0..(size.width / sp).toInt() + 2)
                for (r in 0..(size.height / sp).toInt() + 2)
                    drawCircle(Color(0xFF6D28D9).copy(alpha = 0.1f), 1.5.dp.toPx(), Offset(c * sp, r * sp))
            particles.forEachIndexed { i, pos ->
                val prog = (particleAnim + i * (1f / particles.size)) % 1f
                val x = pos.x * size.width + sin(prog * 2f * PI.toFloat() + i * 1.1f) * 22.dp.toPx()
                val y = size.height * (1f - prog)
                val a = sin(prog * PI.toFloat()).coerceIn(0f, 1f) * 0.7f
                drawCircle(when (i % 3) { 0 -> Color(0xFF8B5CF6).copy(alpha = a); 1 -> Color(0xFF22D3EE).copy(alpha = a * 0.8f); else -> Color(0xFFC46A6A).copy(alpha = a * 0.65f) }, (1.5f + (i % 3) * 1.2f).dp.toPx(), Offset(x, y))
            }
            val rc = Offset(size.width / 2f, 140.dp.toPx())
            listOf(ring1, ring2).forEach { p ->
                drawCircle(Color(0xFF8B5CF6).copy(alpha = (1f - p) * 0.3f), p * 80.dp.toPx() + 36.dp.toPx(), rc, style = Stroke(1.dp.toPx()))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo
            Box(modifier = Modifier.size(80.dp).scale(logoScale.value), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(brush = Brush.radialGradient(listOf(Color(0xFF8B5CF6).copy(alpha = glowPulse * 0.5f), Color.Transparent)))
                }
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF9F3A3A), Color(0xFF6D28D9)))), contentAlignment = Alignment.Center) {
                    Text("IX", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Instruxa", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-1).sp)
            Text("Welcome back", fontSize = 14.sp, color = Color.White.copy(alpha = 0.4f))
            Spacer(Modifier.height(32.dp))

            // Artist / Client toggle
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp)
                    .clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28))
                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .alpha(cardAlpha.value)
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(0.5f).padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))))
                        .align(if (isArtist) Alignment.CenterEnd else Alignment.CenterStart)
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { isArtist = false }, contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎧", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Client", fontSize = 14.sp, fontWeight = if (!isArtist) FontWeight.Bold else FontWeight.Normal, color = if (!isArtist) Color.White else Color.White.copy(alpha = 0.4f))
                        }
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { isArtist = true }, contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎸", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Artist", fontSize = 14.sp, fontWeight = if (isArtist) FontWeight.Bold else FontWeight.Normal, color = if (isArtist) Color.White else Color.White.copy(alpha = 0.4f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Login card
            Column(
                modifier = Modifier.fillMaxWidth().offset(y = cardSlide.value.dp).alpha(cardAlpha.value)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF1A0E35), Color(0xFF110A22))))
                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Text(if (isArtist) "Artist Login" else "Client Login", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(if (isArtist) "Sign in to manage your gigs" else "Sign in to find instrumentalists", fontSize = 13.sp, color = Color.White.copy(alpha = 0.4f), modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(24.dp))

                // Firebase error banner
                if (firebaseError.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.12f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(firebaseError, fontSize = 12.sp, color = Color(0xFFEF4444))
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Email
                OutlinedTextField(
                    value = email, onValueChange = { email = it; emailError = ""; firebaseError = "" },
                    label = { Text("Email address", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF8B5CF6)) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError.isNotEmpty(),
                    supportingText = { if (emailError.isNotEmpty()) Text(emailError, color = Color(0xFFEF4444), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8B5CF6), unfocusedBorderColor = Color.White.copy(alpha = 0.15f), cursorColor = Color(0xFF8B5CF6), focusedLabelColor = Color(0xFF8B5CF6), errorBorderColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(14.dp))

                // Password
                OutlinedTextField(
                    value = password, onValueChange = { password = it; passwordError = ""; firebaseError = "" },
                    label = { Text("Password", color = Color.White.copy(alpha = 0.4f)) },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF8B5CF6)) },
                    trailingIcon = { IconButton(onClick = { showPassword = !showPassword }) { Icon(if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color.White.copy(alpha = 0.4f)) } },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError.isNotEmpty(),
                    supportingText = { if (passwordError.isNotEmpty()) Text(passwordError, color = Color(0xFFEF4444), fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF8B5CF6), unfocusedBorderColor = Color.White.copy(alpha = 0.15f), cursorColor = Color(0xFF8B5CF6), focusedLabelColor = Color(0xFF8B5CF6), errorBorderColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(14.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        if (email.isNotBlank() && email.contains("@")) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        } else {
                            emailError = "Enter your email first"
                        }
                    }) {
                        Text("Forgot password?", fontSize = 12.sp, color = Color(0xFF8B5CF6).copy(alpha = 0.8f))
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Sign In button — Firebase Auth wired
                Button(
                    onClick = {
                        var valid = true
                        if (email.isBlank()) { emailError = "Email is required"; valid = false }
                        else if (!email.contains("@")) { emailError = "Enter a valid email"; valid = false }
                        if (password.isBlank()) { passwordError = "Password is required"; valid = false }
                        else if (password.length < 6) { passwordError = "At least 6 characters"; valid = false }
                        if (valid) {
                            isLoading = true
                            firebaseError = ""
                            // ── FIREBASE AUTH ─────────────────────
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)
                                .addOnSuccessListener {
                                    isLoading = false
                                    navController.navigate(ROUT_HOME) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    firebaseError = when {
                                        e.message?.contains("password") == true -> "Incorrect password"
                                        e.message?.contains("user") == true     -> "No account found with this email"
                                        e.message?.contains("network") == true  -> "No internet connection"
                                        else -> e.message ?: "Sign in failed"
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp).scale(btnScale),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF9F3A3A), Color(0xFF8B5CF6))), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
                        else Text("Sign In  →", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Equalizer divider
            Box(modifier = Modifier.alpha(cardAlpha.value).height(20.dp).width(100.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val count = 9; val gap = size.width / (count * 2f - 1); val bw = gap * 0.7f
                    for (i in 0 until count) {
                        val bh = (0.3f + 0.7f * abs(sin(waveAnim + i * 0.7f))) * size.height
                        drawRoundRect(Color(0xFF8B5CF6).copy(alpha = 0.5f), Offset(i * gap * 2f, size.height - bh), Size(bw, bh), CornerRadius(bw / 2f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.alpha(cardAlpha.value), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?  ", fontSize = 14.sp, color = Color.White.copy(alpha = 0.4f))
                Text("Sign Up", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6), modifier = Modifier.clickable { navController.navigate(ROUT_REGISTER) })
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    InstruxaTheme { LoginScreen(navController = rememberNavController()) }
}