package com.resepku.app.data.remote

import com.resepku.app.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient — singleton pembuat instance Retrofit + ApiService.
 *
 * BASE_URL menggunakan 10.0.2.2 karena itu adalah alias localhost
 * untuk Android Emulator. Jika memakai HP fisik, ganti dengan
 * IP laptop di jaringan yang sama (misalnya "http://192.168.1.x:3000/").
 *
 * CUKUP ubah BASE_URL di satu tempat ini.
 */
object RetrofitClient {

    // Ganti ini dengan IP laptop jika pakai HP asli (bukan emulator)
    const val BASE_URL = "http://10.0.2.2:3000/"

    /**
     * Buat ApiService dengan auth (AuthInterceptor + TokenAuthenticator).
     * Dipakai oleh semua endpoint yang butuh token.
     */
    fun buildAuthClient(tokenManager: TokenManager): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenManager))        // tambah header Bearer
            .authenticator(TokenAuthenticator(tokenManager))      // handle 401 → refresh
            .build()

        return create(okHttpClient)
    }

    /**
     * OkHttpClient DASAR tanpa auth — dipakai untuk:
     * - Endpoint login/register (tidak butuh token)
     * - Panggilan refresh di dalam TokenAuthenticator (hindari loop)
     */
    fun buildBaseClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    /** Buat ApiService dari OkHttpClient yang sudah dikonfigurasi */
    fun create(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
