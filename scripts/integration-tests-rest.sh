#!/bin/bash
#rm -rf /tmp/gxbootstrap*
#rm -rf /tmp/shed_tools/
#pkill -u gitlab-runner -f "python ./scripts/paster.py" || true
echo 'drop database if exists irida_rest_test; drop database if exists irida_rest_galaxy_test; create database irida_rest_test;create database irida_rest_galaxy_test;' | mysql -u test -ptest
#pushd lib
#./install-libs.sh
#popd
xvfb-run -a mvn verify -Prest_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_rest_test" -Djetty.port=8080 -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_rest_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" #> rest_out.txt
