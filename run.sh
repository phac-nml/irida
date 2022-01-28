#!/bin/bash
export MIN_JS=false

ADD=""

if [ "$1" = "--create-db" ]; then
   ADD="--hbm.dev.auto=create"
   echo "Dropping then Creating/Recreating database schema"
else
   echo "Updating database schema without dropping"
  fi

mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev ${ADD}"