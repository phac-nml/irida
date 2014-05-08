#!/bin/bash

mvn install:install-file -Dfile=jbzip2-0.9.jar -DgroupId=org.itadaki.bzip2 -DartifactId=jbzip2 -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn install:install-file -Dfile=sam-1.32.jar -DgroupId=net.sf.samtools -DartifactId=samtools -Dversion=1.32 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn install:install-file -Dfile=fastqc-0.10.1.jar -Dsources=fastqc-0.10.1-src.jar -DpomFile=fastqc-0.10.1.pom -DcreateChecksum=true
mvn install:install-file -Dfile=galaxybootstrap-0.3.0-rc4-SNAPSHOT.jar -DpomFile=galaxybootstrap-0.3.0-rc4-SNAPSHOT.pom -DcreateChecksum=true
