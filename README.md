# 🍽️ ResepKu

Aplikasi Android untuk menyimpan, mengelola, mencari, dan mengeksplorasi resep masakan.  
Dibangun sebagai proyek akhir kuliah dengan arsitektur MVVM dan REST API sendiri.

---

## Tech Stack

| Lapisan | Teknologi |
|---------|-----------|
| Android | Kotlin, Jetpack Compose, Navigation Compose, ViewModel, StateFlow |
| Network | Retrofit 2, OkHttp 4, Gson |
| Storage | DataStore Preferences (token) |
| Gambar  | Coil Compose |
| Backend | Node.js, Express |
| Database | MySQL 8 |
| Auth | JWT (Access Token 15m + Refresh Token 7d), bcrypt |

---

## Cara Menjalankan Backend

### Prasyarat
- Node.js ≥ 18
- MySQL 8 berjalan di lokal

### Langkah
```bash
# 1. Masuk ke folder backend
cd backend

# 2. Salin file konfigurasi dan isi nilainya
cp .env.example .env
# Edit .env: isi DB_HOST, DB_USER, DB_PASSWORD, DB_NAME, JWT_ACCESS_SECRET, JWT_REFRESH_SECRET

# 3. Install dependency
npm install

# 4. Jalankan schema SQL di MySQL (cukup sekali)
# Buka MySQL client, lalu jalankan:
# source /path/ke/resepku/backend/schema.sql

# 5. Seed data awal (admin + 5 kategori)
npm run seed

# 6. Jalankan server
npm start
# atau npm run dev (dengan auto-reload via nodemon)
```

Server berjalan di `http://localhost:3000`. Uji dengan membuka `backend/requests.http` di VS Code (ekstensi REST Client).

---

## Cara Menjalankan Android

1. Buka **Android Studio**
2. Pilih **File → Open** → arahkan ke folder `android/`  
   *(bukan folder root `resepku/`)*
3. Tunggu **Gradle Sync** selesai
4. Pastikan emulator atau HP sudah terhubung
5. Klik **Run ▶** (Shift+F10)

> **Catatan:** Jika menggunakan HP fisik (bukan emulator), ganti `BASE_URL` di  
> [`RetrofitClient.kt`](android/app/src/main/java/com/resepku/app/data/remote/RetrofitClient.kt)  
> dari `http://10.0.2.2:3000/` ke `http://<IP-laptop>:3000/`

---

## Akun Default (dari seed)

| Email | Password | Role |
|-------|----------|------|
| admin@resepku.com | Admin123 | admin |

---

## 4. Progress Mingguan (Weekly Progress)

Berikut adalah linimasa pengembangan proyek ResepKu yang direkonstruksi secara riil berdasarkan riwayat git commit:

### 🗓️ Minggu 1 (24 November 2026)

- **Inisialisasi & Setup Lingkungan** ( `52b4dab` , `17a151f` , `3dc9d5e` ): Inisialisasi struktur direktori proyek, konfigurasi `.gitignore`, inisialisasi backend Express.js, konfigurasi koneksi pool MySQL 2, integrasi version catalog Android (`libs.versions.toml`), dan perizinan akses jaringan (`INTERNET` serta `usesCleartextTraffic`).
- **Perancangan Basis Data & Seeder** ( `e6bb9ea` , `faa8b8b` ): Desain skema basis data menyeluruh (`schema.sql`) mencakup relasi tabel `users`, `categories`, `recipes`, `ingredients`, `collections`, `collection_recipes`, dan `refresh_tokens`, implementasi script seeder (`seed.js`) akun admin dan 5 kategori default, serta penyusunan berkas uji coba request HTTP (`requests.http`).
- **Layanan Autentikasi & Otorisasi Backend** ( `f8f02b0` , `0d86191` , `8f84e11` ): Implementasi endpoint registrasi pengguna dengan enkripsi password `bcrypt`, endpoint login dengan penerbitan token ganda (Access Token JWT 15 menit & Refresh Token 7 hari), endpoint refresh token, pembatalan sesi via endpoint logout, endpoint proteksi profil pengguna (`/auth/me`), serta middleware verifikasi JWT (`verifyToken`) dan proteksi hak akses (`requireRole('admin')`).
- **Lapisan Jaringan & Penyimpanan Sesi Android** ( `99c19fd` , `3919641` ): Integrasi `RetrofitClient` berbasis OkHttp dan Gson Converter, perancangan DTO request/response, pengelolaan token persisten dengan `DataStore Preferences` (`TokenManager`), serta pembuatan `AuthInterceptor` (injeksi otomatis header Bearer) dan `TokenAuthenticator` (penanganan error 401 dan auto-refresh token tanpa memutus alur pengguna).
- **Antarmuka & Pengelolaan State Autentikasi** ( `52b004b` , `a8a905d` , `865297b` , `650ad0d` ): Penerapan tema Material 3 warna oranye-krem bertema kuliner, pemisahan logika validasi input formulir pada utility `Validator`, perancangan arsitektur MVVM dengan `AuthViewModel` berbasis `StateFlow` dan `AuthUiState`, pembuatan antarmuka `LoginScreen` dengan toggle visibilitas password, antarmuka `RegisterScreen` dengan pesan error real-time per field, serta penyesuaian konfigurasi workspace IDE.
- **Navigasi & Dasbor Placeholder** ( `c14983f` , `39922fd` ): Implementasi navigasi deklaratif menggunakan Navigation Compose (`AppNavGraph`) dengan penentuan rute awal dinamis berdasarkan ketersediaan token aktif, pembuatan antarmuka `HomeScreen` placeholder berfitur informasi sapaan nama pengguna, badge peran akun, tombol aksi logout, dan pembaruan dokumentasi linimasa proyek.

---

## Status Fitur

| Fitur | Status | Minggu |
|-------|--------|--------|
| Authentication (login, register, JWT, refresh, logout) | ✅ Selesai | 1 |
| Otorisasi (role admin/user, middleware verifyToken & requireRole) | ✅ Dasar selesai | 1 |
| Manajemen Kategori | 🔜 Belum | 2 |
| Manajemen Resep (CRUD) | 🔜 Belum | 2–3 |
| Manajemen Bahan (ingredients) | 🔜 Belum | 3 |
| Koleksi Resep | 🔜 Belum | 4 |
| Manajemen Pengguna (panel admin) | 🔜 Belum | 4 |
| Upload foto lokal | 🔜 Belum | 5 |
| UI polish & animasi | 🔜 Belum | 6 |
| Testing (unit & UI) | 🔜 Belum | 6 |

---

## Struktur Folder

```
resepku/
├── android/                   # Project Android Studio
│   └── app/src/main/java/com/resepku/app/
│       ├── data/
│       │   ├── local/         # TokenManager (DataStore)
│       │   ├── remote/        # ApiService, RetrofitClient, Interceptor, dto/
│       │   └── repository/    # AuthRepository
│       ├── navigation/        # Routes, AppNavGraph
│       ├── ui/
│       │   ├── auth/          # LoginScreen, RegisterScreen, AuthViewModel
│       │   ├── home/          # HomeScreen (placeholder)
│       │   └── theme/         # Color, Theme, Type
│       └── util/              # Resource, Validator
├── backend/
│   ├── src/
│   │   ├── config/            # database.js
│   │   ├── controllers/       # authController.js
│   │   ├── middleware/        # auth.js (verifyToken, requireRole)
│   │   ├── routes/            # auth.js
│   │   ├── app.js             # Entry point Express
│   │   └── seed.js            # Script seed data awal
│   ├── schema.sql             # Semua tabel MySQL
│   ├── requests.http          # Contoh request (VS Code REST Client)
│   └── .env.example           # Template konfigurasi environment
└── README.md
```
