# create user table

# --- !Ups

CREATE TABLE users (
    id char(36),
    name varchar not null,
    email varchar not null,
    picture varchar not null,
    google_user_id varchar,
    primary key (id)
);

create index users_email on users(email);
create index users_google_user_id on users(google_user_id);

# --- !Downs
