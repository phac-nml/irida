#!/bin/bash
#rm -rf /tmp/gxbootstrap*
#rm -rf /tmp/shed_tools/
#pkill -u gitlab-runner -f "python ./scripts/paster.py" || true
echo 'drop database if exists irida_ui_test; drop database if exists irida_ui_galaxy_test; create database irida_ui_test;create database irida_ui_galaxy_test;' | mysql -u test -ptest
#pushd lib
#./install-libs.sh
#popd
xvfb-run -a mvn verify -Pui_testing -Djdbc.url="jdbc:mysql://localhost:3306/irida_ui_test" -Djetty.port=8082 -Dtest.galaxy.database.connection="mysql://test:test@localhost/irida_ui_galaxy_test?unix_socket=/var/run/mysqld/mysqld.sock" #> ui_out.txt
