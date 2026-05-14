package com.clementvexegon.instruxa.ui.screens.userprofile

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.*
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import androidx.compose.material.icons.automirrored.filled.Logout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.*

// ─────────────────────────────────────────
//  INSTRUXA — User Profile Screen
//
//  Reads userType from Firestore to show
//  the correct view — Artist or Client.
//
//  Artist view:
//  • Earnings card + this month + bar chart
//  • Profile completeness bar
//  • Upcoming sessions
//  • Edit profile button → EditProfileScreen
//
//  Client view:
//  • Sessions booked / Artists hired / Spent
//  • Favourite artists grid
//  • My bookings shortcut
//
//  Both:
//  • Real name/email from Firebase Auth
//  • Avatar with initials from their name
//  • Settings → About → Sign Out
//  • CvX cornerstone mark at bottom
// ─────────────────────────────────────────

data class QuickActionItem(val icon: ImageVector, val title: String, val subtitle: String, val route: String, val color: Color)

@Composable
fun UserProfileScreen(navController: NavController) {

    val isPreview = LocalInspectionMode.current

    // ── Load user data from Firebase ──────
    var userName     by remember { mutableStateOf(if (isPreview) "Clement Vexegon" else "") }
    var userEmail    by remember { mutableStateOf(if (isPreview) "clement@instruxa.com" else "") }
    var userType     by remember { mutableStateOf(if (isPreview) "artist" else "client") } // "artist" or "client"
    var isLoading    by remember { mutableStateOf(!isPreview) }

    LaunchedEffect(Unit) {
        if (isPreview) return@LaunchedEffect

        val auth = FirebaseAuth.getInstance()
        val db   = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null) {
            userEmail = user.email ?: ""
            // Load user profile from Firestore
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userName  = doc.getString("name")     ?: user.email?.substringBefore("@") ?: "User"
                        userType  = doc.getString("userType") ?: "client"
                    } else {
                        userName = user.email?.substringBefore("@") ?: "User"
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    userName  = user.email?.substringBefore("@") ?: "User"
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    val initials = userName.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        .ifEmpty { userEmail.firstOrNull()?.uppercaseChar()?.toString() ?: "U" }

    // Entry animation
    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(isLoading) {
        if (!isLoading) contentAlpha.animateTo(1f, tween(500))
    }

    val inf = rememberInfiniteTransition(label = "up")
    val glowPulse  by inf.animateFloat(0.3f,0.85f, infiniteRepeatable(tween(2200,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val avatarGlow by inf.animateFloat(0.4f,1f, infiniteRepeatable(tween(1600,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="av")

    Box(modifier=Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.6f),Color.Transparent),Offset(size.width*0.5f,200.dp.toPx()),size.minDimension*0.75f))
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF3A1010).copy(alpha=0.35f),Color.Transparent),Offset(size.width*0.85f,size.height*0.85f),size.minDimension*0.5f))
            val sp=50.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f),1.dp.toPx(),Offset(c*sp,r*sp))
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center) {
                CircularProgressIndicator(color=Color(0xFF8B5CF6))
            }
        } else {
            Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).alpha(contentAlpha.value)) {

                Spacer(Modifier.height(52.dp))

                // ── HEADER ROW ────────────────────
                Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("My Profile",fontSize=22.sp,fontWeight=FontWeight.Bold,color=Color.White)

                    // User type badge — shows Artist or Client clearly
                    Box(
                        modifier=Modifier.clip(RoundedCornerShape(20.dp))
                            .background(
                                if(userType=="artist")
                                    Brush.horizontalGradient(listOf(Color(0xFF9F3A3A).copy(alpha=0.25f),Color(0xFF8B5CF6).copy(alpha=0.2f)))
                                else
                                    Brush.horizontalGradient(listOf(Color(0xFF10B981).copy(alpha=0.2f),Color(0xFF22D3EE).copy(alpha=0.15f)))
                            )
                            .border(1.dp, if(userType=="artist") Color(0xFF8B5CF6).copy(alpha=0.4f) else Color(0xFF10B981).copy(alpha=0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal=14.dp,vertical=7.dp)
                    ) {
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Text(if(userType=="artist") "🎸" else "🎧", fontSize=14.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if(userType=="artist") "Artist" else "Client",
                                fontSize=13.sp, fontWeight=FontWeight.Bold,
                                color=if(userType=="artist") Color(0xFF8B5CF6) else Color(0xFF10B981)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── PROFILE HERO ──────────────────
                Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment=Alignment.CenterHorizontally) {

                    // Avatar
                    Box(modifier=Modifier.size(100.dp), contentAlignment=Alignment.Center) {
                        Canvas(Modifier.fillMaxSize()){
                            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF8B5CF6).copy(alpha=avatarGlow*0.45f),Color.Transparent)))
                        }
                        Box(modifier=Modifier.size(82.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9)))).border(2.dp,Color.White.copy(alpha=0.15f),CircleShape), contentAlignment=Alignment.Center){
                            Text(initials,fontSize=28.sp,fontWeight=FontWeight.Bold,color=Color.White)
                        }
                        // Online dot
                        Box(modifier=Modifier.size(16.dp).align(Alignment.BottomEnd).offset((-4).dp,(-4).dp)){
                            Canvas(Modifier.fillMaxSize()){
                                drawCircle(Color(0xFF10B981).copy(alpha=0.35f),size.minDimension*0.7f)
                                drawCircle(Color(0xFF10B981),size.minDimension*0.38f)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        userName.ifEmpty { "Your Name" },
                        fontSize=22.sp, fontWeight=FontWeight.Bold,
                        color=Color.White, letterSpacing=(-0.3).sp
                    )
                    Text(userEmail, fontSize=12.sp, color=Color.White.copy(alpha=0.35f))
                    Spacer(Modifier.height(6.dp))

                    // Location
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.LocationOn,null,tint=Color(0xFF8B5CF6),modifier=Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Nairobi, Kenya",fontSize=12.sp,color=Color.White.copy(alpha=0.4f))
                    }

                    Spacer(Modifier.height(16.dp))

                    // Edit profile button
                    Box(modifier=Modifier.clip(RoundedCornerShape(20.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6)))).clickable{navController.navigate(ROUT_EDIT_PROFILE)}.padding(horizontal=24.dp,vertical=10.dp)){
                        Row(verticalAlignment=Alignment.CenterVertically){
                            Icon(Icons.Default.Edit,null,tint=Color.White,modifier=Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Profile",fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                Column(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp)) {

                    // ── STATS ROW ─────────────────────
                    Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF130A28)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.12f),RoundedCornerShape(18.dp)).padding(16.dp), Arrangement.SpaceEvenly) {
                        if(userType=="artist"){
                            // Artist stats
                            UpStat("KSh 48K","Earned",Color(0xFF10B981))
                            Box(Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha=0.07f)))
                            UpStat("4.9 ⭐","Rating",Color(0xFFF59E0B))
                            Box(Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha=0.07f)))
                            UpStat("120+","Gigs",Color(0xFF8B5CF6))
                        } else {
                            // Client stats
                            UpStat("12","Sessions",Color(0xFF8B5CF6))
                            Box(Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha=0.07f)))
                            UpStat("6","Artists",Color(0xFF22D3EE))
                            Box(Modifier.width(1.dp).height(36.dp).background(Color.White.copy(alpha=0.07f)))
                            UpStat("KSh 14K","Spent",Color(0xFF10B981))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── ARTIST-SPECIFIC CONTENT ───────
                    if(userType=="artist"){

                        // Earnings card
                        Column(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF0A1F0A),Color(0xFF0A0F0A)))).border(1.dp,Color(0xFF10B981).copy(alpha=0.25f),RoundedCornerShape(18.dp)).padding(18.dp)){
                            Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween,Alignment.CenterVertically){
                                Column{
                                    Text("THIS MONTH",fontSize=10.sp,color=Color(0xFF10B981).copy(alpha=0.6f),letterSpacing=1.sp)
                                    Text("KSh 12,400",fontSize=24.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                    Text("+18% vs last month",fontSize=11.sp,color=Color(0xFF10B981))
                                }
                                Box(modifier=Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF10B981).copy(alpha=0.15f)), contentAlignment=Alignment.Center){
                                    Text("💰",fontSize=24.sp)
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            // Mini bar chart
                            Row(Modifier.fillMaxWidth().height(40.dp), Arrangement.spacedBy(6.dp), Alignment.Bottom){
                                listOf(0.4f,0.65f,0.5f,0.8f,0.6f,0.9f,0.75f).forEach { h ->
                                    Box(modifier=Modifier.weight(1f).fillMaxHeight(h).clip(RoundedCornerShape(topStart=4.dp,topEnd=4.dp)).background(Color(0xFF10B981).copy(alpha=0.3f+h*0.4f)))
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Profile completeness
                        Column(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF130A28)).border(1.dp,Color.White.copy(alpha=0.07f),RoundedCornerShape(16.dp)).padding(16.dp)){
                            Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween,Alignment.CenterVertically){
                                Text("Profile Completeness",fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                Text("85%",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color(0xFF8B5CF6))
                            }
                            Spacer(Modifier.height(10.dp))
                            Box(modifier=Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha=0.08f))){
                                Box(modifier=Modifier.fillMaxWidth(0.85f).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6)))))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Add portfolio videos to reach 100%",fontSize=11.sp,color=Color.White.copy(alpha=0.35f))
                        }

                        Spacer(Modifier.height(16.dp))

                        // Upcoming sessions
                        Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween,Alignment.CenterVertically){
                            Text("Upcoming Sessions",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            Text("See all →",fontSize=12.sp,color=Color(0xFF8B5CF6),modifier=Modifier.clickable{navController.navigate(ROUT_MY_BOOKINGS)})
                        }
                        Spacer(Modifier.height(10.dp))
                        listOf(Triple("Brian M.","Tue May 6 · 11:00 AM","KSh 1,650"), Triple("Sarah K.","Thu May 8 · 3:00 PM","KSh 2,450")).forEach { (name,time,price) ->
                            Row(modifier=Modifier.fillMaxWidth().padding(vertical=4.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.12f),RoundedCornerShape(14.dp)).padding(14.dp), verticalAlignment=Alignment.CenterVertically){
                                Box(modifier=Modifier.size(40.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF8B5CF6).copy(alpha=0.4f),Color(0xFF6D28D9)))), contentAlignment=Alignment.Center){ Text(name.first().toString(),fontSize=16.sp,fontWeight=FontWeight.Bold,color=Color.White) }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)){
                                    Text(name,fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                    Text(time,fontSize=11.sp,color=Color.White.copy(alpha=0.4f))
                                }
                                Text(price,fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            }
                        }

                    } else {
                        // ── CLIENT-SPECIFIC CONTENT ───────

                        // Favourite artists
                        Text("Favourite Artists",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White,modifier=Modifier.padding(bottom=12.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)){
                            listOf(Triple("KO","Kevin","🎸"),Triple("GN","Grace","🎻"),Triple("DP","DJ P","🎧")).forEach { (_,name,emoji) ->
                                Column(Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,Color.White.copy(alpha=0.06f),RoundedCornerShape(14.dp)).clickable{navController.navigate(ROUT_ARTIST_PROFILE)}.padding(12.dp), horizontalAlignment=Alignment.CenterHorizontally){
                                    Box(modifier=Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF8B5CF6).copy(alpha=0.5f),Color(0xFF6D28D9)))), contentAlignment=Alignment.Center){ Text(emoji,fontSize=22.sp) }
                                    Spacer(Modifier.height(6.dp))
                                    Text(name,fontSize=11.sp,color=Color.White.copy(alpha=0.7f),textAlign=TextAlign.Center)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Quick actions
                        Text("Quick Actions",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White,modifier=Modifier.padding(bottom=12.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)){
                            listOf(
                                QuickActionItem(Icons.Default.Search, "Explore", "Find Artists", ROUT_EXPLORE, Color(0xFF8B5CF6)),
                                QuickActionItem(Icons.Default.DateRange, "Bookings", "My Sessions", ROUT_MY_BOOKINGS, Color(0xFF10B981))
                            ).forEach { item ->
                                Box(modifier=Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(item.color.copy(alpha=0.12f)).border(1.dp,item.color.copy(alpha=0.25f),RoundedCornerShape(16.dp)).clickable{navController.navigate(item.route)}.padding(16.dp)){
                                    Column{
                                        Icon(item.icon,null,tint=item.color,modifier=Modifier.size(22.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text(item.title,fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                        Text(item.subtitle,fontSize=11.sp,color=Color.White.copy(alpha=0.4f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // ── SETTINGS LIST ─────────────────
                    Text("Settings",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White,modifier=Modifier.padding(bottom=12.dp))
                    listOf(
                        Triple(Icons.Default.Notifications,"Notifications",ROUT_NOTIFICATIONS),
                        Triple(Icons.Default.Settings,"App Settings",ROUT_SETTINGS),
                        Triple(Icons.Default.Info,"About Instruxa",ROUT_ABOUT)
                    ).forEach { (icon,label,route) ->
                        Row(modifier=Modifier.fillMaxWidth().padding(vertical=4.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,Color.White.copy(alpha=0.05f),RoundedCornerShape(14.dp)).clickable{navController.navigate(route)}.padding(14.dp), verticalAlignment=Alignment.CenterVertically){
                            Box(modifier=Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF8B5CF6).copy(alpha=0.12f)), contentAlignment=Alignment.Center){ Icon(icon,null,tint=Color(0xFF8B5CF6),modifier=Modifier.size(16.dp)) }
                            Spacer(Modifier.width(12.dp))
                            Text(label,fontSize=14.sp,color=Color.White,modifier=Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight,null,tint=Color.White.copy(alpha=0.2f),modifier=Modifier.size(16.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── SIGN OUT ──────────────────────
                    Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFEF4444).copy(alpha=0.07f)).border(1.dp,Color(0xFFEF4444).copy(alpha=0.22f),RoundedCornerShape(14.dp))
                        .clickable{
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(ROUT_LOGIN){ popUpTo(0){inclusive=true} }
                        }.padding(16.dp),
                        Arrangement.Center, Alignment.CenterVertically){
                        Icon(Icons.AutoMirrored.Filled.Logout,null,tint=Color(0xFFEF4444),modifier=Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sign Out",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color(0xFFEF4444))
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── CvX CORNERSTONE ───────────────
                    Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment=Alignment.CenterHorizontally){
                        Box(modifier=Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF9F3A3A).copy(alpha=0.6f),Color(0xFF6D28D9).copy(alpha=0.6f)))), contentAlignment=Alignment.Center){
                            Text("CX",fontSize=10.sp,fontWeight=FontWeight.Bold,color=Color.White.copy(alpha=0.8f))
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Instruxa v1.0",fontSize=10.sp,color=Color.White.copy(alpha=0.12f),letterSpacing=1.sp)
                        Text("by Clement Vexegon",fontSize=9.sp,color=Color.White.copy(alpha=0.08f),letterSpacing=0.5.sp)
                    }
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun UpStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment=Alignment.CenterHorizontally){
        Text(value,fontSize=17.sp,fontWeight=FontWeight.Bold,color=color)
        Text(label,fontSize=11.sp,color=Color.White.copy(alpha=0.35f))
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun UserProfileScreenPreview() { InstruxaTheme { UserProfileScreen(rememberNavController()) } }
