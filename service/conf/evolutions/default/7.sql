# add owner to fractals
# owner = null means systemuser
# --- !Ups

ALTER TABLE fractals
    ADD COLUMN owner char(36),
    ADD COLUMN published boolean NOT NULL;

create index fractals_owner on fractals(owner);
create index fractals_published on fractals(published);

# --- !Downs
