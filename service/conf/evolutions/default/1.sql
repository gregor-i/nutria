# Fractal schema

# --- !Ups

CREATE TABLE fractals (
  id varchar(255) NOT NULL,
  fractal varchar NOT NULL,
  PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE fractals;
