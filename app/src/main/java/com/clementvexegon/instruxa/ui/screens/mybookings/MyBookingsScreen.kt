package com.clementvexegon.instruxa.ui.screens.mybookings

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
import com.clementvexegon.instruxa.navigation.ROUT_REVIEWS
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.*

data class BookingItem(val id: String, val artistName: String, val artistEmoji: String, val instrument: String, val date: String, val time: String, val duration: Int, val price: Int, val status: BookingStatus)
enum class BookingStatus { UPCOMING, COMPLETED, CANCELLED }

val sampleBookings = listOf(
    BookingItem("1","Kevin Omondi","🎸","Guitarist","Tue May 6","11:00 AM",2,1650,BookingStatus.UPCOMING),
    BookingItem("2","Amina Wanjiku","🎹","Pianist","Sat May 10","3:00 PM",1,1050,BookingStatus.UPCOMING),
    BookingItem("3","Grace Njeri","🎻","Violinist","Wed Apr 30","9:00 AM",3,3650,BookingStatus.COMPLETED),
    BookingItem("4","DJ Pulse","🎧","DJ","Sat Apr 26","7:00 PM",4,6050,BookingStatus.COMPLETED),
    BookingItem("5","Brian Kamau","🥁","Drummer","Sun Apr 20","1:00 PM",2,1450,BookingStatus.CANCELLED)
)

fun statusColor(s: BookingStatus) = when(s){ BookingStatus.UPCOMING->Color(0xFF8B5CF6); BookingStatus.COMPLETED->Color(0xFF10B981); BookingStatus.CANCELLED->Color(0xFFEF4444) }
fun statusLabel(s: BookingStatus) = when(s){ BookingStatus.UPCOMING->"Upcoming"; BookingStatus.COMPLETED->"Completed"; BookingStatus.CANCELLED->"Cancelled" }
fun statusEmoji(s: BookingStatus) = when(s){ BookingStatus.UPCOMING->"⏰"; BookingStatus.COMPLETED->"✅"; BookingStatus.CANCELLED->"❌" }

@Composable
fun MyBookingsScreen(navController: NavController) {

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All","Upcoming","Completed","Cancelled")

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(500)) }

    val inf = rememberInfiniteTransition(label = "bk")
    val glowPulse by inf.animateFloat(0.3f,0.8f, infiniteRepeatable(tween(2200,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")

    val filtered = when(selectedTab){
        1 -> sampleBookings.filter{it.status==BookingStatus.UPCOMING}
        2 -> sampleBookings.filter{it.status==BookingStatus.COMPLETED}
        3 -> sampleBookings.filter{it.status==BookingStatus.CANCELLED}
        else -> sampleBookings
    }

    val totalSpent   = sampleBookings.filter{it.status==BookingStatus.COMPLETED}.sumOf{it.price}
    val upcomingCount= sampleBookings.count{it.status==BookingStatus.UPCOMING}
    val completedCount=sampleBookings.count{it.status==BookingStatus.COMPLETED}

    Box(modifier=Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.55f),Color.Transparent),Offset(size.width*0.5f,size.height*0.15f),size.minDimension*0.7f))
            val sp=52.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f),1.dp.toPx(),Offset(c*sp,r*sp))
        }

        Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).alpha(contentAlpha.value)) {

            // Header
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(top=52.dp,bottom=20.dp),
                horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
                Box(modifier=Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(alpha=0.08f)).clickable{navController.popBackStack()},contentAlignment=Alignment.Center){
                    Icon(Icons.Default.ArrowBack,null,tint=Color.White,modifier=Modifier.size(20.dp))
                }
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    Text("My Bookings",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color.White)
                    Text("${sampleBookings.size} total sessions",fontSize=12.sp,color=Color(0xFF8B5CF6))
                }
                Box(modifier=Modifier.size(42.dp))
            }

            // Stats
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                listOf(Triple("$upcomingCount","Upcoming",Color(0xFF8B5CF6)), Triple("$completedCount","Completed",Color(0xFF10B981)), Triple("KSh ${totalSpent/1000}K","Spent",Color(0xFFF59E0B))).forEach { (v,l,c) ->
                    Box(modifier=Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,c.copy(alpha=0.2f),RoundedCornerShape(14.dp)).padding(12.dp)) {
                        Column(horizontalAlignment=Alignment.CenterHorizontally, modifier=Modifier.fillMaxWidth()) {
                            Text(v,fontSize=18.sp,fontWeight=FontWeight.Bold,color=c)
                            Text(l,fontSize=10.sp,color=Color.White.copy(alpha=0.35f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Filter tabs
            Row(modifier=Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=20.dp), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                tabs.forEachIndexed { i,t ->
                    val isSel=selectedTab==i
                    Box(modifier=Modifier.clip(RoundedCornerShape(20.dp)).background(if(isSel) Color(0xFF8B5CF6).copy(alpha=0.22f) else Color(0xFF130A28)).border(if(isSel) 1.5.dp else 1.dp,if(isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.09f),RoundedCornerShape(20.dp)).clickable{selectedTab=i}.padding(horizontal=16.dp,vertical=8.dp)){
                        Text(t,fontSize=12.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,color=if(isSel) Color.White else Color.White.copy(alpha=0.4f))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Booking cards
            Column(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                filtered.forEach { booking ->
                    val sColor = statusColor(booking.status)
                    Column(
                        modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF1A0E35),Color(0xFF110A22))))
                            .border(1.dp,sColor.copy(alpha=0.2f),RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Row(verticalAlignment=Alignment.CenterVertically) {
                                Box(modifier=Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(sColor.copy(alpha=0.4f),Color(0xFF6D28D9)))),contentAlignment=Alignment.Center){ Text(booking.artistEmoji,fontSize=20.sp) }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(booking.artistName,fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                    Text(booking.instrument,fontSize=12.sp,color=Color.White.copy(alpha=0.45f))
                                }
                            }
                            Box(modifier=Modifier.clip(RoundedCornerShape(10.dp)).background(sColor.copy(alpha=0.15f)).padding(horizontal=10.dp,vertical=5.dp)){
                                Text("${statusEmoji(booking.status)} ${statusLabel(booking.status)}",fontSize=11.sp,color=sColor,fontWeight=FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha=0.06f)))
                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Column {
                                Text("DATE & TIME",fontSize=9.sp,color=Color.White.copy(alpha=0.3f),letterSpacing=1.sp)
                                Text("${booking.date} · ${booking.time}",fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            }
                            Column(horizontalAlignment=Alignment.End) {
                                Text("DURATION",fontSize=9.sp,color=Color.White.copy(alpha=0.3f),letterSpacing=1.sp)
                                Text("${booking.duration} hr${if(booking.duration>1)"s" else ""}",fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("KSh ${booking.price}",fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            if (booking.status==BookingStatus.COMPLETED) {
                                Box(modifier=Modifier.clip(RoundedCornerShape(10.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6)))).clickable{navController.navigate(ROUT_REVIEWS)}.padding(horizontal=14.dp,vertical=7.dp)){
                                    Text("Rate Session ⭐",fontSize=12.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                }
                            } else if (booking.status==BookingStatus.UPCOMING) {
                                Box(modifier=Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFF8B5CF6).copy(alpha=0.15f)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.3f),RoundedCornerShape(10.dp)).padding(horizontal=14.dp,vertical=7.dp)){
                                    Text("View Ticket",fontSize=12.sp,color=Color(0xFF8B5CF6),fontWeight=FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun MyBookingsScreenPreview() { InstruxaTheme { MyBookingsScreen(rememberNavController()) } }