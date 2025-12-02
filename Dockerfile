FROM eclipse-temurin:25-jdk-alpine
VOLUME /tmp
ADD backend/target/moviemanager-backend-0.0.1-SNAPSHOT.jar /app.jar
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar