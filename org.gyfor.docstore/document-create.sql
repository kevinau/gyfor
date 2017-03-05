-- Table: public."documentStore"

DROP SEQUENCE document_id_seq CASCADE;
CREATE SEQUENCE document_id_seq;


DROP TABLE public.document;

CREATE TABLE public.document
(
  id integer NOT NULL DEFAULT nextval('document_id_seq'),
  hash_code character(17) not null,
  origin_time timestamp not null,
  origin_name character varying(32) not null,
  origin_extension character varying(8) not null,
  import_time timestamp not null
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.document
  OWNER TO postgres;
ALTER SEQUENCE document_id_seq OWNED BY document.id;

  
DROP TABLE public.document_segment;

CREATE TABLE public.document_segment
(
  document_id integer not null,
  page_index integer not null,
  segment_id integer not null,
  x0 float not null,
  y0 float not null,
  x1 float not null,
  y1 float not null,
  font_size float not null,
  text character varying (255),
  segment_type character (1),
  segment_value character varying (255)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.document_segment
  OWNER TO postgres;


DROP TABLE public.document_image;

CREATE TABLE public.document_image
(
  document_id integer not null,
  image_index integer not null,
  width int not null,
  height int not null
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.document_image
  OWNER TO postgres;
  