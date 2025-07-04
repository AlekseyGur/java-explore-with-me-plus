CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    annotation VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    event_date TIMESTAMP NOT NULL,
    location_id BIGINT REFERENCES locations(id) ON DELETE CASCADE NOT NULL,
    category_id BIGINT REFERENCES categories(id),
    initiator_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    paid BOOLEAN,
    participant_limit BIGINT,
    confirmed_requests BIGINT,
    request_moderation BOOLEAN,
    state VARCHAR(50),
    created_on TIMESTAMP,
    published_on TIMESTAMP,
    views INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT REFERENCES compilations(id) ON DELETE CASCADE NOT NULL,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE NOT NULL,
    requester_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    status VARCHAR(50),
    created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS views (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE NOT NULL,
    ip VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE NOT NULL,
    create_date TIMESTAMP
);