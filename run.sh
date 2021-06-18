#!/bin/bash
export MIN_JS=false

PROFILE="dev"
CREATE_DB=""
YARN="-Dskip.yarn"

for arg in "$@"
do
    if [ "$arg" = "--create-db" ]; then
       CREATE_DB="-Dhbm.dev.auto=create"
    fi

    if [ "$arg" = "--yarn" ]; then
       YARN=""
    fi

    if [ "$arg" = "--prod" ]; then
       PROFILE="prod"
    fi
done


if [ "$CREATE_DB" = "-Dhbm.dev.auto=create" ]; then
   echo "Dropping then Creating/Recreating database schema"
else
   echo "Updating database schema without dropping"
  fi

if [ "$YARN" = "" ]; then
   echo "Building Yarn"
else
   echo "Continuing without yarn"
  fi

if [ "$PROFILE" = "prod" ]; then
   echo "Running with Production Profile"
else
   echo "Running with Development Profile"
  fi

mvn clean jetty:run -Dspring.profiles.active=${PROFILE} ${CREATE_DB} ${YARN}
