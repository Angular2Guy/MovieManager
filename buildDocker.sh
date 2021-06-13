#!/bin/sh
#./mvnw clean install -Ddocker=true -Dnpm.test.script=test-chromium
./mvnw clean install -Ddocker=true
docker build -t angular2guy/moviemanager:latest --build-arg JAR_FILE=moviemanager-0.0.1-SNAPSHOT.jar --no-cache .
docker run -p 8080:8080 --memory="1g" --network="host" angular2guy/moviemanager:latest