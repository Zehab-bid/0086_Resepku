package com.resepku.app.util

/**
 * Resource — wrapper generik untuk state dari operasi async (network/DB).
 * Dipakai di Repository dan ViewModel untuk membawa data + pesan error.
 *
 * Penggunaan:
 *   Resource.Success(data)
 *   Resource.Error("Pesan error")
 *   Resource.Loading
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
