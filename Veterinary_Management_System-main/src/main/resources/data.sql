INSERT INTO users (username, password, email, full_name, role)
SELECT 'admin', '$2a$10$rDkPvvAFV6GgJjXpYWYwXe6QZQZQZQZQZQZQZQZQZQZQZQZQZQZQ', 'admin@vet.com', 'Admin User', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password, email, full_name, role)
SELECT 'admin2', '$2a$10$8bQwQwQwQwQwQwQwQwQwQeQwQwQwQwQwQwQwQwQwQwQwQwQwQw', 'admin2@vet.com', 'Second Admin', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin2'); 