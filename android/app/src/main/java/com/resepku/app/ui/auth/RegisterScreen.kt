package com.resepku.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.resepku.app.util.Validator

/**
 * RegisterScreen — layar pendaftaran akun baru.
 *
 * Validasi dilakukan per field (saat user meninggalkan field),
 * plus validasi keseluruhan saat tombol Daftar ditekan.
 * Semua logika validasi ada di Validator.kt, bukan di sini.
 *
 * @param viewModel AuthViewModel bersama
 * @param onRegisterSuccess dipanggil setelah register sukses → kembali ke Login
 * @param onNavigateToLogin dipanggil saat user klik link Login
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // State input
    var nama    by remember { mutableStateOf("") }
    var email   by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm  by remember { mutableStateOf("") }

    // Toggle lihat password
    var showPass    by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    // Error per field (ditampilkan di bawah masing-masing field)
    var errorNama    by remember { mutableStateOf<String?>(null) }
    var errorEmail   by remember { mutableStateOf<String?>(null) }
    var errorPass    by remember { mutableStateOf<String?>(null) }
    var errorConfirm by remember { mutableStateOf<String?>(null) }

    // Reaksi terhadap state dari ViewModel
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // scroll jika keyboard muncul
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ===== Judul =====
        Text(
            text  = "Buat Akun Baru",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text  = "Bergabunglah dan simpan resep favoritmu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ===== Field Nama =====
        OutlinedTextField(
            value         = nama,
            onValueChange = {
                nama = it
                // Validasi saat user mengetik (setelah ada input)
                errorNama = if (it.isNotEmpty() && !Validator.isValidNama(it))
                    "Nama minimal 2 karakter" else null
            },
            label        = { Text("Nama Lengkap") },
            leadingIcon  = { Icon(Icons.Default.Person, contentDescription = null) },
            isError      = errorNama != null,
            supportingText = { if (errorNama != null) Text(errorNama!!) },  // pesan error di bawah field
            singleLine   = true,
            modifier     = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== Field Email =====
        OutlinedTextField(
            value         = email,
            onValueChange = {
                email = it
                errorEmail = if (it.isNotEmpty() && !Validator.isValidEmail(it))
                    "Format email tidak valid" else null
            },
            label           = { Text("Email") },
            leadingIcon     = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError         = errorEmail != null,
            supportingText  = { if (errorEmail != null) Text(errorEmail!!) },
            singleLine      = true,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== Field Password =====
        OutlinedTextField(
            value         = password,
            onValueChange = {
                password = it
                errorPass = if (it.isNotEmpty() && !Validator.isValidPassword(it))
                    "Password minimal 8 karakter" else null
                // Re-validasi konfirmasi saat password berubah
                errorConfirm = if (confirm.isNotEmpty() && !Validator.isPasswordMatch(it, confirm))
                    "Konfirmasi password tidak cocok" else null
            },
            label        = { Text("Password") },
            leadingIcon  = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError              = errorPass != null,
            supportingText       = { if (errorPass != null) Text(errorPass!!) },
            singleLine           = true,
            modifier             = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== Field Konfirmasi Password =====
        OutlinedTextField(
            value         = confirm,
            onValueChange = {
                confirm = it
                errorConfirm = if (it.isNotEmpty() && !Validator.isPasswordMatch(password, it))
                    "Konfirmasi password tidak cocok" else null
            },
            label        = { Text("Konfirmasi Password") },
            leadingIcon  = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showConfirm = !showConfirm }) {
                    Icon(
                        imageVector = if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError              = errorConfirm != null,
            supportingText       = { if (errorConfirm != null) Text(errorConfirm!!) },
            singleLine           = true,
            modifier             = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== Error dari ViewModel (error server) =====
        if (uiState is AuthUiState.Error) {
            Text(
                text     = (uiState as AuthUiState.Error).message,
                color    = MaterialTheme.colorScheme.error,
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== Tombol Daftar =====
        Button(
            onClick = {
                // Validasi semua field sebelum kirim
                val hasFieldError = listOf(
                    errorNama, errorEmail, errorPass, errorConfirm
                ).any { it != null }

                val hasEmptyField = listOf(nama, email, password, confirm).any { it.isBlank() }

                if (!hasFieldError && !hasEmptyField) {
                    viewModel.register(nama, email, password, confirm)
                } else {
                    // Tampilkan error pada field yang kosong
                    if (nama.isBlank()) errorNama = "Nama tidak boleh kosong"
                    if (email.isBlank()) errorEmail = "Email tidak boleh kosong"
                    if (password.isBlank()) errorPass = "Password tidak boleh kosong"
                    if (confirm.isBlank()) errorConfirm = "Konfirmasi password tidak boleh kosong"
                }
            },
            enabled  = uiState !is AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color    = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Daftar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ===== Link ke Login =====
        TextButton(onClick = onNavigateToLogin) {
            Text("Sudah punya akun? Masuk")
        }
    }
}
