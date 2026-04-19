FROM eclipse-temurin:25-jdk-alpine AS builder
#FROM bellsoft/liberica-openjre-debian:25-cds AS builder
WORKDIR /builder
ARG JAR_FILE=backend/target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --destination extracted

FROM eclipse-temurin:25-jdk-alpine AS trainer
WORKDIR /at-work
COPY --from=builder /builder/extracted/ ./
ENV JAVA_OPTS="-Xmx384m -Xms384m \
               -XX:+UseG1GC \
               -XX:MaxGCPauseMillis=50 \
               -XX:+UseCompressedOops \
               -XX:+UseCompressedClassPointers \
               -XX:-UseCompactObjectHeaders \
               -XX:MaxDirectMemorySize=64m \
               -XX:+UseStringDeduplication"
RUN java $JAVA_OPTS -XX:+AOTClassLinking \
    -XX:AOTCacheOutput=app.aot \
    -Dspring.context.exit=onRefresh \
    -Dspring.profiles.active=prod \
    -jar application.jar || echo "AOT Training finished with exit code $?"

FROM eclipse-temurin:25-jdk-alpine
#FROM bellsoft/liberica-openjre-debian:25-cds
WORKDIR /application
COPY --from=trainer /at-work/ ./

ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=50 \
               -XX:+UseCompressedOops \
               -XX:+UseCompressedClassPointers \
               -XX:-UseCompactObjectHeaders \
               -XX:MaxDirectMemorySize=64m \
               -XX:+UseStringDeduplication"
ENTRYPOINT exec java $JAVA_OPTS -XX:+AOTClassLinking \
    -XX:AOTCache=app.aot \
    -Xlog:aot \
    -Djava.security.egd=file:/dev/./urandom \
    -jar application.jar
