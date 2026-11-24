require('dotenv').config();
const express = require('express');
const cors = require('cors');
const pool = require('./config/database');

// Import routes
const authRoutes = require('./routes/auth');

const app = express();
const PORT = process.env.PORT || 3000;

// ===== Middleware global =====
app.use(cors());                        // izinkan cross-origin request (Android)
app.use(express.json());                // parse body JSON

// ===== Routes =====
app.use('/auth', authRoutes);

// Route health-check
app.get('/', (req, res) => {
  res.json({ success: true, message: 'ResepKu API berjalan 🍽️' });
});

// ===== Error handler global =====
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ success: false, message: 'Internal server error' });
});

// ===== Start server =====
async function start() {
  try {
    // Uji koneksi database sebelum server menerima request
    await pool.getConnection();
    console.log('✅ Terhubung ke MySQL');
    app.listen(PORT, () => {
      console.log(`🚀 Server berjalan di http://localhost:${PORT}`);
    });
  } catch (err) {
    console.error('❌ Gagal konek ke MySQL:', err.message);
    process.exit(1);
  }
}

start();
