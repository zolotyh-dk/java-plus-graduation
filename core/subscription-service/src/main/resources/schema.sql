CREATE TABLE IF NOT EXISTS subscriptions
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subscriber_id BIGINT NOT NULL,
    target_id     BIGINT NOT NULL,
    CONSTRAINT subscriptions_subscriber_target_ux UNIQUE (subscriber_id, target_id)
);