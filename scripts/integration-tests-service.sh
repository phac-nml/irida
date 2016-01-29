#!/bin/bash
#rm -rf /tmp/gxbootstrap*
#rm -rf /tmp/shed_tools/
#pkill -u gitlab-runner -f "python ./scripts/paster.py" || true
echo 'drop database if exists irida_service_test; drop database if exists irida_service_galaxy_test; create database irida_service_test;create database irida_service_galaxy_test;' | mysql -u test -ptest
#pushd lib
#./install-libs.sh
#popd
xvfb-run -a mvn verify -Pservice_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_service_test" -Djetty.port=8081 -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_service_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" #> service_out.txt