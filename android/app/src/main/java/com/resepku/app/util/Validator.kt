package com.resepku.app.util

/**
 * Validator — kumpulan fungsi validasi form.
 * Semua validasi ada di sini, bukan di Composable (sesuai arsitektur MVVM).
 */
object Validator {

    /** Cek apakah format email valid menggunakan pola bawaan Android */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /** Password minimal 8 karakter */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    /** Nama tidak boleh kosong dan minimal 2 karakter */
    fun isValidNama(nama: String): Boolean {
        return nama.trim().length >= 2
    }

    /** Konfirmasi password harus cocok dengan password asli */
    fun isPasswordMatch(password: String, confirm: String): Boolean {
        return password == confirm
    }

    /**
     * Validasi lengkap form register.
     * Mengembalikan RegisterError pertama yang ditemukan, atau null jika semua valid.
     */
    fun validateRegisterForm(
        nama: String,
        email: String,
        password: String,
        confirm: String
    ): RegisterError? {
        return when {
            !isValidNama(nama)                  -> RegisterError.NamaTerlalupendek
            !isValidEmail(email)                -> RegisterError.FormatEmailSalah
            !isValidPassword(password)          -> RegisterError.PasswordTerlalupendek
            !isPasswordMatch(password, confirm) -> RegisterError.PasswordTidakCocok
            else                                -> null   // null = semua valid
        }
    }
}

/** Sealed class untuk error registrasi — mudah di-when di ViewModel */
sealed class RegisterError(val message: String) {
    data object NamaTerlalupendek    : RegisterError("Nama minimal 2 karakter")
    data object FormatEmailSalah     : RegisterError("Format email tidak valid")
    data object PasswordTerlalupendek: RegisterError("Password minimal 8 karakter")
    data object PasswordTidakCocok   : RegisterError("Konfirmasi password tidak cocok")
}
