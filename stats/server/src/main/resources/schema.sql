CREATE TABLE IF NOT EXISTS HITS
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    app     VARCHAR(255) NOT NULL,
    uri     VARCHAR(255) NOT NULL,
    ip      VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP
);