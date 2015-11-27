#!/bin/bash

./gradlew clean :${PROJECT}:installDist

cd ${PROJECT}/build/install/${PROJECT}

JAVA_OPTS="-server -XX:+UseNUMA -XX:+UseParallelGC -XX:+AggressiveOpts -Dratpack.hikari.dataSourceProperties.URL=jdbc:mysql://${DBHOST}:3306/hello_world" ./bin/${PROJECT}
