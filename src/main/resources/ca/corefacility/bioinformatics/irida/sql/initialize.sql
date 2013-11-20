INSERT INTO system_role (`name`,`description`) VALUES ('ROLE_USER','A basic user in the system.');
INSERT INTO system_role (`name`,`description`) VALUES ('ROLE_ADMIN','An administrative user in the system.');
INSERT INTO system_role (`name`,`description`) VALUES ('ROLE_CLIENT','A client tool in the system.');
INSERT INTO system_role (`name`,`description`) VALUES ('ROLE_MANAGER','A manager role in the system.');

-- user account required for integration tests
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'you@e-mail.ca', 'Administrator', 'Administrator', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '867-5309', 'admin', 1, 'ROLE_ADMIN', 0);
