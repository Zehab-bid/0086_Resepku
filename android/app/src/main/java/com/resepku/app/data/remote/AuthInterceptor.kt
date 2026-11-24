package com.resepku.app.data.remote

import com.resepku.app.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor — menambahkan header "Authorization: Bearer <token>"
 * ke setiap request HTTP secara otomatis.
 *
 * OkHttp memanggil intercept() sebelum mengirim request.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Baca access token dari DataStore secara synchronous
        // (runBlocking dipakai karena intercept() bukan suspend function)
        val token = runBlocking { tokenManager.accessToken.first() }

        val request = if (token != null) {
            // Tambahkan header Authorization ke request
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()  // Lanjutkan tanpa token (akan 401 dari server)
        }

        return chain.proceed(request)
    }
}
