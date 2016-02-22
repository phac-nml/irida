#!/bin/bash

# Script for running a single test class or test profile
# Outputs the logs in folder

DATE=`date +%Y-%m-%d:%H:%M`

while getopts "T:P:dsc" opt;
do
    case ${opt} in
        T)
        # specify testing class to be run
            TESTCLASS="-Dit.test=${2}"
            FILENAME="$2"
            ;;
        P)
        # specify testing profile to be run
            PROFILE="-P${2}"
            FILENAME="$2"
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

if [ "${DROPDB}" == "true" ]
then
    echo "drop database irida_test; create database irida_test;" | mysql -u root
fi

mkdir ~/Desktop/logs/
xvfb-run mvn clean verify ${SKIP} ${TESTCLASS} ${PROFILE} ${DEBUG} | tee ~/Desktop/logs/${DATE}-${FILENAME}.logs
