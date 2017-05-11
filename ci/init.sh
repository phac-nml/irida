#!/bin/bash

echo '{ "allow_root": true }' >/root/.bowerrc
pushd lib
./install-libs.sh
popd

apt install xvfb
