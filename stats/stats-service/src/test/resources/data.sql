INSERT INTO endpoint_hits (app, uri, ip, timestamp)
VALUES
  ('mainService', 'endpointA', '127.0.0.1', '2000-01-01T00:00:00'),
  ('mainService', 'endpointA', '127.0.0.1', '2000-01-01T00:00:01'),
  ('mainService', 'endpointA', '127.0.0.1', '2000-02-02T00:00:03'),
  ('mainService', 'endpointB', '127.0.0.1', '2000-01-01T00:00:00'),
  ('mainService', 'endpointB', '127.0.0.1', '2000-01-01T00:00:01'),
  ('mainService', 'endpointB', '127.0.0.2', '2000-01-31T13:30:55'),
  ('mainService', 'endpointB', '127.0.0.2', '2000-02-02T00:00:02'),
  ('mainService', 'endpointB', '127.0.0.2', '2000-02-02T00:00:03'),
  ('mainService', 'endpointC', '127.0.0.1', '2000-01-01T00:00:00'),
  ('mainService', 'endpointC', '127.0.0.1', '2000-01-01T00:00:01'),
  ('mainService', 'endpointC', '127.0.0.2', '2000-01-31T13:30:55'),
  ('mainService', 'endpointC', '127.0.0.3', '2000-01-31T13:30:55'),
  ('mainService', 'endpointC', '127.0.0.4', '2000-02-02T00:00:02'),
  ('mainService', 'endpointC', '127.0.0.4', '2000-02-02T00:00:03');
