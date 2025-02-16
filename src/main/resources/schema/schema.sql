CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    hashed_password TEXT,
    social_id TEXT,
    image_id TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_created ON users(created_at);

CREATE TABLE user_information (
    user_info_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    title VARCHAR(100),
    company_id UUID,
    metadata JSONB
);

CREATE INDEX idx_user_info_user ON user_information(user_id);

CREATE INDEX CONCURRENTLY idx_users_compound ON users USING BRIN(created_at)
INCLUDE (email, phone);