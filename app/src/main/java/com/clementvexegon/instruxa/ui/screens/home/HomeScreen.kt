package com.clementvexegon.instruxa.ui.screens.home

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
import com.clementvexegon.instruxa.navigation.*
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.*

data class HomeArtist(val id: String, val name: String, val initials: String, val role: String, val distance: String, val rating: Float, val reviews: Int, val price: Int, val isAvailable: Boolean, val emoji: String, val accentColor: Color)
data class HomeCategory(val name: String, val emoji: String, val color: Color)

val homeArtists = listOf(
    HomeArtist("1","Kevin Omondi","KO","Guitarist · Pianist","2.1 km",4.9f,47,800,true,"🎸",Color(0xFF8B5CF6)),
    HomeArtist("2","Grace Njeri","GN","Violinist · Vocalist","3.4 km",4.8f,31,1200,false,"🎻",Color(0xFFC46A6A)),
    HomeArtist("3","DJ Pulse","DP","DJ · Producer","1.8 km",4.6f,62,1500,true,"🎧",Color(0xFF22D3EE)),
    HomeArtist("4","Amina Wanjiku","AW","Pianist · Keys","4.2 km",4.7f,28,1000,true,"🎹",Color(0xFF10B981)),
    HomeArtist("5","Brian Kamau","BK","Drummer · Percussionist","5.1 km",4.5f,19,700,false,"🥁",Color(0xFFF59E0B)),
    HomeArtist("6","Natasha M.","NM","Saxophonist · Flutist","3.8 km",4.9f,55,1100,true,"🎷",Color(0xFF9F3A3A))
)

val homeCategories = listOf(
    HomeCategory("All","🎵",Color(0xFF8B5CF6)),
    HomeCategory("Guitar","🎸",Color(0xFF9F3A3A)),
    HomeCategory("Piano","🎹",Color(0xFF22D3EE)),
    HomeCategory("Drums","🥁",Color(0xFFF59E0B)),
    HomeCategory("DJ","🎧",Color(0xFF10B981)),
    HomeCategory("Violin","🎻",Color(0xFFA78BFA)),
    HomeCategory("Sax","🎷",Color(0xFFEF4444))
)

@Composable
fun HomeScreen(navController: NavController) {

    var selectedCategory by remember { mutableIntStateOf(0) }
    var selectedTab      by remember { mutableIntStateOf(0) }

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(700)) }

    val inf = rememberInfiniteTransition(label = "home")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(5000,easing=LinearEasing),RepeatMode.Restart),label="p")
    val glowPulse    by inf.animateFloat(0.3f,0.9f, infiniteRepeatable(tween(2500,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val availPulse   by inf.animateFloat(0.3f,1f, infiniteRepeatable(tween(700,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="av")
    val waveAnim     by inf.animateFloat(0f,(2f*PI).toFloat(), infiniteRepeatable(tween(1100,easing=LinearEasing),RepeatMode.Restart),label="w")

    val filteredArtists = if (selectedCategory == 0) homeArtists
    else homeArtists.filter { it.role.lowercase().contains(homeCategories[selectedCategory].name.lowercase()) }

    val pSeeds = remember {
        listOf(Offset(0.08f,0.3f),Offset(0.92f,0.15f),Offset(0.05f,0.65f),
            Offset(0.95f,0.7f),Offset(0.5f,0.05f),Offset(0.25f,0.85f),
            Offset(0.75f,0.75f),Offset(0.15f,0.45f),Offset(0.85f,0.45f))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.65f),Color.Transparent),Offset(size.width*0.75f,size.height*0.12f),size.minDimension*0.85f))
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF3A1010).copy(alpha=0.4f),Color.Transparent),Offset(size.width*0.15f,size.height*0.88f),size.minDimension*0.6f))
            val sp=48.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.07f),1.dp.toPx(),Offset(c*sp,r*sp))
            pSeeds.forEachIndexed { i,pos ->
                val prog=(particleAnim+i*(1f/pSeeds.size))%1f
                val x=pos.x*size.width+sin(prog*2f*PI.toFloat()+i.toFloat())*20.dp.toPx()
                val y=size.height*(1f-prog)
                val a=sin(prog*PI.toFloat()).coerceIn(0f,1f)*0.55f
                drawCircle(when(i%3){0->Color(0xFF8B5CF6).copy(alpha=a);1->Color(0xFF22D3EE).copy(alpha=a*0.7f);else->Color(0xFFC46A6A).copy(alpha=a*0.5f)},(1.5f+i%3).dp.toPx(),Offset(x,y))
            }
        }

        Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).alpha(contentAlpha.value).padding(bottom=92.dp)) {

            // HEADER
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(top=50.dp,bottom=4.dp),
                horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
                Column {
                    Text("Instruxa", fontSize=28.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=(-0.5).sp)
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint=Color(0xFF8B5CF6), modifier=Modifier.size(12.dp))
                        Spacer(Modifier.width(3.dp))
                        Text("Nairobi, Kenya", fontSize=12.sp, color=Color.White.copy(alpha=0.4f))
                    }
                }
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp), verticalAlignment=Alignment.CenterVertically) {

                    // ── NOTIFICATION BELL — wired ──
                    Box(
                        modifier=Modifier.size(40.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha=0.08f))
                            .clickable { navController.navigate(ROUT_NOTIFICATIONS) },
                        contentAlignment=Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, null, tint=Color.White, modifier=Modifier.size(18.dp))
                    }

                    // ── CV AVATAR — wired to User Profile ──
                    Box(
                        modifier=Modifier.size(40.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9))))
                            .clickable { navController.navigate(ROUT_USER_PROFILE) },
                        contentAlignment=Alignment.Center
                    ) {
                        Text("CV", fontSize=13.sp, fontWeight=FontWeight.Bold, color=Color.White)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // LIVE BANNER
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF8B5CF6).copy(alpha=0.1f)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.22f),RoundedCornerShape(14.dp)).padding(horizontal=16.dp,vertical=11.dp),
                verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Canvas(Modifier.size(9.dp)) { drawCircle(Color(0xFF10B981).copy(alpha=availPulse*0.4f),size.minDimension*0.7f); drawCircle(Color(0xFF10B981),size.minDimension*0.38f) }
                    Spacer(Modifier.width(9.dp))
                    Text("47 artists live in Nairobi now", fontSize=13.sp, color=Color.White.copy(alpha=0.75f))
                }
                Box(Modifier.height(16.dp).width(28.dp)){
                    Canvas(Modifier.fillMaxSize()){
                        val count=4; val bw=size.width/(count*2f-1)
                        for(i in 0 until count){ val h=(0.3f+0.7f* abs(sin(waveAnim+i*0.8f)))*size.height; drawRoundRect(Color(0xFF10B981).copy(alpha=0.7f),topLeft=Offset(i*bw*2f,size.height-h),size=Size(bw*0.7f,h),cornerRadius=CornerRadius(4f)) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // SEARCH BAR — taps into Explore
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).height(54.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF130A28)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.18f),RoundedCornerShape(16.dp)).clickable { navController.navigate(ROUT_EXPLORE) }.padding(horizontal=16.dp), verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint=Color(0xFF8B5CF6), modifier=Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Search guitarists, DJs, pianists...", fontSize=14.sp, color=Color.White.copy(alpha=0.25f), modifier=Modifier.weight(1f))
                Box(modifier=Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFF8B5CF6).copy(alpha=0.18f)).padding(horizontal=10.dp,vertical=5.dp)){
                    Text("Filter", fontSize=11.sp, color=Color(0xFF8B5CF6), fontWeight=FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(22.dp))

            // CATEGORIES
            Text("Categories", fontSize=16.sp, fontWeight=FontWeight.Bold, color=Color.White, modifier=Modifier.padding(start=20.dp,bottom=12.dp))
            Row(modifier=Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=20.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)){
                homeCategories.forEachIndexed { i,cat ->
                    val isSel=selectedCategory==i
                    Box(modifier=Modifier.clip(RoundedCornerShape(16.dp)).background(if(isSel) cat.color.copy(alpha=0.22f) else Color(0xFF130A28)).border(if(isSel) 1.5.dp else 1.dp,if(isSel) cat.color else Color.White.copy(alpha=0.09f),RoundedCornerShape(16.dp)).clickable{selectedCategory=i}.padding(horizontal=14.dp,vertical=10.dp)){
                        Column(horizontalAlignment=Alignment.CenterHorizontally){ Text(cat.emoji,fontSize=22.sp); Spacer(Modifier.height(4.dp)); Text(cat.name,fontSize=11.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,color=if(isSel) Color.White else Color.White.copy(alpha=0.45f)) }
                    }
                }
            }

            Spacer(Modifier.height(26.dp))

            // FEATURED
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){
                Text("Featured", fontSize=16.sp, fontWeight=FontWeight.Bold, color=Color.White)
                Text("See all →", fontSize=12.sp, color=Color(0xFF8B5CF6), modifier=Modifier.clickable{navController.navigate(ROUT_EXPLORE)})
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier=Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=20.dp), horizontalArrangement=Arrangement.spacedBy(14.dp)){
                homeArtists.take(4).forEach{ FeaturedCard(artist=it){ navController.navigate(ROUT_ARTIST_PROFILE) } }
            }

            Spacer(Modifier.height(26.dp))

            // NEAR YOU
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){
                Text("Near You", fontSize=16.sp, fontWeight=FontWeight.Bold, color=Color.White)
                Text("${filteredArtists.size} found", fontSize=12.sp, color=Color.White.copy(alpha=0.35f))
            }
            Spacer(Modifier.height(12.dp))
            Column(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
                filteredArtists.forEach{ NearbyCard(artist=it){ navController.navigate(ROUT_ARTIST_PROFILE) } }
            }

            Spacer(Modifier.height(16.dp))
            Text("Crafted with purpose · Instruxa", fontSize=10.sp, color=Color.White.copy(alpha=0.12f), textAlign=TextAlign.Center, letterSpacing=1.5.sp, modifier=Modifier.fillMaxWidth().padding(bottom=8.dp))
        }

        // Bottom nav — Profile tab wired
        InstruxaBottomNav(
            selectedTab=selectedTab,
            onTabSelected={
                selectedTab=it
                when(it) {
                    1 -> navController.navigate(ROUT_EXPLORE)
                    2 -> navController.navigate(ROUT_MY_BOOKINGS)
                    3 -> navController.navigate(ROUT_USER_PROFILE)
                }
            },
            modifier=Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun FeaturedCard(artist: HomeArtist, onClick: () -> Unit) {
    Box(modifier=Modifier.width(152.dp).height(195.dp).clip(RoundedCornerShape(20.dp)).background(Brush.verticalGradient(listOf(artist.accentColor.copy(alpha=0.28f),Color(0xFF130A28)))).border(1.dp,artist.accentColor.copy(alpha=0.28f),RoundedCornerShape(20.dp)).clickable(onClick=onClick).padding(14.dp)){
        Column {
            Box(modifier=Modifier.size(54.dp).clip(CircleShape).background(Brush.linearGradient(listOf(artist.accentColor.copy(alpha=0.65f),Color(0xFF6D28D9)))),contentAlignment=Alignment.Center){ Text(artist.emoji,fontSize=24.sp) }
            Spacer(Modifier.height(10.dp))
            Text(artist.name,fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White,maxLines=1,overflow=TextOverflow.Ellipsis)
            Text(artist.role.split("·").first().trim(),fontSize=11.sp,color=Color.White.copy(alpha=0.42f))
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment=Alignment.CenterVertically){ Text("⭐",fontSize=11.sp); Text(" ${artist.rating}",fontSize=11.sp,color=Color(0xFFF59E0B),fontWeight=FontWeight.Bold); Text(" (${artist.reviews})",fontSize=10.sp,color=Color.White.copy(alpha=0.3f)) }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween,modifier=Modifier.fillMaxWidth()){ Text("KSh ${artist.price}/hr",fontSize=11.sp,fontWeight=FontWeight.Bold,color=Color.White); Box(modifier=Modifier.clip(CircleShape).size(8.dp).background(if(artist.isAvailable) Color(0xFF10B981) else Color(0xFFF59E0B))) }
        }
    }
}

@Composable
fun NearbyCard(artist: HomeArtist, onClick: () -> Unit) {
    Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF1A0E35),Color(0xFF110A22)))).border(1.dp,Color.White.copy(alpha=0.06f),RoundedCornerShape(18.dp)).clickable(onClick=onClick).padding(14.dp),verticalAlignment=Alignment.CenterVertically){
        Box(modifier=Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(listOf(artist.accentColor.copy(alpha=0.6f),Color(0xFF6D28D9)))),contentAlignment=Alignment.Center){ Text(artist.emoji,fontSize=24.sp) }
        Spacer(Modifier.width(14.dp))
        Column(modifier=Modifier.weight(1f)){
            Text(artist.name,fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White)
            Text(artist.role,fontSize=12.sp,color=Color.White.copy(alpha=0.42f),maxLines=1,overflow=TextOverflow.Ellipsis)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
                Text("⭐ ${artist.rating}",fontSize=11.sp,color=Color(0xFFF59E0B))
                Text("(${artist.reviews})",fontSize=11.sp,color=Color.White.copy(alpha=0.3f))
                Row(verticalAlignment=Alignment.CenterVertically){ Icon(Icons.Default.LocationOn,null,tint=Color.White.copy(alpha=0.28f),modifier=Modifier.size(10.dp)); Text(artist.distance,fontSize=11.sp,color=Color.White.copy(alpha=0.28f)) }
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(horizontalAlignment=Alignment.End){
            Text("KSh ${artist.price}",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color.White)
            Text("/hr",fontSize=10.sp,color=Color.White.copy(alpha=0.28f))
            Spacer(Modifier.height(7.dp))
            Box(modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(if(artist.isAvailable) Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6))) else Brush.horizontalGradient(listOf(Color(0xFF1A0E35),Color(0xFF1A0E35)))).padding(horizontal=12.dp,vertical=6.dp),contentAlignment=Alignment.Center){
                Text(if(artist.isAvailable)"Hire" else "Busy",fontSize=12.sp,fontWeight=FontWeight.Bold,color=if(artist.isAvailable) Color.White else Color.White.copy(alpha=0.25f))
            }
        }
    }
}

@Composable
fun InstruxaBottomNav(selectedTab: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    val tabs = listOf(Pair(Icons.Default.Home,"Home"),Pair(Icons.Default.Search,"Explore"),Pair(Icons.Default.DateRange,"Bookings"),Pair(Icons.Default.Person,"Profile"))
    Box(modifier=modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=12.dp).clip(RoundedCornerShape(26.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF1A0E35).copy(alpha=0.97f),Color(0xFF110A22).copy(alpha=0.97f)))).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.18f),RoundedCornerShape(26.dp))){
        Row(modifier=Modifier.fillMaxWidth().padding(vertical=9.dp),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically){
            tabs.forEachIndexed { index,(icon,label) ->
                val isSel=selectedTab==index
                val scale by animateFloatAsState(if(isSel) 1.12f else 1f,spring(Spring.DampingRatioMediumBouncy),label="nav$index")
                Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.scale(scale).clip(RoundedCornerShape(12.dp)).clickable{onTabSelected(index)}.padding(horizontal=18.dp,vertical=6.dp)){
                    Icon(icon,null,tint=if(isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.3f),modifier=Modifier.size(22.dp))
                    Spacer(Modifier.height(3.dp))
                    Text(label,fontSize=10.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,color=if(isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.3f))
                }
            }
        }
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun HomeScreenPreview() { InstruxaTheme { HomeScreen(rememberNavController()) } }