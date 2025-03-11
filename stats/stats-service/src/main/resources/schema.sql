CREATE TABLE IF NOT EXISTS endpoint_hits
(
  id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  app       VARCHAR(255)                NOT NULL,
  uri       VARCHAR(512)                NOT NULL,
  ip        VARCHAR(40)                 NOT NULL,
  timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
