package com.clementvexegon.instruxa.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clementvexegon.instruxa.ui.screens.about.AboutScreen
import com.clementvexegon.instruxa.ui.screens.auth.login.LoginScreen
import com.clementvexegon.instruxa.ui.screens.auth.register.RegisterScreen
import com.clementvexegon.instruxa.ui.screens.booking.BookingConfirmScreen
import com.clementvexegon.instruxa.ui.screens.booking.BookingScreen
import com.clementvexegon.instruxa.ui.screens.explore.ExploreScreen
import com.clementvexegon.instruxa.ui.screens.home.HomeScreen
import com.clementvexegon.instruxa.ui.screens.mybookings.MyBookingsScreen
import com.clementvexegon.instruxa.ui.screens.notifications.NotificationsScreen
import com.clementvexegon.instruxa.ui.screens.onboarding.OnboardingScreen
import com.clementvexegon.instruxa.ui.screens.profile.ArtistProfileScreen
import com.clementvexegon.instruxa.ui.screens.profile.EditProfileScreen
import com.clementvexegon.instruxa.ui.screens.reviews.ReviewScreen
import com.clementvexegon.instruxa.ui.screens.settings.SettingsScreen
import com.clementvexegon.instruxa.ui.screens.splash.SplashScreen
import com.clementvexegon.instruxa.ui.screens.userprofile.UserProfileScreen

// ─────────────────────────────────────────
//  INSTRUXA — App Navigation Host
//
//  Full flow:
//  Splash → Onboarding → Login/Register
//  → Home → ArtistProfile → Booking
//  → BookingConfirm → Reviews
//  → Notifications → UserProfile
//  → Explore → MyBookings
//  → Settings → About
// ─────────────────────────────────────────

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        modifier         = modifier
    ) {

        // 1. SPLASH
        composable(ROUT_SPLASH) {
            SplashScreen(navController = navController)
        }

        // 2. ONBOARDING
        composable(ROUT_ONBOARDING) {
            OnboardingScreen(navController = navController)
        }

        // 3. LOGIN
        composable(ROUT_LOGIN) {
            LoginScreen(navController = navController)
        }

        // 4. REGISTER
        composable(ROUT_REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 5. HOME
        composable(ROUT_HOME) {
            HomeScreen(navController = navController)
        }

        // 6. ARTIST PROFILE
        composable(ROUT_ARTIST_PROFILE) {
            ArtistProfileScreen(navController = navController)
        }

        // 7. EDIT PROFILE
        composable(ROUT_EDIT_PROFILE) {
            EditProfileScreen(navController = navController)
        }

        // 8. BOOKING
        composable(ROUT_BOOKING) {
            BookingScreen(navController = navController)
        }

        // 9. BOOKING CONFIRM
        composable(ROUT_BOOKING_CONFIRM) {
            BookingConfirmScreen(navController = navController)
        }

        // 10. REVIEWS
        composable(ROUT_REVIEWS) {
            ReviewScreen(navController = navController)
        }

        // 11. NOTIFICATIONS
        composable(ROUT_NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }

        // 12. USER PROFILE
        composable(ROUT_USER_PROFILE) {
            UserProfileScreen(navController = navController)
        }

        // 13. EXPLORE
        composable(ROUT_EXPLORE) {
            ExploreScreen(navController = navController)
        }

        // 14. MY BOOKINGS
        composable(ROUT_MY_BOOKINGS) {
            MyBookingsScreen(navController = navController)
        }

        // 15. SETTINGS
        composable(ROUT_SETTINGS) {
            SettingsScreen(navController = navController)
        }

        // 16. ABOUT (CvX cornerstone)
        composable(ROUT_ABOUT) {
            AboutScreen(navController = navController)
        }
    }
}