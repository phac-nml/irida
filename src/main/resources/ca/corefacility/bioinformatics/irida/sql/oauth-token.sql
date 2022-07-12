DROP TABLE IF EXISTS `oauth2_authorization`;
DROP TABLE IF EXISTS `oauth2_authorization_consent`;

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
insert into client_details (id, clientId, clientSecret, redirect_uri, token_validity, createdDate) values (5, "webClient", "webClientSecret", "http://127.0.0.1:8080/api/oauth/authorization/token", 43200,now());

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

CREATE TABLE `oauth2_authorization` ( `id` varchar(100) NOT NULL, `registered_client_id` varchar(100) NOT NULL, `principal_name` varchar(200) NOT NULL, `authorization_grant_type` varchar(100) NOT NULL, `attributes` varchar(4000) DEFAULT NULL, `state` varchar(500) DEFAULT NULL, `authorization_code_value` blob DEFAULT NULL, `authorization_code_issued_at` timestamp NULL DEFAULT NULL, `authorization_code_expires_at` timestamp NULL DEFAULT NULL, `authorization_code_metadata` varchar(2000) DEFAULT NULL, `access_token_value` blob DEFAULT NULL, `access_token_issued_at` timestamp NULL DEFAULT NULL, `access_token_expires_at` timestamp NULL DEFAULT NULL, `access_token_metadata` varchar(2000) DEFAULT NULL, `access_token_type` varchar(100) DEFAULT NULL, `access_token_scopes` varchar(1000) DEFAULT NULL, `oidc_id_token_value` blob DEFAULT NULL, `oidc_id_token_issued_at` timestamp NULL DEFAULT NULL, `oidc_id_token_expires_at` timestamp NULL DEFAULT NULL, `oidc_id_token_metadata` varchar(2000) DEFAULT NULL, `refresh_token_value` blob DEFAULT NULL, `refresh_token_issued_at` timestamp NULL DEFAULT NULL, `refresh_token_expires_at` timestamp NULL DEFAULT NULL, `refresh_token_metadata` varchar(2000) DEFAULT NULL, PRIMARY KEY (`id`));

CREATE TABLE `oauth2_authorization_consent` ( `registered_client_id` varchar(100) NOT NULL, `principal_name` varchar(200) NOT NULL, `authorities` varchar(1000) NOT NULL, PRIMARY KEY (`registered_client_id`, `principal_name`));