-- ============================================================
-- ResepKu - Database Schema
-- Semua tabel dibuat di sini agar skema tidak berubah-ubah
-- ============================================================

CREATE DATABASE IF NOT EXISTS resepku_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE resepku_db;

-- ========== Tabel Users ==========
CREATE TABLE IF NOT EXISTS users (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  nama         VARCHAR(100)                      NOT NULL,
  email        VARCHAR(150) UNIQUE               NOT NULL,
  password_hash VARCHAR(255)                     NOT NULL,
  role         ENUM('admin','user') DEFAULT 'user' NOT NULL,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========== Tabel Categories ==========
CREATE TABLE IF NOT EXISTS categories (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  nama       VARCHAR(100) NOT NULL,
  deskripsi  TEXT
);

-- ========== Tabel Recipes ==========
CREATE TABLE IF NOT EXISTS recipes (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  user_id       INT           NOT NULL,
  category_id   INT,
  judul         VARCHAR(200)  NOT NULL,
  deskripsi     TEXT,
  langkah       LONGTEXT,          -- langkah-langkah masak (JSON array atau teks)
  durasi_menit  INT,
  porsi         INT,
  kesulitan     ENUM('mudah','sedang','sulit') DEFAULT 'mudah',
  foto_url      VARCHAR(500),
  is_public     BOOLEAN DEFAULT TRUE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- ========== Tabel Ingredients ==========
CREATE TABLE IF NOT EXISTS ingredients (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  recipe_id   INT          NOT NULL,
  nama_bahan  VARCHAR(150) NOT NULL,
  jumlah      VARCHAR(50),
  satuan      VARCHAR(50),
  catatan     VARCHAR(255),
  FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- ========== Tabel Collections ==========
CREATE TABLE IF NOT EXISTS collections (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT  NOT NULL,
  nama       VARCHAR(150) NOT NULL,
  deskripsi  TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ========== Tabel Collection_Recipes ==========
CREATE TABLE IF NOT EXISTS collection_recipes (
  collection_id INT NOT NULL,
  recipe_id     INT NOT NULL,
  PRIMARY KEY (collection_id, recipe_id),         -- primary key gabungan
  FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE,
  FOREIGN KEY (recipe_id)     REFERENCES recipes(id)     ON DELETE CASCADE
);

-- ========== Tabel Refresh Tokens ==========
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT          NOT NULL,
  token      TEXT         NOT NULL,
  expired_at DATETIME     NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
