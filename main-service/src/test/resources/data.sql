INSERT INTO categories(name)
VALUES ('concerts'),
       ('cinemas');

INSERT INTO users(name, email)
VALUES
    ('First User', 'first@test.com'),
    ('Second User', 'second@test.com');

INSERT INTO events(initiator_id, title, category_id, event_date, lat, lon, annotation, description, participant_limit,
                   paid, request_moderation, created_on, state)
VALUES (1, 'Concert', 1, '2100-12-31T23:59:59', 51.28, 0.0, 'First concert', 'You have been waiting for it', 0, false,
        false, '2000-01-01T00:00:01', 'PENDING');

INSERT INTO compilations(pinned, title)
VALUES (true, 'Compilation title 1');

INSERT INTO compilation_event(compilation_id, event_id)
VALUES (1, 1);