FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG SPRING_PROFILE=prod
ENV MY_SPRING_PROFILE=$SPRING_PROFILE
ARG JAR_FILE
ADD backend/target/$JAR_FILE /app.jar
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=150 -XX:+UseStringDeduplication"
ENTRYPOINT exec java $JAVA_OPTS -Dspring.profiles.active=$MY_SPRING_PROFILE -Djava.security.egd=file:/dev/./urandom -jar /app.jar