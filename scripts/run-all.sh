#!/bin/bash

echo "Running service test script"
/bin/bash ./scripts/integration-tests-service.sh &

echo "Running UI test script"
/bin/bash ./scripts/integration-tests-ui.sh &

echo "Running REST test script"
/bin/bash ./scripts/integration-tests-rest.sh &

echo "Running Galaxy test script"
/bin/bash ./scripts/integration-tests-galaxy.sh &

wait
echo "All integration tests complete"
