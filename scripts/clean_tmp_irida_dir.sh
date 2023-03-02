#!/bin/sh

# This is intended to only be run during development, so that uploaded data can be cleared easily.
# This must be run with sudo as it affects /tmp/

rm -r /tmp/irida/sequence-files/*
rm -r /tmp/irida/output-files/*
rm -r /tmp/irida/reference-files/*
rm -r /tmp/irida/assembly-files/*
