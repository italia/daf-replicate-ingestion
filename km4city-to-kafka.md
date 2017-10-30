# Km4City to Kafka

## Introduction

This project implements a microservice that ingests data from the
Km4City REST API into a Kafka topic.

This is a Spring Framework project containing three services,
[running](#running) in the same JVM:

- a Kafka producer, periodically triggered as prescribed by
  [`application.yml`](#configuration)
- a [REST endpoint](#rest-api), allowing to trigger ingestions upon
  request
- a periodic consumer, which polls the Kafka topic and logs the
  messages it finds there for testing purpose

Note that if you do not want the periodic ingestor to do any I/O, you
can tweak the generated configuration and remove the contained service
URIs, or just use a cron string far in the future.

By default the input data is accessed from the endpoint at
http://servicemap.km4city.org/WebAppGrafo/api/v1/ and written to a
topic named `km4city` on `kafka:9092`.

## Technologies

- Spring Boot
- Spring Kafka
- Spring Retry
- Springfox Swagger2 & UI
- Apache Avro

## Configuration

A valid YAML configuration file must be written to
`src/main/resource/application.yml` before building.

We include a shell script `generate_configuration.sh` to help the user
define a configuration based on her/his preferred service Category,
Location and Maximum Distance. This is achieved by querying km4city's
Service Search API.

The script requires the following programs to be available:

- bash
- curl
- jq

For example, the following command generates a configuration by
searching Service URIs of category `Car_park` which are no more than
0.5km distant from Florence SMN Train Station:

    $ ./generate-configuration.sh Car_park "43.7756;11.2490" 0.5

You can check the effect of the previous command as follows:

    $ cat src/main/resources/application.yml
    spring:
      profiles:
        active: prod
      kafka:
        bootstrap-servers: kafka:9092
    kafka:
      topic:
        km4city: km4city
    km4city:
      base_url: http://servicemap.km4city.org/WebAppGrafo/api/v1/
      ingestion_cron: 0/30 * * * * ?
      parkings:
        -  "http://www.disit.org/km4city/resource/CarParkStazioneFirenzeS.M.N."
        -  "http://www.disit.org/km4city/resource/2f414975490c98ffef08b8bf3f01fe02"
        -  "http://www.disit.org/km4city/resource/0ea2be6c3b1e600f93be94e46144c6af"
        -  "http://www.disit.org/km4city/resource/79b7b7df3f955ea9cbff956a14226218"
        -  "http://www.disit.org/km4city/resource/005c6b72fed5acb40800bd6784dc659c"
        -  "http://www.disit.org/km4city/resource/CarParkS.Lorenzo"

If you want to change other parts of the configuration, just tweak it
after generating it.

## Compilation and testing

Requirements:

- Java 1.8.0
- Maven 3.3.9

Just run:

```shell
$ mvn clean install spring-boot:repackage
```

## Building the container

Before building the container you must generate a valid configuration
in `src/resources/application.yml` (see above).

### With dockerfile-maven

Thanks to Spotify's [Dockerfile
Maven](https://github.com/spotify/dockerfile-maven/) plugin, you can
create a container image for the service as follows:

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

## Running

### With maven

After producing a correct configuration at
`src/main/resource/application.yml` (see above), you can try running
the microservice(s) with

    mvn spring-boot:run

This expects Kafka to be reachable at `kafka:9092`. You can tweak the
configuration (`spring.kafka.bootstrap-servers`) if you want to use
another Kafka broker.

### With docker-compose

In `docker-compose.yml` you can find an example local deployment of
this microservice with Kafka and Zookeeper.

Just run:

    docker-compose up

## REST API

The microservice sports a Swagger-generated REST API.

If you run the service with maven or docker-compose with standard
configuration, you can find the Swagger UI documenting the API at
http://localhost:8080/swagger-ui.html .

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
- Add a Dockerfile that gives an image without a host Maven
- Test with an external Kafka cluster
- Find out if `spring-boot:repackage` must really be called explicitly
