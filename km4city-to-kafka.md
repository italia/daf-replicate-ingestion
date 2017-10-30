# Km4City to Kafka

## Synopsis

This project implements a microservice that ingests data from
Replicate's platform into DAF.

By default the input data is accessed from Km4City's REST API endpoint
at http://servicemap.km4city.org/WebAppGrafo/api/v1/ and written to a
Kafka topic named `km4city` on `localhost:9092` (see
[Configuration](#Configuration) below).

## Technologies

- Spring Boot
- Spring Kafka
- Apache Avro

## Compiling and unit testing

Requirements:

- Java 1.8.0
- Maven 3.3.9

Just run:

```shell
$ mvn clean install spring-boot:repackage
```

## Building the container

### With dockerfile-maven

Thanks to Spotify's [Dockerfile
Maven](https://github.com/spotify/dockerfile-maven/) plugin, you can
create a container image for the service with.

```
$ mvn dockerfile:build
```

### By hand

If the Maven plugin doesn't work in your setup, try the following:

```
$ mvn clean install spring-boot:repackage
[..]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 30.489 s
[INFO] Finished at: 2017-10-29T20:14:57+01:00
[INFO] Final Memory: 34M/314M
[INFO] ------------------------------------------------------------------------

$ docker rm dafreplicateingestion_k42k_1 && docker rmi dafreplicateingestion_k42k
[..]

$ docker build -f Dockerfile -t teamdigitale/daf-replicate-ingestion --build-arg JAR_FILE=daf-replicate-ingestion-0.0.1-SNAPSHOT.jar .

Sending build context to Docker daemon  36.53MB
Step 1/7 : FROM openjdk:8-jdk-alpine
 ---> 3b1fdb34c52a
Step 2/7 : MAINTAINER TeamDigitale <teamdigitale@example.com>
 ---> Using cache
 ---> 47ac2afbc99a
Step 3/7 : VOLUME /tmp
 ---> Using cache
 ---> ebef3e78ab28
Step 4/7 : ARG JAR_FILE
 ---> Using cache
 ---> 5c7b1b647073
Step 5/7 : ADD target/${JAR_FILE} app.jar
 ---> 8e702b945005
Removing intermediate container c7fd4365691b
Step 6/7 : ENV JAVA_OPTS ""
 ---> Running in eb33777a0ab4
 ---> 271545881bce
Removing intermediate container eb33777a0ab4
Step 7/7 : ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
 ---> Running in fc5409dcfdd9
 ---> 3694e138d174
Removing intermediate container fc5409dcfdd9
Successfully built 3694e138d174
Successfully tagged teamdigitale/daf-replicate-ingestion:latest

$ docker create --name k42k teamdigitale/daf-replicate-ingestion
0af8b1a1055311517efc8b33b0b4bd27e8c052c8ad59471af84dd89a447f5235

$ docker start -a k42k

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v1.5.7.RELEASE)

...
```

## Configuration

A valid YAML configuration file must be written to
`src/main/resource/application.yml` before compilation.

We include a shell script to help the user build a configuration based
on her/his preferred category, location and maximum distance.

For example, the following command generates a configuration by
querying the service search API for URIs of category `Car_park` which
are no more than 0.5km distant from Florence SMN Train Station:

    $ ./generate-configuration.sh Car_park "43.7756;11.2490" 0.5

To check the effect of the previous command:

    $ cat src/main/resources/application.yml
    spring:
      profiles:
        active: prod
      kafka:
        bootstrap-servers: localhost:9092
    kafka:
      topic:
        km4city: km4city.t
    km4city:
      base_url: http://servicemap.km4city.org/WebAppGrafo/api/v1/
      ingestion_cron: 0 0/30 * * * ?
      parkings:
        -  "http://www.disit.org/km4city/resource/CarParkStazioneFirenzeS.M.N."
        -  "http://www.disit.org/km4city/resource/2f414975490c98ffef08b8bf3f01fe02"
        -  "http://www.disit.org/km4city/resource/0ea2be6c3b1e600f93be94e46144c6af"
        -  "http://www.disit.org/km4city/resource/79b7b7df3f955ea9cbff956a14226218"
        -  "http://www.disit.org/km4city/resource/005c6b72fed5acb40800bd6784dc659c"
        -  "http://www.disit.org/km4city/resource/CarParkS.Lorenzo"

## Running

### With maven

After producing a correct configuration at
`src/main/resource/application.yml` (see above), you can try running
the microservice(s) with

    mvn spring-boot:run

This expects Kafka to be reachable at `kafka:9092`.

You can tweak the configuration (`spring.kafka.bootstrap-servers`) if
you want to use another Kafka broker.

### With docker-compose

In `docker-compose.yml` you can find an example local deployment of
this microservice with Kafka and Zookeeper.

Just run:

    docker-compose up

## Hacking

We use a standard Maven-based workflow.

If you need to change the event schema in `resources/avro/event.avsc`,
you will need to compile it again to Java classes by using the Apache
Avro tools, e.g.:

    $ curl -O http://central.maven.org/maven2/org/apache/avro/avro-tools/1.8.2/avro-tools-1.8.2.jar
    $ java -jar avro-tools-1.8.2.jar compile schema src/main/resources/avro/event.avsc src/main/java

## Deployment

In order to use this microservice in production, you need to setup a
Kafka cluster, with a topic named "km4city".

## TODOs

- Integrate Avro classes generation in Maven
- Add a Dockerfile that allows to obtain an image without a host Maven
- Test with an external Kafka cluster
- Sort out whether spring-boot:repackage should really be called
  explicitly
- Document the microservice's Swagger REST API
- Add a container that consumes the topic and prints (aggregated?)
  data to console, so that something actually happens when you
  `docker-compose up`
