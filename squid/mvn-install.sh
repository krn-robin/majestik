#!/bin/sh

export SQUID_VERSION=0.10.1
wget --no-verbose -O magik-squid-${SQUID_VERSION}.jar https://github.com/StevenLooman/magik-tools/releases/download/${SQUID_VERSION}/magik-squid-${SQUID_VERSION}.jar && \
mvn3 --batch-mode install:install-file -Dfile=magik-squid-${SQUID_VERSION}.jar && \
wget --no-verbose -O magik-tools-pom.xml https://raw.githubusercontent.com/StevenLooman/magik-tools/${SQUID_VERSION}/pom.xml && \
mvn3 --batch-mode install:install-file -Dfile=magik-tools-pom.xml -DgroupId=nl.ramsolutions -DartifactId=magik-tools -Dversion=${SQUID_VERSION} -Dpackaging=pom
