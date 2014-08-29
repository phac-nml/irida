DROP TABLE IF EXISTS oauth_access_token;

CREATE TABLE oauth_access_token (token_id VARCHAR(255), token LONGBLOB NOT NULL, authentication_id VARCHAR(255) NOT NULL, user_name VARCHAR(255) NOT NULL, client_id VARCHAR(255) NOT NULL, authentication LONGBLOB NOT NULL, refresh_token VARCHAR(255), PRIMARY KEY(token_id), CONSTRAINT `FK_OAUTH_TOKEN_CLIENT_DETAILS` FOREIGN KEY (`client_id`) REFERENCES `client_details` (`clientId`) ON DELETE CASCADE);
