package com.clementvexegon.instruxa.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_HOME
import com.clementvexegon.instruxa.navigation.ROUT_ONBOARDING
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.math.*

// ─────────────────────────────────────────
//  INSTRUXA — Splash Screen
//
//  CRITICAL FIX: Checks Firebase Auth state
//  on every launch. If user is already logged
//  in → go straight to Home (no login loop).
//  If not logged in → go to Onboarding.
//
//  This is why you were seeing login every time.
//  The app was always going to Onboarding
//  without checking if you were already signed in.
// ─────────────────────────────────────────

@Composable
fun SplashScreen(navController: NavController) {

    val logoScale    = remember { Animatable(0f) }
    val nameAlpha    = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            1f, spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow
            )
        )
        nameAlpha.animateTo(1f, tween(500))
        taglineAlpha.animateTo(1f, tween(400))
        delay(1800)

        // ── FIREBASE AUTH CHECK ───────────────────────
        // This is the fix for the login loop.
        // If currentUser exists → already logged in → Home
        // If currentUser is null → not logged in → Onboarding
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is already signed in — skip login entirely
            navController.navigate(ROUT_HOME) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            // User is not signed in — show onboarding/login
            navController.navigate(ROUT_ONBOARDING) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val inf = rememberInfiniteTransition(label = "splash")
    val glowPulse  by inf.animateFloat(0.4f,1f, infiniteRepeatable(tween(2000,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val ring1      by inf.animateFloat(0f,1f, infiniteRepeatable(tween(2200,easing=LinearEasing),RepeatMode.Restart),label="r1")
    val ring2      by inf.animateFloat(0f,1f, infiniteRepeatable(tween(2200,delayMillis=700,easing=LinearEasing),RepeatMode.Restart),label="r2")
    val ring3      by inf.animateFloat(0f,1f, infiniteRepeatable(tween(2200,delayMillis=1400,easing=LinearEasing),RepeatMode.Restart),label="r3")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(5000,easing=LinearEasing),RepeatMode.Restart),label="p")
    val waveAnim   by inf.animateFloat(0f,(2f*PI).toFloat(), infiniteRepeatable(tween(1000,easing=LinearEasing),RepeatMode.Restart),label="w")

    val pSeeds = remember {
        listOf(Offset(0.12f,0.18f),Offset(0.82f,0.12f),Offset(0.22f,0.72f),
            Offset(0.72f,0.78f),Offset(0.50f,0.08f),Offset(0.91f,0.42f),
            Offset(0.08f,0.52f),Offset(0.62f,0.28f),Offset(0.38f,0.88f),
            Offset(0.88f,0.62f),Offset(0.44f,0.44f),Offset(0.18f,0.38f))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            // Animated background glow
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.75f),Color.Transparent),
                    center = Offset(size.width*0.5f, size.height*0.4f),
                    radius = size.minDimension*0.9f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF3A0A0A).copy(alpha=0.4f),Color.Transparent),
                    center = Offset(size.width*0.85f, size.height*0.82f),
                    radius = size.minDimension*0.55f
                )
            )
            // Grid dots
            val sp = 38.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.08f),1.5.dp.toPx(),Offset(c*sp,r*sp))
            // Pulsing rings
            val rc = Offset(size.width/2f, size.height*0.42f)
            listOf(ring1,ring2,ring3).forEach { p ->
                drawCircle(Color(0xFF8B5CF6).copy(alpha=(1f-p)*0.4f),p*size.minDimension*0.45f+50.dp.toPx(),rc,style=Stroke(1.5.dp.toPx()))
            }
            // Floating particles
            pSeeds.forEachIndexed { i,pos ->
                val prog=(particleAnim+i*(1f/pSeeds.size))%1f
                val x=pos.x*size.width+sin(prog*2f*PI.toFloat()+i.toFloat())*18.dp.toPx()
                val y=size.height*(1f-prog)
                val a=sin(prog*PI.toFloat()).coerceIn(0f,1f)*0.65f
                drawCircle(
                    color=when(i%3){0->Color(0xFF8B5CF6).copy(alpha=a);1->Color(0xFF22D3EE).copy(alpha=a*0.7f);else->Color(0xFFC46A6A).copy(alpha=a*0.55f)},
                    radius=(1.5f+i%3).dp.toPx(),center=Offset(x,y)
                )
            }
            // Diagonal holographic lines
            val shAlpha = glowPulse * 0.03f
            for(i in 0..10){ val x=i*(size.width/10f); drawLine(Color(0xFF22D3EE).copy(alpha=shAlpha),Offset(x-size.height,0f),Offset(x,size.height),0.8.dp.toPx()) }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with glow
            Box(modifier=Modifier.size(130.dp).scale(logoScale.value), contentAlignment=Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF8B5CF6).copy(alpha=glowPulse*0.55f),Color.Transparent)))
                }
                Box(
                    modifier=Modifier.size(100.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9))))
                        .border(2.dp,Color.White.copy(alpha=0.15f),CircleShape),
                    contentAlignment=Alignment.Center
                ){
                    Text("IX", fontSize=38.sp, fontWeight=FontWeight.Bold, color=Color.White)
                }
            }

            Spacer(Modifier.height(36.dp))

            Text(
                "Instruxa",
                fontSize=46.sp, fontWeight=FontWeight.Bold,
                color=Color.White, letterSpacing=(-1.5).sp,
                modifier=Modifier.alpha(nameAlpha.value)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "HIRE THE SOUND YOU NEED",
                fontSize=11.sp, fontWeight=FontWeight.Normal,
                color=Color.White.copy(alpha=0.45f), letterSpacing=3.sp,
                textAlign=TextAlign.Center,
                modifier=Modifier.alpha(taglineAlpha.value)
            )

            Spacer(Modifier.height(52.dp))

            // Animated equalizer bars
            Box(modifier=Modifier.alpha(taglineAlpha.value).height(28.dp).width(72.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val count=7; val bw=size.width/(count*2f)
                    for(i in 0 until count){
                        val h=(0.25f+0.75f* abs(sin(waveAnim+i*0.9f)))*size.height
                        drawRoundRect(
                            color=if(i in 2..4) Color(0xFF22D3EE).copy(alpha=0.85f) else Color(0xFF8B5CF6).copy(alpha=0.7f),
                            topLeft=Offset(i*bw*2f,size.height-h),
                            size=Size(bw*0.7f,h),
                            cornerRadius=CornerRadius(bw/2f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun SplashScreenPreview() { InstruxaTheme { SplashScreen(rememberNavController()) } }