FROM openjdk:8-jdk-alpine
MAINTAINER TeamDigitale <teamdigitale@example.com>

VOLUME /tmp
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar