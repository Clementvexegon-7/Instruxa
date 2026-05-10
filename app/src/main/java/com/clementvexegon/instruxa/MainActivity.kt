package com.clementvexegon.instruxa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clementvexegon.instruxa.data.network.CloudinaryConfig
import com.clementvexegon.instruxa.navigation.AppNavHost
import com.clementvexegon.instruxa.ui.theme.InstruxaTheme

// ─────────────────────────────────────────
//  INSTRUXA — Main Activity
//  This is the single entry point of the app.
//  It launches the Instruxa theme and hands
//  control over to AppNavHost which manages
//  all screen navigation from here.
// ─────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        CloudinaryConfig.init(this)
        super.onCreate(savedInstanceState)

        // Makes the app use the full screen
        // (content goes behind status/nav bars)
        enableEdgeToEdge()

        setContent {
            // Apply Instruxa colors and typography globally
            InstruxaTheme {

                // Hand control to the navigation system
                // AppNavHost decides which screen to show first
                AppNavHost()
            }
        }
    }
}