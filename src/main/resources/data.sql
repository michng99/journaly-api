-- Insert default user if not exists
INSERT INTO users (id, email, password_hash, created_at)
SELECT 
    gen_random_uuid(),
    'default@journaly.app',
    'default_password_hash',
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'default@journaly.app');
