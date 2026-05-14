package com.clementvexegon.instruxa.ui.screens.auth.register

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
import com.clementvexegon.instruxa.navigation.ROUT_LOGIN
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.*

val registerInstrumentOptions = listOf(
    "🎸 Guitar","🎹 Piano","🥁 Drums","🎻 Violin",
    "🎺 Trumpet","🎷 Saxophone","🎙️ Vocals","🎧 DJ / Producer","🪗 Accordion"
)

fun passwordStrength(password: String): Pair<Float, Color> = when {
    password.length < 4                                            -> 0.15f to Color(0xFFEF4444)
    password.length < 7                                            -> 0.40f to Color(0xFFF59E0B)
    password.length < 10                                           -> 0.70f to Color(0xFF8B5CF6)
    password.any { it.isUpperCase() } && password.any { it.isDigit() } -> 1.00f to Color(0xFF10B981)
    else                                                           -> 0.85f to Color(0xFF22D3EE)
}

@Composable
fun RegisterScreen(navController: NavController) {

    var fullName           by remember { mutableStateOf("") }
    var email              by remember { mutableStateOf("") }
    var password           by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }
    var showPassword       by remember { mutableStateOf(false) }
    var showConfirm        by remember { mutableStateOf(false) }
    var isArtist           by remember { mutableStateOf(false) }
    var selectedInstrument by remember { mutableStateOf("") }
    var isLoading          by remember { mutableStateOf(false) }
    var firebaseError      by remember { mutableStateOf("") }
    var nameError          by remember { mutableStateOf("") }
    var emailError         by remember { mutableStateOf("") }
    var passError          by remember { mutableStateOf("") }
    var confirmError       by remember { mutableStateOf("") }

    val cardAlpha = remember { Animatable(0f) }
    val cardSlide = remember { Animatable(100f) }
    val logoScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        kotlinx.coroutines.delay(100)
        cardAlpha.animateTo(1f, tween(500))
        cardSlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    val inf = rememberInfiniteTransition(label = "reg")
    val glowPulse    by inf.animateFloat(0.3f,0.9f, infiniteRepeatable(tween(2500,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")
    val particleAnim by inf.animateFloat(0f,1f, infiniteRepeatable(tween(6000,easing=LinearEasing),RepeatMode.Restart),label="p")
    val helixAnim    by inf.animateFloat(0f,(2f*PI).toFloat(), infiniteRepeatable(tween(4000,easing=LinearEasing),RepeatMode.Restart),label="h")
    val waveAnim     by inf.animateFloat(0f,(2f*PI).toFloat(), infiniteRepeatable(tween(1000,easing=LinearEasing),RepeatMode.Restart),label="w")
    val btnScale     by animateFloatAsState(if(isLoading) 0.96f else 1f, spring(Spring.DampingRatioMediumBouncy),label="btn")

    val (strengthVal, strengthColor) = passwordStrength(password)
    val strengthAnim by animateFloatAsState(if(password.isEmpty()) 0f else strengthVal, tween(400),label="str")

    val particles = remember {
        listOf(Offset(0.08f,0.25f),Offset(0.92f,0.18f),Offset(0.18f,0.75f),Offset(0.82f,0.72f),
            Offset(0.48f,0.05f),Offset(0.55f,0.95f),Offset(0.72f,0.38f),Offset(0.28f,0.48f),
            Offset(0.88f,0.55f),Offset(0.12f,0.62f))
    }

    Box(modifier=Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF2D1060).copy(alpha=glowPulse*0.65f),Color.Transparent),Offset(size.width*0.5f,size.height*0.15f),size.minDimension*0.7f))
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF3A0A0A).copy(alpha=0.45f),Color.Transparent),Offset(size.width*0.15f,size.height*0.9f),size.minDimension*0.4f))
            val sp=44.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.09f),1.4.dp.toPx(),Offset(c*sp,r*sp))
            // DNA Helix
            val helixX=size.width*0.92f; val helixH=size.height; val amp=18.dp.toPx(); val period=80.dp.toPx()
            for(step in 1..60){
                val t=step.toFloat()/60; val prevT=(step-1).toFloat()/60
                val y=helixH*t; val prevY=helixH*prevT
                val x1=helixX+amp*sin(helixAnim+t*2f*PI.toFloat()*(helixH/period))
                val px1=helixX+amp*sin(helixAnim+prevT*2f*PI.toFloat()*(helixH/period))
                val x2=helixX-amp*sin(helixAnim+t*2f*PI.toFloat()*(helixH/period))
                val px2=helixX-amp*sin(helixAnim+prevT*2f*PI.toFloat()*(helixH/period))
                val a=0.25f+0.15f*sin(t*4f*PI.toFloat())
                drawLine(Color(0xFF8B5CF6).copy(alpha=a),Offset(px1,prevY),Offset(x1,y),1.5.dp.toPx())
                drawLine(Color(0xFF22D3EE).copy(alpha=a*0.7f),Offset(px2,prevY),Offset(x2,y),1.5.dp.toPx())
                if(step%5==0){ drawLine(Color(0xFF8B5CF6).copy(alpha=a*0.5f),Offset(x1,y),Offset(x2,y),0.8.dp.toPx()); drawCircle(Color(0xFF8B5CF6).copy(alpha=a),2.dp.toPx(),Offset(x1,y)); drawCircle(Color(0xFF22D3EE).copy(alpha=a*0.7f),2.dp.toPx(),Offset(x2,y)) }
            }
            // Particles
            particles.forEachIndexed { i,pos ->
                val prog=(particleAnim+i*(1f/particles.size))%1f
                val x=pos.x*size.width*0.85f+sin(prog*2f*PI.toFloat()+i*1.3f)*18.dp.toPx()
                val y=size.height*(1f-prog)
                val a=sin(prog*PI.toFloat()).coerceIn(0f,1f)*0.65f
                drawCircle(when(i%3){0->Color(0xFF8B5CF6).copy(alpha=a);1->Color(0xFF22D3EE).copy(alpha=a*0.75f);else->Color(0xFFC46A6A).copy(alpha=a*0.6f)},(1.5f+i%3).dp.toPx(),Offset(x,y))
            }
        }

        Column(
            modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=26.dp),
            horizontalAlignment=Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // Logo
            Box(modifier=Modifier.size(70.dp).scale(logoScale.value), contentAlignment=Alignment.Center) {
                Canvas(Modifier.fillMaxSize()){ drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF8B5CF6).copy(alpha=glowPulse*0.5f),Color.Transparent))) }
                Box(modifier=Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF9F3A3A),Color(0xFF6D28D9)))), contentAlignment=Alignment.Center){
                    Text("IX",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color.White)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Create Account",fontSize=28.sp,fontWeight=FontWeight.Bold,color=Color.White,letterSpacing=(-0.5).sp)
            Text("Join the Instruxa community",fontSize=13.sp,color=Color.White.copy(alpha=0.4f))
            Spacer(Modifier.height(24.dp))

            // Artist / Client Toggle
            Box(modifier=Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.25f),RoundedCornerShape(14.dp)).alpha(cardAlpha.value)){
                Box(modifier=Modifier.fillMaxHeight().fillMaxWidth(0.5f).padding(4.dp).clip(RoundedCornerShape(10.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF8B5CF6),Color(0xFF6D28D9)))).align(if(isArtist) Alignment.CenterEnd else Alignment.CenterStart))
                Row(modifier=Modifier.fillMaxSize()) {
                    Box(modifier=Modifier.weight(1f).fillMaxHeight().clickable{isArtist=false}, contentAlignment=Alignment.Center){ Row(verticalAlignment=Alignment.CenterVertically){ Text("🎧",fontSize=15.sp); Spacer(Modifier.width(6.dp)); Text("Client",fontSize=14.sp,fontWeight=if(!isArtist) FontWeight.Bold else FontWeight.Normal,color=if(!isArtist) Color.White else Color.White.copy(alpha=0.4f)) } }
                    Box(modifier=Modifier.weight(1f).fillMaxHeight().clickable{isArtist=true}, contentAlignment=Alignment.Center){ Row(verticalAlignment=Alignment.CenterVertically){ Text("🎸",fontSize=15.sp); Spacer(Modifier.width(6.dp)); Text("Artist",fontSize=14.sp,fontWeight=if(isArtist) FontWeight.Bold else FontWeight.Normal,color=if(isArtist) Color.White else Color.White.copy(alpha=0.4f)) } }
                }
            }
            Spacer(Modifier.height(20.dp))

            // Card
            Column(modifier=Modifier.fillMaxWidth().offset(y=cardSlide.value.dp).alpha(cardAlpha.value).clip(RoundedCornerShape(24.dp)).background(Brush.verticalGradient(listOf(Color(0xFF1A0E35),Color(0xFF110A22)))).border(1.dp,Color(0xFF8B5CF6).copy(alpha=0.18f),RoundedCornerShape(24.dp)).padding(24.dp)) {
                Text(if(isArtist)"Artist Registration" else "Client Registration",fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color.White)
                Text(if(isArtist)"Set up your profile to start getting gigs" else "Sign up to start hiring instrumentalists",fontSize=12.sp,color=Color.White.copy(alpha=0.4f),modifier=Modifier.padding(top=3.dp))
                Spacer(Modifier.height(20.dp))

                if(firebaseError.isNotEmpty()){
                    Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFEF4444).copy(alpha=0.12f)).padding(12.dp),verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.Error,null,tint=Color(0xFFEF4444),modifier=Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(firebaseError,fontSize=12.sp,color=Color(0xFFEF4444))
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Fields
                RegField(fullName,{fullName=it;nameError=""},"Full Name",Icons.Default.Person,nameError)
                Spacer(Modifier.height(12.dp))
                RegField(email,{email=it;emailError=""},"Email address",Icons.Default.Email,emailError,KeyboardType.Email)
                Spacer(Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value=password, onValueChange={password=it;passError=""},
                    label={Text("Password",color=Color.White.copy(alpha=0.4f))},
                    leadingIcon={Icon(Icons.Default.Lock,null,tint=Color(0xFF8B5CF6))},
                    trailingIcon={IconButton(onClick={showPassword=!showPassword}){Icon(if(showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,null,tint=Color.White.copy(alpha=0.4f))}},
                    visualTransformation=if(showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier=Modifier.fillMaxWidth(), singleLine=true,
                    keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),
                    isError=passError.isNotEmpty(),
                    supportingText={if(passError.isNotEmpty()) Text(passError,color=Color(0xFFEF4444),fontSize=12.sp)},
                    colors=RegFieldColors(), shape=RoundedCornerShape(14.dp)
                )
                if(password.isNotEmpty()){
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){
                        Box(modifier=Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.White.copy(alpha=0.1f))){
                            Box(modifier=Modifier.fillMaxHeight().fillMaxWidth(strengthAnim).clip(RoundedCornerShape(2.dp)).background(strengthColor))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(when{strengthVal<=0.15f->"Weak";strengthVal<=0.40f->"Fair";strengthVal<=0.70f->"Good";strengthVal<=0.85f->"Strong";else->"Very Strong"},fontSize=11.sp,color=strengthColor,fontWeight=FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Confirm password
                OutlinedTextField(
                    value=confirmPassword, onValueChange={confirmPassword=it;confirmError=""},
                    label={Text("Confirm Password",color=Color.White.copy(alpha=0.4f))},
                    leadingIcon={Icon(Icons.Default.Lock,null,tint=Color(0xFF8B5CF6))},
                    trailingIcon={IconButton(onClick={showConfirm=!showConfirm}){Icon(if(showConfirm) Icons.Default.Visibility else Icons.Default.VisibilityOff,null,tint=Color.White.copy(alpha=0.4f))}},
                    visualTransformation=if(showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier=Modifier.fillMaxWidth(), singleLine=true,
                    keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),
                    isError=confirmError.isNotEmpty(),
                    supportingText={if(confirmError.isNotEmpty()) Text(confirmError,color=Color(0xFFEF4444),fontSize=12.sp)},
                    colors=RegFieldColors(), shape=RoundedCornerShape(14.dp)
                )

                // Artist-only: instrument selector
                if(isArtist){
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.MusicNote,null,tint=Color(0xFF8B5CF6),modifier=Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Your Primary Instrument",fontSize=13.sp,fontWeight=FontWeight.Medium,color=Color.White.copy(alpha=0.7f))
                    }
                    Spacer(Modifier.height(10.dp))
                    registerInstrumentOptions.chunked(3).forEach { rowItems ->
                        Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            rowItems.forEach { inst ->
                                val sel=selectedInstrument==inst
                                Box(modifier=Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if(sel) Brush.horizontalGradient(listOf(Color(0xFF9F3A3A),Color(0xFF8B5CF6))) else Brush.horizontalGradient(listOf(Color(0xFF1A0E35),Color(0xFF1A0E35)))).border(1.dp,if(sel) Color(0xFF8B5CF6) else Color.White.copy(alpha=0.1f),RoundedCornerShape(10.dp)).clickable{selectedInstrument=inst}.padding(vertical=8.dp,horizontal=4.dp), contentAlignment=Alignment.Center){
                                    Text(inst,fontSize=11.sp,color=if(sel) Color.White else Color.White.copy(alpha=0.5f),textAlign=TextAlign.Center,fontWeight=if(sel) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── REGISTER BUTTON — Firebase Auth wired ──
                Button(
                    onClick = {
                        var valid = true
                        if(fullName.isBlank()){ nameError="Name is required"; valid=false }
                        if(email.isBlank()||!email.contains("@")){ emailError="Valid email required"; valid=false }
                        if(password.length<6){ passError="At least 6 characters"; valid=false }
                        if(confirmPassword!=password){ confirmError="Passwords don't match"; valid=false }
                        if(isArtist&&selectedInstrument.isEmpty()){ passError="Select your instrument"; valid=false }
                        if(valid){
                            isLoading=true
                            firebaseError=""
                            // ── CREATE FIREBASE ACCOUNT ───────────────────
                            FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(email.trim(), password)
                                .addOnSuccessListener { result ->
                                    val uid = result.user?.uid ?: return@addOnSuccessListener
                                    // ── SAVE USER TYPE TO FIRESTORE ───────────
                                    // This is what makes Artist vs Client different
                                    val userDoc = hashMapOf(
                                        "uid"        to uid,
                                        "name"       to fullName.trim(),
                                        "email"      to email.trim(),
                                        "userType"   to if(isArtist) "artist" else "client",
                                        "instrument" to if(isArtist) selectedInstrument else "",
                                        "bio"        to "",
                                        "price"      to 0,
                                        "rating"     to 0f,
                                        "reviews"    to 0,
                                        "gigs"       to 0,
                                        "isAvailable" to true,
                                        "location"   to "Nairobi, Kenya",
                                        "photoUrl"   to "",
                                        "createdAt"  to System.currentTimeMillis()
                                    )
                                    FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(uid)
                                        .set(userDoc)
                                        .addOnSuccessListener {
                                            isLoading=false
                                            navController.navigate(ROUT_HOME){
                                                popUpTo(0){inclusive=true}
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading=false
                                            firebaseError="Account created but profile save failed: ${e.message}"
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isLoading=false
                                    firebaseError=when{
                                        e.message?.contains("email")   ==true->"Email already in use"
                                        e.message?.contains("network") ==true->"No internet connection"
                                        else->e.message?:"Registration failed"
                                    }
                                }
                        }
                    },
                    modifier=Modifier.fillMaxWidth().height(54.dp).scale(btnScale),
                    shape=RoundedCornerShape(14.dp),
                    colors=ButtonDefaults.buttonColors(containerColor=Color.Transparent),
                    contentPadding=PaddingValues(0.dp)
                ){
                    Box(modifier=Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF6D28D9),Color(0xFF9F3A3A))),RoundedCornerShape(14.dp)), contentAlignment=Alignment.Center){
                        if(isLoading) CircularProgressIndicator(modifier=Modifier.size(24.dp),color=Color.White,strokeWidth=2.5.dp)
                        else Text("Create Account  →",fontSize=15.sp,fontWeight=FontWeight.Bold,color=Color.White,letterSpacing=0.5.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Equalizer divider
            Box(modifier=Modifier.alpha(cardAlpha.value).height(20.dp).width(100.dp)){
                Canvas(Modifier.fillMaxSize()){
                    val count=9; val gap=size.width/(count*2f-1); val bw=gap*0.7f
                    for(i in 0 until count){ val bh=(0.3f+0.7f* abs(sin(waveAnim+i*0.7f)))*size.height; drawRoundRect(Color(0xFF8B5CF6).copy(alpha=0.45f),Offset(i*gap*2f,size.height-bh),Size(bw,bh),CornerRadius(bw/2f)) }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(modifier=Modifier.alpha(cardAlpha.value), horizontalArrangement=Arrangement.Center, verticalAlignment=Alignment.CenterVertically){
                Text("Already have an account?  ",fontSize=14.sp,color=Color.White.copy(alpha=0.4f))
                Text("Sign In",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color(0xFF8B5CF6),modifier=Modifier.clickable{navController.navigate(ROUT_LOGIN)})
            }
            Spacer(Modifier.height(44.dp))
        }
    }
}

@Composable
fun RegField(value: String, onValueChange: (String)->Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, error: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value=value,onValueChange=onValueChange,label={Text(label,color=Color.White.copy(alpha=0.4f))},leadingIcon={Icon(icon,null,tint=Color(0xFF8B5CF6))},modifier=Modifier.fillMaxWidth(),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=keyboardType),isError=error.isNotEmpty(),supportingText={if(error.isNotEmpty()) Text(error,color=Color(0xFFEF4444),fontSize=12.sp)},colors=RegFieldColors(),shape=RoundedCornerShape(14.dp))
}

@Composable
fun RegFieldColors() = OutlinedTextFieldDefaults.colors(focusedTextColor=Color.White,unfocusedTextColor=Color.White,focusedBorderColor=Color(0xFF8B5CF6),unfocusedBorderColor=Color.White.copy(alpha=0.12f),cursorColor=Color(0xFF8B5CF6),focusedLabelColor=Color(0xFF8B5CF6),errorBorderColor=Color(0xFFEF4444),focusedContainerColor=Color(0xFF130A28),unfocusedContainerColor=Color(0xFF130A28))

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun RegisterScreenPreview() { InstruxaTheme { RegisterScreen(rememberNavController()) } }