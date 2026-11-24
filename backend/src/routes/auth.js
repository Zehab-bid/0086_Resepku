// ============================================================
// Routes autentikasi
// Base path: /auth  (didaftarkan di app.js)
// ============================================================
const express = require('express');
const router  = express.Router();
const { register, login, refresh, logout, getMe } = require('../controllers/authController');
const { verifyToken } = require('../middleware/auth');

// POST /auth/register  - daftar akun baru
router.post('/register', register);

// POST /auth/login     - login, dapat access + refresh token
router.post('/login', login);

// POST /auth/refresh   - perbarui access token dari refresh token
router.post('/refresh', refresh);

// POST /auth/logout    - hapus refresh token (invalidate session)
router.post('/logout', logout);

// GET  /auth/me        - info user saat ini (butuh token)
router.get('/me', verifyToken, getMe);

module.exports = router;
