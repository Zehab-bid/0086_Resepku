package com.resepku.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient — singleton yang membuat instance Retrofit.
 *
 * BASE_URL menggunakan 10.0.2.2 karena itu adalah alias localhost
 * untuk Android Emulator. Jika memakai HP fisik, ganti dengan
 * IP laptop di jaringan yang sama (misalnya "http://192.168.1.x:3000/").
 *
 * Cukup ubah BASE_URL di satu tempat ini saja.
 */
object RetrofitClient {

    // Ganti ini dengan IP laptop jika pakai HP asli (bukan emulator)
    const val BASE_URL = "http://10.0.2.2:3000/"

    /**
     * Buat instance Retrofit dengan interceptor yang diberikan.
     * Dipanggil dari kelas lain yang sudah menyiapkan OkHttpClient-nya.
     */
    fun create(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())  // konversi JSON <-> data class
            .build()
            .create(ApiService::class.java)
    }

    /** OkHttpClient dasar dengan logging — dipakai untuk request refresh token (tanpa auth) */
    fun buildBaseClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // Log body request+response saat DEBUG, kurangi ke NONE saat RELEASE
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}
