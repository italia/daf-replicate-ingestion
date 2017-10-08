#!/bin/bash

CONFIGURATION_FILE=src/main/resources/application.yml
BASE_URL=http://servicemap.disit.org/WebAppGrafo/api/v1/
KAFKA=localhost:9092

echo -n >${CONFIGURATION_FILE}

cat <<EOF >${CONFIGURATION_FILE}
spring:
  kafka:
EOF

echo "    bootstrap-servers: $KAFKA" >> $CONFIGURATION_FILE

cat <<EOF >${CONFIGURATION_FILE}
kafka:
   topic:
     km4city: km4city.t
km4city:
EOF

echo "  base_url: $BASE_URL" >> $CONFIGURATION_FILE

echo "[1] Default application.yml written"

if [ "$#" -ne 3 ]; then
    echo "Usage: ./generate-configuration.sh <category> <coordinates> <max-distance>"
fi

CATEGORY=$1
COORDINATES=$2
MAX_DISTANCE=$3
SERVICES="?selection=$COORDINATES&categories=$CATEGORY&maxResults=0&maxDists=$MAX_DISTANCE&lang=it&format=json"

SERVICE_URIS=`curl $BASE_URL/$SERVICES | jq '.[] | ."features" | .[] | ."properties" | ."serviceUri" '`

for i in $SERVICE_URIS; do
    echo "    - " $i >> $CONFIGURATION_FILE
done

echo "[2] Added service URIs from $BASE_URL, if any"
