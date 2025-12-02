#!/bin/bash
# First start the Postgresql Docker Container
#java -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar backend/target/moviemanager-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
#java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:AOTCache=app.aot -Djava.security.egd=file:/dev/./urandom -jar backend/target/moviemanager-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
java -Djarmode=tools -jar backend/target/moviemanager-backend-0.0.1-SNAPSHOT.jar extract --destination extracted
java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -Djava.security.egd=file:/dev/./urandom -jar extracted/moviemanager-backend-0.0.1-SNAPSHOT.jar  --spring.profiles.active=prod
java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:AOTCache=app.aot -Djava.security.egd=file:/dev/./urandom -jar extracted/moviemanager-backend-0.0.1-SNAPSHOT.jar  --spring.profiles.active=prod
