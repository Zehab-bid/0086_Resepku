package com.resepku.app.data.remote.dto

// ===== Request DTOs (data yang dikirim ke server) =====

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class LogoutRequest(
    val refreshToken: String
)

// ===== Response DTOs (data yang diterima dari server) =====

/** Wrapper umum untuk semua response: { success, message, data } */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

/** Data user yang dikembalikan dari login */
data class UserDto(
    val id: Int,
    val nama: String,
    val email: String,
    val role: String
)

/** Response dari endpoint login */
data class LoginResponseData(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

/** Response dari endpoint refresh token */
data class RefreshResponseData(
    val accessToken: String
)

/** Response dari endpoint /auth/me */
data class MeResponseData(
    val id: Int,
    val nama: String,
    val email: String,
    val role: String,
    val created_at: String?
)
