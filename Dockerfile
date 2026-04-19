FROM eclipse-temurin:25-jdk-alpine AS builder
#FROM bellsoft/liberica-openjre-debian:25-cds AS builder
WORKDIR /builder
ARG JAR_FILE=backend/target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --destination extracted

FROM eclipse-temurin:25-jdk-alpine
#FROM bellsoft/liberica-openjre-debian:25-cds
WORKDIR /application
COPY --from=builder /builder/extracted/ ./
RUN ls -las application.jar

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:MaxDirectMemorySize=64m"
RUN java $JAVA_OPTS -XX:AOTCacheOutput=/app.aot -Dspring.context.exit=onRefresh -jar application.jar --spring.profiles.active=prod || true
ENTRYPOINT exec java $JAVA_OPTS -XX:AOTCache=/app.aot -Xlog:aot -Djava.security.egd=file:/dev/./urandom -jar application.jar
