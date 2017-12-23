# POC Km4City2Kafka Microservice

This microservice fetch car traffic data of the Florence city from the API Km4City. The data is then stored into Kafka and Elasticsearch.

A first version of this microservice was developed during the hackaton https://hack.developers.italia.it/.

After the competion, for fun, i refactored the project structure introducing Akka actors and Akka Http.

The service can started and resumed with the rest call:

```bash
curl -XGET http://localhost:8080/pause
```
```js
```

```bash
```
curl -XGET http://localhost:8080/resume
```js
```

It is also possible to check the health of the service:

```bash
curl -XGET http://localhost:8080/stats
```

The stats request will provide a list of stats (number of successes vs number of failure) relative to every Akka actor composing the micorservice.

```js
{
  "elasticsearch": {
    "ko": 8,
    "ok": 252
  },
  "http://www.disit.org/km4city/resource/87f1e99a72066a599613318ba3eaafc7": {
    "ko": 0,
    "ok": 56
  },
  "kafka_producer": {
    "ko": 0,
    "ok": 260
  },
  "http://www.disit.org/km4city/resource/CarParkPortaalPrato": {
    "ko": 0,
    "ok": 43
  },
  ....
}

```







