# coding: utf-8

import urllib.request
import xml.etree.ElementTree as ET
from pykafka import KafkaClient
import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter
import io
import time
import datetime
import socket

schema = avro.schema.Parse(open("Event.avsc", "r").read())
client = KafkaClient(hosts="127.0.0.1:9092")
topic = client.topics[b'test']
id=0
while True:
    source = urllib.request.urlopen("http://opendata.5t.torino.it/get_fdt")
    source2 = (source.read()).decode('utf-8')
    root = ET.fromstring(source2)
    i=1
    with topic.get_sync_producer() as producer:
        for FDT_data in root.findall("{http://www.5t.torino.it/simone/ns/traffic_data}FDT_data"):
            lcd1 = FDT_data.get('lcd1')
            road_LCD = FDT_data.get('Road_LCD')
            road_name = FDT_data.get('Road_name')
            offset = FDT_data.get('offset')
            lat = FDT_data.get('lat')
            lon = FDT_data.get('lng')
            latlon = lat+"-"+lon
            direction = FDT_data.get('direction')
            accuracy = FDT_data.get('accuracy')
            period = FDT_data.get('period')
            flow = source2.split('<speedflow flow="')[i].split('" speed="')[0] #non ho capito la divisione (?)
            speed = source2.split('" speed="')[i].split('"/>\n  </FDT')[0] 
            print(lcd1,road_LCD,road_name,offset,latlon,direction,accuracy,period,flow,speed)
            i+=1
            writer = avro.io.DatumWriter(schema)
            bytes_writer = io.BytesIO()
            encoder = avro.io.BinaryEncoder(bytes_writer)
            writer.write({"version": 3, "id": str(id)+"-"+str(i), "ts": int(time.time()), "event_type_id": 1, "source": "", "location": str(road_name), "host": socket.gethostname(),"service": "producer", "body": b"", "attributes": { "lcd": lcd1, "road_LCD": road_LCD, "offset": offset, "latlon":latlon , "direction": direction, "accuracy":accuracy, "period": period, "flow": flow, "speed": speed}}, encoder)
            raw_bytes = bytes_writer.getvalue()
            producer.produce(raw_bytes)
            time.sleep(0.000001)
        time.sleep(30)
        id+=1

#{ "lcd": lcd1, "road_LCD": road_LCD, "offset": offset, "latlon":latlon , "direction": direction, "accuracy":accuracy, "period": period, "flow": flow, "speed": speed}
#writer.write({"version": 3, "id": str(id)+"-"+str(i), "ts": time.time(), "event_type_id": 1, "source": "", "location": str(road_name), "host": socket.gethostname(), "service": "producer", "body": b""}, encoder)
