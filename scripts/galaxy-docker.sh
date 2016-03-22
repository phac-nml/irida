#!/usr/bin/env bash



xvfb-run mvn clean verify -Pgalaxy_testing -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock"