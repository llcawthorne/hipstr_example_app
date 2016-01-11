CREATE TABLE artists
( artist_id SERIAL NOT NULL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
      updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
      CONSTRAINT artist_name UNIQUE(name));
--;;
INSERT INTO artists (name) VALUES ('The Arthur Digby Sellers Band')
--;;
INSERT INTO artists (name) VALUES ('Fort Knox Harrington')
--;;
INSERT INTO artists (name) VALUES ('Hungus')
--;;
INSERT INTO artists (name) VALUES ('Smokey Fouler')
--;;
INSERT INTO artists (name) VALUES ('Brant')
