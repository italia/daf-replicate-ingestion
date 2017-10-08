# POC Km4City2Kafka

Simple API to kafka producer. The API info are collected every N seconds and published to Kafka.
We tested the API in the Florence Area.

----------Alessio

This project is thinked as some kind of bridge between the data acquisition the API exposed from the Km4City application and a Data Warehouse system, using Kafka as a Message Broker for data trasnport.

Using Kafka as Message Broker permitted us to design a scalable and distributed architecture for the application, so that the application can be easily adapted to different scenarios.

As a proof of concept, data acquired are then stored in an Elastic Search Data Warehouse System. Elastic Search is a distributed, highly scalable high performance search and analytics platform. 
Even in this case we decided to adopt this kind of technology to easily permit and incourage future project improvements and advancements in terms of data analytics. 

At the moment, data are acquired from the Km4City REST Api returning territorial information system in unstructured Json format (no schema files are provided at the moment). 
The software queries the service using an active polling protocol and collecting API infos avery N seconds (N is intended as configurable).
Data are then encapsulated in an Avro message, using the Json data acquired from the aPI as a payload of the message and then pushed the message in a Kafka queue, and then stored in the Elastic Data Warehouse. 

At the moment, as mentioned, data json schema information are not provided, so that we could not perform an automatic data forma conversion between json and avro format (natively supported by Kafka). 

Anyway we implemented a generic json to avro transcoder that, given a json data and avro schema files can produce a valid avro file.
Just having the json schema files could permit us to authomatically generate the avro schema file to be used in the data conversion 
The Json2Avro transcoder file and test are present in the project, for further developments. 





