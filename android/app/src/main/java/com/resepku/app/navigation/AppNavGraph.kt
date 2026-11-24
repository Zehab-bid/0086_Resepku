package com.resepku.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resepku.app.ui.auth.AuthViewModel
import com.resepku.app.ui.auth.LoginScreen
import com.resepku.app.ui.auth.RegisterScreen
import com.resepku.app.ui.home.HomeScreen

/**
 * AppNavGraph — graf navigasi utama aplikasi.
 *
 * Start destination ditentukan secara dinamis:
 * - Jika ada token tersimpan → langsung ke Home (auto-login)
 * - Jika tidak → mulai dari Login
 *
 * Navigasi menggunakan NavHostController dari Navigation Compose.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // ViewModel dibuat di sini (scope NavGraph) agar dibagi antara Login dan Register
    val authViewModel: AuthViewModel = viewModel()

    // Tentukan start destination berdasarkan token tersimpan
    var startDest by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDest = if (authViewModel.isLoggedIn()) Routes.HOME else Routes.LOGIN
    }

    // Tunggu sampai start destination ditentukan
    if (startDest == null) return

    NavHost(
        navController    = navController,
        startDestination = startDest!!
    ) {
        // ===== Login =====
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel          = authViewModel,
                onLoginSuccess     = {
                    // Hapus Login dari back stack sehingga Back tidak kembali ke Login
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ===== Register =====
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel         = authViewModel,
                onRegisterSuccess = {
                    // Setelah register, kembali ke Login agar user login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ===== Home =====
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = authViewModel,
                onLogout  = {
                    // Logout: hapus Home dari back stack, kembali ke Login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
