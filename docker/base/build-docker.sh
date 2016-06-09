#!/bin/sh
# Builds base Docker image for IRIDA/Galaxy

# Copy necessary files so we don't have to store dupliates
cp ../../src/main/resources/ca/corefacility/bioinformatics/irida/model/enums/workflows/SNVPhyl/1.0/irida_workflow_structure.ga galaxy/

# Build image
docker build -t apetkau/galaxy-irida-15.10:1.0 .
