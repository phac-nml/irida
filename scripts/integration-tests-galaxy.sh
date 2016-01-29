#!/bin/bash
#rm -rf /tmp/gxbootstrap*
#rm -rf /tmp/shed_tools/
#pkill -u gitlab-runner -f "python ./scripts/paster.py" || true
echo 'drop database if exists irida_galaxygroup_test; drop database if exists irida_galaxygroup_galaxy_test; create database irida_galaxygroup_test;create database irida_galaxygroup_galaxy_test;' | mysql -u test -ptest
#pushd lib
#./install-libs.sh
#popd
xvfb-run -a mvn verify -Pgalaxy_testing -Djdbc.url="jdbc:mysql://localhost:3310/irida_galaxygroup_test" -Djetty.port=8083 -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_galaxygroup_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" #> galaxy_out.txt
