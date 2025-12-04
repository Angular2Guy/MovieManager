#FROM eclipse-temurin:25-jdk-alpine
FROM mcr.microsoft.com/openjdk/jdk:25-azurelinux
VOLUME /tmp
WORKDIR /
ARG LIB_PATH
COPY ${LIB_PATH}/lib/* /lib/
ARG JAR_PATH
COPY ${JAR_PATH} /app.jar
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication"
RUN java $JAVA_OPTS -XX:AOTCacheOutput=/app.aot -Dspring.context.exit=onRefresh -jar app.jar --spring.profiles.active=prod || true
ENTRYPOINT exec java $JAVA_OPTS -XX:AOTCache=/app.aot -Xlog:aot -Djava.security.egd=file:/dev/./urandom -jar /app.jar
