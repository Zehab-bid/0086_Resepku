// ============================================================
// Middleware autentikasi & otorisasi
// ============================================================
const jwt = require('jsonwebtoken');

/**
 * verifyToken - cek Authorization header, decode JWT
 * Menyimpan payload ke req.user jika valid
 */
function verifyToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  // Format header: "Bearer <token>"
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({ success: false, message: 'Token tidak ditemukan' });
  }

  try {
    const payload = jwt.verify(token, process.env.JWT_ACCESS_SECRET);
    req.user = payload;   // { id, role, iat, exp }
    next();
  } catch (err) {
    return res.status(401).json({ success: false, message: 'Token tidak valid atau sudah kadaluarsa' });
  }
}

/**
 * requireRole - factory middleware untuk cek role
 * Gunakan setelah verifyToken
 * Contoh: router.get('/admin-only', verifyToken, requireRole('admin'), handler)
 */
function requireRole(role) {
  return (req, res, next) => {
    if (req.user?.role !== role) {
      return res.status(403).json({ success: false, message: 'Akses ditolak: role tidak mencukupi' });
    }
    next();
  };
}

module.exports = { verifyToken, requireRole };
