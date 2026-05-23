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

CREATE TABLE IF NOT EXISTS course (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255),
    name VARCHAR(255)
);

INSERT INTO course (code, name)
SELECT 'CS101', 'Software Engineering'
WHERE NOT EXISTS (
    SELECT 1 FROM course WHERE code = 'CS101'
);