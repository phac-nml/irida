#!/bin/bash

#
# Script for building and then starting a Docker-Galaxy instance
#   specifically built for IRIDA's integration testing, then
#   starting IRIDA's Galaxy integration tests
#

#kill all running containers
docker stop $(docker ps -a -q)
docker ps -a | awk '{print $1}' | xargs --no-run-if-empty docker rm

MOUNTPATH="$PWD"

#run docker container and save the outputted container ID
OUTPUT="$(docker run -d -p 48888:80 -v ${MOUNTPATH}:${MOUNTPATH} -v /tmp:/tmp jcuratcha/irida-galaxy-integration:0.1.1)"

#run the test suite
mvn clean verify -Pgalaxy_testing

#kill the container with the long container id
docker kill ${OUTPUT}