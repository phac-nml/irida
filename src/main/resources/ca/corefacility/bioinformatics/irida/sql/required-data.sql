-- user account required for integration tests
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'franklin.bristow@phac-aspc.gc.ca', 'Franklin', 'Bristow', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '7029', 'fbristow', 1, 'ROLE_USER', 1);
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'josh.adam@phac-aspc.gc.ca', 'Josh', 'Adam', 'en', '$1a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '7418', 'josh', 1, 'ROLE_USER', 1);
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'thomas.matthews@phac-aspc.gc.ca', 'Tom', 'Matthews', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '7418', 'tom', 1, 'ROLE_USER', 1);
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'admin@nowhere.ca', 'Admin', 'Admin', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '0000', 'admin', 1, 'ROLE_ADMIN', 1);
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'test@nowhere.ca', 'Test', 'User', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '1234', 'test', 1, 'ROLE_USER', 1);
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'disabled-guy@nowhere.ca', 'Disabled', 'Guy', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '0000', 'disabledguy', 0, 'ROLE_USER', 1);
INSERT INTO user (`createdDate`, `modifiedDate`, `email`, `firstName`, `lastName`, `locale`, `password`, `phoneNumber`, `username`, `enabled`, `system_role`, `credentialsNonExpired`) VALUES (now(), now() , 'manager@nowhere.ca', 'Mr.', 'Manager', 'en', '$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW', '1234', 'manager', 1, 'ROLE_MANAGER', 1);

insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 1', 'description 1');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 2', 'description 2');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 3', 'description 3');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 4', 'description 4');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 5', 'description 5');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 6', 'description 6');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 7', 'description 7');
insert into user_group(created_date, modified_date, name, description) values (now(), now(), 'group 8', 'description 8');

insert into user_group_member(created_date, role, group_id, user_id) values (now(), 'GROUP_OWNER', 8, 1);
insert into user_group_member(created_date, role, group_id, user_id) values (now(), 'GROUP_OWNER', 8, 2);
insert into user_group_member(created_date, role, group_id, user_id) values (now(), 'GROUP_OWNER', 8, 3);

-- projects required for integration tests
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2016-06-01 08:24:09' , 'Project 1', 'E. coli O157 I am a very long that is out fo control, this should be even longer');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-07-02 08:24:09' , 'Project 3', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-08-03 08:24:09' , 'Project 2', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-09-04 08:24:09' , 'Project 5', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-10-05 08:24:09' , 'Project 6', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-15 08:24:09' , 'Project 4', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-07 08:24:09' , 'Project 7', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-08 08:24:09' , 'Project 8', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-09 08:24:09' , 'Project 9', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-10 08:24:09' , 'Project 10', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-11 08:24:09' , 'Listeria Outbreak 2013', 'Listeria');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', now() , 'Listeria Outbreak 2014', 'Listeria');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', now() , 'Listeria Outbreak 2015', 'Listeria');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-14 08:24:09' , 'E. coli O157 Outbreak 2012', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-15 08:24:09' , 'Project 15', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-16 08:24:09' , 'Project 16', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-17 08:24:09' , 'Project 17', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-18 08:24:09' , 'Project 18', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-19 08:24:09' , 'Project 19', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-20 08:24:09' , 'Project 20', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-21 08:24:09' , 'Project 21', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-22 08:24:09' , 'Project 22', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-23 08:24:09' , 'Project 23', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-24 08:24:09' , 'Project 24', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-25 08:24:09' , 'Project 25', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-26 08:24:09' , 'Project 26', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`, `organism`) VALUES ('2015-06-01 08:24:09', '2015-06-27 08:24:09' , 'Project 27', 'E. coli');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-06-28 08:24:28' , 'Project 28');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-06-29 08:24:29' , 'Project 29');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-06-30 08:24:30' , 'Project 30');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-01 08:24:09' , 'Project 31');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-02 08:24:09' , 'Project 32');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-03 08:24:09' , 'Project 33');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-04 08:24:09' , 'Project 34');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-05 08:24:09' , 'Project 35');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-06 08:24:09' , 'Project 36');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-07 08:24:09' , 'Project 37');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-08 08:24:09' , 'Project 38');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-09 08:24:09' , 'Project 39');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-10 08:24:09' , 'Project 40');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-11 08:24:09' , 'Project 41');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-12 08:24:09' , 'Project 42');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-13 08:24:09' , 'Project 43');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-14 08:24:09' , 'Project 44');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-15 08:24:09' , 'Project 45');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-16 08:24:09' , 'Project 46');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-17 08:24:09' , 'Project 47');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-18 08:24:09' , 'Project 48');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-19 08:24:09' , 'Project 49');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-20 08:24:09' , 'Project 50');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-21 08:24:09' , 'Project 51');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-22 08:24:09' , 'Project 52');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-23 08:24:09' , 'Project 53');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-24 08:24:09' , 'Project 54');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-25 08:24:09' , 'Project 55');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-26 08:24:09' , 'Project 56');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-27 08:24:09' , 'Project 57');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-28 08:24:09' , 'Project 58');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-29 08:24:09' , 'Project 59');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-07-30 08:24:09' , 'Project 60');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-01 08:24:09' , 'Project 61');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-02 08:24:09' , 'Project 62');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-03 08:24:09' , 'Project 63');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-04 08:24:09' , 'Project 64');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-05 08:24:09' , 'Project 65');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-06 08:24:09' , 'Project 66');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-07 08:24:09' , 'Project 67');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-08 08:24:09' , 'Project 68');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-09 08:24:09' , 'Project 69');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-10 08:24:09' , 'Project 70');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-11 08:24:09' , 'Project 71');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-12 08:24:09' , 'Project 72');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-13 08:24:09' , 'Project 73');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-14 08:24:09' , 'Project 74');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-15 08:24:09' , 'Project 75');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-16 08:24:09' , 'Project 76');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-17 08:24:09' , 'Project 77');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-18 08:24:09' , 'Project 78');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-19 08:24:09' , 'Project 79');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-20 08:24:09' , 'Project 80');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-21 08:24:09' , 'Project 81');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-22 08:24:09' , 'Project 82');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-23 08:24:09' , 'Project 83');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-24 08:24:09' , 'Project 84');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-25 08:24:09' , 'Project 85');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-26 08:24:09' , 'Project 86');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-27 08:24:09' , 'Project 87');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-28 08:24:09' , 'Project 88');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-29 08:24:09' , 'Project 89');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-08-30 08:24:09' , 'Project 90');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-01 08:24:09' , 'Project 91');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-02 08:24:09' , 'Project 92');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-03 08:24:09' , 'Project 93');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-04 08:24:09' , 'Project 94');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-05 08:24:09' , 'Project 95');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-06 08:24:09' , 'Project 96');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-07 08:24:09' , 'Project 97');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-08 08:24:09' , 'Project 98');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-09 08:24:09' , 'Project 99');
INSERT INTO project (`createdDate`, `modifiedDate`, `name`) VALUES ('2015-06-01 08:24:09', '2015-09-10 08:24:09' , 'Project 100');

INSERT INTO user_group_project(`created_date`, `project_role`, `project_id`, `user_group_id`) values (now(), 'PROJECT_USER', 50, 8);

-- relationship between projects and users
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 1, 2, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 3, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 3, 1, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 4, 2, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 5, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 5, 3, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 6, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 6, 1, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 7, 2, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 8, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 8, 3, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 9, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 9, 3, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 10, 2, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 11, 2, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 12, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 12, 3, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 13, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 13, 1, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 14, 2, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 14, 3, 'PROJECT_OWNER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 1, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 2, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 3, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 4, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 5, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 6, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 7, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 8, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 9, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 10, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 11, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 12, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 13, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 14, 5, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 1, 1, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 2, 1, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 4, 1, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 5, 1, 'PROJECT_USER', 0);
INSERT INTO project_user (`createdDate`, `project_id`, `user_id`, `projectRole`, `email_subscription`) VALUES (now(), 100, 1, 'PROJECT_USER', 0);

-- genome assembly
INSERT INTO `genome_assembly` (id,created_date) VALUES (1, '2014-07-30 08:24:35');

-- samples
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:09', 'E. coli', '2015-07-01 08:24:09', '10-8727', 'The first sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:11', 'E. coli', '2015-07-02 08:24:09', '10-1928', 'The second sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:12', 'E. coli', '2015-07-03 08:24:09', '10-2295', 'The third sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:13', 'E. coli', '2015-07-04 08:24:09', '10-1358', 'The fourth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:14', 'E. coli', '2015-07-05 08:24:09', '10-1677', 'The fifth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:15', 'E. coli', '2015-07-06 08:24:09', '10-1930', 'The sixth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:16', 'S. pneumoniae', '2015-07-07 08:24:09', '10-2347', 'The seventh sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:17', 'S. pneumoniae', '2015-07-08 08:24:09', '10-1430', 'The eigth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:18', 'S. milleri', '2015-07-09 08:24:09', '15-7569', 'The nineth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:19', 'S. orisratti', '2015-07-10 08:24:09', '15-7570', 'The tenth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:20', 'S. sanguinis', '2015-07-11 08:24:09', '15-7570', 'The eleventh sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:21', 'E. coli', '2015-07-12 08:24:09', '15-7571', 'The twelveth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:22', 'E. coli', '2015-07-13 08:24:09', '15-7572', 'The thirteenth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:23', 'E. coli', '2015-07-14 08:24:09', '15-7573', 'The fourteenth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:24', 'E. coli', '2015-07-15 08:24:09', '15-7574', 'The fifteenth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:25', 'E. coli', '2015-07-16 08:24:09', '15-7575', 'The sixteenth sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:26', 'E. coli', '2015-07-17 08:24:09', '15-7576', 'The 17th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:27', 'E. coli', '2015-07-18 08:24:09', '15-7577', 'The 18th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:28', 'E. coli', '2015-07-19 08:24:09', '15-7578', 'The 19th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:29', 'E. coli', '2015-07-20 08:24:09', '15-7579', 'The 20th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:30', 'E. coli', '2015-07-21 08:24:09', '15-7580', 'The 21st sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:31', 'E. coli', '2015-07-22 08:24:09', '15-7581', 'The 22nd sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:32', 'E. coli', '2015-07-23 08:24:09', '15-7582', 'The 23rd sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:33', 'E. coli', '2015-07-24 08:24:09', '15-7583', 'The 24th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:34', 'E. coli', '2015-07-25 08:24:09', '15-7584', 'The 25th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:35', 'E. coli', '2015-07-26 08:24:09', '15-7585', 'The 26th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:36', 'E. coli', '2015-07-27 08:24:09', '15-7586', 'The 27th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:37', 'E. coli', '2015-07-28 08:24:09', '15-3614', 'The 28th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:38', 'E. coli', '2015-07-29 08:24:09', '15-4068', 'The 29th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:39', 'E. coli', '2015-07-30 08:24:09', '15-4163', 'The 30th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:40', 'E. coli', '2015-08-01 08:24:09', 'test-0001', 'The 31st sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:41', 'E. coli', '2015-08-02 08:24:09', 'test-0002', 'The 32nd sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:42', 'E. coli', '2015-08-03 08:24:09', 'test-0003', 'The 33rd sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:43', 'E. coli', '2015-08-04 08:24:09', 'test-0004', 'The 34th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:44', 'E. coli', '2015-08-05 08:24:09', 'test-0005', 'The 35th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:45', 'E. coli', '2015-08-06 08:24:09', 'test-0006', 'The 36th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:46', 'E. coli', '2015-08-07 08:24:09', 'test-0007', 'The 37th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:47', 'E. coli', '2015-08-08 08:24:09', 'test-0008', 'The 38th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:48', 'E. coli', '2015-08-09 08:24:09', 'test-0009', 'The 39th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:49', 'E. coli', '2015-08-10 08:24:09', 'test-0010', 'The 40th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:50', 'E. coli', '2015-08-11 08:24:09', 'test-0011', 'The 41st sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:51', 'E. coli', '2015-08-12 08:24:09', 'test-0012', 'The 42nd sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:52', 'E. coli', '2015-08-13 08:24:09', 'test-0013', 'The 43rd sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:53', 'E. coli', '2015-08-14 08:24:09', 'test-0014', 'The 44th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:54', 'E. coli', '2015-08-15 08:24:09', '10-5737', 'The 45th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:55', 'E. coli', '2015-08-16 08:24:09', '10-6966', 'The 46th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:56', 'E. coli', '2015-08-17 08:24:09', '10-7700', 'The 47th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:57', 'E. coli', '2015-08-18 08:24:09', '10-8231', 'The 48th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:58', 'E. coli', '2015-08-19 08:24:09', '10-6366', 'The 49th sample');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:59', 'E. coli', '2015-08-20 08:24:09', '10-7165', 'The 50th sample');
INSERT INTO sample (`createdDate`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:09', '2015-08-21 08:24:09' , '01-1111', 'The 51th sample');
INSERT INTO sample (`createdDate`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:10', '2015-08-22 08:24:09' , '02-2222', 'The 52nd sample');
INSERT INTO sample (`createdDate`, `modifiedDate`, `sampleName`, `description`, `latitude`, `longitude`) VALUES ('2014-07-30 08:24:10', '2015-08-23 08:24:09' , '03-3333', 'The 53rd sample', '49.8994','-97.1392');
INSERT INTO sample (`createdDate`,  `organism`, `modifiedDate`, `sampleName`, `description`) VALUES ('2014-07-30 08:24:59', 'E. coli', '2015-08-20 08:24:09', '10-7165', 'The 54th sample');

-- sample genome assembly
INSERT INTO sample_genome_assembly(`sample_id`, `genome_assembly_id`, `createdDate`) VALUES (54, 1, '2014-07-30 08:24:59');

-- sample relationship
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:09', 5, 1, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:11', 4, 2, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:12', 4, 3, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:13', 4, 4, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:14', 5, 5, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:15', 4, 6, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:16', 4, 7, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:17', 4, 8, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:18', 4, 9, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:19', 4, 10, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:20', 4, 11, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:21', 4, 12, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:22', 4, 13, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:23', 4, 14, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:24', 4, 15, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:25', 4, 16, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:26', 4, 17, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:27', 4, 18, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:28', 4, 19, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:29', 4, 20, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:30', 4, 21, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:31', 4, 22, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:32', 4, 23, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:33', 4, 24, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:34', 4, 25, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:35', 4, 26, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:36', 4, 27, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:37', 4, 28, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:38', 4, 29, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:39', 4, 30, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:40', 4, 31, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:41', 4, 32, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:42', 4, 33, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:43', 4, 34, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:44', 4, 35, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:45', 4, 36, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:46', 4, 37, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:47', 4, 38, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:48', 4, 39, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:49', 4, 40, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:50', 4, 41, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:51', 4, 42, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:52', 4, 43, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:53', 4, 44, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:54', 4, 45, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:55', 4, 46, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:56', 4, 47, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:57', 4, 48, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:58', 4, 49, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:24:59', 4, 50, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:25:59', 4, 51, 0);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:26:59', 4, 52, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:27:59', 4, 53, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:26:59', 5, 52, 1);

INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:26:59', 3, 1, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:27:59', 3, 2, 1);
INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:26:59', 3, 3, 1);

INSERT INTO project_sample (`createdDate`, `project_id`, `sample_id`, `owner`) VALUES ('2014-07-30 08:26:59', 3, 54, 1);
-- related projects
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (1,2,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (1,3,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (1,4,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (4,3,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (3,2,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (5,10,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (3,1,now());
insert into related_project (`subject_id`, `relatedProject_id`, `createdDate`) VALUES (2,5,now());

-- remote apis
insert into remote_api (name,clientId,clientSecret,description,serviceURI,createdDate) values ("Test Web Client","webClient","webClientSecret","A local testing api","http://localhost:8080/api",now());

-- sequence run
insert into sequencing_run (createdDate, description, modifiedDate, upload_status, layout_type, user_id) values ('2014-07-30 08:24:33','Superbug','2014-07-30 08:24:33','COMPLETE', "SINGLE_END", 1);
insert into sequencing_run (createdDate, description, modifiedDate, upload_status, layout_type, user_id) values ('2014-06-30 08:22:33','Buggy!','2014-06-30 08:24:33','COMPLETE', "SINGLE_END", 2);
insert into sequencing_run (createdDate, description, modifiedDate, upload_status, layout_type, user_id) values ('2014-05-30 08:22:33','Another one!!','2014-05-30 08:24:33','COMPLETE', "SINGLE_END", 3);
insert into miseq_run(id,workflow,read_lengths, application, assay, chemistry, experimentName, investigatorName,projectName) values (1,"test workflow",250, "FASTQ", "Nextera", "Amplicon", "Test Experiment", "Jon Doe", "Test Project");
insert into miseq_run(id,workflow,read_lengths, application, assay, chemistry, experimentName, investigatorName,projectName) values (2,"Another workflow",400, "FASTQ", "Nextera", "Amplicon", "Test 2 Experiment", "Jon Doe", "Test Project 2");
insert into miseq_run(id,workflow,read_lengths, application, assay, chemistry, experimentName, investigatorName,projectName) values (3,"craxy workflow", 300, "FASTQ", "Nextera", "Amplicon", "Test 3 Experiment", "Jon Doe", "Test Project 3");

-- sequence files
insert into sequence_file (id, created_date, file_revision_number, modified_date, file_path) values (2, '2014-07-30 08:24:34',2,'2014-07-30 08:24:34','/tmp/sequence-files/2/2/02-2222_S1_L001_R2_001.fastq');
insert into sequence_file (id, created_date, file_revision_number, modified_date, file_path) values (1, '2014-07-30 08:24:34',2,'2014-07-30 08:24:34','/tmp/sequence-files/1/2/02-2222_S1_L001_R1_001.fastq');
insert into sequence_file (id, created_date, file_revision_number, modified_date, file_path) values (3, '2014-07-30 08:24:35',2,'2014-07-30 08:24:35','/tmp/sequence-files/3/2/01-1111_S1_L001_R1_001.fastq');
insert into sequence_file (id, created_date, file_revision_number, modified_date, file_path) values (4, '2014-07-30 08:24:35',2,'2014-07-30 08:24:35','/tmp/sequence-files/4/2/01-1111_S1_L001_R2_001.fastq');
insert into sequence_file (id, created_date, file_revision_number, modified_date, file_path) values (5, '2014-07-30 08:24:35',2,'2014-07-30 08:24:35','/tmp/sequence-files/5/2/03-3333_S1_L001_R1_001.fastq');
insert into sequence_file (id, created_date, file_revision_number, modified_date, file_path) values (6, '2014-07-30 08:24:35',2,'2014-07-30 08:24:35','/tmp/sequence-files/6/2/03-3333_S1_L001_R2_001.fastq');

INSERT INTO `sequence_file` (id, created_date, file_revision_number, modified_date, file_path) VALUES (7, '2014-08-06 10:01:02', 2, '2014-08-06 10:01:02', '/tmp/sequence-files/7/2/02-2222_S1_L001_R1_001.fastq'), (8, '2014-08-06 10:01:03', 2, '2014-08-06 10:01:03', '/tmp/sequence-files/8/2/02-2222_S1_L001_R2_001.fastq'), (9, '2014-08-06 10:01:03', 2, '2014-08-06 10:01:03', '/tmp/sequence-files/9/2/01-1111_S1_L001_R1_001.fastq'), (10, '2014-08-06 10:01:03', 2, '2014-08-06 10:01:03', '/tmp/sequence-files/10/2/01-1111_S1_L001_R2_001.fastq'), (11, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/11/2/03-3333_S1_L001_R1_001.fastq'), (12, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/03-3333_S1_L001_R2_001.fastq'), (13, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0001_S1_L001_R1_001.fastq'), (14, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0001_S1_L001_R2_001.fastq'), (15, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0003_S1_L001_R1_001.fastq'), (16, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0003_S1_L001_R2_001.fastq'), (17, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0002_S1_L001_R1_001.fastq'), (18, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0002_S1_L001_R2_001.fastq'), (19, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0008_S1_L001_R1_001.fastq'), (20, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0008_S1_L001_R2_001.fastq'), (21, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0012_S1_L001_R1_001.fastq'), (22, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0012_S1_L001_R2_001.fastq'), (23, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0013_S1_L001_R1_001.fastq'), (24, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0013_S1_L001_R2_001.fastq'), (25, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0005_S1_L001_R1_001.fastq'), (26, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0005_S1_L001_R2_001.fastq'), (27, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0010_S1_L001_R1_001.fastq'), (28, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0010_S1_L001_R2_001.fastq'), (29, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0011_S1_L001_R1_001.fastq'), (30, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0011_S1_L001_R2_001.fastq'), (31, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0006_S1_L001_R1_001.fastq'), (32, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0006_S1_L001_R2_001.fastq'), (33, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0007_S1_L001_R1_001.fastq'), (34, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0007_S1_L001_R2_001.fastq'), (35, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0014_S1_L001_R1_001.fastq'), (36, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0014_S1_L001_R2_001.fastq'), (37, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0004_S1_L001_R1_001.fastq'), (38, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0004_S1_L001_R2_001.fastq'), (39, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0009_S1_L001_R1_001.fastq'), (40, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0009_S1_L001_R2_001.fastq'), (41, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0010-newname_S1_L001_R1_001.fastq'), (42, '2014-08-06 10:01:04', 2, '2014-08-06 10:01:04', '/tmp/sequence-files/12/2/test-0010-newname_S1_L001_R2_001.fastq');

-- sequencing object
insert into sequencing_object (id, created_date, sequencing_run_id) values (1, '2014-07-30 08:24:34', 1), (2, '2014-07-30 08:24:34', 1), (3, '2014-07-30 08:24:34', 1),(4, '2014-07-30 08:24:34', 1),(5, '2014-07-30 08:24:34', 1),(6, '2014-07-30 08:24:34', 1),(7, '2014-07-30 08:24:34', 1),(8, '2014-07-30 08:24:34', 1),(9, '2014-07-30 08:24:34', 1),(10, '2014-07-30 08:24:34', 1),(11, '2014-07-30 08:24:34', 1),(12, '2014-07-30 08:24:34', 1),(13, '2014-07-30 08:24:34', 1),(14, '2014-07-30 08:24:34', 1),(15, '2014-07-30 08:24:34', 1),(16, '2014-07-30 08:24:34', 1),(17, '2014-07-30 08:24:34', 1),(18, '2014-07-30 08:24:34', 1),(19, '2014-07-30 08:24:34', 1),(20, '2014-07-30 08:24:34', 1),(21, '2014-07-30 08:24:34', 1),(22, '2014-07-30 08:24:34', 2),(23, '2014-07-30 08:24:34', 2),(24, '2014-07-30 08:24:34', 2), (25, '2014-07-30 08:24:34', 2);

-- sequence_file_pair
INSERT INTO `sequence_file_pair` (id) VALUES (1), (2), (11), (12), (13), (14), (15), (16), (17), (18), (19), (20), (21), (22), (23), (24), (25);

-- sequence_file_pair_files
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (1, 1);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (1, 2);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (2, 3);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (2, 4);

INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (11, 13);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (11, 14);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (12, 15);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (12, 16);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (13, 17);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (13, 18);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (14, 19);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (14, 20);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (15, 21);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (15, 22);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (16, 23);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (16, 24);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (17, 25);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (17, 26);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (18, 27);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (18, 28);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (19, 29);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (19, 30);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (20, 31);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (20, 32);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (21, 33);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (21, 34);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (22, 35);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (22, 36);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (23, 37);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (23, 38);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (24, 39);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (24, 40);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (25, 41);
INSERT INTO `sequence_file_pair_files` (pair_id, files_id) VALUES (25, 42);

-- sequence_file_single_end
insert into sequence_file_single_end (id, file_id) values (3, 5), (4,6), (5,7), (6,8), (7,9), (8,10), (9,11), (10,12);


-- sequencefile sample
INSERT INTO `sample_sequencingobject` (created_date, sample_id, sequencingobject_id) VALUES ('2014-07-30 08:24:34', 52, 1), ('2014-07-30 08:24:35', 52, 2), ('2014-07-30 08:24:35', 53, 3), ('2014-07-30 08:24:35', 53, 4), ('2014-08-06 10:01:03', 52, 5), ('2014-08-06 10:01:03', 52, 6), ('2014-08-06 10:01:03', 51, 7), ('2014-08-06 10:01:04', 51, 8), ('2014-08-06 10:01:04', 53, 9), ('2014-08-06 10:01:04', 53, 10), ('2014-08-06 10:01:04', 31, 11), ('2014-08-06 10:01:04', 32, 13), ('2014-08-06 10:01:04', 33, 12), ('2014-08-06 10:01:04', 34, 23), ('2014-08-06 10:01:04', 35, 17), ('2014-08-06 10:01:04', 36, 20), ('2014-08-06 10:01:04', 37, 21), ('2014-08-06 10:01:04', 38, 14), ('2014-08-06 10:01:04', 39, 24), ('2014-08-06 10:01:04', 40, 18), ('2014-08-06 10:01:04', 41, 19), ('2014-08-06 10:01:04', 42, 15), ('2014-08-06 10:01:04', 43, 16), ('2014-08-06 10:01:04', 44, 22), ('2014-08-06 10:01:04', 54, 25);

-- qc_entry
INSERT INTO `qc_entry` (created_date, sequencingObject_id, DTYPE) VALUES (now(), 3, 'FileProcessorErrorQCEntry');
INSERT INTO `qc_entry` (created_date, sequencingObject_id, total_bases, DTYPE) VALUES (now(), 3, '100', 'CoverageQCEntry');

-- analysis
INSERT INTO `analysis` (id, createdDate, description, executionManagerAnalysisId, analysis_type) VALUES (1,'2014-07-30 08:24:35','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(2,'2014-07-30 08:24:35','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(3,'2014-07-30 08:24:34','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(4,'2014-07-30 08:24:35','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(5,'2014-07-30 08:24:35','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(6,'2014-07-30 08:24:35','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(7,'2014-08-06 10:01:04','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(8,'2014-08-06 10:01:03','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(9,'2014-08-06 10:01:03','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(10,'2014-08-06 10:01:04','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(11,'2014-08-06 10:01:04','Analysis produced by FastQC','internal-fastqc', 'FASTQC'),(12,'2014-08-06 10:01:03','Analysis produced by FastQC','internal-fastqc', 'FASTQC');
INSERT INTO `analysis` (id, createdDate, executionManagerAnalysisId, analysis_type) VALUES (13,'2014-07-30 08:24:35', 'Whole Genome Phylogenomics Pipeline', 'PHYLOGENOMICS');
INSERT INTO `analysis` (id, createdDate, executionManagerAnalysisId, analysis_type) VALUES (14,'2014-07-30 08:24:35', 'SISTR Pipeline PASS', 'SISTR_TYPING');
INSERT INTO `analysis` (id, createdDate, executionManagerAnalysisId, analysis_type) VALUES (15,'2014-07-30 08:24:35', 'SISTR Pipeline FAIL', 'SISTR_TYPING');
INSERT INTO `analysis` (id, createdDate, executionManagerAnalysisId, analysis_type) VALUES (16,'2014-07-30 08:24:35', 'Assembly Test', 'ASSEMBLY_ANNOTATION');
INSERT INTO `analysis` (id, createdDate, executionManagerAnalysisId, analysis_type) VALUES (17,'2014-07-30 08:24:35', 'Bio Hansel PASS', 'BIO_HANSEL');

-- tool_execution
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (1, '2015-03-04 13:58:46', 'galaxyIdentifier', 'SNP Matrix', '0.0.1', '/bin/snp-matrix');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (2, '2015-03-04 13:58:49', 'galaxyIdentifier', 'VCF 2 pseudoalignment', '0.0.10', '/bin/vcf2pseudoalign');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (3, '2015-03-04 13:58:45', 'galaxyIdentifier', 'Find Repeats', '0.0.2', '/bin/find-repeats');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (5, '2015-03-04 13:58:45', 'galaxyIdentifier', 'Filter vcf', '0.0.1', '/bin/filter-vcf');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (14, '2015-03-04 13:58:45', 'galaxyIdentifier', 'bcftools view', '0.0.1', '/bin/bcftools');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (4, '2015-03-04 13:58:45', 'galaxyIdentifier', 'Upload File', '1.1.4', '/bin/upload');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (6, '2015-03-04 13:58:45', 'galaxyIdentifier', 'FreeBayes', '0.0.4', '/bin/freebayes');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (7, '2015-03-04 13:58:45', 'galaxyIdentifier', 'Upload File', '1.1.4', '/bin/upload');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (8, '2015-03-04 13:58:45', 'galaxyIdentifier', 'SAM-to-BAM', '1.1.4', '/bin/sam-to-bam');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (9, '2015-03-04 13:58:45', 'galaxyIdentifier', 'sistr_cmd', '0.4.3', '/bin/sistr');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (10, '2015-03-04 13:58:45', 'galaxyIdentifier', 'sistr_cmd', '0.4.3', '/bin/sistr');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (11, '2015-03-04 13:58:45', 'galaxyIdentifier', 'assembly', '0.4.3', '/bin/assembly');
INSERT INTO `tool_execution` (id, created_date, execution_manager_identifier, tool_name, tool_version, command_line) VALUES (12, '2015-03-04 13:58:45', 'galaxyIdentifier', 'bio_hansel', '2.0.0', '/bin/bio_hansel');

-- tool_execution_parameters for "SNP Matrix"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('1', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');

-- tool_execution_parameters for "VCF 2 pseudoalignment"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('2', 'ambiguous', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('2', 'coverage', '15');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('2', 'numcpus', '4');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('2', 'reference', 'Reference');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('2', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');

-- tool_execution_parameters for "Find Repeats"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('3', 'length', '150');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('3', 'pid', '90');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('3', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');

-- tool_execution_parameters for "Filter vcf"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('5', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');

-- tool_execution_parameters for "bcftools view"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'b', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'c', 'true');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'e', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'g', 'true');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'i.alt_indel_snp_ratio', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'p.variant_filter', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 't.mutation_rate', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'u', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'v', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'a', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'd.seq_dictionary', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'f', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'n', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', 'd', 'false');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('14', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');


-- tool_execution_parameters for "Upload File"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('4', 'files.file_data', '/Warehouse/Applications/irida/galaxy/galaxy-dist/database/tmp/upload_file_data_zpavd2');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('4', 'files.to_posix_lines', 'Yes');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('4', 'files.url_paste', 'None');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('4', 'files.name', 'M04-240196.fasta');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('4', 'files_metadata.file_type', 'fasta');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('4', 'file_type', 'fasta');

-- tool_execution_parameters for "FreeBayes"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.dont_left_align_indels', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.input_variant_type.input_variant_type_selector', 'do_not_provide');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.options_type_selector', 'advanced');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.output_failed_alleles_option', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.output_trace_option', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.report_all_haplotype_alleles', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.exclude_unobserved_genotypes', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.genotype_qualities', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.genotype_variant_threshold', '');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.genotyping_max_banddepth', '7');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.genotyping_max_iterations', '1000');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.harmonic_indel_quality', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.posterior_integration_limits_m', '3');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.posterior_integration_limits_n', '1');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.read_dependence_factor', '0.9');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.report_genotype_likelihood_max', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.section_algorithmic_features_type_selector', 'set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.site_selection_max_iterations', '5');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_algorithmic_features_type.use_mapping_quality', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_allele_scope_type.section_allele_scope_type_selector', 'do_not_set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.indel_exclusion_window', '-1');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.min_alternate_count', '2');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.min_alternate_fraction', '0.75');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.min_alternate_qsum', '0');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.min_alternate_total', '1');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.min_coverage', '15');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.mismatch_base_quality_threshold', '10');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.quality_filter_type.min_base_quality', '30');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.quality_filter_type.min_mapping_quality', '30');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.quality_filter_type.min_supporting_allele_qsum', '0');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.quality_filter_type.min_supporting_mapping_qsum', '0');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.quality_filter_type.quality_filter_type_selector', 'apply_filters');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.read_indel_limit', '');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.read_max_mismatch_fraction', '1.0');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.read_mismatch_limit', '');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.read_snp_limit', '');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.section_input_filters_type_selector', 'set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_input_filters_type.use_duplicate_reads', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_mappability_priors_expectations_type.section_mappability_priors_expectations_type_selector', 'do_not_set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_population_model_type.ploidy', '1');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_population_model_type.pooled_continuous', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_population_model_type.pooled_discrete', 'False');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_population_model_type.section_population_model_type_selector', 'set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_population_model_type.theta', '0.001');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.section_population_priors_type.section_population_priors_type_selector', 'do_not_set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'options_type.target_limit_type.target_limit_type_selector', 'do_not_set');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'reference_source.input_bams.input_bam', '6723');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'reference_source.reference_source_selector', 'history');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', 'reference_source.ref_file', '6706');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('6', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');

-- tool_execution_parameters for "Upload File"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('7', 'files.file_data', '/Warehouse/Applications/irida/galaxy/galaxy-dist/database/tmp/upload_file_data_zpavd2');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('7', 'files.to_posix_lines', 'Yes');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('7', 'files.url_paste', 'None');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('7', 'files.name', 'M04-240196.fasta');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('7', 'files_metadata.file_type', 'fasta');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('7', 'file_type', 'fasta');

-- tool_execution_parameters for "SAM-TO-BAM"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('8', 'source.index_source', 'history');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('8', 'source.input1', '6712');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('8', 'source.ref_file', '6706');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('8', '__workflow_invocation_uuid__', 'e4acd686c29f11e49e0f5254006919ab');

-- tool_execution_parameters for "sistr"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('9', 'key', 'value');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('10', 'key', 'value');

-- tool_execution_parameters for "bio_hansel"
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'dev_args.use_json', 'true');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'kmer_vals.kmer_max', '1000');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'kmer_vals.kmer_min', '8');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'qc_vals.low_coverage_warning', '20');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'qc_vals.low_cov_depth_freq', '20');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'qc_vals.max_intermediate_tiles', '0.05');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'qc_vals.max_missing_tiles', '0.05');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'qc_vals.min_ambiguous_tiles', '3');
INSERT INTO `tool_execution_parameters` (tool_id, execution_parameter_key, execution_parameter_value) VALUES ('12', 'type_of_scheme.scheme_type ', 'heidelberg');

-- tool_execution_prev_steps
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (1, 2);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (2, 3);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (2, 5);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (2, 14);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (3, 4);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (5, 6);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (6, 7);
INSERT INTO `tool_execution_prev_steps` (tool_execution_id, tool_execution_prev_id) VALUES (6, 8);

-- analysis_output_file
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (1, '2014-08-06 10:01:02', 'Whole Genome Phylogenomics Pipeline', '/tmp/analysis-files/phylogeneticTree.newick', 1, NULL);
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (2, '2014-08-06 10:01:02', 'Whole Genome Phylogenomics Pipeline', '/tmp/analysis-files/snpMatrix.tsv', 2, NULL);
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (3, '2014-08-06 10:01:02', 'Whole Genome Phylogenomics Pipeline', '/tmp/analysis-files/snpTable.tsv', 3, NULL);
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (4, '2014-08-06 10:01:02', 'SISTR Pipeline PASS', '/tmp/analysis-files/sistr-predictions-pass.json', 9, NULL);
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (5, '2014-08-06 10:01:02', 'SISTR Pipeline FAIL', '/tmp/analysis-files/sistr-predictions-fail.json', 10, NULL);
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (6, '2014-08-06 10:01:02', 'Assembly Test', '/tmp/analysis-files/contigs.fasta', 11, 'sample');
INSERT INTO `analysis_output_file` (id, created_date, execution_manager_file_id, file_path, tool_execution_id, label_prefix) VALUES (7, '2014-08-06 10:01:02', 'Bio Hansel PASS', '/tmp/analysis-files/bio_hansel-results.json', 12, NULL);


-- analysis_output_file_map
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (13, 1, 'tree');
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (13, 2, 'matrix');
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (13, 3, 'table');
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (14, 4, 'sistr-predictions');
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (15, 5, 'sistr-predictions');
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (16, 6, 'contigs-with-repeats');
INSERT INTO `analysis_output_file_map` (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) VALUES (17, 7, 'bio_hansel-results.json');

-- analysis_fastqc
INSERT INTO `analysis_fastqc` (encoding, fileType, filteredSequences, gcContent, maxLength, minLength, totalBases, totalSequences, id, fastqcVersion) VALUES ('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,1,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,2,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,3,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,4,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,5,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,6,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,7,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,8,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,9,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,10,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,11,'0.10.1'),('Sanger / Illumina 1.9','Conventional base calls',0,30,251,184,937,4,12,'0.10.1');

-- overrepresented_sequence
INSERT INTO `overrepresented_sequence` (id, createdDate, overrepresentedSequenceCount, percentage, possibleSource, sequence) VALUES (1,'2014-07-30 08:24:36',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(2,'2014-07-30 08:24:36',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(3,'2014-07-30 08:24:36',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(4,'2014-07-30 08:24:36',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(5,'2014-07-30 08:24:36',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(6,'2014-07-30 08:24:36',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(7,'2014-07-30 08:24:36',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(8,'2014-07-30 08:24:36',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(9,'2014-07-30 08:24:36',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(10,'2014-07-30 08:24:36',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(11,'2014-07-30 08:24:36',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(12,'2014-07-30 08:24:36',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(13,'2014-07-30 08:24:36',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(14,'2014-07-30 08:24:36',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(15,'2014-07-30 08:24:36',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(16,'2014-07-30 08:24:36',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(17,'2014-07-30 08:24:36',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(18,'2014-07-30 08:24:36',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(19,'2014-07-30 08:24:36',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(20,'2014-07-30 08:24:36',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(21,'2014-07-30 08:24:36',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(22,'2014-07-30 08:24:36',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(23,'2014-07-30 08:24:36',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(24,'2014-07-30 08:24:36',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(25,'2014-08-06 10:01:05',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(26,'2014-08-06 10:01:05',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(27,'2014-08-06 10:01:05',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(28,'2014-08-06 10:01:05',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(29,'2014-08-06 10:01:05',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(30,'2014-08-06 10:01:05',1,25.00,'No Hit','CTTCTAAATCACGCTGATGCAAAGAACTGATAAAACAATTGTGATTTTCT'),(31,'2014-08-06 10:01:05',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(32,'2014-08-06 10:01:05',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(33,'2014-08-06 10:01:05',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(34,'2014-08-06 10:01:05',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(35,'2014-08-06 10:01:05',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(36,'2014-08-06 10:01:05',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(37,'2014-08-06 10:01:05',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(38,'2014-08-06 10:01:05',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(39,'2014-08-06 10:01:05',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(40,'2014-08-06 10:01:05',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(41,'2014-08-06 10:01:05',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(42,'2014-08-06 10:01:05',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(43,'2014-08-06 10:01:05',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(44,'2014-08-06 10:01:05',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(45,'2014-08-06 10:01:05',1,25.00,'No Hit','CAGTTATGAATACTTATTATATAGGGCTTTTAAAGAATTCTAAAAATCCA'),(46,'2014-08-06 10:01:05',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT'),(47,'2014-08-06 10:01:05',1,25.00,'No Hit','TTATTTCTTTGCAAATCTATATGGAACAGAACTTGTTATCTATCTTAAAT'),(48,'2014-08-06 10:01:05',1,25.00,'No Hit','GGCATAGGTATTTAAAGCATTTATAAGCTCCATTCTTTTTGCACTAAAAT');

-- analysis_fastqc_overrepresented_sequence
INSERT INTO `analysis_fastqc_overrepresented_sequence` (analysis_fastqc_id, overrepresentedSequences_id) VALUES (8,25),(10,26),(11,27),(9,28),(7,29),(12,30),(10,31),(8,32),(11,33),(7,34),(9,35),(7,36),(8,37),(10,38),(11,39),(9,40),(8,41),(7,42),(9,43),(11,44),(12,45),(10,46),(12,47),(12,48);

-- analysis_properties

-- reference_file
INSERT INTO `reference_file` (id, createdDate, filePath, fileRevisionNumber, modifiedDate) VALUES (1, '2014-08-06 10:01:02', '/tmp/sequence-files/7/2/02-2222_S1_L001_R1_001.fastq', 1, '2014-08-06 10:01:02');

-- project_referencefile
INSERT INTO `project_referencefile` (id, createdDate, project_id, reference_file_id) VALUES (1, '2014-08-06 10:01:02', 4, 1);

-- analysis_submission
INSERT INTO `analysis_submission` (analysis_state, name, created_date, analysis_id, workflow_id, submitter, analysis_cleaned_state, priority, update_samples, automated, email_pipeline_result, DTYPE) VALUES ('COMPLETED', 'Analysis-1', '2014-08-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 2, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-2', '2014-07-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-3', '2014-07-09 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-4', '2014-07-15 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-5', '2014-07-21 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-6', '2014-07-21 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-7', '2014-07-22 10:01:03', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-8', '2014-07-23 10:01:04', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-9', '2014-07-24 10:01:05', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-10', '2014-07-25 10:01:06', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-11', '2014-07-26 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-12', '2014-07-27 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-13_has_a_very_long_name_that_really_needs_to_be_handled_well', '2014-07-28 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-14', '2014-07-29 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-15', '2014-07-20 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-16', '2014-07-21 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-17', '2010-07-21 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-18', '2011-07-21 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-19', '2012-07-21 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-20', '2013-07-21 10:03:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-21', '2014-08-06 10:07:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-22', '2014-01-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-24', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-25', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-26', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-27', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-28', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-29', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-30', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-31', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-32', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-33', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-34', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-35', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-36', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-37', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-38', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-39', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-40', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-41', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-42', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-43', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-44', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-45', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-46', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-47', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-48', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARED', 'Analysis-49', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-50', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-51', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-52', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-53', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-54', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-55', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-56', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-57', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-58', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'Analysis-59', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-60', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-61', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-62', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-63', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-64', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-65', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-66', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-67', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-68', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-69', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-70', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-71', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-72', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('ERROR', 'Analysis-73', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-74', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-75', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-76', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-77', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-78', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-79', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-80', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-81', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-82', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-83', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('SUBMITTING', 'Analysis-84', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-85', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('NEW', 'Analysis-86', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-87', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-88', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-89', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-90', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-91', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-92', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-93', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-94', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-95', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-96', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-97', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-98', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-99', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('PREPARING', 'Analysis-100', '2014-02-06 10:01:02', 13, 'b7c8b437-3c41-485e-92e5-72b67e37959f', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'SISTR PASS 101', '2014-02-06 10:01:02', 14, 'e8f9cc61-3264-48c6-81d9-02d9e84bccc7', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'SISTR FAIL 102', '2014-02-06 10:01:02', 15, 'e8f9cc61-3264-48c6-81d9-02d9e84bccc7', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission'), ('COMPLETED', 'BIO_HANSEL PASS 101', '2014-02-06 10:01:02', 17, '0e8738e6-2aeb-4627-9d6e-15ae32f21c44', 1, 'NOT_CLEANED', 'MEDIUM', 0, 0, 0,'AnalysisSubmission');

INSERT INTO `project_analysis_submission` (created_date, project_id, analysis_submission_id) VALUES (now(), 4, 1),(now(), 4, 2),(now(), 4, 3),(now(), 4, 4),(now(), 4, 5), (now(), 4, 13);

-- genome assembly
INSERT INTO `genome_assembly_analysis` (id,analysis_submission_id) VALUES (1, 102);

INSERT INTO `workflow_named_parameters` (id, created_date, name, workflow_id) VALUES (1, now(), 'NML SNVPhyl', 'b7c8b437-3c41-485e-92e5-72b67e37959f');
INSERT INTO `workflow_named_parameter_values` (named_parameters_id, named_parameter_value, named_parameter_name) VALUES (1, '-1', 'repeat-minimum-length');
INSERT INTO `workflow_named_parameter_values` (named_parameters_id, named_parameter_value, named_parameter_name) VALUES (1, '-2', 'repeat-minimum-pid');
INSERT INTO `workflow_named_parameter_values` (named_parameters_id, named_parameter_value, named_parameter_name) VALUES (1, '-3', 'snv-abundance-ratio');
INSERT INTO `workflow_named_parameter_values` (named_parameters_id, named_parameter_value, named_parameter_name) VALUES (1, '-6', 'minimum-read-coverage');

-- analysis_submission_sequence_file_pair
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 11);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 12);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 13);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 14);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 15);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 16);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 17);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 18);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 19);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 20);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 21);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 22);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 23);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (13, 24);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (100 ,1);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (101 ,2);
INSERT INTO `analysis_submission_sequencing_object` (analysis_submission_id, sequencing_object_id) VALUES (102 ,1);

INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), 'PROJECT_OWNER', 1, 2, NULL, 'UserRoleSetProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), 'PROJECT_USER', 3, 2, NULL, 'UserRoleSetProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), 'PROJECT_OWNER', 3, 1, NULL, 'UserRoleSetProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 3, 5, 1, 'SampleAddedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 3, 4, 2, 'SampleAddedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 3, 4, 3, 'SampleAddedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');
INSERT INTO `project_event` (created_date, role, project_id, user_id, sample_id, DTYPE) values (now(), NULL, 1, 3, NULL, 'UserRemovedProjectEvent');

INSERT INTO `ncbi_export_submission` (id, created_date, bio_project_id, namespace, organization, upload_state, project_id, submitter) VALUES (1, now(), 'FakeBioProjet001', 'IRIDA', 'IRIDA', 'NEW', 4, 3);
INSERT INTO `ncbi_export_biosample` (id, bioSample, instrument_model, library_construction_protocol, library_name, library_selection, library_source, library_strategy, submission_status) VALUES ('SUB001XXX', 'FakeBioSample001', 'ILLUMINAMISEQ', 'Fake Library Prep', 'Library001', 'RANDOM', 'GENOMIC', 'WGS', 'NEW');
INSERT INTO `ncbi_export_submission_biosample` (ncbi_export_submission_id, bioSampleFiles_id) VALUES (1, 'SUB001XXX');
INSERT INTO `ncbi_export_biosample_sequence_file_pair` (ncbi_export_biosample_id, pairs_id) VALUES ('SUB001XXX', 1);

-- announcement
INSERT INTO `announcement` (id, created_date, message, created_by_id) VALUES (1, now(), "Welcome **to IRIDA!**", 4);
INSERT INTO `announcement` (id, created_date, message, created_by_id) VALUES (2, '2016-08-15 08:05:53', "Here's `another` announcement!", 4);
INSERT INTO `announcement` (id, created_date, message, created_by_id) VALUES (3, '2016-08-10 08:05:53', "Oh man this is cool!", 4);
INSERT INTO `announcement` (id, created_date, message, created_by_id) VALUES (4, '2016-08-05 08:05:53', "Time to **analyze!**", 4);
INSERT INTO `announcement` (id, created_date, message, created_by_id) VALUES (5, '2016-07-25 08:05:53', "You are **now** ready to try science.", 4);
INSERT INTO `announcement` (id, created_date, message, created_by_id) VALUES (6, '2016-06-10 08:05:53', "**Don't** forget **your** *wallet*", 4);

-- announcement_user
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (1, now(), 1, 4);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (2, now(), 2, 4);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (3, now(), 3, 4);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (4, now(), 4, 4);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (5, now(), 5, 4);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (6, now(), 6, 4);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (7, now(), 2, 1);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (8, now(), 2, 2);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (9, now(), 2, 3);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (10, now(), 2, 5);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (11, now(), 2, 6);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (12, now(), 4, 1);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (13, now(), 4, 2);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (14, now(), 4, 3);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (15, now(), 4, 5);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (16, now(), 4, 6);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (17, now(), 6, 1);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (18, now(), 6, 2);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (19, now(), 6, 3);
INSERT INTO `announcement_user` (id, created_date, announcement_id, user_id) VALUES (20, now(), 6, 5);

INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (1, "PFGE-XbaI-pattern", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (2, "PFGE-BlnI-pattern", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (3, "Province", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (4, "SourceSite", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (5, "Genus", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (6, "Serotype", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (7, "onsetDate", "date", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (8, "species", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (10, "primaryPfge", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (11, "phageType", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (12, "id", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (13, "lastName", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (14, "firstName", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (15, "secondaryPfge", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (16, "birthDate", "date", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (17, "healthAuthority", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (18, "firstSymptom", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE) VALUES (19, "city", "text", "MetadataTemplateField");
INSERT INTO `metadata_field` (id, label, type, DTYPE, static_id) VALUES (20, "Created Date", "date", "StaticMetadataTemplateField", "created");
INSERT INTO `metadata_field` (id, label, type, DTYPE, static_id) VALUES (21, "Modified Date", "date", "StaticMetadataTemplateField", "modified");


-- METADATA TEMPLATES
INSERT INTO `metadata_template` (id, created_date, modified_date, name) VALUES (1, '2016-06-10 08:05:53', '2016-07-10 08:05:53', 'Test Date Template 01');
INSERT INTO `metadata_template` (id, created_date, modified_date, name) VALUES (2, '2016-07-10 04:45:53', '2016-07-10 08:05:53', 'Special Pathogen Template');

-- METADATA TEMPLATE METADATA FIELD
-- METADATA TEMPLATE 1 FIELDS
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (1, 1);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (1, 2);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (1, 3);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (1, 4);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (1, 5);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (1, 6);

-- METADATA TEMPLATE 2 FIELDS
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (2, 1);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (2, 2);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (2, 3);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (2, 5);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (2, 8);
INSERT INTO `metadata_template_metadata_field` (metadata_template_id, fields_id) values (2, 11);

-- PROJECT METADATA TEMPLATE
INSERT INTO `project_metadata_template` (id, created_date, project_id, template_id) VALUES (1, '2016-06-10 08:05:53',4, 1);
INSERT INTO `project_metadata_template` (id, created_date, project_id, template_id) VALUES (2, '2017-01-11 08:05:53',4, 2);

INSERT INTO `metadata_entry` (id, value, type) VALUES (1, 'Jon', 'text'), (2, 'Doe', 'text'), (3, 'Winnipeg', 'text'), (4, 'Winnipeg Regional Health Authority', 'text'), (5, '1987-11-28', 'text'), (6, 'Coughing', 'text'), (7, '2015-10-15T10:16:42+00:00', 'text'), (8, 'Salmonella', 'text'), (9, 'XXX', 'text'), (10, 'ABC123', 'text'), (11, 'DEF456', 'text'), (12, '5', 'text'), (13, 'Damion', 'text'), (14, 'Dooley', 'text'), (15, 'Vancouver', 'text'), (16, 'BC Fraser Health Authority', 'text'), (17, '1966-01-13', 'text'), (18, 'Blurred vision', 'text'), (19, '2015-02-11T10:16:42+00:00', 'text'), (20, 'Salmonella enterica', 'text'), (21, 'SENXAI.0001', 'text'), (22, 'SENBNI.0017', 'text'), (23, 'DEF456', 'text'), (24, 'DT4b', 'text'), (25, 'Foo', 'text'), (26, 'Bar', 'text'), (27, 'Comox', 'text'), (28, 'BC Vancouver Island Health Authority', 'text'), (29, '1986-02-28', 'text'), (30, 'Diarrhea', 'text'), (31, '2015-06-04T10:16:42+00:00', 'text'), (32, 'Salmonella enterica', 'text'), (33, '', 'text'), (34, 'SENXAI.0038', 'text'), (35, 'SENBNI.0016', 'text'), (36, 'DT13', 'text');

INSERT INTO `sample_metadata_entry` (sample_id, metadata_id, metadata_KEY) VALUES (51, 1, 14), (51, 2, 13), (51, 3, 19), (51, 4, 17), (51, 5, 16), (51, 6, 18), (51, 7, 7), (51, 8, 8), (51, 9, 6), (51, 10, 10), (51, 11, 15), (51, 12, 11), (52, 13, 14), (52, 14, 13), (52, 15, 19), (52, 16, 17), (52, 17, 16), (52, 18, 18), (52, 19, 7), (52, 20, 8), (52, 21, 6), (52, 22, 10), (52, 23, 15), (52, 24, 11), (53, 25, 14), (53, 26, 13), (53, 27, 19), (53, 28, 17), (53, 29, 16), (53, 30, 18), (53, 31, 7), (53, 32, 8), (53, 33, 6), (53, 34, 10), (53, 35, 15), (53, 36, 11);
