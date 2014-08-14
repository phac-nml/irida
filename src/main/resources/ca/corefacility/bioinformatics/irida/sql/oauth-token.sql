DROP TABLE IF EXISTS oauth_access_token;

CREATE TABLE oauth_access_token (token_id VARCHAR(256), token LONGBLOB NOT NULL, authentication_id VARCHAR(256) NOT NULL, user_name VARCHAR(256) NOT NULL, client_id VARCHAR(256) NOT NULL, authentication LONGBLOB NOT NULL, refresh_token VARCHAR(256), PRIMARY KEY(token_id));
