CREATE TABLE tasks (
                       id          BIGSERIAL PRIMARY KEY,
                       title       VARCHAR(255) NOT NULL,
                       description VARCHAR(2000),
                       status      VARCHAR(50)  NOT NULL DEFAULT 'NEW',
                       created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                       updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);