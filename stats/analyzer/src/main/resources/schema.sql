CREATE TABLE IF NOT EXISTS similarity
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_a_id BIGINT         NOT NULL,
    event_b_id BIGINT         NOT NULL,
    similarity DECIMAL(6, 5) NOT NULL,
    timestamp  TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS weight
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id   BIGINT         NOT NULL,
    event_id  BIGINT         NOT NULL,
    weight    DECIMAL(6, 5) NOT NULL,
    timestamp TIMESTAMP      NOT NULL
);