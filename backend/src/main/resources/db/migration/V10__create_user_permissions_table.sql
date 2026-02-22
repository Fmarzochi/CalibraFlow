CREATE TABLE user_permissions (
    user_id BIGINT NOT NULL,
    permission VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_permissions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (user_id, permission)
);