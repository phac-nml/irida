#!/bin/bash
cd `dirname $0`
SCRIPT_DIR=`pwd`

DATABASE_NAME=irida_integration_test
DATABASE_USER=test
DATABASE_PASSWORD=test
DATABASE_HOST=localhost
DATABASE_PORT=3306
JDBC_URL=jdbc:mysql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME
TMP_DIRECTORY=`mktemp -d /tmp/irida-test-XXXXXXXX`
chmod 777 $TMP_DIRECTORY # Needs to be world-accessible so that Docker/Galaxy can access

GALAXY_DOCKER=phacnml/galaxy-irida-20.09:21.05.2-it
GALAXY_DOCKER_NAME=irida-galaxy-test
GALAXY_PORT=48889
GALAXY_URL=http://localhost:$GALAXY_PORT
GALAXY_INVALID_URL=http://localhost:48890
GALAXY_INVALID_URL2=http://localhost:48891
CHROME_DRIVER=$SCRIPT_DIR/src/main/webapp/chromedriver
SELENIUM_DOCKER_NAME=irida-selenium
SELENIUM_DOCKER_TAG=latest
SELENIUM_URL=http://localhost:4444/wd/hub
HOSTNAME=`hostname`

DO_KILL_DOCKER=true
NO_CLEANUP=false
HEADLESS=true
SELENIUM_DOCKER=false

OPEN_API_FILE=doc/swagger-ui/open-api.json

if [ -z "$DB_MAX_WAIT_MILLIS" ];
then
	export DB_MAX_WAIT_MILLIS=10000
fi

check_dependencies() {
	mvn --version 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		exit_error "Command 'mvn' does not exist.  Please install Maven (e.g., 'apt-get install maven') to continue."
	fi

	docker --version 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		exit_error "Command 'docker' does not exist.  Please install Docker (e.g., 'curl -sSL https://get.docker.com/ | sh') to continue."
	fi

	mysql --version 1>/dev/null 2>/dev/null
	if [ $? -ne 0 ];
	then
		exit_error "Command 'mysql' does not exist.  Please install MySQL/MariaDB (e.g., 'apt-get install mariadb-client mariadb-server') to continue."
	fi
}

pretest_cleanup() {
	JDBC_URL=jdbc:mysql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME

	if [ "$NO_CLEANUP" = true ];
	then
		return
	else
		DB_ERR="Failed to clean/create new database named '$DATABASE_NAME'. Perhaps you need to grant permission first with 'echo \"grant all privileges on $DATABASE_NAME.* to '$DATABASE_USER'@'localhost';\" | mysql -u root -p'."

		set -x
		echo "drop database if exists $DATABASE_NAME; create database $DATABASE_NAME;" | mysql -h $DATABASE_HOST -P $DATABASE_PORT -u$DATABASE_USER -p$DATABASE_PASSWORD
		if [ $? -ne 0 ];
		then
			set +x
			exit_error $DB_ERR
		fi
		mysql -h $DATABASE_HOST -P $DATABASE_PORT -u$DATABASE_USER -p$DATABASE_PASSWORD $DATABASE_NAME < $SCRIPT_DIR/ci/irida_latest.sql
		if [ $? -ne 0 ];
		then
			set +x
			exit_error $DB_ERR
		fi

		if [ "$(docker ps | grep $GALAXY_DOCKER_NAME)" != "" ];
		then
			docker rm -f -v $GALAXY_DOCKER_NAME
		fi

		tmp_dir_cleanup
	fi
}

tmp_dir_cleanup() {
	# Remove any file contents from these directories (possibly from other tests)
	rm -rf $TMP_DIRECTORY/*
	rm -f $OPEN_API_FILE
}

posttest_cleanup() {
	rm -rf $TMP_DIRECTORY
	rm -f $OPEN_API_FILE
}

exit_error() {
	echo $1
	posttest_cleanup
	exit 1
}

test_service() {
	mvn clean verify -B -Pservice_testing -Dspring.datasource.url=$JDBC_URL -Dfile.processing.decompress=true -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dspring.datasource.dbcp2.max-wait=$DB_MAX_WAIT_MILLIS $@
	exit_code=$?
	return $exit_code
}

test_rest() {
	mvn clean verify -B -Prest_testing -Dspring.datasource.url=$JDBC_URL -Dfile.processing.decompress=true -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dspring.datasource.dbcp2.max-wait=$DB_MAX_WAIT_MILLIS $@
	exit_code=$?
	return $exit_code
}

test_ui() {
    SELENIUM_OPTS=""
    if [ "$SELENIUM_DOCKER" = false ];
    then
		if [ -z ${CHROMEWEBDRIVER} ]
		then
			# use default CHROME_DRIVER if CHROMEWEBDRIVER env var is not set
        	SELENIUM_OPTS="-Dwebdriver.chrome.driver=$CHROME_DRIVER"
		else
			if [ -d $CHROMEWEBDRIVER ] && [ -f "$CHROMEWEBDRIVER/chromedriver" ]
			then
				SELENIUM_OPTS="-Dwebdriver.chrome.driver=$CHROMEWEBDRIVER/chromedriver"
			else
				SELENIUM_OPTS="-Dwebdriver.chrome.driver=$CHROMEWEBDRIVER"
			fi
		fi
    else
        # create the $TMP_DIRECTORY/irida folder before docker runs so that root doesn't create it
        mkdir -p $TMP_DIRECTORY/irida
        # reuse selenium docker image if it exists
        docker start $SELENIUM_DOCKER_NAME || docker run -d -p 4444:4444 --name $SELENIUM_DOCKER_NAME -v $PWD:$PWD -v $TMP_DIRECTORY/irida:$TMP_DIRECTORY/irida -v /dev/shm:/dev/shm selenium/standalone-chrome:$SELENIUM_DOCKER_TAG
        SELENIUM_OPTS="-Dwebdriver.selenium_url=$SELENIUM_URL -Dserver.port=33333 -Dserver.base.url=http://$HOSTNAME:33333/irida -Djava.io.tmpdir=$TMP_DIRECTORY/irida"
    fi
	mvn clean verify -B -Pui_testing $SELENIUM_OPTS -Dirida.it.nosandbox=true -Dirida.it.headless=$HEADLESS -Dspring.datasource.url=$JDBC_URL -Dfile.processing.decompress=true -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dspring.datasource.dbcp2.max-wait=$DB_MAX_WAIT_MILLIS $@
	exit_code=$?
	if [[ "$DO_KILL_DOCKER" = true && "$SELENIUM_DOCKER" = true ]]; then docker rm -f -v $SELENIUM_DOCKER_NAME; fi
	return $exit_code
}

test_galaxy() {
	test_galaxy_internal galaxy_testing $@
	exit_code=$?
	return $exit_code
}

test_galaxy_pipelines() {
	test_galaxy_internal galaxy_pipeline_testing $@
	exit_code=$?
	return $exit_code
}

test_galaxy_internal() {
	profile=$1
	shift

	docker run -d -p $GALAXY_PORT:80 --name $GALAXY_DOCKER_NAME -v $TMP_DIRECTORY:$TMP_DIRECTORY -v $SCRIPT_DIR:$SCRIPT_DIR $GALAXY_DOCKER && \
	mvn clean verify -B -P$profile -Dfile.processing.decompress=true -Dspring.datasource.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dtest.galaxy.url=$GALAXY_URL -Dtest.galaxy.invalid.url=$GALAXY_INVALID_URL -Dtest.galaxy.invalid.url2=$GALAXY_INVALID_URL2 -Dspring.datasource.dbcp2.max-wait=$DB_MAX_WAIT_MILLIS $@
	exit_code=$?
	if [ "$DO_KILL_DOCKER" = true ]; then docker rm -f -v $GALAXY_DOCKER_NAME; fi
	return $exit_code
}

test_doc() {
	mvn clean site $@
	exit_code=$?
	return $exit_code
}

test_open_api() {
	mvn clean verify -B -Dspring-boot.run.arguments="--spring.datasource.url=$JDBC_URL --spring.datasource.dbcp2.max-wait=$DB_MAX_WAIT_MILLIS --spring.profiles.active=dev,swagger" -Dspring.profiles.active=dev,swagger -DskipTests=true -Dliquibase.update.database.schema=true -Dspring.datasource.url=$JDBC_URL -Dspring.datasource.dbcp2.max-wait=$DB_MAX_WAIT_MILLIS
	test -f $OPEN_API_FILE
	exit_code=$?
	return $exit_code
}

test_all() {
	for test_profile in test_rest test_service test_ui test_galaxy test_galaxy_pipelines test_doc test_open_api;
	do
		tmp_dir_cleanup
		eval $test_profile
		if [ $? -ne 0 ];
		then
			exit_error "FAILED at $test_profile"
		fi
	done

	echo "SUCCESS for all integration tests"
	return 0
}

############
### MAIN ###
############

if [ $# -eq 0 ];
then
	echo -e "Usage: $0 [options..] test_type [Maven options]"
	echo -e "Options:"
	echo -e "\t-d|--database:   Override name of database ($DATABASE_NAME) used for testing."
	echo -e "\t-c|--no-cleanup: Do not cleanup previous test database before execution."
	echo -e "\t--db-host:   Override the database host ($DATABASE_HOST) used for testing."
	echo -e "\t--db-port:   Override the port used to connect to the database ($DATABASE_PORT) for testing."
	echo -e "\t--no-kill-docker: Do not kill Galaxy Docker after Galaxy tests have run."
	echo -e "\t--no-headless: Do not run chrome in headless mode (for viewing results of UI tests)."
	echo -e "\t--selenium-docker: Use selenium/standalone-chrome docker container for executing UI tests."
	echo -e "\ttest_type:     One of the IRIDA test types {service_testing, ui_testing, rest_testing, galaxy_testing, galaxy_pipeline_testing, doc_testing, open_api_testing, all}."
	echo -e "\t[Maven options]: Additional options to pass to 'mvn'.  In particular, can pass '-Dit.test=ca.corefacility.bioinformatics.irida.fully.qualified.name' to run tests from a particular class.\n"
	echo -e "Examples:\n"
	echo -e "$0 service_testing\n"
	echo -e "\tThis will test the Service layer of IRIDA, cleaning up the test database/docker containers first.\n"
	echo -e "$0 -d irida_integration_test2 galaxy_testing\n"
	echo -e "\tRuns the Galaxy integration tests for IRIDA, using a database named 'irida_integration_test2'."
	echo -e "\tThis will also attempt to launch Galaxy Docker on $GALAXY_URL\n"
	echo -e "$0 rest_testing -Dit.test=ca.corefacility.bioinformatics.irida.web.controller.test.integration.analysis.RESTAnalysisSubmissionControllerIT\n"
	echo -e "\tThis will run IRIDA REST API tests found in the class 'RESTAnalysisSubmissionControllerIT'.\n"
	echo -e "$0 all\n"
	echo -e "\tThis will run all integration tests in IRIDA, reporting 'SUCCESS for all integration tests' on successessful completion of all tests."

	posttest_cleanup
	exit 0
fi

check_dependencies

cd $SCRIPT_DIR

while [ "$1" = "--database" -o "$1" = "-d" -o "$1" = "--db-host" -o "$1" = "--db-port" -o "$1" = "--no-kill-docker" -o "$1" = "-c" -o "$1" = "--no-cleanup" -o "$1" = "--no-headless" -o "$1" = "--selenium-docker" ];
do
	if [ "$1" = "--database" -o "$1" = "-d" ];
	then
		shift
		DATABASE_NAME=$1
		shift
	elif [ "$1" = "--db-host" ];
	then
		shift
		DATABASE_HOST=$1
		shift
	elif [ "$1" = "--db-port" ];
	then
		shift
		DATABASE_PORT=$1
		shift
	elif [ "$1" = "--no-kill-docker" ];
	then
		DO_KILL_DOCKER=false
		shift
	elif [ "$1" = "--no-cleanup" -o "$1" = "-c" ];
	then
		NO_CLEANUP=true
		shift
	elif [ "$1" = "--no-headless" ];
	then
		HEADLESS=false
		shift
	elif [ "$1" = "--selenium-docker" ];
	then
	    SELENIUM_DOCKER=true
	    shift
	else
		shift
	fi
done

exit_code=1
case "$1" in
	service_testing)
		shift
		pretest_cleanup
		test_service $@
		exit_code=$?
		posttest_cleanup
	;;
	ui_testing)
		shift
		pretest_cleanup
		test_ui $@
		exit_code=$?
		posttest_cleanup
	;;
	rest_testing)
		shift
		pretest_cleanup
		test_rest $@
		exit_code=$?
		posttest_cleanup
	;;
	galaxy_testing)
		shift
		pretest_cleanup
		test_galaxy $@
		exit_code=$?
		posttest_cleanup
	;;
	galaxy_pipeline_testing)
		shift
		pretest_cleanup
		test_galaxy_pipelines $@
		exit_code=$?
		posttest_cleanup
	;;
	doc_testing)
		shift
		#pretest_cleanup
		test_doc $@
		exit_code=$?
		posttest_cleanup
	;;
	open_api_testing)
		shift
		pretest_cleanup
		test_open_api $@
		exit_code=$?
		posttest_cleanup
	;;
	all)
		shift
		pretest_cleanup
		test_all $@
		exit_code=$?
		posttest_cleanup
	;;
	*)
		exit_error "Unrecogized command [$1]"
	;;
esac

exit $exit_code
