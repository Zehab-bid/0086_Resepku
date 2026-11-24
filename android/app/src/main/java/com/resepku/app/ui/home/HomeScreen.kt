package com.resepku.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.resepku.app.ui.auth.AuthViewModel

/**
 * HomeScreen — placeholder untuk fitur utama (minggu 2+).
 *
 * Saat ini hanya menampilkan:
 * - Sapaan dengan nama user
 * - Role user (admin/user)
 * - Tombol logout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    // Amati data user dari DataStore via ViewModel
    val namaUser by viewModel.namaUser.collectAsState(initial = null)
    val role     by viewModel.role.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ResepKu 🍽️") },
                actions = {
                    // Tombol logout di toolbar
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "Halo, ${namaUser ?: "..."}! 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge peran user
            SuggestionChip(
                onClick = {},
                label   = {
                    Text(
                        text  = "Peran: ${role?.uppercase() ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info bahwa fitur utama akan datang
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text  = "🚧 Fitur dalam pengembangan",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text  = "Daftar resep, kategori, koleksi, dan fitur lainnya akan hadir di minggu 2+.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol logout cadangan (selain di toolbar)
            OutlinedButton(
                onClick  = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}
