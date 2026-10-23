-- Skills Workshop Management System
-- Database creation script
-- Database: SQLite

CREATE TABLE participants
(
    participant_id   TEXT PRIMARY KEY,
    first_name       TEXT    NOT NULL,
    surname          TEXT    NOT NULL,
    email            TEXT    NOT NULL UNIQUE,
    telephone        TEXT,
    participant_type TEXT    NOT NULL,
    active           INTEGER NOT NULL
);

CREATE TABLE workshops
(
    workshop_id      TEXT PRIMARY KEY,
    title            TEXT    NOT NULL,
    category         TEXT    NOT NULL,
    facilitator      TEXT    NOT NULL,
    workshop_date    TEXT    NOT NULL,
    fee              REAL    NOT NULL,
    maximum_capacity INTEGER NOT NULL,
    active           INTEGER NOT NULL
);

CREATE TABLE registrations
(
    registration_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    participant_id      TEXT NOT NULL,
    workshop_id         TEXT NOT NULL,
    registration_date   TEXT NOT NULL,
    amount_payable      REAL NOT NULL,
    payment_status      TEXT NOT NULL,
    registration_status TEXT NOT NULL,

    FOREIGN KEY (participant_id)
        REFERENCES participants (participant_id),

    FOREIGN KEY (workshop_id)
        REFERENCES workshops (workshop_id),

    UNIQUE (participant_id, workshop_id)
);