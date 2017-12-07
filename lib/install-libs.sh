#!/bin/bash

mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=jbzip2-0.9.jar -DgroupId=org.itadaki.bzip2 -DartifactId=jbzip2 -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=sam-1.32.jar -DgroupId=net.sf.samtools -DartifactId=samtools -Dversion=1.32 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=fastqc-0.10.1.jar -Dsources=fastqc-0.10.1-src.jar -DpomFile=fastqc-0.10.1.pom -DcreateChecksum=true
mvn --settings maven-central-secure-settings.xml install:install-file -Dfile=ui-dependencies/dandelion-core-1.1.1-IRIDA.jar -DgroupId=com.github.dandelion -DartifactId=dandelion-core -Dversion=1.1.1-IRIDA -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true

# Galaxy Dependencies
# Git commit fac9057d047377e679b6c14672b364a20b9a462b from https://github.com/apetkau/blend4j
mvn install:install-file -Dfile=blend4j-0.2.0-SNAPSHOT-fac9057d047377e679b6c14672b364a20b9a462b.jar -DpomFile=blend4j-0.2.0-SNAPSHOT-fac9057d047377e679b6c14672b364a20b9a462b.pom -Dversion=0.2.0-SNAPSHOT-fac9057d047377e679b6c14672b364a20b9a462b -DcreateChecksum=true
