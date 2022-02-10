#!/bin/bash
export MIN_JS=false

COMMANDS=()
ADD_DB=false
PROFILE=dev

while [ $# -ne 0 ];
  do
    if [ "$1" = "--create-db" ];
    then
       COMMANDS+=("-Dspring-boot.run.arguments=\"--hbm.dev.auto=create\"")
       ADD_DB=true
       echo "Dropping then Creating/Recreating database schema"
    elif [ "$1" = "--no-yarn" ];
    then
      COMMANDS+=("-Dskip.yarn")
    elif [ "$1" = "--prod" ];
    then
      PROFILE="prod"
    fi
    shift
done

if [ "$ADD_DB" = false ]; then
   echo "Updating database schema without dropping"
fi

ADD="$(printf "%s " "${COMMANDS[@]}" )"
mvn clean spring-boot:run -Dspring-boot.run.profiles=${PROFILE} $ADD