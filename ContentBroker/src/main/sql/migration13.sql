--
-- Name: known_errors; Type: TABLE; Schema: public; Owner: cb_usr; Tablespace: 
-- Adds new column advice and new advice text
--
ALTER TABLE known_errors ADD COLUMN advice varchar(1000);

UPDATE known_errors SET advice = 'Der weitere Ingest des betroffenen Pakets kann fortgesetzt werden, wird aber nicht empfohlen: Es sind Probleme mit der zukünftigen Bestandserhaltung möglich. Eine Übersteuerung dieses Fehlers wird für eine spätere Nachvollziehbarkeit gespeichert.' WHERE error_name = 'WRONG_DATA_TYPE_IPTC';

