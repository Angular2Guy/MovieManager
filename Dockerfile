FROM eclipse-temurin:25-jdk-alpine
WORKDIR /application

ARG JAR_FILE=backend/target/*.jar
COPY ${JAR_FILE} application.jar
COPY extracted/ extracted/
COPY app.aot app.aot

ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=50 \
               -XX:+UseCompressedOops \               
               -XX:+UseCompactObjectHeaders \
               -XX:MaxDirectMemorySize=64m \
               -XX:+UseStringDeduplication"
               
ENTRYPOINT exec java $JAVA_OPTS -XX:+AOTClassLinking \
    -XX:AOTCache=app.aot \
    -Xlog:aot=info \
    -Djava.security.egd=file:/dev/./urandom \
    -jar application.jar
