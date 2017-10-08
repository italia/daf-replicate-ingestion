# coding: utf-8

import urllib.request
import xml.etree.ElementTree as ET
from pykafka import KafkaClient
import avro.schema
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter
import io
import time
import socket

schema = avro.schema.Parse(open("Event.avsc", "r").read()) #schema event for avro
client = KafkaClient(hosts="127.0.0.1:9092") #start kafka client 
topic = client.topics[b'test'] #connect to "test" topic
end_time = ""
new_end_time = ""
id=0 #unique id
while True:
    source = urllib.request.urlopen("http://opendata.5t.torino.it/get_fdt") #data traffic download
    source2 = (source.read()).decode('utf-8') #utf-8 decode
    root = ET.fromstring(source2) #"xml.etree.ElementTree" lib for xml
    new_end_time = source2.split('end_time="')[1].split('" source')[0] #get xml time
    i=1 #index to get "flow" and "speed"
    if end_time != new_end_time: 
        with topic.get_sync_producer() as producer:
            for FDT_data in root.findall("{http://www.5t.torino.it/simone/ns/traffic_data}FDT_data"): #get all FDT_data
                lcd1 = FDT_data.get('lcd1') #get xml data
                road_LCD = FDT_data.get('Road_LCD')
                road_name = FDT_data.get('Road_name')
                offset = FDT_data.get('offset')
                lat = FDT_data.get('lat')
                lon = FDT_data.get('lng')
                latlon = lat+"-"+lon
                direction = FDT_data.get('direction')
                accuracy = FDT_data.get('accuracy')
                period = FDT_data.get('period')
                flow = source2.split('<speedflow flow="')[i].split('" speed="')[0]
                speed = source2.split('" speed="')[i].split('"/>\n  </FDT')[0] 
                print(lcd1,road_LCD,road_name,offset,latlon,direction,accuracy,period,flow,speed) #print in terminal (optional)
                writer = avro.io.DatumWriter(schema) #schema for avro
                bytes_writer = io.BytesIO() #bytes' buffer 
                encoder = avro.io.BinaryEncoder(bytes_writer) #encoder with bytes_writer
                writer.write({"version": 3, "id": str(id)+"-"+str(i), "ts": int(time.time()), "event_type_id": 1, "source": "", "location": str(road_name), "host": socket.gethostname(),"service": "producer", "body": b"", "attributes": { "lcd": lcd1, "road_LCD": road_LCD, "offset": offset, "latlon":latlon , "direction": direction, "accuracy":accuracy, "period": period, "flow": flow, "speed": speed}}, encoder) #encode data with "encoder"
                raw_bytes = bytes_writer.getvalue() #store bytes into raw_bytes
                producer.produce(raw_bytes) #send to kafka
                time.sleep(0.000001) 
            id+=1
            time.sleep(5) 
    time.sleep(3) 
    end_time = new_end_time #refresh end_time