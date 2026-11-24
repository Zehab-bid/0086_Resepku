// ============================================================
// Seed script: buat akun admin dan 5 kategori awal
// Jalankan: npm run seed
// ============================================================
require('dotenv').config();
const bcrypt = require('bcrypt');
const pool = require('./config/database');

async function seed() {
  const conn = await pool.getConnection();
  try {
    console.log('🌱 Memulai proses seed...');

    // ===== Buat akun admin =====
    const saltRounds = 10;
    const passwordHash = await bcrypt.hash('Admin123', saltRounds);

    // Gunakan INSERT IGNORE agar tidak error jika sudah ada
    await conn.execute(
      `INSERT IGNORE INTO users (nama, email, password_hash, role)
       VALUES (?, ?, ?, 'admin')`,
      ['Administrator', 'admin@resepku.com', passwordHash]
    );
    console.log('✅ Akun admin dibuat: admin@resepku.com / Admin123');

    // ===== Buat 5 kategori awal =====
    const categories = [
      ['Sarapan',    'Resep-resep untuk menu sarapan pagi'],
      ['Makan Siang','Resep hidangan untuk makan siang'],
      ['Makan Malam','Resep hidangan untuk makan malam'],
      ['Dessert',    'Kue, puding, dan hidangan penutup'],
      ['Minuman',    'Jus, teh, kopi, dan minuman lainnya'],
    ];

    for (const [nama, deskripsi] of categories) {
      // Cek dulu apakah sudah ada
      const [rows] = await conn.execute(
        'SELECT id FROM categories WHERE nama = ?', [nama]
      );
      if (rows.length === 0) {
        await conn.execute(
          'INSERT INTO categories (nama, deskripsi) VALUES (?, ?)',
          [nama, deskripsi]
        );
        console.log(`✅ Kategori "${nama}" ditambahkan`);
      } else {
        console.log(`⏭️  Kategori "${nama}" sudah ada, dilewati`);
      }
    }

    console.log('\n🎉 Seed selesai!');
  } catch (err) {
    console.error('❌ Error saat seed:', err.message);
  } finally {
    conn.release();
    process.exit(0);
  }
}

seed();
