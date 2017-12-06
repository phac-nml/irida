#!/bin/bash

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=jbzip2-0.9.jar -DgroupId=org.itadaki.bzip2 -DartifactId=jbzip2 -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=sam-1.32.jar -DgroupId=net.sf.samtools -DartifactId=samtools -Dversion=1.32 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=fastqc-0.10.1.jar -Dsources=fastqc-0.10.1-src.jar -DpomFile=fastqc-0.10.1.pom -DcreateChecksum=true

# Galaxy Dependencies
# git submodule needs to run from top level of git repository
pushd `git rev-parse --show-toplevel`
git submodule update --init --recursive
popd

sh galaxy-dependencies/install.sh
