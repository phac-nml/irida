DROP TABLE IF EXISTS oauth_access_token;
DROP TABLE IF EXISTS oauth_refresh_token;

-- Data from client details must be manually removed due to FK constraints with oauth_access_token
DELETE FROM client_details_authorities;
DELETE FROM client_details_scope;
DELETE FROM client_details_grant_types;
DELETE FROM client_details_resource_ids;
DELETE FROM client_details;

-- client details
insert into client_details (id, clientId, clientSecret, token_validity, createdDate) values (1, "sequencer", "N9Ywc6GKWWZotzsJGutj3BZXJDRn65fXJqjrk29yTjI", 43200,now());
insert into client_details (id, clientId, clientSecret, token_validity, createdDate) values (2, "linker", "ZG5K1AFVSycE25ooxgcBRGCWFzSTfDnJ1DxSkdEmEho", 43200,now());
insert into client_details (id, clientId, clientSecret, token_validity, createdDate) values (3, "pythonLinker", "bySZBP5jNO9pSZTz3omFRtJs3XFAvshxGgvXIlZ2zjk", 43200,now());
insert into client_details (id, clientId, clientSecret, token_validity, createdDate) values (4, "testClient", "testClientSecret", 43200,now());
insert into client_details (id, clientId, clientSecret, redirect_uri, token_validity, createdDate) values (5, "webClient", "webClientSecret", "http://localhost:8080/api/oauth/authorization/token", 43200,now());

insert into client_role (name, description) values ("ROLE_CLIENT","A basic OAuth2 client");

insert into client_details_authorities (client_details_id,authority_name) values (1,"ROLE_CLIENT");
insert into client_details_authorities (client_details_id,authority_name) values (2,"ROLE_CLIENT");
insert into client_details_authorities (client_details_id,authority_name) values (3,"ROLE_CLIENT");
insert into client_details_authorities (client_details_id,authority_name) values (4,"ROLE_CLIENT");
insert into client_details_authorities (client_details_id,authority_name) values (5,"ROLE_CLIENT");

insert into client_details_scope (client_details_id,scope) values (1,"read"), (1,"write");
insert into client_details_scope (client_details_id,scope) values (2,"read");
insert into client_details_scope (client_details_id,scope) values (3,"read");
insert into client_details_scope (client_details_id,scope) values (4,"read"), (4,"write");
insert into client_details_scope (client_details_id,scope) values (5,"read"), (5,"write");

insert into client_details_grant_types (client_details_id,grant_value) values (1,"password");
insert into client_details_grant_types (client_details_id,grant_value) values (2,"password");
insert into client_details_grant_types (client_details_id,grant_value) values (3,"password");
insert into client_details_grant_types (client_details_id,grant_value) values (4,"password");
insert into client_details_grant_types (client_details_id,grant_value) values (5,"authorization_code");

insert into client_details_resource_ids (client_details_id,resource_id) values (1,"NmlIrida");
insert into client_details_resource_ids (client_details_id,resource_id) values (2,"NmlIrida");
insert into client_details_resource_ids (client_details_id,resource_id) values (3,"NmlIrida");
insert into client_details_resource_ids (client_details_id,resource_id) values (4,"NmlIrida");
insert into client_details_resource_ids (client_details_id,resource_id) values (5,"NmlIrida");

CREATE TABLE oauth_access_token (token_id VARCHAR(255), token LONGBLOB NOT NULL, authentication_id VARCHAR(255) NOT NULL, user_name VARCHAR(255) NOT NULL, client_id VARCHAR(255) NOT NULL, authentication LONGBLOB NOT NULL, refresh_token VARCHAR(255), PRIMARY KEY(token_id), CONSTRAINT `FK_OAUTH_TOKEN_CLIENT_DETAILS` FOREIGN KEY (`client_id`) REFERENCES `client_details` (`clientId`) ON DELETE CASCADE, UNIQUE (`authentication_id`));

CREATE TABLE oauth_refresh_token (token_id VARCHAR(255), token LONGBLOB NOT NULL, authentication LONGBLOB NOT NULL, PRIMARY KEY(token_id));