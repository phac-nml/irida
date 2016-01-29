#!/bin/bash

echo 'drop database if exists irida_rest_test; drop database if exists irida_rest_galaxy_test; create database irida_rest_test;create database irida_rest_galaxy_test;' | mysql -u test -ptest

xvfb-run -a mvn verify -Prest_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_rest_test" -Djetty.port=8080 -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_rest_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" #> rest_out.txt
