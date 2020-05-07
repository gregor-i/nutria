# Fractal schema

# --- !Ups

CREATE TABLE fractals (
  id char(36) NOT NULL,
  fractal varchar NOT NULL,
  owner char(36) NOT NULL,
  published boolean NOT NULL,
  PRIMARY KEY (id)
);

create unique index fractals_id on fractals(id);
create index fractals_owner on fractals(owner);
create index fractals_published on fractals(published);

# --- !Downs
