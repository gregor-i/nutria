# indices for fractals and fractal_images

# --- !Ups

create unique index fractals_id on fractals(id);
create unique index fractal_images_fractal_id on fractal_images(fractal_id);


# --- !Downs

drop index fractals_id;
drop index fractal_images_fractal_id