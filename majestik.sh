#!/bin/sh

java -ea:com.keronic... -Djava.util.logging.config.file=src/test/resources/jul.properties --enable-preview -jar target/majestik-0.0.0-SNAPSHOT.jar "$@"
