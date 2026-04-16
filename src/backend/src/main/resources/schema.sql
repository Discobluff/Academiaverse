CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password CHAR(64),
    role VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP

);