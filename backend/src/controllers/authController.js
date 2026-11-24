// ============================================================
// Controller Autentikasi
// Semua logika bisnis ada di sini, bukan di routes
// ============================================================
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const pool = require('../config/database');

// ===== Helper: buat response konsisten =====
const ok  = (res, data, message = 'Berhasil', status = 200) =>
  res.status(status).json({ success: true, message, data });

const err = (res, message = 'Terjadi kesalahan', status = 400) =>
  res.status(status).json({ success: false, message, data: null });

// ===== Helper: validasi email format =====
function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// ===== Helper: buat access token (15 menit) =====
function generateAccessToken(user) {
  return jwt.sign(
    { id: user.id, role: user.role },
    process.env.JWT_ACCESS_SECRET,
    { expiresIn: '15m' }
  );
}

// ===== Helper: buat refresh token (7 hari) =====
function generateRefreshToken(user) {
  return jwt.sign(
    { id: user.id },
    process.env.JWT_REFRESH_SECRET,
    { expiresIn: '7d' }
  );
}

// ===== Helper: simpan refresh token ke DB =====
async function saveRefreshToken(userId, token) {
  const expiredAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // +7 hari
  await pool.execute(
    'INSERT INTO refresh_tokens (user_id, token, expired_at) VALUES (?, ?, ?)',
    [userId, token, expiredAt]  // parameterized query, aman dari SQL injection
  );
}

// ============================================================
// POST /auth/register
// Body: { nama, email, password }
// ============================================================
async function register(req, res) {
  const { nama, email, password } = req.body;

  // Validasi input tidak boleh kosong
  if (!nama || !email || !password) {
    return err(res, 'Nama, email, dan password wajib diisi');
  }
  if (!isValidEmail(email)) {
    return err(res, 'Format email tidak valid');
  }
  if (password.length < 8) {
    return err(res, 'Password minimal 8 karakter');
  }

  try {
    // Cek apakah email sudah terdaftar (parameterized query)
    const [existing] = await pool.execute(
      'SELECT id FROM users WHERE email = ?', [email]
    );
    if (existing.length > 0) {
      return err(res, 'Email sudah terdaftar', 409);
    }

    // Hash password sebelum disimpan
    const passwordHash = await bcrypt.hash(password, 10);

    // Simpan user baru, role otomatis 'user'
    const [result] = await pool.execute(
      'INSERT INTO users (nama, email, password_hash) VALUES (?, ?, ?)',
      [nama, email, passwordHash]
    );

    return ok(res, { id: result.insertId, nama, email, role: 'user' },
      'Registrasi berhasil', 201);
  } catch (e) {
    console.error('register error:', e);
    return err(res, 'Terjadi kesalahan server', 500);
  }
}

// ============================================================
// POST /auth/login
// Body: { email, password }
// ============================================================
async function login(req, res) {
  const { email, password } = req.body;

  if (!email || !password) {
    return err(res, 'Email dan password wajib diisi');
  }

  try {
    // Ambil user berdasarkan email
    const [rows] = await pool.execute(
      'SELECT id, nama, email, password_hash, role FROM users WHERE email = ?',
      [email]
    );
    if (rows.length === 0) {
      return err(res, 'Email atau password salah', 401);
    }

    const user = rows[0];

    // Bandingkan password dengan hash di DB
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return err(res, 'Email atau password salah', 401);
    }

    // Buat token
    const accessToken  = generateAccessToken(user);
    const refreshToken = generateRefreshToken(user);

    // Simpan refresh token ke database
    await saveRefreshToken(user.id, refreshToken);

    return ok(res, {
      accessToken,
      refreshToken,
      user: { id: user.id, nama: user.nama, email: user.email, role: user.role }
    }, 'Login berhasil');
  } catch (e) {
    console.error('login error:', e);
    return err(res, 'Terjadi kesalahan server', 500);
  }
}

// ============================================================
// POST /auth/refresh
// Body: { refreshToken }
// ============================================================
async function refresh(req, res) {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return err(res, 'Refresh token tidak ditemukan', 401);
  }

  try {
    // Verifikasi signature refresh token
    const payload = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET);

    // Pastikan token ada di DB dan belum expired
    const [rows] = await pool.execute(
      'SELECT id FROM refresh_tokens WHERE token = ? AND user_id = ? AND expired_at > NOW()',
      [refreshToken, payload.id]
    );
    if (rows.length === 0) {
      return err(res, 'Refresh token tidak valid atau sudah kadaluarsa', 401);
    }

    // Ambil data user terbaru (role bisa berubah)
    const [users] = await pool.execute(
      'SELECT id, role FROM users WHERE id = ?', [payload.id]
    );
    if (users.length === 0) {
      return err(res, 'User tidak ditemukan', 401);
    }

    const newAccessToken = generateAccessToken(users[0]);
    return ok(res, { accessToken: newAccessToken }, 'Token diperbarui');
  } catch (e) {
    return err(res, 'Refresh token tidak valid', 401);
  }
}

// ============================================================
// POST /auth/logout
// Body: { refreshToken }
// ============================================================
async function logout(req, res) {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return err(res, 'Refresh token tidak ditemukan');
  }

  try {
    // Hapus refresh token dari DB (invalidate session)
    await pool.execute(
      'DELETE FROM refresh_tokens WHERE token = ?', [refreshToken]
    );
    return ok(res, null, 'Logout berhasil');
  } catch (e) {
    console.error('logout error:', e);
    return err(res, 'Terjadi kesalahan server', 500);
  }
}

// ============================================================
// GET /auth/me  (terproteksi - butuh verifyToken middleware)
// ============================================================
async function getMe(req, res) {
  try {
    // req.user sudah diisi oleh middleware verifyToken
    const [rows] = await pool.execute(
      'SELECT id, nama, email, role, created_at FROM users WHERE id = ?',
      [req.user.id]
    );
    if (rows.length === 0) {
      return err(res, 'User tidak ditemukan', 404);
    }
    return ok(res, rows[0]);
  } catch (e) {
    console.error('getMe error:', e);
    return err(res, 'Terjadi kesalahan server', 500);
  }
}

module.exports = { register, login, refresh, logout, getMe };
