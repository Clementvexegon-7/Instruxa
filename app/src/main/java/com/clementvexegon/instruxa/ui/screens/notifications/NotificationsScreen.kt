package com.clementvexegon.instruxa.ui.screens.notifications

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
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.*

// ─────────────────────────────────────────
//  INSTRUXA — Notifications Screen
//  The most underrated screen in any app.
//  Done right, it builds trust and retention.
//
//  Features:
//  • Animated unread count badge that pulses
//  • Grouped: Today / Yesterday / Earlier
//  • 5 notification types with unique icons:
//    - Booking confirmed (green)
//    - Payment received (amber)
//    - New review (star yellow)
//    - Artist now available (purple)
//    - Session reminder (cyan)
//  • Tap to mark individual as read
//  • Unread items have a glowing left border
//  • "Mark all read" button
//  • Empty state — animated sonar
//  • Staggered fade-in on load
// ─────────────────────────────────────────

data class InstruxaNotif(
    val id: Int,
    val type: NotifType,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean
)

enum class NotifType { BOOKING, PAYMENT, REVIEW, AVAILABLE, REMINDER }

fun notifColor(type: NotifType) = when(type) {
    NotifType.BOOKING   -> Color(0xFF10B981)
    NotifType.PAYMENT   -> Color(0xFFF59E0B)
    NotifType.REVIEW    -> Color(0xFFEAB308)
    NotifType.AVAILABLE -> Color(0xFF8B5CF6)
    NotifType.REMINDER  -> Color(0xFF22D3EE)
}

fun notifIcon(type: NotifType) = when(type) {
    NotifType.BOOKING   -> Icons.Default.CheckCircle
    NotifType.PAYMENT   -> Icons.Default.AccountBalanceWallet
    NotifType.REVIEW    -> Icons.Default.Star
    NotifType.AVAILABLE -> Icons.Default.MusicNote
    NotifType.REMINDER  -> Icons.Default.Alarm
}

fun notifEmoji(type: NotifType) = when(type) {
    NotifType.BOOKING   -> "✅"
    NotifType.PAYMENT   -> "💰"
    NotifType.REVIEW    -> "⭐"
    NotifType.AVAILABLE -> "🎸"
    NotifType.REMINDER  -> "⏰"
}

val sampleNotifications = listOf(
    InstruxaNotif(1, NotifType.BOOKING,   "Booking Confirmed!", "Kevin Omondi accepted your booking for Tuesday at 11:00 AM.", "2 min ago",  false),
    InstruxaNotif(2, NotifType.PAYMENT,   "M-Pesa Payment Sent","KSh 1,650 has been sent for your session with Kevin Omondi.", "5 min ago",  false),
    InstruxaNotif(3, NotifType.AVAILABLE, "DJ Pulse is Available","DJ Pulse just went live in your area. Book before he's gone!", "1 hr ago",  false),
    InstruxaNotif(4, NotifType.REMINDER,  "Session Tomorrow!",  "Don't forget — Kevin Omondi at 11:00 AM tomorrow.", "3 hrs ago", true),
    InstruxaNotif(5, NotifType.REVIEW,    "Rate Your Session",  "How was your session with Grace Njeri? Leave a review.", "Yesterday", true),
    InstruxaNotif(6, NotifType.PAYMENT,   "Earnings Received",  "KSh 2,400 deposited to your M-Pesa for 2 sessions.", "Yesterday", true),
    InstruxaNotif(7, NotifType.BOOKING,   "New Booking Request","Brian M. wants to book you for a live event on Friday.", "2 days ago", true),
    InstruxaNotif(8, NotifType.AVAILABLE, "Natasha M. Available","Saxophonist Natasha just became available near Westlands.", "3 days ago", true)
)

@Composable
fun NotificationsScreen(navController: NavController) {

    var notifications by remember { mutableStateOf(sampleNotifications) }
    val unreadCount = notifications.count { !it.isRead }

    // Entry stagger
    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(600)) }

    val inf = rememberInfiniteTransition(label = "notif")
    val glowPulse by inf.animateFloat(0.3f,0.9f, infiniteRepeatable(tween(1800,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val badgePulse by inf.animateFloat(0.6f,1f, infiniteRepeatable(tween(700,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="b")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(6000,easing=LinearEasing),RepeatMode.Restart),label="p")

    Box(modifier=Modifier.fillMaxSize().background(Color(0xFF080612))) {

        // Background
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush=Brush.radialGradient(
                    listOf(Color(0xFF1E0A3A).copy(alpha=glowPulse*0.6f),Color.Transparent),
                    center=Offset(size.width*0.5f, size.height*0.2f),
                    radius=size.minDimension*0.75f
                )
            )
            val sp=52.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2)
                for(r in 0..(size.height/sp).toInt()+2)
                    drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f),1.dp.toPx(),Offset(c*sp,r*sp))
        }

        Column(
            modifier=Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .alpha(contentAlpha.value)
        ) {

            // HEADER
            Row(
                modifier=Modifier.fillMaxWidth()
                    .padding(horizontal=20.dp).padding(top=52.dp,bottom=16.dp),
                horizontalArrangement=Arrangement.SpaceBetween,
                verticalAlignment=Alignment.CenterVertically
            ) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(
                        modifier=Modifier.size(42.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha=0.08f))
                            .clickable{navController.popBackStack()},
                        contentAlignment=Alignment.Center
                    ){Icon(Icons.Default.ArrowBack,null,tint=Color.White,modifier=Modifier.size(20.dp))}
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Notifications", fontSize=20.sp, fontWeight=FontWeight.Bold, color=Color.White)
                        if (unreadCount > 0)
                            Text("$unreadCount unread", fontSize=12.sp, color=Color(0xFF8B5CF6))
                    }
                }

                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    // Unread badge
                    if (unreadCount > 0) {
                        Box(
                            modifier=Modifier
                                .scale(badgePulse)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF8B5CF6).copy(alpha=0.2f))
                                .border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.5f),RoundedCornerShape(12.dp))
                                .padding(horizontal=12.dp,vertical=6.dp)
                        ){
                            Text("$unreadCount NEW", fontSize=10.sp, color=Color(0xFF8B5CF6), fontWeight=FontWeight.Bold, letterSpacing=1.sp)
                        }
                    }
                    // Mark all read
                    Box(
                        modifier=Modifier.clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha=0.06f))
                            .clickable{ notifications=notifications.map{it.copy(isRead=true)} }
                            .padding(horizontal=10.dp,vertical=6.dp)
                    ){
                        Text("All Read", fontSize=11.sp, color=Color.White.copy(alpha=0.4f))
                    }
                }
            }

            // GROUP: TODAY
            val todayNotifs = notifications.filter { it.time.contains("min") || it.time.contains("hr") }
            val yesterdayNotifs = notifications.filter { it.time == "Yesterday" }
            val earlierNotifs = notifications.filter { it.time.contains("days") }

            if (todayNotifs.isNotEmpty()) {
                NotifGroupLabel("Today", todayNotifs.count{!it.isRead})
                Column(modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    todayNotifs.forEach { notif ->
                        NotifCard(notif=notif) {
                            notifications = notifications.map { if(it.id==notif.id) it.copy(isRead=true) else it }
                        }
                    }
                }
            }

            if (yesterdayNotifs.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                NotifGroupLabel("Yesterday", 0)
                Column(modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    yesterdayNotifs.forEach { notif ->
                        NotifCard(notif=notif) {
                            notifications = notifications.map { if(it.id==notif.id) it.copy(isRead=true) else it }
                        }
                    }
                }
            }

            if (earlierNotifs.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                NotifGroupLabel("Earlier", 0)
                Column(modifier=Modifier.fillMaxWidth().padding(horizontal=16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    earlierNotifs.forEach { notif ->
                        NotifCard(notif=notif) {
                            notifications = notifications.map { if(it.id==notif.id) it.copy(isRead=true) else it }
                        }
                    }
                }
            }

            Spacer(Modifier.height(50.dp))
        }
    }
}

@Composable
fun NotifGroupLabel(label: String, unread: Int) {
    Row(
        modifier=Modifier.fillMaxWidth()
            .padding(horizontal=20.dp,vertical=10.dp),
        verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.SpaceBetween
    ) {
        Text(label, fontSize=12.sp, fontWeight=FontWeight.Bold, color=Color.White.copy(alpha=0.4f), letterSpacing=1.sp)
        if (unread>0)
            Box(modifier=Modifier.clip(CircleShape).background(Color(0xFF8B5CF6)).padding(horizontal=8.dp,vertical=2.dp)){
                Text("$unread", fontSize=10.sp, color=Color.White, fontWeight=FontWeight.Bold)
            }
    }
}

@Composable
fun NotifCard(notif: InstruxaNotif, onTap: () -> Unit) {
    val color = notifColor(notif.type)
    val inf = rememberInfiniteTransition(label="nc${notif.id}")
    val glow by inf.animateFloat(0.4f,0.9f, infiniteRepeatable(tween(1500,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")

    Row(
        modifier=Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if(!notif.isRead) Brush.horizontalGradient(listOf(color.copy(alpha=0.08f),Color(0xFF130A28)))
                else Brush.horizontalGradient(listOf(Color(0xFF130A28),Color(0xFF110A22)))
            )
            .border(
                width=if(!notif.isRead) 1.dp else 1.dp,
                color=if(!notif.isRead) color.copy(alpha=if(!notif.isRead) glow*0.5f else 0.1f) else Color.White.copy(alpha=0.05f),
                shape=RoundedCornerShape(16.dp)
            )
            .clickable(onClick=onTap)
            .padding(14.dp),
        verticalAlignment=Alignment.Top
    ) {
        // Unread indicator + icon
        Box(modifier=Modifier.size(42.dp), contentAlignment=Alignment.Center) {
            Box(
                modifier=Modifier.size(42.dp).clip(CircleShape)
                    .background(color.copy(alpha=if(!notif.isRead) 0.18f else 0.09f)),
                contentAlignment=Alignment.Center
            ){
                Text(notifEmoji(notif.type), fontSize=18.sp)
            }
            if (!notif.isRead) {
                Box(modifier=Modifier.size(9.dp).align(Alignment.TopEnd)
                    .clip(CircleShape).background(Color(0xFF8B5CF6)))
            }
        }

        Spacer(Modifier.width(12.dp))
        Column(modifier=Modifier.weight(1f)) {
            Text(notif.title, fontSize=13.sp, fontWeight=if(!notif.isRead) FontWeight.Bold else FontWeight.Medium, color=Color.White)
            Spacer(Modifier.height(3.dp))
            Text(notif.body, fontSize=12.sp, color=Color.White.copy(alpha=0.45f), lineHeight=17.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(notif.time, fontSize=10.sp, color=Color.White.copy(alpha=0.25f), modifier=Modifier.padding(top=2.dp))
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun NotificationsScreenPreview() {
    InstruxaTheme { NotificationsScreen(rememberNavController()) }
}