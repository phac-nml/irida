#!/bin/bash

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=jbzip2-0.9.jar -DgroupId=org.itadaki.bzip2 -DartifactId=jbzip2 -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=sam-1.103.jar -DgroupId=net.sf.samtools -DartifactId=samtools -Dversion=1.103 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=cisd-jhdf5.jar -DpomFile=jhdf5-13.06.2.pom -DcreateChecksum=true

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=fastqc-0.11.9.jar -Dsources=fastqc-0.11.9-src.jar -DpomFile=fastqc-0.11.9.pom -DcreateChecksum=true
