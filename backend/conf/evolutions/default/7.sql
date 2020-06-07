# add 'inserted_at' and 'updated_at' to 'images' and 'templates'

# --- !Ups

alter table images
  add updated_at timestamp with time zone,
  add inserted_at timestamp with time zone;

update images
  set updated_at = now(), inserted_at = now();

alter table images
  alter column updated_at set not null,
  alter column inserted_at set not null;

alter table templates
  add updated_at timestamp with time zone,
  add inserted_at timestamp with time zone;

update templates
  set updated_at = now(), inserted_at = now();

alter table templates
  alter column updated_at set not null,
  alter column inserted_at set not null;

# --- !Downs
