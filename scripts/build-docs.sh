#!/bin/bash
pushd lib
./install-libs.sh
popd
mvn clean site
