# create votes table

# --- !Ups

CREATE TABLE votes (
    for_fractal varchar not null,
    by_user varchar not null,
    verdict smallint not null
);

create unique index votes_pk on votes(for_fractal, by_user);

# --- !Downs
