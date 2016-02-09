#!/bin/bash

ADD=""

if [ "$1" = "--create-db" ]; then
   ADD="-Dhbm.dev.auto=create"
   echo "Dropping then Creating/Recreating database schema"
else
   echo "Updating database schema without dropping"
  fi

echo ${ADD}

mvn clean jetty:run ${ADD}