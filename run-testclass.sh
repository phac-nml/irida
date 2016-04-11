#!/bin/bash

# Script for running a single test class or test profile
# Outputs the logs in folder

DATE=`date +%Y-%m-%d:%H:%M`

while getopts "P:C:dsc" opt;
do
    case ${opt} in
        P)
        # specify testing profile to be run
            PROFILE="-P${2}"
            PNAME="$2"
            shift
            ;;
        C)
        # specify testing class to be run
            TESTCLASS="-Dit.test=${2}"
            CNAME="~$2"
            ;;
        d)
        # enable remote debugging
            DEBUG="-Dmaven.failsafe.debug"
            ;;
        s)
        # Skip unit tests
            SKIP="-Dskip.surefire.tests=true"
            ;;
        c)
        # reset/empty database before running
            DROPDB=true
            ;;
    esac
done

COMMIT=`git rev-parse HEAD`

for run in {1..1}
do

DATE=`date +%Y-%m-%d:%H:%M`

if [ "${DROPDB}" == "true" ]
then
    echo "drop database irida_test; create database irida_test;" | mysql -u root
fi

mkdir ~/Desktop/logs/
echo "Current commit: ${COMMIT}"
echo "xvfb-run mvn clean verify ${SKIP} ${PROFILE} ${TESTCLASS}${TEST} ${DEBUG}"
xvfb-run mvn clean verify ${SKIP} ${PROFILE} ${TESTCLASS} ${DEBUG} | tee ~/Desktop/logs/${DATE}-${COMMIT:0:7}-${PNAME}${CNAME}.logs

done