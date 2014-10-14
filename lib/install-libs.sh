#!/bin/bash

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=jbzip2-0.9.jar -DgroupId=org.itadaki.bzip2 -DartifactId=jbzip2 -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=sam-1.32.jar -DgroupId=net.sf.samtools -DartifactId=samtools -Dversion=1.32 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=fastqc-0.10.1.jar -Dsources=fastqc-0.10.1-src.jar -DpomFile=fastqc-0.10.1.pom -DcreateChecksum=true

# Git commit 1167ef08bbf7359d781e910ce8253fc36295f291
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=blend4j-0.2.0-SNAPSHOT.jar -DpomFile=blend4j-0.2.0-SNAPSHOT.pom -DcreateChecksum=true
