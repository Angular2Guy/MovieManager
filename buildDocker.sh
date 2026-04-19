#!/bin/sh
#./mvnw clean install -Ddocker=true -Dnpm.test.script=test-chromium
./mvnw clean install -Ddocker=true
#java -Djarmode=tools -jar backend/target/moviemanager-backend-0.0.1-SNAPSHOT.jar extract --destination extracted
docker build -t angular2guy/moviemanager:latest --no-cache .
docker run -p 8080:8080 --memory="512m" -e SPRING_PROFILES_ACTIVE="prod" --network="host" --name moviemanager angular2guy/moviemanager:latest

docker start moviemanager
docker stop moviemanager
