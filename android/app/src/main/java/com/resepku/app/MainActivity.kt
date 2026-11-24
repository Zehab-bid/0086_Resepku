package com.resepku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.resepku.app.navigation.AppNavGraph
import com.resepku.app.ui.theme.ResepkuTheme

/**
 * MainActivity — entry point aplikasi.
 *
 * Hanya bertanggung jawab untuk:
 * 1. Menyiapkan tema aplikasi (ResepkuTheme)
 * 2. Memanggil AppNavGraph (seluruh navigasi dikelola di sana)
 *
 * Tidak ada logika bisnis di sini.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResepkuTheme {
                AppNavGraph()
            }
        }
    }
}
