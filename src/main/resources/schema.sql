DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
CREATE TABLE IF NOT EXISTS users
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR (255) NOT NULL,
    email VARCHAR (512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR (512) NOT NULL,
    requestor_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    created DATE NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS items
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR (255) NOT NULL,
    description VARCHAR (512) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT FALSE,
    owner_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    request_id INTEGER REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)

    );

CREATE TABLE IF NOT EXISTS bookings
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    item_id INTEGER REFERENCES items (id) ON DELETE CASCADE,
    booker_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    status VARCHAR (30) NOT NULL DEFAULT 'WAITING',
    CONSTRAINT pk_booking PRIMARY KEY (id)

    );

CREATE TABLE IF NOT EXISTS comments
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR (512) NOT NULL,
    item_id INTEGER REFERENCES items (id) ON DELETE CASCADE,
    author_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    created DATE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
    );