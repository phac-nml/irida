#!/bin/bash
export MIN_JS=false

SPRING_BOOT_ARGS=()
GRADLE_ARGS=()
ADD_DB=false
PROFILE=dev

while [ $# -ne 0 ];
  do
    if [ "$1" = "--create-db" ];
    then
       SPRING_BOOT_ARGS+=("--hbm.dev.auto=create")
       ADD_DB=true
       echo "Dropping then Creating/Recreating database schema"
    elif [ "$1" = "--no-yarn" ];
    then
      GRADLE_ARGS+=("-xassembleFrontend")
    elif [ "$1" = "--prod" ];
    then
      PROFILE="prod"
    fi
    shift
done

if [ "$ADD_DB" = false ]; then
   echo "Updating database schema without dropping"
fi

FORMATTED_SPRING_BOOT_ARGS="$(printf "%s " "${SPRING_BOOT_ARGS[@]}" )"
FORMATTED_GRADLE_ARGS="$(printf "%s " "${GRADLE_ARGS[@]}" )"
./gradlew clean bootRun --args="--spring.profiles.active=${PROFILE} $FORMATTED_SPRING_BOOT_ARGS" $FORMATTED_GRADLE_ARGS