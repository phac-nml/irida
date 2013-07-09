drop table audit if exists;
drop table project if exists; 

CREATE TABLE audit (id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, resource integer , createdBy varchar(45) , created timestamp NULL );
CREATE TABLE project (id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, audit integer , name varchar(100));
CREATE TABLE identifiable (id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, audit integer , nonNull varchar(100), integerValue integer, label varchar(100));

ALTER TABLE project ADD CONSTRAINT fk_project_audit FOREIGN KEY (audit) REFERENCES audit(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE identifiable ADD CONSTRAINT fk_identifiable_audit FOREIGN KEY (audit) REFERENCES audit(id) ON DELETE CASCADE ON UPDATE CASCADE;
