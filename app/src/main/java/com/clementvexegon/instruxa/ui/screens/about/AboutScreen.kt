package com.clementvexegon.instruxa.ui.screens.about

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.*

// ─────────────────────────────────────────
//  INSTRUXA — About Screen
//
//  This is the cornerstone.
//  The one screen that tells the story.
//  "Designed by Apple in California."
//  "Built in Nairobi by Clement Vexegon."
//
//  This is where the CvX mark lives fully.
//  Not on every screen. Just here. Permanent.
// ─────────────────────────────────────────

@Composable
fun AboutScreen(navController: NavController) {

    val contentAlpha = remember { Animatable(0f) }
    val logoScale    = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
        contentAlpha.animateTo(1f, tween(600))
    }

    val inf = rememberInfiniteTransition(label = "about")
    val glowPulse  by inf.animateFloat(0.4f,1f, infiniteRepeatable(tween(2000,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val ring1      by inf.animateFloat(0f,1f, infiniteRepeatable(tween(3000,easing=LinearEasing),RepeatMode.Restart),label="r1")
    val ring2      by inf.animateFloat(0f,1f, infiniteRepeatable(tween(3000,delayMillis=1000,easing=LinearEasing),RepeatMode.Restart),label="r2")
    val ring3      by inf.animateFloat(0f,1f, infiniteRepeatable(tween(3000,delayMillis=2000,easing=LinearEasing),RepeatMode.Restart),label="r3")
    val holoPulse  by inf.animateFloat(0f,1f, infiniteRepeatable(tween(4000,easing=LinearEasing),RepeatMode.Restart),label="h")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(6000,easing=LinearEasing),RepeatMode.Restart),label="p")

    val pSeeds = remember {
        listOf(Offset(0.08f,0.2f),Offset(0.92f,0.15f),Offset(0.05f,0.6f),Offset(0.95f,0.55f),
            Offset(0.5f,0.05f),Offset(0.2f,0.85f),Offset(0.8f,0.8f),Offset(0.55f,0.92f))
    }

    Box(modifier=Modifier.fillMaxSize().background(Color(0xFF050410))) {

        Canvas(Modifier.fillMaxSize()) {
            // Deep cosmic background
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.75f),Color.Transparent),Offset(size.width*0.5f,size.height*0.42f),size.minDimension*0.85f))
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF3A0A0A).copy(alpha=0.4f),Color.Transparent),Offset(size.width*0.1f,size.height*0.9f),size.minDimension*0.55f))
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF0A1A2A).copy(alpha=0.5f),Color.Transparent),Offset(size.width*0.95f,size.height*0.1f),size.minDimension*0.45f))
            val sp=54.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f),1.dp.toPx(),Offset(c*sp,r*sp))
            // Pulsing rings
            val rc=Offset(size.width/2f,size.height*0.38f)
            listOf(ring1,ring2,ring3).forEach { p ->
                drawCircle(Color(0xFF8B5CF6).copy(alpha=(1f-p)*0.25f),p*size.minDimension*0.42f+60.dp.toPx(),rc,style=Stroke(1.dp.toPx()))
            }
            // Holo shimmer diagonal
            val shAlpha=(sin(holoPulse*2f*PI.toFloat())+1f)*0.025f
            for(i in 0..14){ val x=i*(size.width/14f); drawLine(Color(0xFF22D3EE).copy(alpha=shAlpha),Offset(x-size.height,0f),Offset(x,size.height),0.8.dp.toPx()) }
            // Particles
            pSeeds.forEachIndexed { i,pos ->
                val prog=(particleAnim+i*(1f/pSeeds.size))%1f
                val x=pos.x*size.width+sin(prog*2f*PI.toFloat()+i.toFloat())*18.dp.toPx()
                val y=size.height*(1f-prog)
                val a=sin(prog*PI.toFloat()).coerceIn(0f,1f)*0.5f
                drawCircle(when(i%3){0->Color(0xFF8B5CF6).copy(alpha=a);1->Color(0xFF22D3EE).copy(alpha=a*0.7f);else->Color(0xFFC46A6A).copy(alpha=a*0.5f)},(1.5f+i%3).dp.toPx(),Offset(x,y))
            }
        }

        Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).alpha(contentAlpha.value), horizontalAlignment=Alignment.CenterHorizontally) {

            // Back button
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=52.dp)){
                Box(modifier=Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(alpha=0.08f)).clickable{navController.popBackStack()},contentAlignment=Alignment.Center){
                    Icon(Icons.Default.ArrowBack,null,tint=Color.White,modifier=Modifier.size(20.dp))
                }
            }

            // App logo — large, with glow
            Box(modifier=Modifier.size(140.dp).scale(logoScale.value), contentAlignment=Alignment.Center) {
                Canvas(Modifier.fillMaxSize()){
                    drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF8B5CF6).copy(alpha=glowPulse*0.5f),Color.Transparent)))
                }
                Box(
                    modifier=Modifier.size(110.dp).clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9))))
                        .border(1.5.dp,Color.White.copy(alpha=0.15f),RoundedCornerShape(28.dp)),
                    contentAlignment=Alignment.Center
                ){
                    Column(horizontalAlignment=Alignment.CenterHorizontally){
                        Text("IX", fontSize=40.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-1).sp)
                        Text("INSTRUXA", fontSize=8.sp, color=Color.White.copy(alpha=0.55f), letterSpacing=3.sp)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text("Instruxa", fontSize=36.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-1).sp)
            Text("Version 1.0.0", fontSize=13.sp, color=Color.White.copy(alpha=0.35f), modifier=Modifier.padding(top=4.dp))

            Spacer(Modifier.height(32.dp))

            // Mission
            Column(modifier=Modifier.fillMaxWidth().padding(horizontal=24.dp).clip(RoundedCornerShape(20.dp)).background(Brush.verticalGradient(listOf(Color(0xFF1A0E35),Color(0xFF110A22)))).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.18f),RoundedCornerShape(20.dp)).padding(22.dp)) {
                Text("Our Mission", fontSize=16.sp, fontWeight=FontWeight.Bold, color=Color.White)
                Spacer(Modifier.height(10.dp))
                Text("Instruxa was built to bridge the gap between talented instrumentalists and the people who need them.\n\nEvery musician deserves to be discovered. Every event deserves the perfect sound.", fontSize=14.sp, color=Color.White.copy(alpha=0.58f), lineHeight=22.sp)
            }

            Spacer(Modifier.height(16.dp))

            // Stats
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=24.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                listOf(Triple("47+","Artists",Color(0xFF8B5CF6)), Triple("1","City",Color(0xFF22D3EE)), Triple("v1.0","Build",Color(0xFF10B981))).forEach { (v,l,c) ->
                    Box(modifier=Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,c.copy(alpha=0.18f),RoundedCornerShape(14.dp)).padding(14.dp)){
                        Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth()){
                            Text(v,fontSize=18.sp,fontWeight=FontWeight.Bold,color=c)
                            Text(l,fontSize=10.sp,color=Color.White.copy(alpha=0.3f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tech stack
            Column(modifier=Modifier.fillMaxWidth().padding(horizontal=24.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF130A28)).border(1.dp,Color.White.copy(alpha=0.06f),RoundedCornerShape(20.dp)).padding(20.dp)) {
                Text("Built with", fontSize=14.sp, fontWeight=FontWeight.Bold, color=Color.White, modifier=Modifier.padding(bottom=12.dp))
                listOf(
                    Triple("⚡","Kotlin + Jetpack Compose","Native Android development"),
                    Triple("🔥","Firebase","Auth, Firestore, Storage"),
                    Triple("☁️","Cloudinary","Portfolio image hosting"),
                    Triple("💳","M-Pesa Daraja","Secure local payments")
                ).forEach { (emoji,name,desc) ->
                    Row(modifier=Modifier.fillMaxWidth().padding(vertical=6.dp), verticalAlignment=Alignment.CenterVertically) {
                        Text(emoji,fontSize=18.sp,modifier=Modifier.width(32.dp))
                        Column {
                            Text(name,fontSize=13.sp,fontWeight=FontWeight.Medium,color=Color.White)
                            Text(desc,fontSize=11.sp,color=Color.White.copy(alpha=0.35f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── THE CvX CORNERSTONE ───────────────────────
            // This is where the builder signs the work.
            // One screen. One place. Permanent.
            // Like "Designed by Apple in California."
            // Like Banksy's tag on a wall.
            // The work speaks — but the name endures.
            // ─────────────────────────────────────────────
            Column(
                modifier=Modifier.fillMaxWidth().padding(horizontal=24.dp).clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF1A0838),Color(0xFF1A0A1A))))
                    .border(1.dp, Brush.linearGradient(listOf(Color(0xFF9F3A3A).copy(alpha=0.4f),Color(0xFF6D28D9).copy(alpha=0.4f))),RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment=Alignment.CenterHorizontally
            ) {
                // CvX monogram
                Box(
                    modifier=Modifier.size(56.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9))))
                        .border(1.dp,Color.White.copy(alpha=0.15f),CircleShape),
                    contentAlignment=Alignment.Center
                ){
                    Text("CX", fontSize=18.sp, fontWeight=FontWeight.Bold, color=Color.White)
                }

                Spacer(Modifier.height(14.dp))

                Text("Designed & Built in Nairobi", fontSize=12.sp, color=Color.White.copy(alpha=0.35f), letterSpacing=0.5.sp)
                Spacer(Modifier.height(6.dp))
                Text("Clement Vexegon", fontSize=20.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-0.3).sp)
                Spacer(Modifier.height(8.dp))
                Text("Full Stack Developer · eMobilis · Class of 2026", fontSize=11.sp, color=Color.White.copy(alpha=0.3f), textAlign=TextAlign.Center)

                Spacer(Modifier.height(18.dp))

                // Divider
                Box(Modifier.fillMaxWidth(0.6f).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent,Color.White.copy(alpha=0.1f),Color.Transparent))))

                Spacer(Modifier.height(18.dp))

                // The three principles — his holy trinity
                Text("\"Humankind cannot gain anything without first\ngiving something in return.\"", fontSize=11.sp, color=Color.White.copy(alpha=0.22f), textAlign=TextAlign.Center, lineHeight=17.sp, fontStyle=androidx.compose.ui.text.font.FontStyle.Italic)
                Text("— Alphonse Elric", fontSize=10.sp, color=Color.White.copy(alpha=0.15f), modifier=Modifier.padding(top=6.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text("Instruxa v1.0.0 · © 2026 Clement Vexegon\nAll rights reserved.", fontSize=10.sp, color=Color.White.copy(alpha=0.12f), textAlign=TextAlign.Center, lineHeight=16.sp)

            Spacer(Modifier.height(50.dp))
        }
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun AboutScreenPreview() { InstruxaTheme { AboutScreen(rememberNavController()) } }