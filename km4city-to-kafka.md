# Km4City to Kafka

## Synopsis

In this repository you can find the implementation of a microservice
that ingests data coming from Replicate's platform into DAF.

By default, the input data is accessed from Km4City's REST API
endpoint at http://servicemap.disit.org/WebAppGrafo/api/v1/ and
written to a Kafka topic named `km4city` on `localhost:9092` (see
[[Configuration]] below).

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
$ mvn install
```

## Configuration

A valid YAML configuration file must be written to
`src/main/resource/application.yml` before compilation.

We built a small shell script to help the user build a configuration
based on her/his preferred category, location and maximum distance.

For example, the following command generates a configuration by
querying the service search API for URIs of services of type
SensorSite which are no more than 1km distant from Florence SMN Train
Station:

    $ ./generate-configuration.sh SensorSite "43.7756;11.2490" 1

To check the effect of the previous command:

    $ cat src/main/resources/application.yml

```yaml
    kafka:
    topic:
     km4city: km4city.t
km4city:
  base_url: http://servicemap.disit.org/WebAppGrafo/api/v1/
    -  "http://www.disit.org/km4city/resource/FI055ZTL00101"
    -  "http://www.disit.org/km4city/resource/FI055ZTL01601"
    -  "http://www.disit.org/km4city/resource/FI055ZTL00801"
    -  "http://www.disit.org/km4city/resource/FI055ZTL02101"
    -  "http://www.disit.org/km4city/resource/FI055ZTL01501"
    -  "http://www.disit.org/km4city/resource/FI055ZTL00901"
    -  "http://www.disit.org/km4city/resource/FI055ZTL02601"
    -  "http://www.disit.org/km4city/resource/FI055ZTL01801"
    -  "http://www.disit.org/km4city/resource/FI055ZTL00301"
    -  "http://www.disit.org/km4city/resource/FI055ZTL02501"
    -  "http://www.disit.org/km4city/resource/FI055ZTL00601"
    -  "http://www.disit.org/km4city/resource/FI055ZTL00501"
```

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
