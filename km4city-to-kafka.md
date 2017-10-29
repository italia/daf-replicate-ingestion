# Km4City to Kafka

## Synopsis

This project implements a microservice that ingests data from
Replicate's platform into DAF.

By default the input data is accessed from Km4City's REST API endpoint
at http://servicemap.disit.org/WebAppGrafo/api/v1/ and written to a
Kafka topic named `km4city` on `localhost:9092` (see
[Configuration](#Configuration) below).

## Technologies

- Spring Boot
- Spring Kafka
- Apache Avro

## Compilation and unit testing

Requirements:

- Java 1.8.0
- Maven 3.3.9

Just run:

```shell
$ mvn clean install
```

## Configuration

A valid YAML configuration file must be written to
`src/main/resource/application.yml` before compilation.

We built a small shell script to help the user build a configuration
based on her/his preferred category, location and maximum distance.

For example, the following command generates a configuration by
querying the service search API for URIs of category Car_park which
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

If produced a correct configuration (see above), you can try running
the microservice(s) with

    mvn spring-boot:run

This expects Kafka to be reachable at localhost:9092.

You can tweak the configuration (`spring.kafka.bootstrap-servers`) if
you want to use another Kafka broker.

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
- Add docker-compose.yml and a Dockerfile in order to give a complete
  (local) testing environment
- Add main entry point and document its usage
- Periodically trigger ingestion
- REST endpoint to request ingestion of specific services
- Test with DAF's Kafka cluster
