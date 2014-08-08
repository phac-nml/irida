#!/bin/bash

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=jbzip2-0.9.jar -DpomFile=jbzip2-0.9.pom -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=sam-1.32.jar -DpomFile=sam-1.32.pom -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=fastqc-0.10.1.jar -Dsources=fastqc-0.10.1-src.jar -DpomFile=fastqc-0.10.1.pom -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=galaxybootstrap-0.3.0-rc4-SNAPSHOT.jar -DpomFile=galaxybootstrap-0.3.0-rc4-SNAPSHOT.pom -DcreateChecksum=true
