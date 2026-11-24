package com.resepku.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property untuk membuat DataStore singleton per Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "resepku_prefs")

/**
 * TokenManager — menyimpan dan membaca token autentikasi menggunakan DataStore Preferences.
 * DataStore lebih aman dan modern dibanding SharedPreferences.
 */
class TokenManager(private val context: Context) {

    companion object {
        // Key-key yang disimpan di DataStore
        private val KEY_ACCESS_TOKEN  = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_ROLE          = stringPreferencesKey("role")
        private val KEY_NAMA          = stringPreferencesKey("nama_user")
    }

    // ===== Simpan semua data setelah login =====
    suspend fun saveLoginData(accessToken: String, refreshToken: String, role: String, nama: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN]  = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_ROLE]          = role
            prefs[KEY_NAMA]          = nama
        }
    }

    // ===== Update access token saja (setelah refresh) =====
    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
        }
    }

    // ===== Hapus semua data saat logout =====
    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }

    // ===== Flow untuk membaca token (reactive) =====
    val accessToken: Flow<String?>  = context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }
    val role: Flow<String?>         = context.dataStore.data.map { it[KEY_ROLE] }
    val namaUser: Flow<String?>     = context.dataStore.data.map { it[KEY_NAMA] }
}
