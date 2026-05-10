package com.clementvexegon.instruxa.ui.screens.explore

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
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_ARTIST_PROFILE
import com.clementvexegon.instruxa.ui.screens.home.homeArtists
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(navController: NavController) {

    var query          by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableIntStateOf(0) }
    var selectedSort   by remember { mutableIntStateOf(0) }
    var maxPrice       by remember { mutableIntStateOf(5000) }
    var showFilters    by remember { mutableStateOf(false) }

    val filters = listOf("All","Guitar","Piano","Drums","DJ","Violin","Sax","Vocals")
    val sortOptions = listOf("Nearest","Top Rated","Price ↑","Price ↓")

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(500)) }

    val inf = rememberInfiniteTransition(label = "explore")
    val glowPulse by inf.animateFloat(0.3f,0.8f, infiniteRepeatable(tween(2500,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")

    val filtered = homeArtists.filter { artist ->
        val matchesQuery  = query.isEmpty() || artist.name.lowercase().contains(query.lowercase()) || artist.role.lowercase().contains(query.lowercase())
        val matchesFilter = selectedFilter == 0 || artist.role.lowercase().contains(filters[selectedFilter].lowercase())
        val matchesPrice  = artist.price <= maxPrice
        matchesQuery && matchesFilter && matchesPrice
    }.let { list ->
        when (selectedSort) {
            1    -> list.sortedByDescending { it.rating }
            2    -> list.sortedBy { it.price }
            3    -> list.sortedByDescending { it.price }
            else -> list.sortedBy { it.distance.replace(" km","").toFloatOrNull() ?: 0f }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.6f),Color.Transparent),Offset(size.width*0.5f,size.height*0.18f),size.minDimension*0.7f))
            val sp=52.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f),1.dp.toPx(),Offset(c*sp,r*sp))
        }

        Column(modifier=Modifier.fillMaxSize().alpha(contentAlpha.value)) {

            // Header
            Column(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(top=52.dp,bottom=12.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Explore", fontSize=26.sp, fontWeight=FontWeight.Bold, color=Color.White)
                        Text("${filtered.size} artists found", fontSize=12.sp, color=Color.White.copy(alpha=0.4f))
                    }
                    Box(
                        modifier=Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                            .background(if(showFilters) Color(0xFF8B5CF6).copy(alpha=0.25f) else Color(0xFF130A28))
                            .border(1.dp,if(showFilters) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.1f),RoundedCornerShape(12.dp))
                            .clickable{showFilters=!showFilters},
                        contentAlignment=Alignment.Center
                    ) { Icon(Icons.Default.Tune,null,tint=if(showFilters) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.5f),modifier=Modifier.size(18.dp)) }
                }

                Spacer(Modifier.height(14.dp))

                // Search field
                OutlinedTextField(
                    value=query, onValueChange={query=it},
                    placeholder={Text("Search by name, instrument...",color=Color.White.copy(alpha=0.2f),fontSize=14.sp)},
                    leadingIcon={Icon(Icons.Default.Search,null,tint=Color(0xFF8B5CF6))},
                    trailingIcon={if(query.isNotEmpty()) IconButton(onClick={query=""}){Icon(Icons.Default.Clear,null,tint=Color.White.copy(alpha=0.4f))}},
                    modifier=Modifier.fillMaxWidth(), singleLine=true,
                    colors=OutlinedTextFieldDefaults.colors(focusedTextColor=Color.White,unfocusedTextColor=Color.White,focusedBorderColor=Color(0xFF8B5CF6),unfocusedBorderColor=Color.White.copy(alpha=0.12f),cursorColor=Color(0xFF8B5CF6),focusedContainerColor=Color(0xFF130A28),unfocusedContainerColor=Color(0xFF130A28)),
                    shape=RoundedCornerShape(14.dp)
                )
            }

            // Expandable filters
            if (showFilters) {
                Column(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF130A28)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.15f),RoundedCornerShape(16.dp)).padding(14.dp)) {

                    Text("Sort by", fontSize=12.sp, fontWeight=FontWeight.Bold, color=Color.White.copy(alpha=0.6f), modifier=Modifier.padding(bottom=8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        sortOptions.forEachIndexed { i,s ->
                            val isSel=selectedSort==i
                            Box(modifier=Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if(isSel) Color(0xFF8B5CF6).copy(alpha=0.25f) else Color(0xFF1A0E35)).border(1.dp,if(isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.08f),RoundedCornerShape(10.dp)).clickable{selectedSort=i}.padding(vertical=8.dp),contentAlignment=Alignment.Center){
                                Text(s,fontSize=10.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,color=if(isSel) Color.White else Color.White.copy(alpha=0.4f),textAlign=TextAlign.Center)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Max price / hr", fontSize=12.sp, fontWeight=FontWeight.Bold, color=Color.White.copy(alpha=0.6f))
                        Text("KSh $maxPrice", fontSize=12.sp, color=Color(0xFF8B5CF6), fontWeight=FontWeight.Bold)
                    }
                    Slider(value=maxPrice.toFloat(), onValueChange={maxPrice=it.toInt()}, valueRange=200f..5000f,
                        colors=SliderDefaults.colors(thumbColor=Color(0xFF8B5CF6),activeTrackColor=Color(0xFF8B5CF6),inactiveTrackColor=Color.White.copy(alpha=0.1f)))
                }
                Spacer(Modifier.height(8.dp))
            }

            // Filter chips
            Row(modifier=Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=20.dp,vertical=8.dp), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                filters.forEachIndexed { i,f ->
                    val isSel=selectedFilter==i
                    Box(modifier=Modifier.clip(RoundedCornerShape(20.dp)).background(if(isSel) Color(0xFF8B5CF6).copy(alpha=0.25f) else Color(0xFF130A28)).border(if(isSel) 1.5.dp else 1.dp,if(isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.1f),RoundedCornerShape(20.dp)).clickable{selectedFilter=i}.padding(horizontal=14.dp,vertical=8.dp)){
                        Text(f,fontSize=12.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,color=if(isSel) Color.White else Color.White.copy(alpha=0.45f))
                    }
                }
            }

            // Results
            if (filtered.isEmpty()) {
                Box(modifier=Modifier.fillMaxSize(), contentAlignment=Alignment.Center) {
                    Column(horizontalAlignment=Alignment.CenterHorizontally) {
                        Text("🔍", fontSize=48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No artists found", fontSize=18.sp, fontWeight=FontWeight.Bold, color=Color.White)
                        Text("Try a different search or filter", fontSize=13.sp, color=Color.White.copy(alpha=0.4f))
                    }
                }
            } else {
                Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=20.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Spacer(Modifier.height(4.dp))
                    filtered.forEach { artist ->
                        Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF1A0E35),Color(0xFF110A22)))).border(1.dp,Color.White.copy(alpha=0.06f),RoundedCornerShape(16.dp)).clickable{navController.navigate(ROUT_ARTIST_PROFILE)}.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
                            Box(modifier=Modifier.size(52.dp).clip(CircleShape).background(Brush.linearGradient(listOf(artist.accentColor.copy(alpha=0.6f),Color(0xFF6D28D9)))),contentAlignment=Alignment.Center){ Text(artist.emoji,fontSize=22.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier=Modifier.weight(1f)){
                                Text(artist.name,fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                Text(artist.role,fontSize=11.sp,color=Color.White.copy(alpha=0.42f),maxLines=1,overflow=TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                                    Text("⭐ ${artist.rating}",fontSize=11.sp,color=Color(0xFFF59E0B))
                                    Text(artist.distance,fontSize=11.sp,color=Color.White.copy(alpha=0.3f))
                                }
                            }
                            Column(horizontalAlignment=Alignment.End){
                                Text("KSh ${artist.price}",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color.White)
                                Text("/hr",fontSize=10.sp,color=Color.White.copy(alpha=0.3f))
                                Spacer(Modifier.height(6.dp))
                                Box(modifier=Modifier.clip(CircleShape).size(8.dp).background(if(artist.isAvailable) Color(0xFF10B981) else Color(0xFFF59E0B)))
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun ExploreScreenPreview() { InstruxaTheme { ExploreScreen(rememberNavController()) } }