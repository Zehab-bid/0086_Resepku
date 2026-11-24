package com.resepku.app.data.remote

import com.resepku.app.data.local.TokenManager
import com.resepku.app.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * TokenAuthenticator — dipanggil oleh OkHttp ketika server mengembalikan 401.
 *
 * Alur:
 * 1. Ambil refresh token dari DataStore
 * 2. Panggil /auth/refresh untuk dapat access token baru
 * 3. Simpan access token baru
 * 4. Ulangi request asli dengan token baru
 * 5. Jika refresh gagal → hapus semua token (user harus login ulang)
 *
 * Catatan: gunakan client TERPISAH (tanpa AuthInterceptor) untuk memanggil
 * /auth/refresh, agar tidak terjadi loop tak terbatas.
 */
class TokenAuthenticator(
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Jika sudah coba refresh tapi masih 401, berhenti (return null)
        if (response.request.header("X-Retry-After-Refresh") != null) return null

        val refreshToken = runBlocking { tokenManager.refreshToken.first() }
            ?: return null  // Tidak ada refresh token → paksa logout

        return runBlocking {
            try {
                // Buat client baru TANPA AuthInterceptor untuk memanggil /auth/refresh
                val baseClient = RetrofitClient.buildBaseClient()
                val apiService = RetrofitClient.create(baseClient)

                val refreshResponse = apiService.refresh(RefreshTokenRequest(refreshToken))

                if (refreshResponse.isSuccessful) {
                    val newAccessToken = refreshResponse.body()?.data?.accessToken
                    if (newAccessToken != null) {
                        // Simpan access token baru
                        tokenManager.saveAccessToken(newAccessToken)

                        // Ulangi request asli dengan token baru
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .header("X-Retry-After-Refresh", "true")  // tandai agar tidak loop
                            .build()
                    } else {
                        // Server berhasil tapi token null → logout
                        tokenManager.clearAll()
                        null
                    }
                } else {
                    // Refresh token tidak valid/expired → hapus semua data login
                    tokenManager.clearAll()
                    null
                }
            } catch (e: Exception) {
                // Error jaringan saat refresh → logout
                tokenManager.clearAll()
                null
            }
        }
    }
}
