package com.clementvexegon.instruxa.ui.screens.profile

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
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import kotlin.math.*

// ─────────────────────────────────────────
//  INSTRUXA — Edit Profile Screen
//  For artists to build their public page.
//  This is where careers are constructed.
//
//  Sections:
//  1. Avatar upload area with glow
//  2. Live profile preview card
//  3. Basic info (name, bio, location)
//  4. Instrument chips (toggle on/off)
//  5. Price per hour selector
//  6. Availability toggle
//  7. Skills tags
//  8. Save with success animation
// ─────────────────────────────────────────

val instrumentOptions = listOf(
    "🎸 Guitar","🎹 Piano","🥁 Drums","🎻 Violin",
    "🎷 Saxophone","🎺 Trumpet","🎸 Bass","🎤 Vocals",
    "🎧 DJ","🎙️ Recording","🎵 Producer","🪗 Accordion"
)

val skillOptions = listOf(
    "Afrobeats","Gospel","Jazz","Classical",
    "R&B","Hip-hop","Pop","Live Events",
    "Studio Recording","Lessons","Weddings","Corporate"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(navController: NavController) {

    // ── Form state ────────────────────────
    var artistName        by remember { mutableStateOf("") }
    var bio               by remember { mutableStateOf("") }
    var location          by remember { mutableStateOf("Nairobi, Kenya") }
    var price             by remember { mutableIntStateOf(800) }
    var isAvailable       by remember { mutableStateOf(true) }
    var selectedInstruments by remember { mutableStateOf(setOf<String>()) }
    var selectedSkills    by remember { mutableStateOf(setOf<String>()) }
    var isSaved           by remember { mutableStateOf(false) }
    var saveScale         by remember { mutableFloatStateOf(1f) }

    // Entry animation
    val contentAlpha = remember { Animatable(0f) }
    val contentSlide = remember { Animatable(50f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(600))
        contentSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    // Background animations
    val inf = rememberInfiniteTransition(label = "edit")
    val glowPulse by inf.animateFloat(0.3f,0.9f, infiniteRepeatable(tween(2200,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(5500,easing=LinearEasing),RepeatMode.Restart),label="p")
    val saveBtnScale by animateFloatAsState(saveScale, spring(Spring.DampingRatioMediumBouncy),label="save")

    val pSeeds = remember {
        listOf(Offset(0.1f,0.2f),Offset(0.9f,0.1f),Offset(0.05f,0.7f),Offset(0.95f,0.6f),
            Offset(0.5f,0.08f),Offset(0.3f,0.88f),Offset(0.7f,0.8f),Offset(0.85f,0.42f))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080612))) {

        // Background
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF1E0A3A).copy(alpha=glowPulse*0.7f),Color.Transparent),
                    center = Offset(size.width*0.5f,size.height*0.25f),
                    radius = size.minDimension*0.75f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF3A1010).copy(alpha=0.35f),Color.Transparent),
                    center = Offset(size.width*0.85f,size.height*0.85f),
                    radius = size.minDimension*0.5f
                )
            )
            val sp = 46.dp.toPx()
            for (c in 0..(size.width/sp).toInt()+2)
                for (r in 0..(size.height/sp).toInt()+2)
                    drawCircle(Color(0xFF6D28D9).copy(alpha=0.07f),1.dp.toPx(),Offset(c*sp,r*sp))
            pSeeds.forEachIndexed { i,pos ->
                val prog = (particleAnim + i*(1f/pSeeds.size)) % 1f
                val x = pos.x*size.width + sin(prog*2f*PI.toFloat()+i.toFloat())*18.dp.toPx()
                val y = size.height*(1f-prog)
                val a = sin(prog*PI.toFloat()).coerceIn(0f,1f)*0.5f
                drawCircle(
                    color=if(i%2==0) Color(0xFF8B5CF6).copy(alpha=a) else Color(0xFFC46A6A).copy(alpha=a*0.6f),
                    radius=(1.5f+i%3).dp.toPx(),center=Offset(x,y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .offset(y = contentSlide.value.dp)
                .alpha(contentAlpha.value)
        ) {

            // TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal=16.dp).padding(top=50.dp,bottom=12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier=Modifier.size(42.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha=0.09f)).clickable{navController.popBackStack()},
                    contentAlignment=Alignment.Center
                ){ Icon(Icons.Default.ArrowBack,null,tint=Color.White,modifier=Modifier.size(20.dp)) }
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    Text("Edit Profile",fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color.White)
                    Text("Build your artist page",fontSize=12.sp,color=Color(0xFF8B5CF6))
                }
                Box(
                    modifier=Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981).copy(alpha=0.15f))
                        .padding(horizontal=10.dp,vertical=5.dp).clickable{ navController.navigate(ROUT_HOME){ popUpTo(0){inclusive=true} } }
                ){ Text("Preview",fontSize=11.sp,color=Color(0xFF10B981),fontWeight=FontWeight.Bold) }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal=20.dp)) {

                // ── AVATAR UPLOAD ─────────────
                Box(modifier=Modifier.fillMaxWidth(), contentAlignment=Alignment.Center){
                    Box(modifier=Modifier.size(110.dp), contentAlignment=Alignment.Center){
                        // Glow
                        Canvas(Modifier.fillMaxSize()){
                            drawCircle(
                                brush=Brush.radialGradient(
                                    listOf(Color(0xFF8B5CF6).copy(alpha=glowPulse*0.45f),Color.Transparent)
                                )
                            )
                        }
                        // Avatar
                        Box(
                            modifier=Modifier.size(90.dp).clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9))))
                                .border(2.dp,Color.White.copy(alpha=0.15f),CircleShape)
                                .clickable {},
                            contentAlignment=Alignment.Center
                        ){
                            if(artistName.isBlank()){
                                Icon(Icons.Default.Person,null,tint=Color.White.copy(alpha=0.5f),modifier=Modifier.size(38.dp))
                            } else {
                                Text(artistName.take(2).uppercase(),fontSize=28.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            }
                        }
                        // Camera overlay
                        Box(
                            modifier=Modifier.size(28.dp).align(Alignment.BottomEnd)
                                .offset(x=(-4).dp,y=(-4).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6))
                                .clickable {},
                            contentAlignment=Alignment.Center
                        ){ Icon(Icons.Default.CameraAlt,null,tint=Color.White,modifier=Modifier.size(14.dp)) }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap avatar to upload from gallery",
                    fontSize=11.sp,color=Color.White.copy(alpha=0.28f),
                    textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // ── LIVE PREVIEW CARD ─────────
                if (artistName.isNotBlank()) {
                    Row(
                        modifier=Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF8B5CF6).copy(alpha=0.15f),Color(0xFF9F3A3A).copy(alpha=0.1f))))
                            .border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.3f),RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalAlignment=Alignment.CenterVertically
                    ){
                        Box(
                            modifier=Modifier.size(46.dp).clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9)))),
                            contentAlignment=Alignment.Center
                        ){ Text(artistName.take(2).uppercase(),fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White) }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier=Modifier.weight(1f)){
                            Text(artistName,fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            if(selectedInstruments.isNotEmpty())
                                Text(selectedInstruments.take(2).joinToString(" · "){it.split(" ").last()},fontSize=11.sp,color=Color(0xFF8B5CF6))
                        }
                        Column(horizontalAlignment=Alignment.End){
                            Text("KSh $price/hr",fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color.White)
                            Row(verticalAlignment=Alignment.CenterVertically){
                                Box(Modifier.size(6.dp).clip(CircleShape).background(if(isAvailable) Color(0xFF10B981) else Color(0xFFF59E0B)))
                                Spacer(Modifier.width(4.dp))
                                Text(if(isAvailable)"Available" else "Busy",fontSize=10.sp,color=if(isAvailable) Color(0xFF10B981) else Color(0xFFF59E0B))
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // ── BASIC INFO ────────────────
                EpSectionLabel("Basic Info")
                Spacer(Modifier.height(12.dp))

                EpTextField(value=artistName, onValueChange={artistName=it}, label="Your Name", placeholder="e.g. Kevin Omondi", icon=Icons.Default.Person)
                Spacer(Modifier.height(12.dp))
                EpTextField(value=location, onValueChange={location=it}, label="Location", placeholder="e.g. Nairobi, Kenya", icon=Icons.Default.LocationOn)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value=bio, onValueChange={if(it.length<=200) bio=it},
                    label={Text("Bio",color=Color.White.copy(alpha=0.35f))},
                    placeholder={Text("Tell people what makes you unique...",color=Color.White.copy(alpha=0.18f),fontSize=13.sp)},
                    modifier=Modifier.fillMaxWidth().height(100.dp),
                    maxLines=4,
                    colors=OutlinedTextFieldDefaults.colors(
                        focusedTextColor=Color.White,unfocusedTextColor=Color.White,
                        focusedBorderColor=Color(0xFF8B5CF6),unfocusedBorderColor=Color.White.copy(alpha=0.1f),
                        cursorColor=Color(0xFF8B5CF6),focusedLabelColor=Color(0xFF8B5CF6),
                        focusedContainerColor=Color(0xFF130A28),unfocusedContainerColor=Color(0xFF130A28)
                    ),
                    shape=RoundedCornerShape(14.dp)
                )
                Row(Modifier.fillMaxWidth(),Arrangement.End){
                    Text("${bio.length}/200",fontSize=10.sp,color=Color.White.copy(alpha=0.22f),modifier=Modifier.padding(top=3.dp))
                }

                Spacer(Modifier.height(24.dp))

                // ── INSTRUMENTS ───────────────
                EpSectionLabel("Your Instruments")
                Text("Tap to select all that apply",fontSize=11.sp,color=Color.White.copy(alpha=0.3f),modifier=Modifier.padding(top=3.dp,bottom=12.dp))

                FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                    instrumentOptions.forEach { inst ->
                        val isSel = inst in selectedInstruments
                        Box(
                            modifier=Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if(isSel) Brush.horizontalGradient(listOf(Color(0xFF8B5CF6).copy(alpha=0.3f),Color(0xFF9F3A3A).copy(alpha=0.2f))) else Brush.horizontalGradient(listOf(Color(0xFF1A0E35),Color(0xFF110A22))))
                                .border(if(isSel) 1.5.dp else 1.dp, if(isSel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.09f),RoundedCornerShape(20.dp))
                                .clickable{ selectedInstruments = if(isSel) selectedInstruments-inst else selectedInstruments+inst }
                                .padding(horizontal=12.dp,vertical=7.dp)
                        ){
                            Text(inst,fontSize=12.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,
                                color=if(isSel) Color.White else Color.White.copy(alpha=0.42f))
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── PRICE SELECTOR ────────────
                EpSectionLabel("Price Per Hour")
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier=Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF130A28))
                        .border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.15f),RoundedCornerShape(18.dp))
                        .padding(18.dp)
                ){
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween,Alignment.CenterVertically){
                        Text("Session Rate",fontSize=14.sp,color=Color.White.copy(alpha=0.6f))
                        Text("KSh $price / hr",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color.White)
                    }
                    Spacer(Modifier.height(14.dp))
                    Slider(
                        value=price.toFloat(),
                        onValueChange={price=it.toInt()},
                        valueRange=200f..5000f,
                        steps=47,
                        colors=SliderDefaults.colors(
                            thumbColor=Color(0xFF8B5CF6),
                            activeTrackColor=Color(0xFF8B5CF6),
                            inactiveTrackColor=Color.White.copy(alpha=0.1f)
                        )
                    )
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceBetween){
                        Text("KSh 200",fontSize=10.sp,color=Color.White.copy(alpha=0.25f))
                        Text("KSh 5,000",fontSize=10.sp,color=Color.White.copy(alpha=0.25f))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── AVAILABILITY TOGGLE ───────
                Row(
                    modifier=Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF130A28))
                        .border(1.dp,if(isAvailable) Color(0xFF10B981).copy(alpha=0.25f) else Color.White.copy(alpha=0.07f),RoundedCornerShape(16.dp))
                        .clickable{isAvailable=!isAvailable}
                        .padding(16.dp),
                    verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.SpaceBetween
                ){
                    Column{
                        Text("Availability",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White)
                        Text(if(isAvailable)"Clients can book you now" else "You appear as Busy",fontSize=12.sp,
                            color=if(isAvailable) Color(0xFF10B981) else Color.White.copy(alpha=0.35f))
                    }
                    Box(
                        modifier=Modifier.size(48.dp).clip(CircleShape)
                            .background(if(isAvailable) Color(0xFF10B981).copy(alpha=0.2f) else Color.White.copy(alpha=0.05f)),
                        contentAlignment=Alignment.Center
                    ){ Text(if(isAvailable)"🟢" else "🔴",fontSize=22.sp) }
                }

                Spacer(Modifier.height(24.dp))

                // ── SKILLS ────────────────────
                EpSectionLabel("Music Genres & Skills")
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                    skillOptions.forEach { skill ->
                        val isSel = skill in selectedSkills
                        Box(
                            modifier=Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if(isSel) Color(0xFF9F3A3A).copy(alpha=0.25f) else Color(0xFF130A28))
                                .border(if(isSel) 1.5.dp else 1.dp, if(isSel) Color(0xFFC46A6A) else Color.White.copy(alpha=0.09f),RoundedCornerShape(20.dp))
                                .clickable{ selectedSkills=if(isSel) selectedSkills-skill else selectedSkills+skill }
                                .padding(horizontal=12.dp,vertical=7.dp)
                        ){
                            Text(skill,fontSize=12.sp,fontWeight=if(isSel) FontWeight.Bold else FontWeight.Normal,
                                color=if(isSel) Color(0xFFC46A6A) else Color.White.copy(alpha=0.4f))
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── SAVE BUTTON ───────────────
                Box(
                    modifier=Modifier.fillMaxWidth().height(58.dp)
                        .scale(saveBtnScale)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if(artistName.isNotBlank())
                                Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6),Color(0xFF22D3EE).copy(alpha=0.7f)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF1A0E35),Color(0xFF130A28)))
                        )
                        .clickable(enabled=artistName.isNotBlank()){
                            saveScale=0.94f
                            // TODO: save to Firestore here
                            isSaved=true
                        },
                    contentAlignment=Alignment.Center
                ){
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.Check,null,tint=if(artistName.isNotBlank()) Color.White else Color.White.copy(alpha=0.2f),modifier=Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if(isSaved)"Profile Saved!" else "Save Profile",
                            fontSize=16.sp,fontWeight=FontWeight.Bold,letterSpacing=0.3.sp,
                            color=if(artistName.isNotBlank()) Color.White else Color.White.copy(alpha=0.22f)
                        )
                    }
                }

                if(artistName.isBlank()){
                    Spacer(Modifier.height(8.dp))
                    Text("Add your name to enable saving",fontSize=11.sp,color=Color.White.copy(alpha=0.22f),textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun EpSectionLabel(text: String) {
    Text(text, fontSize=15.sp, fontWeight=FontWeight.Bold, color=Color.White, letterSpacing=0.2.sp)
}

@Composable
fun EpTextField(value: String, onValueChange: (String)->Unit, label: String, placeholder: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedTextField(
        value=value, onValueChange=onValueChange,
        label={Text(label,color=Color.White.copy(alpha=0.35f))},
        placeholder={Text(placeholder,color=Color.White.copy(alpha=0.18f),fontSize=13.sp)},
        leadingIcon={Icon(icon,null,tint=Color(0xFF8B5CF6))},
        modifier=Modifier.fillMaxWidth(), singleLine=true,
        colors=OutlinedTextFieldDefaults.colors(
            focusedTextColor=Color.White,unfocusedTextColor=Color.White,
            focusedBorderColor=Color(0xFF8B5CF6),unfocusedBorderColor=Color.White.copy(alpha=0.1f),
            cursorColor=Color(0xFF8B5CF6),focusedLabelColor=Color(0xFF8B5CF6),
            focusedContainerColor=Color(0xFF130A28),unfocusedContainerColor=Color(0xFF130A28)
        ),
        shape=RoundedCornerShape(14.dp)
    )
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun EditProfileScreenPreview() {
    InstruxaTheme { EditProfileScreen(navController = rememberNavController()) }
}