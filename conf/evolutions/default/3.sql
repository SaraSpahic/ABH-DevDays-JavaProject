# --- !Ups

CREATE TABLE IF NOT EXISTS activity_log (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  timestamp timestamp without time zone NOT NULL,
  activity_type character varying(256) NOT NULL,
  description text
);

# --- !Downs

DROP TABLE IF EXISTS activity_log;
