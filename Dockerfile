FROM eclipse-temurin:25-jdk-alpine AS builder
#FROM bellsoft/liberica-openjre-debian:25-cds AS builder
WORKDIR /builder
ARG JAR_FILE=backend/target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --destination extracted

FROM eclipse-temurin:25-jdk-alpine AS trainer
WORKDIR /at-work
COPY --from=builder /builder/extracted/ ./
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:MaxDirectMemorySize=64m"
RUN java $JAVA_OPTS -XX:AOTCacheOutput=app.aot \
    -Dspring.context.exit=onRefresh \
    -Dspring.profiles.active=prod \
    -jar application.jar || echo "AOT Training finished with exit code $?"

FROM eclipse-temurin:25-jdk-alpine
#FROM bellsoft/liberica-openjre-debian:25-cds
WORKDIR /application
COPY --from=trainer /at-work/ ./

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:MaxDirectMemorySize=64m"
ENTRYPOINT exec java $JAVA_OPTS \
    -XX:AOTCache=app.aot \
    -Xlog:aot \
    -Djava.security.egd=file:/dev/./urandom \
    -jar application.jar
