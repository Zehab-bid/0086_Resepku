package com.resepku.app.data.remote

import com.resepku.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * ApiService — definisi endpoint REST API menggunakan Retrofit.
 * Setiap fungsi = satu endpoint HTTP.
 */
interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<ApiResponse<LoginResponseData>>

    // Endpoint refresh dipanggil oleh TokenAuthenticator, TANPA header Authorization
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequest): Response<ApiResponse<RefreshResponseData>>

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<ApiResponse<Any>>

    // Endpoint ini terproteksi — AuthInterceptor akan menambahkan header Bearer
    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<MeResponseData>>
}
