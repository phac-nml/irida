#!/usr/bin/env bash

#
# Script for building and then starting a Docker-Galaxy instance
#   specifically built for IRIDA's integration testing, then
#   starting IRIDA's Galaxy integration tests
#

set -e

#kill all running containers then delete those containers.
#if [ "$(docker ps -a -q)" != "" ];
#then
#	docker rm -f -v $(docker ps -a -q)
#fi

rm -rf /tmp/irida
mkdir /tmp/irida

#get current working directory for mounting into container
MOUNTPATH="$PWD"

#run docker container and save the outputted container ID
OUTPUT="$(docker run -d -p 48888:80 -v ${MOUNTPATH}:${MOUNTPATH} \
    -v /tmp/irida:/tmp/irida phacnml/galaxy-irida-17.01:0.17.0-it)"

#run the test suite
mvn clean verify -B -Pgalaxy_testing -Djdbc.url=jdbc:mysql://localhost:3306/irida_test -Dirida.it.rootdirectory=/tmp/irida

#kill the container with the long container id
docker rm -f -v ${OUTPUT}
