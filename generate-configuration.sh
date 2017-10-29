#!/bin/bash

CONFIGURATION_FILE=src/main/resources/application.yml
BASE_URL=http://servicemap.km4city.org/WebAppGrafo/api/v1/
KAFKA=kafka:9092
CRON_EXPRESSION="0/30 * * * * ?"

echo -n >${CONFIGURATION_FILE}

cat <<EOF >${CONFIGURATION_FILE}
spring:
  profiles:
    active: prod
  kafka:
    bootstrap-servers: $KAFKA
kafka:
  topic:
    km4city: km4city
km4city:
  base_url: $BASE_URL
  ingestion_cron: $CRON_EXPRESSION
EOF

echo "[1] Default application.yml written"

if [ "$#" -ne 3 ]; then
    echo "Usage: ./generate-configuration.sh <category> <coordinates> <max-distance> in order to add a list of services to ingest"
    exit 1
fi

CATEGORY=$1
COORDINATES=$2
MAX_DISTANCE=$3
SERVICES="?selection=$COORDINATES&categories=$CATEGORY&maxResults=0&maxDists=$MAX_DISTANCE&lang=it&format=json"i

SERVICE_URIS=`curl $BASE_URL/$SERVICES | jq '.[] | ."features" | .[] | ."properties" | ."serviceUri" '`

echo "  services:" >> $CONFIGURATION_FILE

if [ $CATEGORY == "Car_park" ]; then
    echo "    parkings:" >> $CONFIGURATION_FILE
    for i in $SERVICE_URIS; do
        echo "      - " $i >> $CONFIGURATION_FILE
    done

    echo "[2] Added service URIs from $BASE_URL, if any"
    exit 0
else
    echo "We only support category Car_park at the moment."
fi
