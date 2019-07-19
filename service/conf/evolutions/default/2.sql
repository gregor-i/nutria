# Fractal Images schema

# --- !Ups

CREATE TABLE fractal_images (
  fractal_id varchar(255)
    REFERENCES fractals(id)
    UNIQUE,
  creation_time timestamp with time zone
    default current_timestamp,
  image bytea
);

# --- !Downs

DROP TABLE fractal_images;
