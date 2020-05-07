# drop picture from user table

# --- !Ups

ALTER TABLE users
    DROP COLUMN picture;

# --- !Downs
