#!/bin/bash

./install-libs.sh
popd
xvfb-run mvn clean verify -Pui_testing -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock"
