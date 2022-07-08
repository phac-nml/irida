DROP TABLE IF EXISTS `oauth2_registered_client`;
DROP TABLE IF EXISTS `oauth2_authorization`;
DROP TABLE IF EXISTS `oauth2_authorization_consent`;

CREATE TABLE `oauth2_registered_client` (
    `id` varchar(100) NOT NULL,
    `client_id` varchar(100) NOT NULL,
    `client_id_issued_at` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `client_secret` varchar(200) DEFAULT NULL,
    `client_secret_expires_at` timestamp NULL DEFAULT NULL,
    `client_name` varchar(200) NOT NULL,
    `client_authentication_methods` varchar(1000) NOT NULL,
    `authorization_grant_types` varchar(1000) NOT NULL,
    `redirect_uris` varchar(1000) DEFAULT NULL,
    `scopes` varchar(1000) NOT NULL,
    `client_settings` varchar(2000) NOT NULL,
    `token_settings` varchar(2000) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `oauth2_authorization` (
    `id` varchar(100) NOT NULL,
    `registered_client_id` varchar(100) NOT NULL,
    `principal_name` varchar(200) NOT NULL,
    `authorization_grant_type` varchar(100) NOT NULL,
    `attributes` varchar(4000) DEFAULT NULL,
    `state` varchar(500) DEFAULT NULL,
    `authorization_code_value` blob DEFAULT NULL,
    `authorization_code_issued_at` timestamp NULL DEFAULT NULL,
    `authorization_code_expires_at` timestamp NULL DEFAULT NULL,
    `authorization_code_metadata` varchar(2000) DEFAULT NULL,
    `access_token_value` blob DEFAULT NULL,
    `access_token_issued_at` timestamp NULL DEFAULT NULL,
    `access_token_expires_at` timestamp NULL DEFAULT NULL,
    `access_token_metadata` varchar(2000) DEFAULT NULL,
    `access_token_type` varchar(100) DEFAULT NULL,
    `access_token_scopes` varchar(1000) DEFAULT NULL,
    `oidc_id_token_value` blob DEFAULT NULL,
    `oidc_id_token_issued_at` timestamp NULL DEFAULT NULL,
    `oidc_id_token_expires_at` timestamp NULL DEFAULT NULL,
    `oidc_id_token_metadata` varchar(2000) DEFAULT NULL,
    `refresh_token_value` blob DEFAULT NULL,
    `refresh_token_issued_at` timestamp NULL DEFAULT NULL,
    `refresh_token_expires_at` timestamp NULL DEFAULT NULL,
    `refresh_token_metadata` varchar(2000) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `oauth2_authorization_consent` (
    `registered_client_id` varchar(100) NOT NULL,
    `principal_name` varchar(200) NOT NULL,
    `authorities` varchar(1000) NOT NULL,
    PRIMARY KEY (`registered_client_id`, `principal_name`)
);

CREATE TABLE `oauth2_registered_client` (
    `id` varchar(100) NOT NULL,
    `client_id` varchar(100) NOT NULL,
    `client_id_issued_at` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `client_secret` varchar(200) DEFAULT NULL,
    `client_secret_expires_at` timestamp NULL DEFAULT NULL,
    `client_name` varchar(200) NOT NULL,
    `client_authentication_methods` varchar(1000) NOT NULL,
    `authorization_grant_types` varchar(1000) NOT NULL,
    `redirect_uris` varchar(1000) DEFAULT NULL,
    `scopes` varchar(1000) NOT NULL,
    `client_settings` varchar(2000) NOT NULL,
    `token_settings` varchar(2000) NOT NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_secret`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `scopes`, `client_settings`, `token_settings`) VALUES ('915e1a4f-8bef-4040-9bb2-5b899136e504', 'sequencer','N9Ywc6GKWWZotzsJGutj3BZXJDRn65fXJqjrk29yTjI', '915e1a4f-8bef-4040-9bb2-5b899136e504', 'client_secret_post,client_secret_basic', 'password', 'read,write', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",43200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.core.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",2592000.000000000]}');
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_secret`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `scopes`, `client_settings`, `token_settings`) VALUES ('8c5d5645-8c1a-4327-88e7-3e716960f484', 'linker', 'ZG5K1AFVSycE25ooxgcBRGCWFzSTfDnJ1DxSkdEmEho', '8c5d5645-8c1a-4327-88e7-3e716960f484', 'client_secret_post,client_secret_basic', 'password', 'read', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",43200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.core.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",2592000.000000000]}');
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_secret`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `scopes`, `client_settings`, `token_settings`) VALUES ('efd65c5c-a572-4433-9355-a3abc0dc0048', 'pythonLinker', 'bySZBP5jNO9pSZTz3omFRtJs3XFAvshxGgvXIlZ2zjk', 'efd65c5c-a572-4433-9355-a3abc0dc0048', 'client_secret_post,client_secret_basic', 'password', 'read', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",43200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.core.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",2592000.000000000]}');
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_secret`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `scopes`, `client_settings`, `token_settings`) VALUES ('cd7d55a1-970d-4ceb-a097-cd5168678864', 'testClient', '2022-07-08 15:06:52', 'testClientSecret', 'cd7d55a1-970d-4ceb-a097-cd5168678864', 'client_secret_post,client_secret_basic', 'password', 'read,write', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",43200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.core.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",2592000.000000000]}');
INSERT INTO `oauth2_registered_client` (`id`, `client_id`, `client_secret`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uri`, `scopes`, `client_settings`, `token_settings`) VALUES ('f727d1e0-922b-4caf-856e-9476532755b9', 'webClient', 'webClientSecret', 'f727d1e0-922b-4caf-856e-9476532755b9', 'client_secret_post,client_secret_basic', 'authorization_code', 'http://127.0.0.1:8080/api/oauth/authorization/token', 'read,write', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",43200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.core.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",2592000.000000000]}');