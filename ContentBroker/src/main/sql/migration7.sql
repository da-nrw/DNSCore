begin;
--
-- Name: systemevent; Type: TABLE; Schema: public; Owner: cb_usr; Tablespace:
--

CREATE TABLE systemevent (
    id integer NOT NULL,
    last_executed timestamp without time zone,
    parameter character varying(255),
    period character varying(255),
    type character varying(255),
    node_id integer,
    user_id integer
);


ALTER TABLE public.systemevent OWNER TO cb_usr;

rollback;
