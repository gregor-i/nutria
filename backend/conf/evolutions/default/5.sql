# Fractal schema

# --- !Ups

CREATE TABLE templates (
  id char(36) NOT NULL,
  template varchar NOT NULL,
  owner char(36) NOT NULL,
  published boolean NOT NULL,
  PRIMARY KEY (id)
);

create unique index templates_id on templates(id);
create index templates_owner on templates(owner);
create index templates_published on templates(published);

# --- !Downs
