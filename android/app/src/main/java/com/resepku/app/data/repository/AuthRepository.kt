package com.resepku.app.data.repository

import com.resepku.app.data.local.TokenManager
import com.resepku.app.data.remote.RetrofitClient
import com.resepku.app.data.remote.dto.*
import com.resepku.app.util.Resource

/**
 * AuthRepository — lapisan antara ViewModel dan sumber data (network).
 *
 * Bertanggung jawab untuk:
 * - Memanggil API
 * - Menyimpan/menghapus token via TokenManager
 * - Membungkus hasil dalam Resource<T>
 *
 * ViewModel tidak perlu tahu detail HTTP atau DataStore.
 */
class AuthRepository(private val tokenManager: TokenManager) {

    // Client tanpa auth (untuk login & register)
    private val baseApi = RetrofitClient.create(RetrofitClient.buildBaseClient())

    // Client dengan auth (untuk endpoint terproteksi)
    private val authApi = RetrofitClient.buildAuthClient(tokenManager)

    /** Login: panggil API, simpan token jika berhasil */
    suspend fun login(email: String, password: String): Resource<LoginResponseData> {
        return try {
            val response = baseApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    // Simpan semua data login ke DataStore
                    tokenManager.saveLoginData(
                        accessToken  = data.accessToken,
                        refreshToken = data.refreshToken,
                        role         = data.user.role,
                        nama         = data.user.nama
                    )
                    Resource.Success(data)
                } else {
                    Resource.Error(response.body()?.message ?: "Login gagal")
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login gagal"
                Resource.Error(parseErrorMessage(errorMsg))
            }
        } catch (e: Exception) {
            Resource.Error("Tidak bisa terhubung ke server: ${e.localizedMessage}")
        }
    }

    /** Register: kirim data user baru */
    suspend fun register(nama: String, email: String, password: String): Resource<UserDto> {
        return try {
            val response = baseApi.register(RegisterRequest(nama, email, password))
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) Resource.Success(data)
                else Resource.Error(response.body()?.message ?: "Registrasi gagal")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registrasi gagal"
                Resource.Error(parseErrorMessage(errorMsg))
            }
        } catch (e: Exception) {
            Resource.Error("Tidak bisa terhubung ke server: ${e.localizedMessage}")
        }
    }

    /** Logout: hapus refresh token dari server + hapus lokal */
    suspend fun logout(refreshToken: String): Resource<Unit> {
        return try {
            authApi.logout(LogoutRequest(refreshToken))
            tokenManager.clearAll()
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Tetap hapus token lokal meskipun request gagal
            tokenManager.clearAll()
            Resource.Success(Unit)
        }
    }

    /** Parse pesan error dari JSON response body */
    private fun parseErrorMessage(errorBody: String): String {
        return try {
            val json = org.json.JSONObject(errorBody)
            json.optString("message", "Terjadi kesalahan")
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }
}
