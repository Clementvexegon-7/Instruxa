package com.clementvexegon.instruxa.ui.screens.booking

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
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_HOME
import com.clementvexegon.instruxa.navigation.ROUT_REVIEWS
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlinx.coroutines.delay
import kotlin.math.*

// ─────────────────────────────────────────
//  INSTRUXA — Booking Confirm Screen
//
//  The most cinematic screen in the app.
//  This is the moment everything becomes REAL.
//
//  What makes it legendary:
//  • Holographic concert ticket materializes
//    from particles — drawn entirely in Canvas
//  • Live countdown timer (HH:MM:SS) to session
//  • Animated dashed border traces itself around
//    the ticket like a pen drawing it
//  • QR code drawn geometrically with Canvas
//  • Perforated tear line separates sections
//  • Status pipeline animates step by step:
//    Confirmed → M-Pesa Sent → Artist Notified → Ready
//  • Soundwave animation pulses at the bottom
//  • Share / Calendar / Message action buttons
//  • Floating particles in session accent color
//  • Ticket glows and breathes with pulse animation
//  • "Add to contacts" for the artist
// ─────────────────────────────────────────

@Composable
fun BookingConfirmScreen(navController: NavController) {

    // ── Ticket entry animation ─────────────
    val ticketScale = remember { Animatable(0f) }
    val ticketAlpha = remember { Animatable(0f) }
    val statusStep  = remember { Animatable(0f) }
    var showActions by remember { mutableStateOf(false) }

    // Live countdown — 2 days 14 hrs to session
    var hours   by remember { mutableIntStateOf(14) }
    var minutes by remember { mutableIntStateOf(32) }
    var seconds by remember { mutableIntStateOf(47) }

    LaunchedEffect(Unit) {
        // Ticket materializes with spring bounce
        ticketScale.animateTo(1f, spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ))
        ticketAlpha.animateTo(1f, tween(400))
        delay(300)

        // Status steps animate one by one
        for (step in 1..4) {
            delay(600)
            statusStep.animateTo(step.toFloat(), tween(400, easing = FastOutSlowInEasing))
        }

        delay(200)
        showActions = true
    }

    // Countdown timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds--
            if (seconds < 0) { seconds = 59; minutes-- }
            if (minutes < 0) { minutes = 59; hours-- }
            if (hours < 0)   { hours = 0; minutes = 0; seconds = 0 }
        }
    }

    // ── Infinite background animations ─────
    val inf = rememberInfiniteTransition(label = "confirm")
    val glowPulse by inf.animateFloat(0.3f,1f, infiniteRepeatable(tween(2200,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(5000,easing=LinearEasing),RepeatMode.Restart),label="p")
    val dashAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(2000,easing=LinearEasing),RepeatMode.Restart),label="d")
    val waveAnim by inf.animateFloat(0f,(2f*PI).toFloat(), infiniteRepeatable(tween(900,easing=LinearEasing),RepeatMode.Restart),label="w")
    val ticketGlow by inf.animateFloat(0.4f,0.9f, infiniteRepeatable(tween(1800,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="tg")
    val holoPulse by inf.animateFloat(0f,1f, infiniteRepeatable(tween(3000,easing=LinearEasing),RepeatMode.Restart),label="h")

    val pSeeds = remember {
        listOf(
            Offset(0.05f,0.1f), Offset(0.95f,0.08f),
            Offset(0.02f,0.55f), Offset(0.98f,0.45f),
            Offset(0.5f,0.03f), Offset(0.18f,0.92f),
            Offset(0.82f,0.88f), Offset(0.35f,0.08f),
            Offset(0.65f,0.05f), Offset(0.88f,0.25f)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050410))) {

        // ── BACKGROUND CANVAS ─────────────
        Canvas(Modifier.fillMaxSize()) {
            // Deep space gradient
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF1E0A40).copy(alpha=glowPulse*0.8f), Color.Transparent),
                    center = Offset(size.width*0.5f, size.height*0.4f),
                    radius = size.minDimension*0.9f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF2A0A0A).copy(alpha=0.5f), Color.Transparent),
                    center = Offset(size.width*0.8f, size.height*0.85f),
                    radius = size.minDimension*0.55f
                )
            )
            // Holographic shimmer ring
            drawCircle(
                color = Color(0xFF22D3EE).copy(alpha=(sin(holoPulse*2f*PI.toFloat())+1f)*0.04f),
                radius = size.minDimension*0.65f,
                center = Offset(size.width*0.5f, size.height*0.38f),
                style = Stroke(1.dp.toPx())
            )
            // Grid
            val sp = 52.dp.toPx()
            for (c in 0..(size.width/sp).toInt()+2)
                for (r in 0..(size.height/sp).toInt()+2)
                    drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f), 1.dp.toPx(), Offset(c*sp,r*sp))
            // Particles
            pSeeds.forEachIndexed { i,pos ->
                val prog = (particleAnim + i*(1f/pSeeds.size)) % 1f
                val x = pos.x*size.width + sin(prog*2f*PI.toFloat()+i.toFloat())*16.dp.toPx()
                val y = size.height*(1f-prog)
                val a = sin(prog*PI.toFloat()).coerceIn(0f,1f)*0.5f
                drawCircle(
                    color = when(i%3){0->Color(0xFF8B5CF6).copy(alpha=a) 1->Color(0xFF22D3EE).copy(alpha=a*0.7f) else->Color(0xFF10B981).copy(alpha=a*0.6f)},
                    radius=(1.5f+i%3).dp.toPx(), center=Offset(x,y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(52.dp))

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha=0.08f))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint=Color.White, modifier=Modifier.size(20.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Booking Confirmed", fontSize=18.sp, fontWeight=FontWeight.Bold, color=Color.White)
                    Text("Session locked in ✓", fontSize=12.sp, color=Color(0xFF10B981))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981).copy(alpha=0.15f))
                        .padding(horizontal=10.dp, vertical=5.dp)
                ) {
                    Text("#IN-2847", fontSize=11.sp, color=Color(0xFF10B981), fontWeight=FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(28.dp))

            // ══════════════════════════════
            //  THE HOLOGRAPHIC TICKET
            // ══════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(ticketScale.value)
                    .alpha(ticketAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Canvas(modifier = Modifier.fillMaxWidth().height(480.dp)) {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF8B5CF6).copy(alpha=ticketGlow*0.3f),
                                Color(0xFF22D3EE).copy(alpha=ticketGlow*0.2f),
                                Color(0xFF9F3A3A).copy(alpha=ticketGlow*0.25f)
                            )
                        ),
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        style = Stroke(width=2.dp.toPx())
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A0E35), Color(0xFF0E0820), Color(0xFF1A1020))
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(Color(0xFF8B5CF6).copy(alpha=0.5f), Color(0xFF22D3EE).copy(alpha=0.3f), Color(0xFF9F3A3A).copy(alpha=0.4f))),
                            RoundedCornerShape(22.dp)
                        )
                ) {
                    // ── TICKET TOP: Holographic header ──
                    Box(
                        modifier = Modifier.fillMaxWidth().height(130.dp)
                    ) {
                        Canvas(Modifier.fillMaxSize()) {
                            // Holographic background
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2D1060),
                                        Color(0xFF1A0838),
                                        Color(0xFF2A0A1A)
                                    )
                                ),
                                topLeft = Offset.Zero,
                                size = Size(size.width, size.height),
                                cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx())
                            )
                            // Holographic shimmer diagonal lines
                            val shimmerAlpha = (sin(holoPulse*2f*PI.toFloat())+1f)*0.04f + 0.02f
                            for (i in 0..12) {
                                val x = i*(size.width/12f)
                                drawLine(
                                    color = Color(0xFF22D3EE).copy(alpha=shimmerAlpha),
                                    start = Offset(x-size.height, 0f),
                                    end   = Offset(x, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            // Sound wave decoration
                            val waveY = size.height * 0.75f
                            val wPoints = 40
                            for (i in 0..wPoints) {
                                val wx = i * (size.width / wPoints)
                                val wy = waveY + sin(waveAnim + i * 0.4f) * 8.dp.toPx()
                                drawCircle(
                                    color = Color(0xFF8B5CF6).copy(alpha=0.35f),
                                    radius = 1.dp.toPx(),
                                    center = Offset(wx, wy)
                                )
                            }
                        }

                        // Artist info overlay
                        Row(
                            modifier = Modifier.fillMaxSize().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(64.dp).clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9))))
                                    .border(2.dp, Color.White.copy(alpha=0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("KO", fontSize=22.sp, fontWeight=FontWeight.Bold, color=Color.White)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Kevin Omondi", fontSize=20.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-0.3).sp)
                                Text("Guitarist · Pianist", fontSize=12.sp, color=Color(0xFF8B5CF6))
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Canvas(Modifier.size(7.dp)) {
                                        drawCircle(Color(0xFF10B981).copy(alpha=0.4f), size.minDimension*0.7f)
                                        drawCircle(Color(0xFF10B981), size.minDimension*0.38f)
                                    }
                                    Spacer(Modifier.width(5.dp))
                                    Text("Live Event · 2 hrs", fontSize=11.sp, color=Color(0xFF10B981))
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("KSh 1,650", fontSize=18.sp, fontWeight=FontWeight.Bold, color=Color.White)
                                Text("incl. fee", fontSize=10.sp, color=Color.White.copy(alpha=0.3f))
                            }
                        }
                    }

                    // ── PERFORATED TEAR LINE ──────────
                    Box(
                        modifier = Modifier.fillMaxWidth().height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(Modifier.fillMaxSize()) {
                            // Left semicircle notch
                            drawArc(
                                color = Color(0xFF050410),
                                startAngle = -90f,
                                sweepAngle = 180f,
                                useCenter = true,
                                topLeft = Offset(-12.dp.toPx(), size.height/2f - 12.dp.toPx()),
                                size = Size(24.dp.toPx(), 24.dp.toPx())
                            )
                            // Right semicircle notch
                            drawArc(
                                color = Color(0xFF050410),
                                startAngle = 90f,
                                sweepAngle = 180f,
                                useCenter = true,
                                topLeft = Offset(size.width - 12.dp.toPx(), size.height/2f - 12.dp.toPx()),
                                size = Size(24.dp.toPx(), 24.dp.toPx())
                            )
                            // Dashed perforation line
                            val dashW = 8.dp.toPx()
                            val gapW  = 5.dp.toPx()
                            val totalW = dashW + gapW
                            val count = (size.width / totalW).toInt()
                            val dashOffset = (dashAnim * totalW)
                            for (i in -1..count+1) {
                                val startX = i*totalW + dashOffset
                                drawLine(
                                    color = Color.White.copy(alpha=0.18f),
                                    start = Offset(startX, size.height/2f),
                                    end   = Offset((startX+dashW).coerceAtMost(size.width), size.height/2f),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }
                        }
                    }

                    // ── TICKET BODY ───────────────────
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {

                        // Session details row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TicketField("DATE", "Tue, May 6")
                            TicketField("TIME", "11:00 AM")
                            TicketField("DURATION", "2 Hours")
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TicketField("TYPE", "Live Event")
                            TicketField("LOCATION", "Nairobi CBD")
                            TicketField("M-PESA REF", "QFZ2847AB")
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── QR CODE (Canvas drawn) ────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // QR geometric pattern
                            Box(
                                modifier = Modifier.size(90.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0E0820))
                                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha=0.3f), RoundedCornerShape(10.dp))
                            ) {
                                Canvas(Modifier.fillMaxSize().padding(8.dp)) {
                                    val cellSize = size.width / 9f
                                    // QR pattern (fixed decorative cells)
                                    val pattern = listOf(
                                        Pair(0,0),Pair(1,0),Pair(2,0),Pair(0,1),Pair(2,1),Pair(0,2),Pair(1,2),Pair(2,2),
                                        Pair(6,0),Pair(7,0),Pair(8,0),Pair(6,1),Pair(8,1),Pair(6,2),Pair(7,2),Pair(8,2),
                                        Pair(0,6),Pair(1,6),Pair(2,6),Pair(0,7),Pair(2,7),Pair(0,8),Pair(1,8),Pair(2,8),
                                        Pair(4,4),Pair(3,3),Pair(5,5),Pair(4,3),Pair(3,4),Pair(5,4),Pair(4,5),Pair(3,5),Pair(5,3),
                                        Pair(7,4),Pair(7,5),Pair(4,7),Pair(5,7),Pair(6,4),Pair(8,5),Pair(7,6),Pair(3,7),Pair(6,6)
                                    )
                                    pattern.forEach { (col,row) ->
                                        drawRect(
                                            color = Color(0xFF8B5CF6),
                                            topLeft = Offset(col*cellSize, row*cellSize),
                                            size = Size(cellSize*0.85f, cellSize*0.85f)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("BOOKING REFERENCE", fontSize=9.sp, color=Color.White.copy(alpha=0.3f), letterSpacing=1.sp)
                                Text("#IN-2847-KO", fontSize=17.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-0.3).sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Show this code on the day of your session. Valid for 1 entry.", fontSize=10.sp, color=Color.White.copy(alpha=0.3f), lineHeight=14.sp)
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF10B981).copy(alpha=0.15f))
                                        .padding(horizontal=10.dp, vertical=5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, null, tint=Color(0xFF10B981), modifier=Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Verified & Secured", fontSize=10.sp, color=Color(0xFF10B981), fontWeight=FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── COUNTDOWN TIMER ───────────────
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1A0838),Color(0xFF1A0820))))
                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha=0.2f), RoundedCornerShape(18.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SESSION STARTS IN", fontSize=10.sp, color=Color.White.copy(alpha=0.35f), letterSpacing=2.sp)
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownUnit(value = "02", label = "DAYS")
                    Text(":", fontSize=28.sp, fontWeight=FontWeight.Bold, color=Color(0xFF8B5CF6))
                    CountdownUnit(value = "%02d".format(hours), label = "HRS")
                    Text(":", fontSize=28.sp, fontWeight=FontWeight.Bold, color=Color(0xFF8B5CF6))
                    CountdownUnit(value = "%02d".format(minutes), label = "MIN")
                    Text(":", fontSize=28.sp, fontWeight=FontWeight.Bold, color=Color(0xFF8B5CF6))
                    CountdownUnit(value = "%02d".format(seconds), label = "SEC")
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── STATUS PIPELINE ───────────────
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF130A28))
                    .border(1.dp, Color.White.copy(alpha=0.06f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Text("Session Status", fontSize=14.sp, fontWeight=FontWeight.Bold, color=Color.White)
                Spacer(Modifier.height(16.dp))

                val steps = listOf(
                    Triple("Booking Created", "Just now", Color(0xFF8B5CF6)),
                    Triple("M-Pesa Payment Sent", "Processing", Color(0xFF10B981)),
                    Triple("Artist Notified", "Kevin informed", Color(0xFF22D3EE)),
                    Triple("Session Confirmed", "Ready to go!", Color(0xFFF59E0B))
                )

                steps.forEachIndexed { i, (title, sub, color) ->
                    val isActive   = statusStep.value > i
                    val isCurrent  = statusStep.value.toInt() == i + 1

                    Row(verticalAlignment = Alignment.Top) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(32.dp)
                                    .clip(CircleShape)
                                    .background(if(isActive) color.copy(alpha=0.2f) else Color(0xFF1A0E35))
                                    .border(1.5.dp, if(isActive) color else Color.White.copy(alpha=0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isActive) {
                                    Icon(Icons.Default.Check, null, tint=color, modifier=Modifier.size(16.dp))
                                } else {
                                    Text("${i+1}", fontSize=11.sp, color=Color.White.copy(alpha=0.3f), fontWeight=FontWeight.Bold)
                                }
                            }
                            if (i < steps.size-1) {
                                Box(modifier = Modifier.width(2.dp).height(28.dp)
                                    .background(if(isActive) color.copy(alpha=0.4f) else Color.White.copy(alpha=0.06f)))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.padding(top=6.dp)) {
                            Text(title, fontSize=13.sp, fontWeight=if(isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color=if(isActive) Color.White else Color.White.copy(alpha=0.3f))
                            Text(sub, fontSize=11.sp, color=if(isActive) color else Color.White.copy(alpha=0.2f))
                        }
                    }

                    if (i < steps.size-1) Spacer(Modifier.height(4.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── ACTION BUTTONS ─────────────────
            if (showActions) {
                // Primary: Message Artist
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f).height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1A0838),Color(0xFF110A22))))
                            .border(1.dp, Color(0xFF8B5CF6).copy(alpha=0.3f), RoundedCornerShape(14.dp))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, null, tint=Color(0xFF8B5CF6), modifier=Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Message", fontSize=13.sp, color=Color.White, fontWeight=FontWeight.Medium)
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f).height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1A0838),Color(0xFF110A22))))
                            .border(1.dp, Color(0xFF22D3EE).copy(alpha=0.3f), RoundedCornerShape(14.dp))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, null, tint=Color(0xFF22D3EE), modifier=Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Calendar", fontSize=13.sp, color=Color.White, fontWeight=FontWeight.Medium)
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f).height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1A0838),Color(0xFF110A22))))
                            .border(1.dp, Color(0xFF10B981).copy(alpha=0.3f), RoundedCornerShape(14.dp))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, null, tint=Color(0xFF10B981), modifier=Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share", fontSize=13.sp, color=Color.White, fontWeight=FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Cancel booking (destructive)
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEF4444).copy(alpha=0.06f))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha=0.2f), RoundedCornerShape(14.dp))
                        .clickable {}
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Cancel, null, tint=Color(0xFFEF4444).copy(alpha=0.7f), modifier=Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cancel Booking", fontSize=13.sp, color=Color(0xFFEF4444).copy(alpha=0.7f))
                }

                Spacer(Modifier.height(16.dp))

                // Main CTA: Rate after session
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6),Color(0xFF22D3EE).copy(alpha=0.7f))))
                        .clickable { navController.navigate(ROUT_REVIEWS) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint=Color.White, modifier=Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Rate After Session", fontSize=15.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=0.3.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Back to Home",
                    fontSize=13.sp, color=Color.White.copy(alpha=0.3f),
                    textAlign=TextAlign.Center,
                    modifier=Modifier.fillMaxWidth().clickable {
                        navController.navigate(ROUT_HOME) { popUpTo(0){inclusive=true} }
                    }
                )
            }

            // ── BOTTOM SOUNDWAVE ──────────────
            Spacer(Modifier.height(20.dp))
            Box(modifier=Modifier.fillMaxWidth().height(28.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val count = 28
                    val bw = size.width / (count*2f)
                    for (i in 0 until count) {
                        val h = (0.2f+0.8f* abs(sin(waveAnim+i*0.45f)))*size.height
                        drawRoundRect(
                            color = Color(0xFF8B5CF6).copy(alpha=0.3f),
                            topLeft = Offset(i*bw*2f, size.height-h),
                            size = Size(bw*0.7f, h),
                            cornerRadius = CornerRadius(bw/2f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Instruxa · Booking #IN-2847", fontSize=10.sp, color=Color.White.copy(alpha=0.1f),
                textAlign=TextAlign.Center, letterSpacing=1.sp, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Helper: Ticket field ──────────────────
@Composable
fun TicketField(label: String, value: String) {
    Column {
        Text(label, fontSize=9.sp, color=Color.White.copy(alpha=0.3f), letterSpacing=1.sp)
        Text(value, fontSize=13.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-0.2).sp)
    }
}

// ── Helper: Countdown unit ────────────────
@Composable
fun CountdownUnit(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width=56.dp, height=52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A0E35))
                .border(1.dp, Color(0xFF8B5CF6).copy(alpha=0.25f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(value, fontSize=24.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-0.5).sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize=9.sp, color=Color.White.copy(alpha=0.3f), letterSpacing=1.sp)
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun BookingConfirmScreenPreview() {
    InstruxaTheme { BookingConfirmScreen(navController = rememberNavController()) }
}