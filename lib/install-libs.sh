#!/bin/bash

# Download and install binary files from remote site
JARS=(blend4j-0.2.0-SNAPSHOT.jar galaxybootstrap-0.4.0-SNAPSHOT.jar)
POMS=(blend4j-0.2.0-SNAPSHOT.pom galaxybootstrap-0.4.0-SNAPSHOT.pom)
for (( i=0; i<${#JARS[@]}; i++))
do
	jar=${JARS[$i]}
	pom=${POMS[$i]}
	curl https://github.com/apetkau/galaxy-dependencies/raw/master/$jar -O -L
	curl https://github.com/apetkau/galaxy-dependencies/raw/master/$pom -O -L
	mvn install:install-file -Dfile=$jar -DpomFile=$pom -DcreateChecksum=true
done

mvn install:install-file -Dfile=jbzip2-0.9.jar -DgroupId=org.itadaki.bzip2 -DartifactId=jbzip2 -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn install:install-file -Dfile=sam-1.32.jar -DgroupId=net.sf.samtools -DartifactId=samtools -Dversion=1.32 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
mvn install:install-file -Dfile=fastqc-0.10.1.jar -Dsources=fastqc-0.10.1-src.jar -DpomFile=fastqc-0.10.1.pom -DcreateChecksum=true
