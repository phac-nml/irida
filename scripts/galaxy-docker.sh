#!/usr/bin/env bash

#
# Script for building and then starting a Docker-Galaxy instance
#   specifically built for IRIDA's integration testing, then
#   starting IRIDA's Galaxy integration tests
#

#pull build the docker image, or download it if it doesn't yet exist
docker build jcuratcha/irida-galaxy-integration:0.1.1

#run docker container and save the outputted container ID
OUTPUT="$(docker run -d -p 48888:80 -v /Warehouse/Temporary/irida-john:/Warehouse/Temporary/irida-john jcuratcha/irida-galaxy-integration:0.1.1)"

#run the test suite
sudo -u curatchaj mvn clean verify -Pgalaxy_testing

#kill the container with the long container id
docker kill ${OUTPUT}