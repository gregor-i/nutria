# indices for fractals and fractal_images

# --- !Ups

truncate table fractal_images;
alter table fractal_images
    add column hash char(40) not null;

# --- !Downs

alter table fractal_images
    drop column hash;