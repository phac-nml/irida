#!/bin/bash

echo '{ "allow_root": true }' >/root/.bowerrc
pushd lib
./install-libs.sh
popd

apt-get update -qyy
apt-get install -qyy xvfb chromium-browser
