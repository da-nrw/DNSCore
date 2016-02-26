begin;
--
-- Name: known_errors; Type: TABLE; Schema: public; Owner: cb_usr; Tablespace: 
--

CREATE TABLE known_errors (
    id integer NOT NULL,
    description character varying(255),
    error_name character varying(100),
    question character varying(255),
    std_err_contains_regex character varying(255)
);

--
-- Name: known_errors_pkey; Type: CONSTRAINT; Schema: public; Owner: cb_usr; Tablespace: 
--

ALTER TABLE ONLY known_errors
    ADD CONSTRAINT known_errors_pkey PRIMARY KEY (id);
    
INSERT INTO known_errors (id,error_name,std_err_contains_regex,description,question) VALUES (1,'WRONG_DATA_TYPE_IPTC','(?s).*RichTIFFIPTC.*TIFFErrors.*','Probleme mit IPTC Tag im IFD bei BigTiff','IPTC_ERROR_STORE_ALLOWED?');
    
--
-- Name: dafile_knownerror; Type: TABLE; Schema: public; Owner: cb_usr; Tablespace: 
--

CREATE TABLE dafile_knownerror (
    dafile_id integer NOT NULL,
    knownerror_id integer NOT NULL
);

ALTER TABLE public.dafile_knownerror OWNER TO cb_usr;

--
-- Name: fk_dqi669jwb2trscbqp9swgvgj6; Type: FK CONSTRAINT; Schema: public; Owner: cb_usr
--

ALTER TABLE ONLY dafile_knownerror
    ADD CONSTRAINT fk_dqi669jwb2trscbqp9swgvgj6 FOREIGN KEY (dafile_id) REFERENCES dafiles(id);



ALTER TABLE ONLY dafile_knownerror
    ADD CONSTRAINT fk_nxn3g97s3jy8s40ovr9vlllpy FOREIGN KEY (knownerror_id) REFERENCES known_errors(id);


commit;
