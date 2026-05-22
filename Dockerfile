#FROM eclipse-temurin:25.0.3_9-jdk-alpine
FROM eclipse-temurin:25.0.3_9-jdk-jammy
WORKDIR /application

ARG JAR_FILE=extracted/*.jar
COPY ${JAR_FILE} moviemanager-backend-0.0.1-SNAPSHOT.jar
COPY extracted/ ./
COPY app.aot app.aot

ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=50 \
               -XX:+UseCompressedOops \               
               -XX:+UseCompactObjectHeaders \
               -XX:MaxDirectMemorySize=64m \
               -XX:+UseStringDeduplication"
               
ENTRYPOINT exec java $JAVA_OPTS -XX:+AOTClassLinking \
    -XX:AOTCache=app.aot \
    -Xlog:class+path=info \
    -Djava.security.egd=file:/dev/./urandom \
    -jar moviemanager-backend-0.0.1-SNAPSHOT.jar
