package com.resepku.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.resepku.app.data.local.TokenManager
import com.resepku.app.data.repository.AuthRepository
import com.resepku.app.util.RegisterError
import com.resepku.app.util.Resource
import com.resepku.app.util.Validator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ===== State untuk seluruh layar auth =====
sealed class AuthUiState {
    data object Idle    : AuthUiState()   // awal / reset
    data object Loading : AuthUiState()  // sedang proses
    data class  Success(val message: String) : AuthUiState()
    data class  Error(val message: String)   : AuthUiState()
}

/**
 * AuthViewModel — mengelola state untuk LoginScreen dan RegisterScreen.
 * Memakai AndroidViewModel karena butuh Application context untuk TokenManager.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager  = TokenManager(application)
    private val repository    = AuthRepository(tokenManager)

    // StateFlow: UI mengamati ini untuk update tampilan
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ===== Cek apakah user sudah login (ada token tersimpan) =====
    suspend fun isLoggedIn(): Boolean {
        return tokenManager.accessToken.first() != null
    }

    // ===== Ambil nama user dari DataStore =====
    val namaUser = tokenManager.namaUser
    val role     = tokenManager.role

    // ===== Login =====
    fun login(email: String, password: String) {
        // Validasi cepat sebelum network call
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email dan password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.login(email.trim(), password)) {
                is Resource.Success -> _uiState.value = AuthUiState.Success("Login berhasil")
                is Resource.Error   -> _uiState.value = AuthUiState.Error(result.message)
                is Resource.Loading -> {} // tidak akan terjadi dari repository
            }
        }
    }

    // ===== Register =====
    fun register(nama: String, email: String, password: String, confirm: String) {
        // Validasi form — semua logika ada di Validator, bukan di Composable
        val error: RegisterError? = Validator.validateRegisterForm(nama, email, password, confirm)
        if (error != null) {
            _uiState.value = AuthUiState.Error(error.message)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.register(nama.trim(), email.trim(), password)) {
                is Resource.Success -> _uiState.value = AuthUiState.Success("Registrasi berhasil! Silakan login.")
                is Resource.Error   -> _uiState.value = AuthUiState.Error(result.message)
                is Resource.Loading -> {}
            }
        }
    }

    // ===== Logout =====
    fun logout() {
        viewModelScope.launch {
            val refreshToken = tokenManager.refreshToken.first() ?: ""
            repository.logout(refreshToken)
            _uiState.value = AuthUiState.Idle
        }
    }

    // ===== Reset state ke Idle (misal setelah navigasi) =====
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
