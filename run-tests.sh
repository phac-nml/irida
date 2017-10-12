#!/bin/bash
cd `dirname $0`
SCRIPT_DIR=`pwd`

DATABASE_NAME=irida_integration_test
DATABASE_USER=test
DATABASE_PASSWORD=test
JDBC_URL=jdbc:mysql://localhost:3306/$DATABASE_NAME
TMP_DIRECTORY=/tmp
GALAXY_DOCKER=phacnml/galaxy-irida-17.01:0.17.0-it
GALAXY_DOCKER_NAME=irida-galaxy-test
GALAXY_PORT=48889
GALAXY_URL=http://localhost:$GALAXY_PORT
GALAXY_INVALID_URL=http://localhost:48890
GALAXY_INVALID_URL2=http://localhost:48891
CHROME_DRIVER=$SCRIPT_DIR/src/main/webapp/node_modules/chromedriver/lib/chromedriver/chromedriver

DO_KILL_DOCKER=true

check_dependencies() {
	mvn --version 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		echo "Command 'mvn' does not exist.  Please install Maven (e.g., 'apt-get install maven') to continue."
		exit 1
	fi

	xvfb-run -h 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		echo "Command 'xvfb-run' does not exist.  Please install xvfb (e.g., 'apt-get install xvfb') to continue."
		exit 1
	fi

	docker --version 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		echo "Command 'docker' does not exist.  Please install Docker (e.g., 'curl -sSL https://get.docker.com/ | sh') to continue."
		exit 1
	fi

	mysql --version 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		echo "Command 'mysql' does not exist.  Please install MySQL/MariaDB (e.g., 'apt-get install mariadb-client mariadb-server') to continue."
		exit 1
	fi
}

clean_database_docker() {
	DB_ERR="Failed to clean/create new database named '$DATABASE_NAME'. Perhaps you need to grant permission first with 'echo \"grant all privileges on $DATABASE_NAME.* to '$DATABASE_USER'@'localhost';\" | mysql -u root -p'."

	set -x
	echo "drop database if exists $DATABASE_NAME; create database $DATABASE_NAME;" | mysql -u$DATABASE_USER -p$DATABASE_PASSWORD
	if [ $? -ne 0 ];
	then
		set +x
		echo $DB_ERR
		exit 1
	fi
	mysql -u$DATABASE_USER -p$DATABASE_PASSWORD $DATABASE_NAME < $SCRIPT_DIR/ci/irida_latest.sql
	if [ $? -ne 0 ];
	then
		set +x
		echo $DB_ERR
		exit 1
	fi

	if [ "$(docker ps | grep $GALAXY_DOCKER_NAME)" != "" ];
	then
		docker rm -f -v $GALAXY_DOCKER_NAME
	fi
}

test_service() {
	clean_database_docker
	mvn clean verify -B -Pservice_testing -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY $@
}

test_rest() {
	clean_database_docker
	mvn clean verify -Prest_testing -B -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY $@
}

test_ui() {
	clean_database_docker
	xvfb-run --auto-servernum --server-num=1 mvn clean verify -B -Pui_testing -Dwebdriver.chrome.driver=$CHROME_DRIVER -Dirida.it.nosandbox=true -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY $@
}

test_galaxy() {
	clean_database_docker
	docker run -d -p $GALAXY_PORT:80 --name $GALAXY_DOCKER_NAME -v $TMP_DIRECTORY:$TMP_DIRECTORY -v $SCRIPT_DIR:$SCRIPT_DIR $GALAXY_DOCKER
	mvn clean verify -B -Pgalaxy_testing -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dtest.galaxy.url=$GALAXY_URL -Dtest.galaxy.invalid.url=$GALAXY_INVALID_URL -Dtest.galaxy.invalid.url2=$GALAXY_INVALID_URL2 $@
	if [ "$DO_KILL_DOCKER" = true ]; then docker rm -f -v $GALAXY_DOCKER_NAME; fi
}

test_all() {
	clean_database_docker
	docker run -d -p $GALAXY_PORT:80 --name $GALAXY_DOCKER_NAME -v $TMP_DIRECTORY:$TMP_DIRECTORY -v $SCRIPT_DIR:$SCRIPT_DIR $GALAXY_DOCKER
	xvfb-run --auto-servernum --server-num=1 mvn clean verify -B -Pall_testing -Dwebdriver.chrome.driver=$CHROME_DRIVER -Dirida.it.nosandbox=true -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dtest.galaxy.url=$GALAXY_URL -Dtest.galaxy.invalid.url=$GALAXY_INVALID_URL -Dtest.galaxy.invalid.url2=$GALAXY_INVALID_URL2 $@
	if [ "$DO_KILL_DOCKER" = true ]; then docker rm -f -v $GALAXY_DOCKER_NAME; fi
}

############
### MAIN ###
############

if [ $# -eq 0 ];
then
	echo -e "Usage: $0 [-d database] [--no-kill-docker] test_type [Maven options]"
	echo -e "Options:"
	echo -e "\t-d|--database:   Override name of database ($DATABASE_NAME) used for testing."
	echo -e "\t--no-kill-docker: Do not kill Galaxy Docker after Galaxy tests have run."
	echo -e "\ttest_type:     One of the IRIDA test types {service_testing, ui_testing, rest_testing, galaxy_testing, all_testing}."
	echo -e "\t[Maven options]: Additional options to pass to 'mvn'.  In particular, can pass '-Dtest.it=ca.corefacility.bioinformatics.irida.fully.qualified.name' to run tests from a particular class.\n"
	echo -e "Example:\n"
	echo -e "$0 service_testing"
	echo -e "\tThis will test the Service layer of IRIDA, cleaning up the test database/docker containers first.\n"
	echo -e "$0 -d irida_integration_test2 galaxy_testing"
	echo -e "\tRuns the Galaxy integration tests for IRIDA, using a database named 'irida_integration_test2'."
	echo -e "\tThis will also attempt to launch Galaxy Docker on $GALAXY_URL\n"
	echo -e "$0 rest_testing -Dtest.it=ca.corefacility.bioinformatics.irida.web.controller.test.integration.analysis.RESTAnalysisSubmissionControllerIT"
	echo -e "\tThis will run IRIDA REST API tests found in the class 'RESTAnalysisSubmissionControllerIT'. This will *not* clean up the previous tests database files."
	exit 0
fi

check_dependencies

cd $SCRIPT_DIR

while [ "$1" = "--database" -o "$1" = "-d" -o "$1" = "--no-kill-docker" ];
do
	if [ "$1" = "--database" -o "$1" = "-d" ];
	then
		shift
		DATABASE_NAME=$1
		shift
	elif [ "$1" = "--no-kill-docker" ];
	then
		DO_KILL_DOCKER=false
		shift
	else
		shift
	fi
done

case "$1" in
	service_testing)
		shift
		test_service $@
	;;
	ui_testing)
		shift
		test_ui $@
	;;
	rest_testing)
		shift
		test_rest $@
	;;
	galaxy_testing)
		shift
		test_galaxy $@
	;;
	all_testing)
		shift
		test_all $@
	;;
	*)
		echo "Unrecogized test [$1]"
		exit 1
	;;
esac
