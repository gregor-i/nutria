# Fractal schema

# --- !Ups

CREATE TABLE images (
  id char(36) NOT NULL,
  image varchar NOT NULL,
  owner char(36) NOT NULL,
  published boolean NOT NULL,
  PRIMARY KEY (id)
);

create unique index images_id on images(id);
create index images_owner on images(owner);
create index images_published on images(published);

# --- !Downs
