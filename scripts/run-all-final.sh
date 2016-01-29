#!/bin/bash

rm -rf /tmp/gxbootstrap*
rm -rf /tmp/shed_tools/
pkill -u gitlab-runner -f "python ./scripts/paster.py" || true

pushd lib
./install-libs.sh | while read line; do echo "[SETUP] $line"; done &
popd

# MySQL database initializations
echo 'drop database if exists irida_rest_test; drop database if exists irida_rest_galaxy_test; create database irida_rest_test;create database irida_rest_galaxy_test;' | mysql -u test -ptest
echo 'drop database if exists irida_service_test; drop database if exists irida_service_galaxy_test; create database irida_service_test;create database irida_service_galaxy_test;' | mysql -u test -ptest
echo 'drop database if exists irida_ui_test; drop database if exists irida_ui_galaxy_test; create database irida_ui_test;create database irida_ui_galaxy_test;' | mysql -u test -ptest
echo 'drop database if exists irida_galaxygroup_test; drop database if exists irida_galaxygroup_galaxy_test; create database irida_galaxygroup_test;create database irida_galaxygroup_galaxy_test;' | mysql -u test -ptest

# REST API integration tests
xvfb-run -a mvn verify -Prest_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_rest_test" -Djetty.port=8080 \
-Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_rest_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" \
| while read line; do echo "[REST] $line"; done &

# Service layer integration tests
xvfb-run -a mvn verify -Pservice_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_service_test" -Djetty.port=8081 \
-Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_service_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" \
| while read line; do echo "[SERVICE] $line"; done &

# UI integration tests
xvfb-run -a mvn verify -Pui_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_ui_test" -Djetty.port=8082 \
-Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_ui_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" \
| while read line; do echo "[UI] $line"; done &

# Galaxy integration tests
xvfb-run -a mvn verify -Pgalaxy_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_galaxygroup_test" -Djetty.port=8083 \
-Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_galaxygroup_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" \
| while read line; do echo "[GALAXY] $line"; done &

wait
echo "All integration tests complete"
