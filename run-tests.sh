#!/bin/sh
cd `dirname $0`
SCRIPT_DIR=`pwd`

JDBC_URL=jdbc:mysql://localhost:3306/irida_test
TMP_DIRECTORY=/tmp
GALAXY_DOCKER=phacnml/galaxy-irida-17.01:0.17.0-it
GALAXY_PORT=48889
GALAXY_URL=http://localhost:$GALAXY_PORT
GALAXY_INVALID_URL=http://localhost:48890
GALAXY_INVALID_URL2=http://localhost:48891

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
}

test_service() {
	mvn clean verify -B -Pservice_testing -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY
}

test_rest() {
	mvn clean verify -Prest_testing -B
}

test_ui() {
	xvfb-run --auto-servernum --server-num=1 mvn clean verify -B -Pui_testing -Dwebdriver.chrome.driver=./src/main/webapp/node_modules/chromedriver/lib/chromedriver/chromedriver -Dirida.it.nosandbox=true -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY
}

test_galaxy() {
	DOCKER_ID=$(docker run -d -p $GALAXY_PORT:80 -v $TMP_DIRECTORY:$TMP_DIRECTORY $GALAXY_DOCKER)
	mvn clean verify -B -Pgalaxy_testing -Djdbc.url=$JDBC_URL -Dirida.it.rootdirectory=$TMP_DIRECTORY -Dtest.galaxy.url=$GALAXY_URL -Dtest.galaxy.invalid.url=$GALAXY_INVALID_URL -Dtest.galaxy.invalid.url2=$GALAXY_INVALID_URL2
	docker rm -f -v $DOCKER_ID
}

############
### MAIN ###
############

if [ $# -eq 0 ];
then
	echo -e "Usage: $0 [type]"
	echo -e "\t[type]: One of {service, ui, rest, galaxy, all}."
	exit 0
fi

check_dependencies

cd $SCRIPT_DIR

set -x

case "$1" in
	service)
	test_service
	;;
	ui)
	test_ui
	;;
	rest)
	test_rest
	;;
	galaxy)
	test_galaxy
	;;
	all)
	test_service
	test_ui
	test_rest
	test_galaxy
	;;
	*)
	echo "Unrecogized test [$1]"
	exit 1
esac
