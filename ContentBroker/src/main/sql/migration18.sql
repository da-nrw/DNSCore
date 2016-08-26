begin;

ALTER TABLE known_errors ADD COLUMN advice varchar(1000);


update known_errors SET advice='Der weitere Ingest des betroffenen Pakets kann fortgesetzt werden, wird aber nicht empfohlen: Es sind Probleme mit der zukünftigen Bestandserhaltung möglich. Eine Übersteuerung dieses Fehlers wird für eine spätere Nachvollziehbarkeit gespeichert.' where id=1;
commit;