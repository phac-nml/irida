#!/bin/bash

rm -rf /tmp/gxbootstrap*
rm -rf /tmp/shed_tools/
pkill -u gitlab-runner -f "python ./scripts/paster.py" || true

pushd lib
./install-libs.sh
popd

# TODO: create common script for running each test profile

/bin/bash ./scripts/integration-tests-rest.sh | while read line; do echo "[REST] $line"; done &
##/bin/bash ./scripts/integration-tests-rest.sh > rest_out2.txt &

/bin/bash ./scripts/integration-tests-service.sh | while read line; do echo "[SERVICE] $line"; done &
##/bin/bash ./scripts/integration-tests-service.sh > service_out2.txt &

/bin/bash ./scripts/integration-tests-ui.sh | while read line; do echo "[UI] $line"; done &
#/bin/bash ./scripts/integration-tests-ui.sh > ui_out2.txt &

/bin/bash ./scripts/integration-tests-galaxy.sh | while read line; do echo "[GALAXY] $line"; done &
##/bin/bash ./scripts/integration-tests-galaxy.sh > galaxy_out.txt &

wait
echo "All integration tests complete"
