package com.clementvexegon.instruxa.ui.screens.settings

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
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.navigation.ROUT_ABOUT
import com.clementvexegon.instruxa.navigation.ROUT_LOGIN
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.*

@Composable
fun SettingsScreen(navController: NavController) {

    // Toggle states
    var notifBookings    by remember { mutableStateOf(true) }
    var notifPayments    by remember { mutableStateOf(true) }
    var notifReviews     by remember { mutableStateOf(true) }
    var notifPromos      by remember { mutableStateOf(false) }
    var locationEnabled  by remember { mutableStateOf(true) }
    var biometricLogin   by remember { mutableStateOf(false) }
    var darkMode         by remember { mutableStateOf(true) }

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) { contentAlpha.animateTo(1f, tween(500)) }

    val inf = rememberInfiniteTransition(label = "settings")
    val glowPulse by inf.animateFloat(0.3f,0.75f, infiniteRepeatable(tween(2800,easing=FastOutSlowInEasing),RepeatMode.Reverse),label="g")

    Box(modifier=Modifier.fillMaxSize().background(Color(0xFF080612))) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(brush=Brush.radialGradient(listOf(Color(0xFF1E0A3A).copy(alpha=glowPulse*0.55f),Color.Transparent),Offset(size.width*0.5f,size.height*0.12f),size.minDimension*0.65f))
            val sp=52.dp.toPx()
            for(c in 0..(size.width/sp).toInt()+2) for(r in 0..(size.height/sp).toInt()+2)
                drawCircle(Color(0xFF6D28D9).copy(alpha=0.06f),1.dp.toPx(),Offset(c*sp,r*sp))
        }

        Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).alpha(contentAlpha.value)) {

            // Header
            Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(top=52.dp,bottom=24.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Box(modifier=Modifier.size(42.dp).clip(CircleShape).background(Color.White.copy(alpha=0.08f)).clickable{navController.popBackStack()},contentAlignment=Alignment.Center){
                    Icon(Icons.Default.ArrowBack,null,tint=Color.White,modifier=Modifier.size(20.dp))
                }
                Text("Settings",fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color.White)
                Box(Modifier.size(42.dp))
            }

            Column(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp)) {

                // Notifications section
                SettSection("Notifications 🔔")
                SettToggle("Booking updates","When artists confirm or change",Color(0xFF8B5CF6),notifBookings){notifBookings=it}
                Spacer(Modifier.height(8.dp))
                SettToggle("Payment alerts","M-Pesa transactions & receipts",Color(0xFF10B981),notifPayments){notifPayments=it}
                Spacer(Modifier.height(8.dp))
                SettToggle("Review reminders","Rate sessions after completion",Color(0xFFF59E0B),notifReviews){notifReviews=it}
                Spacer(Modifier.height(8.dp))
                SettToggle("Promotions","Deals and featured artists near you",Color(0xFF22D3EE),notifPromos){notifPromos=it}

                Spacer(Modifier.height(22.dp))

                // Privacy & Security
                SettSection("Privacy & Security 🔒")
                SettToggle("Location services","Find artists near your area",Color(0xFF9F3A3A),locationEnabled){locationEnabled=it}
                Spacer(Modifier.height(8.dp))
                SettToggle("Biometric login","Use fingerprint to sign in faster",Color(0xFF8B5CF6),biometricLogin){biometricLogin=it}
                Spacer(Modifier.height(8.dp))
                SettToggle("Dark mode","Deep space theme (recommended)",Color(0xFF6D28D9),darkMode){darkMode=it}

                Spacer(Modifier.height(22.dp))

                // Account
                SettSection("Account ⚙️")
                listOf(
                    Triple(Icons.Default.Lock,"Change Password",Color(0xFF8B5CF6)),
                    Triple(Icons.Default.Language,"Language · English",Color(0xFF22D3EE)),
                    Triple(Icons.Default.Storage,"Clear Cache",Color(0xFFF59E0B)),
                    Triple(Icons.Default.Download,"Export My Data",Color(0xFF10B981)),
                    Triple(Icons.Default.Info,"About Instruxa",Color(0xFFA78BFA))
                ).forEach { (icon,label,color) ->
                    Row(
                        modifier=Modifier.fillMaxWidth().padding(vertical=4.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,Color.White.copy(alpha=0.05f),RoundedCornerShape(14.dp))
                            .clickable{if(label.contains("About")) navController.navigate(ROUT_ABOUT)}
                            .padding(14.dp),
                        verticalAlignment=Alignment.CenterVertically
                    ){
                        Box(modifier=Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha=0.15f)),contentAlignment=Alignment.Center){
                            Icon(icon,null,tint=color,modifier=Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(label,fontSize=14.sp,color=Color.White,modifier=Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight,null,tint=Color.White.copy(alpha=0.2f),modifier=Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Danger zone
                SettSection("Danger Zone ⚠️")
                Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFEF4444).copy(alpha=0.07f)).border(1.dp,Color(0xFFEF4444).copy(alpha=0.22f),RoundedCornerShape(14.dp)).clickable{navController.navigate(ROUT_LOGIN){popUpTo(0){inclusive=true}}}.padding(16.dp),
                    horizontalArrangement=Arrangement.Center, verticalAlignment=Alignment.CenterVertically){
                    Icon(Icons.Default.Logout,null,tint=Color(0xFFEF4444),modifier=Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color(0xFFEF4444))
                }
                Spacer(Modifier.height(10.dp))
                Row(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFEF4444).copy(alpha=0.04f)).border(1.dp,Color(0xFFEF4444).copy(alpha=0.1f),RoundedCornerShape(14.dp)).padding(16.dp),
                    horizontalArrangement=Arrangement.Center, verticalAlignment=Alignment.CenterVertically){
                    Icon(Icons.Default.DeleteForever,null,tint=Color(0xFFEF4444).copy(alpha=0.5f),modifier=Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Account",fontSize=13.sp,color=Color(0xFFEF4444).copy(alpha=0.5f))
                }

                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun SettSection(title: String) {
    Text(title, fontSize=13.sp, fontWeight=FontWeight.Bold, color=Color.White.copy(alpha=0.5f), letterSpacing=0.5.sp, modifier=Modifier.padding(bottom=10.dp))
}

@Composable
fun SettToggle(title: String, subtitle: String, color: Color, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF130A28)).border(1.dp,if(checked) color.copy(alpha=0.2f) else Color.White.copy(alpha=0.05f),RoundedCornerShape(14.dp)).padding(14.dp),
        verticalAlignment=Alignment.CenterVertically
    ){
        Box(modifier=Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(if(checked) color.copy(alpha=0.18f) else Color.White.copy(alpha=0.04f)),contentAlignment=Alignment.Center){
            Box(modifier=Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(if(checked) color else Color.White.copy(alpha=0.2f)))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier=Modifier.weight(1f)){
            Text(title,fontSize=14.sp,fontWeight=FontWeight.Medium,color=Color.White)
            Text(subtitle,fontSize=11.sp,color=Color.White.copy(alpha=0.38f))
        }
        Switch(checked=checked, onCheckedChange=onToggle,
            colors=SwitchDefaults.colors(checkedThumbColor=Color.White,checkedTrackColor=color,uncheckedThumbColor=Color.White.copy(alpha=0.5f),uncheckedTrackColor=Color.White.copy(alpha=0.1f),uncheckedBorderColor=Color.Transparent))
    }
}

@Preview(showBackground=true, showSystemUi=true)
@Composable
fun SettingsScreenPreview() { InstruxaTheme { SettingsScreen(rememberNavController()) } }