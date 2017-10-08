# PyData
Python implementation of a microservice which links Torino traffic datacenter with Kafka
## Requirements
* [Zookeeper 3.4.x](https://zookeeper.apache.org/releases.html#download)
* [Kafka 0.10.x](https://kafka.apache.org/downloads)
* [PyKafka] (https://github.com/Parsely/pykafka)
* [Avro 1.8.x] (http://avro.apache.org/releases.html)
## Transform all data into an internal format (i.e. Event) 
I use Apache Avro to serialize data to Kafka. 
A generic Event is defined as follows:
* Version (long) - Version of this schema
* ID ([null,string]) - A globally unique identifier for this event.
* ts (long) - Epoch timestamp in millis. Required.
* event_type_id(int) - ID indicating the type of event. Required.
* source([string,null]) - Deprecated event source. Optional.
* location (string) - Location from which the event was generated.
* Host (string) - Hostname, IP, or other device identifier from which the event was generated. Required.
* Service (string) - Service or process from which the event was generated. Required.
* Body (byte array) - Raw event content in bytes. Optional.
* Attributes{ "type": "map", "values": "string" } - the combination of a tag key-value 